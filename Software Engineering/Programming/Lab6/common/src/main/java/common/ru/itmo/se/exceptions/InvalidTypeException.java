package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception for invalid music genre types.
 */
@Getter
public class InvalidTypeException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs an InvalidTypeException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public InvalidTypeException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
