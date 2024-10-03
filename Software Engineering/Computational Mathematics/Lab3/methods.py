def l_rects_method(function, a, b, n):
    h = (b - a) / n
    x_list = [a + i * h for i in range(n + 1)]
    return h * sum([function(x_list[i]) for i in range(n)])

def r_rects_method(function, a, b, n):
    h = (b - a) / n
    x_list = [a + i * h for i in range(n + 1)]
    return h * sum([function(x_list[i]) for i in range(1, n + 1)])

def mid_rects_method(function, a, b, n):
    h = (b - a) / n
    x_list = [a + i * h for i in range(n + 1)]
    return h * sum([function((x_list[i - 1] + x_list[i]) / 2) for i in range(1, n + 1)])

def trapezoid_method(function, a, b, n):
    h = (b - a) / n
    x_list = [a + i * h for i in range(n + 1)]
    y_list = [function(x) for x in x_list]
    return h * ((y_list[0] + y_list[n]) / 2 +
                sum([y_list[i] for i in range(1, n)]))

def simpson_method(function, a, b, n):
    h = (b - a) / n
    x_list = [a + i * h for i in range(n + 1)]
    y_list = [function(x) for x in x_list]
    return h / 3 * (y_list[0] +
                    4 * sum([y_list[i] for i in range(1, n, 2)]) +
                    2 * sum([y_list[i] for i in range(2, n - 1, 2)]) +
                    y_list[n])
