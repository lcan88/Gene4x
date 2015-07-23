package org.geworkbench.components.pathwaydecoder.parallel;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;


public class CleanAdjMatrixCluster {
    public CleanAdjMatrixCluster() {
    }

    public static void main(String[] args) {
        new CleanAdjMatrixCluster().doIt(args);
    }

    void doIt(String[] args) {
        String expFileName = args[0];
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        File adjMatrixFile = new File(args[1]);

        double tolerance = Double.parseDouble(args[2]);
        double miThresh = Double.parseDouble(args[3]);
        File writeFile = new File(args[4]);

        int startIndex = Integer.parseInt(args[5]);
        int endIndex = Integer.parseInt(args[6]);

        cleanFile(mArraySet, adjMatrixFile, tolerance, miThresh, writeFile, startIndex, endIndex);
    }

    void cleanFile(DSMicroarraySet mArraySet, File adjMatrixFile, double tolerance, double miThresh, File writeFile, int startIndex, int endIndex) {

        writeFile.getParentFile().mkdirs();

        System.out.println(writeFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.setMicroarraySet(mArraySet);
        adjMatrix.read(adjMatrixFile.getAbsolutePath(), (float) miThresh);
        adjMatrix.clean(mArraySet, miThresh, tolerance, startIndex, endIndex);
        adjMatrix.print(mArraySet, writeFile, 0.0, startIndex, endIndex);
    }

}
