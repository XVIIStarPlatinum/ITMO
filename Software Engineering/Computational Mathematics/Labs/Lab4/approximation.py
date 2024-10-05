from methods import *
import matplotlib.pyplot as plt

def draw_plot(a, b, func, dx=0.1):
    xs, ys = [], []
    a -= dx
    b += dx
    x = a
    while x <= b:
        xs.append(x)
        ys.append(func(x))
        x += dx
    plt.plot(xs, ys, 'g')

if __name__ == '__main__':
    read_number("Введите количество точек: ")
    xs = list(map(float, input("x: ").split()))
    ys = list(map(float, input("y: ").split()))
    if len(xs) != len(ys):
        print("Некорректные данные")
    n = len(xs)
    names = {
        linear_approximation: "Линейная",
        power_approximation: "Степенная",
        exponential_approximation: "Экспоненциальная",
        logarithmic_approximation: "Логарифмическая",
        quadratic_approximation: "Квадратичная",
        cubic_approximation: "Кубическая"
    }
    if all(map(lambda x: x > 0, xs)) and all(map(lambda x: x > 0, ys)):
        approximation_funcs = [
            linear_approximation,
            power_approximation,
            exponential_approximation,
            logarithmic_approximation,
            quadratic_approximation,
            cubic_approximation
        ]
    else:
        approximation_funcs = [
            linear_approximation,
            quadratic_approximation,
            cubic_approximation
        ]
    best_sigma = float('inf')
    best_apprxmt_f = None
    for apprxmt_f in approximation_funcs:
        print(names[apprxmt_f], ": ")
        fi, *coeffs = apprxmt_f(xs, ys, n)
        s = calc_deviation(xs, ys, fi)
        sigma = calc_standard_deviation(xs, ys, fi, n)
        if sigma < best_sigma:
            best_sigma = sigma
            best_apprxmt_f = apprxmt_f
        r2 = calc_coefficient_of_determination(xs, ys, fi, n)
        print('fi(x) =', get_str_content_of_func(fi))
        print(f'coeffs:', list(map(lambda cf: round(cf, 4), coeffs)))
        print(f'S = {s:.5f}, σ = {sigma:.5f}, R^2 = {r2:.5f}')
        if apprxmt_f is linear_approximation:
            print('r =', calc_pearson_correlation_coefficient(xs, ys, n))
        plt.title(names[apprxmt_f])
        draw_plot(xs[0], xs[-1], fi)
        for i in range(n):
            plt.scatter(xs[i], ys[i], c='r')
        plt.xlabel("X")
        plt.ylabel("Y")
        plt.show()
        print('-' * 50)
    print(f'Лучшая функция: {names[best_apprxmt_f]}')
