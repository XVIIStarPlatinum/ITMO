import re
while True:
    codeInput = input("Введите код: ")
    if codeInput == "stop":
        break
    else:
        bin7 = re.compile("^[01]+$")
        if re.findall(bin7, codeInput):
            binPlace = re.compile("^[01]{7}$")
            if re.findall(binPlace, codeInput):
                codeList = [int(b) for b in str(codeInput)]
                r1 = bool(codeList[0])
                r2 = bool(codeList[1])
                i1 = bool(codeList[2])
                r3 = bool(codeList[3])
                i2 = bool(codeList[4])
                i3 = bool(codeList[5])
                i4 = bool(codeList[6])
                boolList = [r1, r2, i1, r3, i2, i3, i4]
                s1 = r1 ^ i1 ^ i2 ^ i4
                s2 = r2 ^ i1 ^ i3 ^ i4
                s3 = r3 ^ i2 ^ i3 ^ i4
                s1i = int(s1)
                s2i = int(s2)
                s3i = int(s3)
                notIndex = (s3i * 4 + s2i * 2 + s1i)-1
                if notIndex == -1:
                    print("Нет ошибок. Хорошего дня!")
                else:
                    boolList[notIndex] = not boolList[notIndex]
                    i1f = int(boolList[2])
                    i2f = int(boolList[4])
                    i3f = int(boolList[5])
                    i4f = int(boolList[6])
                    print("Исправленное сообщение: ", i1f, i2f, i3f, i4f)
            else:
                print("Не забыли ли вы, что данная программа принимает точно и только 7 цифр?")
        else:
            print("Зачем творить дичь? Формат ввода - двоичная.")
