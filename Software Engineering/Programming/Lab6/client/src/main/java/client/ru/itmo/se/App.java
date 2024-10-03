package client.ru.itmo.se;

import client.ru.itmo.se.utility.UserHandler;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import common.ru.itmo.se.exceptions.ValueRangeException;
import common.ru.itmo.se.utility.PrettyPrinter;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.File;
import java.util.Scanner;

/**
 * Driver class for the client-side CLI-application.
 */
public class App {
    /**
     * String for CLI indentation.
     */
    public static final String CS1 = "$ ";
    /**
     * String for CLI indentation.
     */
    public static final String CS2 = "> ";
    /**
     * Timeout between reconnection attempts.
     */
    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    /**
     * Timeout attempt count limit.
     */
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;
    /**
     * The host. Used for initiating a connection to the server.
     */
    private static String host;
    /**
     * The port number. Also used for initiating a connection to the server.
     */
    private static int port;

    /**
     * This method is used to initiate a connection to the server.
     * @param hostAndPort the host and the port.
     * @return true if the client has successfully connected to the server, <p>and false if the client didn't.
     */
    private static boolean initConnection(String[] hostAndPort) {
        try {
            if(hostAndPort.length != 2) {
                throw new InvalidArgumentCountException("Incorrect jar usage.", new RuntimeException());
            }
            host = hostAndPort[0];
            port = Integer.parseInt(hostAndPort[1]);
            if(port < 0) {
                throw new ValueRangeException("Port value cannot be negative.", new RuntimeException());
            }
            return true;
        } catch (InvalidArgumentCountException e) {
            String jarName = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            PrettyPrinter.println("Usage: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException e) {
            PrettyPrinter.printError("Port value must be a number.");
        } catch (ValueRangeException e) {
            PrettyPrinter.printError("Port value cannot be negative.");
        }
        return false;
    }

    /**
     * The driver method used to launch the CLI application.
     * @param args arguments provided from the user. In this case it's the host and the port.
     */
    public static void main(String[] args) {
        if(!initConnection(args)) {
            return;
        }
        PrettyPrinter.println("W E L C O M E.");
        SignalHandler handler = sig -> {
            System.out.println();
            PrettyPrinter.printError("Ctrl+C? How dare you!\u001B[0m");
            System.exit(0);
        };
        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("ABRT"), handler);
        Signal.handle(new Signal("TERM"), handler);
        Scanner userScanner = new Scanner(System.in);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, userHandler);
        client.run();
        userScanner.close();
    }
}
