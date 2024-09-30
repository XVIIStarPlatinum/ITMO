from ExtraTask_1 import RegEx2

def test_duplicate_detector():
    result = RegEx2.duplicateDetector(r"test_5.txt")
    assert result == "Украiнська сало УКРАIНСЬКА САЛО\nУкраiнська сало\nСАЛО\nУкраiнська     сало"
