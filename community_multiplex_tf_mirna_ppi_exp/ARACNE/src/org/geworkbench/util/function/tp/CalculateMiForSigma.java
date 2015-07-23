package org.geworkbench.util.function.tp;

import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsBase;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsFactory;
import org.geworkbench.util.function.mi.FunctionMi;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class CalculateMiForSigma {
    public CalculateMiForSigma() {
    }

    public static void main(String[] args) {
        new CalculateMiForSigma().calculateMis(args);
    }

    void calculateMis(String[] args) {
        try {
            int baseType = Integer.parseInt(args[0]);
            //            int paramsIndex = Integer.parseInt(args[1]);
            int paramsIndex = 4;
            int startIndex = Integer.parseInt(args[2]);
            int endIndex = Integer.parseInt(args[3]);

            String dirBase;
            if (baseType == 2) {
                dirBase = "/users/aam2110";
            } else {
                dirBase = "Z:/Adam/";
            }
            File readFile = new File(dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/NoDegenerates/Params" + paramsIndex + ".txt");
            File writeFile = new File(dirBase + "/MI/BimodalGaussian/BimodalGaussianMIs/MpiTest/Accurate/MisForSigmas/Params" + paramsIndex + "/" + startIndex + "_" + endIndex + ".txt");
            writeFile.getParentFile().mkdirs();
            FileWriter writer = null;

            writer = new FileWriter(writeFile);

            Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(readFile);

            String[] headers = (String[]) fileData.get(0);
            for (int i = 0; i < headers.length; i++) {
                System.out.print(headers[i] + "\t");
                //                writer.write(headers[i] + "\t");
            }

            int numDataPoints = 340;

            //            for (int lineIndex = startIndex; lineIndex < endIndex; lineIndex++) {
            for (int lineIndex = 1; lineIndex < fileData.size(); lineIndex++) {
                String[] lineData = (String[]) fileData.get(lineIndex);
                String paramsName = lineData[0];
                double gaussianCovar1 = Double.parseDouble(lineData[1]);
                double gaussianCovar2 = Double.parseDouble(lineData[2]);
                double expectedMi = Double.parseDouble(lineData[3]);

                BimodalGaussianParamsBase params = BimodalGaussianParamsFactory.getParams(paramsName);
                params.setCovariance1(gaussianCovar1);
                params.setCovariance2(gaussianCovar2);
                MultivariateGaussianBimodal gaussian = new MultivariateGaussianBimodal(params);

                for (int i = 0; i < lineData.length; i++) {
                    System.out.print(lineData[i] + "\t");
                    writer.write(lineData[i] + "\t");
                }

                gaussian.setNumDataPoints(numDataPoints);
                double[][] data;
                try {
                    data = gaussian.getData();
                } catch (Exception e) {
                    //                    e.printStackTrace();
                    System.out.println("Exception Covar 1 " + gaussianCovar1 + " covar 2 " + gaussianCovar2);
                    writer.write("\n");
                    continue;
                }

                for (double sigma = .005; sigma < .5; sigma += .005) {
                    double mi = FunctionMi.getMi(data, sigma);
                    writer.write(mi + "\t");
                    System.out.print(mi + "\t");
                }

                System.out.println();
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
