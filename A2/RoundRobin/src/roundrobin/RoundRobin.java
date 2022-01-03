package roundrobin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: March 9th 2020
 * Implement the simulation of a process scheduler that is responsible for
 * scheduling a given list of processes. The scheduler is running on a machine 
 * with one CPU.
 * Rules:
 *      -Scheduler works in a cyclic manner, i.e. it gives the CPU to a process 
 *       for a quantum of time and then get the CPU back.
 *      -The quantum for each process is equal to 10 percent of the remaining 
 *       execution time of the process.
 *      -Each process comes with its own arrival time and burst time.
 *      -Each time, the scheduler gives the CPU to a process (say P1) that has 
 *       the shortest remaining processing time, but this should not starve other 
 *       processes in the queue and which are ready to start. These processes 
 *       should be allocated to the CPU before it is given back to P1, i.e. 
 *       include some fairness for long jobs already in the queue.
 *      -In the case that two or more processes have equal remaining time for 
 *       completion, the scheduler gives priority to the older process 
 *       (i.e. process that has been in the system for longer time).
 * The simulation should determine and print out the time each process has spent 
 * in the waiting queue.
 * Input: input.txt
 *  Information related to processes
 *  Arrival time, execution time
 * Output: output.txt
 *  Set of strings indicating events in the program.
 *  Start and end of each process
 *  Start and end of each time slice.
 *  Waiting time for each process
 */
public class RoundRobin 
{
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    // Initialize output string
   // private static String output = "";
    
