package common.ru.itmo.se.utility;

/**
 * Class used for outputting colored and formatted strings.
 */
public class PrettyPrinter {
    /**
     * This method is a custom implementation of the print() method. The output is colored purple with the Help of a corresponding ANSI code.
     * @param toOut the object to be printed.
     */
    public static void print(Object toOut) {
        System.out.print("\u001B[1;35m" + toOut + "\u001B[0m");
    }

    /**
     * This method is a custom implementation of the println() method. The output is colored green with the Help of a corresponding ANSI code.
     * @param toOut the object to be printed.
     */
    public static void println(Object toOut) {
        System.out.println("\u001B[32m" + toOut + "\u001B[0m");
    }

    /**
     * This method is used to create a newline.
     */
    public static void println() {
        System.out.println();
    }
    /**
     * This method is a custom implementation of the err.print() method. The output is colored black with red background with the help of corresponding ANSI codes.
     * @param toOut the error that was raised.
     */
    public static void printError(Object toOut) {
        System.out.println("\u001B[41m" + "\u001B[30m" + toOut + "\u001B[0m");
    }

    /**
     * This method is used to output all the commands in a table format. The row border is colored purple, the object and the column border is colored cyan with the Help of corresponding ANSI codes.
     * @param e1 first column object.
     * @param e2 second column object.
     */
    public static void printTable(Object e1, Object e2) {
        System.out.printf("\u001B[36m" + "| %-64s | %-12s | %-75s | %n", e1, e2);
        System.out.print("\u001B[35m" + "=-".repeat(73) + "\n");
    }
}
