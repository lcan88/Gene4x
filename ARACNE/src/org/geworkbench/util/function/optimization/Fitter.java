package org.geworkbench.util.function.optimization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An abstract fitter.
 * <p/>
 * <p/>
 * Extensions of this class typically need to do the following:
 * <ul>
 * <li>provide the appropriate setData() methods or some other
 * way of handling the measured data points;
 * <li>override the addFunction() method if fitting children of
 * {@link Function} or something entirely different;
 * <li>implement the {@link Minimizable#calculate} method to
 * calculate the value of the appropriate likelihood function,
 * given the data points and the set of {@link Function}s.
 * </ul>
 * <p/>
 */
public abstract class Fitter implements Minimizable, Function {

    protected List m_functions = new ArrayList();
    protected int m_nparameters = 0;

    protected String m_name;

    /**
     * Adds a {@link Function} to this {@link Fitter}.
     */
    public void addFunction(Function f) {
        m_functions.add(f);
        m_nparameters += f.getParameters().length;
    }

    /**
     * Removes all {@link Function}s from this {@link Fitter}.
     * The Fitter may then be reused with existing data.
     */
    public void removeAllFunctions() {
        m_functions = new ArrayList();
        m_nparameters = 0;
    }

    /**
     * Returns the value of the sum of functions at the given point.
     * Useful for plotting the fitted function after the fit converges.
     */
    public double /* y = */ calculate(double x) {
        double value = 0;
        Iterator i = m_functions.iterator();
        while (i.hasNext()) value += ((Function) i.next()).calculate(x);
        return value;
    }

    /**
     * Returns a function name for this fitter.
     * By default the name is created from the names of all functions.
     * If setName() was called, that is returned instead.
     */
    public String getName() {
        if (null != m_name) return m_name;
        Iterator it = m_functions.iterator();
        StringBuffer buf = new StringBuffer();
        while (it.hasNext()) {
            if (0 < buf.length()) buf.append('+');
            buf.append(((Function) it.next()).getName());
        }
        return buf.toString();
    }

    /**
     * Sets the function name for this fitter.
     */
    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Creates a list of all the {@link Function} parameters for this {@link Fitter}.
     * This method retrieves the parameters lists from individual {@link Function}s and
     * copies them into a single array to pass to a {@link Minimizer}.
     */
    public Parameter[] getParameters() {
        Parameter[] p = new Parameter[m_nparameters];
        int pindex = 0;
        Iterator it = m_functions.iterator();
        while (it.hasNext()) {
            Parameter[] subp = ((Function) it.next()).getParameters();
            for (int i = 0; i < subp.length; i++) p[pindex++] = subp[i];
        }
        return p;
    }

}
