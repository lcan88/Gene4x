package org.geworkbench.util;

import java.util.Arrays;

public class SpearmanRankDistance implements Distance {
    public final static SpearmanRankDistance instance = new SpearmanRankDistance();

    protected SpearmanRankDistance() {
    }

    public static double distance(DoubleIterator i, DoubleIterator j) {
        double result = 0;
        int an = 0;
        DoubleIterator ii = i;
        DoubleIterator jj = j;
        while (ii.hasNext()) {
            ++an;
            ii.next(); // discard value
        }
        double[] a = new double[an];
        for (int k = 0; k < an; ++k) {
            a[k] = ii.next();
        }

        int bn = 0;
        while (jj.hasNext()) {
            ++bn;
            jj.next(); // discard value
        }
        double[] b = new double[bn];
        for (int k = 0; k < bn; ++k) {
            b[k] = jj.next();
        }

        return distance(a, b);
    }

    public static double distance(double[] a, double[] b) {
        double result = 0;
        double denom1 = 0;
        double denom2 = 0;
        int m = Math.min(a.length, b.length);
        double[] rank1 = getRank(a);
        double[] rank2 = getRank(b);
        double avgrank = 0.5 * (m - 1); /* Average rank */
        for (int i = 0; i < m; i++) {
            double value1 = rank1[i];
            double value2 = rank2[i];
            result += value1 * value2;
            denom1 += value1 * value1;
            denom2 += value2 * value2;
        }
        /* Note: denom1 and denom2 cannot be calculated directly from the number
         * of elements. If two elements have the same rank, the squared sum of
         * their ranks will change.
         */
        result /= m;
        denom1 /= m;
        denom2 /= m;
        result -= avgrank * avgrank;
        denom1 -= avgrank * avgrank;
        denom2 -= avgrank * avgrank;
        result = result / Math.sqrt(denom1 * denom2);
        result = 1. - result;
        return result;
    }

    public double compute(DoubleIterator i, DoubleIterator j) {
        return distance(i, j);
    }

    public double compute(double[] a, double[] b) {
        return distance(a, b);
    }


    /**
     * getRank computes the ranks for the array <code>a</code>; for tied
     * elements, their average rank is used for them.
     *
     * @param data double[]
     * @return double[]
     */
    public static double[] getRank(double[] data) {
        int n = data.length;
        double[] rank = new double[n];
        Integer[] index = new Integer[n]; // to use the customized sort has to be class
        for (int i = 0; i < n; i++) index[i] = i;
        Arrays.sort(index, new IndexSortComparator(data));

        /* Build a rank table */
        for (int i = 0; i < n; i++) rank[index[i]] = i;
        /* Fix for equal ranks */
        int i = 0;
        while (i < n) {
            int m;
            double value = data[index[i]];
            int j = i + 1;
            while (j < n && data[index[j]] == value) j++;
            m = j - i; /* number of equal ranks found */
            value = rank[index[i]] + (m - 1) / 2.;
            for (j = i; j < i + m; j++) rank[index[j]] = value;
            i += m;
        }
        return rank;
    }

    /**
     * getRank computes the ranks for the array <code>a</code>; for tied
     * elements, their average rank is used for them.
     *
     * @param data double[]
     * @param rank double[] - allocated by caller, will store the ranks
     * @return double - sum of m^3 - m for correction
     */
    public static double getRank(double[] data, double[] rank) {
        int n = data.length;
        Integer[] index = new Integer[n]; // to use the customized sort has to be class
        for (int i = 0; i < n; i++) index[i] = i;
        Arrays.sort(index, new IndexSortComparator(data));

        /* Build a rank table */
        for (int i = 0; i < n; i++) rank[index[i]] = i;
        /* Fix for equal ranks */
        int i = 0;
        double s = 0.0;
        while (i < n) {
            int m;
            double value = data[index[i]];
            int j = i + 1;
            while (j < n && data[index[j]] == value) j++;
            m = j - i; /* number of equal ranks found */
            value = rank[index[i]] + (m - 1) / 2.;
            for (j = i; j < i + m; j++) rank[index[j]] = value;
            i += m;
            s += m * m * m - m;
        }
        return s;
    }

}
