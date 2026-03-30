import json
import time
from collections import deque
from threading import Thread

import paho.mqtt.client as mqtt
from flask import Flask, render_template_string

app = Flask(__name__)

sensor_data = deque(maxlen=100)
cloud_data = deque(maxlen=100)

stats = {
    'total_sensor': 0,
    'total_cloud': 0,
    'bandwidth_saved': 0,
    'avg_sensor_temp': 0,
    'avg_cloud_temp': 0
}

HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Панель сравнения</title>
    <style>
        body {
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background: #050505;
            color: #f0f0f0;
            margin: 0;
            padding: 20px;
            line-height: 1.6;
        }
        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: rgba(255, 255, 255, 0.05);
            padding: 10px 25px;
            border-radius: 12px;
            margin-bottom: 20px;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        .header {
            text-align: center;
            padding: 40px 20px;
            background: linear-gradient(145deg, #1e3a8a 0%, #7c3aed 100%);
            border-radius: 20px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(124, 58, 237, 0.2);
        }
        h1 { margin: 0; font-size: 2.8em; letter-spacing: -1px; }
        .subtitle { opacity: 0.9; font-size: 1.1em; margin-top: 10px; }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: #111;
            padding: 25px;
            border-radius: 16px;
            text-align: center;
            border: 1px solid #333;
            transition: all 0.3s ease;
        }
        .stat-card:hover { transform: translateY(-5px); border-color: #7c3aed; }
        .stat-value { font-size: 2.5em; font-weight: 800; color: #fff; }
        .stat-label { color: #888; text-transform: uppercase; font-size: 0.8em; margin-top: 8px; font-weight: 600; }

        .efficiency-badge {
            background: #064e3b;
            color: #10b981;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.9em;
            display: inline-block;
            margin-bottom: 10px;
        }

        .comparison {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
            margin-bottom: 30px;
        }
        .panel {
            background: #111;
            padding: 25px;
            border-radius: 20px;
            border-top: 5px solid;
            display: flex;
            flex-direction: column;
        }
        .panel-sensor { border-color: #3b82f6; }
        .panel-cloud { border-color: #ef4444; }

        .panel-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .panel h2 { margin: 0; font-size: 1.4em; }

        .data-stream {
            height: 450px;
            overflow-y: auto;
            background: #000;
            padding: 10px;
            border-radius: 12px;
            border: 1px solid #222;
        }
        .data-item {
            padding: 12px 15px;
            margin: 8px 0;
            border-radius: 8px;
            font-family: 'JetBrains Mono', 'Fira Code', monospace;
            font-size: 0.9em;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .temp-normal { background: rgba(59, 130, 246, 0.1); color: #60a5fa; border: 1px solid rgba(59, 130, 246, 0.2); }
        .temp-high { background: rgba(239, 68, 68, 0.1); color: #f87171; border: 1px solid rgba(239, 68, 68, 0.3); font-weight: bold; }

        .empty-state {
            text-align: center;
            padding: 100px 20px;
            color: #444;
        }

        footer {
            text-align: center;
            padding: 40px;
            border-top: 1px solid #222;
            color: #666;
            font-size: 0.9em;
        }
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-thumb { background: #333; border-radius: 10px; }
    </style>
</head>
<body>
    <div class="header">
        <div class="subtitle">Фильтрация данных на границе сети (Edge Filtering)</div>
    </div>

    <div class="stats-grid">
        <div class="stat-card" style="border-bottom: 4px solid #10b981;">
            <div class="efficiency-badge">Efficiency High</div>
            <div class="stat-value">{{ "%.1f"|format(stats.bandwidth_saved) }}%</div>
            <div class="stat-label">Экономия трафика</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">{{ stats.total_sensor }}</div>
            <div class="stat-label">Raw Events (Sensor)</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">{{ stats.total_cloud }}</div>
            <div class="stat-label">Processed (Cloud)</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">{{ "%.1f"|format(stats.avg_sensor_temp) }}°</div>
            <div class="stat-label">Средняя t° (Общая)</div>
        </div>
    </div>

    <div class="comparison">
        <div class="panel panel-sensor">
            <div class="panel-header">
                <span style="color: #3b82f6; font-size: 0.8em;">Все данных</span>
            </div>
            <div class="data-stream">
                {% for item in sensor_data %}
                    <div class="data-item {% if item.temp > 80 %}temp-high{% else %}temp-normal{% endif %}">
                        <span>{{ item.temp }}°C</span>
                        <span style="opacity: 0.6; font-size: 0.8em;">{{ item.time }}</span>
                    </div>
                {% endfor %}
            </div>
        </div>

        <div class="panel panel-cloud">
            <div class="panel-header">
                <h2>☁️ Central Cloud Log</h2>
                <span style="color: #ef4444; font-size: 0.8em;">Только критичные (>80°C)</span>
            </div>
            <div class="data-stream">
                {% if cloud_data %}
                    {% for item in cloud_data %}
                        <div class="data-item temp-high">
                            <span>Внимание: {{ item.temp }}°C</span>
                            <span>{{ item.time }}</span>
                        </div>
                    {% endfor %}
                {% else %}
                    <div class="empty-state">
                        <div style="font-size: 3em; margin-bottom: 10px;">🛡️</div>
                        <p>Критических событий не обнаружено.<br>Облако в режиме ожидания.</p>
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
    return render_template_string(
        HTML_TEMPLATE,
        sensor_data=list(sensor_data)[::-1],
        cloud_data=list(cloud_data)[::-1],
        stats=stats
    )


def on_connect_sensor(client, userdata, flags, reason_code, properties):
    print(f"Connected to Edge Broker (sensor data)")
    client.subscribe("factory/temp")


def on_message_sensor(client, userdata, msg):
    try:
        data = json.loads(msg.payload.decode())
        temp = data.get('temp')
        ts = data.get('ts')

        stats['total_sensor'] += 1

        sensor_temps = [item['temp'] for item in sensor_data] + [temp]
        stats['avg_sensor_temp'] = sum(sensor_temps) / len(sensor_temps)

        sensor_data.append({
            'temp': temp,
            'time': time.strftime('%H:%M:%S', time.localtime(ts))
        })

        if stats['total_sensor'] > 0:
            stats['bandwidth_saved'] = ((stats['total_sensor'] - stats['total_cloud']) / stats['total_sensor']) * 100

    except Exception as e:
        print(f"Error processing sensor message: {e}")


def on_connect_cloud(client, userdata, flags, reason_code, properties):
    print(f"Connected to Cloud Broker (filtered data)")
    client.subscribe("cloud/alerts")


def on_message_cloud(client, userdata, msg):
    try:
        data = json.loads(msg.payload.decode())
        temp = data.get('temp')
        ts = data.get('ts')

        stats['total_cloud'] += 1

        cloud_temps = [item['temp'] for item in cloud_data] + [temp]
        if cloud_temps:
            stats['avg_cloud_temp'] = sum(cloud_temps) / len(cloud_temps)

        cloud_data.append({
            'temp': temp,
            'time': time.strftime('%H:%M:%S', time.localtime(ts))
        })

        if stats['total_sensor'] > 0:
            stats['bandwidth_saved'] = ((stats['total_sensor'] - stats['total_cloud']) / stats['total_sensor']) * 100

    except Exception as e:
        print(f"Error processing cloud message: {e}")


def mqtt_sensor_loop():
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.on_connect = on_connect_sensor
    client.on_message = on_message_sensor
    client.connect("edge-broker", 1883, 60)
    client.loop_forever()


def mqtt_cloud_loop():
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.on_connect = on_connect_cloud
    client.on_message = on_message_cloud
    client.connect("cloud-broker", 1883, 60)
    client.loop_forever()


if __name__ == "__main__":
    sensor_thread = Thread(target=mqtt_sensor_loop, daemon=True)
    cloud_thread = Thread(target=mqtt_cloud_loop, daemon=True)

    sensor_thread.start()
    cloud_thread.start()

    app.run(host="0.0.0.0", port=5002)
