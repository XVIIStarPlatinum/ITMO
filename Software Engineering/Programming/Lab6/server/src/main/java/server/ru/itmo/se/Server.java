package server.ru.itmo.se;

import common.ru.itmo.se.exceptions.OpeningServerSocketException;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.utility.PrettyPrinter;
import server.ru.itmo.se.utility.RequestHandler;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.*;

/**
 * Class used for initiating a connection eit the client.
 * Implementation suggested by @bilyardvmetro.
 */
public class Server {
    /**
     * This field holds the server's port.
     */
    private final int port;
    /**
     * This field holds an instance of a RequestHandler which is used to interpret client's requests.
     */
    private final RequestHandler requestHandler;
    /**
     * This field holds a Selector which enable the server to work with multiple clients simultaneously in a single thread.
     */
    private Selector serverSelector;
    /**
     * This field holds a request from the client-side.
     */
    private Request requestFromUser;
    /**
     * This field holds the request that's about to be sent to the client.
     */
    private Response responseToUser;

    /**
     * Constructs a Server with the specified port and request handler.
     * @param port           the specified port.
     * @param requestHandler the specified RequestHandler.
     */
    public Server(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }
    /**
     * This method acts as a driver to initiate a connection with the client.<p>
     * It uses a set of selector keys to interact with the chosen clients, where each of them are granted READ, WRITE and ACCEPT permissions.
     */
    public void run() {
        try {
            openServerSocketChannel();
            while (true) {
                serverSelector.select();
                Iterator<SelectionKey> selectedKeys = serverSelector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    App.logger.log(Level.INFO, "Selected key: " + key);
                    try {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                PrettyPrinter.println("Listening on port '" + port + "'...");
                                App.logger.log(Level.INFO, "Listening on port '" + port + "'...");
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverSocketChannel.accept();
                                clientChannel.configureBlocking(false);
                                clientChannel.register(serverSelector, SelectionKey.OP_READ);
                            }
                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);
                                ByteBuffer clientData = ByteBuffer.allocate(4096);
                                App.logger.log(Level.INFO, clientChannel.read(clientData) + " bytes has been received.");
                                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(clientData.array());
                                     ObjectInputStream clientDataIn = new ObjectInputStream(byteArrayInputStream)) {
                                    requestFromUser = (Request) clientDataIn.readObject();
                                } catch (StreamCorruptedException e) {
                                    e.printStackTrace();
                                    App.logger.log(Level.SEVERE, e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
                                    key.cancel();
                                }
                                if (requestFromUser != null) {
                                    responseToUser = requestHandler.handle(requestFromUser);
                                    App.logger.log(Level.INFO, "A new request: " + requestFromUser.getCommandName() + " " + requestFromUser.getCommandStrArg() + " " + requestFromUser.getCommandObjArg() + " has been successfully processed.");
                                    clientChannel.register(serverSelector, SelectionKey.OP_WRITE);
                                }
                            }
                            if (key.isWritable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);
                                try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                     ObjectOutputStream clientDataOut = new ObjectOutputStream(bytes)) {
                                    clientDataOut.writeObject(responseToUser);

                                    ByteBuffer clientData = ByteBuffer.wrap(bytes.toByteArray());
                                    ByteBuffer dataLen = ByteBuffer.allocate(64).putInt(clientData.limit());
                                    dataLen.flip();

                                    clientChannel.write(dataLen);
                                    App.logger.log(Level.INFO, "Response length: " + dataLen.limit());
                                    clientChannel.write(clientData);
                                    App.logger.log(Level.INFO, "Response has been successfully sent to the client.");
                                    clientData.clear();
                                    PrettyPrinter.println("=".repeat(60));
                                }
                                clientChannel.register(serverSelector, SelectionKey.OP_READ);
                            }
                        }
                    } catch (SocketException | CancelledKeyException e) {
                        App.logger.log(Level.WARNING, "The client '" + key.channel().toString() + "' has disconnected.");
                        key.cancel();
                    }
                    selectedKeys.remove();
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (OpeningServerSocketException e) {
            PrettyPrinter.printError("The server cannot be launched.");
            App.logger.log(Level.SEVERE, "FATAL: The server cannot be launched.");
        } catch (IOException e) {
            PrettyPrinter.printError("An I/O error occurred.");
            App.logger.log(Level.SEVERE, "An I/O error occurred.");
        } catch (ClassNotFoundException e) {
            PrettyPrinter.printError("Non-matching classes.");
            App.logger.log(Level.SEVERE, "Non-matching classes.");
        }
    }

    /**
     * This method is used to open a ServerSocketChannel connection.
     */
    private void openServerSocketChannel() {
        try {
            App.logger.log(Level.INFO, "Starting the server...");
            serverSelector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            App.logger.log(Level.INFO, "The server socket channel is ready for operations.");
            serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
            App.logger.log(Level.INFO, "The server has been successfully launched.");
        } catch (IllegalArgumentException e) {
            PrettyPrinter.printError("The port '" + port + "' is outside the allowed range.");
            App.logger.log(Level.SEVERE, "FATAL: The port '" + port + "' is outside the allowed range.");
            throw new OpeningServerSocketException("The port '" + port + "' is outside the allowed range.", new RuntimeException());
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while trying to use the port '" + port + "'.");
            App.logger.log(Level.SEVERE, "FATAL: An error occurred while trying to use the port '" + port + "'.");
            throw new OpeningServerSocketException("The port '" + port + "' is outside the allowed range.", new RuntimeException());
        }
    }
}

