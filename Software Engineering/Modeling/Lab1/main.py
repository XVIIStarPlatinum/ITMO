import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import scipy.stats as stats

print(f"Path to data: data.csv")
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
        confidence_intervals.append(conf_int)
        #print(f"{conf_int}\n")

# 2. Заданная числовая последовательность
plt.figure(figsize=(10,6))
plt.plot(data, marker=".", linestyle='-', color='g')
plt.title("График числовой последовательности")
plt.xlabel("Индекс")
plt.ylabel("Значение")
plt.grid(True)
plt.show()

# 3. Автокорреляционный анализ
ac = [np.corrcoef(data[:-lag], data[lag:])[0, 1] for lag in range(1,11)]
plt.figure(figsize=(10,6))
plt.stem(range(1, 11), ac, basefmt="")
plt.title("Автокорреляционный анализ (сдвиг 1-10)")
plt.xlabel("Сдвиг")
plt.ylabel("Коэффициент автокорреляции")
plt.grid(True)
plt.show()

# 4. Гистограмма распределения частот
plt.figure(figsize=(10,6))
plt.hist(data, bins=20, edgecolor='yellow', alpha=0.8)
plt.title("Гистограмма распределения частот")
plt.xlabel("Значение")
plt.ylabel("Частота")
plt.grid(True)
plt.show()

# 5. Аппроксимация распределения
if coef_variation < 30:
    dist = "Равномерное распределение"
    params = stats.uniform.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.uniform.pdf(x, *params)
if 30 <= coef_variation < 100:
    dist = "Экспоненциальное распределение"
    params = stats.expon.fit(data)
    x = np.linspace(np.min(data), np.max(data), 100)
    y = stats.expon.pdf(x, *params)
else:
    dist = "Гиперэкспоненциальное распределение"
    # params = stats.h
