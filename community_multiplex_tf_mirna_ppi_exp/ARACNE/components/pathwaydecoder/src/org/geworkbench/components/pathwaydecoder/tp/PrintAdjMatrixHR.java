package org.geworkbench.components.pathwaydecoder.tp;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;

public class PrintAdjMatrixHR {
    public PrintAdjMatrixHR() {
    }

    public static void main(String[] args) {
        new PrintAdjMatrixHR().doIt(args);
    }

    void doIt(String[] args) {
        //        File expFile = new File("Y:/RevEng/Yeast/Rosetta/processedRosetta1-300.exp");
        //        String adjFileName = "Y:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/Rosetta_tol_15.adj";
        //        File writeFile = new File("Y:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/Rosetta_tol_15_HR.adj");
        File expFile = new File("Y:/RevEng/Yeast/StressResponse/SegalComplete_v2.exp");
        String adjFileName = "Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/miThresh_0.08/tolerance_0.15/0.adj";
        File writeFile = new File("Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/miThresh_0.08/tolerance_0.15/0_HR.adj");

        printTargetsOfGene(expFile, adjFileName, writeFile);

    }

    void printTargetsOfGene(File expFile, String adjFileName, File writeFile) {
        //            writeFile.getParentFile().mkdirs();

        System.out.println("Reading microarray set");
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFileName, 0.0f);

        adjMatrix.printGeneNamesAndAccessions(mArraySet, writeFile);

    }

}
