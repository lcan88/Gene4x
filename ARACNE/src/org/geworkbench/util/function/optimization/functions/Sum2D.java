package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Function;
import org.geworkbench.util.function.optimization.Function2D;
import org.geworkbench.util.function.optimization.Parameter;

/**
 * A two-dimensional sum of two one-dimensional {@link Function}s.
 * In other words, z = xfunction(x) + yfunction(y).  Either xfunction
 * or yfunction may be null.
 *
 * @author Dejan Vucinic &lt;dvucinic@users.sourceforge.net&gt;
 */
public class Sum2D implements Function2D {

    Function m_xfunction = null;
    Function m_yfunction = null;

    String m_name = null;

    /**
     * Constructor.
     *
     * @param xfunction the function to extend along the y axis
     * @param yfunction the function to extend along the x axis
     */
    public Sum2D(Function xfunction, Function yfunction) {
        m_xfunction = xfunction;
        m_yfunction = yfunction;
    }

    /**
     * Calculates the value of the function at the given x-y point using the
     * preset parameters.
     * The returned value is the sum of xfunction(x) and yfunction(y).
     *
     * @param x the x coordinate to calculate the function at
     * @param y the y coordinate to calculate the function at
     * @return the value of the function at the given x-y point
     */
    public double /* z = */ calculate(double x, double y) {
        double value = 0;
        if (null != m_xfunction) value += m_xfunction.calculate(x);
        if (null != m_yfunction) value += m_yfunction.calculate(y);
        return value;
    }

    public Parameter[] getParameters() {
        int nparams = 0;
        if (null != m_xfunction) nparams += m_xfunction.getParameters().length;
        if (null != m_yfunction) nparams += m_yfunction.getParameters().length;
        Parameter[] ret = new Parameter[nparams];
        int counter = 0;
        if (null != m_xfunction) {
            for (int i = 0; i < m_xfunction.getParameters().length; i++) {
                ret[counter++] = m_xfunction.getParameters()[i];
            }
        }
        if (null != m_yfunction) {
            for (int i = 0; i < m_yfunction.getParameters().length; i++) {
                ret[counter++] = m_yfunction.getParameters()[i];
            }
        }
        return ret;
    }

    public double /* y = */ calculate(double x) {
        throw new UnsupportedOperationException("Don't know what to calculate from a single parameter.");
    }

    public String getName() {
        if (null != m_name) return m_name;

        StringBuffer ret = new StringBuffer();
        if (null != m_xfunction) {
            ret.append(m_xfunction.getName());
        }
        if (null != m_xfunction && null != m_yfunction) {
            ret.append(" + ");
        }
        if (null != m_yfunction) {
            ret.append(m_yfunction.getName());
        }
        return ret.toString();
    }

    public void setName(String newname) {
        m_name = newname;
    }

}
