package org.geworkbench.util.function.bimodalGaussian;

import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.function.MultivariateGaussianBimodal;
import org.geworkbench.util.function.functionParameters.bimodalGaussianParams.BimodalGaussianParamsDefault;
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
        int fileIndex = Integer.parseInt(args[0]);
        //        int fileIndex = 0;
        String directoryRoot = "/users/aam2110";
        //        String directoryRoot = "Y:";
        File directoryBase = new File(directoryRoot + "/MI/BimodalGaussian/BimodalGaussianMIs");

        File[] allFiles = directoryBase.listFiles();
        File readFile = allFiles[fileIndex];
        File writeFile = new File(directoryBase + "/SigmaResults/" + readFile.getName());
        writeFile.getParentFile().mkdirs();

        if (readFile.isDirectory()) {
            return;
        }
        calculateMis(readFile, writeFile, 340);

    }

    void calculateMis(File readFile, File writeFile, int numDataPoints) {
        Vector fileData = FileUtil.readFile(readFile);

        try {
            FileWriter writer = new FileWriter(writeFile);
            for (int lineIndex = 0; lineIndex < fileData.size(); lineIndex++) {
                String[] lineData = (String[]) fileData.get(lineIndex);

                BimodalGaussianParamsDefault params = new BimodalGaussianParamsDefault(lineData);

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
                    e.printStackTrace();
                    //                    System.out.println("Exception Covar 1 " + gaussianCovar1 + " covar 2 " + gaussianCovar2);
                    //                    writer.write("\n");
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
