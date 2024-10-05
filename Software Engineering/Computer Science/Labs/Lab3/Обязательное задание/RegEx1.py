import re
# ;<{(
def BalakshinCounter(file_name):
        test_file = open(file_name, encoding='utf-8')
        test_str = test_file.read()
        BalakshinRegex = re.compile(";<\{\(")
        result = BalakshinRegex.findall(test_str)
        return len(result)

# while True:
#     textinput = input("Введите текст: ")
#     if textinput == "stop":
#         break
#     else:
#         regex = re.compile(";<\{\(")
#         result = re.findall(regex, textinput)
#         print("Количество совпадений: " + str(len(result)))
