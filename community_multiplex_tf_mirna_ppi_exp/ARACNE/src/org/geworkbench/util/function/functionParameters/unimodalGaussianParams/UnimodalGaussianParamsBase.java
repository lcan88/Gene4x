package org.geworkbench.util.function.functionParameters.unimodalGaussianParams;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import org.geworkbench.util.function.functionParameters.FunctionParameters;

abstract public class UnimodalGaussianParamsBase extends FunctionParameters {
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

    public UnimodalGaussianParamsBase(double[] mu, double varX, double varY, double covariance) {
        this.mu[0] = mu[0];
        this.mu[1] = mu[1];

        covarianceMatrix[0][0] = varX;
        covarianceMatrix[1][1] = varY;
        covarianceMatrix[0][1] = covariance;
        covarianceMatrix[1][0] = covariance;

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    public UnimodalGaussianParamsBase() {
        initialize();
        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    void initializeHashMap() {
        put("mu", mu);
        put("covarianceMatrix", covarianceMatrix);
        put("mgd", mgd);
    }

    public abstract void initialize();

}
