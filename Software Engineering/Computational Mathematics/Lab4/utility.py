import inspect
from math import sqrt, exp, log

def add_col(m, col):
    for k, row in enumerate(m):
        row.append(col[k])
    return m

def remove_last_col(m):
    for k, row in enumerate(m):
        row.pop()
    return m

def plus(src, ind, m):
    for i in range(src + 1, len(m)):
        _plus(src, i, m, -m[i][ind] / m[src][ind])

def _plus(src, dest, m, mul: float = 1):
    for i in range(len(m[0])):
        m[dest][i] += m[src][i] * mul

def swap(src, dest, m):
    m[src], m[dest] = m[dest], m[src]

def rang(m):
    return sum(any(row) for row in m)

def determinant(m, k):
    p = 1
    for i in range(len(m)):
        p *= m[i][i]
    return (-1) ** k * p

def solve(m):
    k = 0
    row = 0
    col = 0
    n = len(m)
    while col < n:
        for j in range(row, n):
            if m[j][col]:
                swap(j, row, m)
                k += row != j
                plus(row, col, m)
                row += 1
                break
        col += 1
    xs = []
    for i in range(len(m)):
        x = m[len(m) - i - 1][-1]
        for j in range(1, i + 2):
            if j == i + 1:
                x /= m[len(m) - i - 1][-j - 1]
            else:
                x -= m[len(m) - i - 1][-j - 1] * xs[j - 1]
        xs.append(x)
    return xs[::-1]

def summator_of_exponents(arr, exp: int):
    res = []
    for i in range(1, exp + 1):
        res.append(sum(a ** i for a in arr))
    return res

def summator_of_products(arr1, arr2, exp: int):
    res = []
    for i in range(exp + 1):
        res.append(sum(x ** i * y for x, y in zip(arr1, arr2)))
    return res

def calc_deviation(xs, ys, fi):
    return sum((eps ** 2 for eps in [fi(x) - y for x, y in zip(xs, ys)]))

def calc_standard_deviation(xs, ys, fi, n):
    return sqrt(sum(((fi(x) - y) ** 2 for x, y in zip(xs, ys))) / n)

def calc_pearson_correlation_coefficient(xs, ys, n):
    av_x, av_y = sum(xs) / n, sum(ys) / n
    return sum((x - av_x) * (y - av_y) for x, y in zip(xs, ys)) / \
           sqrt(sum((x - av_x) ** 2 for x in xs) *
                sum((y - av_y) ** 2 for y in ys))

def calc_coefficient_of_determination(xs, ys, fi, n):
    return 1 - sum((y - fi(x)) ** 2 for x, y in zip(xs, ys)) / (sum(fi(x) ** 2 for x in xs) - sum(fi(x) for x in xs) ** 2 / n)

def get_str_content_of_func(func):
    str_func = inspect.getsourcelines(func)[0][0]
    return str_func.split('lambda x: ')[-1].split(',')[0].strip()

def read_number(s: str):
    while True:
        try:
            return float(input(s))
        except ValueError:
            continue