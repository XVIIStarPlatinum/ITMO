/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception class for disallowed program state.
 */
public class IllegalStateException extends Exception{
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception IllegalStateException
     * @param message String
     * @param cause String
     */
    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * IllegalStateException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}