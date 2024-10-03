package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for null values in non-null fields.
 */
@Getter
public class NullValueException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs a NullValueException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public NullValueException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
