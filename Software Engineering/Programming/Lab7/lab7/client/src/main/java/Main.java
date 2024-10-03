import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    private static String HOST;
    private static int PORT;
    public static void main(String[] args) {
        HOST = args[0];
        PORT = Integer.parseInt(args[1]);
        try {
            //  для теста на локалхосте
        //    Client client = new Client(InetAddress.getByName("localhost"), 8000);
            Client client = new Client(InetAddress.getByName(HOST), PORT); // порт выбирается самостоятельно
            client.run();
        } catch (UnknownHostException e) {
            System.out.println("Хоста с таким именем не существует.");
        }
    }
}
