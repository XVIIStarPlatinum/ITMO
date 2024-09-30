/**
 * Package for custom commands.
 */
package commands;

import data.OrganizationType;
import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command filter_less_than_type.
 */
public class filterLessThanType extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class filter_less_than_type.
     * @param collectionManager collection manager.
     */
    public filterLessThanType(CollectionManager collectionManager){
        super("filter_less_than_type <type>", "outputs elements which are less than the given type (by ordinal)");
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
            if (arg.isEmpty()) throw new InvalidElementCountException("Inappropriate element count", new RuntimeException());
            if (collectionManager.collectionSize() == 0) throw new EmptyCollectionException("Empty collection", new RuntimeException());
            OrganizationType organizationType = OrganizationType.valueOf(arg.toUpperCase());
            String filteredInfo = collectionManager.OrganizationTypeFilteredInfo(organizationType);
            if (!filteredInfo.isEmpty()) {
                Console.println(filteredInfo);
                return true;
            } else Console.println("No organizations with less value than given type");
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        } catch (IllegalArgumentException exception) {
            Console.printError("No such type among valid organization types!");
            Console.println("List of types - " + OrganizationType.nameList());
        }
        return false;
    }
}

