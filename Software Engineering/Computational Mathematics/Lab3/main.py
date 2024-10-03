from methods import *
from scipy.integrate import quad

def solve(function, a, b, eps, method):
    k = 1
    n = 8
    i0 = method(function, a, b, int(n / 2))
    i1 = method(function, a, b, n)
    if method == l_rects_method or method == r_rects_method:
        k = 1
    elif method == mid_rects_method or method == trapezoid_method:
        k = 2
    elif method == simpson_method:
        k = 4
    while abs(i1 - i0) / (2**k - 1) > eps:
        i0 = i1
        n *= 2
        i1 = method(function, a, b, n)
    return i1, n

methods = {
    "Метод левых прямоугольников": l_rects_method,
    "Метод правых прямоугольников": r_rects_method,
    "Метод средних прямоугольников": mid_rects_method,
    "Метод трапеции": trapezoid_method,
    "Метод Симпсона": simpson_method
}
k = 0
eps = 0.0001
if eps <= 0.00001:
    places = int(str(eps)[-1:]) + 4
else:
    places = str(eps)[::-1].find(".") + 4
print("\033[1;36m*/*/*/*/*/*/" + "Лабораторная работа №3: численное интегрирование" + "\*\*\*\*\*\*\033[0m")
print("\033[1;34m========================" + "Доступные уравнения:" + "============================\033[0m")
functions = ["-x ^ 3 - x ^ 2 - 2 * x + 1", "x ^ 3 + 4 * x", "x ^ 4"]
for func in functions:
    k += 1
    print("\033[3;35m" + f"f_{k}(x) = {func}" + "\033[0m")
f_order = input("\033[1;32mВведите номер функции (1 - 3): \033[0m")
while f_order not in {"1", "2", "3"}:
    print("\033[1;31mЭто не то.\033[0m")
    f_order = input("\033[1;32mВведите номер функции (1 - 3): \033[0m")

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
    f = lambda x: -x ** 3 - x ** 2 - 2 * x + 1
elif f_order == 2:
    f = lambda x: x ** 3 + 4 * x
else:
    f = lambda x: x ** 4
k = 0
for method in methods:
    k += 1
    print("\033[1;34m" + str(k) + ".", method, "\033[0m")
print("\033[1;34m6. Все методы\033[0m")
while True:
    try:
        method_order = int(input("\033[4;35mВыберите метод вычисления интеграла: \033[0m"))
    except Exception:
        continue
    break
res = quad(f, a, b)
print("\033[1;35m" + f"Точное значение интеграла: I = ∫({functions[f_order - 1]})dx = {round(res[0], places)}" + "\033[0m")
match method_order:
    case 1:
        res, n = solve(f, a, b, eps, l_rects_method)
        print(f"Метод левых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}, n = {n}")
    case 2:
        res, n = solve(f, a, b, eps, r_rects_method)
        print(f"Метод правых прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}, n = {n}")
    case 3:
        res, n = solve(f, a, b, eps, mid_rects_method)
        print(f"Метод средних прямоугольников: I = ∫({functions[f_order - 1]})dx = {round(res, places)}, n = {n}")
    case 4:
        res, n = solve(f, a, b, eps, trapezoid_method)
        print(f"Метод трапеции: I = ∫({functions[f_order - 1]})dx = {round(res, places)}, n = {n}")
    case 5:
        res, n = solve(f, a, b, eps, simpson_method)
        print(f"Метод Симпсона: I = ∫({functions[f_order - 1]})dx = {round(res. places)}, n = {n}")
    case _:
        for method in methods:
            res, n = solve(f, a, b, eps, methods[method])
            print(f"{method}: I = ∫({functions[f_order - 1]})dx = {round(res, places)}, n = {n}")
