import java.lang.Math;

public class Lab1_firstvar {
    public static void main(String[] args) {
        short[] c = new short[13];
        for (int i = 0; i < c.length; i++) {
            c[i] = (short) (i+3);
        }
        float[] x = new float[15];
        for (int j = 0; j < x.length; j++) {
            x[j] = (float) (Math.random() * 13 - 5);
        }

        double[][] d = new double[13][15];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < x.length; j++) {
                switch(c[i]){
                    case 4:
                            d[i][j] = Math.pow(Math.atan(Math.sin(x[j])) / (0.75 + Math.pow((Math.pow((1 - x[j]) / x[j], x[j]) - 4) / 3 / 4, Math.pow(x[j], 2 / x[j]))), 2);
                            break;
                    case 6: case 7: case 8: case 9: case 14: case 15:
                            d[i][j] = Math.pow(Math.pow(Math.PI * Math.pow((0.5 - x[j]), 3), Math.asin((x[j] + 1.5) / 13) / 2), 2); //объяснение: я с3,14здил эту идею от Лёвы Разуваева и совершенствовал(!), #sorrynotsorry
                            break;
                    default:
                            d[i][j] = Math.pow(Math.pow(0.33333 * Math.pow(Math.tan(x[j]), 2), (Math.tan(Math.sin(x[j])) + Math.PI)), 2);
                            break;
                }System.out.printf("%9.5f ", d[i][j]);
            }System.out.println();
        }
    }
}
