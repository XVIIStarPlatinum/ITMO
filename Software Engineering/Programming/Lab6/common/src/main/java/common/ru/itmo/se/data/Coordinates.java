package common.ru.itmo.se.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a composite data type which describes a coordinate pair value of X and Y.
 * -- CONSTRUCTOR --
 * Constructs a Coordinates with the specified X and Y.
 */
@Getter
@AllArgsConstructor
public class Coordinates implements Serializable {
    /**
     * This field holds the value for SerialVersion, which is a good practice when you're trying to serialize an object.
     */
    @Serial
    private static final long SerialVersionUID = 1;
    /**
     * This field holds the allowed deviation for comparing 2 float values.
     */
    private static final float EPSILON = 0.000000001F;
    /**
     * This field holds the value of coordinate X.
     */
    private float x;
    /**
     * This field holds the value of coordinate Y.
     * -- GETTER --
     * Getter method for coordinate Y. Since this field doesn't have initial constraints, it doesn't justify having a custom implementation.
     */
    private float y;

    /**
     * A custom implementation of the hashCode() method in Coordinates.
     *
     * @return hash code of a Coordinates instance.
     */
    @Override
    public int hashCode() {
        return Math.round(x * y);
    }

    /**
     * A custom implementation of the toString() method in Coordinates.
     * @return values of X and Y parsed to String data type.
     */
    @Override
    public String toString() {
        return "X = " + x + "; " + "Y = " + y;
    }

    /**
     * A custom implementation of the equals(Object obj) method in Coordinates. It works by checking whether the differences of every field is less than EPSILON.
     * @param obj the object to be compared.
     * @return boolean value of whether the two coordinates were equal as defined in this method.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Coordinates coordinates) {
            return Math.abs(x - coordinates.getX()) < EPSILON && Math.abs(y - coordinates.getY()) < EPSILON;
        }
        return false;
    }
}
