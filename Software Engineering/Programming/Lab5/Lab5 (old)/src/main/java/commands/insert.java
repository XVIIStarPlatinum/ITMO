/**
 * Package for custom commands.
 */
package commands;

import data.Organization;
import exceptions.IllegalKeyException;
import exceptions.InvalidInputException;
import utility.CollectionManager;
import utility.Console;
import utility.OrganizationValidator;

import java.time.Instant;

/**
 * Class for command insert.
 */
public class insert extends Command {
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager ;
    /**
     * Instance of class organizationValidator.
     */
    private final OrganizationValidator organizationValidator;
    /**
     * Constructor for command class insert
     * @param collectionManager collection manager
     * @param organizationValidator organization validator
     */
    public insert(CollectionManager collectionManager, OrganizationValidator organizationValidator){
        super("insert null {element}", "inserts new elements with the given key");
        this.collectionManager = collectionManager;
        this.organizationValidator = organizationValidator;
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg){
        try {
            if (arg.isEmpty()) throw new IllegalArgumentException("There must be a key value", new RuntimeException());
            Integer intKey = Integer.valueOf(arg);
            if (intKey <= 0) throw new InvalidInputException("Element ID (key) must be a positive integer", new RuntimeException());
            if (collectionManager.getCollection().containsKey(intKey)) {
                throw new IllegalKeyException("Element with this ID already exists", new RuntimeException());
            }
            collectionManager.insertToCollection(intKey, new Organization(
                    collectionManager.generateNextId(),
                    organizationValidator.askName(),
                    organizationValidator.askCoordinates(),
                    java.util.Date.from(Instant.now()),
                    organizationValidator.askAnnualTurnover(),
                    organizationValidator.askOrganizationType(),
                    organizationValidator.askAddress()
            ));
            Console.println("\u001b[3m" + "\"A fine addition to my collection.\" \u2014 General Grievous" + "\u001b[0m");
            return true;
        } catch (IllegalArgumentException exception) {
            Console.println("Usage: '" + getName() + "'");
        } catch (InvalidInputException exception) {
            Console.printError("Key value must be a natural number");
        } catch (IllegalKeyException ike){
            Console.printError("Element with this ID already exists");
        }
        return false;
    }
}

