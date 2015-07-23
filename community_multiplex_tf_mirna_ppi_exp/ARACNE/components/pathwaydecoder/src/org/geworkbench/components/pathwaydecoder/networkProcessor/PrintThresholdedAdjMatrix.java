package org.geworkbench.components.pathwaydecoder.networkProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PrintThresholdedAdjMatrix {
    public PrintThresholdedAdjMatrix() {
    }

    public static void main(String[] args) {
        new PrintThresholdedAdjMatrix().doIt(args);
    }

    void doIt(String[] args) {
        File adjFile = new File(args[0]);
        double thresh = Double.parseDouble(args[1]);
        printThresholdedAdjMatrix(adjFile, thresh);
    }

    void printThresholdedAdjMatrix(File adjFile, double thresh) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(adjFile));

            String line;
            while ((line = reader.readLine()) != null) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
