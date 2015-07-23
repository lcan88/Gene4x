package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.function.MultivariateGaussianBimodal;

public class IntegrateGaussian {
    double[] gaussian1_mu1 = new double[2];
    double[] gaussian1_mu2 = new double[2];

    double[][] gaussian1_covar1 = new double[2][2];
    double[][] gaussian1_covar2 = new double[2][2];

    double gaussian1_tau1;
    double gaussian1_tau2;

    double[] gaussian2_mu1 = new double[2];
    double[] gaussian2_mu2 = new double[2];

    double[][] gaussian2_covar1 = new double[2][2];
    double[][] gaussian2_covar2 = new double[2][2];

    double gaussian2_tau1;
    double gaussian2_tau2;

    MultivariateGaussianBimodal bimodalGaussian1;
    MultivariateGaussianBimodal bimodalGaussian2;

    public IntegrateGaussian() {
        //        initializeTrial1();
    }


    public static void main(String[] args) {
        new IntegrateGaussian().doIt();
    }

    void doIt() {
        FunctionIntegrator integrator = new FunctionIntegrator();
        MultivariateGaussian mvg = new MultivariateGaussian();
        double val = integrator.getMI(mvg, -3.0, 3.0, -3.0, 3.0, .01);
        //        double gaussian1MI = integrator.getMI(bimodalGaussian1, -3.0, 3.0, -3.0, 3.0);
        //        double gaussian2MI = integrator.getMI(bimodalGaussian2, -3.0, 3.0, -3.0, 3.0);
        //        double val = integrator.getEntropy(uvg, -3.0, 3.0, -3.0, 3.0);
        //        System.out.println("Gaussian1MI\t" + gaussian1MI + "\tGaussian2MI\t" + gaussian2MI);
        System.out.println("MI " + val);
    }
}
