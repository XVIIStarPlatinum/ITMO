def Parse_Regex():
    import re
    inputAddress = r"C:\Users\Dell\OneDrive\Desktop\IT's MOrbin' time\IKT_17starplatinum\I\I semester\lab4\schedule.xml"
    outputAddress = r"C:\Users\Dell\OneDrive\Desktop\IT's MOrbin' time\IKT_17starplatinum\I\I semester\lab4\schedule.json"
    inputFile = open(inputAddress, 'r', encoding='utf-8')
    outputFile = open(outputAddress, 'w', encoding='utf-8')
    inputFile.readline()
    sumLines = 0
    for s in inputFile:
        countTab = 4
        while s[0] == ' ':
            s = s[1::]
            countTab+=1
        xmltag = str(*(re.findall(r'^<\w*[^\\]>', s)))[1:-1] + '\" : '
        token = ""
        if '<' in s and (len(re.findall(r">.*<", s)) > 0):
            token = "\"" + str(*(re.findall(r'>.*<', s)))[1:-1] + "\""
        if countTab > sumLines:
            outputFile.write('{' + '\n' + countTab * " " + "\"" + xmltag + token)
        elif countTab < sumLines:
            outputFile.write('\n' + " " * countTab + "}")
        else:
            outputFile.write(',' + '\n' + countTab * ' ' + "\"" + xmltag + token)
        sumLines = countTab
    outputFile.write("\n}")
    outputFile.close()
    inputFile.close()
Parse_Regex()