/**
 * Package for custom data.
 */
package data;

/**
 * Custom class for organization coordinates.
 * It points to the general location of an organization.
 */
public class Coordinates {
    /**
     * (double) abscissa axis of data class Coordinates
     */
    private Double x; //Field must not be null
    /**
     * (float) ordinate axis of data class Coordinates
     */
    private float y; //Maximum value of y: 614

    /**
     * Constructor for data class Coordinates
     * @param x double
     * @param y float
     */
    public Coordinates(Double x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for value x
     * @return x
     */
    public double getX() {
        try {
            return x;
        } catch (NullPointerException e) {
            return Float.MIN_VALUE;
        }
    }
    /**
     * Getter for value y
     * @return y
     */
    public float getY(){
        return y;
    }
    /**
     * Coordinates implementation of general method toString()
     * @return String
     */
    @Override
    public String toString(){
        return "X = " + x + "; " + "Y = " + y;
    }
    /**
     * Coordinates implementation of general method equals()
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o instanceof Coordinates coordinates){
            return x.equals(coordinates.x) && (y == coordinates.getY());
        }
        return false;
    }
}

