package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.EmptyCollectionException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command filter_less_than_number_of_participants (handful, I know). It outputs all the elements with a fewer number of participants than given.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the filter_less_than_number_of_participants class.
 */
@ToString
public class FilterLessThanNumberOfParticipants extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructs a filter_less_than_number_of_participants with the specified CollectionManager.
     *
     * @param collectionManager the specified CollectionManager.
     */
    public FilterLessThanNumberOfParticipants(CollectionManager collectionManager) {
        super("filter_less_than_number_of_participants", "<number_of_participants>", "Outputs elements which have less participants than given", CommandType.WITH_ARGS);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the filter_less_than_number_of_participants command.
     * @param commandStrArg the command's string argument (necessary).
     * @param commandObjArg the command's object argument (unnecessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You need an argument here.", new RuntimeException());
            }
            if (collectionManager.collectionSize() == 0) {
                throw new EmptyCollectionException("Empty collection.", new RuntimeException());
            }
            long numberOfParticipants = Long.parseLong(commandStrArg);
            if (numberOfParticipants <= 0L) {
                throw new IllegalArgumentException("Why are you expecting a negative number of participants?", new RuntimeException());
            }
            String filteredInfo = collectionManager.musicBandParticipantsFilteredInfo(numberOfParticipants);
            if (filteredInfo.isEmpty()) {
                ResponseAppender.appendln("No music bands with less than " + numberOfParticipants + " participants has been found.");
            } else {
                ResponseAppender.appendln(filteredInfo);
                return true;
            }
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        } catch (EmptyCollectionException e) {
            ResponseAppender.appendError("Empty collection.");
        } catch (IllegalArgumentException e) {
            ResponseAppender.appendError("Why are you expecting a negative number of participants?");
        }
        return false;
    }
}
