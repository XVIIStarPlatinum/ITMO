/**
 * Package for custom commands.
 */
package commands;

import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import utility.CollectionManager;
import utility.Console;

/**
 * Class for command print_field_descending_annual_turnover.
 */
public class printFieldDescendingAnnualTurnover extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class print_field_descending_annual_turnover
     * @param collectionManager collection manager
     */
    public printFieldDescendingAnnualTurnover(CollectionManager collectionManager){
        super("print_field_descending_annual_turnover", "outputs all collections by descending order of the field annualTurnover");
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
            if (!arg.isEmpty()) throw new InvalidElementCountException("Inappropriate element count", new RuntimeException());
            collectionManager.printFieldDescendingAnnualTurnover();
            if (collectionManager.collectionSize() == 0) throw new EmptyCollectionException("Empty collection", new RuntimeException());
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        }
        return false;
    }
}
