/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for null value in not null fields
 */
public class NullValueException extends IllegalArgumentException {
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception NullValueException
     * @param message String
     * @param cause String
     */
    public NullValueException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * NullValueException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}