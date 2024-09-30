/**
 * Utility package for console app.
 */
package utility;

import commands.Command;
import exceptions.EmptyHistoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing commands.
 * @author Ariguun Erkevich Bolorbold
 */
public class CommandManager {
    /**
     * Field for size of command history.
     */
    private final int CommandHistorySize = 9;
    /**
     * Field for array initialization of command history.
     */
    private final String[] CommandHistory = new String[CommandHistorySize];
    /**
     * Field for list initialization of used commands.
     */
    private final List<Command> commands = new ArrayList<>();
    /**
     * Field for command clear.
     */
    private final Command clear;
    /**
     * Field for command exit.
     */
    private final Command exit;
    /**
     * Field for command execute_script.
     */
    private final Command executeScript;
    /**
     * Field for command filter_contains_name.
     */
    private final Command filterContainsName;
    /**
     * Field for command filter_less_than_type.
     */
    private final Command filterLessThanType;
    /**
     * Field for command help.
     */
    private final Command help;
    /**
     * Field for command history.
     */
    private final Command history;
    /**
     * Field for command info.
     */
    private final Command info;
    /**
     * Field for command insert.
     */
    private final Command insert;
    /**
     * Field for command print_field_descending_annual_turnover.
     */
    private final Command printFieldDescendingAnnualTurnover;
    /**
     * Field for command remove_key.
     */
    private final Command removeKey;
    /**
     * Field for command remove_lower.
     */
    private final Command removeLower;
    /**
     * Field for command remove_lower_key.
     */
    private final Command removeLowerKey;
    /**
     * Field for command save.
     */
    private final Command save;
    /**
     * Field for command show.
     */
    private final Command show;
    /**
     * Field for command update.
     */
    private final Command updateID;

    /**
     * Constructor of utility class CommandManager.
     * @param clear clear
     * @param executeScript execute_script
     * @param exit exit
     * @param filterContainsName filter_contains_name
     * @param filterLessThanType filter_less_than_type
     * @param help help
     * @param history history
     * @param info info
     * @param insert insert
     * @param printFieldDescendingAnnualTurnover print_field_descending_annual_turnover
     * @param removeKey remove_key
     * @param removeLower remove_lower
     * @param removeLowerKey remove_lower_key
     * @param save save
     * @param show show
     * @param updateID update
     */
    public CommandManager(Command clear, Command executeScript, Command exit, Command filterContainsName, Command filterLessThanType, Command help, Command history, Command info, Command insert, Command printFieldDescendingAnnualTurnover, Command removeKey, Command removeLower, Command removeLowerKey, Command save, Command show, Command updateID){
        this.clear = clear;
        this.executeScript = executeScript;
        this.exit = exit;
        this.filterContainsName = filterContainsName;
        this.filterLessThanType = filterLessThanType;
        this.help = help;
        this.history = history;
        this.info = info;
        this.insert = insert;
        this.printFieldDescendingAnnualTurnover = printFieldDescendingAnnualTurnover;
        this.removeKey = removeKey;
        this.removeLower = removeLower;
        this.removeLowerKey = removeLowerKey;
        this.save = save;
        this.show = show;
        this.updateID = updateID;

        commands.add(clear);
        commands.add(executeScript);
        commands.add(exit);
        commands.add(filterContainsName);
        commands.add(filterLessThanType);
        commands.add(help);
        commands.add(history);
        commands.add(info);
        commands.add(insert);
        commands.add(printFieldDescendingAnnualTurnover);
        commands.add(removeKey);
        commands.add(removeLower);
        commands.add(removeLowerKey);
        commands.add(save);
        commands.add(show);
        commands.add(updateID);
    }

    /**
     * Getter of command history.
     * @return commandHistory array string
     */
    public String[] getCommandHistory(){
        return CommandHistory;
    }

