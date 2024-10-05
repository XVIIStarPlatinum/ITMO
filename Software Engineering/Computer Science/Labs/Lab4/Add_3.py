import time
import main as parseStd
import Add_1 as parseLib
import Add_2 as parseRe
timeDef = time.time()
for u in range(100):
    parseStd.Parse_default()
timeDef = time.time() - timeDef

timeLib = time.time()
for u in range(100):
    parseLib.Parse_lib()
timeLib = time.time() - timeLib

timeRe = time.time()
for u in range(100):
  parseRe.Parse_Regex()
timeRe = time.time() - timeRe
print("Парсер без готовых библиотек: " + str(timeDef) + '\n' + "Парсер с готовыми библиотеками: " + str(timeLib) + '\n' + "Парсер с регулярками: " + str(timeRe))
