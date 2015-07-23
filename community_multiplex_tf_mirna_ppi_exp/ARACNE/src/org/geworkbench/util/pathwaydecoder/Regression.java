package org.geworkbench.util.pathwaydecoder;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Regression {
    static double DBL_EPSILON = 1E-9;
    double[][] values = null;

    // Constructor using an array of Point2D objects
    // This is also the default constructor
    public Regression(double[][] v) {
        values = v;
        int i;
        if (values.length > 0)
            for (n = 0, i = 0; i < values.length; i++)
                addXY(values[i][0], values[i][1]);
    }

    public void calculate() {
        if (haveData()) {
            if (Math.abs(n * sumXsquared - sumX * sumX) > DBL_EPSILON) {
                b = (n * sumXY - sumY * sumX) / (n * sumXsquared - sumX * sumX);
                a = (sumY - b * sumX) / n;
                double sx = b * (sumXY - sumX * sumY / n);
                double sy2 = sumYsquared - sumY * sumY / n;
                double sy = sy2 - sx;

                coefD = sx / sy2;
                coefC = Math.sqrt(coefD);
                stdError = Math.sqrt(sy / (n - 2));
            } else {
                a = b = coefD = coefC = stdError = 0.0;
            }
        }
    }

    void addXY(double x, double y) {
        n++;
        sumX += x;
        sumY += y;
        sumXsquared += x * x;
        sumYsquared += y * y;
        sumXY += x * y;
    }

    // Must have at least 3 points to calculate
    // standard error of estimate.
    //Do we have enough data?
    boolean haveData() {
        return ((values != null) && (values.length > 2));
    }

    int items() {
        return values.length;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getCoefDeterm() {
        return coefD;
    }

    public double getCoefCorrel() {
        return coefC;
    }

    public double getStdErrorEst() {
        return stdError;
    }

    public double estimateY(double x) {
        return (a + b * x);
    }

    double n = 0;
    double sumX = 0;
    double sumY = 0;        // sums of x and y
    double sumXsquared = 0; // sum of x squares
    double sumYsquared = 0; // sum y squares
    double sumXY = 0;       // sum of x*y
    double a = 0;
    double b = 0;           // coefficients of f(x) = a + b*x
    double coefD = 0;       // coefficient of determination
    double coefC = 0;       // coefficient of correlation
    double stdError = 0;    // standard error of estimate
}
