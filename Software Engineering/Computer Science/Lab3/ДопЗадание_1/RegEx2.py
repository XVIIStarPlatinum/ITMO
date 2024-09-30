import re
#Повторы слов
def duplicateDetector(filename):
    test_file = open(filename, encoding="utf8")
    test_str = test_file.read()
    regexCap = re.compile(r'\b(\w+)(( +\1)*\b)*', re.IGNORECASE)
    duplicatesL = re.sub(regexCap, r'\1', test_str)
    return duplicatesL

# while True:
#     textinput = input("Уберём эти повторы, заколебали. Набивайте текст: ")
#     if textinput == "stop":
#         break
#     else:
#         dupregex = r'\b(\w+)(( *\1)*\b)+'
#         duprep = r'\1'
#         result = re.sub(dupregex, duprep, textinput)
#         print("Вывод без повторов: " + result)
