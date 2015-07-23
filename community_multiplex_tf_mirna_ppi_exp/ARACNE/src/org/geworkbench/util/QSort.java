package org.geworkbench.util;

import java.util.Vector;

/*
Copyright @ 1999-2003, The Institute for Genomic Research (TIGR).
All rights reserved.
*/

public class QSort {

    private int[] origIndx;
    private float[] sorted;
    private double[] sortedDouble;
    private int[] NaNIndices;
    private int[] negInfinityIndices;
    public static final int ASCENDING = 1;
    public static final int DESCENDING = 2;
    private boolean ascending;

    public QSort(float[] origA) {
        float[] copyA = new float[origA.length];
        this.ascending = true;
        Vector NaNIndicesVector = new Vector();
        Vector negInfinityIndicesVector = new Vector();

        for (int i = 0; i < copyA.length; i++) {
            copyA[i] = origA[i];
            if (Float.isNaN(origA[i])) {
                NaNIndicesVector.add(new Integer(i));
                copyA[i] = Float.NEGATIVE_INFINITY; // so that NaN's are always first when the array is sorted in ascending order
            }
            if (Float.isInfinite(origA[i]) && (origA[i] < 0)) { // in case there are actual negative infinity values in origA
                negInfinityIndicesVector.add(new Integer(i));
            }
        }


        NaNIndices = new int[NaNIndicesVector.size()];

        for (int i = 0; i < NaNIndices.length; i++) {
            NaNIndices[i] = ((Integer) (NaNIndicesVector.get(i))).intValue();
        }

        negInfinityIndices = new int[negInfinityIndicesVector.size()];

        for (int i = 0; i < negInfinityIndices.length; i++) {
            negInfinityIndices[i] = ((Integer) (negInfinityIndicesVector.get(i))).intValue();
        }

        sort(copyA);
    }

    public QSort(double[] origA) {
        double[] copyA = new double[origA.length];
        this.ascending = true;
        Vector NaNIndicesVector = new Vector();
        Vector negInfinityIndicesVector = new Vector();

        for (int i = 0; i < copyA.length; i++) {
            copyA[i] = origA[i];
            if (Double.isNaN(origA[i])) {
                NaNIndicesVector.add(new Integer(i));
                copyA[i] = Double.NEGATIVE_INFINITY; // so that NaN's are always first when the array is sorted in ascending order
            }
            if (Double.isInfinite(origA[i]) && (origA[i] < 0)) { // in case there are actual negative infinity values in origA
                negInfinityIndicesVector.add(new Integer(i));
            }
        }

        NaNIndices = new int[NaNIndicesVector.size()];

        for (int i = 0; i < NaNIndices.length; i++) {
            NaNIndices[i] = ((Integer) (NaNIndicesVector.get(i))).intValue();
        }

        negInfinityIndices = new int[negInfinityIndicesVector.size()];

        for (int i = 0; i < negInfinityIndices.length; i++) {
            negInfinityIndices[i] = ((Integer) (negInfinityIndicesVector.get(i))).intValue();
        }

        sort(copyA);
    }

    public QSort(float[] origA, int ascOrDesc) {
        this(origA);
        if (ascOrDesc == QSort.ASCENDING) {
            this.ascending = true;
        } else if (ascOrDesc == QSort.DESCENDING) {
            this.ascending = false;
        }
    }

    public QSort(double[] origA, int ascOrDesc) {
        this(origA);
        if (ascOrDesc == QSort.ASCENDING) {
            this.ascending = true;
        } else if (ascOrDesc == QSort.DESCENDING) {
            this.ascending = false;
        }
    }

    public void sort(float a[]) {

        origIndx = new int[a.length];
        for (int i = 0; i <= origIndx.length - 1; i++) {
            origIndx[i] = i;
        }
        quickSort(a, 0, a.length - 1);
    }

    public void sort(double a[]) {

        origIndx = new int[a.length];
        for (int i = 0; i <= origIndx.length - 1; i++) {
            origIndx[i] = i;
        }
        quickSort(a, 0, a.length - 1);
    }

