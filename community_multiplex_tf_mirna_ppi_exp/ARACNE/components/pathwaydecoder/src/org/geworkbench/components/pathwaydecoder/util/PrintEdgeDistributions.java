package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;


public class PrintEdgeDistributions {
    public PrintEdgeDistributions() {
    }

    public static void main(String[] args) {
        String adjFileName = args[0].replaceAll("/cygdrive/y/", "y:/");
        String writeFileName = args[1].replaceAll("/cygdrive/y/", "y:/");

        File adjFile = new File(adjFileName);
        File writeFile = new File(writeFileName);
        new PrintEdgeDistributions().printDistributions(adjFile, writeFile);
    }

    void printDistributions(File adjFile, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);

            AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
            adjMatrix.read(adjFile.getAbsolutePath(), 0.0f);

            for (int i = 0; i < adjMatrix.size(); i++) {
                writer.write(adjMatrix.getConnectionNo(i, 0.0) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
