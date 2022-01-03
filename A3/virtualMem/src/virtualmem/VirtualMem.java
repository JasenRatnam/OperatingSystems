package virtualmem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 3
 * Due date: April 5th 2020
 * Implement the simulation of a process scheduler that is responsible for
 * scheduling a given list of processes. The scheduler is running on a machine 
 * with two CPU, and simulate Virtual Memory Management
 */
public class VirtualMem 
{
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
   
    /**
     * Main method to start scheduling
     * @param args  
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {    
        //display quantum
        System.out.println("Please try again if a thread error happens.");
        System.out.println("Quantum is of 3000ms.");
        
        //delete output and vm file for new outputs
        File output= new File("output.txt");   
        File vm= new File("vm.txt");   
        output.delete();
        vm.delete();
        
        //initializes variables
        ArrayList<Process> S_processes = new ArrayList();
        ArrayList<String> commands = new ArrayList();
        int num_frames_main_memory = 0;
        
        //create IO class to read input files
        IOFile read = new IOFile();
        
        //read input file and create processes arraylist
        S_processes = read.readFile();
        
        //get list of commands
        commands = read.getCommands();
        
        //make main thread have most priority
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        // Create blocking queues to send messages and vmem between threads
        BlockingQueue<Double> messages = new ArrayBlockingQueue<Double>(2);
        BlockingQueue<Vmem> vmem = new ArrayBlockingQueue<Vmem>(2);
        
        //create scheduler
        Scheduler scheduler = new Scheduler(messages,vmem,commands,num_frames_main_memory);
        
        //give scheduler the processes
        scheduler.setProcesses(S_processes);
        
        //get the number of frames that the main memory should have
        //size of main memory
        num_frames_main_memory = read.getNumbOfMemFrames();
        
        //start scheduling thread
        scheduler.start();
       
        //create threads for all processes
        for(Process process: S_processes)
        {
            //create thread of a process
            MyThread t1 = new MyThread(process.getID(),process,commands,num_frames_main_memory, messages, vmem);
            t1.start();
            t1.suspend();
            //add processe to the scheduler
            scheduler.addThread(t1);
        }
        
        System.out.println("Running....");
    }
}