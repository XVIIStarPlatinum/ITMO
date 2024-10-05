from functools import reduce
from math import factorial
import pandas as pd

def calc_lagrange_polynomial(xs, ys):
    n = len(xs) - 1
    f = lambda x: sum([ys[i] *
                       reduce(lambda a, b: a * b,
                              [(x - xs[j]) / (xs[i] - xs[j])
                               for j in range(n + 1) if i != j])
                       for i in range(n + 1)])
    return f

def calc_newton_finite_difference_polynomial(xs, ys, x):
    median = (xs[len(xs) // 2] + xs[len(xs) // 2 + 1]) / 2
    fin_difs = [ys[:]]
    n = len(xs) - 1
    for k in range(1, n + 1):
        last = fin_difs[-1][:]
        fin_difs.append(
            [last[i + 1] - last[i] for i in range(n - k + 1)])
    print("Конечные разности:")
    df = pd.DataFrame(fin_difs)
    df = df.transpose()
    for i in range(len(df)):
        print(*df.iloc[i].values, sep='\t')
    print('-' * 60)
    h = xs[1] - xs[0]
    if x < median:
        f = lambda x: ys[0] + sum([
            reduce(lambda a, b: a * b,
                [(x - xs[0]) / h - j for j in range(k)])
            * fin_difs[k][0] / factorial(k)
            for k in range(1, n + 1)])
    else:
        f = lambda x: ys[n] + sum([
            reduce(lambda a, b: a * b,
                [(x - xs[n]) / h + j for j in range(0, n - k + 1)])
            * fin_difs[n - k + 1][k - 1] / factorial(n - k + 1)
            for k in range(n, 0, -1)])
    return f

def calc_gauss_polynomial(xs, ys, x):
    n = len(xs) - 1
    alpha_ind = n // 2
    fin_difs = []
    fin_difs.append(ys[:])
    for k in range(1, n + 1):
        last = fin_difs[-1][:]
        fin_difs.append(
            [last[i + 1] - last[i] for i in range(n - k + 1)])
    h = xs[1] - xs[0]
    dts1 = [0, -1, 1, -2, 2, -3, 3, -4, 4]
    f1 = lambda x: ys[alpha_ind] + sum([
        reduce(lambda a, b: a * b,
               [(x - xs[alpha_ind]) / h + dts1[j] for j in range(k)])
        * fin_difs[k][len(fin_difs[k]) // 2] / factorial(k)
        for k in range(1, n + 1)])
    f2 = lambda x: ys[alpha_ind] + sum([
        reduce(lambda a, b: a * b,
               [(x - xs[alpha_ind]) / h - dts1[j] for j in range(k)])
        * fin_difs[k][len(fin_difs[k]) // 2 - (1 - len(fin_difs[k]) % 2)] / factorial(k)
        for k in range(1, n + 1)])
    return lambda x: f1(x) if x > xs[alpha_ind] else f2(x)
