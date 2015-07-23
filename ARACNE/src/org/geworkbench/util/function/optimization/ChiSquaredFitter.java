package org.geworkbench.util.function.optimization;

import java.util.Iterator;

/**
 * <p/>
 * &chi;&sup2; fitter.
 * This class can be passed to a {@link Minimizer} to minimize &chi;&sup2; of a
 * sum of one or more functions fit to the given data set.
 * Error bars may be provided for the data points if the data has
 * compounded errors that are Gaussian.  sqrt(y) errors are assumed otherwise.
 * For different likelihood functions you can extend the {@link #calculate}
 * method on this class.
 * </p>
 * <p/>
 * <p/>
 * A typical usage of this class would go as follows:
 * <pre>
 *   // The data points.
 *   double[] x = new double[]{  1,   2,   3,    4,  5};
 *   double[] y = new double[]{1.1, 4.0, 8.7, 15.4, 26};
 * <p/>
 *   // The {@link Function} to fit to the data points.
 *   Function f = new Quadratic();
 * <p/>
 *   ChiSquaredFitter fitter = new ChiSquaredFitter();
 *   fitter.setData(x, y);
 *   fitter.addFunction(f);
 *   Minimizer.minimize(fitter);
 * <p/>
 *   // At this point, if the fit converged, the {@link Function} f will have
 *   // its {@link Parameter}s set to the optimal values.
 * </pre>
 * </p>
 *
 * @author Dejan Vucinic &lt;dvucinic@users.sourceforge.net&gt;
 */
public class ChiSquaredFitter extends Fitter {

    private double[] xpoint, ypoint;
    private double[] yerror = null;

    /**
     * Constructor.
     */
    public ChiSquaredFitter() {
    }

    /**
     * Constructor.
     * Assumes sqrt(y) errors.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     */
    public ChiSquaredFitter(double[] x, double[] y) {
        setData(x, y);
    }

    /**
     * Constructor.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param errors the error bars on y coordinates of points
     */
    public ChiSquaredFitter(double[] x, double[] y, double[] errors) {
        setData(x, y, errors);
    }

    /**
     * Constructor.
     * Assumes sqrt(y) errors.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     * @param f the {@link Function} to fit to the given set of points
     */
    public ChiSquaredFitter(double[] x, double[] y, Function f) {
        this(x, y);
        addFunction(f);
    }

    /**
     * Constructor.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param errors the error bars on y coordinates of points
     * @param f      the {@link Function} to fit to the given set of points
     */
    public ChiSquaredFitter(double[] x, double[] y, double[] errors, Function f) {
        this(x, y, errors);
        addFunction(f);
    }

    /**
     * Fit the sum of functions to this set of points.
     * Assumes sqrt(y) error bars.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     */
    public void setData(double[] x, double[] y) {
        xpoint = x;
        ypoint = y;
        yerror = null;
    }

    /**
     * Fit the sum of functions to this set of points.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param errors the error bars on y coordinates of points
     */
    public void setData(double[] x, double[] y, double[] errors) {
        xpoint = x;
        ypoint = y;
        yerror = errors;
    }

    /**
     * Calculates the &chi;&sup2; given parameter values set by a {@link Minimizer}
     * using reflection.
     * The formula is:
     * <pre>
     *   for each point in the data set {
     *     f = 0
     *     for each {@link Function} {
     *       f += {@link Function#calculate}(x[point])
     *     }
     *     &chi;&sup2; += (y[point] - f)&sup2; / error[point]&sup2;
     *   }
     * </pre>
     */
    public double calculate() {
        double value = 0;
        for (int point = 0; point < xpoint.length; point++) {
            Iterator it = m_functions.iterator();
            double fvalue = 0;
            while (it.hasNext()) {
                Function f = (Function) it.next();
                fvalue += f.calculate(xpoint[point]);
            }
            double chisq = (ypoint[point] - fvalue);
            chisq = chisq * chisq;
            if (null != yerror) chisq /= yerror[point] * yerror[point];
            value += chisq;
        }
        return value;
    }

}
