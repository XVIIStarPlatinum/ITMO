/**
 * Utility package for console app.
 */
package utility;

import data.Organization;
import data.OrganizationType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for managing collection.
 *
 * @author Ariguun Erkevich Bolorbold
 */
public class CollectionManager {
    /**
     * Field for last initialization time.
     */
    private LocalDateTime lastInitTime;
    /**
     * Field for hashtable initialization.
     */
    private Hashtable<Integer, Organization> OrgCollection = new Hashtable<>();
    /**
     * Field for last save time.
     */
    private LocalDateTime lastSaveTime;
    /**
     * Instance of file manager class.
     */
    private final FileManager fileManager;
    /**
     * Constructor for utility class CollectionManager.
     *
     * @param fileManager      File Manager
     */
    public CollectionManager(FileManager fileManager) throws IOException {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.fileManager = fileManager;
        loadCollection();
    }
    /**
     * Getter for hashtable.
     * @return hashtable
     */
    public Hashtable<Integer, Organization> getCollection(){
        return OrgCollection;
    }
    /**
     * Getter for last initialization time.
     * @return last init time
     */
    public LocalDateTime getLastInitTime(){
        return lastInitTime;
    }
    /**
     * Getter for last save time.
     * @return last save time
     */
    public LocalDateTime getLastSaveTime(){
        return lastSaveTime;
    }
    /**
     * Getter for collection type.
     * @return collection type
     */
    public String getCollectionType(){
        return OrgCollection.getClass().getName();
    }
    /**
     * This method returns the size of the collection.
     * @return collection size
     */
    public int collectionSize(){
        return OrgCollection.size();
    }
    /**
     * Getter for last element of the collection.
     * @return last element
     */
    public Organization getLast(){
        Set<Integer> setOfKeys = OrgCollection.keySet();
        Iterator<Integer> iterator = setOfKeys.iterator();
        if(OrgCollection.isEmpty()) return null;
        if(!iterator.hasNext()){
            Integer key = iterator.next();
            return OrgCollection.get(key);
        } else return null;
    }
    /**
     *
     */
    public TreeMap<Integer, Organization> sortHashtable(Hashtable<Integer, Organization> OrgCollection){
        return new TreeMap<>(OrgCollection);
    }
    /**
     * Field for set of keys for hashtable management.
     */
    Set<Integer> setOfKeys = OrgCollection.keySet();
    /**
     * Getter for hashtable element by ID.
     * @param id int
     * @return id
     */
    public Organization getByID(Integer id){
        Organization organization =  OrgCollection.get(id);
        if (organization == null){
            Console.printError("No such organization with given ID");
        }
        return organization;
    }
    /**
     * This method checks whether a collection element contains a certain key.
     * @param id int
     * @return boolean value
     */
    public boolean containsKey(Integer id){
        return OrgCollection.containsKey(id);
    }
    /**
     * This method returns a collection element by value.
     * @param orgToFind Organization
     * @return Organization
     */
    public Organization getByValue(Organization orgToFind){
        for(Integer key : setOfKeys){
            if(OrgCollection.get(key).equals(orgToFind)) return OrgCollection.get(key);
        }
        return null;
    }
    /**
     * This method returns collection elements by given type.
     * @param type Organization
     * @return type of organization
     */
    public Organization getByOrgType(String type){
        Set<Integer> setOfKeys = OrgCollection.keySet();
        for (Integer key : setOfKeys) {
            if (OrgCollection.get(key).getType().toString().equals(type.toUpperCase(Locale.ROOT)))
                return OrgCollection.get(key);
        }
        return null;
    }
    /**
     * This method returns annual turnovers by descending order.
     */
    public void printFieldDescendingAnnualTurnover(){
        TreeSet<Organization> copy = new TreeSet<>(Collections.reverseOrder(Organization::compareToAnnualTurnover));
        ArrayList<Double> arrayList = new ArrayList<>();
        copy.addAll(OrgCollection.values());
        for(Organization org : copy){
            arrayList.add(org.getAnnualTurnover());
        }
        Console.println(arrayList.toString().trim() + "\n");
    }
    /**
     * This method filters the collection by given type.
     * @param OrgTypeToFilter type
     * @return String type
     */
    public String OrganizationTypeFilteredInfo(OrganizationType OrgTypeToFilter){
        Set<Integer> setOfKeys = OrgCollection.keySet();
        Iterator<Integer> iterator = setOfKeys.iterator();
        StringBuilder info = new StringBuilder();
        while(iterator.hasNext()){
            Integer key = iterator.next();
            if(OrgCollection.get(key).getType().ordinal() < OrgTypeToFilter.ordinal()){
                info.append(OrgCollection.get(key)).append("\n\n");
            }
        }
        return info.toString().trim();
    }
    /**
     * This method inserts into the collection a given element.
     * @param key int
     * @param org Organization
     */
    public void insertToCollection(Integer key, Organization org){
        OrgCollection.put(key, org);
    }
    /**
     * This method removes an element from the collection.
     * @param key int
     */
    public void removeFromCollection(Integer key){
        OrgCollection.remove(key);
    }
    /**
     * This method removes lower value (annual turnover) elements.
     * @param annualTurnover annualTurnover
     */
    public void removeLower(Double annualTurnover) {
        OrgCollection.entrySet().removeIf(organization -> organization.getValue().getAnnualTurnover() < annualTurnover);
    }
    /**
     * This method removes elements with lower key value.
     * @param key int
     */
    public void removeLowerKey(Integer key){
        OrgCollection.entrySet().removeIf(e -> e.getKey() < key);
    }
    /**
     * This method filters the collection by name.
     * @param name String
     * @return String
     */
    public String filterContainsName(String name){
        Map <Integer, String> result = OrgCollection.values().stream().filter(organization -> name.equals(organization.getName())).collect(Collectors.toMap(Organization::getId, Organization::getName));
        return result.toString().replaceAll("[{}]", "").replaceAll("=", " = ");
    }
    /**
     * This method loads the collection.
     */
    private void loadCollection() {
        ContentValidator contentValidator = new ContentValidator();
        OrgCollection = contentValidator.validateContent();
        lastInitTime = LocalDateTime.now();
    }
    /**
     * This method clears the collection.
     */
    public void clearCollection() {
        OrgCollection.clear();
    }

    /**
     * This method saves the collection.
     */
    public void saveCollection(){
        TreeMap<Integer, Organization> treeMap = sortHashtable(OrgCollection);
        fileManager.writeCollection(treeMap);
        lastSaveTime = LocalDateTime.now();
    }
    /**
     * This method is an automatic id generator.
     * @return id
     */
    public int generateNextId() {
        return (OrgCollection.isEmpty()) ? 1 : OrgCollection.values().stream().max(Comparator.comparing(Organization::getId)).get().getId()+1;
    }
    /**
     * CollectionManager implementation of general method toString()
     * @return String
     */
    @Override
    public String toString(){
        if(OrgCollection.isEmpty()) return "Empty collection";
        Set<Integer> setOfKeys = OrgCollection.keySet();
        Iterator<Integer> iterator = setOfKeys.iterator();
        StringBuilder info = new StringBuilder();
        while(iterator.hasNext()){
            int key = iterator.next();
            info.append(key).append(". ");
            info.append(OrgCollection.get(key).toString());
            info.append("\n--------------------------------------------------------------------------------------\n");
        }
        return info.toString();
    }
}