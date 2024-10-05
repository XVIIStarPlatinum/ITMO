import xmltodict, csv
xmlFile = open(r"/schedule.xml", 'r', encoding='utf-8')
xml = xmltodict.parse(xmlFile.read())
csvFile = open(r"/schedule.csv", 'w', encoding='utf-8', newline='')
csvFile_writer = csv.writer(csvFile)
csvFile_writer.writerow(["time", "weeks", "room", "location", "name", "week", "teacher", "format"])
for subject in xml["schedule"]["day"]["subjects"]["subject"]:
    csv_line = [subject["time"], subject["weeks"], subject["room"], subject["location"], subject["week"], subject["name"], subject["teacher"], subject["format"]]
    csvFile_writer.writerow(csv_line)