package virtualmem;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: April 5th 2020
 * Class for scheduler thread
 * schedule processes
 */
public class Scheduler extends Thread
{
    //list of processes
    private CircularList queue;
    //array of processes
    private ArrayList<Process> S_processes = new ArrayList();
    //system time
    private static double time = 1;
    //system time for second process
    private static double time2 = 1;
    //debugging boolean
    private static final Boolean DEBUG = false;
    //output string
    private static String output = "";
    
    //blocking queues to communicate between threads
    private static BlockingQueue<Double> messages = null;
    private static BlockingQueue<Vmem> Vmem = null;
    
    // size of main memory, number of frames
    private int mem_size;
    
    //list of commands
    private static ArrayList<String> commands = new ArrayList<String>();
    
    //constructor
    public Scheduler(BlockingQueue<Double> messages, BlockingQueue<Vmem> Vmem,ArrayList<String> commands, int memSize) {
        
        //set commands
        this.commands = commands;
        //set memory size
        this.mem_size = memSize;
        //set blocking queue for time
        this.messages = messages;
        //set blocking queue for vmem
        this.Vmem = Vmem;
        //create queue for all processes
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
    * set list of processes for scheduler
    * input list of processes
    */
    public void setProcesses(ArrayList<Process> S_processes)
    {
        this.S_processes =S_processes;
    }

    /*
    * puts the scheduler to sleep for a time quantum
    * input quantum time
    */
    private void schedulerSleep(double quantum) {

        try {
            //uses milisecond
            Thread.sleep((long)(quantum*1000));

        } catch (InterruptedException e) { };

    }
    
    /*
    * find next process to schedule
    */
    public static int nextProcess(ArrayList<Process> S_processes, int past)
    {     
        //initialize variables
        
        //if no more process, return -1
        int id= -1;
        double remaining_time = 0;
        boolean first = true;
        double timeLeft = 0;
        boolean fairness = true;
        
        // go trough every process in the list
        for(Process processe : S_processes)
        {
            //if process has time left
            if (processe.getRemainingTime() > 0) 
            { 
                if(DEBUG)
                {
                    System.out.println("System time: " + time);
                }
                
                //if current process already being done by another CPU
                if(processe.getID() == past)
                {
                    //ignore since its already chosen
                }
                
                // if process arrived
                else if(time >= processe.getArrivalTime())
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
        
        // initialize formatter for output and create IO class to write output
        DecimalFormat formatter = new DecimalFormat("#0.0000");  
        IOFile write = new IOFile();
        boolean secondProcess = false;
        boolean flag = true;
        
        //start virtual memory
        Vmem mem = new Vmem(mem_size, commands);
        mem.start();
        //suspend immeaditly to let processes take CPU
        //processes will restart it
        mem.suspend();
        
        if(DEBUG)
        {
            System.out.println("time: " +  time);
        }
        
        //create & initialize threads object for both CPUs
        Thread current = null;
        Thread current2 = null;
        
        // set the priority of the scheduler to the highest priority
        // to regain control when it wakes up
        this.setPriority(6);
        
        //while theres a process
        while (flag) {
            
            try {
                //get next process for both CPUS
                int i = nextProcess(S_processes,-2);
                int j = nextProcess(S_processes,i);
                
                if(DEBUG)
                {
                    System.out.println("Chosen process: " + i);
                }
                
                //no more process, simulation done
                if(i == -1)
                {
                    //no more processes
                    output += "\nWaiting Times:\n";
                    if(DEBUG)
                    {
                        System.out.println("\nWaiting Times:"); 
                    }

                    //print waiting time
                    for (int z = 0; z < S_processes.size(); z++) { 
                        output += S_processes.get(z).displayWaitingTime() + "\n";
                        if(DEBUG)
                        {
                            System.out.println(S_processes.get(z).displayWaitingTime()); 
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
                //get next process and find the thread associated with it in the queue
                current = (Thread)queue.getProcess(i);
                
                //multiprocessing if theres an avaible process
                //for second CPU
                if(j != -1)
                {
                    current2 = (Thread)queue.getProcess(j);
                    secondProcess = true;
                }
                
                //check if next process exists
                //do process if exists
                if ( (current != null) && (current.isAlive()) ) {
                    
                    //if first time process is started
                    //print time and started statment
                    if(S_processes.get((int)current.getId()).getRemainingTime() == S_processes.get((int)current.getId()).getBurstTime())
                    {
                        output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Started\n";
                        if(DEBUG)
                        {
                            System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Started");
                        }
                    }
                    
                    //print output fot second CPU
                    if(secondProcess)
                    {
                        if(S_processes.get((int)current2.getId()).getRemainingTime() == S_processes.get((int)current2.getId()).getBurstTime())
                        {
                            output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Started\n";
                            if(DEBUG)
                            {
                                System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Started");
                            }
                        }   
                    }
                    
                    //give process to next thread by giving it a higher priority
                    current.setPriority(4);
                    
                    //send time to process
                     try {
                            messages.put(time);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                     
                     //send virtual memory to process
                     try {
                        Vmem.put(mem);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                     
                    //start second process in second CPU
                    if(secondProcess)
                    {
                        current2.setPriority(4);
                        //give process the time
                         try {
                            messages.put(time);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                         
                         //give the process the virtual memory
                         try {
                            Vmem.put(mem);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    if(DEBUG)
                    {
                        System.out.println("\n* * * Context Switch * * * ");
                        System.out.println("Process " + current.getName() + "  id: " + current.getId());
                        System.out.println("Quantum: " + S_processes.get((int)current.getId()).getQuantum());
                        if(secondProcess)
                        {
                            System.out.println("\n* * * Context Switch second process * * * ");
                            System.out.println("Process " + current2.getName() + "  id: " + current2.getId());
                            System.out.println("Quantum: " + S_processes.get((int)current2.getId()).getQuantum());
                        }
                    }
                    
                    //resume process
                    output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Resumed\n";
                    if(secondProcess)
                    {
                        output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Resumed\n";
                    }
                    
                    if(DEBUG)
                    {
                        System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Resumed");
                        if(secondProcess)
                        {
                           System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Resumed"); 
                        }
                    }
                   
                    //create second time for second CPU
                    time2 = time;
                    //if last time process is done
                    if(S_processes.get((int)current.getId()).getRemainingTime() < S_processes.get((int)current.getId()).getQuantum())
                    {
                        //increment time by remaining time
                        time += S_processes.get((int)current.getId()).getRemainingTime();
                    }
                    else
                    {
                        //increment time by quantum
                        time += S_processes.get((int)current.getId()).getQuantum();
                    }
                    
                    if(secondProcess)
                    {
                        //if last time process is done
                        if(S_processes.get((int)current2.getId()).getRemainingTime() < S_processes.get((int)current2.getId()).getQuantum())
                        {

                            //increment time by remaining time
                            time2 += S_processes.get((int)current2.getId()).getRemainingTime();

                        }
                        else
                        {
                            //increment time by quantum
                            time2 += S_processes.get((int)current2.getId()).getQuantum();
                        }
                    }
                    
                    //resume process thread
                    current.resume();
                  
                    //second CPU
                    if(secondProcess)
                    {
                        current2.resume();
                    }
                    
                    //write to the output file, what the processes have done so far
                    write.writeFile(output);
                    
                    //make scheduler go to sleep
                    //will retake control after quantum of the process chosen
                    schedulerSleep(S_processes.get((int)current.getId()).getQuantum());
                    //processe takes control
                    
                    //scheduler retakes control from processe
                    
                    //take update virtual memory from processe
                    try {
                        mem = Vmem.take();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    output = "";
                    
                    
                    //output that process has been pauses 
                    output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Paused\n";
                    //if last time process is done
                    if(S_processes.get((int)current.getId()).getRemainingTime() < S_processes.get((int)current.getId()).getQuantum())
                    {
                        // store wait time 
                        S_processes.get((int)current.getId()).updateWaitingTime(time - S_processes.get((int)current.getId()).getBurstTime()-S_processes.get((int)current.getId()).getArrivalTime()); 
                        //reset remaining time
                        S_processes.get((int)current.getId()).setRemainingTime(0);
                        
                        //process finished
                        output+="Clock: " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Finished\n";
                       
                        if(DEBUG)
                        {
                            System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Finished\n");
                        }
                        
                        //display waiting time of process
                        output+="waiting time for process " + (S_processes.get((int)current.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current.getId()).getWaitingTime()*1000) +"\n";
                        
                        if(DEBUG)
                        {
                            System.out.println("waiting time for process " + (S_processes.get((int)current.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current.getId()).getWaitingTime()*1000));
                        } 
                    }
                    
                    if(secondProcess)
                    {
                        output+="Clock: " + formatter.format(time2*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Paused\n";
                    }
                    
                    if(DEBUG)
                    {
                        System.out.println("Time " + formatter.format(time*1000) + ", Process " + (S_processes.get((int)current.getId()).getID()+1) + ", Paused\n");
                        System.out.println("Time " + formatter.format(time2*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Paused\n");
                    }
                    
                    if(secondProcess)
                    {
                        //if last time process is done
                        if(S_processes.get((int)current2.getId()).getRemainingTime() < S_processes.get((int)current2.getId()).getQuantum())
                        {
                            // store wait time 
                            S_processes.get((int)current2.getId()).updateWaitingTime(time2 - S_processes.get((int)current2.getId()).getBurstTime()-S_processes.get((int)current2.getId()).getArrivalTime()); 
                            //reset remaining time
                            S_processes.get((int)current2.getId()).setRemainingTime(0);

                            //process finished
                            output+="Clock: " + formatter.format(time2*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Finished\n";

                            if(DEBUG)
                            {
                                System.out.println("Time " + formatter.format(time2*1000) + ", Process " + (S_processes.get((int)current2.getId()).getID()+1) + ", Finished\n");
                            }

                            //display waiting time of process
                            output+="waiting time for process " + (S_processes.get((int)current2.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current2.getId()).getWaitingTime()*1000) +"\n";

                            if(DEBUG)
                            {
                                System.out.println("waiting time for process " + (S_processes.get((int)current2.getId()).getID()+1) + " is: " + formatter.format(S_processes.get((int)current2.getId()).getWaitingTime()*1000));
                            } 
                        }
                        //update real system time 
                        if(time >= time2)
                        {
                            time = time;
                            time2 = 0;
                        }
                        else
                        {
                            time = time2;
                            time2 = 0;
                        }
                    }
                    
                    //give process less priority for next round to allow other process to take over
                    current.setPriority(2);
                    if(secondProcess)
                    {
                        current2.setPriority(2);
                    }
                }

                //catch errors
            } catch (NullPointerException e3) { } catch (IOException ex) {
                Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
        //next cycle with new processes
        }
    }
}
