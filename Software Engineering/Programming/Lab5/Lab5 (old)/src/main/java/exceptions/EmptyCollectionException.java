/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception class for empty collections.
 */
public class EmptyCollectionException extends RuntimeException{
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception EmptyCollectionException
     * @param message String
     * @param cause String
     */
    public EmptyCollectionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * EmptyCollectionException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}
