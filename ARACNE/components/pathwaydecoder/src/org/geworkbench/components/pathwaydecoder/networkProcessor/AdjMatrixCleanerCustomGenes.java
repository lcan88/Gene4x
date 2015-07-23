package org.geworkbench.components.pathwaydecoder.networkProcessor;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;


public class AdjMatrixCleanerCustomGenes {
    public AdjMatrixCleanerCustomGenes() {
    }

    public static void main(String[] args) {
        new AdjMatrixCleanerCustomGenes().doIt(args);
    }

    void doIt(String[] args) {
        String expFileName = args[0];
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        File adjMatrixFile = new File(args[1]);

        double tolerance = Double.parseDouble(args[2]);
        double miThresh = Double.parseDouble(args[3]);
        File writeFile = new File(args[4]);
        File accessionsMapFile = new File(args[5]);
        HashMap accessionsMap = org.geworkbench.bison.util.FileUtil.readHashMap(accessionsMapFile);

        File customGenesFile = new File(args[6]);
        Vector geneNames = FileUtil.readVector(customGenesFile);

        cleanFile(mArraySet, adjMatrixFile, tolerance, miThresh, writeFile, accessionsMap, geneNames);
    }

    void cleanFile(DSMicroarraySet mArraySet, File adjMatrixFile, double tolerance, double miThresh, File writeFile, HashMap accessionsMap, Vector geneNames) {
        writeFile.getParentFile().mkdirs();

        System.out.println(writeFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjMatrixFile.getAbsolutePath(), accessionsMap, geneNames, (float) miThresh);
        //        adjMatrix.read(adjMatrixFile.getAbsolutePath(),
        //                       (float)miThresh);
        if (tolerance < 1.0) {
            adjMatrix.clean_new(mArraySet, miThresh, tolerance);
        }
        adjMatrix.print(mArraySet, writeFile, 0.0);
    }


}
