package org.geworkbench.util.function.optimization.functions;

/**
 * A function that can make parameters stick to given values.
 */
public abstract class ConstrainableFunction implements org.geworkbench.util.function.optimization.Function {
    /**
     * Removes the given parameter from the parameter list.
     * This constraint is mandatory to implement.
     */
    public abstract void constrain(String parametername);

    /**
     * Remembers a value and error bar to constrain the given parameter to.
     * The actual constraining must happen in the {@link Fitter}, since it
     * depends on the particular likelihood function in use.
     */
    public void constrain(String parametername, double value, double error) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remembers a value and error bars to constrain the given parameter to.
     * The actual constraining must happen in the {@link Fitter}, since it
     * depends on the particular likelihood function in use.
     */
    public void constrain(String parametername, double value, double pluserror, double minuserror) {
        throw new UnsupportedOperationException();
    }

    /**
     * Flags indicating which parameters are constrained.
     */
    protected boolean[] m_constrained;

    /**
     * Convenience function calculating the number of unconstrained parameters.
     */
    protected int countUnconstrained() {
        int num = 0;
        for (int i = 0; i < m_constrained.length; i++) {
            if (!m_constrained[i]) num++;
        }
        return num;
    }
}
