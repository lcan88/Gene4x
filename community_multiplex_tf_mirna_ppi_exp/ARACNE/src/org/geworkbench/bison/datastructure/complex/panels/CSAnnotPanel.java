package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSAnnotPanel <T extends DSNamed, U extends Comparable> extends CSPanel<T> implements DSPanel<T>, DSAnnotatedPanel<T, U> {
    class comparator <T> implements Comparator<T> {
        public int compare(T o1, T o2) {
            U x = objects.get(o1);
            U y = objects.get(o2);
            return x.compareTo(y);
        }
    }

    protected HashMap<T, U> objects = new HashMap<T, U>();
    boolean needsSorting = false;

    public CSAnnotPanel(String aString) {
        super(aString);
    }

    public boolean add(T t, U u) {
        boolean result = super.add(t);
        if (result) {
            objects.put(t, u);
            needsSorting = true;
        }
        return result;
    }

    public U getObject(T item) {
        if (needsSorting) {
            sort();
        }
        return objects.get(item);
    }

    public T get(int i) {
        return super.get(i);
    }

    public U getObject(int i) {
        if (needsSorting) {
            sort();
        }
        return objects.get(i);
    }

    public void sort() {
        Collections.sort(this, new comparator());
        needsSorting = false;
    }

    public DSItemList<DSPanel<T>> panels() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DSPanel<T> getPanel(T t) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DSPanel<T> getSelection() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDirty() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
