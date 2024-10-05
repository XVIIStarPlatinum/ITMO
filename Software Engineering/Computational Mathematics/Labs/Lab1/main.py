from random import randint
from decimal import Decimal
import numpy as np
import math

def get_random_matrix(sz):
    mn = -1000
    mx = 1000
    an = [
        [randint(mn, mx) for __ in range(sz)]
        for _ in range(sz)
    ]
    bn = [randint(mn, mx) for __ in range(sz)]
    return an, bn

def print_line():
    print("\033[0;32m" + "=+" * 50 + "=" + "\033[0m")

def print_condition(str):
    print("\033[0;36m" + str + "\033[0m")

def print_message(str):
    print("\033[0;31m" + str + "\033[0m")

def print_result(str):
    print("\033[0;35m" + str + "\033[0m")

def get_data():
    global b, cur
    random_word = 'RANDOM'
    random_word_russian = 'РАНДОМ'
    sn = input("\033[0;36m" + 'Введите размерность матрицы (n): ' + "\033[0m").strip()
    while not sn.isdigit() or int(sn) < 1 or int(sn) > 20:
        print_line()
        print_message('Размер n должно быть числом на отрезке [1;20]')
        sn = input("\033[0;36m" + 'Введите размерность матрицы (' + sn + '): ' + "\033[0m").strip()
    n_sz = int(sn)
    rw = input(
        "\033[0;36m" + f'Введите "{random_word}" | "{random_word_russian}" для генерации случайной матрицы (A и B): ' + "\033[0m").strip()
    if rw.casefold() == 'RANDOM'.casefold() or rw.casefold() == 'РАНДОМ'.casefold() or rw.casefold() == 'HFYLJV'.casefold() or rw.casefold() == 'КФТВЩЬ'.casefold():
        a, b = get_random_matrix(n_sz)
        print_result("\033[1;35m" + 'Сгенерированная матрица: ')
        for i in range(n_sz):
            print(*a[i], '|', b[i], sep='\t')
        print_line()
        print("\033[0m", end="")
    else:
        print_condition('Введите элементы матрицы A:')
        a = []
        for i in range(n_sz):
            fl = False
            while not fl:
                try:
                    print_condition(f'Введите строчку ({i}) (n чисел): ')
                    cur = list(map(float, input().split()))
                    assert len(cur) == n_sz
                    fl = True
                except Exception:
                    pass
            a.append(cur)

        print_condition('Введите элементы матрицы B:')
        fl = False
        while not fl:
            try:
                print_condition('Введите строчку (n чисел): ')
                b = list(map(float, input().split()))
                assert len(b) == n_sz
                fl = True
            except Exception:
                pass

    return n_sz, a, b

def get_data_from_file():
    with open('input.txt') as f:
        n = int(f.readline())
        f.readline()
        a = [
            list(map(float, f.readline().split()))
            for _ in range(n)
        ]
        f.readline()
        b = list(map(float, f.readline().split()))
        return n, a, b

def get_determinant(triangular_matrix, k):
    res = 1
    for i in range(len(triangular_matrix)):
        res *= triangular_matrix[i][i]
    return (-1) ** k * res

def get_solution_and_k(a, b):
    x = [None] * n
    k_ = 0

    for i in range(0, n - 1):
        while a[i][i] == 0:
            a = a[:i] + a[i + 1:] + [a[i]]
            b = b[:i] + b[i + 1:] + [b[i]]
            k_ += 1
            if k_ > math.factorial(n):
                print_message("Матрица СЛАУ не соответствует теореме Кронекера-Капелли.")
                exit(0)

        for k in range(i + 1, n):
            c = a[k][i] / a[i][i]
            a[k][i] = 0
            for j in range(i + 1, n):
                a[k][j] = a[k][j] - c * a[i][j]
            b[k] = b[k] - c * b[i]

    for i in range(n - 1, -1, -1):
        s = 0
        for j in range(i + 1, n):
            s = s + a[i][j] * x[j]
        if (not np.any(a)) == 0:
            print_message("Нет решения: несовместная система.")
            print_result(str(k))
            exit(0)
        x[i] = (b[i] - s) / a[i][i]
        print(a[i][i])

    return x, k_, a, b

def get_r(a, b, x):
    n = len(a)
    r = [0] * n
    for i in range(n):
        for j in range(n):
            r[i] += a[i][j] * x[j]
        r[i] -= b[i]
    return r

print("\033[1;33m" + "Вычислительная математика - Лабораторная №1" + "\033[0m")
print_line()
print("Каким образом будет осуществлен ввод СЛАУ?")
while True:
    inp = input(
        "\033[1;33m" + "Вручную (+)" + "\033[0m" + " | " + "\033[0;35m" + "С файла \'input.txt\' (-): " + "\033[0m")
    if inp == "+":
        n, a, b = get_data()
        break
    elif inp == "-":
        n, a, b = get_data_from_file()
        break
    else:
        print_message("Ответ не распознан. Попробуйте снова.")

det = Decimal(str(np.linalg.det(np.array(a))))

initial_a = [el[:] for el in a]
initial_b = b[:]

x, k, a, b = get_solution_and_k(a, b)
if det == 0:
    print_message('Матрица СЛАУ является вырожденным, det A = 0.')
    print_result(str(k))
    exit(0)

print("\033[0;36m" + 'Определитель (Δ/det): ', get_determinant(a, k), "\033[0m")
print("\033[0;36m" + 'Определитель (Δ/det) из numpy: ', det, "\033[0m")
print_result('Число перестановок: ' + str(k))

print_line()
print('\033[0;34m' + 'Треугольная матрица: ')

for i in range(n):
    print(*a[i], '|', b[i], sep='\t')
print("\033[0m", end="")
print_line()

print('\033[0;35m' + 'Вектор неизвестных: ')
for i in range(len(x)):
    print("x_", end="")
    print(i, "=", Decimal(str(round(x[i], 5))))
print('\033[0m', end="")
print_line()
print('\033[0;35m' + 'Вектор невязок: ')
for i in range(len(get_r(initial_a, initial_b, x))):
    print("r_", end="")
    print(i, "=", Decimal(str(get_r(initial_a, initial_b, x)[i])))
print('\033[0m', end="")
