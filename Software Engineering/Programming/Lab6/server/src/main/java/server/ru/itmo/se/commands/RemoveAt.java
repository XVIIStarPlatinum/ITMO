package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.data.MusicBand;
import common.ru.itmo.se.exceptions.EmptyCollectionException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import common.ru.itmo.se.exceptions.InvalidInputException;
import common.ru.itmo.se.exceptions.NullMusicBandException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command remove_at. It removes an element from the collection through its index.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the add class.
 */
@ToString
public class RemoveAt extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructs a remove_at with the specified CollectionManager.
     *
     * @param collectionManager the specified CollectionManager.
     */
    public RemoveAt(CollectionManager collectionManager) {
        super("remove_at", "<index>", "removes an element by its index in the collection", CommandType.WITH_ARGS);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the remove_at command.
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
            MusicBand musicBandToRemove = collectionManager.getByIndex(Integer.parseInt(commandStrArg));
            collectionManager.removeFromCollection(musicBandToRemove);
            ResponseAppender.appendln("Music band successfully removed.");
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException e) {
            ResponseAppender.appendError("Empty collection.");
        } catch (NullMusicBandException e) {
            ResponseAppender.appendError("No music band with given index.");
        } catch (InvalidInputException e) {
            ResponseAppender.appendError("What am I supposed to remove? Please enter an index.");
        }
        return false;
    }
}
