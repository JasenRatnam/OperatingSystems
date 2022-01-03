package defectivebulb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jasen Ratnam 
 * 40094237
 * COEN 346, programming assignment 1
 * Due date: February 6th 2019
 * Binary tree traversal to find defective light bulbs using multi-threading
 * This is the main class
 * Need to write a recursive threading method to find the defective bulbs and 
 * the number of threads that have been created for this purpose.
 * 0: indicates the bulb is defective
 * 1: indicates the bulb is functioning properly
 */
public class DefectiveBulb 
{
    //Boolean used when debugging the code, prints information needed
    private static final Boolean DEBUG = false;
    //Array to store the status of the bulbs given in a test file
    private static int[] bulbStatus = null;
    // The number of bulbs given
    private static int numberOfBulbs = 0;
    // COunter for the number of threads
    private static int numberOfThreads = 0;
    //Vector to store location of defective bulbs
    private static Vector<Integer> defectiveBulb = new Vector<Integer>();
    
    /**
     * Function that selects a �pivot� and divides its input array (e.g. bulbstatus)
     * into two sub-arrays. FindDefective is then called recursively in a new
     * thread for both left and right arrays. Both Arrays are traversed concurrently.
     * 0: indicates the bulb is defective
     * 1: indicates the bulb is functioning properly
     * @param bulbstatus            The array of bulbs given by the text file 
     *                              and the sub-arrays in the recursive calls
     * 
     * @param numberOfBulbs         The number of bulbs in the array given above
     * 
     * @param start                 Keeps track of the position of the first 
     *                              element of the array
     * 
     * @throws InterruptedException The threads may cause exceptions that should be catched
     */
    public static void FindDefective(int[] bulbstatus, int numberOfBulbs, int start) throws InterruptedException
    {   
        // Assume that no bulbs in current array is defective
        Boolean isDefective = false;
        
        //check if array has atleast one defective bulb:
        for(int i = 0;i<numberOfBulbs;i++)
        {
            if(bulbstatus[i] == 0)
            {
                isDefective = true;
            }
        }
        
        //if array has more than one bulb and has atleast one defective bulb 
        if(numberOfBulbs > 1 && isDefective)
        {
            //split array in two initialization
            //create two arrays of size defined by pivot
            int pivot = numberOfBulbs/2;
            int[] right = new int[numberOfBulbs - pivot];
            int[] left = new int[pivot];

            //split array in two
            for(int i = 0;i<numberOfBulbs;i++)
            {
                // fill left array
                if(i<left.length)
                {
                    left[i] = bulbstatus[i];
                }
                //fill right array
                else
                {
                    right[i-left.length] = bulbstatus[i];
                }
            }
            
            //DEBUGING
            if(DEBUG)
            {
                //print sub arrays
                System.out.println("---------------------------------------");
                System.out.println("Thread number: " + numberOfThreads);
                System.out.println("Array has at least one defective bulb if true: " + isDefective);
                System.out.println("Right sub array: " + Arrays.toString(right));
                System.out.println("Left sub array: " + Arrays.toString(left));
                System.out.println("The defective bulb is number: " + defectiveBulb);        
                System.out.println("---------------------------------------");
            }
            
             //Initializing thread for left array
            Thread tLeft = new Thread(new Runnable() 
            {
                public void run() 
                {
                    // increment number of threads
                    numberOfThreads++;
                    try {
                        //reecurisve "FindDefective" for left array
                        FindDefective(left,pivot,start);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DefectiveBulb.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            // start the thread for left subarray
            tLeft.start();
            
            //Initializing thread for right array
            Thread tRight = new Thread(new Runnable() 
            {
                public void run() 
                {
                    // increment number of threads
                    numberOfThreads++;
                    try {
                        //reecurisve "FindDefective" for right array
                        FindDefective(right,(numberOfBulbs - pivot),start+pivot);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DefectiveBulb.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            // start the thread for right subarray
            tRight.start();
               
            //Wait till left thread is over
            tLeft.join();
            //Wait till right thread is over
            tRight.join();
        }
        // defective bulb found
        // array has only one element and that element is defective
        else if(numberOfBulbs <= 1 && isDefective)
        {
            //return position of bulb
            System.out.println("Bulb number " + start + " is defective.");
            //add position of defective bulb to vector
            defectiveBulb.add(start);
        }
    }
    
    /**
     * The main function of the class
     * runs the program
     * @param args                      //args sent to main function
     * @throws FileNotFoundException    // catch errors that may come from file reading
     * @throws InterruptedException     // catch exception errors that may happen
     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException 
    {
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
        
        // intialize counter
        int i = 0;
        
        // read input file
        Scanner sc2 = null;
        sc2 = new Scanner(new File(correctFile));
        
        //read every line of file
        while (sc2.hasNextLine()) 
        {
            Scanner s2 = new Scanner(sc2.nextLine());

            while (s2.hasNext()) 
            {
                String number = s2.next();
                // get integer value of the string in the text file
                int intNumber = Integer.parseInt(number);
                
                // first number
                // number of bulbs 
                if(i==0)
                {
                    // save the number of bulbs
                    numberOfBulbs = intNumber;
                    // initialize array of size of number of bulbs
                    bulbStatus = new int[numberOfBulbs];
                }
                else
                {
                    // add status of bulbs to array
                    bulbStatus[i-1]=intNumber;
                }
                
                // increment counter
                i++;
                     
                //DEBUGING
                if(DEBUG)
                {
                    //print words in input file
                    System.out.println("Number read from text file in string: " + number);
                    System.out.println("Number read from text file in integer: " + intNumber);
                }

            }
        }
        
        //Initializing thread to do function FindDefective
        Thread t1 = new Thread(new Runnable() 
        {
            public void run() 
            {
                try {
                    //increment number of threads
                    numberOfThreads++;
                    FindDefective(bulbStatus,numberOfBulbs,1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DefectiveBulb.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        //start the trhead
        t1.start();
          
        //DEBUGGING
        if(DEBUG)
        {
            //print words in input file
            //testing inut reading
            System.out.println("Number of bulbs: " + numberOfBulbs);
            for(int j=0; j<numberOfBulbs;j++)
            {
                System.out.println("Status of bulb " + j + ": " + bulbStatus[j]);
            }
        }
        
        //Wait until the trhead is over
        t1.join();
        
        // print number of threads
        System.out.println("The number of threads for this problem was: " + numberOfThreads + ".");
        
        // print the positions of the defective bulb
        System.out.print("The defective bulbs are in position: ");
        for (int numb : defectiveBulb) 
        {
            System.out.print(numb + ", ");
        }
        System.out.print("\n");
    }
}
  
