package roundrobin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 2
 * Due date: March 9th 2020
 * Class to read/write a file
 */
public class IOFile 
{
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    // Array of the arrival time of each process
    private static ArrayList<Integer> arrival = new ArrayList(); 
    // Array of the burst_time of each process
    private static ArrayList<Integer> burst_time = new ArrayList();
    // count number od processes
    private static int num_processe = 0;
    
    public ArrayList<Process> S_processes = new ArrayList();
    
    /*
    * Read input file 
    * Saves the arrival time and burst time of each process and counts the 
    * number of processes
    * Create process objects with their characteristics
    */
    public ArrayList<Process> readFile() throws FileNotFoundException
    { 
        int i = 0;
        
        //Get text file with number of bulbs and their status
        // ask for input file name
        System.out.println("Please enter the name of input text file: ");
        Scanner keyboard = new Scanner(System.in);
        // get filename from keyboard
        String filename = keyboard.nextLine();
        String correctFile;
        // add .txt to end of the file name if not already there
        if(filename.endsWith(".txt"))
        {
            correctFile = filename;
        }
        else
        {
            correctFile = filename + ".txt";
        }
        
        // open text file
        File f = new File(correctFile);
        
        // keep asking for input file name until file is found
        while(!f.exists())
        {
            System.out.println("ERROR: Please enter the name of input text file: ");
            keyboard = new Scanner(System.in);
            filename = keyboard.nextLine();
            
            // add .txt to end of the file name if not already there
            if(filename.endsWith(".txt"))
            {
                correctFile = filename;
            }
            else
            {
                correctFile = filename + ".txt";
            }
            
            f = new File(correctFile);
        }
        
        // read input file
        Scanner sc2 = null;
        try{
            sc2 = new Scanner(new File(correctFile));
        } catch (Exception ex){
            System.out.println("Can not open file.");
        }
        
        //read every line of file
        while (sc2.hasNextInt()) 
        {
            int number = sc2.nextInt();
            
            if(i%2 == 0)
            {
                //save arrival times of process
                arrival.add(number);
                num_processe++;
            }
            else
            {
                //save burst time of process
                burst_time.add(number);
            }
            i++;
            
        }
        
        S_processes=processes(arrival, burst_time);
       
        //DEBUGING
        if(DEBUG)
        {
            System.out.println("DEBUG");
            System.out.println("Arrival time: " + arrival);
            System.out.println("Burst Time: " + burst_time);
            System.out.println("Number of processes: " + num_processe);
        }
        return S_processes;
    }
    
    public ArrayList<Process> getProcesses()
    {
        return S_processes;
    }
    
    /*
    * create processes from the inputed arrival times and burst times
    * outputs an array of processes
    */
    public static ArrayList<Process> processes(ArrayList<Integer> arrivalTime, ArrayList<Integer> burst_time)
    {
        ArrayList<Process> S_processes = new ArrayList();
    
        int i = 0;
        for(double arrival : arrivalTime)
        {
            //create process
            Process newP = new Process(i, burst_time.get(i), arrival);
            //add process to arraylist
            S_processes.add(newP);
            i++;
             //DEBUGING
            if(DEBUG)
            {
                System.out.println("DEBUG");
                System.out.println("Process " + i + " created");
            }
        }
        
        return S_processes;
    }
    
    /*
    * write output file from given text
    */
    public void writeFile(String text) throws FileNotFoundException, IOException
    { 
        try {
            //create output file
            FileWriter outputWriter = new FileWriter("output.txt");
            // write to file
            outputWriter.write(text);
            //close file
            outputWriter.close();
             //DEBUGING
            if(DEBUG)
            {
                System.out.println("DEBUG");
                System.out.println("Successfully wrote to the file.");
            }
           
          } catch (IOException e) {
            System.out.println("An error occurred. While writing file");
            e.printStackTrace();
          }
    }
    
    /*
    * getter method for arrival time array
    */
    public ArrayList<Integer> getArrivalTime()
    {
        return arrival;
    }
    
    /*
    * getter method for number of processes 
    */
    public int getNumbOfProcess()
    {
        return num_processe;
    }
    
    /*
    * getter method for burst time array
    */
    public ArrayList<Integer> getBurstTime()
    {
        return burst_time;
    }
}
