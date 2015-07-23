package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * A default {@link DSItemList} implementation that is backed by an ArrayList and a HashMap.
 */
public class CSItemList <T extends DSNamed> extends ArrayList<T> implements DSItemList<T> {

    String id = RandomNumberGenerator.getID();
    //use Hashtable to not allow null keys
    protected Hashtable<String, T> objectMap = new Hashtable<String, T>();

    public CSItemList() {
    }

    /**
     * Gets an item by label, using the HashMap.
     *
     * @param label the label of the item.
     * @return the item found or <code>null</code> if not found.
     */
    public T get(String label) {
        if (label == null) {
            return null;
        }

        T object = objectMap.get(label);
        if (object != null) {
            return object;
        } else {
            return null;
        }
    }

    /**
     * Adds a new item to the item list, if it does not already exist.
     *
     * @param item the item to add.
     * @return <code>true</code> always.
     */
    @Override public boolean add(T item) {
        boolean result = false;
        if (item != null) {
            if (!this.contains(item)) {
                result = super.add(item);
                if (result) {
                    String label = item.getLabel();
                    if (label != null) {
                        objectMap.put(label, item);
                    }
                }
            }
        }
        return result;
    }

    @Override public boolean addAll(Collection<? extends T> ts) {
        boolean success = true;
        for (Iterator<? extends T> iterator = ts.iterator(); iterator.hasNext();) {
            if (!add(iterator.next())) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Inserts the item at the specified index.
     * watkin - note: this violates the contract for List-- it does not add if index < size(), but replaces!
     *
     * @param index the index at which to insert the item.
     * @param item  the item to insert.
     */
    @Override public void add(int index, T item) {
        if (size() > index) {
            super.set(index, item);
        } else {
            super.add(index, item);
        }
        String label = item.getLabel();
        if (label != null) {
            objectMap.put(label, item);
        }
    }

    /**
     * Gets an item by item template. Only the {@link org.geworkbench.bison.datastructure.properties.DSNamed#getLabel()} method
     * is called.
     *
     * @param item the template item.
     * @return the full item, or <code>null</code> if it was not found.
     */
    public T get(T item) {
        String label = item.getLabel();
        if (label == null) {
            return null;
        } else {
            return objectMap.get(label);
        }
    }

    /**
     * Determines if T is a member of this item list.
     *
     * @param item the item to check for membership.
     * @return <code>true</code> if the item is in the item list, <code>false</code> otherwise.
     */
    boolean contains(T item) {
        String label = item.getLabel();
        if (label == null) {
            return false;
        } else {
            return objectMap.containsKey(label);
        }
    }

    /**
     * Removes the object from the item list.
     *
     * @param item the item to remove
     * @return <code>true</code> if the item was found and removed, <code>false</code> if it was not found.
     */
    @Override public boolean remove(Object item) {
        boolean result = super.remove(item);
        objectMap.remove(((T) item).getLabel());
        return result;
    }

    /**
     * Gets the ID for this object.
     *
     * @return the id of this item list.
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the ID for this object.
     *
     * @param id the new id for this item list.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Clears the contents of this item list.
     */
    @Override public void clear() {
        super.clear();
        objectMap.clear();
    }

    public void rename(T t, String label) {
        T other = objectMap.get(t.getLabel());
        if (t != other) {
            throw new RuntimeException("Item not found: " + t);
        }
        objectMap.remove(t.getLabel());
        t.setLabel(label);
        objectMap.put(label, t);
    }

    public boolean equals(Object o) {
        if (o instanceof DSItemList) {
            DSItemList other = (DSItemList) o;
            return id.equals(other.getID());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return id.hashCode();
    }
}
