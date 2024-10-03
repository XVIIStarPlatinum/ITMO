package server.ru.itmo.se;

import common.ru.itmo.se.utility.PrettyPrinter;
import server.ru.itmo.se.commands.*;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.FileManager;
import server.ru.itmo.se.utility.CommandManager;
import server.ru.itmo.se.utility.RequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.*;

/**
 * Driver class for the server application.
 */
public class App {
    /**
     * The server's port number. Used as an interface for initiating a connection with the client.
     */
    public static int PORT;
    /**
     * The server's file name argument.
     */
    public static String cliArgument = null;
    /**
     * The server's logging utility. It records every action.
     */
    public static Logger logger = Logger.getLogger(App.class.getName());

    /**
     * The driver method used to start the server.
     * @param args arguments provided from the user. In this case it's the port and the file name.
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            PrettyPrinter.printError("There must be an argument. Please try again.");
            System.exit(0);
        } else {
            cliArgument = args[0];
            File file = new File(cliArgument);
            if(!Files.isReadable(file.toPath())) {
                PrettyPrinter.printError("You don't have access to this file. Try again, maybe after doing chmod 777 or something.");
                System.exit(1);
            }
            PORT = Integer.parseInt(args[1]);
            if(!file.isFile()) {
                try {
                    if(file.createNewFile()) {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                        outputStreamWriter.write("{}");
                        PrettyPrinter.println("File has been successfully created.");
                        outputStreamWriter.close();
                    }
                } catch (IOException e) {
                    PrettyPrinter.printError("File could not be created.");
                    e.printStackTrace();
                }
            }
        }
        FileManager fileManager = new FileManager(cliArgument);
        CollectionManager collectionManager = new CollectionManager(fileManager);
        CommandManager commandManager = new CommandManager(){{
            addCommand("add", new Add(collectionManager));
            addCommand("clear", new Clear(collectionManager));
            addCommand("execute_script", new ExecuteScript());
            addCommand("exit", new Exit(collectionManager));
            addCommand("filter_less_than_number_of_participants", new FilterLessThanNumberOfParticipants(collectionManager));
            addCommand("group_counting_by_establishment_date", new GroupCountingByEstablishmentDate(collectionManager));
            addCommand("help", new Help(this));
            addCommand("history", new History(this));
            addCommand("info", new Info(collectionManager));
            addCommand("print_field_descending_establishment_date", new PrintFieldDescendingEstablishmentDate(collectionManager));
            addCommand("remove_at", new RemoveAt(collectionManager));
            addCommand("remove_by_id", new RemoveByID(collectionManager));
            addCommand("save", new Save(collectionManager));
            addCommand("show", new Show(collectionManager));
            addCommand("shuffle", new Shuffle(collectionManager));
            addCommand("update", new UpdateID(collectionManager));
        }};
        RequestHandler requestHandler = new RequestHandler(commandManager);
        Server server = new Server(PORT, requestHandler);
        server.run();
    }
}