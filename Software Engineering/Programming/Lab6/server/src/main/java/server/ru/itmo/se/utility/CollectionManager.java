package server.ru.itmo.se.utility;

import lombok.Getter;
import common.ru.itmo.se.data.MusicBand;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class used for operations with a double-linked list and its management.
 */
@Getter
public class CollectionManager {
    /**
     * This field holds the value of the latest session's date and time.
     * -- GETTER --
     * Getter method for the last initialization time of the console application.
     */
    private LocalDateTime lastInitTime;
    /**
     * This field holds the collection which is the main focus of this program. It stores all MusicBand instances.
     * -- GETTER --
     * Getter method for the collection.
     */
    private LinkedList<MusicBand> musicBandCollection = new LinkedList<>();
    /**
     * This field holds the value of the session's last Save date and time.
     * -- GETTER --
     * Getter method for the last Save time of the console application.
     */
    private LocalDateTime lastSaveTime;
    /**
     * This field holds an instance of a FileManager which is responsible for operations with files.
     * -- GETTER --
     * Getter method for the FileManager instance.
     */
    private final FileManager fileManager;

    /**
     * Constructs a CollectionManager with the specified file manager.
     * @param fileManager FileManager instance.
     */
    public CollectionManager(FileManager fileManager) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.fileManager = fileManager;
        loadCollection();
    }

    /**
     * This method is technically a getter for the collection's size.
     * @return linked list's size.
     */
    public int collectionSize() {
        return musicBandCollection.size();
    }

    /**
     * This method is used to get the collection's type (spoiler: it's LinkedList).
     * @return collection's type.
     */
    public String getCollectionType() {
        return musicBandCollection.getClass().getName();
    }

    /**
     * This method is used to return the collection's first element.
     *
     * @return first element of the collection.
     */
    @Deprecated
    public MusicBand getFirst() {
        return musicBandCollection.getFirst();
    }

    /**
     * This method is used to return the collection's last element.
     *
     * @return last element of the collection.
     */
    @Deprecated
    public MusicBand getLast() {
        return musicBandCollection.getLast();
    }

    /**
     * This method is used to check whether a MusicBand instance belongs to the collection.
     * @param musicBand the music band to be checked.
     * @return true if the list contains the element, <p>false if it doesn't.
     */
    public boolean collectionContains(MusicBand musicBand) {
        return musicBandCollection.contains(musicBand);
    }

    /**
     * This method is used to sort the collection (by the music bands' ID value).
     * @param musicBands the collection to be sorted. Java usually uses Merge sort for this problem.
     */
    private static void sortCollection(List<MusicBand> musicBands) {
        musicBands.sort(MusicBand::compareTo);
    }

    /**
     * This method prints all the collection's unique dates and the number of their occurrences.
     */
    public void groupCountingByEstablishmentDate() {
        ResponseAppender.appendln("Counting the collection's establishment dates grouped by occurrences:");
        Map<LocalDateTime, Long> establishmentDateMap = musicBandCollection.stream().collect(Collectors.groupingBy(MusicBand::getEstablishmentDate, Collectors.counting()));
        establishmentDateMap.forEach((localDateTime, aLong) -> ResponseAppender.appendln(localDateTime + ": " + aLong));
    }

    /**
     * This method is used to access a collection's element by its ID value.
     * @param id the value via which the element is going to be accessed.
     * @return the music band that has the ID, <p>null if there isn't any music band with this ID.
     */
    public MusicBand getByID(Integer id) {
        return musicBandCollection.stream().filter(musicBand -> musicBand.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * This method is used to access a collection's element by its index.
     * @param index the value via which the element is going to be accessed.
     * @return the music band that corresponds to this index, <p>null if the index is out of bounds.
     */
    public MusicBand getByIndex(Integer index) {
        return musicBandCollection.get(index);
    }

    /**
     * This method prints every element's establishment date by descending order.
     */
    public void printFieldDescendingEstablishmentDate() {
        Collection<MusicBand> copy = new TreeSet<>(Collections.reverseOrder(MusicBand::compareToEstablishmentDate));
        ArrayList<LocalDateTime> arrayList = new ArrayList<>();
        copy.addAll(musicBandCollection);
        copy.forEach(musicBand -> arrayList.add(musicBand.getEstablishmentDate()));
        ResponseAppender.appendln(arrayList.toString().trim());
    }

    /**
     * This method filters the collection for elements that have fewer participants than given.
     * @param numberOfParticipants the number of participants to be compared to.
     * @return elements with fewer participants than given as String.
     */
    public String musicBandParticipantsFilteredInfo(Long numberOfParticipants) {
        return musicBandCollection.stream().filter(musicBand -> musicBand.getNumberOfParticipants().equals(numberOfParticipants))
                .reduce("", (sum, p) -> sum + (p + "\n\n"), (sum1, sum2) -> sum1 + sum2).trim();
    }

    /**
     * This method is used to add a new element to the collection.
     * @param musicBand the music band to be added.
     */
    public void addToCollection(MusicBand musicBand) {
        musicBandCollection.add(musicBand);
    }

    /**
     * This method is used to remove an element from the collection.
     * @param musicBand the music band to be removed.
     */
    public void removeFromCollection(MusicBand musicBand) {
        musicBandCollection.remove(musicBand);
    }

    /**
     * This method is used to randomly Shuffle the collection.
     */
    public void shuffleCollection() {
        Collections.shuffle(musicBandCollection);
    }

    /**
     * This method is used to Clear the collection of all the elements.
     */
    public void clearCollection() {
        musicBandCollection.clear();
    }

    /**
     * This method is used to read the collection from the file to the application.
     */
    private void loadCollection() {
        FileContentValidator fileContentValidator = new FileContentValidator();
        musicBandCollection = fileContentValidator.validateFileContent();
        lastInitTime = LocalDateTime.now();
    }

    /**
     * This method is used to write the collection from the application to the file.
     */
    public void saveCollection() {
        sortCollection(musicBandCollection);
        fileManager.writeCollection(musicBandCollection);
        lastSaveTime = LocalDateTime.now();
    }

    /**
     * This method is used to automatically generate a value of an ID.
     * @return 1 if the collection is empty, <p>n + 1 if the collection has n amount of elements.
     */
    public Integer generateNextID() {
        return (musicBandCollection.isEmpty()) ? 1 : musicBandCollection.stream().max(Comparator.comparing(MusicBand::getId)).get().getId() + 1;
    }

    /**
     * This method is a custom implementation of the toString() method in CollectionManager.
     * @return the collection parsed to String data type.
     */
    @Override
    public String toString() {
        if (musicBandCollection.isEmpty()) {
            return "Empty collection.";
        }
        StringBuilder info = new StringBuilder();
        musicBandCollection.forEach(musicBand ->
                info.append(musicBand.toString()).append("\n").append("-=".repeat(41)).append("\n"));
        return info.toString();
    }
}
