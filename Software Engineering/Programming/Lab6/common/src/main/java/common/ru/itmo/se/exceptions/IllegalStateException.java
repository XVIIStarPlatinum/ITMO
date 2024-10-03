package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for illegal program states.
 */
@Getter
public class IllegalStateException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs an IllegalStateException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
