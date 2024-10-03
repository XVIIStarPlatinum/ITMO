package data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * This class represents the primary composite data type which describes a music band.
 * -- CONSTRUCTOR --
 * Constructs a MusicBand with the specified fields.
 */
@Getter
@AllArgsConstructor
public class MusicBand implements Comparable<MusicBand>, Serializable {
    /**
     * This field holds the value for SerialVersion, which is a good practice when you're trying to serialize an object.
     */
    @Serial
    private static final long SerialVersionUID = 1;
    /**
     * This field holds the value of an ID via which a user can identify an element of the collection.
     * -- GETTER --
     * Getter method for ID.
     */
    private Integer id;
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
     * This field holds the value of the date of the bands addition into the collection of a music band. It is auto-generated.
     * -- GETTER --
     * Getter method for the coordinates of the music band.
     */
    private Date creationDate;
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
    /**
     * 
     */
    private String owner;
    /**
     * This method is a custom implementation of a compareTo method from the interface Comparable. It compares the music bands' ID values.
     *
     * @param musicBand the music band to be compared.
     * @return 1 if the ID is greater than getId(),<p> 0 if the ID is equal to getId() and <p>-1 if the ID is lesser than getId().
     */
    public int compareTo(MusicBand musicBand) {
        return id.compareTo(musicBand.getId());
    }

    /**
     * This method is a custom implementation of a comparator. It compares the music bands' establishment dates.
     * @param musicBand the music band to be compared.
     * @return 1 if establishmentDate is later than getEstablishmentDate(),<p>0 if establishmentDate is equal to getEstablishmentDate and <p>-1 if EstablishmentDate is before getEstablishmentDate().
     */
    public int compareToEstablishmentDate(MusicBand musicBand) {
        if (establishmentDate.isAfter(musicBand.getEstablishmentDate())) return 1;
        if (establishmentDate.isEqual(musicBand.getEstablishmentDate())) return 0;
        else return -1;
    }

    /**
     * This method is a custom implementation of the toString() method in MusicBand.
     * @return values of the music band parsed to String data type.
     */
    @Override
    public String toString() {
        SimpleDateFormat creationDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat creationTimeFormat = new SimpleDateFormat("HH:mm:ss");
        String output = "";
        output += "MusicBand №" + id;
        output += "\n(created by " + owner + " at " + creationDateFormat.format(creationDate) + " " + creationTimeFormat.format(creationDate) + ")";
        output += "\n   Name: " + name;
        output += "\n   Coordinates: " + coordinates;
        output += "\n   Number of participants: " + numberOfParticipants;
        output += "\n   Establishment date: " + establishmentDate;
        output += "\n   Musical genre: " + musicGenre;
        output += "\n   Studio: " + studio;
        return output;
    }

    /**
     * A custom implementation of the hashCode() method in MusicBand.
     * @return hash code of a MusicBand instance.
     */
    @Override
    public int hashCode() {
        return id.hashCode() +
                name.hashCode() -
                coordinates.hashCode() +
                creationDate.hashCode() -
                numberOfParticipants.hashCode() +
                establishmentDate.hashCode() -
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
                    creationDate.equals(musicBand.getCreationDate()) &&
                    Objects.equals(numberOfParticipants, musicBand.getNumberOfParticipants()) &&
                    establishmentDate.isEqual(musicBand.getEstablishmentDate()) &&
                    musicGenre.equals(musicBand.getMusicGenre()) &&
                    studio.equals(musicBand.getStudio());
        }
        return false;
    }
}
