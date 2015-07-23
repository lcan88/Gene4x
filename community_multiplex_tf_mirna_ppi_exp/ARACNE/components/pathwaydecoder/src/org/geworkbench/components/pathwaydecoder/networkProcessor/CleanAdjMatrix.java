package org.geworkbench.components.pathwaydecoder.networkProcessor;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;


public class CleanAdjMatrix {
    public CleanAdjMatrix() {
    }

    public static void main(String[] args) {
        new CleanAdjMatrix().doIt(args);
    }

    void doIt(String[] args) {
        String expFileName = args[0];
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        File adjMatrixFile = new File(args[1]);

        double tolerance = Double.parseDouble(args[2]);
        double miThresh = Double.parseDouble(args[3]);
        File writeFile = new File(args[4]);

        cleanFile(mArraySet, adjMatrixFile, tolerance, miThresh, writeFile);
    }

    void cleanFile(DSMicroarraySet mArraySet, File adjMatrixFile, double tolerance, double miThresh, File writeFile) {

        writeFile.getParentFile().mkdirs();

        System.out.println(writeFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjMatrixFile.getAbsolutePath(), (float) miThresh);
        adjMatrix.clean_new(mArraySet, miThresh, tolerance);
        adjMatrix.print(mArraySet, writeFile, 0.0);
    }


}