    void quickSort(float a[], int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        float mid;

        if (hi0 > lo0) {

            /* Arbitrarily establishing partition element as the midpoint of
             * the array.
             */
            mid = a[(lo0 + hi0) / 2];

            // loop through the array until indices cross
            while (lo <= hi) {
                /* find the first element that is greater than or equal to
                 * the partition element starting from the left Index.
                 */
                while ((lo < hi0) && (a[lo] < mid))
                    ++lo;

                /* find an element that is smaller than or equal to
                 * the partition element starting from the right Index.
                 */
                while ((hi > lo0) && (a[hi] > mid))
                    --hi;

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            /* If the right index has not reached the left side of array
             * must now sort the left partition.
             */
            if (lo0 < hi)
                quickSort(a, lo0, hi);

            /* If the left index has not reached the right side of array
             * must now sort the right partition.
             */
            if (lo < hi0)
                quickSort(a, lo, hi0);

        }
        sorted = a;
    }

    void quickSort(double a[], int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        double mid;

        if (hi0 > lo0) {

            /* Arbitrarily establishing partition element as the midpoint of
             * the array.
             */
            mid = a[(lo0 + hi0) / 2];

            // loop through the array until indices cross
            while (lo <= hi) {
                /* find the first element that is greater than or equal to
                 * the partition element starting from the left Index.
                 */
                while ((lo < hi0) && (a[lo] < mid))
                    ++lo;

                /* find an element that is smaller than or equal to
                 * the partition element starting from the right Index.
                 */
                while ((hi > lo0) && (a[hi] > mid))
                    --hi;

                // if the indexes have not crossed, swap
                if (lo <= hi) {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }

            /* If the right index has not reached the left side of array
             * must now sort the left partition.
             */
            if (lo0 < hi)
                quickSort(a, lo0, hi);

            /* If the left index has not reached the right side of array
             * must now sort the right partition.
             */
            if (lo < hi0)
                quickSort(a, lo, hi0);

        }
        sortedDouble = a;
    }

    private void swap(float a[], int i, int j) {

        float T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;

        int TT = origIndx[i];
        origIndx[i] = origIndx[j];
        origIndx[j] = TT;
    }

    private void swap(double a[], int i, int j) {

        double T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;

        int TT = origIndx[i];
        origIndx[i] = origIndx[j];
        origIndx[j] = TT;
    }

    public float[] getSorted() {
        /*
        Vector sortedVector = new Vector();
        Vector NaNSortedIndices = new Vector();

        for (int i = 0; i < sorted.length; i++) {
            if (Float.isNaN(sorted[i])) {
                NaNSortedIndices.add(new Integer(i));
            } else {
                sortedVector.add(new Float(sorted[i]));
            }
        }

        for (int i = 0; i < NaNSortedIndices.size(); i++) {
            sortedVector.add(0, new Float(Float.NaN));
        }
        for (int i = 0; i < sortedVector.size(); i++) {
            sorted[i] = ((Float)(sortedVector.get(i))).floatValue();
        }
         */
        for (int i = 0; i < NaNIndices.length; i++) {
            sorted[i] = Float.NaN;
        }
        if (!ascending) {
            float[] revSorted = reverse(sorted);
            return revSorted;
        } else {
            return sorted;
        }
        //return sorted;
    }

    public double[] getSortedDouble() {
        /*
        Vector sortedVector = new Vector();
        Vector NaNSortedIndices = new Vector();

        for (int i = 0; i < sortedDouble.length; i++) {
            if (Double.isNaN(sortedDouble[i])) {
                NaNSortedIndices.add(new Integer(i));
            } else {
                sortedVector.add(new Double(sortedDouble[i]));
            }
        }

        for (int i = 0; i < NaNSortedIndices.size(); i++) {
            sortedVector.add(0, new Double(Double.NaN));
        }
        for (int i = 0; i < sortedVector.size(); i++) {
            sortedDouble[i] = ((Double)(sortedVector.get(i))).doubleValue();
        }
         */
        for (int i = 0; i < NaNIndices.length; i++) {
            sortedDouble[i] = Double.NaN;
        }

        if (!ascending) {
            double[] revSortedDouble = reverse(sortedDouble);
            return revSortedDouble;
        } else {
            return sortedDouble;
        }
    }

    public int[] getOrigIndx() {
        /*
        Vector origIndxVector = new Vector();
        for (int i = 0; i < origIndx.length; i++) {
            if (!isNaNIndex(origIndx[i])) {
                origIndxVector.add(new Integer(origIndx[i]));
            }
        }

        for (int i = 0; i < NaNIndices.length; i++) {
            origIndxVector.add(0, new Integer(NaNIndices[i]));
        }

        for (int i = 0; i < origIndxVector.size(); i++) {
            origIndx[i] = ((Integer)(origIndxVector.get(i))).intValue();
        }
         */

        for (int i = 0; i < NaNIndices.length; i++) {
            origIndx[i] = NaNIndices[i];
        }

        for (int i = NaNIndices.length; i < NaNIndices.length + negInfinityIndices.length; i++) {
            origIndx[i] = negInfinityIndices[i - NaNIndices.length];
        }

        if (!ascending) {
            return reverse(origIndx);
        } else {
            return origIndx;
        }
    }

    private boolean isNaNIndex(int index) {
        for (int i = 0; i < NaNIndices.length; i++) {
            if (index == NaNIndices[i]) {
                return true;
            }
        }

        return false;
    }

    private int[] reverse(int[] arr) {
        int[] revArr = new int[arr.length];
        int revCount = 0;
        int count = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            revArr[revCount] = arr[count];
            revCount++;
            count--;
        }
        return revArr;
    }

    private float[] reverse(float[] arr) {
        float[] revArr = new float[arr.length];
        int revCount = 0;
        int count = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            revArr[revCount] = arr[count];
            revCount++;
            count--;
        }
        return revArr;
    }

    private double[] reverse(double[] arr) {
        double[] revArr = new double[arr.length];
        int revCount = 0;
        int count = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            revArr[revCount] = arr[count];
            revCount++;
            count--;
        }
        return revArr;
    }

    public static void main(String[] args) {

        double[] arr = {120d, 0.01d, -4.5d, Double.NaN, 7.6d, -65d, Double.NEGATIVE_INFINITY, 3.5d, -0.95d, Double.POSITIVE_INFINITY, 600d, Double.NaN, 65d, Double.NEGATIVE_INFINITY, Double.MAX_VALUE};

        QSort sortArr = new QSort(arr, QSort.ASCENDING);
        double[] sortedArr = sortArr.getSortedDouble();
        int[] sortedArrIndices = sortArr.getOrigIndx();
        for (int i = 0; i < sortedArr.length; i++) {
            System.out.println("arr[" + i + "] = " + arr[i] + ", sortedArr[" + i + "] = " + sortedArr[i] + ",  sortedArrIndices[" + i + "] = " + sortedArrIndices[i]);
        }
    }
}
