from math import sqrt
from methods import *
from scipy.integrate import quad

def find_discontinuities(function, a, b, eps):
    discontinuities = []
    x = a
    while x <= b:
        try:
            function(x)
        except ArithmeticError:
            discontinuities.append(x)
        x = round(x + eps, 3)
    return discontinuities
def try_to_solve(function, x):
    try:
        return function(x)
    except Exception:
        return None

def solve(function, a, b, eps, method):
    n, k = 4, 0
    i0 = method(function, a, b, n)
    i1 = method(function, a, b, n * 2)
    if method == l_rects_method or method == r_rects_method:
        k = 1
    elif method == mid_rects_method or method == trapezoid_method:
        k = 2
    elif method == simpson_method:
        k = 4
    while abs(i1 - i0) / (2 ** k - 1) > eps:
        n *= 2
        i0 = i1
        i1 = method(function, a, b, n * 2)
    return i1

methods = {
    "Метод левых прямоугольников": l_rects_method,
    "Метод правых прямоугольников": r_rects_method,
    "Метод средних прямоугольников": mid_rects_method,
    "Метод трапеции": trapezoid_method,
    "Метод Симпсона": simpson_method
}
print("\033[1;36m*/*/*/*/*/*/" + "Лабораторная работа №3: численное интегрирование (доп задание)" + "\*\*\*\*\*\*\033[0m")
print("\033[1;34m===============================" + "Доступные уравнения:" + "===================================\033[0m")
eps = 0.0001
functions = ["3 / x", "2 / √x", "4 / (3 - x)", "1 / (4 * x - x ** 3)"]
k = 0
for func in functions:
    k += 1
    print("\033[3;35m" + f"f_{k}(x) = {func}" + "\033[0m")
if eps <= 0.00001:
    places = int(str(eps)[-1:]) + 4
else:
    places = str(eps)[::-1].find(".") + 4
f_order = input("\033[1;32mВведите номер функции (1 - 4): \033[0m")
while f_order not in {"1", "2", "3", "4"}:
    print("\033[1;31mЭто не то.\033[0m")
    f_order = input("\033[1;32mВведите номер функции (1 - 4): \033[0m")

while 1:
    try:
        a = float(input("\033[4;36mВведите нижний предел интеграла (R), a: \033[0m"))
    except Exception:
        continue
    break
while 1:
    try:
        b = float(input("\033[4;36mВведите верхний предел интеграла (R), b: \033[0m"))
    except Exception:
        continue
    break

f_order = int(f_order)
if f_order == 1:
    f = lambda x: 3 / x
elif f_order == 2:
    f = lambda x: 2 / sqrt(x)
elif f_order == 3:
    f = lambda x: 4 / (3 - x)
else:
    f = lambda x: 1 / (4 * x - x ** 3)
cnt = 0
for method in methods:
    cnt += 1
    print("\033[1;34m" + str(cnt) + ".", method, "\033[0m")
print("\033[1;34m6. Все методы\033[0m")
while True:
    try:
        method_order = int(input("\033[4;35mВыберите метод вычисления интеграла: \033[0m"))
    except Exception:
        continue
    break
try:
    discontinuities = find_discontinuities(f, a, b, 0.001)
except ValueError:
    print("\033[7;31mДанный интеграл не определен на заданном промежутке.\033[0m", end = "")
    exit(0)
if not bool(discontinuities):
    print("\033[1;31mРазрыва нет.\033[0m")
else:
    print("\033[1;31mРазрывы: ", *discontinuities, "\033[0m")
for d in discontinuities:
    y1 = try_to_solve(f, d - eps)
    y2 = try_to_solve(f, d + eps)
    if y1 is not None and y2 is not None and abs(y1 - y2) > eps:
        print("\033[7;31mДанный интеграл не сходится.\033[0m", end="")
        exit(0)
stop = False
if not stop:
    if a in discontinuities:
        a += eps
    elif b in discontinuities:
        b -= eps
    elif discontinuities:
        try:
            res = quad(f, a, b)
            res += quad(f, d + eps, b)
            print("\033[1;35m" + f"Точное значение интеграла: I = ∫({functions[f_order - 1]})dx = {round(res[0], places)}" + "\033[0m")
        except ValueError:
            print("Данный интеграл не определен на заданном промежутке.")
        match method_order:
            case 1:
                for d in discontinuities:
                    res = solve(f, a, b, d - eps, l_rects_method)
                    res += solve(f, d + eps, b - eps, eps, l_rects_method)
                print(f"\033[1;35mМетод левых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 2:
                for d in discontinuities:
                    res = solve(f, a, b, d - eps, r_rects_method)
                    res += solve(f, d + eps, b - eps, eps, r_rects_method)
                print(f"\033[1;35mМетод правых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 3:
                for d in discontinuities:
                    res = solve(f, a, b, d - eps, mid_rects_method)
                    res += solve(f, d + eps, b - eps, eps, mid_rects_method)
                print(f"\033[1;35mМетод средних прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 4:
                for d in discontinuities:
                    res = solve(f, a, b, d - eps, trapezoid_method)
                    res += solve(f, d + eps, b - eps, eps, trapezoid_method)
                print(f"\033[1;35mМетод трапеции: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 5:
                for d in discontinuities:
                    res = solve(f, a, b, d - eps, simpson_method)
                    res += solve(f, d + eps, eps - d, eps, simpson_method)
                print(f"\033[1;35mМетод Симпсона: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case _:
                for method in methods:
                    res = solve(f, a, b, d - eps, methods[method])
                    res += solve(f, d + eps, b - eps, eps, methods[method])
                    print(f"\033[1;35m{method}: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")

    if not discontinuities or a - eps in discontinuities or b + eps in discontinuities:
        res = quad(f, a, b)
        print("\033[1;35m" + f"Точное значение интеграла: I = ∫({functions[f_order - 1]})dx = {round(res[0], places)}" + "\033[0m")
        match method_order:
            case 1:
                res = solve(f, a, b, eps, l_rects_method)
                print(f"\033[1;35mМетод левых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 2:
                res = solve(f, a, b, eps, r_rects_method)
                print(f"\033[1;35mМетод правых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 3:
                res = solve(f, a, b, eps, mid_rects_method)
                print(f"\033[1;35mМетод средних прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 4:
                res = solve(f, a, b, eps, trapezoid_method)
                print(f"\033[1;35mМетод трапеции: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case 5:
                res = solve(f, a, b, eps, simpson_method)
                print(f"\033[1;35mМетод Симпсона: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
            case _:
                for method in methods:
                    res = solve(f, a, b, eps, methods[method])
                    print(f"\033[1;35m{method}: I = ∫({functions[f_order - 1]})dx = {round(res, places)}\033[0m")
