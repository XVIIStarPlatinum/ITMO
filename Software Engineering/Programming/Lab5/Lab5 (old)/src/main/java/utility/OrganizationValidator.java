/**
 * Utility package for console app.
 */
package utility;

import consoleApp.Main;
import exceptions.*;
import data.Address;
import data.Coordinates;
import data.OrganizationType;

import java.lang.IllegalStateException;
import java.util.*;

/**
 * Class for validating user input.
 * @author Ariguun Erkevich Bolorbold
 */
public class OrganizationValidator {
    /**
     * Field for y - maximum limit.
     */
    private static final int MAX_Y = 614;
    /**
     * Field for zip code - limit for minimum length of zip code.
     */
    private static final int MIN_ZIP = 9;
    /**
     * Field for annual turnover - limit for minimum value of annual turnover.
     */
    private static final int MIN_ANNUAL_TURNOVER = 0;
    /**
     * Field for Scanner - reads user input.
     */
    private Scanner userScanner;
    /**
     * Field for boolean fileMode.
     */
    private boolean fileMode;
    /**
     * Constructor of utility class OrganizationValidator.
     * @param userScanner Scanner
     */

    static Set<Integer> IDset = new TreeSet<>();
    static Set<Integer> keySet = new TreeSet<>();
    public OrganizationValidator(Scanner userScanner){
        this.userScanner = userScanner;
        fileMode = false;
    }
    /**
     * Setter for userScanner.
     * @param userScanner Scanner
     */
    public void setUserScanner(Scanner userScanner){
        this.userScanner = userScanner;
    }
    /**
     * Getter for userScanner.
     * @return userScanner
     */
    public Scanner getUserScanner() {
        return userScanner;
    }
    /**
     * Setter for fileMode.
     */
    public void setFileMode(){
        fileMode = true;
    }

    /**
     * Setter for userMode.
     */
    public void setUserMode(){
        fileMode = false;
    }

