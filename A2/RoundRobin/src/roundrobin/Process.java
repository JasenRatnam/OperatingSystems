package roundrobin;

import java.text.DecimalFormat;


/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: March 9th 2020
 * Class for processes
 */
public class Process {
    
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    //process id
    public final int id;
    //process burst time
    public double burstTime;
    // number of rounds process has been waiting
    public int waitingRound;
    // remaining time for the process to complete
    public double remainingTime;
    // time the process has been waiting
    public double waitingTime;
    // time the porcess arrives to the system
    public final double arrivalTime;
    // the quantum time
    public double quantum;
    public double systemTime;
 
    /*
    * process construction
    */
    public Process(int id, double burstTime, double arrivalTime) {
       // set default values when process is created
        this.id = id;
        this.waitingRound = 0;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.arrivalTime = arrivalTime;
        // quantum is 10% of remaining time
        this.quantum = 0.1 * this.remainingTime;
        this.waitingTime = 0;
        this.systemTime = 0;
    }
    
    /*
    * update the remaining time of the process by removing the time it has just 
    * completed.
    * update the quantum to be 10% of the new remaining time
    */
    public void updateRemainingTime(double time)
    {
        this.remainingTime -= time;
        //make sure its not getting to small
        if(this.quantum > 0.01)
        {
            this.quantum = 0.1 * this.remainingTime;
            
        }
        else
            this.remainingTime = 0;
    }
    
    /*
    * method to set the remaining time to a given value
    */
    public void setRemainingTime(double time)
    {
        this.remainingTime = time;
    }
    
    /*
    * update waiting round
    * update number of times the process has been waiting
    */
    public void updateWaitingRound()
    {
        this.waitingRound++;
    }
    
    /*
    * update the waiting time to a new value
    */
    public void updateWaitingTime(double time)
    {
        this.waitingTime = time;
    }
    
    /*
    * reset the number of time the process has been waiting to 0
    */
    public void resetWaitingRound()
    {
        waitingRound = 0;
    }
    
    /*
    * getter method
    * returns the id of the process
    */
    public int getID()
    {
        return id;
    }
    
    /*
    * getter method
    * returns the number of times the process has been waiting
    */
    public int getWaitingRound()
    {
        return waitingRound;
    }
    
    /*
    * getter method
    * returns the burst time of the process
    */
    public double getBurstTime()
    {
        return burstTime;
    }
    
    /*
    * getter method
    * returns the arrival time of the process
    */
    public double getArrivalTime()
    {
        return arrivalTime;
    }
    
    /*
    * getter method
    * returns the remaining time of the process to finish
    */
    public double getRemainingTime()
    {
        return remainingTime;
    }
    
    /*
    * getter method
    * returns the waiting time of the process
    */
    public double getWaitingTime()
    {
        return waitingTime;
    }
    
    /*
    * getter method
    * returns the quantum of the process
    */
    public double getQuantum()
    {
        return quantum;
    }
    
    /*
    * displays the id of the process and the time it has been waiting
    */
    public String displayWaitingTime()
    {
        DecimalFormat formatter = new DecimalFormat("#0.0000");  
        String output = "Process " +(getID()+1) + ": " + formatter.format(getWaitingTime());
        if(DEBUG)
        {
            System.out.println("DEBUG");
            System.out.println("Waiting time display:");
            System.out.println(output);
        }
        return output;
    }
}