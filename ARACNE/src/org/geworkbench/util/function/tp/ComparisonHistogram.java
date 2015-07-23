package org.geworkbench.util.function.tp;

import java.io.File;
import java.util.Vector;

public class ComparisonHistogram {
    public ComparisonHistogram() {
    }

    public static void main(String[] args) {
        new ComparisonHistogram().createHistogram();
    }

    void createHistogram() {
        int numSigmas = 0;
        for (double i = .005; i < .5; i += .005) {
            numSigmas++;
        }

        int histogramBins = 11;

        int[][][] results = new int[histogramBins][2][numSigmas];

        File dataFile = new File("Z:/Adam/MI/BimodalGaussian/BimodalGaussianMIs/MpiTest/Accurate/MisForSigmas/EstimateCompare/Comparison.txt");
        Vector fileData = org.geworkbench.bison.util.FileUtil.readFile(dataFile);

        for (int dataCtr = 1; dataCtr < fileData.size() - 1; dataCtr++) {
            String[] lineData = (String[]) fileData.get(dataCtr);
            if (null == lineData) {
                continue;
            }
            double expectedMiDiff = Double.parseDouble(lineData[13]);
            int histogramBin = (int) (Math.abs(expectedMiDiff) * 10);
            if (histogramBin > 10) {
                histogramBin = 10;
            }
            for (int sigmaCtr = 113; sigmaCtr < (numSigmas + 113); sigmaCtr++) {
                int sign = Integer.parseInt(lineData[sigmaCtr]);
                if (sign < 0) {
                    results[histogramBin][0][sigmaCtr - 113]++;
                } else {
                    results[histogramBin][1][sigmaCtr - 113]++;
                }
            }
        }

        double[][] pctResults = new double[histogramBins][numSigmas];
        for (int histoCtr = 0; histoCtr < histogramBins; histoCtr++) {
            for (int sigCtr = 0; sigCtr < numSigmas; sigCtr++) {
                pctResults[histoCtr][sigCtr] = (double) results[histoCtr][0][sigCtr] / ((double) results[histoCtr][1][sigCtr] + (double) results[histoCtr][0][sigCtr]);
            }
        }

        for (int histoCtr = 0; histoCtr < histogramBins; histoCtr++) {
            for (int sigCtr = 0; sigCtr < numSigmas; sigCtr++) {
                //                System.out.print(pctResults[histoCtr][sigCtr] + "\t");
                System.out.print(((double) results[histoCtr][1][sigCtr] + (double) results[histoCtr][0][sigCtr]) + "\t");
            }
            System.out.println();
        }

    }
}
