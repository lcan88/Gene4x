package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Function <b>a * x^3 + b * x^2 + c * x + d</b>.
 * A concrete implementation of joptima.Function that calculates
 * a cubic function of one argument.
 */
public class Cubic extends ConstrainableFunction {

    public double m_a, m_b, m_c, m_d;

    public Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[countUnconstrained()];
        try {
            int cursor = 0;
            if (!m_constrained[0]) parameters[cursor++] = new Parameter(this, "a");
            if (!m_constrained[1]) parameters[cursor++] = new Parameter(this, "b");
            if (!m_constrained[2]) parameters[cursor++] = new Parameter(this, "c");
            if (!m_constrained[3]) parameters[cursor++] = new Parameter(this, "d");
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    public void constrain(String parametername) {
        if ("a".equals(parametername)) m_constrained[0] = true;
        if ("b".equals(parametername)) m_constrained[1] = true;
        if ("c".equals(parametername)) m_constrained[2] = true;
        if ("d".equals(parametername)) m_constrained[3] = true;
    }

    private String m_name = "Cubic";

    public String getName() {
        return m_name;
    }

    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Default constructor: a = 1, b = 0, c = 0, d = 0.
     */
    public Cubic() {
        this(1, 0, 0, 0);
    }

    /**
     * Constructor.
     */
    public Cubic(double a, double b, double c, double d) {
        this.m_a = a;
        this.m_b = b;
        this.m_c = c;
        this.m_d = d;
        m_constrained = new boolean[4];
    }

    public double calculate(double x) {
        return m_a * x * x * x + m_b * x * x + m_c * x + m_d;
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

    public double getD() {
        return m_d;
    }

    public void setD(double d) {
        this.m_d = d;
    }
}
