/**
 * Package for custom commands.
 */
package commands;

import utility.Console;
import utility.CollectionManager;
import exceptions.InvalidElementCountException;

/**
 * Class for command save.
 */
public class save extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class save.
     * @param collectionManager collection manager
     */
    public save(CollectionManager collectionManager){
        super("save", "saves a collection into a file");
        this.collectionManager = collectionManager;
    }

    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg){
        try {
            if (!arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute?", new RuntimeException());
            collectionManager.saveCollection();
            Console.println("File may be successfully saved. If there's an error message above this one, it means that the file is unable to be saved.");
            return true;
        } catch (InvalidElementCountException iece) {
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}
