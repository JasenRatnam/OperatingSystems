
package rrscheduler;

/**
 *
 * @author jasen
 */
class TestThread extends Thread
{
    private String name;

    public TestThread(String id) {
        name = id;
    }

    public void run() {
        /*
        * The thread does something
        **/
        while (true) {
        //for (int i = 0; i < 500000; i++);
        //System.out.println("I am thread " + name);
        }
    }   
}
