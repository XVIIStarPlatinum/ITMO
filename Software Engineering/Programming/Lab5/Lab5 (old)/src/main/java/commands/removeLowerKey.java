/**
 * Package for custom commands.
 */
package commands;

import data.Organization;
import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import exceptions.NullOrganizationException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command remove_lower_key.
 */
public class removeLowerKey extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructor for command class remove_lower_key.
     * @param collectionManager collection manager
     */
    public removeLowerKey(CollectionManager collectionManager){
        super("remove_lower_key <int>", "removes from the collection all elements with lower key value than given");
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
            int id = Integer.parseInt(arg);
            Organization groupToRemove = collectionManager.getByID(id);
            if (groupToRemove == null) throw new NullOrganizationException("No such organization", new RuntimeException());
            collectionManager.removeLowerKey(id);
            Console.println("Organization successfully removed!");
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage:  " + getName() + "'");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        } catch (NumberFormatException exception) {
            Console.printError("ID must be an integer");
        } catch (NullOrganizationException exception) {
            Console.printError("There's no organization with key value less than given");
        }
        return false;
    }
}

