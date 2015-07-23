package org.geworkbench.util.function;

import org.geworkbench.util.function.functionParameters.FunctionParameters;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;

public class MultivariateGaussianBimodal extends FunctionBase implements IProbabilityFunctionBivariate {
    double[] mu1 = new double[2];
    double[] mu2 = new double[2];

    double[][] covar1 = new double[2][2];
    double[][] covar2 = new double[2][2];

    double tau1;
    double tau2;

    MultivariateGaussian gaussian1;
    MultivariateGaussian gaussian2;


    public MultivariateGaussianBimodal() {
        BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams("BimodalGaussianParams4");

        double[] mu1 = {0, 0};
        double varX1 = Math.pow(.5, 2);
        double varY1 = Math.pow(.5, 2);
        double rho1 = .5;
        double covar1 = calculateCovariance(varX1, varY1, rho1);

        double[] mu2 = {1, 1};
        double varX2 = Math.pow(.1, 2);
        double varY2 = Math.pow(.1, 2);
        double rho2 = 0;
        double covar2 = calculateCovariance(varX2, varY2, rho2);

        params.setGaussian1(new MultivariateGaussian(mu1, varX1, varY1, covar1));
        params.setGaussian2(new MultivariateGaussian(mu2, varX2, varY2, covar2));

        //        double gaussianCovar2 = 0;
        //
        //
        //        params.setCovariance1(gaussianCovar1);
        //        params.setCovariance2(gaussianCovar2);

        this.mu1 = (double[]) params.get("mu1");
        this.covar1 = (double[][]) params.get("covar1");
        this.tau1 = ((Double) params.get("tau1")).doubleValue();

        this.mu2 = (double[]) params.get("mu2");
        this.covar2 = (double[][]) params.get("covar2");
        this.tau2 = ((Double) params.get("tau2")).doubleValue();

        gaussian1 = (MultivariateGaussian) params.get("gaussian1");
        gaussian2 = (MultivariateGaussian) params.get("gaussian2");

    }

    public MultivariateGaussianBimodal(FunctionParameters params) {
        this.mu1 = (double[]) params.get("mu1");
        this.covar1 = (double[][]) params.get("covar1");
        this.tau1 = ((Double) params.get("tau1")).doubleValue();

        this.mu2 = (double[]) params.get("mu2");
        this.covar2 = (double[][]) params.get("covar2");
        this.tau2 = ((Double) params.get("tau2")).doubleValue();

        gaussian1 = (MultivariateGaussian) params.get("gaussian1");
        gaussian2 = (MultivariateGaussian) params.get("gaussian2");
    }

    /**
     * getProbability
     *
     * @param x double
     * @param y double
     * @return double
     */
    public double getProbability(double x, double y) {
        double prob1 = gaussian1.getProbability(x, y);
        double prob2 = gaussian2.getProbability(x, y);

        double prob = tau1 * prob1 + tau2 * prob2;
        return prob;
        //        return (tau1 * prob1) + (tau2 * prob2);
    }

    /**
     * getData
     *
     * @return double[][]
     */
    public double[][] getData() {

        double[] xSeries = new double[numDataPoints];
        double[] ySeries = new double[numDataPoints];

        for (int i = 0; i < numDataPoints; i++) {
            double rand = Math.random();
            double[] randData;
            if (rand < tau1) {
                randData = gaussian1.getDataPoint();
            } else {
                randData = gaussian2.getDataPoint();
            }

            double xVal = randData[0];
            double yVal = randData[1];

            xSeries[i] = xVal;
            ySeries[i] = yVal;
        }
        double[][] data = {xSeries, ySeries};
        return data;

    }

    double calculateCovariance(double variance1, double variance2, double corCoef) {
        double sd1 = Math.sqrt(variance1);
        double sd2 = Math.sqrt(variance2);
        return sd1 * sd2 * corCoef;
    }


}
