package org.geworkbench.util.function.bimodalGaussian;

import org.geworkbench.bison.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

public class ComparisonHistogram {
    public ComparisonHistogram() {
    }

    public static void main(String[] args) {
        new ComparisonHistogram().createHistogram(args);
    }

    void createHistogram(String[] args) {
        int startIndex = Integer.parseInt(args[0]);
        int endIndex = Integer.parseInt(args[1]);
        //        int startIndex = 0;
        //        int endIndex = 10000;
        try {
            int expectedMiPctDiffIndex = 5;
            int sigmaMiStartIndex = 6;
            int numSigmas = 0;
            //            for (double i = .005; i < .5; i += .005) {
            for (double sigma = .005; sigma < .5; sigma += .005) {
                numSigmas++;
            }

            int histogramBins = 11;

            int[][][] results = new int[histogramBins][2][numSigmas];

            String fileRoot = "/users/aam2110";
            //            String fileRoot = "Y:";
            File dataFile = new File(fileRoot + "/MI/BimodalGaussian/BimodalGaussianMIs/EstimateCompare/Comparison.txt");
            System.out.println("Reading file");
            Vector fileData = FileUtil.readFile(dataFile, 1, startIndex, endIndex);

            File writeFile = new File(fileRoot + "/MI/BimodalGaussian/BimodalGaussianMIs/ComparisonHistogram/ComparisonHistogram_" + startIndex + "_" + endIndex + ".txt");
            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);

            for (int dataCtr = 0; dataCtr < fileData.size(); dataCtr++) {
                if (dataCtr % 1000 == 0) {
                    System.out.println("Calculating " + dataCtr);
                }
                String[] lineData = (String[]) fileData.get(dataCtr);
                if (null == lineData) {
                    continue;
                }
                double expectedMiPctDiff = Double.parseDouble(lineData[expectedMiPctDiffIndex]);
                int histogramBin = (int) (Math.abs(expectedMiPctDiff) * 10);
                if (histogramBin > 10) {
                    histogramBin = 10;
                }
                for (int sigmaCtr = sigmaMiStartIndex; sigmaCtr < (numSigmas + sigmaMiStartIndex); sigmaCtr++) {
                    if (sigmaCtr < lineData.length) {
                        try {
                            int sign = Integer.parseInt(lineData[sigmaCtr]);
                            if (sign > 0) {
                                results[histogramBin][1][sigmaCtr - sigmaMiStartIndex]++;
                            } else {
                                results[histogramBin][0][sigmaCtr - sigmaMiStartIndex]++;
                            }
                        } catch (Exception e) {
                            System.out.println("Unable to parse line " + dataCtr + " column " + (sigmaCtr + sigmaMiStartIndex));
                        }
                    }
                }
            }

            double[][] pctResults = new double[histogramBins][numSigmas];
            for (int i = 0; i < pctResults.length; i++) {
                for (int j = 0; j < pctResults[i].length; j++) {
                    pctResults[i][j] = 0;
                }
            }
            for (int histoCtr = 0; histoCtr < histogramBins; histoCtr++) {
                for (int sigCtr = 0; sigCtr < numSigmas; sigCtr++) {
                    pctResults[histoCtr][sigCtr] = (double) results[histoCtr][0][sigCtr] / ((double) results[histoCtr][1][sigCtr] + (double) results[histoCtr][0][sigCtr]);
                }
            }

            //            String res = "";
            for (double i = .005; i < .5; i += .005) {
                writer.write("\t" + i);
                //                res += "\t" + i;
            }
            writer.write("\n");
            //            res += "\n";

            for (int histoCtr = 0; histoCtr < histogramBins; histoCtr++) {
                writer.write((histoCtr * 10) + "\t");
                System.out.print((histoCtr * 10) + "\t");
                //                res += (histoCtr * 10) + "\t";
                for (int sigCtr = 0; sigCtr < numSigmas; sigCtr++) {
                    System.out.print(pctResults[histoCtr][sigCtr] + "\t");

                    writer.write(pctResults[histoCtr][sigCtr] + "\t");
                    //                    res += pctResults[histoCtr][sigCtr] + "\t";
                    //                System.out.print(((double)results[histoCtr][1][sigCtr] + (double)results[histoCtr][0][sigCtr]) + "\t");
                }
                System.out.println();
                writer.write("\n");
                //                res += "\n";
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
