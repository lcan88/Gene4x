package org.geworkbench.util.function.gaussian;

import cern.jet.random.Normal;
import edu.cornell.lassp.houle.RngPack.RandomJava;
import org.geworkbench.util.function.IProbabilityFunctionUnivariate;

public class TruncatedGaussianUnivariate implements IProbabilityFunctionUnivariate {
    double mean;
    double sigma;
    double sigma2x2;
    Normal n;

    public TruncatedGaussianUnivariate(double sigma) {
        this(0, sigma);
    }

    public TruncatedGaussianUnivariate(double mean, double sigma) {
        this.mean = mean;
        this.sigma = sigma;
        this.sigma2x2 = 2 * sigma * sigma;
        n = new Normal(mean, sigma, new RandomJava());
    }

    /**
     * getProbability
     *
     * @param x double
     * @return double
     */
    public double getProbability(double x) {
        //Integral from -2 * sigma to 2 * sigma
        //        double integral2Sigma = 0.9544997361036416;

        //Integral from -3 * sigma to 3 * sigma
        //        double integral3Sigma = 0.9973002039367398;

        double d = x - mean;

        if (Math.abs(d) > 3 * sigma) {

            //          if(Math.abs(d) > 2 * sigma){
            return 0;
        } else {

            //just return it totally unnormalized
            double d2 = d * d;
            double prob = Math.exp(-d2 / sigma2x2);

            //            prob /= (Math.sqrt(2 * Math.PI * sigma * sigma));
            //            prob /= integral2Sigma;


            //            double prob = n.pdf(d);
            //            prob /= integral2Sigma;
            //            prob /= integral3Sigma;


            return prob;
        }
    }
}
