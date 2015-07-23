package org.geworkbench.util.function.optimization;

/**
 * A {@link Function} of two arguments.
 * May or may not meaningfully implement {@link Function#calculate}.
 *
 * @author Dejan Vucinic &lt;dvucinic@users.sourceforge.net&gt;
 */
public interface Function2D extends Function {

    /**
     * Calculates the value of the function at the given x-y point using the
     * preset parameters.
     *
     * @param x the x coordinate to calculate the function at
     * @param y the y coordinate to calculate the function at
     * @return the value of the function at the given x-y point
     */
    public double /* z = */ calculate(double x, double y);

}
