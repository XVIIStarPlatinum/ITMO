import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class HammingCode {
    public static void main(String[] args) {
        String code;
        do {
            System.out.println("Введите код: ");
            Scanner sc = new Scanner(System.in);
            code = sc.next();
            Pattern bin7 = Pattern.compile("^[01]+$");
            Pattern amount = Pattern.compile("^.{7}$");
            Matcher binVer = bin7.matcher(code);
            Matcher amountVer = amount.matcher(code);
            boolean binFormat = binVer.find();
            boolean amountFormat = amountVer.find();
            if (binFormat) {
                if (amountFormat) {
                    char[] input = code.toCharArray();
                    byte[] chArr = new byte[input.length];
                    for (int i = 0; i <= input.length - 1; i++) {
                        chArr[i] = (byte) Character.getNumericValue(input[i]);
                    }
                    boolean[] b = new boolean[chArr.length];
                    for (int i = 0; i < chArr.length; i++) {
                        b[i] = chArr[i] != 0;
                    }
                    boolean s1 = b[0] ^ b[2] ^ b[4] ^ b[5];
                    boolean s2 = b[1] ^ b[2] ^ b[5] ^ b[6];
                    boolean s3 = b[3] ^ b[4] ^ b[5] ^ b[6];
                    int siu = 4 * ((s1) ? 1 : 0) + 2 * ((s2) ? 1 : 0) + ((s3) ? 1 : 0) - 1;
                    if (siu == -1) {
                        System.out.println("Нет ошибок.");
                    } else {
                        b[siu] = !b[siu];
                        int i1f = b[2] ? 1 : 0;
                        int i2f = b[4] ? 1 : 0;
                        int i3f = b[5] ? 1 : 0;
                        int i4f = b[6] ? 1 : 0;
                        System.out.println("Исправленное сообщение: " + "" + i1f + "" + i2f + "" + i3f + "" + i4f);
                    }
                } else {
                    System.out.println("Не забыли ли вы, что данная программа принимает точно и только 7 цифр?");
                }
            } else {
                System.out.println("Зачем творить дичь? Формат ввода - двоичная.");
            }
        } while (!code.equals("stop"));
    }
}
