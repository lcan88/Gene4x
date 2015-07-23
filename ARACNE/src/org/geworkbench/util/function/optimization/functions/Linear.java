package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Function <b>a * x + b</b>.
 * A concrete implementation of joptima.Function that calculates
 * a linear function of one argument.
 */
public class Linear extends ConstrainableFunction {

    public double m_a, m_b;

    public Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[countUnconstrained()];
        try {
            int cursor = 0;
            if (!m_constrained[0]) parameters[cursor++] = new Parameter(this, "a");
            if (!m_constrained[1]) parameters[cursor++] = new Parameter(this, "b");
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    public void constrain(String parametername) {
        if ("a".equals(parametername)) m_constrained[0] = true;
        if ("b".equals(parametername)) m_constrained[1] = true;
    }

    private String m_name = "Linear";

    public String getName() {
        return m_name;
    }

    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Default constructor: a = 1, b = 0.
     */
    public Linear() {
        this(1, 0);
    }

    /**
     * Constructor.
     */
    public Linear(double a, double b) {
        this.m_a = a;
        this.m_b = b;
        m_constrained = new boolean[2];
    }

    public double calculate(double x) {
        return m_a * x + m_b;
    }

    public double getA() {
        return m_a;
    }

    public void setA(double a) {
        this.m_a = a;
    }

    public double getB() {
        return m_b;
    }

    public void setB(double b) {
        this.m_b = b;
    }
}
