from matplotlib import pyplot as plt
from interp import *

def draw_plot(a, b, func, dx=0.01):
    xs, ys = [], []
    a -= dx
    b += dx
    x = a
    while x <= b:
        xs.append(x)
        ys.append(func(x))
        x += dx
    plt.plot(xs, ys, 'g')

def main(xs, ys, x):
    methods = [calc_lagrange_polynomial,
               calc_newton_finite_difference_polynomial,
               calc_gauss_polynomial]
    for method in methods:
        if method is calc_gauss_polynomial and len(xs) % 2 == 0:
            continue
        print(method.__name__)
        if method == calc_lagrange_polynomial:
            P = method(xs, ys)
        else:
            P = method(xs, ys, x)
        plt.title(method.__name__)
        draw_plot(xs[0], xs[-1], P)
        for i in range(len(xs)):
            plt.scatter(xs[i], ys[i], c='r')
        plt.xlabel("X")
        plt.ylabel("Y")
        plt.show()
        print(f'P({x}) = {P(x)}')
        print('-' * 60)

def read_number(s: str):
    while True:
        try:
            return float(input(s))
        except Exception:
            continue

if __name__ == '__main__':
    mode = read_number("Введите режим: ")
    while mode not in (1, 2, 3):
        mode = read_number("Введите режим: ")
    if mode == 1:
        xs = list(map(float, input('Введите иксы: ').split()))
        ys = list(map(float, input('Введите игреки: ').split()))
        x = float(input('Введите икс: '))
    elif mode == 2:
        with open('tests/1') as f:
            xs = list(map(float, f.readline().strip().split()))
            ys = list(map(float, f.readline().strip().split()))
            x = float(f.readline().strip())
    elif mode == 3:
        print('Функции: ')
        print('1. x ^ 2 - 3 * x')
        print('2. x ^ 5')
        func_number = read_number("Выберите функцию")
        f = lambda x: x ** 2 - 3 * x if func_number == 1 else x ** 5
        n = int(input('Введите n: '))
        x0 = float(input('Введите первый x: '))
        xn = float(input('Введите последний x: '))
        h = (xn - x0) / (n - 1)
        xs = [x0 + h * i for i in range(n)]
        ys = list(map(f, xs))
        x = float(input('Введите икс: '))
    xs = sorted(xs)
    if len(set(xs)) != len(xs):
        print('Иксы должны быть разными')
    else:
        main(xs, ys, x)
