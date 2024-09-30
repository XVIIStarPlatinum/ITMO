package consoleApp; /**
 * Package which contains the main class.
 */

import commands.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import utility.*;
import utility.Console;

import java.io.*;
import java.util.Scanner;

/**
 * consoleApp.Main class - runs the console app.
 */
public class Main {
    /**
     * String for CLI indentation.
     */
    public static final String CS1 = "$ ";
    /**
     * String for CLI indentation.
     */
    public static final String CS2 = "> ";
    /**
     * CLI argument passes on to this variable.
     */
    public static String CLI_ARGUMENT = null;
    /**
     * consoleApp.Main method - launches the console app.
     *
     * @param args main
     */
    public static void main(String[] args) throws IOException {
            Console.println("W E L C O M E.");
            Scanner userScanner = new Scanner(System.in);
        SignalHandler handler = sig -> {
            Console.printError("Ctrl+C? How dare you!");
            System.exit(0);
        };
        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("ABRT"), handler);
        Signal.handle(new Signal("TERM"), handler);
            if (args.length != 0) {
                CLI_ARGUMENT = args[0];
                File file = new File(CLI_ARGUMENT);
                if (!file.exists()) {
                    try {
                        if (file.createNewFile()) {
                            OutputStream outputStream = new FileOutputStream(file);
                            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                            osw.write("{}");
                            Console.println("File successfully created.");
                            osw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Console.printError("There must be an argument. Try again.");
                System.exit(1);
            }
            FileManager fileManager = new FileManager(CLI_ARGUMENT);
            OrganizationValidator organizationValidator = new OrganizationValidator(userScanner);
            CollectionManager collectionManager = new CollectionManager(fileManager);
            CommandManager commandManager = new CommandManager(
                    new clear(collectionManager),
                    new executeScript(),
                    new exit(),
                    new filterContainsName(collectionManager),
                    new filterLessThanType(collectionManager),
                    new help(),
                    new history(),
                    new info(collectionManager),
                    new insert(collectionManager, organizationValidator),
                    new printFieldDescendingAnnualTurnover(collectionManager),
                    new removeKey(collectionManager),
                    new removeLower(collectionManager),
                    new removeLowerKey(collectionManager),
                    new save(collectionManager),
                    new show(collectionManager),
                    new updateID(collectionManager, organizationValidator)
            );
            Console console = new Console(commandManager, userScanner, organizationValidator);
            console.InteractiveMode();
            Console.println("             We'll meet again");
            Console.println("    Don't know where, don't know when");
            Console.println("But I know we'll meet again some sunny day.");
    }
}