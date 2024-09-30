/**
 * Package for custom commands.
 */
package commands;

import java.util.Objects;

/**
 * Abstract parent class of Command.
 */
public abstract class Command implements iCommand{
    /**
     * Field for command name.
     */
    private final String name;
    /**
     * Field for command specification.
     */
    private final String spec;
    /**
     * Constructor for abstract class Command.
     * @param name name
     * @param spec spec
     */
    public Command(String name, String spec) {
        this.name = name;
        this.spec = spec;
    }
    /**
     * Getter for command name.
     * @return String name
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * Getter for command specification.
     * @return String spec
     */
    @Override
    public String getSpec() {
        return spec;
    }
    /**
     * Command implementation of general method hashCode.
     * @return int hash code
     */
    @Override
    public int hashCode(){
        return name.hashCode() + spec.hashCode();
    }
    /**
     * Command implementation of general method equals.
     * @param obj Object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Command command = (Command) obj;
        return Objects.equals(name, command.name) && Objects.equals(spec, command.spec);
    }
    /**
     * Abstract method for command logic and execution.
     * @param arg String
     * @return boolean
     */
    public abstract boolean apply(String arg);
}
