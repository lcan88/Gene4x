package org.geworkbench.util.function.optimization.functions;

import org.geworkbench.util.function.optimization.Parameter;

/**
 * Function <b>norm / (sigma * sqrt(2pi)) * exp(- (x - mean)^2 / (2 * sigma^2))</b>.
 * A concrete implementation of joptima.Function that calculates
 * a Gaussian.
 */
public class Gaussian extends ConstrainableFunction {

    private static final double sqrttwopi = Math.sqrt(2 * Math.PI);

    public double m_norm, m_mean, m_sigma;

    public Parameter[] getParameters() {
        Parameter[] parameters = new Parameter[countUnconstrained()];
        try {
            int cursor = 0;
            if (!m_constrained[0]) parameters[cursor++] = new Parameter(this, "norm");
            if (!m_constrained[1]) parameters[cursor++] = new Parameter(this, "mean");
            if (!m_constrained[2]) parameters[cursor++] = new Parameter(this, "sigma");
        } catch (NoSuchFieldException nsfe) {
            nsfe.printStackTrace();
        }
        return parameters;
    }

    public void constrain(String parametername) {
        if ("norm".equals(parametername)) m_constrained[0] = true;
        if ("mean".equals(parametername)) m_constrained[1] = true;
        if ("sigma".equals(parametername)) m_constrained[2] = true;
    }

    private String m_name = "Gaussian";

    public String getName() {
        return m_name;
    }

    public void setName(String newname) {
        m_name = newname;
    }

    /**
     * Default constructor: norm = 1, mean = 0, sigma = 1.
     */
    public Gaussian() {
        this(1, 0, 1);
    }

    /**
     * Constructor.
     */
    public Gaussian(double norm, double mean, double sigma) {
        this.m_norm = norm;
        this.m_mean = mean;
        this.m_sigma = sigma;
        m_constrained = new boolean[3];
    }

    public double calculate(double x) {
        return m_norm / m_sigma / sqrttwopi * Math.exp(-(x - m_mean) * (x - m_mean) / (2 * m_sigma * m_sigma));
    }

    public double getNorm() {
        return m_norm;
    }

    public void setNorm(double norm) {
        m_norm = norm;
    }

    public double getMean() {
        return m_mean;
    }

    public void setMean(double mean) {
        m_mean = mean;
    }

    public double getSigma() {
        return m_sigma;
    }

    public void setSigma(double sigma) {
        m_sigma = sigma;
    }
}