    /**
     * Getter of command history as a list object.
     * @return list of commands
     */
    public List<Command> getCommands(){
        return commands;
    }
    /**
     * This method adds commands to history.
     * @param commandRecent String
     * @throws NullPointerException exception
     */
    public void addToHistory(String commandRecent) throws NullPointerException{
        for (Command command : commands){
            if (command.getName().split(" ")[0].equals(commandRecent)){
                for (int i = CommandHistorySize-1; i>0; i--) {
                    CommandHistory[i] = CommandHistory[i-1];
                }
                CommandHistory[0] = commandRecent;
            }
        }
    }
    /**
     * This method is invoked when the user inputs an unavailable command.
     * @param arg String
     */
    public void noSuchCommand(String arg) {
        Console.println("Command '" + arg + "' not found. Use command 'help' for advice.");
    }
    /**
     * This method invokes the command execute_script.
     * @param arg String
     * @return boolean value
     */
    public boolean executeScript(String arg){
        return executeScript.apply(arg);
    }

    /**
     * This method invokes the command help.
     * @param arg String
     * @return boolean value
     */
    public boolean help(String arg) {
        if (help.apply(arg)) {
            Console.printTable("             COMMAND NAME", "                                 COMMAND SPECIFICATION");
            for (Command command : commands) {
                Console.printTable(command.getName(), command.getSpec());
            }
            return true;
        } else return false;
    }
    /**
     * This method invokes the command info.
     * @param arg String
     * @return boolean value
     */
    public boolean info(String arg) {
        return info.apply(arg);
    }
    /**
     * This method invokes the command show.
     * @param arg String
     * @return boolean value
     */
    public boolean show(String arg) {
        return show.apply(arg);
    }
    /**
     * This method invokes the command exit.
     * @param arg String
     * @return boolean value
     */
    public boolean exit(String arg) {
        return exit.apply(arg);
    }
    /**
     * This method invokes the command insert.
     * @param arg String
     * @return boolean value
     */
    public boolean insert(String arg) {
        return insert.apply(arg);
    }
    /**
     * This method invokes the command print_field_descending_annual_turnover
     * @param arg String
     * @return boolean value
     */
    public boolean printFieldDescendingAnnualTurnover(String arg){
        return printFieldDescendingAnnualTurnover.apply(arg);
    }
    /**
     * This method invokes the command history.
     * @param arg String
     * @return boolean value
     */
    public boolean history(String arg){
        if (history.apply(arg)) {
            try {
                if (CommandHistory.length == 0) throw new EmptyHistoryException("You just started this session, that means the history is empty", new RuntimeException());
                Console.println("Last used commands:");
                for (String s : CommandHistory) {
                    if (s != null) Console.println(" " + s);
                }
                return true;
            } catch (EmptyHistoryException exception) {
                Console.println("Not a single command was used yet!");
            }
        }
        return false;
    }
    /**
     * This method invokes the command update.
     * @param arg String
     * @return boolean value
     */
    public boolean updateID(String arg){
        return updateID.apply(arg);
    }
    /**
     * This method invokes the command clear.
     * @param arg String
     * @return boolean value
     */
    public boolean clear(String arg){
        return clear.apply(arg);
    }
    /**
     * This method invokes the command filter_contains_name.
     * @param arg String
     * @return boolean value
     */
    public boolean filterContainsName(String arg){
        return filterContainsName.apply(arg);
    }
    /**
     *This method invokes the command filter_less_than_type.
     * @param arg String
     * @return boolean value
     */
    public boolean filterLessThanType(String arg){
        return filterLessThanType.apply(arg);
    }
    /**
     * This method invokes the command remove_key.
     * @param arg String
     * @return boolean value
     */
    public boolean removeKey(String arg){
        return removeKey.apply(arg);
    }
    /**
     * This method invokes the command remove_lower.
     * @param arg String
     * @return boolean value
     */
    public boolean removeLower(String arg){
        return removeLower.apply(arg);
    }
    /**
     * This method invokes the command remove_lower_key.
     * @param arg String
     * @return boolean value
     */
    public boolean removeLowerKey(String arg){
        return removeLowerKey.apply(arg);
    }
    /**
     * This method invokes the command save.
     * @param arg String
     * @return boolean value
     */
    public boolean save(String arg){
        return save.apply(arg);
    }
    /**
     * CommandManager implementation of general method toString()
     * @return String
     */
    @Override
    public String toString() {
        return "CommandManager (utility class for commands)";
    }
}

