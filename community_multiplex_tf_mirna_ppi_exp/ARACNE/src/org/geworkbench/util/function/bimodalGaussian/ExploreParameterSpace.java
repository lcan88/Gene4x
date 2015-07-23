package org.geworkbench.util.function.bimodalGaussian;

import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsDefault;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class ExploreParameterSpace {
    public ExploreParameterSpace() {
    }

    public BimodalGaussianParamsBase[] getAllParams() {
        BimodalGaussianParamsDefault params = new BimodalGaussianParamsDefault();
        Vector allParams = new Vector();

        double[] mu1 = {0, 0};
        double[] mu2 = new double[2];

        double d = 1.0;


        double[] varianceX1s = {Math.pow(.1, 2), Math.pow(.5, 2), Math.pow(1.0, 2)};
        double[] varianceY1s = varianceX1s;
        double[] rho1s = {-.8, -.6, -.4, -.2, 0, .2, .4, .6, .8};

        double[] varianceX2s = varianceX1s;
        double[] varianceY2s = varianceX1s;
        double[] rho2s = rho1s;

        double covariance1;
        double covariance2;

        double[] taus = {.25, .5, .75};
        double[] thetas = {0.0, 0.25};

        //Gaussian 1 variances
        for (int varX1Ctr = 0; varX1Ctr < varianceX1s.length; varX1Ctr++) {
            double varianceX1 = varianceX1s[varX1Ctr];

            for (int varY1Ctr = 0; varY1Ctr < varianceY1s.length; varY1Ctr++) {
                double varianceY1 = varianceY1s[varY1Ctr];

                for (int rho1Ctr = 0; rho1Ctr < rho1s.length; rho1Ctr++) {
                    double rho1 = rho1s[rho1Ctr];

                    covariance1 = calculateCovariance(varianceX1, varianceY1, rho1);
                    params.setGaussian1(new MultivariateGaussian(mu1, varianceX1, varianceY1, covariance1));

                    //Gaussian 2 variances
                    for (int varX2Ctr = 0; varX2Ctr < varianceX2s.length; varX2Ctr++) {
                        double varianceX2 = varianceX2s[varX2Ctr];

                        for (int varY2Ctr = 0; varY2Ctr < varianceY2s.length; varY2Ctr++) {
                            double varianceY2 = varianceY2s[varY2Ctr];

                            for (int rho2Ctr = 0; rho2Ctr < rho2s.length; rho2Ctr++) {
                                double rho2 = rho2s[rho2Ctr];

                                covariance2 = calculateCovariance(varianceX2, varianceY2, rho2);

                                for (int thetaCtr = 0; thetaCtr < thetas.length; thetaCtr++) {
                                    double theta = thetas[thetaCtr];

                                    mu2[0] = Math.cos(theta * Math.PI);
                                    mu2[1] = Math.sin(theta * Math.PI);
                                    params.setGaussian2(new MultivariateGaussian(mu2, varianceX2, varianceY2, covariance2));

                                    for (int tauCtr = 0; tauCtr < taus.length; tauCtr++) {
                                        double tau = taus[tauCtr];

                                        params.setTau1(tau);
                                        allParams.add(new BimodalGaussianParamsDefault(params));
                                        //                                        System.out.println(new BimodalGaussianParamsDefault(params).toString());
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        BimodalGaussianParamsDefault[] tmp = {new BimodalGaussianParamsDefault()};
        return (BimodalGaussianParamsDefault[]) allParams.toArray(tmp);

    }

    void test() {
        try {
            BimodalGaussianParamsBase[] allParams = getAllParams();
            File writeFile = new File("Z:/BimodalGaussianParamsAll.txt");
            FileWriter writer = new FileWriter(writeFile);
            writer.write("MuX1\tMuY1\tVarX1\tVarY1\tCovar1\tMuX2\tMuY2\tVarX2\tVarY2\tCovar2\tTau1\tTau2\n");
            for (int i = 0; i < allParams.length; i++) {
                BimodalGaussianParamsBase curParams = allParams[i];
                writer.write(curParams.toString() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ExploreParameterSpace().test();
    }

    double calculateCovariance(double variance1, double variance2, double corCoef) {
        double sd1 = Math.sqrt(variance1);
        double sd2 = Math.sqrt(variance2);
        return sd1 * sd2 * corCoef;
    }
}
