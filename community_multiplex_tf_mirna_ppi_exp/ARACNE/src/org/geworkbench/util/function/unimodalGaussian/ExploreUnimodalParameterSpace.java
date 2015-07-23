package org.geworkbench.util.function.unimodalGaussian;

import org.geworkbench.util.function.MultivariateGaussian;

import java.util.Vector;

public class ExploreUnimodalParameterSpace {
    public ExploreUnimodalParameterSpace() {
    }

    public MultivariateGaussian[] getAllParams() {
        MultivariateGaussian gaussian;
        Vector allGaussians = new Vector();

        double[] mu = {0, 0};

        double d = 1.0;

        double[] sdXs = {.1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
        //        double[] sdXs = {1.0};

        double[] varianceXs = new double[sdXs.length];
        for (int i = 0; i < sdXs.length; i++) {
            varianceXs[i] = Math.pow(sdXs[i], 2);
        }
        //            Math.pow(.1, 2), Math.pow(.5, 2), Math.pow(1.0, 2)};
        double[] varianceYs = varianceXs;
        //        double[] rhos = {
        //             -.8, -.6, -.4, -.2, 0, .2, .4, .6, .8};
        //        double[] rhos = new double[1000];
        //        for(int i = 0; i < rhos.length; i++){
        //            rhos[i] = Math.random() * .8 + .1;
        //        }

        double covariance;

        for (int varXCtr = 0; varXCtr < varianceXs.length; varXCtr++) {
            double varianceX = varianceXs[varXCtr];

            for (int varYCtr = 0; varYCtr < varianceYs.length; varYCtr++) {
                double varianceY = varianceYs[varYCtr];

                //                for (int rhoCtr = 0; rhoCtr < rhos.length; rhoCtr++) {
                for (int rhoCtr = 0; rhoCtr < 10; rhoCtr++) {
                    //                    double rho = rhos[rhoCtr];
                    double rho = Math.random() * .8 + .1;

                    covariance = calculateCovariance(varianceX, varianceY, rho);
                    gaussian = new MultivariateGaussian(mu, varianceX, varianceY, covariance);
                    allGaussians.add(gaussian);
                }
            }
        }
        MultivariateGaussian[] tmp = new MultivariateGaussian[0];
        return (MultivariateGaussian[]) allGaussians.toArray(tmp);
    }

    double calculateCovariance(double variance1, double variance2, double corCoef) {
        double sd1 = Math.sqrt(variance1);
        double sd2 = Math.sqrt(variance2);
        return sd1 * sd2 * corCoef;
    }


}
