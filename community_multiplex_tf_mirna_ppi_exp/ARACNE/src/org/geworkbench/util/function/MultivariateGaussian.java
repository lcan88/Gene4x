package org.geworkbench.util.function;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import org.geworkbench.util.function.functionParameters.FunctionParameters;

public class MultivariateGaussian extends FunctionBase implements IProbabilityFunctionBivariate {
    double[][] covarianceMatrix = new double[2][2];
    double[] mu = new double[2];

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

    public MultivariateGaussian() {
        double covar = .075;

        mu[0] = 0.0;
        mu[1] = 0.0;

        //    covarianceMatrix[0][0] = 0.5 / 2.0 / 3.0;
        covarianceMatrix[0][0] = .083333;
        covarianceMatrix[0][1] = covar;
        covarianceMatrix[1][0] = covar;
        //    covarianceMatrix[1][1] = 0.5 / 2.0 / 3.0;
        covarianceMatrix[1][1] = .083333;

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    public MultivariateGaussian(FunctionParameters params) {
        this.mu = (double[]) params.get("mu");
        this.covarianceMatrix = (double[][]) params.get("covarianceMatrix");
        this.mgd = (MultiGaussianDistribution) params.get("mgd");
    }

    public MultivariateGaussian(double[] mu, double[][] covarianceMatrix) {
        this.mu = mu;
        this.covarianceMatrix = covarianceMatrix;

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    public MultivariateGaussian(double[] mu, double varX, double varY, double covariance) {
        this.mu[0] = mu[0];
        this.mu[1] = mu[1];

        covarianceMatrix[0][0] = varX;
        covarianceMatrix[1][1] = varY;
        covarianceMatrix[0][1] = covariance;
        covarianceMatrix[1][0] = covariance;

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    /**
     * getData
     *
     * @return double[][]
     */
    public double[][] getData() {
        //MultiGaussianDistribution mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
        double[] xSeries = new double[numDataPoints];
        double[] ySeries = new double[numDataPoints];

        for (int i = 0; i < numDataPoints; i++) {
            double[] randData = mgd.generate();
            double xVal = randData[0];
            double yVal = randData[1];

            xSeries[i] = xVal;
            ySeries[i] = yVal;
        }
        double[][] data = {xSeries, ySeries};
        return data;
    }

    public double[] getDataPoint() {
        //MultiGaussianDistribution mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
        return mgd.generate();
    }

    public double getProbability(double x, double y) {
        //MultiGaussianDistribution mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
        double[] arr = {x, y};
        return mgd.probability(arr);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(mu[0]);
        sb.append("\t");
        sb.append(mu[1]);
        sb.append("\t");

        sb.append(covarianceMatrix[0][0]);
        sb.append("\t");
        sb.append(covarianceMatrix[1][1]);
        sb.append("\t");
        sb.append(covarianceMatrix[0][1]);

        return sb.toString();
    }
}
