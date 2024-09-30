def Parse_lib():
    import xmltodict
    import json
    inputAddress = r"C:\Users\Dell\OneDrive\Desktop\IT's MOrbin' time\IKT_17starplatinum\I\I semester\lab4\schedule.xml"
    outputAddress = r"C:\Users\Dell\OneDrive\Desktop\IT's MOrbin' time\IKT_17starplatinum\I\I semester\lab4\schedule.json"
    inputFile = open(inputAddress, 'r', encoding='utf-8')
    outputFile = open(outputAddress, 'w', encoding='utf-8')
    interInput = inputFile.read()
    interOutput = xmltodict.parse(interInput)
    preOutput = json.dumps(interOutput, indent=4, ensure_ascii=False)
    outputFile.write(preOutput)
    outputFile.close()
    inputFile.close()
Parse_lib()