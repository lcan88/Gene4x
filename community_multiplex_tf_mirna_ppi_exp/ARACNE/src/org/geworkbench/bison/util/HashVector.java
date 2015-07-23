package org.geworkbench.bison.util;

import java.util.HashMap;
import java.util.Vector;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @todo - watkin - This class extends {@link HashMap}, but it does not override many of the required methods. This
 * class is therefore incomplete.
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class HashVector <K,V> extends HashMap<K, Vector<V>> {
    /**
     * AM -
     *
     * This will enforce that we don't put in duplicate items for a key. Would be more elegant
     * to use the HashHashSet but I think creating so many HashSets is too heavy weight since we
     * create so many of them. We lose a little bit of speed here but I don't think it's too bad
     * because the Vectors here contain very few objects.
     *
     * watkin -
     *
     * Why is a Vector "lightweight" and a HashSet "heavyweight"?
     * I find this implementation to be prohibitively slow. Linear search is unacceptable for an 'add' operation.
     * Adding 50,000 markers to a CSMarkerVector takes many minutes.
     * I think there are two potentital solutions for this:
     * 1) Do a full, proper map implementation backed by a Set rather than by a Vector.
     * 2) Don't enforce uniqueness at this low level.
     */
    boolean uniqueItems = false;

    public HashVector() {
        super();
    }

    public HashVector(boolean uniqueItems) {
        super();
        this.uniqueItems = uniqueItems;
    }


    public void addItem(K key, V item) {
        Vector<V> values = get(key);
        if (values == null) {
            values = new Vector<V>();
            put(key, values);
        }
        if (!(uniqueItems && values.contains(item))) {
            values.add(item);
        }
    }

    public int getNumValues(K key) {
        Vector<V> values = get(key);
        if (values != null) {
            return values.size();
        }
        return 0;
    }

    public V getValue(K key, int index) {
        Vector<V> values = get(key);
        if ((values != null) && (values.size() > index)) {
            return values.get(index);
        }
        return null;
    }

}
