/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception class for empty command history.
 */
public class EmptyHistoryException extends RuntimeException{
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception EmptyHistoryException
     * @param message String
     * @param cause String
     */
    public EmptyHistoryException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * EmptyHistoryException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}
