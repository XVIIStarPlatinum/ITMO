package common.ru.itmo.se.data;

/**
 * This enum represents all the possible values for the value of the music band's genre.
 */
public enum MusicGenre {
    /**
     * This value represents that of a psychedelic rock band.
     */
    PSYCHEDELIC_ROCK,
    /**
     * This value represents that of a psychedelic cloud rap group.
     */
    PSYCHEDELIC_CLOUD_RAP,
    /**
     * This value represents that of a soul band.
     */
    SOUL,
    /**
     * This value represents that of a pop band.
     */
    POP,
    /**
     * This value represents that of a british pop band.
     */
    BRIT_POP;

    /**
     * This method returns all values of this enum as a list.
     *
     * @return values of the enumeration as a list.
     */
    public static String nameList() {
        StringBuilder nameList = new StringBuilder();
        for (MusicGenre musicGenre : values()) {
            nameList.append(musicGenre.name()).append(",\n");
        }
        nameList = new StringBuilder(nameList.substring(0, nameList.length() - 2));
        nameList.append("\n").append("=".repeat(12));
        return nameList.toString();
    }
}
