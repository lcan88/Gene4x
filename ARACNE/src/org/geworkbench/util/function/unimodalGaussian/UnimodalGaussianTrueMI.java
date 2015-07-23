package org.geworkbench.util.function.unimodalGaussian;

import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.MultivariateGaussian;

import java.io.File;
import java.io.FileWriter;

public class UnimodalGaussianTrueMI {

    double minIntegrationValX;
    double minIntegrationValY;
    double maxIntegrationValX;
    double maxIntegrationValY;

    public UnimodalGaussianTrueMI() {
    }


    public static void main(String[] args) {
        new UnimodalGaussianTrueMI().calcualteMis(args);
    }

    void calcualteMis(String[] args) {
        ExploreUnimodalParameterSpace expl = new ExploreUnimodalParameterSpace();
        MultivariateGaussian[] allGaussians = expl.getAllParams();


        //        String header = BimodalGaussianParamsBase.getHeader() +
        //            "\tMI_pt01\tMI_pt1\tMiDiff";

        //        String dirBase = "Y:";
        //        String dirBase = "/razor/1/aam2110";
        String dirBase = args[0];
        try {
            File miFile = new File(dirBase + "/MI/UnimodalGaussian/UnimodalGaussianMIs/UnimodalGaussianMis.txt");
            File badFile = new File(dirBase + "/MI/UnimodalGaussian/UnimodalGaussianMIs/Bad/BadMis.txt");
            badFile.getParentFile().mkdirs();

            FileWriter miFileWriter = new FileWriter(miFile);
            FileWriter badFileWriter = new FileWriter(badFile);
            //            miFileWriter.write(header + "\n");
            //            badFileWriter.write(header + "\n");

            for (int paramCtr = 0; paramCtr < allGaussians.length; paramCtr++) {
                MultivariateGaussian curGaussian = allGaussians[paramCtr];

                calculateMinMaxIntegrationVals(curGaussian);

                //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
                FunctionIntegrator integrator = new FunctionIntegrator();
                //                double mi_pt01 = integrator.getMI(curGaussian,
                //                                                  minIntegrationValX,
                //                                                  maxIntegrationValX,
                //                                                  minIntegrationValY,
                //                                                  maxIntegrationValY, .01);
                double mi_pt1 = integrator.getMI(curGaussian, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .1);
                //                double mi_diff = mi_pt01 - mi_pt1;

                //                if (mi_diff < .1) {
                miFileWriter.write(curGaussian.toString() + "\t" + mi_pt1 + "\n");
                System.out.print(curGaussian.toString() + "\t" + mi_pt1 + "\n");

                //                    miFileWriter.write(curGaussian.toString() + "\t" + mi_pt01 +
                //                                       "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                //                    System.out.print(curGaussian.toString() + "\t" + mi_pt01 +
                //                                     "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                //                }
                //                else {
                //                    badFileWriter.write(curGaussian.toString() + "\t" + mi_pt01 +
                //                                        "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                //                }

            }
            miFileWriter.close();
            badFileWriter.close();
        } catch (Exception e) {
            System.out.println("Exception\t");
            e.printStackTrace();
        }
    }

    void calculateMinMaxIntegrationVals(MultivariateGaussian gauss) {
        //Calculate the integration range

        double[][] covar = gauss.getCovarianceMatrix();

        double varX = covar[0][0];
        double varY = covar[1][1];

        minIntegrationValX = gauss.getMu()[0] - (Math.sqrt(varX) * 4);
        maxIntegrationValX = gauss.getMu()[0] + (Math.sqrt(varX) * 4);

        minIntegrationValY = gauss.getMu()[1] - (Math.sqrt(varY) * 4);
        maxIntegrationValY = gauss.getMu()[1] + (Math.sqrt(varY) * 4);

        maxIntegrationValX = maxIntegrationValY = Math.max(maxIntegrationValX, maxIntegrationValY);
        minIntegrationValX = minIntegrationValY = Math.min(minIntegrationValX, minIntegrationValY);

    }

}
