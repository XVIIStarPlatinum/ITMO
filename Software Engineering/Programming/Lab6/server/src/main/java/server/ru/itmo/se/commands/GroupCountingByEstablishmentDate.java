package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.EmptyCollectionException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command group_counting_by_establishment_date (also a handful). It groups all music bands by establishment date and outputs the number of occurrences for each date.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the group_counting_by_establishment_date class.
 */
@ToString
public class GroupCountingByEstablishmentDate extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructs a group_counting_by_establishment_date with the specified CollectionManager.
     *
     * @param collectionManager the specified CollectionManager.
     */
    public GroupCountingByEstablishmentDate(CollectionManager collectionManager) {
        super("group_counting_by_establishment_dates", "", "Groups elements by establishment date and outputs instances for each amount", CommandType.WITHOUT_ARGS);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the group_counting_by_establishment_date command.
     * @param commandStrArg the command's string argument (unnecessary).
     * @param commandObjArg the command's object argument (unnecessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (!commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            if (collectionManager.collectionSize() == 0) {
                throw new EmptyCollectionException("Empty collection.", new RuntimeException());
            }
            collectionManager.groupCountingByEstablishmentDate();
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: " + getName() + " " + getUsage() + "'");
        } catch (EmptyCollectionException e) {
            ResponseAppender.appendError("Empty collection.");
        }
        return false;
    }
}
