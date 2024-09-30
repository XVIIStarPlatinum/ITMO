/**
 * Package for custom commands.
 */
package commands;
import exceptions.InvalidElementCountException;
import utility.Console;

/**
 * Class for command execute_script.
 */
public class executeScript extends Command{
    /**
     * Constructor for command class execute_script.
     */
    public executeScript(){
        super("execute_script <fileName>", "executes a script from a given file");
    }
    /**
     * Command logic - executes the command.
     * @param arg user input
     * @return Command exit status.
     */
    @Override
    public boolean apply(String arg){
        try{
            if(arg.isEmpty()) throw new InvalidElementCountException("What did you want to execute?", new RuntimeException());
            else Console.println("Executing script '" + arg + "', please wait...");
            return true;
        } catch (InvalidElementCountException iece){
            Console.println("Usage: '" + getName() + "'");
        }
        return false;
    }
}
