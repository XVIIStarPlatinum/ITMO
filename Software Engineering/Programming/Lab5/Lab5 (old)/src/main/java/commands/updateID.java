/**
 * Package for custom commands.
 */
package commands;

import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;
import exceptions.EmptyCollectionException;
import exceptions.InvalidElementCountException;
import exceptions.InvalidInputException;
import exceptions.NullOrganizationException;
import utility.CollectionManager;
import utility.Console;
import utility.OrganizationValidator;

import java.time.LocalDate;
import java.util.Date;

/**
 * Class for command update id element
 */
public class updateID extends Command{
    /**
     * Instance of class CollectionManager.
     */
    private final CollectionManager collectionManager;
    /**
     * Instance of class OrganizationValidator.
     */
    private final OrganizationValidator organizationValidator;
    /**
     * Constructor for command class update.
     * @param collectionManager collection manager
     * @param organizationValidator organization validator
     */
    public updateID(CollectionManager collectionManager, OrganizationValidator organizationValidator) {
        super("update <ID> {element}", "updates an element's field through it's ID");
        this.collectionManager = collectionManager;
        this.organizationValidator = organizationValidator;
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg) {
        try {
            if (arg.isEmpty()) throw new InvalidInputException("User input not detected", new RuntimeException());
            if (collectionManager.collectionSize() == 0) throw new EmptyCollectionException("Empty collection", new RuntimeException());
            Integer id = Integer.valueOf(arg);
            if (!collectionManager.containsKey(id)) {
                throw new NullOrganizationException("There's no such organization", new RuntimeException());
            } else {
                Organization org = collectionManager.getByID(id);
                String name = org.getName();
                Coordinates coordinates = org.getCoordinates();
                Date creationDate = org.getCreationDate();
                Double annualTurnover = org.getAnnualTurnover();
                OrganizationType organizationType = org.getType();
                Address officialAddress = org.getOfficialAddress();
                collectionManager.removeFromCollection(id);
            if (organizationValidator.askQuestion("Do you want to change the name of the organization?")) name = organizationValidator.askName();
            if (organizationValidator.askQuestion("Do you want to change the coordinates of the organization?")) coordinates = organizationValidator.askCoordinates();
            if (organizationValidator.askQuestion("Do you want to change the annual turnover value of the organization?")) annualTurnover = organizationValidator.askAnnualTurnover();
            if (organizationValidator.askQuestion("Do you want to change the type of the organization?")) organizationType = organizationValidator.askOrganizationType();
            if (organizationValidator.askQuestion("Do you want to change the address of the organization?")) officialAddress = organizationValidator.askAddress();

            collectionManager.insertToCollection(id, new Organization(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    annualTurnover,
                    organizationType,
                    officialAddress
            ));
            }
            Console.println("Organization successfully updated.");
            return true;
        } catch (InvalidElementCountException exception) {
            Console.println("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException exception) {
            Console.printError("Empty collection");
        } catch (NumberFormatException exception) {
            Console.printError("ID must be an integer");
        } catch (NullOrganizationException exception) {
            Console.printError("No such organization with given ID");
        } catch (InvalidInputException exception) {
            Console.printError("What am I supposed to update? Give me an ID");
        }
        return false;
    }
}
