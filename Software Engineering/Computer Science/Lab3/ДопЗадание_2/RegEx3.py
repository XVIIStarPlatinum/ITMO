import re
# %4
def find_words(filename):
    # charDist = input("Задавайте расстояние: ")
    testfile = open(filename, encoding="utf8")
    test_text = testfile.read()
    listOfText = test_text.split(" ")
    matches = re.findall("[А-ЯЁ]", test_text)
    if matches:
        t1 = matches[0]
        t2 = matches[1]
        t3 = matches[2]
        t1l = str.lower(t1)
        t2l = str.lower(t2)
        t3l = str.lower(t3)
        reOrder = re.compile("^[" + t1 + "][^" + t1l + t2l + t3l + "]*[" + t2 + "][^" + t1l + t2l + t3l + "]*[" + t3 + "]$")
        reOutput = list(filter(reOrder.match, listOfText))
        regEx = re.compile("^[" + t1 + "].{1}[" + t2 + "].{1}[" + t3 + "]$")
        resultAg = list(filter(regEx.match, reOutput))
        resultAu = " ".join(resultAg)
        return resultAu

for j in range(1, 6):
    string_name = 'String' + str(j) + '.txt'
    matchedWords = find_words(string_name)
    print("Тест",j, "- Результаты поиска по шаблону первого выделенного слова: ", matchedWords)

# while True:
#     textInput = input("А зачем вам даже требуется найти такие слова? Ладно, Бог знает: ")
#     if textInput == "stop":
#         break
#     else:
#         textList = textInput.split(" ")
#         textDist = input("Задавайте расстояние: ")
#         regexCap = re.findall("[А-ЯЁ]", textInput)
#         if regexCap:
#             r1 = regexCap[0]
#             r2 = regexCap[1]
#             r3 = regexCap[2]
#             r1l = str.lower(r1)
#             r2l = str.lower(r2)
#             r3l = str.lower(r3)
#             regexOrder = re.compile("^[" + r1 + "][^" + r1l + r2l + r3l + "]*[" + r2 + "][^" + r1l + r2l + r3l + "]*[" + r3 + "]$")
#             regexOutput = list(filter(regexOrder.match, textList))
#             regex = re.compile("^[" + r1 + "].{" + str(textDist) + "}[" + r2 + "].{" + str(textDist) + "}[" + r3 + "]$")
#             result = list(filter(regex.match, regexOutput))
#             resultPt = " ".join(result)
#             print(str(resultPt))
# regEx = r"\b\w*" + t1 + "[^ + t1l + t2l + t3l + ]{charD}" + t2 + "[^ + t1l + t2l + t3l + ]{charD}" + t3 + r"\w*\b"
#         resultAg = list(filter(regEx.match, listOfText))
