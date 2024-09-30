/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for invalid organization type.
 */
public class InvalidTypeException extends RuntimeException{
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception InvalidTypeException
     * @param message String
     * @param cause String
     */
    public InvalidTypeException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * InvalidTypeException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}