/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rrscheduler;

/**
 *
 * @author jasen
 */
public class Scheduler extends Thread
{
    private CircularList queue;

    private int timeSlice;

    private static final int DEFAULT_TIME_SLICE = 1000; // 1 second

    public Scheduler() {

        timeSlice = DEFAULT_TIME_SLICE;

        queue = new CircularList();
    }

    public Scheduler(int quantum) {

        timeSlice = quantum;

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

    * this method puts the scheduler to sleep for a time quantum

    */

    private void schedulerSleep() {

        try {

            Thread.sleep(timeSlice);

        } catch (InterruptedException e) { };

    }

    public void run() {

        //initialize 
        Thread current;

        // set the priority of the scheduler to the highest priority
        this.setPriority(6);

        while (true) {

            try {
                //get next process
                current = (Thread)queue.getNext();
                
                if ( (current != null) && (current.isAlive()) ) {
                    //give process to next thread
                    current.setPriority(4);
                    //make scheduler go to sleep
                    //will retake control after quantum
                    schedulerSleep();
                    System.out.println("* * * Context Switch * * * " + this.getName());
                    System.out.println(current.getName());
                    //give process less priority
                    current.setPriority(2);
            }

            } catch (NullPointerException e3) { } ;

        }
    }
}
