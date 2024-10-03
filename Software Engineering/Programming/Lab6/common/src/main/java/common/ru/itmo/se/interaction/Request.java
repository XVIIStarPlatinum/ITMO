package common.ru.itmo.se.interaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class represents a request that is sent from a client.
 * --CONSTRUCTOR--
 * Constructs a Request with the specified fields.
 */
@Getter
@AllArgsConstructor
public class Request implements Serializable {
    /**
     * This field holds the command's name.
     */
    private String commandName;
    /**
     * This field holds the command's string argument.
     */
    private String commandStrArg;
    /**
     * This field holds the command's object argument.
     */
    private Serializable commandObjArg;
    /**
     * Constructs a Request without an object argument.
     * @param commandName the command name.
     * @param commandStrArg the command's string argument.
     */
    public Request(String commandName, String commandStrArg) {
        this(commandName, commandStrArg, null);
    }

    /**
     * Constructs a blank Request.
     */
    public Request() {
        this("", "");
    }

    /**
     * This method is used to check whether a request is blank or not.
     * @return true if the request is blank,<p>and false if the request isn't.
     */
    public boolean isEmpty() {
        return commandName.isEmpty() && commandStrArg.isEmpty() && commandObjArg == null;
    }

    /**
     * This method is a custom implementation of the toString() method in Request.
     * @return values of a Request parsed to String data type.
     */
    @Override
    public String toString() {
        return "Request[" + commandName + " " + commandStrArg + " {" + commandObjArg + "}]";
    }
}
