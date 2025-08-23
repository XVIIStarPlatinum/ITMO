import json
import gmpy2

# Заполните JSON-файл в папке "/data" своими данными перед запуском.
try:
    with open ('data/Crypto_task_6_file_9.json', 'r', encoding='utf-8') as file:
        data = json.load(file)
        p = data['P']
        q = data['Q']
        g = data['G']
        x = data['x']
        k = data['k']
        h_m = data['h(m)']

        Y = gmpy2.powmod_sec(g, x, p)
        R = gmpy2.powmod_sec(g, k, p) % q
        k_inv = gmpy2.invert(k, q)
        S = (k_inv * (h_m + x * R)) % q
        print("Y =", Y)
        print("R =", R)
        print("S =", S)
except FileNotFoundError:
    print("Ошибка: файл не найден.")
    exit(1)
except json.decoder.JSONDecodeError:
    print("Ошибка: некорректный формат JSON в файле.")
    exit(2)