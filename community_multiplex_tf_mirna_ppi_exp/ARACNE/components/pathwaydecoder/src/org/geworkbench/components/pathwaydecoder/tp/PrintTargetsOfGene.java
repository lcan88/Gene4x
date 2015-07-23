package org.geworkbench.components.pathwaydecoder.tp;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;

public class PrintTargetsOfGene {
    public PrintTargetsOfGene() {
    }

    public static void main(String[] args) {
        new PrintTargetsOfGene().doIt(args);
    }

    void doIt(String[] args) {
        //        File expFile = new File("Y:/RevEng/Yeast/Rosetta/processedRosetta1-300.exp");
        //        String adjFileName = "Y:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/Rosetta_tol_15.adj";

        File expFile = new File("Y:/RevEng/Yeast/StressResponse/SegalComplete_v2.exp");
        String adjFileName = "Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/miThresh_0.08/tolerance_0.15/0.adj";
        String centerAccession = "YDL056W";
        File writeFile = new File("Y:/RevEng/Yeast/GOMiner/StressResponse/" + centerAccession + ".txt");

        //        String centerAccession = "16";
        printTargetsOfGene(expFile, adjFileName, centerAccession, writeFile);

        //        printAccessions(new File(args[0]), args[1], args[2], new File(args[3]));
    }

    void printTargetsOfGene(File expFile, String adjFileName, String centerAccession, File writeFile) {
        //            writeFile.getParentFile().mkdirs();

        System.out.println("Reading microarray set");
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFileName, 0.0f);

        adjMatrix.printTargetsOfGene(mArraySet, writeFile, centerAccession);
    }

}
