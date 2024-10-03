package data;

/**
 * This enum represents all the possible values for the value of the music band's genre.
 */
public enum MusicGenre {
    /**
     * This value represents that of a psychedelic rock band.
     */
    PSYCHEDELIC_ROCK("PSYCHEDELIC_ROCK"),
    /**
     * This value represents that of a psychedelic cloud rap group.
     */
    PSYCHEDELIC_CLOUD_RAP("PSYCHEDELIC_CLOUD_RAP"),
    /**
     * This value represents that of a soul band.
     */
    SOUL("SOUL"),
    /**
     * This value represents that of a pop band.
     */
    POP("POP"),
    /**
     * This value represents that of a british pop band.
     */
    BRIT_POP("BRIT_POP");

    private final String genre;

    MusicGenre(String genre) {
        this.genre = genre;
    }
    public String getMusicGenre() {
        return genre;
    }
}
