package virtualmem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 3
 * Due date: April 5th 2020
 * Class to read/write a file
 */
public class IOFile 
{
    // initalize variables
    // Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    // Array of the arrival time of each process
    private static ArrayList<Integer> arrival = new ArrayList(); 
    // Array of the burst_time of each process
    private static ArrayList<Integer> burst_time = new ArrayList();
    // count number od processes
    private static int num_processe = 0;
    // main memory size
    private static int num_frames_main_memory= 0;
    // list of commands
    private static ArrayList<String> commands = new ArrayList<String>();
    // number of commands
    private static int num_commands = 0;
    // list of processes
    public ArrayList<Process> S_processes = new ArrayList();
    
    /*
    * Read input file 
    * Saves the arrival time and burst time of each process and counts the 
    * number of processes
    * Create process objects with their characteristics
    * @return
    * @throws FileNotFoundException
    */
    public ArrayList<Process> readFile() throws FileNotFoundException
    { 
        int i = 0;
        
        // Get text file with n processes
        // ask for input file name
        System.out.println("Please enter the name of processes text file: ");
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
            System.out.println("ERROR: Please enter the name of processes text file: ");
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
        
        boolean first = true;
        //read every line of file
        while (sc2.hasNextInt()) 
        {
            int number = sc2.nextInt();
            
            if(first)
            {
                first = false;
                num_processe = number;
                i--;
            }
            else if(i%2 == 0)
            {
                //save arrival times of process
                arrival.add(number);
            }
            else
            {
                //save burst time of process
                burst_time.add(number);
            }
            i++;
            
        }
        
        //create list of processes
        S_processes=processes(arrival, burst_time);
        
        // Get text file with main memory size
        // ask for input file name
        System.out.println("Please enter the name of memory configuration text file: ");
        keyboard = new Scanner(System.in);
        // get filename from keyboard
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
        
        // open text file
        f = new File(correctFile);
        
        // keep asking for input file name until file is found
        while(!f.exists())
        {
            System.out.println("ERROR: Please enter the name of memory configuration text file: ");
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
        sc2 = null;
        try{
            sc2 = new Scanner(new File(correctFile));
        } catch (Exception ex){
            System.out.println("Can not open file.");
        }
        
        //read every line of file
        while (sc2.hasNextInt()) 
        {
            int number = sc2.nextInt();
            
            num_frames_main_memory = number;
        }
        
         // Get text file with commands
        // ask for input file name
        System.out.println("Please enter the name of commands text file: ");
        keyboard = new Scanner(System.in);
        // get filename from keyboard
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
        
        // open text file
        f = new File(correctFile);
        
        // keep asking for input file name until file is found
        while(!f.exists())
        {
            System.out.println("ERROR: Please enter the name of commands text file: ");
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
        sc2 = null;
        try{
            sc2 = new Scanner(new File(correctFile));
        } catch (Exception ex){
            System.out.println("Can not open file.");
        }
        
        String command = "";
        //read every line of file
        while (sc2.hasNextLine()) 
        {
            command = sc2.nextLine();
            num_commands++;
            commands.add(command);
        }
        
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
    
    /**
     * get list of processes
     * @return
     */
    public ArrayList<Process> getProcesses()
    {
        return S_processes;
    }
    
    /**
     * get number of processes
     * @return
     */
    public int getNumbOfCommands()
    {
        return num_commands;
    }
    
    /**
     * get list of commands
     * @return
     */
    public ArrayList<String> getCommands()
    {
        return commands;
    }
    
    /*
    * create processes from the inputed arrival times and burst times
    * outputs an array of processes
    * @param arrivalTime
    * @param burst_time
    * @return
    */
    public static ArrayList<Process> processes(ArrayList<Integer> arrivalTime, ArrayList<Integer> burst_time)
    {
        //initialize list of process
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
    * append output file with given text
    * @param text
    * @throws FileNotFoundException
    * @throws IOException
    */
    public void writeFile(String text) throws FileNotFoundException, IOException
    { 
        try {
            //create buffer to apend to a file
            BufferedWriter output;
            output = new BufferedWriter(new FileWriter("output.txt", true));
            output.append(text);
            output.close();
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
    
    /**
     * add a page to the disk text file
     * @param newPage
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void vmPages(page newPage) throws FileNotFoundException, IOException
    {
        // append page to vm.txt
        BufferedWriter output;
        output = new BufferedWriter(new FileWriter("vm.txt", true));
        output.append(newPage.print());
        output.newLine();
        output.close();
    } 
    
    /**
     * remove a variable from the disk
     * @param variableId
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public page vmPagesFree(int variableId) throws FileNotFoundException, IOException
    {
        //initialize variables
        page newPage = null; 
        File inputFile = new File("vm.txt");
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        FileWriter writer;
        String lineToRemove = Integer.toString(variableId);
        String currentLine;

        String Content = "";
        currentLine = reader.readLine();
        
        //rewrite file without the variable
        while(currentLine != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            String[] trimmedLineSplit = trimmedLine.split("\\s+");
            if(trimmedLineSplit[0].equals(lineToRemove))
            {
                //skip line
                newPage = new page(Integer.parseInt(trimmedLineSplit[0]), trimmedLineSplit[1], Double.parseDouble(trimmedLineSplit[2]));
                currentLine = reader.readLine();
                continue;
            }
            Content = Content + currentLine + System.lineSeparator();
            currentLine = reader.readLine();
        }
        
        //overwrite disk
        writer = new FileWriter(inputFile);
        writer.write(Content);
        
        writer.close(); 
        reader.close(); 
        return newPage;
    }
    
    /**
     * search disk for a variable
     * @param variableId
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean vmSearch(int variableId) throws FileNotFoundException, IOException
    {
        File inputFile = new File("vm.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));

        String lineToRemove = Integer.toString(variableId);
        String currentLine;

        //search disk
        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            String[] trimmedLineSplit = trimmedLine.split("\\s+");
            if(trimmedLineSplit[0].equals(lineToRemove)) 
            {
                //varaible found in disk
                return true;
            }
        }
        reader.close(); 
        return false;
    }
    
    /*
    * getter method for arrival time array
    * @return
    */
    public ArrayList<Integer> getArrivalTime()
    {
        return arrival;
    }
    
    /*
    * getter method for number of processes 
    * @return
    */
    public int getNumbOfProcess()
    {
        return num_processe;
    }
    
    /*
    * getter method for number of frames in main memory
    * @return
    */
    public int getNumbOfMemFrames()
    {
        return num_frames_main_memory;
    }
    
    /*
    * getter method for burst time array
    * @return
    */
    public ArrayList<Integer> getBurstTime()
    {
        return burst_time;
    }
}
