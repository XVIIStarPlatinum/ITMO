import matplotlib.pyplot as plt
from math import e, sin, cos

def bisection_method(function, a, b, epsilon):
    itr = 0
    while abs(b - a) > epsilon:
        mid = (a + b) / 2
        if function(a) * function(mid) > 0:
            a = mid
        else:
            b = mid
        if abs(function(mid)) < epsilon:
            break
        itr += 1
    return (a + b) / 2, itr
def chord_method(function, a, b, epsilon):
    iteration = 0
    x = a - ((b - a) * function(a)) / (function(b) - function(a))
    while abs(function(x)) > epsilon:
        iteration += 1
        last_x = x
        if function(a) * function(x) < 0:
            b = x
        else:
            a = x
        x = a - ((b - a) * function(a)) / (function(b) - function(a))
        if abs(x - last_x) <= epsilon:
            break
    return x, iteration
def newtons_method(function, x0, epsilon):
    itr = 0
    x1 = x0 - function(x0) / point_derivative(function, x0)
    while abs(x0 - x1) > epsilon and abs(function(x1)) > epsilon:
        x0 = x1
        x1 = x0 - function(x0) / point_derivative(function, x0)
        itr += 1
    return x1, itr
def secant_method(function, x0, epsilon):
    itr = 0
    x1 = x0 - function(x0) / point_derivative(function, x0)
    while abs(x1 - x0) > epsilon:
        x2 = x1 - (x1 - x0) * func(x1) / (func(x1) - func(x0))
        x0, x1 = x1, x2
    itr += 1
    return x1, itr
def iterative_method(function, x0, a, b, epsilon):
    itr = 0
    maximum = 0
    x = a
    while x < b:
        maximum = max(maximum, abs(point_derivative(function, x)))
        x += epsilon
        if point_derivative(function, a) > 0:
            l = -maximum**-1
        else:
            l = maximum**-1
    phi = lambda x: x + l * function(x)
    x = phi(x0)
    while abs(x - x0) > epsilon or abs(function(x)) > epsilon:
        x, x0 = phi(x), x
        itr += 1
    return x, itr

def verification(function, a, b, epsilon=0.0001):
    if function(a) * function(b) < 0:
        x = a
        while x < b:
            x += epsilon
        return True
    return False
def graph(function, a, b, root, epsilon):
    args = []
    vals = []
    x = a
    while x < b:
        args.append(x)
        vals.append(function(x))
        x += epsilon
    plt.xlabel("x")
    plt.ylabel("y")
    plt.plot(args, vals, 'g')
    plt.annotate("x", xy=(root[0], function(root[0])))
    plt.plot([a,b], [0,0], 'b')
    plt.show()
def point_derivative(function, x0, dx = 0.0001):
    return (function(x0 + dx) - function(x0)) / dx
# TODO: добавить выбор начального приближения ради объективной корректности!
def second_point_derivative(function, x0, dx = 0.0001):
    return
print("\033[1;33m+-+-+-+-+-+-" + "Лабораторная работа №2: решение НУ" + "-+-+-+-+-+-+\033[0m")
print("\033[1;32m+-+-" + "Доступные уравнения(всё захардкожено, к сожалению)" + "-+-+\033[0m")
equations = ["1. 2,74x³ - 1,93x² - 15,28x - 3,72", "2. x³ - x + 4", "3. sin(x²) + x - 1", "4. e**cos(x) + x⁷ - 8"]
for equation in equations:
    print("\033[1;34m" + equation + "\033[0m")
case = input("Выберите номер уравнения: ")
if case not in ['1', '2', '3', '4']:
    input_equation = input("Введите уравнение: ")
else:
    case = int(case)
epsilon = float(input("Введите значение эпсилона: "))
match case:
    case 1:
        func = lambda x: 2.74*x**3 - 1.93*x**2 - 15.28*x - 3.72
        intervals = [(-2, -1.8), (-0.3, -0.1), (2.7, 2.9)]
        a, b = intervals[0]
    case 2:
        func = lambda x: x**3 - x + 4
        a, b = -2, -1
    case 3:
        func = lambda x: sin(x**2) + x - 1
        a, b = 0.6, 1
    case 4:
        func = lambda x: e ** cos(x) + x**7 - 8
        a, b = 1, 1.4

fl = input('Хотите ли вы установить интервал изоляции корней? (Д/Y) ')
if fl.casefold() == 'y' or fl.casefold() == 'д':
    while 1:
        print('Введите значения a и b:')
        try:
            a, b = float(input()), float(input())
        except Exception:
            continue
        break

x0 = (a + b) / 2
if not verification(func, a, b):
    print("\033[1;31mДанный интервал [" + str(a) + " " + str(b) + "] не удовлетворяет условие единственности корня на отрезке.", end="")
    exit(0)
if epsilon <= 0.00001:
    places = int(str(epsilon)[-1:]) + 2
else:
    places = str(epsilon)[::-1].find(".") + 2
print("Метод деления пополам:", round(bisection_method(func, a, b, epsilon)[0], places), func(bisection_method(func, a, b, epsilon)[0]), bisection_method(func, a, b, epsilon)[1])
print("\033[1;33mМетод хорд:\033[0m", round(chord_method(func, a, b, epsilon)[0], places), func(chord_method(func, a, b, epsilon)[0]), chord_method(func, a, b, epsilon)[1])
print("Метод Ньютона:", round(newtons_method(func, x0, epsilon)[0], places), func(newtons_method(func, x0, epsilon)[0]), newtons_method(func, x0, epsilon)[1])
print("\033[1;33mМетод секущих:\033[0m", round(secant_method(func, x0, epsilon)[0], places), func(secant_method(func, x0, epsilon)[0]), secant_method(func, x0, epsilon)[1])
print("Метод простой итерации:", round(iterative_method(func, x0, a, b, epsilon)[0], places), func(iterative_method(func, x0, a, b, epsilon)[0]), iterative_method(func, x0, a, b, epsilon)[1])
root = bisection_method(func, a, b, epsilon)
graph(func, -4, 5, root, 0.0001)
