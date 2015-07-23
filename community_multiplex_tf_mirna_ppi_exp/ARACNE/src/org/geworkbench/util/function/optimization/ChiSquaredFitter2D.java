package org.geworkbench.util.function.optimization;

import org.geworkbench.util.function.optimization.functions.Sum2D;

import java.util.Iterator;

/**
 * <p/>
 * &chi;&sup2; fitter.
 * This class can be passed to a {@link Minimizer} to minimize &chi;&sup2; of a
 * sum of one or more functions fit to the given two-dimensional data set.
 * Error bars may be provided for the data points if the data has
 * compounded errors that are Gaussian.  sqrt(z) errors are assumed otherwise.
 * </p>
 * <p/>
 * <p/>
 * Note that the usual two-dimensional matrix of values is passed to this
 * function unfolded into a single array, i.e. the (x[i], y[i], z[i])
 * tuple for an m by n histogram where z = f(x, y) will have indices running
 * from 0 through m * n - 1.
 * </p>
 * <p/>
 * <p/>
 * A typical usage of this class would go as follows:
 * <pre>
 *   // The data points.
 *   double[] x = new double[]{  1,   2,   3,
 *                               1,   2,   3,
 *                               1,   2,   3};
 *   double[] y = new double[]{  1,   1,   1,
 *                               2,   2,   2,
 *                               3,   3,   3};
 *   double[] z = new double[]{1.1, 4.0, 8.7,
 *                             3.9,  16,  24,
 *                             9.2,  22,  38};
 * <p/>
 *   // The {@link Function} to fit to the data points.
 *   Function2D f = new Sum2D(new Quadratic(), new Quadratic);
 * <p/>
 *   ChiSquaredFitter2D fitter = new ChiSquaredFitter2D();
 *   fitter.setData(x, y, z);
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
public class ChiSquaredFitter2D extends Fitter {

    private double[] xpoint, ypoint, zpoint;
    private double[] zerror = null;

    /**
     * Constructor.
     */
    public ChiSquaredFitter2D() {
    }

    /**
     * Constructor.
     * Assumes sqrt(z) errors.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     * @param z the z coordinates of points
     */
    public ChiSquaredFitter2D(double[] x, double[] y, double[] z) {
        setData(x, y, z);
    }

    /**
     * Constructor.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param z      the z coordinates of points
     * @param errors the error bars on z coordinates of points
     */
    public ChiSquaredFitter2D(double[] x, double[] y, double[] z, double[] errors) {
        setData(x, y, z, errors);
    }

    /**
     * Constructor.
     * Assumes sqrt(y) errors.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     * @param z the z coordinates of points
     * @param f the {@link Function} to fit to the given set of points
     */
    public ChiSquaredFitter2D(double[] x, double[] y, double[] z, Function f) {
        this(x, y, z);
        addFunction(f);
    }

    /**
     * Constructor.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param z      the z coordinates of points
     * @param errors the error bars on z coordinates of points
     * @param f      the {@link Function} to fit to the given set of points
     */
    public ChiSquaredFitter2D(double[] x, double[] y, double[] z, double[] errors, Function f) {
        this(x, y, z, errors);
        addFunction(f);
    }

    /**
     * Invalidates this signature of this method on the superclass.
     * This form throws an UnsupportedOperationException() because
     * its meaning is ambiguous for a 2D fitter.  Use {@link #addFunction}
     * instead.
     */
    public void addFunction(Function f) {
        throw new UnsupportedOperationException("Adding a 1D function to a 2D fitter is ambiguous.  Use " + "addFunction1Dx() or addFunction1Dy() instead.");
    }

    /**
     * Adds a {@link Function2D} to this {@link Fitter}.
     */
    public void addFunction(Function2D f) {
        m_functions.add(f);
        m_nparameters += f.getParameters().length;
    }

    /**
     * Adds a one-dimensional {@link Function} to this {@link Fitter}.
     * This form makes 2D extension of the given function along the y-axis,
     * i.e. makes a Sum2D(function, null).
     *
     * @param f the one-dimensional function to extend along the y-axis
     */
    public void addFunction1Dx(Function f) {
        m_functions.add(new Sum2D(f, null));
        m_nparameters += f.getParameters().length;
    }

    /**
     * Adds a one-dimensional {@link Function} to this {@link Fitter}.
     * This form makes 2D extension of the given function along the x-axis,
     * i.e. makes a Sum2D(null, function).
     *
     * @param f the one-dimensional function to extend along the x-axis
     */
    public void addFunction1Dy(Function f) {
        m_functions.add(new Sum2D(null, f));
        m_nparameters += f.getParameters().length;
    }

    /**
     * Fit the sum of functions to this set of points.
     * Assumes sqrt(y) error bars.
     *
     * @param x the x coordinates of points
     * @param y the y coordinates of points
     * @param z the z coordinates of points
     */
    public void setData(double[] x, double[] y, double[] z) {
        xpoint = x;
        ypoint = y;
        ypoint = z;
        zerror = null;
    }

    /**
     * Fit the sum of functions to this set of points.
     *
     * @param x      the x coordinates of points
     * @param y      the y coordinates of points
     * @param z      the z coordinates of points
     * @param errors the error bars on y coordinates of points
     */
    public void setData(double[] x, double[] y, double[] z, double[] errors) {
        xpoint = x;
        ypoint = y;
        zpoint = z;
        zerror = errors;
    }

    /**
     * Calculates the &chi;&sup2; given parameter values set by a {@link Minimizer}
     * using reflection.
     * The formula is:
     * <pre>
     *   for each point in the data set {
     *     f = 0
     *     for each {@link Function} {
     *       f += {@link Function#calculate}(x[point], y[point])
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
                Function2D f = (Function2D) it.next();
                fvalue += f.calculate(xpoint[point], ypoint[point]);
            }
            double chisq = (zpoint[point] - fvalue);
            chisq = chisq * chisq;
            if (null != zerror) chisq /= zerror[point] * zerror[point];
            value += chisq;
        }
        return value;
    }

}
