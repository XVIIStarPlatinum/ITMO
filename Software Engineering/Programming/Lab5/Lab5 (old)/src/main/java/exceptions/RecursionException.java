/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for recursion in scripts.
 */
public class RecursionException extends RuntimeException {
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception RecursionException
     * @param message String
     * @param cause String
     */
    public RecursionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * RecursionException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}
