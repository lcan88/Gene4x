package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.function.functionParameters.unimodalGaussianParams.MultivariateGaussianParams1;

public class GaussianTrueMI {
    public GaussianTrueMI() {
    }

    public static void main(String[] args) {
        new GaussianTrueMI().calculateMi();
    }


    void calculateMi() {
        int numCovarianceSteps = 10;
        MultivariateGaussianParams1 params = new MultivariateGaussianParams1();

        String paramsName = params.getClass().getName();
        //            if(paramsName.length() > paramsName.lastIndexOf(".")){
        paramsName = paramsName.substring(paramsName.lastIndexOf(".") + 1, paramsName.length());
        //            }

        double[] mu = params.getMu();
        double[][] covarianceMatrix = params.getCovarianceMatrix();

        double varX = covarianceMatrix[0][0];
        double varY = covarianceMatrix[1][1];

        double maxCovariance = Math.sqrt(varX) * Math.sqrt(varY);
        double minCovariance = -maxCovariance;
        double covarianceStep = (maxCovariance - minCovariance) / (double) numCovarianceSteps;


        //Calculate the integration range
        double minIntegrationValX = mu[0] - (Math.sqrt(varX) * 4);
        double maxIntegrationValX = mu[0] + (Math.sqrt(varX) * 4);


        double minIntegrationValY = mu[1] - (Math.sqrt(varY) * 4);
        double maxIntegrationValY = mu[1] + (Math.sqrt(varY) * 4);




        //            for (double covariance = minCovariance;
        //                 covariance < maxCovariance; covariance += covarianceStep) {
        //                params.setCovariance(covariance);
        for (double covariance = .075; covariance <= .075; covariance += covarianceStep) {
            params.setCovariance(covariance);


            try {
                MultivariateGaussian gauss = new MultivariateGaussian(params);

                //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
                FunctionIntegrator integrator = new FunctionIntegrator();
                double mi = integrator.getMI(gauss, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .01);

                //                        double covar1ToPrint = curParams.getCovar1()[0][1];
                //                        double covar2ToPrint = curParams.getCovar2()[0][1];
                //                                    System.out.println();
                //System.out.println("Params\t" + paramsName + "\tCovar1\t" + covariance1 + "\tCovar2\t" + covariance2 + "\tMI\t" + mi);
                //                        System.out.println("Params\t" + paramsName +
                //                                           "\tCovar1\t" + covar1ToPrint +
                //                                           "\tCovar2\t" + covar2ToPrint +
                //                                           "\tMI\t" + mi);
                System.out.println("MI\t" + mi);
            } catch (Exception e) {
                System.out.println("Exception\t");
            }

        }
    }


}
