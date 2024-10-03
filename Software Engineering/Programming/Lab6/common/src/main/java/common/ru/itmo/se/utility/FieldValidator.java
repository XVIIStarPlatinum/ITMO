package common.ru.itmo.se.utility;

import common.ru.itmo.se.data.MusicGenre;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class which is responsible for objects' correctness.
 */
public class FieldValidator {
    /**
     * This field stores the minimum allowed value for the abscissa (Coordinates.X) field.
     */
    static final float MIN_X = -584.0F;
    /**
     * This field stores all the values of ID's. Set is used here to ensure the IDs' uniqueness constraint.
     */
    private static final Set<Integer> IDset = new TreeSet<>();
    /**
     * This method is used to check whether an ID conforms to the required constraints.
     *
     * @param ID the ID to be checked.
     * @return true if the ID doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkID(Integer ID) {
        return ID == null || ID <= 0 || checkUniqueID(ID);
    }

    /**
     * This method is used to check whether an ID is unique or not.
     * @param ID the ID to be checked.
     * @return true if the ID is not unique, <p>false if it is.
     */
    private static boolean checkUniqueID(Integer ID) {
        boolean unique;
        if (IDset.contains(ID)) {
            unique = true;
        } else {
            IDset.add(ID);
            unique = false;
        }
        return unique;
    }

    /**
     * This method is used to check whether a name conforms to the required constraints.
     * @param name the ID to be checked.
     * @return true if the name doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkName(CharSequence name) {
        return name == null || name.isEmpty();
    }

    /**
     * This method is used to check whether an X value conforms to the required constraints.
     * @param x the abscissa value to be checked.
     * @return true if the abscissa doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkX(float x) {
        return x < MIN_X || x > Float.MAX_VALUE;
    }

    /**
     * This method is used to check whether a Y value conforms to the required constraints.
     * @param y the ordinate value to be checked.
     * @return true if the ordinate doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkY(float y) {
        return Math.abs(y) > Float.MAX_VALUE;
    }

    /**
     * This method is used to check whether a creation date conforms to the required constraints.
     * @param creationDate the creation date to be checked.
     * @return true if the creation date doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkDate(Date creationDate) {
        return creationDate == null || creationDate.toString().isEmpty();
    }

    /**
     * This method is used to check whether a number of participants value conforms to the required constraints.
     *
     * @param numberOfParticipants the creation date to be checked.
     * @return true if the number of participants doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkNumberOfParticipants(Long numberOfParticipants) {
        if (numberOfParticipants == null) return false;
        return numberOfParticipants <= 0L;
    }

    /**
     * This method is used to check whether an establishment date conforms to the required constraints.
     * @param establishmentDate the establishment date to be checked.
     * @return true if the establishment date doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkEstablishmentDate(LocalDateTime establishmentDate) {
        if(establishmentDate == null) return false;
        return LocalDateTime.parse(establishmentDate.toString()).toString().isEmpty();
    }

    /**
     * This method is used to check whether a music band's genre conforms to the required constraints.
     * @param genre the genre value to be checked.
     * @return true if the genre doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkMusicGenre(MusicGenre genre) {
        if(genre == null) return false;
        for (MusicGenre musicGenre : MusicGenre.values()) {
            if (genre.name().equalsIgnoreCase(musicGenre.name())) return false;
        }
        return true;
    }

    /**
     * This method is used to check whether a music band's studio address conforms to the required constraints.
     * @param address the address to be checked.
     * @return true if the address doesn't meet the requirements, <p>false if it does.
     */
    public static boolean checkAddress(CharSequence address) {
        return address == null || address.isEmpty();
    }
}
