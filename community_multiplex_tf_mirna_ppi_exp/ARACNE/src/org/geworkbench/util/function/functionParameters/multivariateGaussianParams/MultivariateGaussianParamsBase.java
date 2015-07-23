package org.geworkbench.util.function.functionParameters.multivariateGaussianParams;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import org.geworkbench.util.function.functionParameters.FunctionParameters;

abstract public class MultivariateGaussianParamsBase extends FunctionParameters {
    double[] mu = new double[2];
    double[][] covarianceMatrix = new double[2][2];
    MultiGaussianDistribution mgd;

    public MultiGaussianDistribution getMgd() {
        return mgd;
    }

    public double[] getMu() {
        return mu;
    }

    public void setCovarianceMatrix(double[][] covarianceMatrix) {
        this.covarianceMatrix = covarianceMatrix;
    }

    public void setMgd(MultiGaussianDistribution mgd) {
        this.mgd = mgd;
    }

    public void setMu(double[] mu) {
        this.mu = mu;
    }

    public double[][] getCovarianceMatrix() {
        return covarianceMatrix;
    }

    public void setCovariance(double covar) {
        this.covarianceMatrix[0][1] = covar;
        this.covarianceMatrix[1][0] = covar;

        this.mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }


    public MultivariateGaussianParamsBase() {

        initialize();

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
        put("mu", mu);
        put("covarianceMatrix", covarianceMatrix);
        put("mgd", mgd);
    }

    public abstract void initialize();

}
