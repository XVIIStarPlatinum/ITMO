package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.data.*;
import common.ru.itmo.se.interaction.MusicBandRaw;
import common.ru.itmo.se.exceptions.EmptyCollectionException;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import common.ru.itmo.se.exceptions.NullMusicBandException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * This class implements the command update. It updates an element by removing it through its ID and creating a new one from the extracted data.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the update class.
 */
@ToString
public class UpdateID extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructs an update with the specified CollectionManager and FileManager.
     *
     * @param collectionManager  the specified CollectionManager.
     */
    public UpdateID(CollectionManager collectionManager) {
        super("update", "<ID> {element}", "Updates an element of the collection with the given ID", CommandType.WITH_ARGS_FORM);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the update command.
     * @param commandStrArg the command's string argument (necessary).
     * @param commandObjArg the command's object argument (necessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (commandStrArg.isEmpty() || commandObjArg == null) {
                throw new InvalidArgumentCountException("You need an argument here.", new RuntimeException());
            }
            if (collectionManager.collectionSize() == 0) {
                throw new EmptyCollectionException("Empty collection.", new RuntimeException());
            }
            Integer id = Integer.parseInt(commandStrArg);
            MusicBand musicBandToUpdate = collectionManager.getByID(id);
            if (collectionManager.collectionContains(musicBandToUpdate)) {
                MusicBandRaw musicBandRaw = (MusicBandRaw) commandObjArg;
                String name = musicBandRaw.getName() == null ? musicBandToUpdate.getName() : musicBandRaw.getName();
                Coordinates coordinates = musicBandRaw.getCoordinates() == null ? musicBandToUpdate.getCoordinates() : musicBandRaw.getCoordinates();
                Date creationDate = musicBandToUpdate.getCreationDate();
                Long numberOfParticipants = musicBandRaw.getNumberOfParticipants() == null ? musicBandToUpdate.getNumberOfParticipants() : musicBandRaw.getNumberOfParticipants() ;
                LocalDateTime establishmentDate = musicBandRaw.getEstablishmentDate() == null ? musicBandToUpdate.getEstablishmentDate() : musicBandRaw.getEstablishmentDate();
                MusicGenre musicGenre = musicBandRaw.getMusicGenre() == null ? musicBandToUpdate.getMusicGenre() : musicBandRaw.getMusicGenre();
                Studio studio = musicBandRaw.getStudio() == null ? musicBandToUpdate.getStudio() : musicBandRaw.getStudio();
                collectionManager.removeFromCollection(musicBandToUpdate);
                collectionManager.addToCollection(new MusicBand(
                        id, name,
                        coordinates,
                        creationDate,
                        numberOfParticipants,
                        establishmentDate,
                        musicGenre, studio
                ));
                collectionManager.saveCollection();
            } else {
                throw new NullMusicBandException("There's no such music band.", new RuntimeException());
            }
            ResponseAppender.appendln("Music band has been successfully updated.");
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + "'");
        } catch (EmptyCollectionException e) {
            ResponseAppender.appendError("Empty collection.");
        } catch (NumberFormatException e) {
            ResponseAppender.appendError("Negative number of participants???");
        } catch (NullMusicBandException e) {
            ResponseAppender.appendError("No such music band with given ID.");
        } catch (ClassCastException e) {
            ResponseAppender.appendError("Invalid object type from the client.");
        }
        return false;
    }
}
