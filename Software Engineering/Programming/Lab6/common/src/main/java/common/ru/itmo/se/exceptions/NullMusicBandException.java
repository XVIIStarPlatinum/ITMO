package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for non-existing music bands in the collection.
 */
@Getter
public class NullMusicBandException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs a NullMusicBandException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public NullMusicBandException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
