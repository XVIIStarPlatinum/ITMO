package client.ru.itmo.se.utility;

import client.ru.itmo.se.App;
import common.ru.itmo.se.data.*;
import common.ru.itmo.se.exceptions.*;
import common.ru.itmo.se.exceptions.IllegalStateException;
import common.ru.itmo.se.interaction.CommandType;
import common.ru.itmo.se.interaction.MusicBandRaw;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.ResponseCode;
import common.ru.itmo.se.utility.PrettyPrinter;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class used for interpreting user requests.
 */
public class UserHandler {
    /**
     * This field holds the scanner via which the application will receive inputs.
     */
    private Scanner userScanner;
    /**
     * This field holds commands that were input from a script file.
     */
    private final Stack<File> scriptStack = new Stack<>();
    /**
     * This field holds Scanners that are used for receiving inputs from a script file.
     */
    private final Stack<Scanner> scannerStack = new Stack<>();
    /**
     * This structure maps all typos caused by not switching from the Russian ᠱ"ЙЦУКЕН" layout to their actual commands.
     */
    private final Map<String, String> typoCommandMap = new LinkedHashMap<>();
    /**
     * This structure maps all commands to their corresponding types. It is used to discern better between commands.
     */
    private final Map<String, CommandType> commandTypeMap = new LinkedHashMap<>();
    /**
     * This structure maps all shorthand input (for long commands only, typing them is a pain in the ass) to their actual commands.
     * -- GETTER --
     * Getter method for the shorthand map.
     */
    @Getter
    private final Map<String, String> shortHandCommandMap = new LinkedHashMap<>();
    /**
     * Constructs a UserHandler with the specified userScanner.
     * @param userScanner a Scanner instance which takes input from the user.
     */
    public UserHandler(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    {
        typoCommandMap.put("фвв", "add");
        typoCommandMap.put("сдуфк", "clear");
        typoCommandMap.put("учусгеу_ыскшзе", "execute_script");
        typoCommandMap.put("учы", "execute_script");
        typoCommandMap.put("учше", "exit");
        typoCommandMap.put("ашдеук_дуыы_ерфт_тгьиук_ща_зфкешсшзфтеы", "filter_less_than_number_of_participants");
        typoCommandMap.put("адетщз", "filter_less_than_number_of_participants");
        typoCommandMap.put("пкщгз_сщгтештп_ин_уыефидшырьуте_вфеу", "group_counting_by_establishment_date");
        typoCommandMap.put("псиув", "group_counting_by_establishment_date");
        typoCommandMap.put("рудз", "help");
        typoCommandMap.put("ршыещкн", "history");
        typoCommandMap.put("штащ", "info");
        typoCommandMap.put("зкште_ашудв_вуысутвштп_уыефидшырьуте_вфеу", "print_field_descending_establishment_date");
        typoCommandMap.put("завув", "print_field_descending_establishment_date");
        typoCommandMap.put("куьщму_фе", "remove_at");
        typoCommandMap.put("куьщму_ин_шв", "remove_by_id");
        typoCommandMap.put("к_фе", "remove_at");
        typoCommandMap.put("к_шв", "remove_by_id");
        typoCommandMap.put("ыфму", "save");
        typoCommandMap.put("ыукмук_учше", "server_exit");
        typoCommandMap.put("ырщц", "show");
        typoCommandMap.put("ыргааду", "shuffle");
        typoCommandMap.put("гзвфеу", "update");
        commandTypeMap.put("add", CommandType.WITH_FORM);
        commandTypeMap.put("clear", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("execute_script", CommandType.WITH_ARGS);
        commandTypeMap.put("exit", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("filter_less_than_number_of_participants", CommandType.WITH_ARGS);
        commandTypeMap.put("group_counting_by_establishment_date", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("help", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("history", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("info", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("print_field_descending_establishment_date", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("remove_at", CommandType.WITH_ARGS);
        commandTypeMap.put("remove_by_id", CommandType.WITH_ARGS);
        commandTypeMap.put("save", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("server_exit", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("show", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("shuffle", CommandType.WITHOUT_ARGS);
        commandTypeMap.put("update", CommandType.WITH_ARGS_FORM);
        shortHandCommandMap.put("exs", "execute_script");
        shortHandCommandMap.put("fltnop", "filter_less_than_number_of_participants");
        shortHandCommandMap.put("gcbed", "group_counting_by_establishment_date");
        shortHandCommandMap.put("pfded", "print_field_descending_establishment_date");
        shortHandCommandMap.put("r_at", "remove_at");
        shortHandCommandMap.put("r_id", "remove_by_id");
    }

    /**
     * This method is used to interpret user requests and server responses.
     * @param serverResponseCode response code which will determine the method's output.
     * @return interpreted request from the user.
     */
    public Request handle(ResponseCode serverResponseCode) {
        String userInput;
        String[] userCommand;
        ProcessingCode processingCode;
        int rewriteAttempts = 0;
        try {
            do {
                try {
                    if(fileMode() && (serverResponseCode == ResponseCode.ERROR || serverResponseCode == ResponseCode.SERVER_EXIT)) {
                        throw new IncorrectScriptException("", new RuntimeException());
                    }
                    while(fileMode() && !userScanner.hasNextLine()) {
                        userScanner.close();
                        userScanner = scannerStack.pop();
                        PrettyPrinter.println("Returning from script '" + scriptStack.pop().getName() + "'...");
                    }
                    if(fileMode()) {
                        userInput = userScanner.nextLine();
                        if(!userInput.isEmpty()) {
                            PrettyPrinter.print(App.CS1);
                            PrettyPrinter.println(userInput);
                        }
                    } else {
                        PrettyPrinter.print(App.CS1);
                        userInput = userScanner.nextLine();
                    }
                    userCommand = (userInput.trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                } catch (NoSuchElementException | IllegalStateException e) {
                    PrettyPrinter.println();
                    PrettyPrinter.printError("An error occurred during command input.");
                    userCommand = new String[]{"", ""};
                    rewriteAttempts++;
                    int maxRewriteAttempts = 1;
                    if (rewriteAttempts >= maxRewriteAttempts) {
                        PrettyPrinter.printError("Rewrite limit has been exceeded.");
                        System.exit(0);
                    }
                }
                processingCode = processCommand(userCommand[0], userCommand[1]);
            } while (processingCode == ProcessingCode.ERROR && !fileMode() || userCommand[0].isEmpty());
            try {
                if(fileMode() && serverResponseCode == ResponseCode.ERROR || processingCode == ProcessingCode.ERROR) {
                    throw new IncorrectScriptException("", new RuntimeException());
                }
                switch(processingCode) {
                    case OBJECT:
                        MusicBandRaw musicBandAddRaw = generateMusicBandAdd();
                        return new Request(userCommand[0], userCommand[1], musicBandAddRaw);
                    case UPDATE:
                        MusicBandRaw musicBandUpdateRaw = generateMusicBandUpdate();
                        return new Request(userCommand[0], userCommand[1], musicBandUpdateRaw);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if(!scriptFile.exists()) {
                            throw new FileNotFoundException();
                        }
                        if(!scriptStack.empty() && scriptStack.search(scriptFile) != -1) {
                            throw new RecursionException("Execution error: Please debug your script.", new RuntimeException());
                        }
                        scannerStack.push(userScanner);
                        scriptStack.push(scriptFile);
                        userScanner = new Scanner(scriptFile);
                        PrettyPrinter.println("Executing script '" + scriptFile.getName() + "' right now...");
                        break;
                }
            } catch (FileNotFoundException e) {
                PrettyPrinter.printError("Script file not found.\nIf there is one, then try changing the permission of the file.\nMaybe chmod 777, idk.");
            } catch (RecursionException e) {
                PrettyPrinter.printError("Critical error: Recursion detected in script file.");
                throw new IncorrectScriptException("Execution error: Please debug your script.", new RuntimeException());
            }
        } catch (IncorrectScriptException e) {
            PrettyPrinter.printError("Execution error: Please debug your script.");
            while(!scannerStack.isEmpty()) {
                userScanner.close();
                userScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return new Request();
        }
        return new Request(userCommand[0], userCommand[1]);
    }

    /**
     * This method is used to interpret and validate user inputs.
     * @param command    the command's name.
     * @param commandArg the command's argument.
     * @return processing code which determines the console app's output.
     */
    private ProcessingCode processCommand(String command, String commandArg) {
        try {
            if(shortHandCommandMap.containsKey(command)) {
                command = shortHandCommandMap.get(command);
            }
            if(Pattern.matches(".*\\p{InCyrillic}.*", command)) {
                command = typoTranscript(command);
            }
            if(shortHandCommandMap.containsKey(command)) {
                command = shortHandCommandMap.get(command);
            }
            CommandType commandType = commandTypeMap.get(command);
            if(commandType == null) {
                PrettyPrinter.println("Command '" + command + "' not found. Use command 'help' for advice.");
                return ProcessingCode.ERROR;
            } else {
                return switch (commandType) {
                    case WITHOUT_ARGS -> {
                        if(command.equals("save")) {
                            throw new InvalidInputException("This command is deprecated as per v2.3.", new RuntimeException());
                        }
                        if(command.equals("server_exit")){
                            throw new InvalidInputException("This command was once an old programmer's pipe dream...", new RuntimeException());
                        }
                        if (!commandArg.isEmpty()) {
                            throw new CommandUsageException("", new RuntimeException());
                        }
                        yield ProcessingCode.OK;
                    }
                    case WITH_ARGS -> {
                        switch (command) {
                            case "remove_at", "remove_by_id" -> {
                                if(commandArg.isEmpty()) throw new CommandUsageException("<id>", new RuntimeException());
                                yield ProcessingCode.OK;
                            }
                            case "filter_less_than_number_of_participants" -> {
                                if(commandArg.isEmpty()) throw new CommandUsageException("<number_of_participants>", new RuntimeException());
                                yield ProcessingCode.OK;
                            }
                            case "execute_script" -> {
                                if(commandArg.isEmpty()) throw new CommandUsageException("<file_name>", new RuntimeException());
                                yield ProcessingCode.SCRIPT;
                            }
                        }
                        yield ProcessingCode.OK;
                    }
                    case WITH_ARGS_FORM -> {
                        if (commandArg.isEmpty()) {
                            throw new CommandUsageException("<id> {element}", new RuntimeException());
                        }
                        yield ProcessingCode.UPDATE;
                    }
                    case WITH_FORM -> {
                        if (!commandArg.isEmpty()) {
                            throw new CommandUsageException("{element}", new RuntimeException());
                        }
                        yield ProcessingCode.OBJECT;
                    }
                };
            }
        } catch (CommandUsageException e){
            if(e.getMessage() != null) {
                command += " " + e.getMessage();
                PrettyPrinter.println("Usage: '" + command + "'");
                return ProcessingCode.ERROR;
            }
        } catch (InvalidInputException e) {
            PrettyPrinter.printError("This command has been deprecated as per v2.3.");
            return ProcessingCode.ERROR;
        }
        return ProcessingCode.OK;
    }

    /**
     * This method corrects a typo caused by wrong keyboard layout (or a shorthand) into the actual command.
     *
     * @param typo the typo in Russian (or a shorthand).
     * @return autocorrected command.
     */
    String typoTranscript(String typo) {
        return typoCommandMap.get(typo);
    }

    /**
     * This method generates a MusicBand to be serialized and sent to the server.
     * @return Music Band in serializable form.
     */
    private MusicBandRaw generateMusicBandAdd() {
        MusicBandValidator musicBandValidator = new MusicBandValidator(userScanner);
        if(fileMode()) {
            musicBandValidator.setFileMode(fileMode());
        }
        return new MusicBandRaw(
                musicBandValidator.askName(),
                musicBandValidator.askCoordinates(),
                musicBandValidator.askNumberOfParticipants(),
                musicBandValidator.askEstablishmentDate(),
                musicBandValidator.askMusicGenre(),
                musicBandValidator.askStudio()
        );
    }

    /**
     * This method generates a MusicBand to be serialized and sent to the server as an update.
     * @return Music Band in serializable form.
     */
    private MusicBandRaw generateMusicBandUpdate() {
        MusicBandValidator musicBandValidator = new MusicBandValidator(userScanner);
        if(fileMode()) {
            musicBandValidator.setFileMode(fileMode());
        }
        String name = musicBandValidator.askQuestion("Do you want to change the name of the music band?") ? musicBandValidator.askName() : null;
        Coordinates coordinates = musicBandValidator.askQuestion("Do you want to change the coordinates of the organization?") ? musicBandValidator.askCoordinates() : null;
        Long numberOfParticipants = musicBandValidator.askQuestion("Do you want to change the number of participants?") ? musicBandValidator.askNumberOfParticipants() : null;
        LocalDateTime establishmentDate = musicBandValidator.askQuestion("Do you want to change the establishment date?") ? musicBandValidator.askEstablishmentDate() : null;
        MusicGenre musicGenre = musicBandValidator.askQuestion("Do you want to change the genre of music?") ? musicBandValidator.askMusicGenre() : null;
        Studio studio = musicBandValidator.askQuestion("Do you want to change the studio?") ? musicBandValidator.askStudio() : null;
        return new MusicBandRaw(
                name, coordinates,
                numberOfParticipants,
                establishmentDate,
                musicGenre, studio
        );
    }

    /**
     * This method determines whether the input is received from a file or not.
     * @return true if the input is from a file (script),<p>and false if the input is from a keyboard.
     */
    private boolean fileMode() {
        return !scannerStack.isEmpty();
    }
}
