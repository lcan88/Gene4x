package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Constant function.
 * A concrete implementation of joptima.Function that returns a constant.
 */
public class Constant implements org.geworkbench.util.function.optimization.Function {

    public double m_constant;

    public Parameter[] getParameters() {
        Parameter[] parameters = null;
        try {
            parameters = new Parameter[]{new Parameter(this, "constant")};
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    private String name = "Constant";

    public String getName() {
        return name;
    }

    public void setName(String newname) {
        name = newname;
    }

    /**
     * Default constructor: constant = 0;
     */
    public Constant() {
        m_constant = 0;
    }

    /**
     * Constructor.
     */
    public Constant(double constant) {
        this.m_constant = constant;
    }

    public double calculate(double x) {
        return m_constant;
    }

    public double getConstant() {
        return m_constant;
    }

    public void setConstant(double constant) {
        this.m_constant = constant;
    }
}
