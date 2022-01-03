
package rrscheduler;

import java.util.Vector;

/**
 *
 * @author jasen
 */
public class CircularList
{
    private Vector List;
    private int index;

    public CircularList() {
        List = new Vector(10);
        index = 0;
    }

    /**
    * this method returns the next element in the list.
    * @return Object
    */
    public Object getNext() {
        Object nextElement = null;
        int lastElement;

        if (!List.isEmpty() ) {
            if (index == List.size() )
                index = 0;

            nextElement = List.elementAt(index);
            ++index;
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
