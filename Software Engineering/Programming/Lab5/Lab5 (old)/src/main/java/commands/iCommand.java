/**
 * Package for custom commands.
 */
package commands;
/**
 * Primordial interface for all commands.
 */
public interface iCommand {
    /**
     * Abstract command for returning command name.
     * @return String name
     */
    String getName();
    /**
     * Abstract command for returning command specifications.
     * @return String spec
     */
    String getSpec();
    /**
     * Abstract command for command execution.
     * @param arg String
     * @return boolean
     */
    boolean apply(String arg);
}
