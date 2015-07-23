package org.geworkbench.util;


public class EuclideanDistance implements Distance {
    // Singleton
    public final static EuclideanDistance instance = new EuclideanDistance();

    protected EuclideanDistance() {
    }

    public static double distance(DoubleIterator i, DoubleIterator j) {
        double d = 0;
        while (i.hasNext() && j.hasNext()) {
            double a = i.next();
            double b = j.next();
            // The following is a very efficient replacement for Double.isNaN(t).
            double t = a - b;
            if (t == t) {
                d += t * t;
            }
        }
        return Math.sqrt(d);
    }

    public static double distance(double[] a, double[] b) {
        double d = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; ++i) {
            double t = a[i] - b[i];
            // The following is a very efficient replacement for Double.isNaN(t).
            if (t == t) {
                d += t * t;
            }
        }
        return Math.sqrt(d);
    }

    public double compute(DoubleIterator i, DoubleIterator j) {
        return distance(i, j);
    }

    public double compute(double[] a, double[] b) {
        return distance(a, b);
    }

    // Used to test optimizations
    public static void main(String[] args) {
        double[] a = {3.0, Double.NaN};
        double[] b = {4.0, 10.0};
        System.out.println("Distance: " + distance(a, b));
    }
}
