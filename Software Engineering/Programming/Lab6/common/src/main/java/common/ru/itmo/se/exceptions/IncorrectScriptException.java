package common.ru.itmo.se.exceptions;

import lombok.Getter;

/**
 * Exception class for when a user's script contains a line that cannot be executed.
 */
@Getter
public class IncorrectScriptException extends RuntimeException {
    /**
     * This field holds the exception's message.
     * -- GETTER --
     * Getter method for the exception's message.
     */
    private final String message;

    /**
     * Constructs an IncorrectScriptException with the specified message and cause.
     *
     * @param message the specified message.
     * @param cause   the specified Throwable object.
     */
    public IncorrectScriptException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
