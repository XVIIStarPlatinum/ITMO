/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for non-existing organization in the collection.
 */
public class NullOrganizationException extends Exception {
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception NullOrganizationException
     * @param message String
     * @param cause String
     */
    public NullOrganizationException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    /**
     * NullOrganizationException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage() {
        return message;
    }
}