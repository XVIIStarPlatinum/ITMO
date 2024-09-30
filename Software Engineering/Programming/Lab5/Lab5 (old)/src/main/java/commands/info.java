/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import utility.Console;
import utility.CollectionManager;

import java.time.LocalDateTime;

/**
 * Class for command info.
 */
public class info extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructor for command class info.
     * @param collectionManager collection manager
     */
    public info(CollectionManager collectionManager){
        super("info", "gives information about the collection");
        this.collectionManager = collectionManager;
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg){
        try{
            if(!arg.isEmpty()) throw new InvalidElementCountException("Inappropriate element count", new RuntimeException());
            LocalDateTime lastInitTime = collectionManager.getLastInitTime();
            String strLastInitTime = (lastInitTime == null) ? "Initialization in this session has not yet happened" :
                    lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();
            LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
            String lastSaveTimeString = (lastSaveTime == null) ? "Saving in this session has not yet happened" :
                    lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();
            Console.println("Information about the collection:");
            Console.println("Type: " + collectionManager.getCollectionType());
            Console.println("Element count: " + collectionManager.collectionSize());
            Console.println("Last save time: " + lastSaveTimeString);
            Console.println("Last init time: " + strLastInitTime);
            return true;
        } catch (InvalidElementCountException iece){
            Console.println("Usage: " + getName() + "'");
        }
        return false;
    }
}

