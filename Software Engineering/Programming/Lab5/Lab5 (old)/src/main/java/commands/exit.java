/**
 * Package for custom commands.
 */
package commands;

import exceptions.InvalidElementCountException;
import utility.Console;

/**
 * Class for command exit.
 */
public class exit extends Command {
    /**
     * Constructor for command class exit.
     */
    public exit(){
            super("exit", "terminates the console app");
        }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg){
        try{
            if(!arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute?", new RuntimeException());
            return true;
        } catch (InvalidElementCountException iece){
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}
