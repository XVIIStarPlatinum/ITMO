package utility;

import consoleApp.Main;
import data.*;

import java.util.*;

public class ContentValidator {
    /**
     * Field for initialization of a FileManager instance.
     */
    FileManager fileManager = new FileManager(Main.CLI_ARGUMENT);
    /**
     * Validates the contents of a file in case of external editing.
     */
    public Hashtable<Integer, Organization> validateContent() {
        Hashtable<Integer, Organization> org = fileManager.readCollection();
        Set<Integer> keys = org.keySet();
        for (Integer i : keys) {
            Organization orgToCheck = org.get(i);
            Integer ID = orgToCheck.getId();
            String name = orgToCheck.getName();
            Coordinates coordinates = orgToCheck.getCoordinates();
            double x = coordinates.getX();
            float y = coordinates.getY();
            Date date = orgToCheck.getCreationDate();
            Double annualTurnover = orgToCheck.getAnnualTurnover();
            OrganizationType type = orgToCheck.getType();
            Address address = orgToCheck.getOfficialAddress();
            String zip = address.getZipCode();
            if (OrganizationValidator.checkID(ID) || OrganizationValidator.checkUniqueID(ID)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " ID field was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization ID does not meet required constraints: ");
                org.remove(i);
            }
            if (OrganizationValidator.checkName(name)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " name value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's name is either null or empty");
                org.remove(i);
            }
            if (OrganizationValidator.checkX(x)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " abscissa value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's abscissa value must not be null");
                org.remove(i);
            }
            if (OrganizationValidator.checkY(y)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " ordinate value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's ordinate value exceeds the given limit of 614");
                org.remove(i);
            }
            if (OrganizationValidator.checkDate(date)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " date value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The creation time of this entry must not be null and is automatically generated");
                org.remove(i);
            }
            if (OrganizationValidator.checkAnnualTurnover(annualTurnover)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " annual turnover value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's annual turnover does not meet required constraints: must not be null and must be a positive integer");
                org.remove(i);
            }
            if (OrganizationValidator.checkOrgType(type)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " type value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's type does not match any of the available ones");
                org.remove(i);
            }
            if (OrganizationValidator.checkZipCode(zip)) {
                Console.printError("WARNING: This element's " + "(" + i + ")" + " zip code value was altered externally, and therefore to preserve the integrity of data constraints, will not be added to the collection");
                Console.printError("The organization's zip code does not meet required constraints: must not be null and must not be shorter than 9 characters");
                org.remove(i);
            }
        }
        return org;
    }
}
