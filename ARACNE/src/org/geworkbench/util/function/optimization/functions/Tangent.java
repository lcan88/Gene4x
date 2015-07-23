package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Function <b>amplitude * tan(2 * pi * frequency * (x - offset))</b>.
 * A concrete implementation of joptima.Function that calculates
 * a tangent.
 */
public class Tangent extends ConstrainableFunction {

    public double m_amplitude, m_frequency, m_xoffset;

    public Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[countUnconstrained()];
        try {
            int cursor = 0;
            if (!m_constrained[0]) parameters[cursor++] = new Parameter(this, "amplitude");
            if (!m_constrained[1]) parameters[cursor++] = new Parameter(this, "frequency");
            if (!m_constrained[2]) parameters[cursor++] = new Parameter(this, "xoffset");
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    public void constrain(String parametername) {
        if ("amplitude".equals(parametername)) m_constrained[0] = true;
        if ("frequency".equals(parametername)) m_constrained[1] = true;
        if ("xoffset".equals(parametername)) m_constrained[2] = true;
    }

    private String m_name = "Tangent";

    public String getName() {
        return m_name;
    }

    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Default constructor: amplitude = 1, frequency = 1, xoffset = 0.
     */
    public Tangent() {
        this(1, 1, 0);
    }

    /**
     * Constructor.
     */
    public Tangent(double amplitude, double frequency, double xoffset) {
        this.m_amplitude = amplitude;
        this.m_frequency = frequency;
        this.m_xoffset = xoffset;
        m_constrained = new boolean[3];
    }

    public double calculate(double x) {
        return m_amplitude * Math.tan(2 * Math.PI * m_frequency * (x - m_xoffset));
    }

    public void setAngularFrequency(double omega) {
        m_frequency = omega / Math.PI / 2;
    }

    public double getAngularFrequency() {
        return 2 * Math.PI * m_frequency;
    }

    public double getAmplitude() {
        return m_amplitude;
    }

    public void setAmplitude(double amplitude) {
        m_amplitude = amplitude;
    }

    public double getFrequency() {
        return m_frequency;
    }

    public void setFrequency(double frequency) {
        m_frequency = frequency;
    }

    public double getXoffset() {
        return m_xoffset;
    }

    public void setXoffset(double xoffset) {
        m_xoffset = xoffset;
    }
}
