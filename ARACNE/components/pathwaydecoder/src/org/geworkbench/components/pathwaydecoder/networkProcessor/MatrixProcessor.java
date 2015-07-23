package org.geworkbench.components.pathwaydecoder.networkProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MatrixProcessor {
    public MatrixProcessor() {
    }

    public void writeMatrixToleranceByNumSamples() {
        try {
            String writeDirectoryBase = "Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\MatricesByPValues";

            File readFile = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\CompiledResults.txt");
            BufferedReader reader = new BufferedReader(new FileReader(readFile));

            double[] arrPVals = {//.001, .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7};
                .001, .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7};

            double[] arrToleranceVals = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5};

            int[] numSamplesArr = {100, 200, 300, 400, 500, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000, 100, 200, 300, 400, 500, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000};

            for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                double pVal = arrPVals[pValCtr];

                File writeDir = new File(writeDirectoryBase + "\\" + pVal);
                writeDir.mkdirs();

                File sensMatrixFile = new File(writeDir + "\\sens_results_matrix.txt");
                File accuracyMatrixFile = new File(writeDir + "\\accuracy_results_matrix.txt");
                File numSamplesFile = new File(writeDir + "\\numSamples.txt");
                File toleranceValsFile = new File(writeDir + "\\tolerance_vals.txt");

                FileWriter sensWriter = new FileWriter(sensMatrixFile);
                FileWriter accuracyWriter = new FileWriter(accuracyMatrixFile);

                FileWriter numSamplesWriter = new FileWriter(numSamplesFile);
                FileWriter toleranceValsWriter = new FileWriter(toleranceValsFile);

                for (int samplesCtr = 0; samplesCtr < numSamplesArr.length; samplesCtr++) {
                    int numSamples = numSamplesArr[samplesCtr];
                    for (int toleranceCtr = 0; toleranceCtr < arrToleranceVals.length; toleranceCtr++) {
                        double toleranceVal = arrToleranceVals[toleranceCtr];
                        reader = new BufferedReader(new FileReader(readFile));
                        double sens = Temp.getValueInFile(reader, toleranceVal, 3, numSamples, 0, pVal, 1, 10);
                        reader = new BufferedReader(new FileReader(readFile));
                        double accuracy = Temp.getValueInFile(reader, toleranceVal, 3, numSamples, 0, pVal, 1, 11);

                        sensWriter.write(sens + "\t");
                        accuracyWriter.write(accuracy + "\t");
                    }
                    sensWriter.write("\n");
                    accuracyWriter.write("\n");
                }

                for (int samplesCtr = 0; samplesCtr < numSamplesArr.length; samplesCtr++) {
                    int numSamples = numSamplesArr[samplesCtr];
                    numSamplesWriter.write(numSamples + "\n");
                }
                for (int toleranceCtr = 0; toleranceCtr < arrToleranceVals.length; toleranceCtr++) {
                    double toleranceVal = arrToleranceVals[toleranceCtr];
                    toleranceValsWriter.write(toleranceVal + "\n");
                }

                numSamplesWriter.close();
                toleranceValsWriter.close();
                sensWriter.close();
                accuracyWriter.close();

                System.out.println("done " + pVal);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
