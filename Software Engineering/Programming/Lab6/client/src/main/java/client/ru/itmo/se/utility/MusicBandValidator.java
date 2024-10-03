package client.ru.itmo.se.utility;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import client.ru.itmo.se.App;
import common.ru.itmo.se.data.Coordinates;
import common.ru.itmo.se.data.MusicGenre;
import common.ru.itmo.se.data.Studio;
import common.ru.itmo.se.utility.PrettyPrinter;
import common.ru.itmo.se.utility.FieldValidator;
import common.ru.itmo.se.exceptions.*;
import common.ru.itmo.se.exceptions.IllegalStateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Utility class used for validating all data and conforming them to necessary constraints.
 */
@Getter
@Setter
@ToString
public class MusicBandValidator {
    /**
     * This field holds the minimum limit value of coordinate X.
     */
    public final static float MIN_X = -584.0F;
    /**
     * This field holds the scanner via which the application will receive inputs.
     */
    private Scanner userScanner;
    /**
     * This field holds the value of whether the validator is working in file mode (in script) or not.
     */
    private boolean fileMode;
    /**
     * This field stores all possible date formats for the field EstablishmentDate.
     */
    private static final DateTimeFormatterBuilder formats = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ofPattern("[uuuu-MM-dd]" + "[uuuu/MM/dd]" + "[uuuu MM dd]" + "[uuuu.MM.dd]", Locale.ROOT));
    /**
     * This field converts the possible date formats into a formatter.
     */
    private static DateTimeFormatter dateTimeFormatter = formats.toFormatter();

    /**
     * Constructs a MusicBandValidator with the specified Scanner.
     *
     * @param userScanner a Scanner instance which takes input from the user.
     */
    public MusicBandValidator(Scanner userScanner) {
        this.userScanner = userScanner;
        fileMode = false;
    }

    /**
     * This method is used to retrieve a music band's name from the user.
     *
     * @return music band's name.
     */
    public String askName() {
        String name;
        while (true) {
            try {
                PrettyPrinter.println("Enter name:");
                PrettyPrinter.print(App.CS2);
                name = userScanner.nextLine().trim();
                if (fileMode) PrettyPrinter.println(name);
                if (FieldValidator.checkName(name)) throw new NullValueException("Name cannot be null", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Name not found.");
            } catch (NullValueException e) {
                PrettyPrinter.printError("Name must not be empty.");
            } catch (IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return name;
    }

    /**
     * This method is used to retrieve a music band's coordinate (X) from the user.
     *
     * @return abscissa value.
     */
    private float askX() {
        String strX;
        float x;
        while (true) {
            try {
                PrettyPrinter.println("Enter coordinate X:");
                PrettyPrinter.print(App.CS2);
                strX = userScanner.nextLine().trim();
                if (fileMode) PrettyPrinter.println(strX);
                x = Float.parseFloat(strX);
                if (x < MIN_X)
                    throw new ValueRangeException("Your input is less than our allowed value.", new RuntimeException());
                if (FieldValidator.checkX(x))
                    throw new InvalidInputException("Your input has not passed validation.", new RuntimeException());
                break;
            } catch (InvalidInputException e) {
                PrettyPrinter.printError("Coordinate X is null.");
                if (fileMode) throw new NullValueException("Coordinate X is null.", new RuntimeException());
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Coordinate X not recognized.");
                if (fileMode) throw new InvalidInputException("Coordinate X not recognized.", new RuntimeException());
            } catch (ValueRangeException e) {
                PrettyPrinter.printError("Coordinate X cannot be less than -584. Please try again.");
                if (fileMode)
                    throw new InvalidInputException("Coordinate X cannot be less than -584. Please try again.", new RuntimeException());
            } catch (NullPointerException | IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * This method is used to retrieve a music band's coordinate (Y) from the user.
     *
     * @return ordinate value.
     */
    private float askY() {
        String strY;
        float y;
        while (true) {
            try {
                PrettyPrinter.println("Enter coordinate Y:");
                PrettyPrinter.print(App.CS2);
                strY = userScanner.nextLine().trim();
                if (fileMode) PrettyPrinter.println(strY);
                y = Float.parseFloat(strY);
                if (FieldValidator.checkY(y))
                    throw new ValueRangeException("The value of coordinate Y must be within float range.", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Coordinate Y not recognized");
                if (fileMode)
                    throw new InvalidInputException("The value of coordinate Y must be within float range.", new RuntimeException());
            } catch (NumberFormatException e) {
                PrettyPrinter.printError("Coordinate Y must be a number");
                if (fileMode)
                    throw new InvalidInputException("The amount of coordinate Y must be within float range.", new RuntimeException());
            } catch (NullPointerException | IllegalStateException exception) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return y;
    }

    /**
     * This method is used to retrieve a music band's coordinates from the user.
     *
     * @return coordinates set.
     */
    public Coordinates askCoordinates() {
        return new Coordinates(askX(), askY());
    }

    /**
     * This method is used to retrieve a music band's number of participants from the user.
     *
     * @return number of band members.
     */
    public Long askNumberOfParticipants() {
        String strNumberOfParticipants;
        Long numberOfParticipants;
        while (true) {
            try {
                PrettyPrinter.println("Enter the number of participants:");
                PrettyPrinter.print(App.CS2);
                strNumberOfParticipants = userScanner.nextLine().trim();
                if (strNumberOfParticipants.isEmpty()) return null;
                if (fileMode) PrettyPrinter.println(strNumberOfParticipants);
                numberOfParticipants = Long.parseLong(strNumberOfParticipants);
                if (FieldValidator.checkNumberOfParticipants(numberOfParticipants)) {
                    throw new NullValueException("Your input has not passed validation.", new RuntimeException());
                }
                break;
            } catch (NullValueException e) {
                PrettyPrinter.printError("Your input has not passed validation.");
                if (fileMode) throw new NullValueException("Your input has not passed validation.", new RuntimeException());
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Number of participants is not recognized");
                if (fileMode)
                    throw new InvalidInputException("Number of participants is not recognized", new RuntimeException());
            } catch (NumberFormatException e) {
                PrettyPrinter.printError("Number of participants must be a 'Long' number");
                if (fileMode)
                    throw new InvalidInputException("Number of participants must be a 'Long' number", new RuntimeException());
            } catch (NullPointerException | IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return numberOfParticipants;
    }

    /**
     * This method is used to retrieve a music band's establishment date from the user.
     * @return the music band's establishment date.
     */
    public LocalDateTime askEstablishmentDate() {
        String strEstablishmentDate;
        LocalDateTime establishmentDate;
        while (true) {
            try {
                PrettyPrinter.println("Enter establishment date with the following format: yyyy-MM-dd, yyyy.MM.dd, yyyy/MM/dd, yyyy MM dd:");
                PrettyPrinter.print(App.CS2);
                strEstablishmentDate = userScanner.nextLine().trim();
                establishmentDate = LocalDate.parse(strEstablishmentDate, dateTimeFormatter).atStartOfDay();
                if (fileMode) PrettyPrinter.println(strEstablishmentDate);
                break;
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Date not recognized.");
            } catch (DateTimeParseException e) {
                PrettyPrinter.printError("Your input cannot be parsed. Please try again.");
            } catch (IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return establishmentDate;
    }

    /**
     * This method is used to retrieve a music band's genre from the user.
     * @return music band's genre as an enum value.
     */
    public MusicGenre askMusicGenre() {
        String strMusicGenre;
        MusicGenre musicGenre;
        while (true) {
            try {
                System.out.println("\033[1;34m" + "List of music genres:\n" + MusicGenre.nameList() + "\u001B[0m");
                PrettyPrinter.println("Enter music genre:");
                PrettyPrinter.print(App.CS2);
                strMusicGenre = userScanner.nextLine().trim();
                if (strMusicGenre.isEmpty()) return null;
                if (fileMode) PrettyPrinter.println(strMusicGenre);
                musicGenre = MusicGenre.valueOf(strMusicGenre.toUpperCase(Locale.ROOT));
                if (FieldValidator.checkMusicGenre(musicGenre))
                    throw new InvalidInputException("A music genre can be blank, but should be from available genres.", new RuntimeException());
                break;
            } catch (InvalidInputException e) {
                PrettyPrinter.printError("A music genre can be blank, but should be from available genres.");
                if (fileMode)
                    throw new InvalidInputException("A music genre can be blank, but should be from available genres.", new RuntimeException());
            } catch (InvalidTypeException e) {
                PrettyPrinter.printError("Genre not recognized.");
                if (fileMode) throw new InvalidInputException("Genre not recognized.", new RuntimeException());
            } catch (IllegalArgumentException e) {
                PrettyPrinter.printError("There's no such type.");
                if (fileMode) throw new InvalidInputException("There's no such type.", new RuntimeException());
            } catch (IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return musicGenre;
    }

    /**
     * This method is used to retrieve a music band's studio address from the user.
     * @return music band's studio address.
     */
    private String askAddress() {
        String address;
        while (true) {
            try {
                PrettyPrinter.println("Enter address:");
                PrettyPrinter.print(App.CS2);
                address = userScanner.nextLine().trim();
                if (fileMode) PrettyPrinter.println(address);
                if (FieldValidator.checkAddress(address))
                    throw new InvalidInputException("Your input has not passed validation.", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Address not recognized.");
                if (fileMode) throw new InvalidInputException("Address not recognized.", new RuntimeException());
            } catch (InvalidInputException e) {
                PrettyPrinter.printError("Studio address cannot be null.");
            } catch (IllegalStateException e) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return address;
    }

    /**
     * This method is used to retrieve a music band's studio from the user.
     * @return music band's studio.
     */
    public Studio askStudio() {
        return new Studio(askAddress());
    }

    /**
     * This method is used to update a music band's data. Every question is optional.
     *
     * @param question whether the user wants to update a field.
     * @return true if yes ("+"), <p> false if not ("-").
     */
    public boolean askQuestion(String question) {
        String finalQuestion = question + " (+/-)";
        String answer;
        while (true) {
            try {
                PrettyPrinter.println(finalQuestion);
                PrettyPrinter.print(App.CS2);
                answer = userScanner.nextLine().trim();
                if (fileMode) PrettyPrinter.println(answer);
                if (!answer.equals("+") && !answer.equals("-"))
                    throw new InvalidInputException("User input must be either '+' or '-'", new RuntimeException());
                break;
            } catch (NoSuchElementException e) {
                PrettyPrinter.printError("Answer not recognized.");
                if (fileMode) throw new InvalidInputException("Answer not recognized.", new RuntimeException());
            } catch (InvalidInputException exception) {
                PrettyPrinter.printError("Answer must be either '+' or '-'.");
                if (fileMode)
                    throw new InvalidInputException("Answer must be either '+' or '-'.", new RuntimeException());
            } catch (IllegalStateException exception) {
                PrettyPrinter.printError("Unknown error. Stopping the session...");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }
}
