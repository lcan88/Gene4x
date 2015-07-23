package org.geworkbench.util.function;

public class FunctionFactory {
    public FunctionFactory() {
    }

    public static IFunction getFunction(String functionName) {
        if ("Uniform".equals(functionName)) {
            return new UniformFunction();
        } else if ("Multivariate Gaussian".equals(functionName)) {
            return new MultivariateGaussian();
        } else if ("Multivariate Gaussian Bimodal".equals(functionName)) {
            return new MultivariateGaussianBimodal();
        } else {
            return null;
        }
    }
}
