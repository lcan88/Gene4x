package org.geworkbench.util.function.gaussian;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import org.geworkbench.util.function.IProbabilityFunctionBivariate;

public class TruncatedGaussianBivariate implements IProbabilityFunctionBivariate {
    double sigmaX;
    double sigmaY;
    double muX;
    double muY;
    double sigma2x2;

    MultiGaussianDistribution mgd;

    public TruncatedGaussianBivariate(double sigma) {
        this(0.0, 0.0, sigma, sigma);
    }

    public TruncatedGaussianBivariate(double muX, double muY, double sigmaX, double sigmaY) {
        this.sigmaX = sigmaX;
        this.sigmaY = sigmaY;
        this.muX = muX;
        this.muY = muY;

        this.sigma2x2 = 2 * sigmaX * sigmaY;

        double[] mu = {muX, muY};

        double[][] covarianceMatrix = new double[2][2];

        covarianceMatrix[0][0] = sigmaX * sigmaX;
        covarianceMatrix[1][1] = sigmaY * sigmaY;

        covarianceMatrix[1][0] = 0;
        covarianceMatrix[0][1] = 0;

        mgd = new MultiGaussianDistribution(mu, covarianceMatrix);
    }

    /**
     * getProbability
     *
     * @param x double
     * @param y double
     * @return double
     */
    public double getProbability(double x, double y) {
        //Integral from -2 * sigma to 2 * sigma box
        //        double integral2Sigma = 0.9110697462;

        //Integral from -3 * sigma to 3 * sigma box
        //        double integral3Sigma = 0.9946076967722628;


        //        double dxy2 = (dx * dx + dy * dy);
        if (Math.abs(x) > 3 * sigmaX || Math.abs(y) > 3 * sigmaY) {

            //        if(Math.abs(x) > 2 * sigmaX || Math.abs(y) > 2 * sigmaY){
            return 0;
        } else {
            //            double[] xy = {x, y};
            //            double prob = mgd.probability(xy);
            //            prob /= integral3Sigma;
            //            prob /= integral2Sigma;

            //return it totoally unnormalized
            //assume mean is zero
            double d2 = x * x + y * y;
            double prob = Math.exp(-d2 / sigma2x2);
            return prob;
        }


        //        double dx = x;
        //        double dy = y;

        //        double dxy2 = (dx * dx + dy * dy);
        //        if(Math.sqrt(dxy2) > 2 * sigmaX){
        //        if(dxy2 > 2 * sigmaX){
        //
        //            return 0;
        //        }else{
        //            double prob = Math.exp(- dxy2 / (2 * sigmaX * sigmaY));
        //            prob /= 2 * Math.PI * Math.sqrt(sigmaX * sigmaX * sigmaY * sigmaY);
        //            prob /= integral2Sigma;
        //            return prob;
        //        }

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
     * setNumDataPoints
     *
     * @param numDataPoints int
     */
    public void setNumDataPoints(int numDataPoints) {
    }

    /**
     * setRange
     *
     * @param min double
     * @param max double
     */
    public void setRange(double min, double max) {
    }
}
