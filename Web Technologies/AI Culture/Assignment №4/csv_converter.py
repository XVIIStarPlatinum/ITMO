from pathlib import Path

import matplotlib

matplotlib.use("Agg")  # без окна, просто сохраняем картинку в файл
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler

BASE_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/wine-quality/"
FILES = {"red": "winequality-red.csv", "white": "winequality-white.csv"}
OUTPUT_CSV = "wine_quality_ml_ready.csv"
OUTPUT_PLOT = "figs/eda_distributions.png"

TARGET = "quality"          # целевая переменная: не кодируем, не масштабируем
SKEW_THRESHOLD = 1.0        # асимметрия выше порога, значит можно на логарифм
DROP_DUPLICATES = True      # в датасете много полностью одинаковых строк

SCRIPT_DIR = Path(__file__).resolve().parent
pd.set_option("display.width", 160)
pd.set_option("display.max_columns", 30)


def section(title: str) -> None:
    print(f"\n{'=' * 8} {title} {'=' * 8}")

def load_raw() -> pd.DataFrame:
    parts = []
    for wine_type, fname in FILES.items():
        local = SCRIPT_DIR / fname
        src = local if local.exists() else BASE_URL + fname
        part = pd.read_csv(src, sep=";")  # в файлах разделитель ';'
        part["wine_type"] = wine_type     # признак происхождения вина
        parts.append(part)
    return pd.concat(parts, ignore_index=True)


df = load_raw()

section("Структура данных")
print("Размер:", df.shape)
print(df.dtypes)
print("\nПропуски по столбцам:\n", df.isna().sum()[df.isna().sum() > 0])
print("Всего пропусков:", int(df.isna().sum().sum()))
print("Полных дубликатов строк:", int(df.duplicated().sum()))
print("\nУникальных значений в столбцах:")
print(df.nunique())
print("\nРаспределение целевой переменной:")
print(df[TARGET].value_counts().sort_index())

section("Приведение типов")
CATEGORICAL = ["wine_type"]
NUMERIC = [c for c in df.columns if c not in CATEGORICAL + [TARGET]]

for col in NUMERIC:
    df[col] = pd.to_numeric(df[col], errors="coerce").astype("float64")
df[TARGET] = pd.to_numeric(df[TARGET], errors="coerce")
df["wine_type"] = df["wine_type"].astype("category")

bad_rows = df[NUMERIC + [TARGET]].isna().any(axis=1).sum()
if bad_rows:
    print(f"Удаляю строки с нечисловыми значениями: {bad_rows}")
    df = df.dropna(subset=NUMERIC + [TARGET])
df[TARGET] = df[TARGET].astype("int64")

if DROP_DUPLICATES:
    n_before = len(df)
    df = df.drop_duplicates().reset_index(drop=True)
    print(f"Удалено полных дубликатов: {n_before - len(df)} (осталось {len(df)})")

print(df.dtypes)

section("Кодирование категорий")
print(df["wine_type"].value_counts())
df["is_white"] = (df["wine_type"] == "white").astype("int64")  # red=0, white=1
df = df.drop(columns="wine_type")
print("wine_type -> is_white (red=0, white=1)")

section("Числовые признаки: среднее vs медиана и асимметрия")
stats = pd.DataFrame(
    {
        "mean": df[NUMERIC].mean(),
        "median": df[NUMERIC].median(),
        "std": df[NUMERIC].std(),
        "min": df[NUMERIC].min(),
        "max": df[NUMERIC].max(),
        "skew": df[NUMERIC].skew(),
    }
)
# (mean - median) / std: насколько среднее "уехало" от медианы в единицах разброса
stats["(mean-median)/std"] = (stats["mean"] - stats["median"]) / stats["std"]
print(stats.round(3))

section("Логарифмирование")
skew_before = df[NUMERIC].skew()
log_candidates = [
    c for c in NUMERIC if skew_before[c] > SKEW_THRESHOLD and df[c].min() >= 0
]

transformed = []
for col in log_candidates:
    new_skew = np.log1p(df[col]).skew()
    if abs(new_skew) < abs(skew_before[col]):
        new_name = f"log_{col}"
        df[new_name] = np.log1p(df[col])
        df = df.drop(columns=col)
        transformed.append((col, new_name))
        print(f"{col}: skew {skew_before[col]:.2f} -> {new_skew:.2f}  => {new_name}")
    else:
        print(f"{col}: skew {skew_before[col]:.2f} -> {new_skew:.2f}  (не помогло, оставляю как есть)")

not_transformed = [c for c in NUMERIC if c not in log_candidates]
print("Без преобразования (|skew| <= порога):", not_transformed)

if transformed:
    raw_df = load_raw()
    fig, axes = plt.subplots(len(transformed), 2, figsize=(9, 2.6 * len(transformed)))
    axes = np.atleast_2d(axes)
    for i, (old, new) in enumerate(transformed):
        axes[i, 0].hist(raw_df[old].dropna(), bins=40)
        axes[i, 0].set_title(f"{old} (до), skew={skew_before[old]:.2f}")
        axes[i, 1].hist(df[new], bins=40, color="tab:orange")
        axes[i, 1].set_title(f"{new} (после), skew={df[new].skew():.2f}")
    fig.tight_layout()
    fig.savefig(SCRIPT_DIR / OUTPUT_PLOT, dpi=110)
    plt.close(fig)
    print("Картинка сохранена:", OUTPUT_PLOT)

section("Масштабирование")
FEATURES_TO_SCALE = [c for c in df.columns if c not in ["is_white", TARGET]]
scaler = StandardScaler()  # (x - mean) / std -> среднее 0, std 1
df[FEATURES_TO_SCALE] = scaler.fit_transform(df[FEATURES_TO_SCALE])
print("Масштабированы (StandardScaler):", FEATURES_TO_SCALE)
print("Не масштабированы:", ["is_white", TARGET])

section("Итог")
df = df[FEATURES_TO_SCALE + ["is_white", TARGET]]

# проверки "готовности к модели"
assert df.isna().sum().sum() == 0, "остались пропуски"
assert not df.select_dtypes(include=["object", "category"]).shape[1], "остались текстовые столбцы"
assert np.allclose(df[FEATURES_TO_SCALE].mean(), 0, atol=1e-8), "среднее != 0"
assert np.allclose(df[FEATURES_TO_SCALE].std(ddof=0), 1, atol=1e-8), "std != 1"
assert set(df["is_white"].unique()) <= {0, 1}

df.to_csv(SCRIPT_DIR / OUTPUT_CSV, index=False)
print("Форма итоговой таблицы:", df.shape)
print(df.dtypes)
print(df.describe().loc[["mean", "std", "min", "max"]].round(3))
print(f"\nСохранено: {OUTPUT_CSV}")
