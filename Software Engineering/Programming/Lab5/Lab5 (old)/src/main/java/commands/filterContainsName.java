/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import utility.CollectionManager;
import utility.Console;
/**
 * Class for command filter_contains_name.
 */
public class filterContainsName extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class filter_contains_name.
     * @param collectionManager collection manager
     */
    public filterContainsName(CollectionManager collectionManager){
        super("filter_contains_name <name>", "outputs all elements that contain the given name");
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
            String filteredName = collectionManager.filterContainsName(arg);
            if (filteredName.equals("{}")) {
                Console.println("Organization with the name of '" + arg + "' not found.");
            } else {
                Console.println("Organizations with the name of '" + arg + "': " + filteredName);
            }
            return true;
        } catch (NumberFormatException exception) {
            Console.printError("Organization name must be a String value!");
        } catch (InvalidElementCountException exception) {
            Console.printError("Invalid argument count!");
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}

