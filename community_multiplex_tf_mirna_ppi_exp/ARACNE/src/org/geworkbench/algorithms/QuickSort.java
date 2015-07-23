package org.geworkbench.algorithms;


/**
 * file :  QuickSort.java
 * desc :  Implementation of QuickSort for on arrays of simple ints
 * <p/>
 * References
 * ----------
 * [1] T. Standish.  ``Data Structures in Java.''  p. 386.
 */


public class QuickSort {

    /**
     * post :  elements of A are rearranged so that they
     * are sorted in ascending order, i.e.,
     * <p/>
     * A[0] <= A[1] <= ... <= A[A.length-1]
     */
    public static void sort(int[] A) {
        quickHelper(A, 0, A.length - 1);
    }


    /**
     * pre :  0 <= p <= r <= A.length-1
     * <p/>
     * post :  elements of A[p..r] are rearranged so that
     * <p/>
     * A[p] <= A[p+1] <= ... <= A[r]
     */
    private static void quickHelper(int[] A, int p, int r) {
        if (p < r) {

            // partition around a pivot point, q
            int q = partition(A, p, r);

            // recursively sort each part
            quickHelper(A, p, q - 1);
            quickHelper(A, q + 1, r);
        }
    }


    /**
     * pre :  0 <= i <= j <= A.length-1
     * <p/>
     * post :  reorders the elements of A[i..j] and
     * returns a pivot index `p' such that
     * for all m, n in i <= m < p <= n <= j
     * <p/>
     * A[m] < A[p] <= A[n]
     * <p/>
     * Code adapted from [1]
     */
    private static int partition(int[] A, int i, int j) {

        int middle = (i + j) / 2;
        int pivot = A[middle];   // pivot value; choose middle element

        // put pivot element into A[i]
        swap(A, middle, i);

        int p = i;
        int k;
        for (k = i + 1; k <= j; k++) {

            /* invariant:
             *   (i)  A[i+1..p] < pivot
             *   (ii) A[p+1..k-1] >= pivot
             */

            if (A[k] < pivot) {
                p++;
                swap(A, k, p);
            }
        }

        swap(A, i, p);
        return p;
    }

    /**
     * pre :  0 <= i <= j <= A.length-1
     * <p/>
     * post :  A[i] and A[j] are swapped
     */
    private static void swap(int[] A, int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
}

