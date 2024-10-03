package common.ru.itmo.se.interaction;

/**
 * This enum represents all the possible values for a response's representing code.
 */
public enum ResponseCode {
    /**
     * This value represents cases where everything has gone swimmingly.
     */
    OK,
    /**
     * This value represents cases where something went wrong.
     */
    ERROR,
    /**
     * This value represents a special case where the command server_exit is invoked.
     */
    SERVER_EXIT
}
