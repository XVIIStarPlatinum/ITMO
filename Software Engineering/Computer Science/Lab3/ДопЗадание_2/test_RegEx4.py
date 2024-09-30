from ExtraTask_2 import RegEx3

def test_find_words():
    result = RegEx3.find_words(r"String4.txt")
    assert result == "МоРцО МуРлО"