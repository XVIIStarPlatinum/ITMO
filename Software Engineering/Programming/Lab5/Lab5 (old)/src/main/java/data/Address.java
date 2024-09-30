/**
 * Package for custom data.
 */
package data;

/**
 * Custom class for organization address.
 */
public class Address {
    /**
     * Field for street.
     */
    private String street;
    /**
     * Field for zip code.
     */
    private String zipCode;

    /**
     * Constructor for data class Address
     * @param street String
     * @param zipCode String
     */
    public Address(String street, String zipCode){
        this.street = street;
        this.zipCode = zipCode;
    }

    /**
     * @return String Street
     */
    public String getStreet(){
        return street;
    }

    /**
     * Getter method for zip code
     * @return String ZipCode
     */
    public String getZipCode(){
        return zipCode;
    }

    /**
     * Address implementation for general method toString()
     * @return String
     */
    @Override
    public String toString(){
        return "Street: " + street + ", Zip code: " + zipCode;
    }

    /**
     * Address implementation for general method equals()
     * @param o Object
     * @return boolean value
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o instanceof Address addr){
            return street.equals(addr.street) && zipCode.equals(addr.zipCode);
        }
        return false;
    }
}

