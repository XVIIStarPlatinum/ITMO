import java.lang.Math;

public class Lab1_deadmemes {
    public static void main(String[] args) {
        short[] c = new short[13];
        for (int i = 0; i < c.length; i++) {
            c[i] = (short) (i + 3);
        }
        float[] x = new float[15];
        for (int j = 0; j < x.length; j++) {
            x[j] = (float) (Math.random() * 13 - 5);
        }
        double[][] d = new double[13][15];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < x.length; j++) {
                switch(c[i]){
                    case 4: {
                        double amogus = 1-x[j];
                        amogus /= x[j];
                        amogus = Math.pow(amogus, x[j]);
                        amogus -= 4;
                        amogus /= 3;
                        amogus /= 4;
                        System.out.println(amogus);
                        double imposter = Math.pow(x[j], 2/x[j]);
                        amogus = Math.pow(amogus, imposter);
                        amogus += 0.75;
                        double Jerma985 = Math.sin(x[j]);
                        Jerma985 = Math.atan(Jerma985);
                        double whentheImposterissus = Jerma985/amogus;
                        d[i][j] = Math.pow(whentheImposterissus, 2);
                        break;
                    }
                    case 6: case 7: case 8: case 9: case 14: case 15:
                        d[i][j] = Math.pow(Math.pow(Math.PI * Math.pow((0.5 - x[j]), 3), Math.asin((x[j] + 1.5) / 13) / 2), 2);
                        break;
                    default:
                        d[i][j] = Math.pow(Math.pow(0.33333 * Math.pow(Math.tan(x[j]), 2), (Math.tan(Math.sin(x[j])) + Math.PI)), 2);
                        break;
                }System.out.printf("%9.5f ", d[i][j]);
            }System.out.println();
        }
    }
}