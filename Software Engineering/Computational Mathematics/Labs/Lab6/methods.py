def euler_method(f, args, y0, h):
    ords = [y0]
    for i in range(1, len(args)):
        y = ords[i - 1] + h * f(args[i - 1], ords[i - 1])
        ords.append(y)
    return ords

def modified_euler_method(f, args, y0, h):
    ords = [y0]
    for i in range(1, len(args)):
        ords.append(ords[i - 1] + h * f(args[i - 1] + h / 2, ords[i - 1] + h / 2 * f(args[i - 1], ords[i - 1])))
    return ords

def fourth_order_runge_kutta_method(f, args, y0, h):
    ords = [y0]
    for i in range(1, len(args)):
        x, y = args[i - 1], ords[i - 1]
        k1 = h * f(x, y)
        k2 = h * f(x + h / 2, y + k1 / 2)
        k3 = h * f(x + h / 2, y + k2 / 2)
        k4 = h * f(x + h, y + k3)
        y_next = y + (k1 + 2 * k2 + 2 * k3 + k4) / 6
        ords.append(y_next)
    return ords

def milne_method(f, args, y0, h, eps=1e-7):
    ords = fourth_order_runge_kutta_method(f, args[:4], y0, h)
    for i in range(4, len(args)):
        pre_y = ords[i - 4] + 4 * h / 3 * \
                (2 * f(args[i - 3], ords[i - 3]) -
                 f(args[i - 2], ords[i - 2]) +
                 2 * f(args[i - 1], ords[i - 1]))
        cor_y = ords[i - 2] + h / 3 * \
                (f(args[i - 2], ords[i - 2]) +
                 4 * f(args[i - 1], ords[i - 1]) +
                 f(args[i], pre_y))
        while abs(pre_y - cor_y) > eps:
            pre_y = cor_y
            cor_y = ords[i - 2] + h / 3 * \
                    (f(args[i - 2], ords[i - 2]) +
                     4 * f(args[i - 1], ords[i - 1]) +
                     f(args[i], pre_y))
        ords.append(cor_y)
    return ords

def adams_method(f, args, y0, h):
    ords = fourth_order_runge_kutta_method(f, args[:4], y0, h)
    for i in range(4, len(args)):
        df = f(args[i - 1], ords[i - 1]) - f(args[i - 2], ords[i - 2])
        d2f = f(args[i - 1], ords[i - 1]) - 2 * f(args[i - 2], ords[i - 2]) + f(args[i - 3], ords[i - 3])
        d3f = f(args[i - 1], ords[i - 1]) - 3 * f(args[i - 2], ords[i - 2]) + 3 * f(args[i - 3], ords[i - 3]) - f(args[i - 4], ords[i - 4])
        y = ords[i - 1] + h * f(args[i - 1], ords[i - 1]) + h ** 2 / 2 * df + 5 * h ** 3 / 12 * d2f + 3 * h ** 4 / 8 * d3f
        ords.append(y)
    return ords
