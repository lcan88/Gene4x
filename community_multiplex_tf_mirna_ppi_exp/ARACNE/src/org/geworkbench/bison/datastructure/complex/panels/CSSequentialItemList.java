package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSSequential;

/**
 * An item list that maintains a list of items in which each item is aware of its own position in the list.
 */
public class CSSequentialItemList<T extends DSSequential> extends CSItemList<T> {

    /**
     * Create a new CSSequentialItemList.
     */
    public CSSequentialItemList() {
    }

    /**
     * Adds the item to the sequential item list.
     *
     * @param item the item to add.
     * @return <code>true</code> always.
     */
    @Override
    public boolean add(T item) {
        item.setSerial(size());
        return super.add(item);
    }

    /**
     * Adds the item to the sequential item list.
     * watkin - note: this violates the contract for List-- it does not add if index < size(), but replaces!
     *
     * @param index the index at which to insert the item, 0 <= index <= size();
     * @param item  the item to insert.
     */
    @Override
    public void add(int index, T item) {
        String label = item.getLabel();
        boolean exists = index < size();
        super.add(index, item);
        item.setSerial(index);
        // Todo - watkin - Since this method does not actually insert, we should revisit this. 
        if (!exists) {
            // Update the serial for items that were pushed to the right by the insert
            for (int i = index + 1; i < size(); i++) {
                get(i).setSerial(i);
            }
        }
    }

    /**
     * Removes the given item from the item list.
     *
     * @param item the item to remove.
     * @return <code>true</code> if the item was found and removed, <code>false</code> if the item was not found.
     */
    @Override
    public boolean remove(Object item) {
        T removedItem = this.get(((T) item).getLabel());
        if (removedItem != null) {
            int removedSerial = removedItem.getSerial();
            super.remove(removedItem);
            // objectMap.remove(removedItem.getLabel());
            for (int i = removedSerial; i < size(); i++) {
                get(i).setSerial(i);
            }
            return true;
        } else {
            return false;
        }
    }

}
