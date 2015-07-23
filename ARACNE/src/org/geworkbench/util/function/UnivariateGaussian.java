package org.geworkbench.util.function;

import cern.jet.random.Normal;
import edu.cornell.lassp.houle.RngPack.RandomJava;

public class UnivariateGaussian extends FunctionBase implements IProbabilityFunctionUnivariate {
    double mu = 0.0;
    double variance = .08333;

    public UnivariateGaussian() {
    }

    /**
     * getData
     *
     * @return double[][]
     */
    public double[][] getData() {
        return null;
    }

    /**
     * getProbability
     *
     * @param x double
     * @return double
     */
    public double getProbability(double x) {
        Normal n = new Normal(mu, Math.sqrt(variance), new RandomJava());
        return n.pdf(x);
    }
}
