package server.ru.itmo.se.utility;

import lombok.Getter;
import server.ru.itmo.se.commands.CommandImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class used for operations with commands and their management. Some of them are implemented here.
 */
public class CommandManager {
    /**
     * This field stores the capacity of command History.
     */
    private static final int COMMAND_HISTORY_SIZE = 10;
    /**
     * This field stores the most recent 10 commands.
     */
    public String[] commandHistory = new String[COMMAND_HISTORY_SIZE];
    /**
     * This field stores all instances of commands.
     * -- GETTER --
     * Getter method for all instances of commands.
     */
    @Getter
    private final List<String> commands = new ArrayList<>();
    /**
     * This structure maps all aliases to their corresponding commands.
     */
    public Map<String, CommandImpl> commandMap = new LinkedHashMap<>();

    {
        commands.add("add");
        commands.add("clear");
        commands.add("execute_script");
        commands.add("exit");
        commands.add("filter_less_than_number_of_participants");
        commands.add("group_counting_by_establishment_date");
        commands.add("help");
        commands.add("history");
        commands.add("info");
        commands.add("print_field_descending_establishment_date");
        commands.add("remove_at");
        commands.add("remove_by_id");
        commands.add("show");
        commands.add("shuffle");
        commands.add("update");
    }
    /**
     * This method add an alias with its corresponding command.
     * @param commandAlias the command alias (full and shorthand).
     * @param command the command itself.
     */
    protected void addCommand(String commandAlias, CommandImpl command) {
        this.commandMap.put(commandAlias, command);
    }


    /**
     * This method is used to register the 10 most recently used command into history.
     * @param recentCommand the most recent command.
     */
    void addToHistory(String recentCommand) {
        if (commands.contains(recentCommand)) {
            for (int i = COMMAND_HISTORY_SIZE - 1; i > 0; i--) {
                commandHistory[i] = commandHistory[i - 1];
            }
            commandHistory[0] = recentCommand;
        }
    }

    /**
     * This method is used to signify to the user that the command is unavailable.
     * @param arg unavailable command.
     */
    static void noSuchCommand(String arg) {
        ResponseAppender.appendln("Command '" + arg + "' not found. Use command 'help' for advice.");
    }

    /**
     * A custom implementation of the toString() method in CommandManager.
     * @return information about this class.
     */
    @Override
    public String toString() {
        return "CommandManager (utility class for command logic).";
    }
}
