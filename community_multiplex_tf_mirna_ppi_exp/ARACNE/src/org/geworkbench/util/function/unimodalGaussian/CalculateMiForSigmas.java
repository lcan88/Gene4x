package org.geworkbench.util.function.unimodalGaussian;

import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.function.MultivariateGaussian;
import org.geworkbench.util.function.functionParameters.unimodalGaussianParams.UnimodalGaussianParamsDefault;
import org.geworkbench.util.function.mi.FunctionMi;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class CalculateMiForSigmas {
    public CalculateMiForSigmas() {
    }

    public static void main(String[] args) {
        new CalculateMiForSigmas().calculateMis(args);
    }

    void calculateMis(String[] args) {
        //        String fileBase = "/users/aam2110";
        //        String fileBase = args[0];
        File readFile = new File(args[0]);

        String writeDir = args[1];

        int numSamples = Integer.parseInt(args[2]);

        //        File readFile = new File(fileBase + "/MI/UnimodalGaussian/UnimodalGaussianMIs/UnimodalGaussianMis.txt");

        if (args.length > 3) {
            int startIndex = Integer.parseInt(args[3]);
            int endIndex = Integer.parseInt(args[4]);
            calculateMis(readFile, writeDir, numSamples, startIndex, endIndex);
        } else {
            calculateMis(readFile, writeDir, numSamples);
        }
    }

    void calculateMis(File readFile, String writeDir, int numSamples, int startIndex, int endIndex) {
        File writeFile = new File(writeDir + "/UnimodalGaussianMiForSigmas_" + startIndex + "_" + endIndex + ".txt");
        writeFile.getParentFile().mkdirs();

        Vector fileData = FileUtil.readFile(readFile, 0, startIndex, endIndex);
        calculateMis(fileData, writeFile, numSamples);
    }

    void calculateMis(File readFile, String writeDir, int numSamples) {
        File writeFile = new File(writeDir + "/UnimodalGaussianMiForSigmas.txt");
        writeFile.getParentFile().mkdirs();

        Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(readFile, 0);
        calculateMis(fileData, writeFile, numSamples);
    }


    void calculateMis(Vector fileData, File writeFile, int numDataPoints) {

        try {
            FileWriter writer = new FileWriter(writeFile);
            for (int lineIndex = 0; lineIndex < fileData.size(); lineIndex++) {

                if (lineIndex % 10 == 0) {
                    System.out.println("Calculated " + lineIndex);
                }
                String[] lineData = (String[]) fileData.get(lineIndex);

                UnimodalGaussianParamsDefault params = new UnimodalGaussianParamsDefault(lineData);

                MultivariateGaussian gaussian = new MultivariateGaussian(params);

                for (int i = 0; i < lineData.length; i++) {
                    //                    System.out.print(lineData[i] + "\t");
                    writer.write(lineData[i] + "\t");
                }

                gaussian.setNumDataPoints(numDataPoints);
                double[][] data;
                try {
                    data = gaussian.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                    //                    System.out.println("Exception Covar 1 " + gaussianCovar1 + " covar 2 " + gaussianCovar2);
                    writer.write("\n");
                    continue;
                }

                for (double sigma = .01; sigma < .3; sigma += .01) {
                    //                for(double sigma = .09; sigma < .15; sigma += .02){
                    double mi = FunctionMi.getMi(data, sigma);
                    writer.write(mi + "\t");
                    //                    System.out.print(mi + "\t");
                }

                //                System.out.println();
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
