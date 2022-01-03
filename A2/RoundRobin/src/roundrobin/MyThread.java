package roundrobin;

import java.text.DecimalFormat;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: March 9th 2020
 * Class for thread of process
 */
public class MyThread extends Thread
{
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    //id of process
    private int name;
    //process connected to thread
    private Process process = null;

    //contructor
    public MyThread(int id, Process process) {
        //initialize variables
        name = id;
        this.process = process;
    }
    
    //get thread id
    @Override
    public long getId()
    {
        return name;
    }
    
    //run process
    public void run() {

        //format for output
        DecimalFormat formatter = new DecimalFormat("#0.0000"); 
       
        if(DEBUG)
        {
            System.out.println("Thread started: " + name);
        }
        
        //run thread for remaining time of process
        while (process.getRemainingTime() > 0) {
            
            //do process until scheduler takes over
            
            if(DEBUG)
            {
                System.out.println("Thread " + name + " running");
            }
            
            //update remaining time of process
            process.updateRemainingTime(process.getQuantum()); 
            
            //suspend thread
            this.suspend();

        }
    }   
}
