import Modules.DBProvider;
import Modules.Server;
import java.io.File;
import java.net.InetSocketAddress;

import java.util.logging.*;

public class Main {
    public static Logger logger = Logger.getLogger(Main.class.getName());
    private static String url;
    private static String user;
    private static int PORT;
    public static void main(String[] args) {
        if(args.length != 3) {
            String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            System.out.println("Usage: 'java -jar " + jarName + " <url> <user> <port>'");
            System.exit(0);
        } 
        url = args[0];
        user = args[1];
        PORT = Integer.parseInt(args[2]);
        // для сервака в третий аргумент прописать pgpass
        DBProvider.establishConnection(url, user, "xwVxXqHACMOxPOqS");
        Server server = new Server(new InetSocketAddress(PORT));

//       Строчки для теста на локалхосте. Для гелиуса достаточно указать только порт
        // DBProvider.establishConnection("jdbc:postgresql://localhost:9999/studs", "s372799", "xwVxXqHACMOxPOqS");
        // Server server = new Server(new InetSocketAddress("localhost", 8000));
        server.run();
    }
}
