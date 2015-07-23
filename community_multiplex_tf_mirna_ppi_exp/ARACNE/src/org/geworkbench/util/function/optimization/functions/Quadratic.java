package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Function <b>a * x^2 + b * x + c</b>.
 * A concrete implementation of joptima.Function that calculates
 * a quadratic function of one argument.
 */
public class Quadratic extends ConstrainableFunction {

    public double m_a, m_b, m_c;

    public Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[countUnconstrained()];
        try {
            int cursor = 0;
            if (!m_constrained[0]) parameters[cursor++] = new Parameter(this, "a");
            if (!m_constrained[1]) parameters[cursor++] = new Parameter(this, "b");
            if (!m_constrained[2]) parameters[cursor++] = new Parameter(this, "c");
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    public void constrain(String parametername) {
        if ("a".equals(parametername)) m_constrained[0] = true;
        if ("b".equals(parametername)) m_constrained[1] = true;
        if ("c".equals(parametername)) m_constrained[2] = true;
    }

    private String m_name = "Quadratic";

    public String getName() {
        return m_name;
    }

    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Default constructor: a = 1, b = 0, c = 0.
     */
    public Quadratic() {
        this(1, 0, 0);
    }

    /**
     * Constructor.
     */
    public Quadratic(double a, double b, double c) {
        this.m_a = a;
        this.m_b = b;
        this.m_c = c;
        m_constrained = new boolean[3];
    }

    public double calculate(double x) {
        return m_a * x * x + m_b * x + m_c;
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

    public double getC() {
        return m_c;
    }

    public void setC(double c) {
        this.m_c = c;
    }
}
