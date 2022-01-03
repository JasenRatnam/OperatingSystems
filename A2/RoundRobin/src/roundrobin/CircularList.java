
package roundrobin;

import java.util.Vector;

/* 
* This class was gotten from the Applied Operating Systems Concepts book
* and modified for my use
* @author Greg Gagne, Peter Galvin, Avi Silberschatz
* @version 1.0 - July 15, 1999.
* Copyright 2000 by Greg Gagne, Peter Galvin, Avi Silberschatz
* Applied Operating Systems Concepts - John Wiley and Sons, Inc.
*/
public class CircularList 
{
    private Vector List;
    private int index;

    //constructor of list
    public CircularList() {
        List = new Vector();
        index = 0;
    }
    
    //find process with given index
    public Object getProcess(int i) {
        Object nextElement = null;

        if (!List.isEmpty() ) {
            nextElement = List.elementAt(i);
        }

        return nextElement;
    }

    /**
    * this method adds an item to the list
    * @return void
    */
    public void addItem(Object t) {
        List.addElement(t);
    }
}
