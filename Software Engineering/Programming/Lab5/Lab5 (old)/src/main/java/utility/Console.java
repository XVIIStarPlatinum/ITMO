/**
 * Utility package for console app.
 */
package utility;

import exceptions.NullValueException;
import consoleApp.Main;
import exceptions.RecursionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * Class for console management.
 * @author Ariguun Erkevich Bolorbold
 */
public class Console {
    /**
     * Field for command manager
     */
    private final CommandManager commandManager;
    /**
     * Field for Scanner - used for user input.
     */
    private final Scanner userScanner;
    /**
     * Field for organization validator.
     */
    private final OrganizationValidator ov;
    /**
     * Field for list scriptStack - intended for script work.
     */
    private final List<String> scriptStack = new ArrayList<>();
    /**
     * Constructor for utility class Console.
     * @param commandManager command manager
     * @param sc stack
     * @param ov validator
     */
    public Console(CommandManager commandManager, Scanner sc, OrganizationValidator ov) {
        this.commandManager = commandManager;
        this.userScanner = sc;
        this.ov = ov;
    }

    /**
     * Method for invoking interactive mode.
     */
    public void InteractiveMode(){
        String[] userCommand;
        int commandStatus;
        try {
            do {
                Console.print(Main.CS1);
                userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                commandManager.addToHistory(userCommand[0]);
                commandStatus = executeCommand(userCommand);
            } while (commandStatus != 2);
        } catch (NoSuchElementException nsee) {
            Console.print("User input not detected");
        }
    }

    /**
     * Method for invoking script mode.
     * @param arg String
     * @return int
     */
    public int ScriptMode(String arg) {
        String[] userCommand;
        int commandStatus;
        scriptStack.add(arg);
        try (Scanner scrSc = new Scanner(new File(arg))) {
            if(!scrSc.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = ov.getUserScanner();
            ov.setUserScanner(scrSc);
            ov.setFileMode();
            do {
                userCommand = (scrSc.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (scrSc.hasNextLine() && userCommand[0].isEmpty()) {
                    userCommand = (scrSc.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                Console.println(Main.CS1 + String.join(" ", userCommand));
                if (userCommand[0].equals("execute_script")) {
                    for (String script : scriptStack) {
                        if (userCommand[1].equals(script))
                            throw new RecursionException("Unchecked recursion detected", new RuntimeException());
                    }
                }
                commandStatus = executeCommand(userCommand);
            } while(commandStatus == 0 && scrSc.hasNextLine());
            ov.setUserScanner(tmpScanner);
            ov.setUserMode();
            if(commandStatus == 1 && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())){
                Console.printError("EXECUTION ERROR: Please debug your script");
            return commandStatus;}
            else if(commandStatus == 2 && userCommand[0].equals("exit") && userCommand[1].isEmpty()){
                System.exit(0);
            }
        } catch(FileNotFoundException fnfe) {
            Console.printError("Script file not found. If there is one, then try changing the permission of the file. Maybe chmod 777, idk.");
        } catch(NullValueException iece) {
            Console.printError("Script file is empty");
        } catch(RecursionException re) {
            Console.printError("CRITICAL ERROR: Recursion detected in script file");
        } finally {
            scriptStack.remove(scriptStack.size()-1);
        }
        return 1;
    }
    /**
     * Method which executes user-input commands. Added cases just for banter.
     * @param userCommand String array
     * @return int
     */
    private int executeCommand(String[] userCommand) {
        String arg = userCommand[0].toLowerCase();
        switch (arg) {
            case "":
                break;
            case "clear", "сдуфк":
                if (!commandManager.clear(userCommand[1])) return 1;
                break;
            case "execute_script", "учусгеу_ыскшзе", "exs":
                if (!commandManager.executeScript(userCommand[1])) return 1;
                else return ScriptMode(userCommand[1]);
            case "exit", "учше":
                if (!commandManager.exit(userCommand[1])) return 1;
                else return 2;
            case "filter_contains_name", "ашдеук_сщтефшты_тфьу", "fcn":
                if (!commandManager.filterContainsName(userCommand[1])) return 1;
                break;
            case "filter_less_than_type", "ашдеук_дуыы_ерфт_ензу", "fltt":
                if (!commandManager.filterLessThanType(userCommand[1])) return 1;
                break;
            case "help", "рудз":
                if (commandManager.help(userCommand[1])) return 1;
                break;
            case "info", "штащ":
                if (!commandManager.info(userCommand[1])) return 1;
                break;
            case "insert", "штыуке":
                if (!commandManager.insert(userCommand[1])) return 1;
                break;
            case "history", "ршыещкн":
                if (!commandManager.history(userCommand[1])) return 1;
                break;
            case "print_field_descending_annual_turnover", "зкште_ашудв_вуысутвштп_фттгфд_егктщмук", "pfdat":
                if (!commandManager.printFieldDescendingAnnualTurnover(userCommand[1])) return 1;
                break;
            case "remove_key", "куьщму_лун", "rk":
                if (!commandManager.removeKey(userCommand[1])) return 1;
                break;
            case "remove_lower", "куьщму_дщцук", "rl":
                if (!commandManager.removeLower(userCommand[1])) return 1;
                break;
            case "remove_lower_key", "куьщму_дщцук_лун", "rlk":
                if (!commandManager.removeLowerKey(userCommand[1])) return 1;
                break;
            case "save", "ыфму":
                if (!commandManager.save(userCommand[1])) return 1;
                break;
            case "show", "ырщц":
                if (!commandManager.show(userCommand[1])) return 1;
                break;
            case "update", "гзвфеу":
                if (!commandManager.updateID(userCommand[1])) return 1;
                break;
            default:
                commandManager.noSuchCommand(userCommand[0]);
                return 1;
        }
        return 0;
    }
    /**
     * Custom console version of general print method.
     * @param toOut String
     */
    public static void print(Object toOut){
        System.out.print("\033[1;35m" + toOut + "\u001B[0m");
    }
    /**
     * Custom console version of general println method.
     * @param toOut String
     */
    public static void println(Object toOut){
        System.out.println("\u001B[32m" + toOut + "\u001B[0m");
    }
    /**
     * Custom console version of general err method.
     * @param toOut String
     */
    public static void printError(Object toOut){
        System.out.println("\u001B[41m" + "\u001B[30m" + toOut + "\u001B[0m");
    }
    /**
     * Custom console version of general printf method (adopted for printing tables).
     * @param e1 Object
     * @param e2 Object
     */
    public static void printTable(Object e1, Object e2){
        System.out.printf("\u001B[36m" + "| %-38s | %-88s | %n", e1, e2);
        System.out.printf("\u001B[35m" + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=%n");
    }

    /**
     * Console implementation of general method toString()
     * @return String
     */
    @Override
    public String toString(){
        return "Console: CLI class";
    }
}
