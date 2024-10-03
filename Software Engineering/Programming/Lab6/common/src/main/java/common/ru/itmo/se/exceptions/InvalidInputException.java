package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for invalid user input.
 */
@Getter
public class InvalidInputException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs an InvalidInputException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
