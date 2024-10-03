from matplotlib import pyplot as plt
from math import exp
from methods import *

def plot(a, b, func, dx=0.01):
    args, ords = [], []
    a -= dx
    b += dx
    x = a
    while x <= b:
        args.append(x)
        ords.append(func(x))
        x += dx
    plt.plot(args, ords, 'g')
def main(f, args, y0, exact_y, h, eps):
    methods = [euler_method,
               modified_euler_method,
               fourth_order_runge_kutta_method,
               adams_method,
               milne_method]
    methods_names = {
        euler_method: "Метод Эйлера:",
        modified_euler_method: "Усовершенствованный метод Эйлера:",
        fourth_order_runge_kutta_method: "Метод Рунге-Кутта IV порядка:",
        adams_method: "Метод Адамса:",
        milne_method: "Метод Милна:"
    }
    h_original = h
    args_original = args
    for method in methods:
        err = eps + 1
        print(methods_names.get(method))

        if h < h_original:
            h = h_original
        if len(args) > len(args_original):
            args = args_original
        ords_original = method(f, args_original, y0, h)
        while True:
            ords = method(f, args, y0, h)
            if method in (adams_method, milne_method):
                inaccuracy = [abs(exact_y(x) - y) for x, y in zip(args[1:], ords[1:])]
                print(f"h = {h}, n = {len(args)}, Конечный узел: {ords[-1]}")
            else:
                args2 = []
                for x1, x2 in zip(args, args[1:]):
                    args2.extend([x1, (x1 + x2) / 2])
                args2.extend([args[-1]])
                ords2 = method(f, args2, y0, h / 2)
                if method is fourth_order_runge_kutta_method:
                    p = 4
                else:
                    p = 1
                inaccuracy = [abs(y1 - y2) / (2 ** p - 1) for y1, y2 in zip(ords, ords2[0::2])]
                print(f"h = {h}, n = {len(args)}, Конечный узел: {ords[-1]}")
            if err <= eps:
                print("\033[1;34mУзлы:", *map(lambda x: round(x, 5), args), "\033[0m")
                print("\033[1;34mЗначения:", *map(lambda x: round(x, 5), ords), "\033[0m")
                print("Конечный узел:", ords[-1])
                print("\033[1;34mУзлы с уменьшенным шагом:", *map(lambda x: round(x, 5), args2), "\033[0m")
                print("\033[1;34mЗначения с уменьшенным шагом:", *map(lambda x: round(x, 5), ords2), "\033[0m")
                print("Конечный узел:", ords2[-1])
                print(f"Погрешность по правилу Рунге = {max(inaccuracy)}")
                break
            else:
                err = max(inaccuracy)
                h /= 2
                args2.clear()
                for x1, x2 in zip(args, args[1:]):
                    args2.extend([x1, (x1 + x2) / 2])
                args2.extend([args[-1]])
                args = args2.copy()
        plt.title(methods_names.get(method)[:-1])

        plot(args_original[0], args_original[-1], exact_y)
        for i in range(len(args_original)):
            plt.scatter(args_original[i], ords_original[i], c='r')
        plt.xlabel("X")
        plt.ylabel("Y")
        plt.show()
        print('-' * 30)

def read_number(s: str):
    while True:
        try:
            return float(input(s).replace(",", "."))
        except Exception:
            continue

if __name__ == "__main__":
    print("1. y' = x")
    print("2. y' = e ** x")
    print("3. y' = y + (1 + x) * y ** 2")
    mode = read_number("Выберите функцию: ")
    n = read_number("Введите n: ")
    x0 = read_number("Введите x0: ")
    xn = read_number("Введите xn: ")
    eps = read_number("Введите точность: ")
    h = (xn - x0) / n
    xs = [x0 + h * i for i in range(int(n))]
    try:
        if mode == 1:
            f = lambda x, y: x
            y0 = 1
            exact_y = lambda x: x ** 2 / 2 + 1
        elif mode == 2:
            f = lambda x, y: exp(x)
            y0 = 0
            exact_y = lambda x: exp(x) - 1
        elif mode == 3:
            f = lambda x, y: y + (1 + x) * y ** 2
            y0 = -1
            exact_y = lambda x: - (1 / x)
    except (ZeroDivisionError, ArithmeticError) as e:
        print("Функция не определена")
    main(f, xs, y0, exact_y, h, eps)