    /**
     * This method keeps user input name in line with limitations given by the task.
     * @return String name
     * @throws InvalidInputException exception
     */
    public String askName() throws InvalidInputException{
        String name;
        while (true) {
            try {
                Console.println("Insert name:");
                Console.print(Main.CS2);
                name = userScanner.nextLine().trim();
                if (fileMode) System.out.println(name);
                if (name.equals("")) throw new NullValueException("Field must not be empty", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                Console.printError("Name not found");
            } catch (NullValueException e) {
                Console.printError("Name mustn't be empty");
            } catch (IllegalStateException e) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return name;
    }
    /**
     * This method keeps user input x in line with limitations given by the task.
     * @return double x
     * @throws InvalidInputException exception
     */
    public double askX() throws InvalidInputException {
        String strX;
        double x;
        while (true) {
            try {
                Console.println("Insert coordinate x:");
                Console.print(Main.CS2);
                strX = userScanner.nextLine().trim();
                if (fileMode) Console.println(strX);
                x = Double.parseDouble(strX);
                if (checkX(x)) throw new NullValueException("Coordinate x is null", new RuntimeException());
                break;
            } catch (NullValueException nve) {
                Console.printError("Coordinate x is null");
                if (fileMode) throw new NullValueException("Coordinate x is null", new RuntimeException());
            } catch (NoSuchElementException e) {
                Console.printError("Coordinate X not recognized");
                if (fileMode) throw new InvalidInputException("Coordinate X not recognized", new RuntimeException());
            } catch (NumberFormatException e) {
                Console.printError("Coordinate X must be a number");
                if (fileMode) throw new InvalidInputException("Coordinate X must be a number", new RuntimeException());
            } catch (NullPointerException | IllegalStateException e) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return x;
    }
    /**
     * This method keeps user input y in line with limitations given by the task.
     * @return float y
     * @throws InvalidInputException exception
     */
    public float askY() throws InvalidInputException {
        String strY;
        float y;
        while (true) {
            try {
                Console.println("Insert coordinate Y < " + MAX_Y + ":");
                Console.print(Main.CS2);
                strY = userScanner.nextLine().trim();
                if (fileMode) Console.println(strY);
                y = Float.parseFloat(strY);
                if (checkY(y)) throw new ValueExceededException("The amount of coordinate y must not exceed " + MAX_Y, new RuntimeException());
                break;
            } catch (NoSuchElementException nsee) {
                Console.printError("Coordinate Y not recognized");
                if (fileMode) throw new InvalidInputException("The amount of coordinate y must not exceed " + MAX_Y, new RuntimeException());
            } catch (ValueExceededException vee) {
                Console.printError("The amount of coordinate y must not exceed " + MAX_Y);
                if (fileMode) throw new InvalidInputException("The amount of coordinate y must not exceed " + MAX_Y, new RuntimeException());
            } catch (NumberFormatException nfe) {
                Console.printError("Coordinate Y must be a number");
                if (fileMode) throw new InvalidInputException("The amount of coordinate y must not exceed " + MAX_Y, new RuntimeException());
            } catch (NullPointerException | IllegalStateException exception) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return y;
    }
    /**
     * This method keeps user input zip code in line with limitations given by the task.
     * @return String zipCode
     * @throws ValueExceededException exception
     */
    public String askZipCode() throws ValueExceededException {
        String ZipCode;
        while (true) {
            try {
                Console.println("Insert zip code:");
                Console.print(Main.CS2);
                ZipCode = userScanner.nextLine().trim();
                if (fileMode) System.out.println(ZipCode);
                if (checkZipCode(ZipCode)) throw new ValueExceededException("Zip code must be not null and must exceed 9 by character length", new RuntimeException());
                break;
            } catch (ValueExceededException vee){
                Console.printError("Zip code must exceed 9 by character length");
            } catch (NoSuchElementException nsee) {
                Console.printError("Zip code not recognized");
            } catch (NullValueException nve) {
                Console.printError("Field 'zip code' must not be empty");
            } catch (IllegalStateException ise) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return ZipCode;
    }

    /**
     * This method initiates the validation of user input coordinates.
     * @return Coordinates
     * @throws InvalidInputException exception
     */
    public Coordinates askCoordinates() throws InvalidInputException {
        double x;
        float y;
        x = askX();
        y = askY();
        return new Coordinates(x, y);
    }

    /**
     * This method keeps user input annual turnover in line with limitations given by the task.
     * @return Int annual turnover
     * @throws InvalidInputException exception
     */
    public Double askAnnualTurnover() throws InvalidInputException {
        String strAnnualTurnover;
        double annualTurnover;
        while (true) {
            try {
                Console.println("Insert annual turnover:");
                Console.print(Main.CS2);
                strAnnualTurnover = userScanner.nextLine().trim();
                if (fileMode) Console.println(strAnnualTurnover);
                annualTurnover = Integer.parseInt(strAnnualTurnover);
                if (checkAnnualTurnover(annualTurnover)) throw new ValueExceededException("NEGATIVE INCOME?", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                Console.printError("Annual turnover not recognized");
                if (fileMode) throw new InvalidInputException("Annual turnover not recognized", new RuntimeException());
            } catch (NullValueException e) {
                Console.printError("Annual turnover must be at least something");
                if (fileMode) throw new InvalidInputException("Did you really insert a number?", new RuntimeException());
            } catch (NumberFormatException e) {
                Console.printError("Annual turnover must be a natural number");
                if (fileMode) throw new InvalidInputException("Annual turnover must be a natural number", new RuntimeException());
            } catch (NullPointerException | IllegalStateException e) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return annualTurnover;
    }

    /**
     * This method keeps user input organization type in line with limitations given by the task.
     * @return Type
     */
    public OrganizationType askOrganizationType(){
        String strOrgType;
        OrganizationType organizationType;
        while (true) {
            try {
                System.out.println("\033[1;34m" + "List of organization types - " + OrganizationType.nameList() + "\u001B[0m");
                Console.println("Insert organization type:");
                Console.print(Main.CS2);
                strOrgType = userScanner.nextLine().trim();
                if (fileMode) Console.println(strOrgType);
                organizationType = OrganizationType.valueOf(strOrgType.toUpperCase());
                if (checkOrgType(organizationType)) throw new InvalidInputException("An organization type must not be null and must be available.", new RuntimeException());
                break;
            } catch (InvalidInputException e) {
                Console.printError("An organization type must not be null and must be available.");
                if (fileMode) throw new InvalidInputException("An organization type must not be null and must be available.", new RuntimeException());
            } catch (InvalidTypeException exception) {
                Console.printError("Type not recognized.");
                if (fileMode) throw new InvalidInputException("Type not recognized.", new RuntimeException());
            } catch (IllegalArgumentException exception) {
                Console.printError("There's no such type.");
                if (fileMode) throw new InvalidInputException("There's no such type.", new RuntimeException());
            } catch (IllegalStateException exception) {
                Console.printError("Unknown error.");
                System.exit(0);
            }
        }
        return organizationType;
    }

    /**
     * This method keeps user input address in line with limitations given by the task.
     * @return String address name
     */
    public String askAddressName(){
        String strOfficialAddress;
        while (true) {
            try {
                Console.println("Insert address:");
                Console.print(Main.CS2);
                strOfficialAddress = userScanner.nextLine().trim();
                if (fileMode) Console.println(strOfficialAddress);
                break;
            } catch (NoSuchElementException exception) {
                Console.printError("Address not recognized");
                if (fileMode) throw new InvalidInputException("Address not recognized", new IllegalArgumentException());
            } catch (IllegalStateException exception) {
                Console.printError("Unknown error");
                System.exit(0);
            }

        }
        return strOfficialAddress;
    }

    /**
     * This method initiates the validation of user input address.
     * @return String Address
     * @throws InvalidInputException exception
     */
    public Address askAddress() throws InvalidInputException{
        String name;
        String ZipCode;
        name = askAddressName();
        ZipCode = askZipCode();
        return new Address(name, ZipCode);
    }
    /**
     * This method is invoked during a data update.
     * @param question final question
     * @return boolean
     * @throws InvalidInputException exception
     */
    public boolean askQuestion(String question) throws InvalidInputException {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                Console.println(finalQuestion);
                Console.print(Main.CS2);
                answer = userScanner.nextLine().trim();
                if (fileMode) Console.println(answer);
                if (!answer.equals("+") && !answer.equals("-")) throw new InvalidInputException("User input must be either '+' or '-'", new IllegalArgumentException());
                break;
            } catch (NoSuchElementException exception) {
                Console.printError("Answer not recognized");
                if (fileMode) throw new InvalidInputException("Answer not recognized", new IllegalArgumentException());
            } catch (InvalidInputException exception) {
                Console.printError("Answer must be either '+' or '-'");
                if (fileMode) throw new InvalidInputException("Answer must be either '+' or '-'", new IllegalArgumentException());
            } catch (IllegalStateException exception) {
                Console.printError("Unknown error");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }

    /**
     * This method checks the ID for required constraints
     * @param ID Integer
     * @return
     */
    protected static boolean checkID(Integer ID) {
        return ID == null || ID < 0;
    }
    protected static boolean checkUniqueID(Integer ID) {
        if(IDset.contains(ID)) {
            return true;
        } else {
            IDset.add(ID);
            return false;
        }
    }
    protected static boolean checkName(String name) {
        return name == null || name.isEmpty();
    }
    protected static boolean checkX(Double x) {
        return x == Float.MIN_VALUE;
    }
    protected static boolean checkY(float y) {
        return y > MAX_Y;
    }
    protected static boolean checkDate(Date creationDate) {
        return creationDate == null || creationDate.toString().equals("");
    }
    protected static boolean checkAnnualTurnover(Double annualTurnover) {
        return annualTurnover <= MIN_ANNUAL_TURNOVER;
    }
    protected static boolean checkOrgType(OrganizationType type) {
        return type == null || !(type.equals(OrganizationType.COMMERCIAL) || type.equals(OrganizationType.PUBLIC) || type.equals(OrganizationType.PRIVATE_LIMITED_COMPANY));
    }
    protected static boolean checkZipCode(String zipCode) {
        return zipCode == null || zipCode.length() < MIN_ZIP;
    }
    /**
     * OrganizationValidator implementation of general method toString()
     * @return String
     */
    @Override
    public String toString(){
        return "GroupAsker (utility class for user queries)";
    }
}
