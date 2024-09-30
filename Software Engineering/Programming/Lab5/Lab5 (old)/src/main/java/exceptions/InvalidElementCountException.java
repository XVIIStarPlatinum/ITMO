/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for invalid element count in collection.
 */
public class InvalidElementCountException extends IllegalArgumentException {
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception InvalidElementCountException
     * @param message String
     * @param cause String
     */
    public InvalidElementCountException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * InvalidElementCountException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}
