package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;

import java.io.File;

public class PrintExpProps {
    public PrintExpProps() {
    }

    public static void main(String[] args) {
        new PrintExpProps().doIt(args);
    }

    void doIt(String[] args) {
        printExpProps(new File(args[0]));
    }

    void printExpProps(File expFile) {
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);
        int numArrays = mArraySet.size();
        int numMarkers = mArraySet.size();

        System.out.println("Num arrays:  " + numArrays);
        System.out.println("Num markers:  " + numMarkers);
    }
}
