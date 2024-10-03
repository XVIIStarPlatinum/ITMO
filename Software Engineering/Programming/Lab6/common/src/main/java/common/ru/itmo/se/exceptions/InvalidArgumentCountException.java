package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for when a user inputs an argument for a command that doesn't require any (or vice-versa).
 */
@Getter
public class InvalidArgumentCountException extends IllegalArgumentException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs an InvalidArgumentCountException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public InvalidArgumentCountException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
