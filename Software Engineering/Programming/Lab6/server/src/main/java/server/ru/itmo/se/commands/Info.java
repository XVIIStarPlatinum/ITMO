package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

import java.time.LocalDateTime;

/**
 * This class implements the command Info. It outputs all necessary information about the CLI application, for example the collection's type and size, last session time and last Save time.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the Info class.
 */
@ToString
public class Info extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructs an Info with the specified CollectionManager.
     *
     * @param collectionManager the specified collectionManager.
     */
    public Info(CollectionManager collectionManager) {
        super("info", "", "Gives information about the collection", CommandType.WITHOUT_ARGS);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the Info command.
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
            LocalDateTime lastInitTime = collectionManager.getLastInitTime();
            String strLastInitTime = (lastInitTime == null) ? "Initialization has not happened yet." : lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();
            LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
            String strLastSaveTime = (lastSaveTime == null) ? "You haven't saved yet during this session." : lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();
            ResponseAppender.appendln("Information about this collection:");
            ResponseAppender.appendln("Collection type: " + collectionManager.getCollectionType());
            ResponseAppender.appendln("Number of elements: " + collectionManager.collectionSize());
            ResponseAppender.appendln("Last saved: " + strLastSaveTime);
            ResponseAppender.appendln("Last session: " + strLastInitTime);
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: " + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}
