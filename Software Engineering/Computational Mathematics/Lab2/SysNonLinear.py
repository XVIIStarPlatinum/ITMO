import matplotlib.pyplot as plt
from math import cos, sin, sqrt

import numpy as np

def newtons_method(funcs, x0, y0):
    itr = 0
    x, y = 0, 0
    while True:
        prev_x, prev_y = x0, y0
        a11 = point_derivative_x(funcs[0], prev_x, prev_y)
        a12 = point_derivative_y(funcs[0], prev_x, prev_y)
        a21 = point_derivative_x(funcs[1], prev_x, prev_y)
        a22 = point_derivative_y(funcs[1], prev_x, prev_y)
        F = [-funcs[0](prev_x, prev_y), -funcs[1](prev_x, prev_y)]
        det = a11 * a22 - a12 * a21
        if det == 0:
            print("\033[7;31mПоследовательность не сходится к корню.\033[0m")
            break
        d1 = F[0] * a22 - F[1] * a12
        d2 = a11 * F[1] - a21 * F[0]
        dx = d1 / det
        dy = d2 / det
        x = x0 + dx
        y = y0 + dy
        if abs(dx) <= epsilon and abs(dy) <= epsilon:
            break
        x0 = x
        y0 = y
        itr += 1
        if itr > 300:
            print("\033[7;31mНе удалось добиться нужной точности за вменяемое количество итераций.\033[0m")
            break
    return x, y, itr
# TODO: проверка частных производных
# TODO: анализ функции
def iterative_method(func1, func2, x01, x02, eps, max_itr = 1000):
    x1 = func1(x01)
    x2 = func2(x02)
    itr = 0
    while abs(x1 - x01) > eps or abs(x2 - x02) > eps:
        x2, x02 = func1(x1), x2
        x1, x01 = func2(x2), x1
        itr += 1
        if itr >= max_itr:
            print("\033[7;31mНе удалось вычислить корень системы в пределе 1000 итерации.\033[0m")
            break
    return x1, x2, itr

def point_derivative_x(func, x0, y0, dx=0.001):
    return (func(x0 + dx, y0) - func(x0, y0)) / dx

def point_derivative_y(func, x0, y0, dy=0.001):
    return (func(x0, y0 + dy) - func(x0, y0)) / dy

def plot_system(funcs, x1, y1):
    x = np.linspace(-4, 4, 1600)
    y = np.linspace(-4, 4, 1600)
    X, Y = np.meshgrid(x, y)

    Z1 = np.array([funcs[0](x_, y_) for x_, y_ in zip(np.ravel(X), np.ravel(Y))]).reshape(X.shape)
    Z2 = np.array([funcs[1](x_, y_) for x_, y_ in zip(np.ravel(X), np.ravel(Y))]).reshape(X.shape)
    plt.annotate('x', xy=(x1, y1))
    plt.contour(X, Y, Z1, levels=[0], colors='r')
    plt.contour(X, Y, Z2, levels=[0], colors='g')
    plt.xlabel('x')
    plt.ylabel('y')
    plt.show()
print("\033[1;33m+-+-+-+-+-+-" + "Лабораторная работа №2: решение системы НУ" + "-+-+-+-+-+-+\033[0m")
print("\033[1;32m+-+-+-+-" + "Доступные уравнения(всё захардкожено, к сожалению)" + "-+-+-+-+\033[0m")
print("\033[3;34m1. sin(x + 1) - y = 0\n   2x + cos(y) = 2\033[0m")
print("\033[3;34m2. cos(x – 1) + y = 0.5\n   x – cos(y) = 3\033[0m")
print("\033[3;34m3. x ^ 2 + y ^ 2 = 1\n   x ^ 2 - y = 0.5\033[0m")
print("Выберите номер уравнения (1 - 3): ")
case_number = input()
while case_number not in {'1', '2', '3'}:
    print("\033[1;32mВыберите номер уравнения (1 - 3):\033[0m")
    case_number = input()

case_number = int(case_number)
if case_number == 1:
    func1 = lambda x, y: sin(x + 1) - y
    func2 = lambda x, y: 2 * x + cos(y) - 2
    f1 = lambda x: sin(x + 1)
    f2 = lambda y: 1 - cos(y) / 2
    a1, b1 = 0.6, 0.8
    a2, b2 = 0.8, 1
elif case_number == 2:
    func1 = lambda x, y: cos(x - 1) + y - 0.5
    func2 = lambda x, y: x - cos(y) - 3
    f1 = lambda x: 0.5 - cos(x - 1)
    f2 = lambda y: cos(y) + 3
    a1, b1 = 10, 10
    a2, b2 = 15, 15
else:
    func1 = lambda x, y: x ** 2 + y ** 2 - 1
    func2 = lambda x, y: x ** 2 - y - 0.5
    f2 = lambda y: sqrt(1 - y ** 2)
    f1 = lambda x: x ** 2 - 0.5
    a1, b1 = 5, 15
    a2, b2 = 100, 100
funcs = [func1, func2]
x01 = (a1 + b1) / 2
x02 = (a2 + b2) / 2
epsilon = 0.000001
if epsilon <= 0.00001:
    places = int(str(epsilon)[-1:]) + 4
else:
    places = str(epsilon)[::-1].find(".") + 4
x, y, itr = newtons_method(funcs, x01, x02)
print("Метод Ньютона: ")
print(f"x = {round(x, places)}, y = {round(y, places)}, число итераций = {itr}")
print(func1(x, y), func2(x, y))
root = newtons_method(funcs, x01, x02)[0]
x, y, itr = iterative_method(f1, f2, x01, x02, epsilon)
print("\033[1;33mМетод простых итераций:\033[0m")
print(f"x = {round(x, places)}, y = {round(y, places)}, число итераций = {itr}")
print(func1(x, y), func2(x, y))
plot_system(funcs, x, f1(x))
