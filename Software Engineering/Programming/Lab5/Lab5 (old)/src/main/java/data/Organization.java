/**
 * Package for custom data.
 */
package data;

import java.time.LocalDate;
import java.util.Date;

/**
 * Custom class for organizations.
 * The general data class for creating an instance of an organization.
 */
public class Organization implements Comparable<Organization>{
    /**
     * This field represents the unique identifiable of an organization. Field value must not be null, must be more than 0 and must be automatically generated.
     */
    private Integer id;
    /**
     * This field represents the official name of an organization. Field value must not be null and must not be empty.
     */
    private String name;
    /**
     * This field represents the coordinates of an organization. Notice that this field is represented as its own class. Field must not be null.
     */
    private Coordinates coordinates;
    /**
     * This field represents the creation date of an organization. This field is generated automatically and MUST DEFINITELY not be null.
     */
    private Date creationDate;
    /**
     * This field represents the annual turnover of an organization. Field value must be greater than 0.
     */
    private double annualTurnover;
    /**
     * This field represents the type of organization. Notice that this field is represented as its own class. Field must not be null.
     */
    private OrganizationType type;
    /**
     * This field represents the official address of an organization. Notice that this field is represented as its own class. This field CAN be null.
     */
    private Address officialAddress;

    /**
     * Constructor of the data class Organization.
     * @param id int
     * @param name String
     * @param coordinates Coordinates
     * @param creationDate Date
     * @param annualTurnover int
     * @param type OrganizationType
     * @param officialAddress Address
     */
    public Organization(Integer id, String name, Coordinates coordinates, Date creationDate, double annualTurnover, OrganizationType type, Address officialAddress){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;
    }
    /**
     * Getter for ID
     * @return id
     */
    public Integer getId(){
        return id;
    }
    /**
     * Getter for name
     * @return name
     */
    public String getName(){
        return name;
    }
    /**
     * Getter for coordinates
     * @return coordinates Coordinates
     */
    public Coordinates getCoordinates(){
        return coordinates;
    }
    /**
     * Getter for creation date
     * @return creationDate Date
     */
    public Date getCreationDate(){
        return creationDate;
    }
    /**
     * Getter for annual turnover
     * @return annualTurnover int
     */
    public double getAnnualTurnover(){
        return annualTurnover;
    }
    /**
     * Getter for organization type
     * @return organization type
     */
    public OrganizationType getType(){
        return type;
    }

    /**
     * Getter for official address
     * @return officialAddress Address
     */
    public Address getOfficialAddress(){
        return officialAddress;
    }
    /**
     * Comparable method: compares an organization object to another.
     * @param organization organization
     * @return int id
     */
    public int compareTo(Organization organization){
        return id.compareTo(organization.getId());
    }
    /**
     * Comparable method: compares an organization object to another by annual turnover.
     * @param org Organization
     * @return annual turnover
     */
    public int compareToAnnualTurnover(Organization org){
        if(Double.valueOf(annualTurnover).equals(org.getAnnualTurnover())) return -1;
        if(annualTurnover > org.getAnnualTurnover()) return 1;
        else return -1;
    }
    /**
     * Organization implementation of general method equals()
     * @return String
     */
    @Override
    public String toString(){
        String output = "";
        output += "Organization №" + id;
        output += " (added " + LocalDate.now() + ")";
        output += "\n   Name: " + name;
        output += "\n   Coordinates: " + coordinates;
        output += "\n   Annual turnover: " + annualTurnover;
        output += "\n   Organization type: " + type;
        output += "\n   Legal address: " + officialAddress;
        return output;
    }
    /**
     * Organization implementation of general method hashCode()
     * @return int hash code
     */
    @Override
    public int hashCode(){
        return name.hashCode() + coordinates.hashCode() + creationDate.hashCode() + type.hashCode() + officialAddress.hashCode();
    }
    /**
     * Organization implementation of general method equals()
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof Organization org){
            return name.equals(org.getName()) && coordinates.equals(org.getCoordinates()) && creationDate.equals(org.getCreationDate()) && (annualTurnover == org.getAnnualTurnover()) && type.equals(org.getType()) && officialAddress.equals(org.getOfficialAddress());
        }
        return false;
    }
}

