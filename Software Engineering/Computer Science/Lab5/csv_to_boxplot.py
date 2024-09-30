import pandas as pd
import matplotlib.pyplot as mplt
df = pd.read_csv(r"C:\Users\Dell\OneDrive\Desktop\IT's MOrbin' time\IKT_17starplatinum\I\I semester\lab5\lab5_IKT_dop3py.csv")
boxplot = df.boxplot(figsize = (5,5), fontsize=8, grid=True, notch=True, patch_artist=True)
boxplot.set_title("Диаграмма с усами, составленные из данных какого-то биржа")
mplt.plot()
