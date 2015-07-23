package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParams3;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;

public class BimodalGaussianMI {
    BimodalGaussianParamsBase params;
    double minIntegrationValX;
    double minIntegrationValY;

    double maxIntegrationValX;
    double maxIntegrationValY;

    public BimodalGaussianMI() {
    }

    public static void main(String[] args) {
        new BimodalGaussianMI().calculateMi();
        //        new BimodalGaussianMI().doNew();
    }

    void calculateMi() {

        BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams("BimodalGaussianParams4");
        double gaussianCovar1 = 0.774596669;
        double gaussianCovar2 = 0.774596669;


        params.setCovariance1(gaussianCovar1);
        params.setCovariance2(gaussianCovar2);

        MultivariateGaussianBimodal bimodalGaussian = new MultivariateGaussianBimodal(params);

        double var1x = params.getCovar1()[0][0];
        double var1y = params.getCovar1()[1][1];

        double var2x = params.getCovar2()[0][0];
        double var2y = params.getCovar2()[1][1];

        //Calculate the integration range
        double minIntegrationValX1 = params.getMu1()[0] - (Math.sqrt(var1x) * 4);
        double maxIntegrationValX1 = params.getMu1()[0] + (Math.sqrt(var1x) * 4);

        double minIntegrationValY1 = params.getMu1()[1] - (Math.sqrt(var1y) * 4);
        double maxIntegrationValY1 = params.getMu1()[1] + (Math.sqrt(var1y) * 4);

        double minIntegrationValX2 = params.getMu2()[0] - (Math.sqrt(var2x) * 4);
        double maxIntegrationValX2 = params.getMu2()[0] + (Math.sqrt(var2x) * 4);

        double minIntegrationValY2 = params.getMu2()[1] - (Math.sqrt(var2y) * 4);
        double maxIntegrationValY2 = params.getMu2()[1] + (Math.sqrt(var2y) * 4);

        double minIntegrationValX = Math.min(minIntegrationValX1, minIntegrationValX2);
        double maxIntegrationValX = Math.max(maxIntegrationValX1, maxIntegrationValX2);

        double minIntegrationValY = Math.min(minIntegrationValY1, minIntegrationValY2);
        double maxIntegrationValY = Math.max(maxIntegrationValY1, maxIntegrationValY2);

        maxIntegrationValX = maxIntegrationValY = Math.max(maxIntegrationValX, maxIntegrationValY);
        minIntegrationValX = minIntegrationValY = Math.min(minIntegrationValX, minIntegrationValY);

        try {
            MultivariateGaussianBimodal gauss = new MultivariateGaussianBimodal(params);

            //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
            FunctionIntegrator integrator = new FunctionIntegrator();
            double mi = integrator.getMI(gauss, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .001);

            System.out.println(mi + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void calculateMinMaxIntegrationVals() {
        //Calculate the integration range

        double[][] covar1 = params.getCovar1();
        double[][] covar2 = params.getCovar2();

        double var1x = covar1[0][0];
        double var1y = covar1[1][1];

        double var2x = covar2[0][0];
        double var2y = covar2[1][1];

        double minIntegrationValX1 = params.getMu1()[0] - (Math.sqrt(var1x) * 4);
        double maxIntegrationValX1 = params.getMu1()[0] + (Math.sqrt(var1x) * 4);

        double minIntegrationValY1 = params.getMu1()[1] - (Math.sqrt(var1y) * 4);
        double maxIntegrationValY1 = params.getMu1()[1] + (Math.sqrt(var1y) * 4);

        double minIntegrationValX2 = params.getMu2()[0] - (Math.sqrt(var2x) * 4);
        double maxIntegrationValX2 = params.getMu2()[0] + (Math.sqrt(var2x) * 4);

        double minIntegrationValY2 = params.getMu2()[1] - (Math.sqrt(var2y) * 4);
        double maxIntegrationValY2 = params.getMu2()[1] + (Math.sqrt(var2y) * 4);

        minIntegrationValX = Math.min(minIntegrationValX1, minIntegrationValX2);
        maxIntegrationValX = Math.max(maxIntegrationValX1, maxIntegrationValX2);

        minIntegrationValY = Math.min(minIntegrationValY1, minIntegrationValY2);
        maxIntegrationValY = Math.max(maxIntegrationValY1, maxIntegrationValY2);

        maxIntegrationValX = maxIntegrationValY = Math.max(maxIntegrationValX, maxIntegrationValY);
        minIntegrationValX = minIntegrationValY = Math.min(minIntegrationValX, minIntegrationValY);

    }

    void doNew() {
        int numCovarianceSteps = 10;
        //        BimodalGaussianParamsBase[] params = {
        //            new BimodalGaussianParams1(), new BimodalGaussianParams2(),
        //            new BimodalGaussianParams3(), new BimodalGaussianParams4()};
        BimodalGaussianParamsBase[] allParams = {new BimodalGaussianParams3()};
        for (int i = 0; i < allParams.length; i++) {
            params = allParams[i];
            String paramsName = params.getClass().getName();
            //            if(paramsName.length() > paramsName.lastIndexOf(".")){
            paramsName = paramsName.substring(paramsName.lastIndexOf(".") + 1, paramsName.length());
            //            }

            double[][] covar1 = params.getCovar1();
            double[][] covar2 = params.getCovar2();

            double var1x = covar1[0][0];
            double var1y = covar1[1][1];

            double var2x = covar2[0][0];
            double var2y = covar2[1][1];

            double maxCovariance1 = Math.sqrt(var1x) * Math.sqrt(var1y);
            double minCovariance1 = -maxCovariance1;
            double covariance1Step = (maxCovariance1 - minCovariance1) / (double) numCovarianceSteps;

            double maxCovariance2 = Math.sqrt(var2x) * Math.sqrt(var2y);
            double minCovariance2 = -maxCovariance2;
            double covariance2Step = (maxCovariance2 - minCovariance2) / (double) numCovarianceSteps;

            calculateMinMaxIntegrationVals();

            minCovariance1 = 0.069282032302755;
            minCovariance2 = 0.707106781186547;
            maxCovariance1 = minCovariance1 + .001;
            maxCovariance2 = minCovariance2 + .001;

            for (double covariance1 = minCovariance1; covariance1 < maxCovariance1; covariance1 += covariance1Step) {
                params.setCovariance1(covariance1);
                for (double covariance2 = minCovariance2; covariance2 < maxCovariance2; covariance2 += covariance2Step) {
                    params.setCovariance2(covariance2);

                    try {
                        MultivariateGaussianBimodal gauss = new MultivariateGaussianBimodal(params);

                        //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
                        FunctionIntegrator integrator = new FunctionIntegrator();
                        double mi_pt01 = integrator.getMI(gauss, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .01);
                        double mi_pt1 = integrator.getMI(gauss, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .1);
                        double mi_diff = mi_pt01 - mi_pt1;
                        double covar1ToPrint = params.getCovar1()[1][0];
                        double covar2ToPrint = params.getCovar2()[1][0];
                        //                                    System.out.println();
                        //System.out.println("Params\t" + paramsName + "\tCovar1\t" + covariance1 + "\tCovar2\t" + covariance2 + "\tMI\t" + mi);
                        System.out.println("Params\t" + paramsName + "\tCovar1\t" + covar1ToPrint + "\tCovar2\t" + covar2ToPrint + "\tMI .01\t" + mi_pt01 + "\tMI .1\t" + mi_pt1 + "\tMI diff\t" + mi_diff);

                    } catch (Exception e) {
                        System.out.println("Exception\t");
                    }

                }
            }
        }

    }

    /*
         void doIt() {
        double[] mu1 = {
            0.0, 0.0};
        double[] mu2 = {
            -.5, 2.0};

        double tau1 = 0.4;
        double tau2 = 1.0 - tau1;

        for (double var_x1 = 0.05; var_x1 <= .5; var_x1 += 0.05) {
            for (double var_y1 = 0.05; var_y1 <= .5; var_y1 += 0.05) {
                for (double covar_xy1 = -Math.max(var_x1, var_y1);
                     covar_xy1 <= Math.max(var_x1, var_y1); covar_xy1 += .05) {
                    for (double var_x2 = 0.05; var_x2 <= .5; var_x2 += 0.05) {
     for (double var_y2 = 0.05; var_y2 <= .5; var_y2 += 0.05) {
                            for (double covar_xy2 = -Math.max(var_x2, var_y2);
                                 covar_xy2 <= Math.max(var_x2, var_y2);
                                 covar_xy2 += .05) {
                                double[][] covar1 = {
                                    {
                                    var_x1, covar_xy1}
                                    , {
                                    covar_xy1, var_y1}
                                };
                                double[][] covar2 = {
                                    {
                                    var_x2, covar_xy2}
                                    , {
                                    covar_xy2, var_y2}
                                };

                                try {
                                    MultivariateGaussianBimodal gauss = new
                                        MultivariateGaussianBimodal(
                                        mu1,
                                        covar1, tau1, mu2, covar2, tau2);

                                    //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
                                    FunctionIntegrator integrator = new
                                        FunctionIntegrator();
                                    double mi = integrator.getMI(gauss, -2.0,
                                        3.0, -2.0, 3.0);
                                    System.out.println();
                                    System.out.println("Mu x1\t" + mu1[0] +
                                        "\tMu y1\t" + mu1[1] +
                                        "\tVar x1\t" + var_x1 + "\tVar y1\t" +
                                        var_y1 +
     "\tCovar 1\t" + covar_xy1 + "\tTau1\t" +
                                        tau1 +
                                        "\tMu x2\t" + mu2[0] + "\tMu y2\t" +
                                        mu2[1] +
                                        "\tVar x2\t" + var_x2 + "\tVar y2\t" +
                                        var_y2 +
     "\tCovar 2\t" + covar_xy2 + "\tTau2\t" +
                                        tau2 +
                                        "\tMI\t" +
                                        mi);

                                }
                                catch (Exception e) {
                                    System.out.print("Exception\t");
                                }

                            }
                        }
                    }

                }

            }
        }
         }
     */
}
