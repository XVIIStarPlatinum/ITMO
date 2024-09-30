/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for invalid user input.
 */
public class InvalidInputException extends RuntimeException {
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception InvalidInputException
     * @param message String
     * @param cause String
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * InvalidInputException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}