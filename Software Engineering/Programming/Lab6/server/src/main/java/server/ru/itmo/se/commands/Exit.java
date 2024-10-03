package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command Exit. It gracefully terminates the CLI application on the client side.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the Exit class.
 */
@ToString
public class Exit extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with the collection.
     */
    private final CollectionManager collectionManager;
    /**
     * Constructs an Exit with the specified CollectionManager.
     * @param collectionManager the specified CollectionManager.
     */
    public Exit(CollectionManager collectionManager) {
        super("exit", "", "Gracefully terminates the console application on the client side", CommandType.WITHOUT_ARGS);
        this.collectionManager = collectionManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the Exit command.
     *
     * @param commandStrArg the command's string argument (unnecessary).
     * @param commandObjArg the command's object argument (unnecessary).
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (!commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            collectionManager.saveCollection();
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}
