/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import utility.Console;

/**
 * Class for command history.
 */
public class history extends Command{
    /**
     * Constructor for command class history.
     */
    public history() {
        super("history", "outputs the history of last 9 used commands");
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg) {
        try {
            if (!arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute?", new RuntimeException());
            return true;
        } catch (InvalidElementCountException iece) {
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}

