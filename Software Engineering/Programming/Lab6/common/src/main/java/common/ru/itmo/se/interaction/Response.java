package common.ru.itmo.se.interaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class represents a response that is sent from the server.
 * --CONSTRUCTOR--
 * Constructs a Response with the specified fields.
 */
@Getter
@AllArgsConstructor
public class Response implements Serializable {
    /**
     * This field holds a response code which determines the output.
     */
    private ResponseCode responseCode;
    /**
     * This field holds the response body.
     */
    private String responseBody;
    /**
     * This method is a custom implementation of the toString() method in Response.
     * @return values of a Response parsed to String data type.
     */
    @Override
    public String toString() {
        return "Response[" + responseCode + "; " + responseBody + "]";
    }
}
