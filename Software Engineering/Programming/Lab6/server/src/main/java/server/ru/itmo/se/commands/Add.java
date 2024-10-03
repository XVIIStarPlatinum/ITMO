package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import common.ru.itmo.se.interaction.MusicBandRaw;
import lombok.ToString;
import common.ru.itmo.se.data.MusicBand;
import common.ru.itmo.se.exceptions.IncorrectScriptException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

import java.time.Instant;
import java.util.Date;

/**
 * This class implements the command add. It adds a new element to the collection.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the add class.
 */
@ToString
public class Add extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;

    /**
     * Constructs an add with the specified CollectionManager and FileManager.
     *
     * @param collectionManager  the specified CollectionManager.
     */
    public Add(CollectionManager collectionManager) {
        super("add", "{element}", "Adds a new element to the collection", CommandType.WITH_FORM);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the add command.
     * @param commandStrArg the command's string argument (unnecessary).
     * @param commandObjArg the command's object argument (necessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (!commandStrArg.isEmpty() || commandObjArg == null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            MusicBandRaw musicBandRaw = (MusicBandRaw) commandObjArg;
            collectionManager.addToCollection(new MusicBand(
                    collectionManager.generateNextID(),
                    musicBandRaw.getName(),
                    musicBandRaw.getCoordinates(),
                    Date.from(Instant.now()),
                    musicBandRaw.getNumberOfParticipants(),
                    musicBandRaw.getEstablishmentDate(),
                    musicBandRaw.getMusicGenre(),
                    musicBandRaw.getStudio()
            ));
            ResponseAppender.appendln("\u001b[3m" + "\"A fine addition to my collection.\" — General Grievous" + "\u001b[0m");
            collectionManager.saveCollection();
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        } catch (IncorrectScriptException e) {
            ResponseAppender.appendError("Execution error: Please debug your script.");
        }
        return false;
    }
}