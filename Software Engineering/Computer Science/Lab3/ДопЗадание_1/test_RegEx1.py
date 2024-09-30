from ExtraTask_1 import RegEx2

def test_duplicate_detector():
    result = RegEx2.duplicateDetector(r"test_1.txt")
    assert result == "Джордано Бруно — итальянский монах-доминиканец, философ-пантеист и поэт; автор многочисленных трактатов."
