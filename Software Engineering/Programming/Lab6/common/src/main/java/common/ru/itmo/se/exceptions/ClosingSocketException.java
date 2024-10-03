package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for a closed socket.
 */
@Getter
public class ClosingSocketException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;
    /**
     * Constructs an EmptyCollectionException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public ClosingSocketException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
