package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception for user input values that are out of defined bounds.
 */
@Getter
public class ValueRangeException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs a ValueRangeException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public ValueRangeException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
