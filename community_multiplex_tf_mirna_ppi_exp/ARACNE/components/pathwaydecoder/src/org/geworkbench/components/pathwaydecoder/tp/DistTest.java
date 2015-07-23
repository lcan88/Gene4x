package org.geworkbench.components.pathwaydecoder.tp;

import cern.jet.stat.Probability;
import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.gaussian.TruncatedGaussianBivariate;
import org.geworkbench.util.function.gaussian.TruncatedGaussianUnivariate;

public class DistTest {
    public DistTest() {
    }

    public static void main(String[] args) {
        new DistTest().doIt();
    }

    void doIt() {
        //        double mean = 0;
        //        double variance = .27 * .27;

        //        double integral = Probability.normal(mean, variance, 2 * .27);
        double integral = Probability.normal(0.0, 1.0, 3.0);
        double tail = 1.0 - integral;
        double uniIntegral = (1.0 - 2 * tail);
        System.out.println("Integral " + uniIntegral);

        double trueBivariateIntegral = uniIntegral * uniIntegral;
        System.out.println("True bivariate integral " + trueBivariateIntegral);

        //        double erf = Probability.errorFunction(2.0);
        //        System.out.println("Erf " + erf);

        double sigmaU = .11;
        TruncatedGaussianUnivariate g = new TruncatedGaussianUnivariate(sigmaU);
        FunctionIntegrator i = new FunctionIntegrator();

        integral = i.integrate(g, -5.0 * sigmaU, 5.0 * sigmaU);
        System.out.println("Univariate Integral " + integral);

        double sigma = .11;

        TruncatedGaussianBivariate bg = new TruncatedGaussianBivariate(sigma);
        FunctionIntegrator inte = new FunctionIntegrator();
        //        integral = inte.integrate(bg, -2.0 * sigma, 2.0 * sigma, -2.0 * sigma, 2.0 * sigma);
        integral = inte.integrate(bg, -5.0 * sigma, 5.0 * sigma, -5.0 * sigma, 5.0 * sigma);
        //        integral = i.integrate(g, -50, 50, -50, 50);
        System.out.println("Bivariate Integral " + integral);
    }
}
