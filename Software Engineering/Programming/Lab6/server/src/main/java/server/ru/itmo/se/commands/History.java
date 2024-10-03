package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.EmptyHistoryException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CommandManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command History. It outputs the 10 most recently used commands without their arguments.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the History class.
 */
@ToString
public class History extends CommandImpl {
    /**
     * This field holds an instance of a CommandManager which is responsible for operations with commands.
     */
    private final CommandManager commandManager;
    /**
     * Constructs a History with the specified CommandManager.
     * @param commandManager the specified CommandManager.
     */
    public History(CommandManager commandManager) {
        super("history", "", "Outputs the 10 last used commands", CommandType.WITHOUT_ARGS);
        this.commandManager = commandManager;
    }
    /**
     * This method is an implementation of the abstract apply() method for the History command.
     * @param commandStrArg the command's string argument (unnecessary).
     * @param commandObjArg the command's object argument (unnecessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (!commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            if (commandManager.commandHistory[0] == null) {
                throw new EmptyHistoryException("You just started this session, of course the history is empty.", new RuntimeException());
            }
            ResponseAppender.appendln("Recently used 10 commands:");
            for (String s : commandManager.commandHistory) {
                if (s != null) ResponseAppender.appendln(" " + s);
            }
            return true;
        } catch (EmptyHistoryException e) {
            ResponseAppender.appendError("Not a single command was executed yet.");
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}
