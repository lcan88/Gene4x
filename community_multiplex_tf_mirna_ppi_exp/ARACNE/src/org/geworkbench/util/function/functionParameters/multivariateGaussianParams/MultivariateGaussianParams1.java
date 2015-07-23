package org.geworkbench.util.function.functionParameters.multivariateGaussianParams;

public class MultivariateGaussianParams1 extends MultivariateGaussianParamsBase {
    public MultivariateGaussianParams1() {
    }

    /**
     * initialize
     */
    public void initialize() {
        double covar = .075;

        mu[0] = 0.0;
        mu[1] = 0.0;

        //    covarianceMatrix[0][0] = 0.5 / 2.0 / 3.0;
        covarianceMatrix[0][0] = .083333;
        covarianceMatrix[0][1] = covar;
        covarianceMatrix[1][0] = covar;
        //    covarianceMatrix[1][1] = 0.5 / 2.0 / 3.0;
        covarianceMatrix[1][1] = .083333;


    }
}
