package client.ru.itmo.se;

import client.ru.itmo.se.utility.UserHandler;
import common.ru.itmo.se.exceptions.ConnectionErrorException;
import common.ru.itmo.se.exceptions.ValueRangeException;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.utility.PrettyPrinter;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * Class used for initiating a connection with the server.
 * Implementation suggested by @bilyardvmetro.
 */
public class Client {
    /**
     * This field holds the client's host address.
     */
    private final String host;
    /**
     * This field holds the port.
     */
    private final int port;
    /**
     * This field holds the value for timeouts between reconnection attempts.
     */
    private final int reconnectionTimeout;
    /**
     * This field holds the value for reconnection attempts.
     */
    private int reconnectionAttempts;
    /**
     * This field holds the limit for reconnection attempts.
     */
    private final int maxReconnectionAttempts;
    /**
     * This field holds an instance of a UserHandler which is used to interpret user requests.
     */
    private final UserHandler userHandler;
    /**
     * This field holds an instance of a SocketChannel via which an NIO connection is going to be initiated.
     */
    private SocketChannel socketChannel;

    /**
     * Constructs a Client with the specified host, port, reconnection timeout, maximum reconnection attempts and UserHandler.
     * @param host                    the host.
     * @param port                    the port.
     * @param reconnectionTimeout     the reconnection timeout period.
     * @param maxReconnectionAttempts the reconnection attempt limit.
     * @param userHandler             a UserHandler instance.
     */
    public Client(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts, UserHandler userHandler) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
    }

    /**
     * This method acts as a driver to initiate a connection with the server.
     */
    public void run() {
        try {
            boolean processingStatus = true;
            while(processingStatus) {
                try {
                    connectToServer();
                    processingStatus = processRequestToServer();
                } catch (ConnectionErrorException e) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        PrettyPrinter.printError("Connection attempts has exceeded the limit.");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeout) {
                        PrettyPrinter.printError("Connection timeout value: '" + reconnectionTimeout + "' is beyond allowed limits.");
                        PrettyPrinter.println("A reconnection attempt will be made immediately.");
                    } catch (Exception timeoutException) {
                        PrettyPrinter.printError("An error occurred while for a connection.");
                        PrettyPrinter.println("A reconnection attempt will be made immediately.");
                    }
                }
                reconnectionAttempts++;
            }
            if(socketChannel != null) {
                socketChannel.close();
            }
            PrettyPrinter.println("Client session has been successfully terminated.");
        } catch (ValueRangeException e) {
            PrettyPrinter.printError("The client application cannot be launched.");
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while attempting to connect to the server.");
        }
    }

    /**
     * This method initializes a SocketChannel and connects to the server with it.
     * @throws ConnectionErrorException if a connection attempt goes wrong.
     * @throws ValueRangeException      if a port is invalid.
     */
    private void connectToServer() throws ConnectionErrorException, ValueRangeException {
        try {
            if(reconnectionAttempts >= 1) {
                PrettyPrinter.println("Reconnecting to the server...");
            }
            SocketAddress address = new InetSocketAddress(host, port);
            socketChannel = SocketChannel.open();
            socketChannel.connect(address);
            PrettyPrinter.println("Connection to the server has been successfully established.");
        } catch (IllegalArgumentException e) {
            PrettyPrinter.printError("The server address is invalid.");
            throw new ValueRangeException("The server address is invalid.", new RuntimeException());
        } catch (ConnectException e) {
            PrettyPrinter.printError("The server is currently unavailable. Please try later.");
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while attempting to connect to the server.");
            throw new ConnectionErrorException("An error occurred while attempting to connect to the server.", new RuntimeException());
        }
    }

    /**
     * This method is used to process a request.
     * @return false if the cycle is broken (if the application is terminated).
     * @throws IOException if there are problems with I/O streams.
     */
    private boolean processRequestToServer() throws IOException {
        Request requestToServer = null;
        Response responseFromServer = null;
        do {
            try {
                requestToServer = responseFromServer != null ? userHandler.handle(responseFromServer.getResponseCode()) : userHandler.handle(null);
                if(requestToServer.isEmpty()) {
                    continue;
                }
                try(ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                    out.writeObject(requestToServer);
                    ByteBuffer dataToSend = ByteBuffer.wrap(bytes.toByteArray());
                    socketChannel.write(dataToSend);
                    out.flush();
                }
                ByteBuffer dataToReceiveLength = ByteBuffer.allocate(64);
                socketChannel.read(dataToReceiveLength);
                dataToReceiveLength.flip();
                int responseLength = dataToReceiveLength.getInt();
                ByteBuffer dataToReceive = ByteBuffer.allocate(responseLength);
                socketChannel.read(dataToReceive);
                try(ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(dataToReceive.array()))) {
                    responseFromServer = (Response) objectInputStream.readObject();
                    System.out.print(responseFromServer.getResponseBody());
                }
            } catch (InvalidClassException | NotSerializableException e) {
                PrettyPrinter.printError("An error occurred while trying to send data to the server.");
            } catch (ClassNotFoundException e) {
                PrettyPrinter.printError("An error occurred while trying to read data sent from the server.");
            } catch (IOException e) {
                PrettyPrinter.printError("A disconnection from the server occurred.");
                try {
                    reconnectionAttempts++;
                    connectToServer();
                } catch (ConnectionErrorException | ValueRangeException exception) {
                    if (Objects.requireNonNull(requestToServer).getCommandObjArg().equals("exit")) {
                        PrettyPrinter.println("This command will not be registered on the server.");
                    } else {
                        PrettyPrinter.println("Please try later.");
                    }
                }
            }
        } while (!Objects.requireNonNull(requestToServer).getCommandName().equals("exit"));
        return false;
    }
}
