package org.geworkbench.components.pathwaydecoder.networkProcessor;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrixComparator;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;


public class NetworkComparison {
    public NetworkComparison() {
    }

    public static void main(String[] args) {
        new NetworkComparison().compareAllFiles(args[0], args[1], args[2]);
    }

    void compareAllFiles(String rootDirectory, String strTrueAdjFile, String strExpFile) {
        File networkReconstDir = new File(rootDirectory);
        File trueAdjFile = new File(strTrueAdjFile);

        File expFile = new File(strExpFile);
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        compareAllFiles(networkReconstDir, trueAdjFile, mArraySet);
    }

    //    public void compareAllFiles(DSMicroarraySet mArraySet){
    //        File rootDirectory = new File("Z:/Simulations/Results/SF/CleanedMatrices");
    //        compareAllFiles(rootDirectory, mArraySet);
    //    }

    public void compareAllFiles(File rootDirectory, File trueAdjFile, DSMicroarraySet mArraySet) {
        System.out.println();
        DoubleArrayList dalTrueConnections = new DoubleArrayList();
        DoubleArrayList dalRecoveredConnections = new DoubleArrayList();
        DoubleArrayList dalFalseConnections = new DoubleArrayList();
        Vector fileNames = new Vector();

        AdjacencyMatrix trueAdjMatrix = new AdjacencyMatrix();
        trueAdjMatrix.read(trueAdjFile.getAbsolutePath(), mArraySet);

        File[] children = rootDirectory.listFiles();

        boolean containsAdjFiles = false;
        double networkScore = 0.0;
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                if (children[i].getName().equals("NoThreshold")) {
                    continue;
                }
                if (children[i].isDirectory()) {
                    compareAllFiles(children[i], trueAdjFile, mArraySet);
                } else {
                    if (children[i].getName().endsWith(".adj")) {
                        fileNames.add(children[i].getName());
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
        } else {
            System.out.println("No children for directory " + rootDirectory);
        }

        if (containsAdjFiles) {
            double avgTrueConnections = Descriptive.mean(dalTrueConnections);
            double avgRecoveredConnections = Descriptive.mean(dalRecoveredConnections);
            double avgFalseConnections = Descriptive.mean(dalFalseConnections);

            double sdTrueConnections;
            double sdRecoveredConnections;
            double sdFalseConnections;

            if (dalRecoveredConnections.size() > 1) {
                sdTrueConnections = Descriptive.sampleStandardDeviation(dalTrueConnections.size(), Descriptive.sampleVariance(dalTrueConnections, avgTrueConnections));
                sdRecoveredConnections = Descriptive.sampleStandardDeviation(dalRecoveredConnections.size(), Descriptive.sampleVariance(dalRecoveredConnections, avgRecoveredConnections));
                sdFalseConnections = Descriptive.sampleStandardDeviation(dalFalseConnections.size(), Descriptive.sampleVariance(dalFalseConnections, avgFalseConnections));
            } else {
                sdTrueConnections = 0;
                sdRecoveredConnections = 0;
                sdFalseConnections = 0;

            }

            try {
                File writeFile = new File(rootDirectory + "/Recovery.txt");
                FileWriter writer = new FileWriter(writeFile);
                writer.write("\tTrue Connections\t Recovered Connections\t False Connections\tNetwork Score\n");
                for (int i = 0; i < dalRecoveredConnections.size(); i++) {
                    //                    writer.write(i + "\t" + dalTrueConnections.get(i) + "\t" +
                    writer.write(fileNames.get(i) + "\t" + dalTrueConnections.get(i) + "\t" + dalRecoveredConnections.get(i) + "\t" + dalFalseConnections.get(i) + "\t" + networkScore + "\n");
                }

                writer.write("Mean\t" + avgTrueConnections + "\t" + avgRecoveredConnections + "\t" + avgFalseConnections + "\n");
                writer.write("Sd\t" + sdTrueConnections + "\t" + sdRecoveredConnections + "\t" + sdFalseConnections + "\n");

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void compareAdjFiles(DSMicroarraySet mArraySet) {
        File trueAdjFile = new File("Z:/BayesData/GeneSim/GeneSimNet_true.adj");
        File compareFile = new File("Z:/BayesData/ComparisonResults/CleanedMatrices/samples1200/pVal0.0010/1200_1.adj");

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


}
