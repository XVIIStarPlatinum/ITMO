import os

import redis
from flask import Flask, jsonify, render_template_string

redis_client = redis.Redis(
    host=os.getenv("REDIS_HOST", "redis"),
    port=int(os.getenv("REDIS_PORT", "6379")),
    db=int(os.getenv("REDIS_DB", "0")),
    password=os.getenv("REDIS_PASSWORD"),
    decode_responses=True
)

app = Flask(__name__)

HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="mn">
<head>
    <meta charset="UTF-8">
    <title>Онлайн иагазин - ЛР№1</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
        }
        nav {
            width: 100%;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            padding: 15px 0;
            text-align: center;
            position: sticky;
            top: 0;
            z-index: 1000;
        }
        nav a {
            color: white;
            text-decoration: none;
            margin: 0 20px;
            font-weight: bold;
        }
        .container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 800px;
            width: 90%;
            margin: 40px 0;
        }
        h1 {
            color: #667eea;
            margin: 0 0 10px 0;
            font-size: 2.5em;
        }
        .counter {
            font-size: 3em;
            color: #764ba2;
            font-weight: bold;
            margin: 10px 0;
        }
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
        .product-card {
            border: 1px solid #eee;
            padding: 15px;
            border-radius: 15px;
            transition: 0.3s;
        }
        .product-card:hover {
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .product-card img {
            width: 100%;
            border-radius: 10px;
        }
        .button {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 30px;
            font-size: 1em;
            cursor: pointer;
            margin: 10px;
            transition: transform 0.2s;
            text-decoration: none;
            display: inline-block;
        }
        .button:hover {
            transform: scale(1.05);
        }
        .version {
            color: #999;
            font-size: 0.8em;
            margin-top: 30px;
        }
        footer {
            color: white;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

    <nav>
        <a href="#">Домашняя страница</a>
        <a href="#">Продукты</a>
        <a href="#">Про нас</a>
        <a href="#">Корзина (0)</a>
    </nav>

    <div class="container">
        <p style="color: #666;">Число посещений:</p>
        <div class="counter">{{ count }}</div>

        <div class="products-grid">
            <div class="product-card">
                <img src="https://i01.appmifile.com/webfile/globalimg/products/pc/mi-smart-band-5-zc/Spec_PC2560_03.jpg" alt="Продукт">
                <h3>Смарт-часы</h3>
                <p>9,000₽</p>
                <button class="button">В корзину</button>
            </div>
            <div class="product-card">
                <img src="https://lunafon.ru/image/cache/webp/catalog/Accessories/jbl-tune-760nc-black-1000x1000.webp" alt="Продукт">
                <h3>Наушники</h3>
                <p>4,500₽</p>
                <button class="button">В корзину</button>
            </div>
            <div class="product-card">
                <img src="https://cdn.werfstore.ru/wp-content/uploads/2021/05/65.png" alt="Продукт">
                <h3>Рюкзак</h3>
                <p>7,500₽</p>
                <button class="button">В корзину</button>
            </div>
        </div>

        <div style="margin-top: 30px;">
            <a href="/" class="button">🔄</a>
            <a href="/api" class="button">📊</a>
        </div>

        <div class="version">Версия: {{ version }}</div>
    </div>

    <footer>
        &copy; Болорболд Аригуун P3411 | XVIIStarPt__ | 2026.
    </footer>

</body>
</html>
"""


@app.route("/")
def index():
    count = redis_client.incr("hits")
    version = os.getenv("APP_VERSION", "v1")
    return render_template_string(HTML_TEMPLATE, count=count, version=version)


@app.route("/api")
def api():
    count = redis_client.get("hits") or 0
    return jsonify(visit_count=int(count), version=os.getenv("APP_VERSION", "v1")), 200


@app.route("/health")
def health():
    try:
        redis_client.ping()
        return jsonify({"status": "ok"}), 200
    except redis.RedisError as exc:
        return jsonify(
            status="failed",
            detail=str(exc)
        ), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
