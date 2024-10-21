import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import scipy.stats as stats

print(f"Path to data:")
data_df = pd.read_csv(f"data.csv")
data = data_df['data'].values
val_array = [[], [], [], [], [], []]

# 1. Всякие расчеты
for i in [10, 20, 50, 100, 200, 300]:
    mean = data[:i].mean()
    var = np.var(data[:i], ddof=1)
    sigma = np.std(data[:i], ddof=1)
    coef_variation = 100 * (sigma / mean)
    print(mean, var, sigma, coef_variation)
    confidence_intervals = []
    for confidence in [0.9, 0.95, 0.99]:
        conf_int = stats.t.interval(confidence, len(data) - 1, loc=mean, scale=sigma / np.sqrt(len(data)))
        confidence_intervals.append(mean - conf_int[0])
    print(f"{confidence_intervals}\n")

# 2. Заданная числовая последовательность
plt.figure(figsize=(10,6))
plt.plot(data, marker=".", linestyle='-', color='g')
plt.title("График числовой последовательности")
plt.xlabel("Индекс")
plt.ylabel("Значение")
plt.grid(True)
plt.savefig('figs/graph1.png')
plt.show()


# 3. Автокорреляционный анализ
ac = [np.corrcoef(data[:-lag], data[lag:])[0, 1] for lag in range(1,11)]
print(ac)
plt.figure(figsize=(10,6))
plt.stem(range(1, 11), ac, basefmt="")
plt.title("Автокорреляционный анализ (сдвиг 1-10)")
plt.xlabel("Сдвиг")
plt.ylabel("Коэффициент автокорреляции")
plt.grid(True)
plt.savefig('figs/autocorrelation.png')
plt.show()


# 4. Гистограмма распределения частот
plt.figure(figsize=(10,6))
plt.hist(data, bins=20, edgecolor='yellow', alpha=0.8)
plt.title("Гистограмма распределения частот")
plt.xlabel("Значение")
plt.ylabel("Частота")
plt.grid(True)
plt.savefig('figs/histogram1.png')
plt.show()


def uniform_distribution(data):
    """Аппроксимация равномерного распределения"""
    params = stats.uniform.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.uniform.pdf(x, *params)
    return x, y, "Равномерное распределение"

def exponential_distribution(data):
    """Аппроксимация экспоненциального распределения"""
    params = stats.expon.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.expon.pdf(x, *params)
    return x, y, "Экспоненциальное распределение"

def hyperexponential_distribution(data):
    """Аппроксимация гиперэкспоненциального распределения"""
    # Ваш код для гиперэкспоненциального распределения
    lambdas = [0.02, 0.05, 0.08]
    probs = [0.2, 0.3, 0.5]
    hyperexp_values = hyperexponential_gen(lambdas, probs, len(data))
    x = np.linspace(np.min(hyperexp_values), np.max(hyperexp_values), 100)
    y = stats.gaussian_kde(hyperexp_values).evaluate(x)  # Оценка плотности
    return x, y, "Гиперэкспоненциальное распределение"

def hyperexponential_gen(lambdas, probs, size):
    """
    lambdas - список интенсивностей для разных компонент (экспоненциальных распределений)
    probs - вероятности выбора каждой компоненты
    size - размер последовательности
    """
    assert len(lambdas) == len(probs), "Количество интенсивностей должно совпадать с количеством вероятностей."
    assert np.isclose(np.sum(probs), 1), "Сумма вероятностей должна быть равна 1."

    # Выбира компоненты по вероятностям и генерирация экспоненциального распределения
    component_choices = np.random.choice(len(lambdas), size=size, p=probs)
    hyperexp_values = np.array([np.random.exponential(1 / lambdas[i]) for i in component_choices])

    return hyperexp_values

# 5. Аппроксимация распределения
def approximate_distribution(data, coef_variation):
    """Выбор и аппроксимация распределения в зависимости от коэффициента вариации"""
    if coef_variation < 30:
        return uniform_distribution(data)
    elif 30 <= coef_variation < 100:
        return exponential_distribution(data)
    else:
        return hyperexponential_distribution(data)

x, y, dist_name = approximate_distribution(data, coef_variation)

plt.figure(figsize=(10,6))
plt.plot(x, y, label=f'{dist_name}', color='b')
plt.title(f'Аппроксимация распределения: {dist_name}')
plt.xlabel('Значение')
plt.ylabel('Плотность вероятности')
plt.grid(True)
plt.legend()
plt.savefig('figs/comparison_graph.png')
plt.show()


