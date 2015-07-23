package org.geworkbench.util;


/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Compute the Pearson Correlation <b>Distance</b>. Note that if
 * <code>r</code> is correlation, then Correlation Distance is <code>1-r</code></p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Frank Wei Guo
 * @version 3.0
 */
public class CorrelationDistance implements Distance {
    public final static CorrelationDistance instance = new CorrelationDistance();

    protected CorrelationDistance() {
    }

    public static double distance(DoubleIterator i, DoubleIterator j) {
        double sx = 0;
        double sy = 0;
        double sxx = 0;
        double syy = 0;
        double sxy = 0;
        int n = 0;
        while (i.hasNext() && j.hasNext()) {
            double x = i.next();
            double y = j.next();
            if (Double.isNaN(x) || Double.isNaN(y)) continue;
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
            ++n;
        }
        if (n == 0) return 0;
        return 1 - (n * sxy - sx * sy) / Math.sqrt((n * sxx - sx * sx) * (n * syy - sy * sy));
    }

    public static double distance(double[] a, double[] b) {
        int N = Math.min(a.length, b.length);
        double sx = 0;
        double sy = 0;
        double sxx = 0;
        double syy = 0;
        double sxy = 0;
        int n = 0;
        for (int i = 0; i < N; ++i) {
            double x = a[i];
            double y = b[i];
            if (Double.isNaN(x) || Double.isNaN(y)) continue;
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
            ++n;
        }
        if (n == 0) return 0;
        return 1 - (n * sxy - sx * sy) / Math.sqrt((n * sxx - sx * sx) * (n * syy - sy * sy));
    }

    public double compute(DoubleIterator i, DoubleIterator j) {
        return distance(i, j);
    }

    public double compute(double[] a, double[] b) {
        return distance(a, b);
    }
}
