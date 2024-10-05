from ExtraTask_1 import RegEx2

def test_duplicate_detector():
    result = RegEx2.duplicateDetector(r"test_2.txt")
    assert result == "Довольно распространённая ошибка – это лишний повтор слова. Смешно, не правда ли? Не нужно портить хор хоровод."

