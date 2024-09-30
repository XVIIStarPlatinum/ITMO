/**
 * Package for custom commands.
 */
package commands;

import data.Organization;
import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import exceptions.InvalidInputException;
import exceptions.NullOrganizationException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command remove_key.
 */
public class removeKey extends Command {
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class remove_key.
     * @param collectionManager collection manager
     */
    public removeKey(CollectionManager collectionManager) {
        super("remove_key <int>", "removes an element of the collection through it's key value");
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
            if (arg.isEmpty()) throw new InvalidElementCountException("Inappropriate element count", new RuntimeException());
            if (collectionManager.collectionSize() == 0) throw new EmptyCollectionException("Empty collection", new RuntimeException());
            Organization orgToFind = collectionManager.getByID(Integer.valueOf(arg));
            if(orgToFind == null) throw new NullOrganizationException("There's no such organization", new RuntimeException());
            collectionManager.removeFromCollection(Integer.valueOf(arg));
            Console.println("Organization successfully deleted");
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        } catch (NullOrganizationException exception) {
            Console.printError("No group with given key value");
        } catch (InvalidInputException exception) {
            Console.printError("What am I supposed to remove? Give me an ID");
        }
        return false;
    }
}