plt.figure(figsize=(10,6))
plt.hist(data, bins=20, density=True, alpha=0.6, color='g', edgecolor='black', label='Исходные данные')

# Аппроксимированное распределение
plt.plot(x, y, label=f'Аппроксимированное распределение: {dist_name}', color='b', linewidth=2)

plt.title("Сравнение исходных данных и аппроксимированного распределения")
plt.xlabel("Значение")
plt.ylabel("Плотность вероятности")
plt.legend()
plt.grid(True)
plt.savefig('figs/comparison_histogram.png')
plt.show()


# Функция для расчета статистических моментов
def calculate_moments(data, pos):
    mean = data[:pos].mean()
    var = np.var(data[:pos], ddof=1)
    sigma = np.std(data[:pos], ddof=1)
    coef_variation = 100 * (sigma / mean)
    confidence_intervals = []
    for confidence in [0.9, 0.95, 0.99]:
        conf_intervals = stats.t.interval(confidence, len(data) - 1, loc=mean, scale=sigma / np.sqrt(len(data)))
        confidence_intervals.append(mean - list(conf_intervals)[0])
    return mean, var, sigma, coef_variation, confidence_intervals

# Оценка корреляционной зависимости между исходной и сгенерированной последовательностями
def evaluate_correlation(original_data, generated_data):
    correlation_matrix = np.corrcoef(original_data, generated_data)
    correlation_value = correlation_matrix[0, 1]  # Достаем коэффициент корреляции между двумя последовательностями
    return correlation_value

lambdas = [0.02, 0.05, 0.08]
probs = [0.15, 0.35, 0.5]
size = 300

generated_data = hyperexponential_gen(lambdas, probs, size)
correlation_value = evaluate_correlation(data[:size], generated_data)

print(f"Коэффициент корреляции между исходными и сгенерированными данными: {correlation_value:.4f}")

# Для графической иллюстрации корреляции можно дополнительно визуализировать на одном графике исходные и сгенерированные данные
plt.figure(figsize=(10, 6))
plt.plot(data[:size], marker=".", linestyle='-', color='g', label="Исходные данные")
plt.plot(generated_data, marker=".", linestyle='-', color='b', label="Сгенерированные данные")
plt.title(f'Сравнение значений исходной и сгенерированной последовательностей\n(Корреляция: {correlation_value:.4f})')
plt.xlabel("Индекс")
plt.ylabel("Значение")
plt.legend()
plt.grid(True)
plt.savefig("figs/graph_unknown1.png")
plt.show()

# Визуализация гистограммы сгенерированных данных
plt.figure(figsize=(10,6))
plt.hist(generated_data, bins=20, density=True, alpha=0.6, color='b', edgecolor='black', label='Сгенерированные данные')
plt.title('Гистограмма сгенерированной последовательности (гиперэкспоненциальное распределение)')
plt.xlabel('Значение')
plt.ylabel('Частота')
plt.grid(True)
plt.legend()
plt.savefig("figs/graph_unknown2.png")
plt.show()

# Плотность вероятности для сгенерированных данных
x = np.linspace(np.min(generated_data), np.max(generated_data), 100)
y = stats.gaussian_kde(generated_data).evaluate(x)

plt.figure(figsize=(10,6))
plt.plot(x, y, label='Плотность сгенерированных данных', color='b')
plt.title('Плотность вероятности для сгенерированных данных (гиперэкспоненциальное распределение)')
plt.xlabel('Значение')
plt.ylabel('Плотность вероятности')
plt.grid(True)
plt.legend()
plt.savefig("figs/graph_unknown3.png")
plt.show()

# Сравнение аппроксимированных и сгенерированных данных
plt.figure(figsize=(10,6))
plt.hist(data, bins=20, density=True, alpha=0.6, color='g', edgecolor='black', label='Исходные данные')
plt.plot(x, y, label='Плотность сгенерированных данных', color='b', linewidth=2)

plt.title("Сравнение исходных данных и сгенерированной последовательности")
plt.xlabel("Значение")
plt.ylabel("Плотность вероятности")
plt.legend()
plt.grid(True)
plt.savefig("figs/graph_unknown4.png")
plt.show()


def uniform_distribution(data):
    """Аппроксимация равномерного распределения"""
    params = stats.uniform.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.uniform.pdf(x, *params)
    return x, y, "Равномерное распределение"

def exponential_distribution(data):
    """Аппроксимация экспоненциального распределения"""
    params = stats.expon.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.expon.pdf(x, *params)
    return x, y, "Экспоненциальное распределение"

