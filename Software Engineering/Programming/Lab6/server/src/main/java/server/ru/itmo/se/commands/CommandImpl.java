package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * Abstract parent class for all the commands.
 */
@Getter
@AllArgsConstructor
public abstract class CommandImpl implements Command {
    /**
     * This field holds the command's name.
     */
    private final String name;
    /**
     *
     */
    private final String usage;
    /**
     * This field holds the command's specification.
     */
    private final String spec;
    /**
     *
     */
    private final CommandType commandType;
    /**
     * This method is a custom implementation of the hashCode() method.
     * @return hash code of a command instance.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * This method is a custom implementation of the equals(Object obj) method. It works by comparing every field and class.
     * @param obj the object to be compared.
     * @return boolean value of whether the two music bands were equal as defined in this method.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CommandImpl command = (CommandImpl) obj;
        return Objects.equals(name, command.name) && Objects.equals(usage, command.usage) && Objects.equals(spec, command.spec);
    }
}
