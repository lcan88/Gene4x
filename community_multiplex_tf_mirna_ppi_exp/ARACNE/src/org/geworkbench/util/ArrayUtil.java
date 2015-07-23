package org.geworkbench.util;

public class ArrayUtil {
    public ArrayUtil() {
    }

    public static int getArrayIndex(Object[] array, Object target) {
        for (int i = 0; i < array.length; i++) {
            if (target.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static int getArrayIndex(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }


    public static boolean arrayContains(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return true;
            }
        }
        return false;
    }

    public static double[] getArrayColumn(double[][] arr, int columnIndex) {
        int numRows = arr.length;
        double[] col = new double[numRows];
        for (int rowCtr = 0; rowCtr < numRows; rowCtr++) {
            col[rowCtr] = arr[rowCtr][columnIndex];
        }
        return col;
    }

    public static <T> void reverse(T[] array) {
        int n = array.length;
        int m = n/2;
        n -= 1;
        for (int i = 0; i < m; i++) {
            T t = array[i];
            array[i] = array[n - i];
            array[n - i] = t;
        }
    }

}
