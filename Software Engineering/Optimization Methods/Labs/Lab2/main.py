from math import log
def derivative(x: float) -> float:
    return 2*x - 2 + log(x)
def derivative2(x: float) -> float:
    return 2 + 1 / x
def bisection_method(function, a, b, epsilon):
    i = 2
    while abs(b - a) > 2 * epsilon:
        i+=1
        x1, x2 = round((a + b - epsilon) / 2, ndigits=i), round((a + b + epsilon) / 2, ndigits=i)
        y1, y2 = function(x1), function(x2)
        if y1 > y2:
            a = float(x1)
        else:
            b = float(x2)
        # print(f"Итерация №{i}: a = {a}, b = {b}, x1 = {x1}, x2 = {x2}, y1 = {y1}, y2 = {y2}, |b - a| = {round(abs(b - a), ndigits=i+3)}")
    xm = round((a + b) / 2, ndigits=i)
    ym = function(xm)
    return xm, ym
def golden_ratio_method(function, a, b, epsilon):
    i = 2
    x1, x2 = round(b - round((b - a) / 1.618, ndigits=3), ndigits=3), round(a + round((b - a) / 1.618, ndigits=3), ndigits=3)
    y1, y2 = function(x1), function(x2)
    # print(f"Итерация №{1}: a = {a}, b = {b}, x1 = {x1}, x2 = {x2}, y1 = {y1}, y2 = {y2}, |b - a| = {round(abs(b - a), ndigits=i+3)}")
    while abs(b - a) > epsilon:
        i += 1
        if y1 < y2:
            b = x2
            x2 = x1
            y2 = y1
            x1 = round(b - round((b - a) / 1.618, ndigits=3), ndigits=3)
            y1 = function(x1)
        else:
            a = x1
            x1 = x2
            y1 = y2
            x2 = round(a + round((b - a) / 1.618, ndigits=3), ndigits=3)
            y2 = function(x2)
        # print(f"Итерация №{i-1}: a = {a}, b = {b}, x1 = {x1}, x2 = {x2}, y1 = {y1}, y2 = {y2}, |b - a| = {round(abs(b - a), ndigits=i+3)}")
    xm = (a + b) / 2
    ym = function(xm)
    return xm, ym

def chord_method(function, a, b, epsilon):
    i = 2
    d_a = derivative(a)
    d_b = derivative(b)
    _x = a - (d_a / (d_a - d_b)) * (a - b)
    while abs(derivative(_x)) > epsilon:
        i += 1
        # print(f"Итерация №{i - 2}: a = {a}, b = {b}, f'(a) = {d_a}, f'(b) = {d_b}, _x={_x}, |f'(_x)| = {derivative(_x)}")
        if derivative(_x) > 0:
            b = _x
        else:
            a = _x
        _x = a - (d_a / (d_a - d_b)) * (a - b)
    # print(f"Итерация №{i - 1}: a = {a}, b = {b}, f'(a) = {d_a}, f'(b) = {d_b}, _x={_x}, |f'(_x)| = {derivative(_x)}")
    return _x, function(_x)

def newton_method(function, a, b, epsilon):
    i = 2
    x = round((a + b) / 2, ndigits=i)
    # print(f"Итерация №{1}: x = {x}, f'(x) = {derivative(x)}, f\"(x) = {derivative2(x)}, |f'(x)| = {abs(derivative(x))}")
    while abs(derivative(x)) > epsilon:
        i += 1
        x = x - (derivative(x) / derivative2(x))
        # print(f"Итерация №{i - 1}: x = {x}, f'(x) = {derivative(x)}, f\"(x) = {derivative2(x)}, |f'(x)| = {abs(derivative(x))}")
    return x, function(x)
def main():
    function = lambda x: x**2 - 3 * x + x * log(x)
    a, b, epsilon = 1, 2, 20**-1
    golden_ratio_method(function, a, b, epsilon)
    result_list = [bisection_method(function, a, b, epsilon), golden_ratio_method(function, a, b, epsilon), chord_method(function, a, b, epsilon), newton_method(function, a, b, epsilon)]
    print("\033[2;31m" + "Лабораторная работа №2: одномерная оптимизация" + "\033[0m")
    print("\033[1;35m" + "1. Метод половинного деления", "2. Метод золотого сечения", "3. Метод хорд", "4. Метод Ньютона" + "\033[0m", sep="\n")
    match input("\033[1;32m" + "Введите номер метода (или что-то другое для получения значений всех методов):\n" + "\033[0m"):
        case '1':
            xm, ym = result_list[0]
            print("\033[3;36m" + f"x_m = {xm}, y_m = {ym}" + "\033[0m")
        case '2':
            xm, ym = result_list[1]
            print("\033[3;36m" + f"x_m = {xm}, y_m = {ym}" + "\033[0m")
        case '3':
            xm, ym = result_list[2]
            print("\033[3;36m" + f"x_m = {xm}, y_m = {ym}" + "\033[0m")
        case '4':
            xm, ym = result_list[3]
            print("\033[3;36m" + f"x_m = {xm}, y_m = {ym}" + "\033[0m")
        case _:
            for i in range(len(result_list)):
                xm, ym = result_list[i]
                print("\033[3;36m" + f"{i + 1}. x_m = {xm}, y_m = {ym}" + "\033[0m")

if __name__ == "__main__":
    main()
