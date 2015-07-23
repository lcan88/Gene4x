package org.geworkbench.components.pathwaydecoder.networkProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CompiledResultsWriter {

    String pValueDirRoot;
    String resultsDirBase;

    public CompiledResultsWriter() {
    }

    public void writeCompiledResults(String networkReconstDir) {
        File[] numSamplesDirs = new File(networkReconstDir).listFiles();
        for (int numSamplesDirCtr = 0; numSamplesDirCtr < numSamplesDirs.length; numSamplesDirCtr++) {

            File numSamplesDir = numSamplesDirs[numSamplesDirCtr];
            if (!numSamplesDir.isDirectory()) {
                continue;
            }

            System.out.println("*************************** WRITING RESULTS FOR DIRECTORY " + numSamplesDir + "*******************************************************");
            writeCompiledResults(numSamplesDir);
            System.out.print("\n\n");
        }
    }

    public void writeCompiledResults(File numSamplesDir) {
        try {
            //            FileWriter resultsWriter = new FileWriter(new File(
            //                numSamplesDir + "/CompiledResults.txt"));

            //            resultsWriter.write("Data Points\tP Value\t MI Thresh\tTolerance\tSigma\tTrue Connections\tAverage Recovery\tAverage Errors\tRecovery SD\t Errors SD\n");

            String numSamples = numSamplesDir.getName().replaceFirst("samples_", "");

            File writeFile = new File(resultsDirBase + "/samples_" + numSamples + "/CompiledResults.txt");
            writeFile.getParentFile().mkdirs();
            FileWriter resultsWriter = new FileWriter(writeFile);

            File[] dirsBySigmas = numSamplesDir.listFiles();
            for (int sigmaCtr = 0; sigmaCtr < dirsBySigmas.length; sigmaCtr++) {
                File sigmaDir = dirsBySigmas[sigmaCtr];
                if (!sigmaDir.isDirectory()) {
                    continue;
                }
                String sigma = sigmaDir.getName().replaceFirst("sigma_", "");

                File pValueMatrixFile = new File(pValueDirRoot + "/samples_" + numSamples + "/sigma_" + sigma + "/pValueMatrix.txt");
                org.geworkbench.bison.util.HashMatrix pValueMatrix = org.geworkbench.bison.util.FileUtil.readHashMatrix(pValueMatrixFile);
                //                File cleanedMatrixDir = new File(sigmaDir);

                //                File[] dirsByPValues = cleanedMatrixDir.listFiles();
                File[] dirsByPValues = sigmaDir.listFiles();
                if (dirsByPValues != null) {
                    for (int pValCtr = 0; pValCtr < dirsByPValues.length; pValCtr++) {
                        File pValueDir = dirsByPValues[pValCtr];
                        if (!pValueDir.isDirectory()) {
                            continue;
                        }
                        String strPVal = pValueDir.getName().replaceFirst("pVal_", "");

                        double pVal = Double.parseDouble(strPVal);
                        Object objMiThresh = pValueMatrix.get(numSamples + "", new Double(pVal));
                        double miThresh = Double.parseDouble((String) objMiThresh);

                        File[] dirsByTolVals = pValueDir.listFiles();
                        for (int toleranceValCtr = 0; toleranceValCtr < dirsByTolVals.length; toleranceValCtr++) {
                            File tolValDir = dirsByTolVals[toleranceValCtr];
                            String toleranceVal = tolValDir.getName().replaceFirst("tolerance_", "");

                            File resultsFile = new File(tolValDir + "/Recovery.txt");
                            System.out.println("Writing: " + resultsFile + "");

                            BufferedReader resultsReader = new BufferedReader(new FileReader(resultsFile));

                            String[] arrAvgResults = null;
                            String[] arrSdResults = null;
                            String line;
                            while ((line = resultsReader.readLine()) != null) {
                                if (line.startsWith("Mean")) {
                                    arrAvgResults = line.split("\t");
                                }

                                if (line.startsWith("Sd")) {
                                    arrSdResults = line.split("\t");
                                }

                            }
                            resultsWriter.write(numSamples + "\t" + pVal + "\t" + miThresh + "\t" + toleranceVal + "\t" + sigma + "\t" + arrAvgResults[1] + "\t" + arrAvgResults[2] + "\t" + arrAvgResults[3] + "\t" + arrSdResults[2] + "\t" + arrSdResults[3] + "\n");
                        }
                    }
                }
            }

            resultsWriter.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doIt(String[] args) {
        String networkReconstDir = args[0];
        pValueDirRoot = args[1];
        resultsDirBase = args[2];
        writeCompiledResults(networkReconstDir);
    }

    public static void main(String[] args) {
        new CompiledResultsWriter().doIt(args);
    }

}
