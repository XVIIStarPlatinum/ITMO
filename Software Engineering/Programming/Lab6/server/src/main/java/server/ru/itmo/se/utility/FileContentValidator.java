package server.ru.itmo.se.utility;

import server.ru.itmo.se.App;
import common.ru.itmo.se.data.Coordinates;
import common.ru.itmo.se.data.MusicBand;
import common.ru.itmo.se.data.MusicGenre;
import common.ru.itmo.se.utility.FieldValidator;
import common.ru.itmo.se.data.Studio;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;

/**
 * Utility class used for validating the content of the file in case of an external change.
 */
class FileContentValidator {
    /**
     * This field holds an instance of a FileManager which is responsible for operations with files.
     */
    private FileManager fileManager = new FileManager(App.cliArgument);
    /**
     * This field holds a message to be displayed in case something went wrong.
     */
    private static final String message1 = "It seems like the data was externally altered.";
    /**
     * This field holds a message to be displayed if th element is being removed.
     */
    private static final String message2 = "Therefore, this element is being removed.";
    /**
     * This method is used to validate the content of the file. If a violation was found, the corresponding object will be removed.
     * @return validated collection.
     */
    LinkedList<MusicBand> validateFileContent() {
        LinkedList<MusicBand> musicBandLinkedList = fileManager.readCollection();
        for (MusicBand musicBand : musicBandLinkedList) {
            Integer id = musicBand.getId();
            String name = musicBand.getName();
            Coordinates coordinates = musicBand.getCoordinates();
            Date creationDate = musicBand.getCreationDate();
            Long numberOfParticipants = musicBand.getNumberOfParticipants();
            LocalDateTime establishmentDate = musicBand.getEstablishmentDate();
            MusicGenre musicGenre = musicBand.getMusicGenre();
            Studio studio = musicBand.getStudio();
            if (FieldValidator.checkID(id)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (ID: " + id + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkName(name)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Name: " + name + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkX(coordinates.getX())) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Coordinates (X): " + coordinates.getX() + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkY(coordinates.getY())) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Coordinates (Y): " + coordinates.getY() + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkDate(creationDate)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Creation date: " + creationDate + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkNumberOfParticipants(numberOfParticipants)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Number of participants: " + numberOfParticipants + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkEstablishmentDate(establishmentDate)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Establishment date: " + establishmentDate + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkMusicGenre(musicGenre)) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Music genre: " + musicGenre + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
            if (FieldValidator.checkAddress(studio.toString())) {
                ResponseAppender.appendError(message1);
                ResponseAppender.appendError("This field (Studio address: " + studio + ") has violated the necessary constraints.");
                ResponseAppender.appendError(message2);
                musicBandLinkedList.remove(musicBand);
            }
        }
        return musicBandLinkedList;
    }
}
