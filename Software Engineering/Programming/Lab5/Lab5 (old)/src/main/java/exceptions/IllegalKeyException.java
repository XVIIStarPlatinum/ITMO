/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception class for illegal key value (they must be unique)
 */
public class IllegalKeyException extends RuntimeException{
    /**
     * exception message
     */
        private final String message;
    /**
     * Constructor for exception IllegalKeyException
     * @param message String
     * @param cause String
     */
        public IllegalKeyException(String message, Throwable cause) {
            super(message, cause);
            this.message = message;
        }
    /**
     * IllegalKeyException implementation of general method getMessage()
     * @return String message
     */
    @Override
        public String getMessage() {
            return message;
        }
}
