package server.ru.itmo.se.commands;

/**
 * Primordial interface used for all commands.
 * The interface follows the command design pattern.
 */
public interface Command {
    /**
     * Abstract method used for returning a command's name.
     *
     * @return command's name.
     */
    String getName();

    /**
     * Abstract method used for returning a command's specification.
     * @return command's specification.
     */
    String getSpec();

    /**
     * Abstract method used for executing a command.
     * @param commandStrArg command's string argument.
     * @param commandObjArg command's object argument.
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    boolean apply(String commandStrArg, Object commandObjArg);
}
