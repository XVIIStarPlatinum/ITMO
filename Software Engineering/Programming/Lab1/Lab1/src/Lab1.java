import java.lang.Math; //Импорт математических функций от джава
public class Lab1 {
    public static void main(String[] args) {
        short[] c = new short[13]; //создание массива c типа short (целые числа от -32768 до 32767) с 13-ью элементами
        for (var i = 0; i < c.length; i++) { //создание условий для элементов массива с
            c[i] = (short) (i + 3);// заполнение массива целыми числами от 3 до 15 включительно; т.к. первый элемент (нулевой индекс) равен 3-м, добавил к значению индекса 3
        }

        float[] x = new float[15];//создание массива x типа float (дробные числа с точностью одного цифра после запятого) с 15-ью элементами
        for (var j = 0; j < x.length; j++) { //создание условий для элементов массива x
            x[j] = (float) (Math.random() * 13 - 5); // заполнение массива дробными числами от -5 до 8; я указал 0<x[j]<13 и отнял 5, т.к. при обычного условия -5<x[j]<8 получаются неотрицательные числа
        }

        double[][] d = new double[13][15]; //создание матрицы (массива массивов) d типа double (стандартное обозначение дробных чисел в Java) с размерами 13*15
        for (var i = 0; i < c.length; i++) { //создание условий для элементов матрицы d; допёрся до этого после 5 дней
            for (var j = 0; j < x.length; j++) {
                if (c[i] == 4) { //создание циклов if-elif-else для различных условий заполнения матрицы
                    d[i][j] = Math.pow(Math.atan(Math.sin(x[j])) / (0.75 + Math.pow((Math.pow((1 - x[j]) / x[j], x[j]) - 4) / 3 / 4, Math.pow(x[j], 2 / x[j]))), 2);
                } else if (c[i] >= 6 && c[i] <= 9 || c[i] == 14 || c[i] == 15) {
                    d[i][j] = Math.pow(Math.pow(Math.PI * Math.pow((0.5 - x[j]), 3), Math.asin((x[j] + 1.5) / 13) / 2), 2);
                } else {
                    d[i][j] = Math.pow(Math.pow(0.33333 * Math.pow(Math.tan(x[j]), 2), (Math.tan(Math.sin(x[j])) + Math.PI)), 2);
                }
                System.out.printf("%7.5f ", d[i][j]); //вывод результата в формате с пятью знаками после запятой
            }
            System.out.println(); //окончательный вывод программы
        }
    }
}
