package org.geworkbench.util.function.optimization;

/**
 * <p>A contract with functions that want to participate in optimization.</p>
 * <p/>
 * <p/>
 * Function parameters are provided as an array of delegates.  To set the
 * parameters prior to calculating the value of the fuction one must call
 * {@link Parameter#set} which sets member fields using reflection.  Once
 * all the parameters have been set, {@link #calculate} returns the value
 * of the function at the given coordinate.
 * </p>
 *
 * @author Dejan Vucinic &lt;dvucinic@users.sourceforge.net&gt;
 */
public interface Function {

    /**
     * Lists all the mutable parameters to this function.
     *
     * @return a {@link Parameter}[] of delegates that access the proper
     *         member variables.
     */
    public Parameter[] getParameters();

    /**
     * Calculates the value of the function at the given point using the
     * preset parameters.
     *
     * @param x the x coordinate to calculate the function at
     * @return the value of the function at the given coordinate
     */
    public double /* y = */ calculate(double x);

    /**
     * Gets the (user-settable) name of this function.
     *
     * @return the name of the function
     */
    public String getName();

    /**
     * Overrides the default function name.
     *
     * @param newname the new name for the function
     */
    public void setName(String newname);

}
