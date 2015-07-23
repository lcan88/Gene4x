package org.geworkbench.bison.util;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */

public class ArrayIterator <T> implements Iterator<T> {
    private final int size;
    private int cursor;
    private final Object array;

    public ArrayIterator(T[] array) {
        this.array = (T[]) array;
        this.size = Array.getLength(array);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return (cursor < size);
    }

    public T next() {
        return (T) Array.get(array, cursor++);
    }
}
