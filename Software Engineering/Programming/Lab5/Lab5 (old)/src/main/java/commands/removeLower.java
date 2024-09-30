/**
 * Package for custom commands.
 */
package commands;

import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command remove_lower.
 */
public class removeLower extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class remove_lower.
     * @param collectionManager collection manager
     */
    public removeLower(CollectionManager collectionManager){
        super("remove_lower <int>", "removes all elements lesser than given from the collection (compared by annual turnover)");
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
            if (arg.isEmpty()) throw new InvalidElementCountException("Inappropriate element count", new IllegalArgumentException());
            if (collectionManager.collectionSize() == 0) throw new EmptyCollectionException("Empty collection", new RuntimeException());
            collectionManager.removeLower(Double.parseDouble(arg));
            Console.println("Organizations successfully removed!");
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: " + getName() + "'");
        } catch (IllegalArgumentException e){
            Console.println("Argument must be a float type number");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        }
        return false;
    }
}

