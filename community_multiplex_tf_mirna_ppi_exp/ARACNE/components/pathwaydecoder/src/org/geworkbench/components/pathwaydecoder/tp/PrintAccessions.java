package org.geworkbench.components.pathwaydecoder.tp;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PrintAccessions {
    public PrintAccessions() {
    }

    public static void main(String[] args) {
        new PrintAccessions().doIt(args);
    }

    void doIt(String[] args) {
        printAccessions(new File(args[0]), args[1], new File(args[2]));
    }

    void printAccessions(File expFile, String adjFileName, File writeFile) {
        try {
            writeFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(writeFile);
            //        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();

            System.out.println("Reading microarray set");
            CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
            mArraySet.readFromFile(expFile);

            System.out.println("Reading adj file");
            BufferedReader reader = new BufferedReader(new FileReader(new File(adjFileName)));

            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                String[] accTokens = arrLine[0].split(":");
                int geneId = Integer.parseInt(accTokens[accTokens.length - 1]);
                String accession = mArraySet.get(geneId).getLabel();
                System.out.println(count++ + "");
                writer.write(accession + ":" + geneId);
                for (int i = 1; i < arrLine.length; i++) {
                    writer.write("\t" + arrLine[i]);
                }
                writer.write("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