    /*
    * method to find the process that will be executed next by the scheduler
    * find process with smallest remaining time
    * oldest process if same remaining time
    * accounts for fairness, a process that has been waiting for 5 rounds will get priority.
    * without threads not used anymore
    */
//    public static int nextProcess(ArrayList<Process> S_processes, double systemTime)
//    {
//        //if no more process, return -1
//        int id= -1;
//        double remaining_time = 0;
//        //initialize variables
//        boolean first = true;
//        double timeLeft = 0;
//        boolean fairness = true;
//        
//        // go trpugh ecery process
//        for(Process processe : S_processes)
//        {
//            //if process has time left
//            if (processe.getRemainingTime() > 0) 
//            { 
//                // if process arrived
//                if(systemTime >= processe.getArrivalTime())
//                {
//                    //fairness for long jobs already in the queue.
//                    if(processe.getWaitingRound() >= 5)
//                    {
//                        //choose this process
//                        id = processe.getID();
//                        processe.resetWaitingRound();
//                        //dont look for other processes
//                        fairness = false;
//                    }
//                    if (fairness)
//                    {
//                        //assume first process is the smallest
//                        if(first)
//                        {
//                            remaining_time = processe.getRemainingTime();
//                            id = processe.getID();
//                            first = false;
//                            timeLeft = processe.getBurstTime()-processe.getRemainingTime();
//                        }
//                        //check if other processes have less remaining time
//                        else if(processe.getRemainingTime() < remaining_time)
//                        {
//                            remaining_time = processe.getRemainingTime();
//                            id = processe.getID();
//                            timeLeft = processe.getBurstTime()-processe.getRemainingTime();
//                        }
//                        //if to process have the same smallest remaining time
//                        else if(processe.getRemainingTime() == remaining_time)
//                        {
//                            //take the oldest process
//                            if(timeLeft < (processe.getBurstTime()-processe.getRemainingTime()))
//                            {
//                                remaining_time = processe.getRemainingTime();
//                                id = processe.getID();
//                                timeLeft = processe.getBurstTime()-processe.getRemainingTime();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        
//        //count the number of times a process has not been chosen.
//        for(Process processe : S_processes)
//        {
//            //if process not chosen
//            if(processe.getID() != id && processe.getRemainingTime()>0)
//            {
//                //increment waiting rounds
//                processe.updateWaitingRound();
//            }
//        }
//        return id;
//    }
//    
//    /*
//    * do the process chosen from id given
//    * without threads not used anymore
//    */
//    public static double doProcess(ArrayList<Process> S_processes, int id, double time, double systemTime, boolean last)
//    {
//        //format for otput
//        DecimalFormat formatter = new DecimalFormat("#0.0000");  
//        
//        //if first time doing process
//        if(S_processes.get(id).getRemainingTime() == S_processes.get(id).getBurstTime())
//        {
//            //process started
//            output += "\nTime " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Started\n";
//            if(DEBUG)
//            {
//                System.out.println("\nTime " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Started");
//            }
//        }
//        else
//        {
//            output += "\nTime " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Resumed\n";
//            if(DEBUG)
//            {
//                System.out.println("\nTime " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Resumed");
//            }
//        }
//
//        // increase system time by quantum (do process)
//        systemTime += time; 
//        S_processes.get(id).updateRemainingTime(time); 
//        output += "Time " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Paused\n";
//        if(DEBUG)
//        {
//            System.out.println("Time " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Paused");
//        }
//        //if last time process is done
//        if(last)
//        {
//            // store wait time 
//            S_processes.get(id).updateWaitingTime(systemTime - S_processes.get(id).getBurstTime()- S_processes.get(id).getArrivalTime()); 
//            //reset remaining time
//            S_processes.get(id).setRemainingTime(0);
//            //output
//            output += "Time " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Finished\n";
//            if(DEBUG)
//            {
//                System.out.println("Time " + formatter.format(systemTime) + ", Process " + (S_processes.get(id).getID()+1) + ", Finished");
//            }
//        }
//        
//        return systemTime;
//    }
//    
//    //scheduling without threads not used anymore
//    public static void schedule(ArrayList<Process> S_processes, int numberProcess)
//    {
//        //innitialize variables
//        double systemTime = 1; 
//        boolean flag = false; 
//        boolean haveProcess = true; 
//  
//        //loop roundrobin
//        while (true) { 
//            //find the next process
//            int i = nextProcess(S_processes,systemTime);
//            
//            //no process to do
//            if(i==-1)
//            {
//                haveProcess = false;
//            }
//            
//            //doing process flag
//            flag = true; 
//             
//            if(haveProcess)
//            {
//                //if process has time left
//                if (S_processes.get(i).getRemainingTime() > 0) 
//                { 
//                    //doing a process
//                    flag = false;
//
//                    //do process
//
//                    //process has more time than the quantum
//                    if (S_processes.get(i).getRemainingTime() > S_processes.get(i).getQuantum()) { 
//
//                        // increase system time by quantum (do process)
//                        systemTime = doProcess(S_processes, i, S_processes.get(i).getQuantum(), systemTime, false);
//
//                        
//                    } 
//                    //Remaining time is less than quantum
//                    else { 
//
//                        systemTime = doProcess(S_processes, i, S_processes.get(i).getRemainingTime(), systemTime,true);
//                        // Processe finished
//
//                    }
//                }
//            }
//            
//            // for exit the while loop, not doing any process
//            if (flag) { 
//                break; 
//            } 
//        } 
//        
//        output += "\nWaiting Times:\n";
//        if(DEBUG)
//        {
//            System.out.println("\nWaiting Times:"); 
//        }
//        
//        //print waiting time
//        for (int j = 0; j < numberProcess; j++) { 
//            output += S_processes.get(j).displayWaitingTime() + "\n";
//            if(DEBUG)
//            {
//                System.out.println(S_processes.get(j).displayWaitingTime()); 
//            }
//        } 
//    }
                  
    /*
    * main method to start scheduling
    */
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        //initializes variables
        ArrayList<Process> S_processes = new ArrayList();
       
        //create IO class
        IOFile read = new IOFile();
        
        //read input file and create processes arraylist
        S_processes = read.readFile();
        
        //make this have most priority
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        //create schedule
        Scheduler scheduler = new Scheduler(1000);
        scheduler.setProcesses(S_processes);
        
        //start scheduling
        scheduler.start();
        
        //create threads for all processes
        for(Process process: S_processes)
        {
            MyThread t1 = new MyThread(process.getID(),process);
            t1.start();
            t1.suspend();
            scheduler.addThread(t1);
        }
        System.out.println("Running....");
    }
}
