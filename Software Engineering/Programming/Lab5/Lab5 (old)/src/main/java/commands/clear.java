/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command clear.
 */
public class clear extends Command {
    /**
     * Initialization of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class clear.
     * @param collectionManager collection manager
     */
    public clear(CollectionManager collectionManager) {
        super("clear", "clears the collection");
        this.collectionManager = collectionManager;
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg) {
        try {
            if (!arg.isEmpty()) throw new InvalidElementCountException("No elements in the collection", new RuntimeException());
            collectionManager.clearCollection();
            Console.println("Collection successfully cleared.");
            return true;
        } catch (InvalidElementCountException iece) {
            Console.println("Usage: '" + getName() + "'");
            return false;
        }
    }

}

