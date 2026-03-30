import json
import os
import time
from collections import deque
from threading import Thread

import paho.mqtt.client as mqtt
from flask import Flask, render_template_string

BROKER = os.getenv("MQTT_BROKER", "cloud-broker")
PORT = int(os.getenv("MQTT_PORT", 1883))
TOPIC = os.getenv("MQTT_TOPIC", "cloud/alerts")

app = Flask(__name__)

alerts = deque(maxlen=50)
stats = {
    'total_alerts': 0,
    'last_alert_time': None,
    'avg_temp': 0,
    'max_temp': 0
}

HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Cloud Dashboard - Monitoring 2026</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #1a1a2e;
            color: #eee;
            margin: 0;
            padding: 20px;
        }
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #16213e;
            padding: 10px 30px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-bottom: 2px solid #e74c3c;
        }
        .header {
            text-align: center;
            padding: 30px;
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(231, 76, 60, 0.3);
        }
        h1 { margin: 0; font-size: 2.2em; }
        .grid-layout {
            display: grid;
            grid-template-columns: 1fr 2fr;
            gap: 20px;
        }
        .stats {
            display: grid;
            grid-template-columns: 1fr;
            gap: 20px;
        }
        .stat-card {
            background: #16213e;
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            border: 1px solid #30475e;
            transition: 0.3s;
        }
        .stat-card:hover { border-color: #e74c3c; transform: translateY(-5px); }
        .stat-value { font-size: 3em; color: #e74c3c; font-weight: bold; }
        .stat-label { color: #aaa; text-transform: uppercase; letter-spacing: 1px; }

        .main-panel {
            background: #16213e;
            padding: 25px;
            border-radius: 10px;
        }
        .info-box {
            background: rgba(52, 152, 219, 0.1);
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 5px solid #3498db;
            font-size: 0.95em;
            line-height: 1.5;
        }
        .alerts-container {
            max-height: 600px;
            overflow-y: auto;
            padding-right: 10px;
        }
        .alert-item {
            background: #0f3460;
            padding: 15px;
            margin: 10px 0;
            border-radius: 8px;
            border-left: 5px solid #e74c3c;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .alert-temp { font-size: 1.6em; color: #e74c3c; font-weight: bold; }
        .alert-time { color: #888; font-family: monospace; }
        .status-tag {
            background: #e74c3c;
            color: white;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.8em;
            font-weight: bold;
        }
        footer {
            text-align: center;
            margin-top: 40px;
            color: #555;
            font-size: 0.85em;
        }
    </style>
</head>
<body>

    <div class="navbar">
        <div style="font-weight: bold; color: #e74c3c;">FOG-CLOUD ARCHITECTURE</div>
        <div id="current-time">Status: Connected</div>
    </div>

    <div class="header">
        <h1>Панель наблюдения облаки</h1>
        <p>Централизованный анализ критических состояний систем (T > 80°C)</p>
    </div>

    <div class="info-box">
        <strong>Fog Computing Active:</strong> Локальные узлы (Fog Nodes) фильтруют 95% сырых данных. 
        В облако передаются только аномальные значения, требующие немедленного реагирования.
    </div>

    <div class="grid-layout">
        <div class="stats">
            <div class="stat-card">
                <div class="stat-value">{{ stats.total_alerts }}</div>
                <div class="stat-label">Критические события</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">{{ "%.1f"|format(stats.avg_temp) }}°C</div>
                <div class="stat-label">Средний перегрев</div>
            </div>
            <div class="stat-card" style="border-bottom: 4px solid #e74c3c;">
                <div class="stat-value">{{ stats.max_temp }}°C</div>
                <div class="stat-label">Пиковый максимум</div>
            </div>
        </div>

        <div class="main-panel">
            <h2 style="margin-top: 0; display: flex; justify-content: space-between;">
                Журнал оповещений
                <span style="font-size: 0.5em; color: #aaa;">Live Updates</span>
            </h2>
            <div class="alerts-container">
                {% if alerts %}
                    {% for alert in alerts %}
                        <div class="alert-item">
                            <div>
                                <span class="status-tag">CRITICAL</span>
                                <div class="alert-temp">{{ alert.temp }}°C</div>
                            </div>
                            <div class="alert-time">{{ alert.time }}</div>
                        </div>
                    {% endfor %}
                {% else %}
                    <div style="text-align: center; padding: 50px; color: #555;">
                        <p>Ожидание данных от Fog-узлов...</p>
                    </div>
                {% endif %}
            </div>
        </div>
    </div>
    <footer>
        &copy; Болорболд Аригуун P3411 | ЛР№3 | 2026
    </footer>
</body>
</html>
"""


@app.route("/")
def dashboard():
    return render_template_string(HTML_TEMPLATE, alerts=list(alerts)[::-1], stats=stats)


def on_connect(client, userdata, flags, reason_code, properties):
    print(f"Connected to Cloud Broker with result code {reason_code}")
    client.subscribe(TOPIC)
    print(f"Subscribed to topic: {TOPIC}")
    print("=" * 60)
    print("Waiting for critical temperature alerts (temp > 80°C)...")
    print("Dashboard available at http://localhost:5001")
    print("=" * 60)


def on_message(client, userdata, msg):
    try:
        data = json.loads(msg.payload.decode())
        temp = data.get('temp')
        ts = data.get('ts')

        stats['total_alerts'] += 1
        stats['last_alert_time'] = time.strftime('%H:%M:%S', time.localtime(ts))

        temps = [a['temp'] for a in alerts] + [temp]
        stats['avg_temp'] = sum(temps) / len(temps)
        stats['max_temp'] = max(temps)

        alerts.append({
            'temp': temp,
            'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(ts))
        })
        print(f"ALERT RECEIVED: Temp={temp}°C at timestamp={ts}")
    except Exception as e:
        print(f"Error processing message: {e}")


def mqtt_loop():
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.on_connect = on_connect
    client.on_message = on_message
    print(f"Connecting to {BROKER}:{PORT}...")
    client.connect(BROKER, PORT, 60)
    client.loop_forever()


if __name__ == "__main__":
    mqtt_thread = Thread(target=mqtt_loop, daemon=True)
    mqtt_thread.start()
    app.run(host="0.0.0.0", port=5001)
