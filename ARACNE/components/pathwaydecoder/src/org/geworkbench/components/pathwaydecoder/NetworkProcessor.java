package org.geworkbench.components.pathwaydecoder;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrixComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class NetworkProcessor {
    public NetworkProcessor() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cleanAllFiles(DSMicroarraySet mArraySet) {
        double[] arrPVals = {.001, .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7};

        double[] arrToleranceVals = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5};

        File rootReadDirectory = new File("Z:\\BayesData\\ComparisonResults\\NoThreshold\\Fast");
        File rootWriteDirectory = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Fast");
        rootWriteDirectory.mkdirs();

        File pValueMap = new File("Z:\\BayesData\\GeneSim\\GeneSimPValueMatrix_fast.txt");
        org.geworkbench.bison.util.HashMatrix hashMatrix = FileUtil.readHashMatrix(pValueMap);

        File[] numSamplesDirs = rootReadDirectory.listFiles();
        for (int numSamplesDirCtr = 0; numSamplesDirCtr < numSamplesDirs.length; numSamplesDirCtr++) {
            //        for (int numSamplesDirCtr = 2; numSamplesDirCtr < 3; numSamplesDirCtr++) {
            File numSamplesDir = numSamplesDirs[numSamplesDirCtr];
            String numSamples = numSamplesDirs[numSamplesDirCtr].getName();
            File[] filesByNumSamples = numSamplesDir.listFiles();

            for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                //            for (int pValCtr = 0; pValCtr < 1; pValCtr++) {

                double pVal = arrPVals[pValCtr];
                Object objMiThresh = hashMatrix.get(numSamples + "", new Double(pVal));
                double miThresh = Double.parseDouble((String) objMiThresh);

                for (int toleranceValCtr = 0; toleranceValCtr < arrToleranceVals.length; toleranceValCtr++) {
                    double toleranceVal = arrToleranceVals[toleranceValCtr];

                    String writeDir = rootWriteDirectory + "\\samples" + numSamples + "\\pVal" + pVal + "\\toleranceVal" + toleranceVal;
                    new File(writeDir).mkdirs();

                    try {
                        File miThreshFile = new File(writeDir + "\\miThresh_" + miThresh + ".txt");
                        FileWriter miWriter = new FileWriter(miThreshFile);
                        miWriter.write("Mi Thresh\t" + miThresh + "\n");
                        miWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (int fileCtr = 0; fileCtr < filesByNumSamples.length; fileCtr++) {
                        //                for (int fileCtr = 0; fileCtr < 1; fileCtr++) {
                        File readFile = filesByNumSamples[fileCtr];
                        File writeFile = new File(writeDir + "\\" + readFile.getName());
                        //                  convertFile(readFile);
                        System.out.println(writeFile);
                        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
                        adjMatrix.read(readFile.getAbsolutePath(), mArraySet);
                        adjMatrix.clean(mArraySet, miThresh, toleranceVal);
                        adjMatrix.print(mArraySet, writeFile);
                    }
                }
            }
        }
        System.out.println("done");
    }

    public void compareAllFiles(File rootDirectory, DSMicroarraySet mArraySet) {
        System.out.println();
        DoubleArrayList dalTrueConnections = new DoubleArrayList();
        DoubleArrayList dalRecoveredConnections = new DoubleArrayList();
        DoubleArrayList dalFalseConnections = new DoubleArrayList();

        File trueAdjFile = new File("Z:\\BayesData\\GeneSim\\GeneSimNet_true.adj");

        AdjacencyMatrix trueAdjMatrix = new AdjacencyMatrix();
        trueAdjMatrix.read(trueAdjFile.getAbsolutePath(), mArraySet);

        File[] children = rootDirectory.listFiles();

        boolean containsAdjFiles = false;
        double networkScore = 0.0;
        for (int i = 0; i < children.length; i++) {
            if (children[i].isDirectory()) {
                compareAllFiles(children[i], mArraySet);
            } else {
                if (children[i].getName().endsWith(".adj")) {
                    containsAdjFiles = true;
                    System.out.println("Calculate file " + children[i]);

                    AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
                    adjMatrix.read(children[i].getAbsolutePath(), mArraySet);

                    AdjacencyMatrixComparator adjComparator = new AdjacencyMatrixComparator();
                    adjComparator.compare(trueAdjMatrix, adjMatrix, 0.0);

                    int numTrueConnections = adjComparator.getNumTrueConnections();
                    int numRecoveredConnections = adjComparator.getNumRecoveredConnections();
                    int numFalseConnections = adjComparator.getNumFalseConnections();

                    dalTrueConnections.add((double) numTrueConnections);
                    dalRecoveredConnections.add((double) numRecoveredConnections);
                    dalFalseConnections.add((double) numFalseConnections);

                    //Add this part for Bayes results
                    //                    try {
                    //                        BufferedReader reader = new BufferedReader(new
                    //                            FileReader(children[i]));
                    //                        String line;
                    //                        while ( (line = reader.readLine()) != null) {
                    //                            if (line.startsWith("Network Score")) {
                    //                                String[] arrLine = line.split("\t");
                    //                                networkScore = Double.parseDouble(arrLine[1]);
                    //                            }
                    //                        }
                    //                    }
                    //                    catch (Exception e) {
                    //                        e.printStackTrace();
                    //                    }
                }
            }
        }

        if (containsAdjFiles) {
            double avgTrueConnections = Descriptive.mean(dalTrueConnections);
            double avgRecoveredConnections = Descriptive.mean(dalRecoveredConnections);
            double avgFalseConnections = Descriptive.mean(dalFalseConnections);

            double sdTrueConnections = Descriptive.sampleStandardDeviation(dalTrueConnections.size(), Descriptive.sampleVariance(dalTrueConnections, avgTrueConnections));
            double sdRecoveredConnections = Descriptive.sampleStandardDeviation(dalRecoveredConnections.size(), Descriptive.sampleVariance(dalRecoveredConnections, avgRecoveredConnections));
            double sdFalseConnections = Descriptive.sampleStandardDeviation(dalFalseConnections.size(), Descriptive.sampleVariance(dalFalseConnections, avgFalseConnections));

            try {
                File writeFile = new File(rootDirectory + "\\Recovery.txt");
                FileWriter writer = new FileWriter(writeFile);
                writer.write("\tTrue Connections\t Recovered Connections\t False Connections\tNetwork Score\n");
                for (int i = 0; i < dalRecoveredConnections.size(); i++) {
                    writer.write(i + "\t" + dalTrueConnections.get(i) + "\t" + dalRecoveredConnections.get(i) + "\t" + dalFalseConnections.get(i) + "\t" + networkScore + "\n");
                }

                writer.write("Mean\t" + avgTrueConnections + "\t" + avgRecoveredConnections + "\t" + avgFalseConnections + "\n");
                writer.write("Sd\t" + sdTrueConnections + "\t" + sdRecoveredConnections + "\t" + sdFalseConnections + "\n");

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void convertFile(File readFile) {
        System.out.println(readFile.getAbsolutePath());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            //          Vector lines = new Vector(20);
            String[] lines = new String[20];
            String line;
            //            int lineCtr = 0;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                int tokenIndex = Integer.parseInt(tokens[0]);
                tokens[0] = "G" + tokens[0] + ":" + tokenIndex;
                //                lineCtr++;
                String newLine = "";
                for (int i = 0; i < tokens.length; i++) {
                    newLine += tokens[i] + "\t";
                }
                //              lines.insertElementAt(newLine, tokenIndex);
                lines[tokenIndex] = newLine;
            }
            reader.close();

            FileWriter writer = new FileWriter(readFile);
            //          Iterator it = lines.iterator();
            //          while(it.hasNext()){
            //              line = (String)it.next();
            for (int i = 0; i < lines.length; i++) {
                line = lines[i];
                writer.write(line + "\n");
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compareEm(DSMicroarraySet mArraySet) {
        File trueAdjFile = new File("Z:\\BayesData\\GeneSim\\GeneSimNet_true.adj");
        File compareFile = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\samples1200\\pVal0.0010\\1200_1.adj");

        AdjacencyMatrix trueAdjMatrix = new AdjacencyMatrix();
        trueAdjMatrix.read(trueAdjFile.getAbsolutePath(), mArraySet);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(compareFile.getAbsolutePath(), mArraySet);

        AdjacencyMatrixComparator adjComparator = new AdjacencyMatrixComparator();
        adjComparator.compare(trueAdjMatrix, adjMatrix, 0.0);

        int numTrueConnections = adjComparator.getNumTrueConnections();
        int numRecoveredConnections = adjComparator.getNumRecoveredConnections();
        int numFalseConnections = adjComparator.getNumFalseConnections();

        System.out.println("True connections " + numTrueConnections + "\tRecovered Connections " + numRecoveredConnections + "\tFalse Connections " + numFalseConnections);

    }

    public void printMiForPValues() {
        double[] arrPVals = {//.001, .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
            .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
        int[] numSamplesArr = {1600, 1800, 2000, 100, 200, 300, 400, 500, 600, 800, 1000, 1200, 1400, 1600, 1800, 2000};
        //        int[] numSamplesArr = {
        //            1600, 1800, 2000};

        for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
            double pValue = arrPVals[pValCtr];
            System.out.print("\t" + pValue);
        }
        System.out.println();

        for (int numSamplesCtr = 0; numSamplesCtr < numSamplesArr.length; numSamplesCtr++) {
            int numSamples = numSamplesArr[numSamplesCtr];
            System.out.print(numSamples + "");
            for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                double pValue = arrPVals[pValCtr];
                int numIterations = (int) (1.0 / pValue) * 100;

                GeneGeneRelationship bgGgr = new GeneGeneRelationship(null, 0, true);
                bgGgr.computeBackground(numIterations, numSamples, org.geworkbench.util.pathwaydecoder.GeneGeneRelationship.RANK_CHI2, 1);
                //                                        GeneGeneRelationship.RANK_MI, 1);

                double miThresh = bgGgr.getMiByPValue(pValue);
                System.out.print("\t" + miThresh);
            }
            System.out.println();
        }
    }

    public void writeCompiledResults() {
        try {
            FileWriter resultsWriter = new FileWriter(new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Fast\\CompiledResults.txt"));

            resultsWriter.write("Data Points\tP Value\t MI Thresh\tTolerance\tTrue Connections\tAverage Recovery\tAverage Errors\tRecovery SD\t Errors SD\n");

            double[] arrPVals = {.001, .005, .01, .025, .05, .075, .1, .2, .3, .4, .5, .6, .7};

            double[] arrToleranceVals = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5};

            File rootDirectory = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Fast");

            File pValueMap = new File("Z:\\BayesData\\GeneSim\\GeneSimPValueMatrix_fast.txt");
            org.geworkbench.bison.util.HashMatrix hashMatrix = org.geworkbench.bison.util.FileUtil.readHashMatrix(pValueMap);

            File[] numSamplesDirs = rootDirectory.listFiles();
            for (int numSamplesDirCtr = 0; numSamplesDirCtr < numSamplesDirs.length; numSamplesDirCtr++) {
                //        for (int numSamplesDirCtr = 2; numSamplesDirCtr < 3; numSamplesDirCtr++) {
                File numSamplesDir = numSamplesDirs[numSamplesDirCtr];
                if (!numSamplesDir.isDirectory()) {
                    continue;
                }

                String numSamples = numSamplesDirs[numSamplesDirCtr].getName().replaceFirst("samples", "");
                File[] filesByNumSamples = numSamplesDir.listFiles();

                for (int pValCtr = 0; pValCtr < arrPVals.length; pValCtr++) {
                    //            for (int pValCtr = 0; pValCtr < 1; pValCtr++) {

                    double pVal = arrPVals[pValCtr];
                    Object objMiThresh = hashMatrix.get(numSamples + "", new Double(pVal));
                    double miThresh = Double.parseDouble((String) objMiThresh);

                    for (int toleranceValCtr = 0; toleranceValCtr < arrToleranceVals.length; toleranceValCtr++) {
                        double toleranceVal = arrToleranceVals[toleranceValCtr];

                        String resultsFileDir = rootDirectory + "\\samples" + numSamples + "\\pVal" + pVal + "\\toleranceVal" + toleranceVal;

                        File resultsFile = new File(resultsFileDir + "\\Recovery.txt");
                        System.out.println(resultsFile + "");

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
                        resultsWriter.write(numSamples + "\t" + pVal + "\t" + miThresh + "\t" + toleranceVal + "\t" + arrAvgResults[1] + "\t" + arrAvgResults[2] + "\t" + arrAvgResults[3] + "\t" + arrSdResults[2] + "\t" + arrSdResults[3] + "\n");
                    }
                }
            }
            resultsWriter.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCompiledResultsBayes() {
        try {
            FileWriter resultsWriter = new FileWriter(new File("Z:\\BayesData\\ComparisonResults\\Bayes\\CompiledResults.txt"));

            resultsWriter.write("Data Points\tAlgorithm\tIteration\tTrue Connections\t Recovered Connections\t False Connections\tNetwork Score\n");

            File rootDirectory = new File("Z:\\BayesData\\ComparisonResults\\Bayes");

            String[][] algMap = new String[3][2];
            algMap[0][0] = rootDirectory + "\\GreedySL";
            algMap[0][1] = "Greedy";

            algMap[1][0] = rootDirectory + "\\HillClimbingSL";
            algMap[1][1] = "Hill Climbing";

            algMap[2][0] = rootDirectory + "\\SimAnnealingSL";
            algMap[2][1] = "Simmulated Annealing";

            for (int algMapCtr = 0; algMapCtr < algMap.length; algMapCtr++) {
                File algDir = new File(algMap[algMapCtr][0]);
                String algName = algMap[algMapCtr][1];

                File[] sampleDirs = algDir.listFiles();
                for (int sampleDirCtr = 0; sampleDirCtr < sampleDirs.length; sampleDirCtr++) {
                    File sampleDir = sampleDirs[sampleDirCtr];
                    String numSamples = sampleDir.getName();
                    BufferedReader reader = new BufferedReader(new FileReader(new File(sampleDir + "\\Recovery.txt")));
                    String line;
                    reader.readLine();
                    while ((line = reader.readLine()) != null) {
                        String[] arrLine = line.split("\t");
                        resultsWriter.write(numSamples + "\t" + algName);
                        for (int i = 0; i < arrLine.length; i++) {
                            resultsWriter.write("\t" + arrLine[i]);
                        }
                        resultsWriter.write("\n");
                    }
                }

            }
            resultsWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFilteredCompiledResultsFile() {
        int column = 3;
        String value = 0.5 + "";

        File compiledResultsFile = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\CompiledResults.txt");
        File writeFileDirectory = new File("Z:\\BayesData\\ComparisonResults\\CleanedMatrices\\Accurate\\CompiledResults\\Tolerance0.5");

        writeFileDirectory.mkdirs();

        String writeFileName = "CompiledResults_tolerance0.5.txt";

        File writeFile = new File(writeFileDirectory + "\\" + writeFileName);

        try {
            FileWriter writer = new FileWriter(writeFile);

            BufferedReader reader = new BufferedReader(new FileReader(compiledResultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                if (value.equals(arrLine[column])) {
                    writer.write(line + "\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        double sens = getValueInFile(reader, toleranceVal, 3, numSamples, 0, pVal, 1, 10);
                        reader = new BufferedReader(new FileReader(readFile));
                        double accuracy = getValueInFile(reader, toleranceVal, 3, numSamples, 0, pVal, 1, 11);

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

    double getValueInFile(BufferedReader reader, double val1, int index1, double val2, int index2, double val3, int index3, int returnIndex) {
        String line;
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                double fileVal1 = Double.parseDouble(arrLine[index1]);
                double fileVal2 = Double.parseDouble(arrLine[index2]);
                double fileVal3 = Double.parseDouble(arrLine[index3]);
                if (fileVal1 == val1 && fileVal2 == val2 && fileVal3 == val3) {
                    return Double.parseDouble(arrLine[returnIndex]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        //        new NetworkProcessor().writeCompiledResults();
        //        new NetworkProcessor().writeCompiledResultsBayes();
        new NetworkProcessor().printMiForPValues();
    }

    private void jbInit() throws Exception {
    }

}
