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
 * COEN 346, programming assignment 3
 * Due date: April 5th 2020
 * Class for thread of process
 */
public class MyThread extends Thread
{
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    //id of process
    private int name;
    //size of main memory
    private int mem_size;
    //process connected to thread
    private Process process = null;
    //list of commands
    private static ArrayList<String> commands = new ArrayList<String>();
   //output of process
    private String output = "";
    //main memory
    private static page[] mainMemory = null;
    //blocking queues to communicate with other threads
    private static BlockingQueue<Double> messages = null;
    private static BlockingQueue<Vmem> virtualMem = null;
    
    //contructor
    public MyThread(int id, Process process, ArrayList<String> commands, int memSize, BlockingQueue<Double> messages,BlockingQueue<Vmem> vmem) {
        //initialize variables
        this.messages = messages;
        this.virtualMem = vmem;
        name = id;
        this.process = process;
        this.commands = commands;
        this.mem_size = memSize;
        mainMemory = new page [memSize];
    }
    
    //get list of commands
    public ArrayList<String> getCommands()
    {
        return commands;
    }
    
    //get random time for each command to last
    public int[] getRandomCommandQuantum() {
        //initalize variables
        int count = commands.size()+1;
        double sum;
        java.util.Random g = new java.util.Random();
        int vals[] = new int[count];
        
        //get total time process should execute
        if(process.getRemainingTime() > process.getQuantum())
        {
            sum = process.getQuantum();
        }
        else
        {
            sum = process.getRemainingTime();
        }
        sum *= 1000;
        
        sum -= count;

        //get array of variables
        for (int i = 0; i < count-1; ++i) {
            vals[i] = g.nextInt((int) sum);
        }
        vals[count-1] = (int) sum;

        java.util.Arrays.sort(vals);
        for (int i = count-1; i > 0; --i) {
            vals[i] -= vals[i-1];
        }
        for (int i = 0; i < count; ++i) { ++vals[i]; }

        if(DEBUG)
        { 
            for (int i = 0; i < count; ++i) {
            System.out.printf("%4d", vals[i]);
            }
            System.out.printf("\n");
        }
        
        return vals;
    }
    
    //get thread id
    @Override
    public long getId()
    {
        return name;
    }
    
    //run process
    @Override
    public void run() {

        //initialize variables
        DecimalFormat formatter = new DecimalFormat("#0.0000");  
        IOFile write = new IOFile();     
        int[] commandTime = getRandomCommandQuantum();
        double time = 0;
        Vmem mem = null;
       
        if(DEBUG)
        {
            System.out.println("Thread started: " + name);
        }
        
        
        //run thread for remaining time of process
        while (process.getRemainingTime() > 0) {
            
            try {
                //get time that procese has resumed
                time = messages.take();

                //get virtual memory
                if(virtualMem.isEmpty() == false)
                {
                    mem = virtualMem.take();
                }
                else
                {
                    mem = new Vmem(mem_size, commands);
                    System.out.println("empty vmem " + process.getID());
                }
                if(DEBUG)
                {
                    System.out.println(time);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
             
            //get commands
            commands = mem.getCommands();
        
            //do process until scheduler takes over
            
            if(DEBUG)
            {
                System.out.println("Thread " + name + " running");
            }
            
            //increment time with command time
            time = (time*1000)+commandTime[0];
            
            //do list of commands
            //every time processe gets CPU all commands are done
            //because i can not figure out how to split the commands for all processes
            //and every time it resumes
            for(int k=0; k < commands.size();k++)
            {
                //get a command from list
                String command = commands.get(k);
                String[] splitedCommand = command.split("\\s+");
                
                if(DEBUG)
                {
                    System.out.println(command);
                    System.out.println(splitedCommand[0]);
                }
               
                //if command is store
                if(splitedCommand[0].equals("Store"))
                {
                    //output saying storing is being done
                    output += "Clock: " + formatter.format(time) + ", Process " + (process.getID()+1) + ", Store: Variable "
                            + splitedCommand[1] + ", Value " + splitedCommand[2] + "\n";
                    try {
                        write.writeFile(output);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    output = "";
                    
                    //store given variables
                    try {
                        mem.memStore(Integer.parseInt(splitedCommand[1]), splitedCommand[2], time,mainMemory);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //write to output file
                    try {
                        write.writeFile(output);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    output="";
                }
                
                if(DEBUG)
                {
                    System.out.println(mainMemory[0].print());
                }
                
                //if command is lookup
                if(splitedCommand[0].equals("Lookup"))
                {
                    //write output to file
                    try {
                        write.writeFile(output);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    output = "";
                    
                    //lookup variable in memory
                    String value = "";
                    try {
                        value = mem.memLookup(Integer.parseInt(splitedCommand[1]), time, mainMemory);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //create output
                    output += "Clock: " + formatter.format(time) + ", Process " + (process.getID()+1) + ", Lookup: Variable "
                            + splitedCommand[1] + ", Value " + value + "\n";
                    

                }

                //if command is release
                if(splitedCommand[0].equals("Release"))
                {
                    //create output
                    boolean pass = true;
                    output += "Clock: " + formatter.format(time) + ", Process " + (process.getID()+1) + ", Release: Variable "
                            + splitedCommand[1] + "\n";
                    //release varaible from memory
                    try {
                         pass = mem.memFree(Integer.parseInt(splitedCommand[1]), mainMemory);
                    } catch (IOException ex) {
                        Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //if release failed
                    if(pass == false)
                    {
                        output += "Clock: " + formatter.format(time) + ", Process " + (process.getID()+1) + ", Release: Variable failed\n";
                    }

                }

                //increment time by next command time
                time += commandTime[k+1];
            }
            
            //write output file
            try {
                write.writeFile(output);
            } catch (IOException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //update remaining time of process
            process.updateRemainingTime(process.getQuantum()); 
              
            //return modified virtual memory
            try {
                // need to send back vmem for next process
                virtualMem.put(mem);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //suspend thread
            this.suspend();
        }
    }
}