def hyperexponential_distribution(data):
    """Аппроксимация гиперэкспоненциального распределения"""
    # Ваш код для гиперэкспоненциального распределения
    lambdas = [0.02, 0.05, 0.08]
    probs = [0.2, 0.3, 0.5]
    hyperexp_values = hyperexponential_gen(lambdas, probs, len(data))
    x = np.linspace(np.min(hyperexp_values), np.max(hyperexp_values), 100)
    y = stats.gaussian_kde(hyperexp_values).evaluate(x)  # Оценка плотности
    return x, y, "Гиперэкспоненциальное распределение"

def hyperexponential_gen(lambdas, probs, size):
    """
    Lambdas - список интенсивностей для разных компонент (экспоненциальных распределений)
    probs - вероятности выбора каждой компоненты
    size - размер последовательности
    """
    assert len(lambdas) == len(probs), "Количество интенсивностей должно совпадать с количеством вероятностей."
    assert np.isclose(np.sum(probs), 1), "Сумма вероятностей должна быть равна 1."

    # Выбираем компоненты по вероятностям и генерирация экспоненциального распределения
    component_choices = np.random.choice(len(lambdas), size=size, p=probs)
    hyperexp_values = np.array([np.random.exponential(1 / lambdas[i]) for i in component_choices])

    return hyperexp_values

plt.figure(figsize=(10,6))
plt.hist(data, bins=20, density=True, alpha=0.6, color='g', edgecolor='black', label='Исходные данные')

# Объединенная функция для генерации, расчета моментов и вывода сравнения
def generate_and_compare(data, lambdas, probs, size):
    for i in [10, 20, 50, 100, 200, 300]:
        # Генерация последовательности по гиперэкспоненциальному распределению
        generated_data = hyperexponential_gen(lambdas, probs, size)

        # Расчет моментов
        original_mean, original_var, original_sigma, original_coef_variation, original_interval = calculate_moments(data, i)
        generated_mean, generated_var, generated_sigma, generated_coef_variation, generated_interval = calculate_moments(generated_data, i)

        # Вывод для сравнения
        #print(f"Исходные данные: Мат. ожидание = {original_mean:.2f}; Дисперсия = {original_var:.2f}; СКО = {original_sigma:.2f}; КВ = {original_coef_variation:.2f}; Дов.инт. = {original_interval}%")
        print(f"Сгенерированные данные: Мат. ожидание = {generated_mean:.2f}; Дисперсия = {generated_var:.2f}; СКО = {generated_sigma:.2f}; КВ = {generated_coef_variation:.2f}; Дов.инт. = {generated_interval}%".replace(".", ","))

    # Визуализация
    plt.figure(figsize=(10,6))
    plt.hist(generated_data, bins=20, density=True, alpha=0.6, color='b', edgecolor='black', label='Сгенерированные данные')
    plt.title('Гистограмма сгенерированной последовательности (гиперэкспоненциальное распределение)')
    plt.xlabel('Значение')
    plt.ylabel('Частота')
    plt.grid(True)
    plt.legend()
    plt.savefig("figs/graph_unknown5.png")
    plt.show()


    # Визуализация 2: Сравнение гистограмм частот
    plt.figure(figsize=(10, 6))
    plt.hist(data, bins=20, density=True, alpha=0.6, color='g', edgecolor='black', label='Исходные данные')
    plt.hist(generated_data, bins=20, density=True, alpha=0.6, color='b', edgecolor='black',
             label='Сгенерированные данные')
    plt.title('Сравнение гистограмм исходной и сгенерированной последовательностей')
    plt.xlabel('Значение')
    plt.ylabel('Частота')
    plt.legend()
    plt.grid(True)
    plt.savefig("figs/graph_unknown6.png")
    plt.show()

lambdas = [0.02, 0.05, 0.08]
probs = [0.15, 0.35, 0.5]
size = 300
generate_and_compare(data, lambdas, probs, size)

def autocorrelation_analysis(data, max_lag=10):
    """Для данных с разными сдвигами"""
    ac_values = [np.corrcoef(data[:-lag], data[lag:])[0, 1] for lag in range(1, max_lag+1)]
    return ac_values

max_lag = 10
ac_values = autocorrelation_analysis(generated_data, max_lag)

for lag, ac in enumerate(ac_values, start=1):
    print(f"Сдвиг {lag}: Коэффициент автокорреляции = {ac:.4f}")
