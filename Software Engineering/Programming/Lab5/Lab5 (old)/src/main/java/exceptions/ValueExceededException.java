/**
 * Package for custom exceptions.
 */
package exceptions;

/**
 * Exception for user input values that exceed limits.
 */
public class ValueExceededException extends IllegalArgumentException{
    /**
     * Exception message
     */
    private final String message;
    /**
     * Constructor for exception ValueExceededException
     * @param message String
     * @param cause String
     */
    public ValueExceededException(String message, Throwable cause){
        super(message, cause);
        this.message = message;
    }
    /**
     * ValueExceededException implementation of general method getMessage()
     * @return String message
     */
    @Override
    public String getMessage(){
        return message;
    }
}
