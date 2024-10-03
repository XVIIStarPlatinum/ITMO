package data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import Network.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
/**
 * This class represents a serializable version of the primary composite data type which describes a music band. Instances of this class don't have IDs and creation dates because they are not validatable.
 * -- CONSTRUCTOR --
 * Constructs a MusicBand with the specified fields.
 */
@Getter
@AllArgsConstructor
public class MusicBandRaw implements Serializable {
    /**
     * This field holds the value of the name of a music band.
     * -- GETTER --
     * Getter method for the name of the music band.
     */
    private String name;
    /**
     * This field holds the value of the coordinates of a music band which in turn holds two values: X and Y.
     * -- GETTER --
     * Getter method for the coordinates of the music band.
     */
    private Coordinates coordinates;
    /**
     * This field holds the value of the number of participants in the music band.
     * -- GETTER --
     * Getter method for the number of participants in the music band.
     */
    private Long numberOfParticipants;
    /**
     * This field holds the value of the date of establishment of the band.
     * -- GETTER --
     * Getter method for the establishment date of the music band.
     */
    private LocalDateTime establishmentDate;
    /**
     * This field holds the value of the genre of the music band. It is represented by 5 enum values.
     * -- GETTER --
     * Getter method for the genre of the music band.
     */
    private MusicGenre musicGenre;
    /**
     * This field holds the value of the studio of the music band which in turn holds 1 value: address.
     * -- GETTER --
     * Getter method for the studio of the music band.
     */
    private Studio studio;

    private User user;
    /**
     * This method is a custom implementation of the toString() method in MusicBand.
     * @return values of the music band parsed to String data type.
     */
    @Override
    public String toString() {
        return "MusicBand in raw form:" +
                "\n   Name: " + name +
                "\n   Coordinates: " + coordinates +
                "\n   Number of participants: " + numberOfParticipants +
                "\n   Establishment date: " + establishmentDate +
                "\n   Musical genre: " + musicGenre +
                "\n   Studio: " + studio;
    }
    /**
     * A custom implementation of the hashCode() method in MusicBand.
     * @return hash code of a MusicBand instance.
     */
    @Override
    public int hashCode() {
        return name.hashCode() +
               coordinates.hashCode() +
               numberOfParticipants.hashCode() +
               establishmentDate.hashCode() +
               musicGenre.hashCode() +
               studio.hashCode();
    }
    /**
     * A custom implementation of the equals(Object obj) method in Coordinates. It works by comparing every field.
     * @param obj the object to be compared.
     * @return boolean value of whether the two music bands were equal as defined in this method.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MusicBand musicBand) {
            return name.equals(musicBand.getName()) &&
                    coordinates.equals(musicBand.getCoordinates()) &&
                    Objects.equals(numberOfParticipants, musicBand.getNumberOfParticipants()) &&
                    establishmentDate.isEqual(musicBand.getEstablishmentDate()) &&
                    musicGenre.equals(musicBand.getMusicGenre()) &&
                    studio.equals(musicBand.getStudio());
        }
        return false;
    }
}
