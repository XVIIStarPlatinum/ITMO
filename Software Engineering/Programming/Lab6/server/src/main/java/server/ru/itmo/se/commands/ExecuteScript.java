package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command execute_script. It executes a sequence of commands in a file.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the execute_script class.
 */
@ToString
public class ExecuteScript extends CommandImpl {
    /**
     * Constructs an execute_script.
     */
    public ExecuteScript() {
        super("execute_script", "<file_name>", "Executes a script from a given file", CommandType.WITH_ARGS);
    }
    /**
     * This method is an implementation of the abstract apply() method for the execute_script command.
     *
     * @param commandStrArg the command's string argument (necessary).
     * @param commandObjArg the command's object argument (unnecessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You need an argument here.", new RuntimeException());
            }
            ResponseAppender.appendln("Executing script '" + commandStrArg + "' right now...");
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}
