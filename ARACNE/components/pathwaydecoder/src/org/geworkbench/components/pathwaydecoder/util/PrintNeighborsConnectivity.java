package org.geworkbench.components.pathwaydecoder.util;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

public class PrintNeighborsConnectivity {
    public PrintNeighborsConnectivity() {
    }

    public static void main(String[] args) {
        //        File adjFile = new File(args[0].replaceAll("/cygdrive/y/", "y:/"));
        //        File writeFile = new File(args[1].replaceAll("/cygdrive/y/", "y:/"));
        File adjFile = new File("Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/pValue_1.0e-6/tolerance_0.15/0.adj");
        File writeFile = new File("Y:/RevEng/Yeast/StressResponse/CleanedMatrices/samples_173/sigma_0.18/pValue_1.0e-6/tolerance_0.15/medians.txt");
        new PrintNeighborsConnectivity().doIt(adjFile, writeFile);
    }

    void doIt(File adjFile, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            DoubleArrayList neighborConnections = new DoubleArrayList();

            AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
            adjMatrix.read(adjFile.getAbsolutePath(), 0.0f);

            for (int i = 0; i < adjMatrix.size(); i++) {
                int numFirstNeighbors = adjMatrix.getConnectionNo(i, 0.0);
                HashMap geneRow = (HashMap) adjMatrix.getGeneRows().get(new Integer(i));
                if (geneRow != null) {
                    int sum = 0;
                    int size = 0;
                    for (Iterator iter = geneRow.keySet().iterator(); iter.hasNext();) {
                        int connectionIndex = ((Integer) iter.next()).intValue();
                        int connectionSize = adjMatrix.getConnectionNo(connectionIndex, 0.0);
                        //                        sum += connectionSize;
                        //                        size++;
                        neighborConnections.add((double) connectionSize);
                    }
                    double meanNeighborConnections = Descriptive.median(neighborConnections);
                    //                    double meanNeighborConnections = (double) sum /
                    //                        (double) size;
                    writer.write(numFirstNeighbors + "\t" + meanNeighborConnections + "\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
