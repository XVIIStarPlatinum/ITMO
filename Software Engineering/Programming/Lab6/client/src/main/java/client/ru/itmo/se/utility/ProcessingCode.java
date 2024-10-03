package client.ru.itmo.se.utility;

/**
 * This enum represents all the possible values for a request's processing code.
 */
public enum ProcessingCode {
    /**
     * This value represents that of a normal command without arguments.
     */
    OK,
    /**
     * This value represents that of a command that accepts an object as an argument.
     * <p>In this case, it's the command add.</p>
     */
    OBJECT,
    /**
     * This value represents that of a command that accepts an object as an argument.
     * <p>In this case, it's the command update.</p>
     */
    UPDATE,
    /**
     * This value represents that of a command that facilitates the execution of a script file.
     * <p>In this case, it's the command execute_script.</p>
     */
    SCRIPT,
    /**
     * This value represents cases where something has gone wrong in the server-side execution.
     */
    ERROR
}
