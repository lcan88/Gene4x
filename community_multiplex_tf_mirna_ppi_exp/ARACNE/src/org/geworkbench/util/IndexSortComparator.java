package org.geworkbench.util;

import java.util.Comparator;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: comparator to be used to sort an array by its index, i.e.,
 * the original array is not changed.</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Frank Wei Guo
 * @version 3.0
 */

public class IndexSortComparator implements Comparator<Integer> {
    private double[] value; // The underlying array to be sorted


    /**
     * IndexSortComparator
     *
     * @param value The array of type <code>double[]</code> to be sorted.
     */
    public IndexSortComparator(double[] value) {
        this.value = value;
    }

    /**
     * Compares its two arguments for order.
     *
     * @param i the first array index.
     * @param j the second array index.
     * @return a negative integer, zero, or a positive integer as the
     *         <code>value[i]</code> is less than, equal to, or greater than
     *         <code>value[j]</code>.
     * @todo Implement this java.bisonparsers.Comparator method
     */
    public int compare(Integer i, Integer j) {
        double d = value[i] - value[j];
        if (d < 0) return -1;
        if (d > 0) return 1;
        return 0;
    }
}
