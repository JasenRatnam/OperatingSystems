package virtualmem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 3
 * Due date: April 5th 2020
 * Class for virtual memory of process
 */
public class Vmem extends Thread
{
    //initalizes variables
    private static ArrayList<String> commands;
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    //io class to write output
    private IOFile write = new IOFile();   
    //time formatter
    private DecimalFormat formatter = new DecimalFormat("#0.0000");  
    
    //constructor
    public Vmem(int memSize, ArrayList<String> commands) {
        this.commands = commands;
        
    }

    //get the list of commands
    public ArrayList<String> getCommands()
    {
        return commands;
    }
    
    //function to store a variable with an id in memory
    public void memStore(int variableId, String value, double time, page[] mainMemory) throws FileNotFoundException, IOException {
        //initialize classes for the disk and new page to store variable
        IOFile disk = new IOFile();
        page newPage = new page(variableId,value,time);
        
        //if theres space in the main memory
        if(isSpace(mainMemory) != -1)
        {
            if(DEBUG)
            {
                System.out.println(isSpace(mainMemory));
                for(int i =0; i<(mainMemory.length); i++)
                {
                    if(mainMemory[i] != null)
                    {
                        System.out.println(mainMemory[i].print());
                    }
                }
            }
           
            //place variable in main memory at free space
            mainMemory[isSpace(mainMemory)] = newPage;
        
            //also save into disk
            disk.vmPages(newPage);
        }
        // no space, go in disk
        else{
            //swapped
            String output = "Clock: " + formatter.format(time+0.5) + ", Memory Manager, SWAP-STORE\n";
            write.writeFile(output);
            
            //add first page of main memory to disk
            disk.vmPages(mainMemory[0]);
            //remove that page from main memory and shift everything else
            for(int i=1;i<mainMemory.length;i++)
            {
                mainMemory[i-1]=mainMemory[i];
            }
            
            //place page in free space of main memory
            //add new page to main memory
            mainMemory[mainMemory.length-1]=newPage;
        }
        
    }
    
    //find if main memory has free space
    //return position of free space
    public int isSpace(page[] array) {
        for (int i=0; i<array.length; i++) 
        {
            if(array[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    //free a given variable from memory
    public boolean memFree(int variableId, page[] mainMemory) throws IOException {
        //initialize variables
        boolean found = false;
        
        //check if variable is in main memory
        //remove from main memory if it is
        for (int i=0; i<mainMemory.length; i++) 
        {
            if(mainMemory[i].getId() == variableId) {
                found = true;
                mainMemory[i] = null;
                return true;
            }
        }
        
        //if not in main memory, open disk
        IOFile disk = new IOFile();
        //not in main mem check in disk, need to swap
        if(found == false)
        {
            //check disk for variable and remove
            page newPage = disk.vmPagesFree(variableId);
            if(newPage != null)
            {
                return true;
            }
        }
        return false;
    }
    
    //lookup a variable in the memory
    public String memLookup(int variableId, double time, page[] mainMemory) throws IOException {
        
        //initialize variables
        boolean found = false;
        
        //look for variable in main memory
        for (int i=0; i<mainMemory.length; i++) 
        {
            //return variable if in main memory
            if(mainMemory[i].getId() == variableId) {
                found = true;
                if(DEBUG)
                {
                    System.out.println(mainMemory[i].getValue());
                }
                return mainMemory[i].getValue();
            }
        }
        
        //if not in main memory check disk
        IOFile disk = new IOFile();
        if(found == false)
        {
            //if variable is in disk
            if(disk.vmSearch(variableId))
            {
                page selectPage = null;
                if(DEBUG)
                {
                    System.out.println("in disk");
                }
                //if theres space in main memory
                if(isSpace(mainMemory) != -1)
                {
                    //bring to main memory
                    
                    //remove from disk
                    selectPage = disk.vmPagesFree(variableId);
                   // add in main memory
                    mainMemory[isSpace(mainMemory)] = selectPage;
                }
                else
                {
                    double tempLowest = 1000000000;
                    int lowestId = -1;
                    page lowestTime = null;
                    //check for variable with the smallest last time access
                    //swap with this variable
                    for (int i=0; i<mainMemory.length; i++) 
                    {
                        if(mainMemory[i].getLastAccess() < tempLowest)
                        {
                           tempLowest = mainMemory[i].getLastAccess();  
                           lowestTime = mainMemory[i];
                           lowestId = mainMemory[i].getId();
                        }
                    }
                    
                    for (int i=0; i<mainMemory.length; i++) 
                    {
                        if(mainMemory[i].getId() == lowestId)
                        {
                            //swap
                            selectPage = disk.vmPagesFree(variableId);
                            mainMemory[i] = selectPage;
                            disk.vmPages(lowestTime);   
                            String output = "Clock: " + formatter.format(time+0.5) + ", Memory Manager, SWAP-LOOKUP\n";
                            write.writeFile(output);
                        }
                    }
                    
                    
                }
                if(DEBUG)
                {
                    System.out.println(selectPage.getValue());
                }
                //return the variable found
                return selectPage.getValue();
            }
        }
        //variables doesn't exist
        return "-1";
    }
    
    /*
    * run vmem thread
    */
    @Override
    public void run() {
        
    }
}
