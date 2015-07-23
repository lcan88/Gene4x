package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;

public class PrintGenesConnectivity {
    public PrintGenesConnectivity() {
    }

    public static void main(String[] args) {
        //        File adjFile = new File(args[0].replaceAll("/cygdrive/y/", "y:/"));
        //        File writeFile = new File(args[1].replaceAll("/cygdrive/y/", "y:/"));
        //        File adjFile = new File("Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/pValue_1.0e-6/tolerance_0.15/0.adj");
        File writeFile = new File("Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/pValue_1.0e-6/tolerance_0.15/medians.txt");
        //        File expFile = new File("Y:/RevEng/Yeast/StressResponse/SegalComplete_v2.exp");
        File expFile = new File("Y:\\RevEng\\Yeast\\Rosetta\\processedRosetta1-300.exp");
        File adjFile = new File("Y:\\RevEng\\Yeast\\Rosetta\\CleanedMatrices\\samples_300\\sigma_0.16\\pValue_1.0e-6\\tolerance_0.15\\0.adj");
        new PrintGenesConnectivity().doIt(adjFile, writeFile, expFile);
    }

    void doIt(File adjFile, File writeFile, File expFile) {

        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);


        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFile.getAbsolutePath(), 0.0f);

        for (int i = 0; i < adjMatrix.size(); i++) {
            int numFirstNeighbors = adjMatrix.getConnectionNo(i, 0.0);
            String accession = mArraySet.get(i).getLabel();

            System.out.println(accession + "\t" + numFirstNeighbors);

        }

    }

}
