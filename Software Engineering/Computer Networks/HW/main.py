import turtle
import tkinter as tk
import random
import string

class NRZ_I:

    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 50

    def draw(self):
        t.sety(self.logic_high)
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                self.one()

    def zero(self):
        t.forward(self.distance)

    def one(self):
        posx, posy = t.pos()
        if self.logic_low - 1 < posy < self.logic_low + 1:
            t.sety(self.logic_high)
        elif self.logic_high - 1 < posy < self.logic_high + 1:
            t.sety(self.logic_low)
        t.forward(self.distance)


class NRZ_L:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 50

    def draw(self):
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                self.one()

    def zero(self):
        t.sety(self.logic_high)
        t.forward(self.distance)

    def one(self):
        t.sety(self.logic_low)
        t.forward(self.distance)

class RZ:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 25
        self.base = 0

    def draw(self):
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                self.one()

    def zero(self):
        t.sety(self.logic_low)
        t.forward(self.distance)
        t.sety(self.base)
        setTurtle(*invisiline)
        t.write('0', False, 'right', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.forward(self.distance)
    def one(self):
        t.sety(self.logic_high)
        t.forward(self.distance)
        setTurtle(*invisiline)
        t.write('1', False, 'center', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.sety(self.base)
        t.forward(self.distance)


class Manchester:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 25
        self.base = 0

    def draw(self):
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                self.one()

    def zero(self):
        t.sety(self.logic_low)
        t.forward(self.distance)
        setTurtle(*invisiline)
        t.write('0', False, 'center', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.sety(self.logic_high)
        t.forward(self.distance)

    def one(self):
        t.sety(self.logic_high)
        t.forward(self.distance)
        t.sety(self.logic_low)
        setTurtle(*invisiline)
        t.write('1', False, 'right', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.forward(self.distance)

class Logical:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 25
        self.base = 0

    def draw(self):
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                self.one()

    def zero(self):
        t.sety(self.logic_low)
        t.forward(self.distance)
        setTurtle(*invisiline)
        t.write('0', False, 'center', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.sety(self.logic_low)
        t.forward(self.distance)

    def one(self):
        t.sety(self.logic_high)
        t.forward(self.distance)
        setTurtle(*invisiline)
        t.write('1', False, 'center', ("Times New Roman", 14, "normal"))
        setTurtle(*default_settings)
        t.sety(self.logic_high)
        t.forward(self.distance)

class AMI:
    def __init__(self, signal: str, num: int):
        self.signal = signal
        self.num = num
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 50
        self.base = 0
        self.block_coding = {'0000': '11110', '0001': '01001', '0010': '10100', '0011': '10101',
                    '0100': '01010', '0101': '01011', '0110': '01110', '0111': '01111',
                    '1000': '10010', '1001': '10011', '1010': '10110', '1011': '10111',
                    '1100': '11010', '1101': '11011', '1110': '11100', '1111': '11101'}

    def choose(self):
        if self.num == 1:
            pass
        elif self.num == 2:
            self.signal = self.map()
        elif self.num == 3:
            self.signal = self.scramble()

    def map(self):
        result = ""
        for i in range(0, len(self.signal), 4):
            four_bit = self.signal[i:i + 4]

            if four_bit in self.block_coding:
                result += self.block_coding[four_bit]
            else:
                print("Error: Input must be in 4 bit binary")
                break
        print("Значение для кодировки после преобразования:", result)
        return result


    def scramble(self):
        result = ""
        for i in range(0, len(self.signal)):
            if len(result) <= 3:
                result += self.signal[i]
            elif 3 < len(result) <= 5:
                result += str(int(self.signal[i]) ^ int(self.signal[i - 3]))
            elif len(result) > 5:
                result += str(int(self.signal[i]) ^ int(self.signal[i - 3]) ^ int(self.signal[i - 5]))
        print("Значение для кодировки после преобразования:", result)
        return result

    def draw(self):
        temp = 0
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                temp += 1
                count = temp % 2
                if count == 1:
                    self.onepos()
                elif count == 0:
                    self.oneneg()

    def zero(self):
        t.sety(self.base)
        t.forward(self.distance)

    def onepos(self):
        t.sety(self.logic_high)
        t.forward(self.distance)

    def oneneg(self):
        t.sety(self.logic_low)
        t.forward(self.distance)


class MLT_3:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 50
        self.base = 0

    def draw(self):
        temp = 0
        for i in self.signal:
            if i == '0':
                self.zero()
            elif i == '1':
                temp += 1
                count = temp % 4
                if count == 1:
                    self.onepos()
                elif count == 0 or count == 2:
                    self.oneneut()
                else:
                    self.oneneg()

    def zero(self):
        t.forward(self.distance)

    def onepos(self):
        t.sety(self.logic_high)
        t.forward(self.distance)

    def oneneut(self):
        t.sety(self.base)
        t.forward(self.distance)

    def oneneg(self):
        t.sety(self.logic_low)
        t.forward(self.distance)

class PAM_5:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_very_high = 60
        self.logic_high = 20
        self.logic_low = -20
        self.logic_very_low = -60
        self.distance = 100
        self.base = 0

    def draw(self):
        for i, j in slice_n(self.signal, 2):
            if i == '0':
                if j == '0':
                    self.zero()
                elif j == '1':
                    self.one()
            elif i == '1':
                if j == '0':
                    self.two()
                elif i == '1':
                    self.three()

    def zero(self):
        t.sety(self.logic_very_low)
        t.forward(self.distance)
    def one(self):
        t.sety(self.logic_low)
        t.forward(self.distance)
    def two(self):
        t.sety(self.logic_very_high)
        t.forward(self.distance)
    def three(self):
        t.sety(self.logic_high)
        t.forward(self.distance)


class diff_Manchester:
    def __init__(self, signal: str):
        self.signal = signal
        self.logic_high = 50
        self.logic_low = -50
        self.distance = 25
        self.base = 0

    def draw(self):
        prev_num = 2
        for i in self.signal:
            if i == '0':
                self.pattern(prev_num, '0')
            elif i == '1':
                num = 1 if prev_num == 2 else 2
                self.pattern(num, '1')
                prev_num = num

    def pattern(self, num, write):
        if num == 1:
            t.sety(self.logic_low)
            t.forward(self.distance)
            setTurtle(*invisiline)
            t.write(write, False, 'right', ("Times New Roman", 14, "normal"))
            setTurtle(*default_settings)
            t.sety(self.logic_high)
            t.forward(self.distance)
        elif num == 2:
            t.sety(self.logic_high)
            t.forward(self.distance)
            t.sety(self.logic_low)
            setTurtle(*invisiline)
            t.write(write, False, 'right', ("Times New Roman", 14, "normal"))
            setTurtle(*default_settings)
            t.forward(self.distance)


def drawAxes():
    def drawLineAndBack(distance):
        for i in range(distance // 50):
            t.forward(50)
            t.dot(5)
        t.backward(distance)

    t.hideturtle()
    t.speed('fastest')
    t.setx(-len_X // 2 + 100)
    drawLineAndBack(len_X)
    t.rt(90)
    drawLineAndBack(100)
    t.rt(180)
    drawLineAndBack(100)
    t.rt(90)


def setTurtle(size, colour, speed, visibility):
    t.pensize(size)
    t.pencolor(colour)
    t.speed(speed)
    if not visibility:
        t.hideturtle()


def randomstring(length):
    samplestring = '01'
    result = ''.join((random.choice(samplestring) for x in range(length)))
    return result


def length4string(length):
    samplestring = '00001'
    result = ''.join((random.choice(samplestring) for x in range(length)))
    if result.count("0000") >= 1:
        return result
    else:
        return length4string(length)


def length8string(length):
    samplestring = '000000001'
    result = ''.join((random.choice(samplestring) for x in range(length)))
    if result.count("00000000") >= 1:
        return result
    else:
        return length8string(length)

def slice_n(s, n, truncate=False):
    assert n > 0
    while len(s) >= n:
        yield s[:n]
        s = s[n:]
    if len(s) and not truncate:
        yield s


x = input("Выберите метод ввода:\n1. Рандомной\n2. С введенными субсекциями\n3. Ручной\n")
if x == "1":
    size=int(input("Введите длину кода: "))
    signal=randomstring(size)
elif x == "2":
    size = int(input("Введите длину кода: "))
    y = input("Выберите длину нулей:\n1. 4 нулей как субсекция\n2. 8 нулей как субсекция\n")
    if y == '1':
        signal=length4string(size)
    elif y == '2':
        signal=length8string(size)
elif x == "3":
    print('Код (в бинарном формате)')
    signal = input()
print('\nКодировать в:\n1. NRZI\n2. NRZ\n3. RZ\n4. M2\n5. Diff-M2\n6. AMI\n7. PAM-5 (2B1Q)\n8. Логический')
encoding = input()
if encoding == '6':
    print('\nВ формате:\nа. AMI (без скрэмблирования) \nб. MLT-3\nв. 4B/5B\nг. AMI (с скрэмблированием)')
    encoding = input()
print("Ввод:", signal)
root = tk.Tk()
root.title('График сигнала')
root.geometry('1440x300')
cv = turtle.ScrolledCanvas(root, width=1000)
cv.pack()

len_X, len_Y = 5000, 300
default_settings = (2, 'red', 'fastest', False)
invisiline = (1, 'black', 'fastest', False)
map = {'1': NRZ_I(signal), '2': NRZ_L(signal), '3': RZ(signal), '4': Manchester(signal),
       '5': diff_Manchester(signal), 'а': AMI(signal, 1), 'б': MLT_3(signal), 'в': AMI(signal, 2), 'г': AMI(signal, 3), '7': PAM_5(signal), '8': Logical(signal)}

screen = turtle.TurtleScreen(cv)
screen.screensize(len_X, len_Y)
t = turtle.RawTurtle(screen)

drawAxes()
setTurtle(*default_settings)
if encoding == 'в' or encoding == 'г':
    map[encoding].choose()
    map[encoding].draw()
else:
    map[encoding].draw()

root.mainloop()
