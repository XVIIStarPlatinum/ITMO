/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import exceptions.InvalidInputException;
import utility.Console;

/**
 * Class for command help.
 */
public class help extends Command {
    /**
     * Constructor for command class help.
     */
    public help() {
        super("help", "shows the list for available commands");
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg) throws InvalidInputException {
        try {
            if (!arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute?", new RuntimeException());
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}

