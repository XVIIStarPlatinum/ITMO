/**
 * Package for custom data.
 */
package data;

/**
 * Enumeration class for organization types
 */
public enum OrganizationType {
    /**
     * Organization type #1.
     */
    COMMERCIAL,
    /**
     * Organization type #2.
     */
    PUBLIC,
    /**
     * Organization type #3.
     */
    PRIVATE_LIMITED_COMPANY;
    /**
     * This method returns the list of available types.
     * @return String
     */
    public static String nameList(){
        StringBuilder nameList = new StringBuilder("\n  Types: \n");
        for(OrganizationType organizationType : values()){
            nameList.append(organizationType.name()).append(", ");
        }
        nameList = new StringBuilder(nameList.substring(0, nameList.length() - 2));
        nameList.append("\n=====================================================");
        return nameList.toString();
    }
}


