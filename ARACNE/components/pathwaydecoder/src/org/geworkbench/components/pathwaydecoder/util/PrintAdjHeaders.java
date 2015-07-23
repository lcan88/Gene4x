package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class PrintAdjHeaders {
    public PrintAdjHeaders() {
    }

    public static void main(String[] args) {
        new PrintAdjHeaders().doIt(args);
    }

    void doIt(String[] args) {
        printHeaders(new File(args[0]), new File(args[1]));
    }

    void printHeaders(File adjFile, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
            adjMatrix.readMappings(adjFile);
            HashMap keyMapping = adjMatrix.getKeyMapping();

            for (int i = 0; i < keyMapping.size(); i++) {
                String name = (String) keyMapping.get(new Integer(i));
                writer.write(name + "\t");
            }
            writer.close();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
