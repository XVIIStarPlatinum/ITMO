package common.ru.itmo.se.interaction;

import lombok.Getter;

/**
 * This enum represents all possible command types. Implemented for optimization purposes.
 * Idea suggested by @Vediusse.
 */
@Getter
public enum CommandType {
    /**
     * This value represents commands without any arguments. The commands are: <p>
     * clear, exit,<p>group_counting_by_establishment_date,<p>help, history, info,<p>print_field_descending_establishment_date, <p>show, shuffle.
     */
    WITHOUT_ARGS(0, false),
    /**
     * This value represents commands that require a form. The commands are:<p>add</p>
     */
    WITH_FORM(0, true),
    /**
     * This value represents commands that require a string argument. The commands are:<p>
     * execute_script,<p>filter_less_than_number_of_participants,<p>remove_at,<p>remove_by_id.
     */
    WITH_ARGS(1, false),
    /**
     * This value represents commands that require both string argument and form. The commands are:<p>update</p>
     */
    WITH_ARGS_FORM(1, true);
    /**
     * This field holds the amount of arguments needed for a command.
     */
    private final int argumentCount;
    /**
     * This field determines whether a command needs a form.
     */
    private final boolean needForm;

    /**
     * Constructs a CommandType with the specified argument count and form requirement.
     * @param argumentCount the amount of required arguments.
     * @param needForm the need for a form.
     */
    CommandType(int argumentCount, boolean needForm) {
        this.argumentCount = argumentCount;
        this.needForm = needForm;
    }
}
