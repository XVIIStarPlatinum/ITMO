from ExtraTask_2 import RegEx3

def test_find_words():
    result = RegEx3.find_words(r"String5.txt")
    assert result == "АгАмА АгАвА АгАхА"
