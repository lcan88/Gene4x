package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;


public class PrintAdjProps {
    public PrintAdjProps() {
    }

    public static void main(String[] args) {
        new PrintAdjProps().doIt(args);
    }

    void doIt(String[] args) {
        //        printAdjProps(new File(args[0]), args[1]);
        printAdjProps(args[0]);
    }

    //    void printAdjProps(File expFile, String adjFileName){
    void printAdjProps(String adjFileName) {
        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();

        //        System.out.println("Reading microarray set");
        //        ExpressionMicroarraySet mArraySet = new ExpressionMicroarraySet();
        //        mArraySet.readFromFile(expFile);

        float miThresh = 0.055156f;

        System.out.println("Reading adjacency matrix");
        //        adjMatrix.readTmp(adjFileName, mArraySet, null);
        adjMatrix.read(adjFileName, miThresh);

        int numMarkers = adjMatrix.size();
        System.out.println("Num markers:  " + numMarkers);
        System.out.println("Done");

        //        int numArrays = mArraySet.size();
        //        int numMarkers = mArraySet.markers().size();
        //
        //        System.out.println("Num arrays:  " + numArrays);
        //        System.out.println("Num markers:  " + numMarkers);
    }

}
