/**
 * Package for custom commands.
 */
package commands;
import utility.Console;
import utility.CollectionManager;
import exceptions.InvalidElementCountException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for command show.
 */
public class show extends Command{
    /**
     * Instance of class CollectionManager
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class show.
     * @param collectionManager collection manager
     */
    public show(CollectionManager collectionManager){
        super("show", "outputs all elements of the collection");
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
            if (!arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute??", new RuntimeException());
            if (collectionManager.collectionSize() == 0) Console.println("Empty collection");
            List<Integer> keyList = new LinkedList<>(collectionManager.getCollection().keySet());
            keyList.sort(Comparator.naturalOrder());
            for (final Integer i : keyList){
                Console.println(i + ". " + collectionManager.getCollection().get(i));
            }
            return true;
        } catch (InvalidElementCountException iece) {
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}

