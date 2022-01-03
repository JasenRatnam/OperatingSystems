package roundrobin;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: March 9th 2020
 * Class for scheduler thread
 * schedule processes
 */
public class Scheduler extends Thread
{
    //list of processes
    private CircularList queue;
    //quantum of scheduler
    private double quantum;
    //array of processes
    private ArrayList<Process> S_processes = new ArrayList();
    //system time
    private static double time = 1;
    //debugging boolean
    private static final Boolean DEBUG = false;
    //output string
    private static String output = "";
    
    //constructor
    public Scheduler(double quantum) {

        this.quantum = quantum;
        queue = new CircularList();
    }

    /*
    * adds a thread to the queue
    */
    public void addThread(Thread t) {
        t.setPriority(2);
        queue.addItem(t);

    }
    
    /*
    * Get processes for schedulers
    */
    public void setProcesses(ArrayList<Process> S_processes)
    {
        this.S_processes =S_processes;
    }

    /*
    * puts the scheduler to sleep for a time quantum
    */
    private void schedulerSleep(double quantum) {

        try {
            //uses milisecond
            Thread.sleep((long)(quantum*1000));

        } catch (InterruptedException e) { };

    }
    
    /*
    * find next process
    */
    public static int nextProcess(ArrayList<Process> S_processes)
    {
        long now = System.nanoTime();
        long systemNowTime = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);
                
        //if no more process, return -1
        int id= -1;
        double remaining_time = 0;
        //initialize variables
        boolean first = true;
        double timeLeft = 0;
        boolean fairness = true;
        
        // go trough every process
        for(Process processe : S_processes)
        {
            //if process has time left
            if (processe.getRemainingTime() > 0) 
            { 
                if(DEBUG)
                {
                    System.out.println("System time: " + time);
                }
                
                // if process arrived
                if(time >= processe.getArrivalTime())
                {
                    
                    //fairness for long jobs already in the queue.
                    if(processe.getWaitingRound() >= 5)
                    {
                        //choose this process
                        id = processe.getID();
                        processe.resetWaitingRound();
                        //dont look for other processes
                        fairness = false;
                    }
                    if (fairness)
                    {
                        //assume first process is the smallest
                        if(first)
                        {
                            remaining_time = processe.getRemainingTime();
                            id = processe.getID();
                            first = false;
                            timeLeft = processe.getBurstTime()-processe.getRemainingTime();
                        }
                        //check if other processes have less remaining time
                        else if(processe.getRemainingTime() < remaining_time)
                        {
                            remaining_time = processe.getRemainingTime();
                            id = processe.getID();
                            timeLeft = processe.getBurstTime()-processe.getRemainingTime();
                        }
                        //if to process have the same smallest remaining time
                        else if(processe.getRemainingTime() == remaining_time)
                        {
                            //take the oldest process
                            if(timeLeft < (processe.getBurstTime()-processe.getRemainingTime()))
                            {
                                remaining_time = processe.getRemainingTime();
                                id = processe.getID();
                                timeLeft = processe.getBurstTime()-processe.getRemainingTime();
                            }
                        }
                    }
                }
            }
        }
        
        //count the number of times a process has not been chosen.
        for(Process processe : S_processes)
        {
            //if process not chosen
            if(processe.getID() != id && processe.getRemainingTime()>0)
            {
                //increment waiting rounds
                processe.updateWaitingRound();
            }
        }
        return id;
        
    }

    /*
    * run scehduler
    */
    public void run() {
        DecimalFormat formatter = new DecimalFormat("#0.0000");  
        IOFile write = new IOFile();
        if(DEBUG)
        {
            System.out.println("time: " +  time);
        }
        
        Thread current;
        
        // set the priority of the scheduler to the highest priority
        boolean flag = true;
        this.setPriority(6);
        //while theres a process
        while (flag) {
            
            try {
                //get next process
                int i = nextProcess(S_processes);
                
                if(DEBUG)
                {
                    System.out.println("Chosen process: " + i);
                }
                
                //no more process
                if(i == -1)
                {
                    //no more porcesses
                    output += "\nWaiting Times:\n";
                    if(DEBUG)
                    {
                        System.out.println("\nWaiting Times:"); 
                    }

                    //print waiting time
                    for (int j = 0; j < S_processes.size(); j++) { 
                        output += S_processes.get(j).displayWaitingTime() + "\n";
                        if(DEBUG)
                        {
                            System.out.println(S_processes.get(j).displayWaitingTime()); 
                        }
                    } 
                    write.writeFile(output);
                    flag = false;
                    System.exit(0);
                }
                
                if(DEBUG)
                {
                    System.out.println("\nTime: " + time);
                }
                
                //scheduler wakes up after sleep
                //get next process
                current = (Thread)queue.getProcess(i);
                //check if next process exists
                if ( (current != null) && (current.isAlive()) ) {
                    
                    //if first time process is started
                    if(S_processes.get((int)current.getId()).getRemainingTime() == S_processes.get((int)current.getId()).getBurstTime())
                    {
                        output+="Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Started\n";
                        if(DEBUG)
                        {
                            System.out.println("Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Started");
                        }
                    }
                    
                    //give process to next thread by giving it a higher priority
                    current.setPriority(4);
                    if(DEBUG)
                    {
                        System.out.println("\n* * * Context Switch * * * ");
                        System.out.println("Process " + current.getName() + "  id: " + current.getId());
                        System.out.println("Quantum: " + S_processes.get((int)current.getId()).getQuantum());
                    }
                    
                    //resume process
                    output+="Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Resumed\n";
                    
                    if(DEBUG)
                    {
                        System.out.println("Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Resumed");
                    }
                    
                    //increment time by quantum
                    time += S_processes.get((int)current.getId()).getQuantum();
                    
                    //resume process thread
                    current.resume();
                    
                    
                    //make scheduler go to sleep
                    //will retake control after quantum of the process chosen
                    schedulerSleep(S_processes.get((int)current.getId()).getQuantum());
                    
                    //pause thread
                    output+="Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Paused\n";
       
                    if(DEBUG)
                    {
                        System.out.println("Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Paused\n");
                    }
                    
                    
                    //if last time process is done
                    if(S_processes.get((int)current.getId()).getRemainingTime() < S_processes.get((int)current.getId()).getQuantum())
                    {
                        // store wait time 
                        S_processes.get((int)current.getId()).updateWaitingTime(time - S_processes.get((int)current.getId()).getBurstTime()-S_processes.get((int)current.getId()).getArrivalTime()); 
                        //reset remaining time
                        S_processes.get((int)current.getId()).setRemainingTime(0);
                        
                        //process finished
                        output+="Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Finished\n";
                       
                        if(DEBUG)
                        {
                            System.out.println("Time " + formatter.format(time) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Finished\n");
                        }
                        
                        //display waiting time of process
                        output+="waiting time for process " + (S_processes.get((int)current.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current.getId()).getWaitingTime()) +"\n";
                        
                        if(DEBUG)
                        {
                            System.out.println("waiting time for process " + (S_processes.get((int)current.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current.getId()).getWaitingTime()));
                        } 
                    }
                    
                    //give process less priority for next round to allow other process to take over
                    current.setPriority(2);
                }

            } catch (NullPointerException e3) { } catch (IOException ex) {
                Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
