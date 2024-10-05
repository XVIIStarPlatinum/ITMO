from utility import *

def linear_approximation(xs, ys, n):
    args = summator_of_exponents(xs, 2)
    vals = summator_of_products(xs, ys, 1)
    ext_matrix = add_col(
        [
            [n, args[0]],
            [args[0], args[1]]
        ],
        [vals[0], vals[1]]
    )

    a, b = solve(ext_matrix)
    return lambda x: a + b * x, a, b
def quadratic_approximation(xs, ys, n):
    args = summator_of_exponents(xs, 4)
    vals = summator_of_products(xs, ys, 2)
    ext_matrix = add_col(
        [
            [n, args[0], args[1]],
            [args[0], args[1], args[2]],
            [args[1], args[2], args[3]]
        ],
        [vals[0], vals[1], vals[2]]
    )

    a, b, c = solve(ext_matrix)
    return lambda x: a + b * x + c * x ** 2, a, b, c

def cubic_approximation(xs, ys, n):
    args = summator_of_exponents(xs, 6)
    vals = summator_of_products(xs, ys, 3)
    ext_matrix = add_col(
        [
            [n, args[0], args[1], args[2]],
            [args[0], args[1], args[2], args[3]],
            [args[1], args[2], args[3], args[4]],
            [args[2], args[3], args[4], args[5]]
        ],
        [vals[0], vals[1], vals[2], vals[3]]
    )

    a, b, c, d = solve(ext_matrix)
    return lambda x: a + b * x + c * x ** 2 + d * x ** 3, \
           a, b, c, d

def exponential_approximation(xs, ys, n):
    ys_ = list(map(log, ys))
    _, a_, b_ = linear_approximation(xs, ys_, n)
    a = exp(a_)
    b = b_
    return lambda x: a * exp(b * x), a, b

def logarithmic_approximation(xs, ys, n):
    xs_ = list(map(log, xs))
    _, a_, b_ = linear_approximation(xs_, ys, n)
    a = a_
    b = b_
    return lambda x: a + b * log(x), a, b

def power_approximation(xs, ys, n):
    xs_ = list(map(log, xs))
    ys_ = list(map(log, ys))
    _, a_, b_ = linear_approximation(xs_, ys_, n)
    a = exp(a_)
    b = b_
    return lambda x: a * x ** b, a, b