package org.geworkbench.util.function.bimodalGaussian;

import org.geworkbench.util.function.FunctionIntegrator;
import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;

import java.io.File;
import java.io.FileWriter;

public class BimodalGaussianTrueMI {
    double minIntegrationValX;
    double minIntegrationValY;
    double maxIntegrationValX;
    double maxIntegrationValY;

    public BimodalGaussianTrueMI() {
    }

    public static void main(String[] args) {
        int startIndex = Integer.parseInt(args[0]);
        int endIndex = Integer.parseInt(args[1]);

        new BimodalGaussianTrueMI().calcualteMis(startIndex, endIndex);
    }

    void calcualteMis(int startIndex, int endIndex) {
        ExploreParameterSpace expl = new ExploreParameterSpace();
        BimodalGaussianParamsBase[] allParams = expl.getAllParams();

        String header = BimodalGaussianParamsBase.getHeader() + "\tMI";
        //            "\tMI_pt01\tMI_pt1\tMiDiff";

        String dirBase = "/users/aam2110";
        try {
            File miFile = new File(dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/BimodalGaussianMis_" + startIndex + "_" + endIndex + ".txt");
            //            File badFile = new File(
            //                dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/Bad/BadMis_" + startIndex + "_" + endIndex + ".txt");
            miFile.getParentFile().mkdirs();

            FileWriter miFileWriter = new FileWriter(miFile);
            //            FileWriter badFileWriter = new FileWriter(badFile);
            //            miFileWriter.write(header + "\n");
            //            badFileWriter.write(header + "\n");

            for (int paramCtr = startIndex; paramCtr < endIndex; paramCtr++) {
                BimodalGaussianParamsBase curParams = allParams[paramCtr];
                curParams.initializeHashMap();
                calculateMinMaxIntegrationVals(curParams);

                MultivariateGaussianBimodal gauss = new MultivariateGaussianBimodal(curParams);

                //MultivariateGaussian gauss = new MultivariateGaussian(mu1, covar1);
                FunctionIntegrator integrator = new FunctionIntegrator();
                //                double mi_pt01 = integrator.getMI(gauss,
                //                                                  minIntegrationValX,
                //                                                  maxIntegrationValX,
                //                                                  minIntegrationValY,
                //                                                  maxIntegrationValY, .01);
                double mi_pt1 = integrator.getMI(gauss, minIntegrationValX, maxIntegrationValX, minIntegrationValY, maxIntegrationValY, .1);
                //                double mi_diff = mi_pt01 - mi_pt1;

                //                if (mi_diff < .1) {
                //                    miFileWriter.write(curParams.toString() + "\t" + mi_pt01 +
                //                                       "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                //                    System.out.print(curParams.toString() + "\t" + mi_pt01 +
                //                                     "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                miFileWriter.write(curParams.toString() + "\t" + "\t" + mi_pt1 + "\t" + "\n");
                System.out.print(curParams.toString() + "\t" + "\t" + mi_pt1 + "\t" + "\n");


                //                }
                //                else {
                //                    badFileWriter.write(curParams.toString() + "\t" + mi_pt01 +
                //                                        "\t" + mi_pt1 + "\t" + mi_diff + "\n");
                //                }

            }
            miFileWriter.close();
            //            badFileWriter.close();
        } catch (Exception e) {
            System.out.println("Exception\t");
            e.printStackTrace();
        }
    }

    void calculateMinMaxIntegrationVals(BimodalGaussianParamsBase params) {
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

}
