package virtualmem;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 3
 * Due date: April 5th 2020
 * Class to make a memory page
 */
public class page 
{
    // initialize variables
    private static int variableId;
    private static String value;
    private static double lastAccess;

    /**
     * constructor of page
     * @param variableId
     * @param value
     * @param lastAccess
     */
    public page(int variableId, String value, double lastAccess) {
        this.variableId = variableId;
        this.value = value;
        this.lastAccess = lastAccess;
    }
    
    /**
     * getter method of id
     * @return
     */
    public int getId()
    {
        return variableId;
    }
    
    /**
     * getter method of value
     * @return
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * getter method for last access time of the variable
     * @return
     */
    public double getLastAccess()
    {
        return lastAccess;
    }
    
    /**
     * print the page
     * @return
     */
    public String print()
    {
        String output = variableId + " " + value + " " + lastAccess + "\n";
        
        return output;
    }
}
