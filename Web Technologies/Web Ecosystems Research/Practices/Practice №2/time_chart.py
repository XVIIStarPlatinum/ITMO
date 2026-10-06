import matplotlib.pyplot as plt

# Рисунок 1: экспоненциальный рост двух выражений
n_values = [24, 26, 28, 30]
vulnerable_times = [0.855, 3.572, 14.997, 52.519]
safe_time = 0.028  # плоская фигня, независимо от n

fig1, ax1 = plt.subplots(figsize=(7, 5))
ax1.plot(n_values, vulnerable_times, marker='o', color='#d62728',
         linewidth=2, label=r'Уязвимое выражение: /^(a+)+$/')
ax1.axhline(safe_time, color='#2ca02c', linewidth=2, linestyle='--',
            label=r'Безопасное: /^a+$/ (~0.028s, прямая)')
ax1.set_yscale('log')
ax1.set_xlabel('n (количество букв "a" до символа "!")')
ax1.set_ylabel('Время (с, логарифмическая шкала)')
ax1.set_title('Катастрофический возврат: время к размеру символов')
ax1.set_xticks(n_values)
ax1.legend()
ax1.grid(True, which='both', alpha=0.3)
fig1.tight_layout()
fig1.savefig('figs/redos_growth_curve.png', dpi=150)

# Рисунок 2: использование ресурса CPU двух выражений в промежутке времени
vulnerable_cpu = [0.0, 99.0, 99.0, 99.3, 99.2, 99.6, 99.5, 99.5,
                  99.6, 99.6, 99.6, 99.7, 99.6, 99.6]
vulnerable_seconds = list(range(len(vulnerable_cpu)))

safe_cpu = [0.0]
safe_seconds = [0]

fig2, ax2 = plt.subplots(figsize=(7, 5))
ax2.plot(vulnerable_seconds, vulnerable_cpu, marker='o', color='#d62728',
         linewidth=2, label='Уязвимое: /^(a+)+$/  (n=28)')
ax2.plot(safe_seconds, safe_cpu, marker='o', color='#2ca02c',
         linewidth=2, markersize=8, label='Безопасное: /^a+$/  (n=40)')
ax2.set_xlabel('Время (с, промежуток 1 с)')
ax2.set_ylabel('Потребление ресурса CPU (%)')
ax2.set_title('Использование ресурса CPU в течении времени')
ax2.set_ylim(-5, 105)
ax2.legend()
ax2.grid(True, alpha=0.3)
fig2.tight_layout()
fig2.savefig('figs/redos_cpu_usage.png', dpi=150)

print("Done. 🥱")
