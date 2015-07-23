package org.geworkbench.util;

import javax.swing.*;
import java.util.ArrayList;

/**
 * A straightforward, list-backed list model, useful for the JAutoList class.
 * Do not change the list directly, but call the provided methods in this class instead.  
 *
 * @author John Watkinson
 */
public class AutoListModel<T> extends AbstractListModel {

    private ArrayList<T> items;

    public AutoListModel(ArrayList<T> items) {
        this.items = items;
    }

    public int getSize() {
        return items.size();
    }

    public Object getElementAt(int index) {
        return items.get(index);
    }

    /**
     * Add item to the end of the list
     *
     * @param item
     */
    public void addItem(T item) {
        items.add(item);
        int n = items.size() - 1;
        fireIntervalAdded(this, n, n);
    }

    public T removeItem(int index) {
        T item = items.remove(index);
        fireIntervalRemoved(this, index, index);
        return item;
    }

}
