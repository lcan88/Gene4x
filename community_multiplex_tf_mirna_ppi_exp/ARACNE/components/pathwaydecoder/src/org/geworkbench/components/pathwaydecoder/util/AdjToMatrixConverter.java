package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class AdjToMatrixConverter {
    String[] geneNames;
    double[][] miMatrix;

    public AdjToMatrixConverter() {
    }

    public static void main(String[] args) {
        new AdjToMatrixConverter().doIt(args);
    }

    void doIt(String[] args) {
        //        String expFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp";
        String expFileName = args[0].replaceAll("/cygdrive/y/", "y:/");
        String adjFileName = args[1].replaceAll("/cygdrive/y/", "y:/");
        String writeFileName = args[2].replaceAll("/cygdrive/y/", "y:/");

        convertFile(expFileName, adjFileName, writeFileName);
    }

    public void convertFile(String expFileName, String adjFileName, String writeFileName) {
        //        String expFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp";
        //        String fullAdjMatrixFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/SF-001_true.adj";

        //        String writeFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/AdjMatrix_true.txt";
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        AdjacencyMatrix fullAdjMatrix = new AdjacencyMatrix();
        HashMap nameToIndexMapping = fullAdjMatrix.readNameToIndexMappings(new File(adjFileName));

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFileName, mArraySet);

        convertMatrix(nameToIndexMapping, adjMatrix, mArraySet, writeFileName);
    }

    void convertMatrix(HashMap nameToIndexMapping, AdjacencyMatrix adjMatrix, DSMicroarraySet mArraySet, String writeFile) {
        try {
            //            FileWriter writer = new FileWriter(writeFile);
            HashMap tmpMatrixIndexToNameMapping = adjMatrix.getKeyMapping();

            int numGenes = nameToIndexMapping.size();

            Set entrySet = adjMatrix.getGeneRows().entrySet();

            geneNames = new String[numGenes];
            miMatrix = new double[numGenes][numGenes];
            for (int i = 0; i < numGenes; i++) {
                for (int j = 0; j < numGenes; j++) {
                    miMatrix[i][i] = 0.0;
                }
            }

            Iterator entrySetIt = entrySet.iterator();
            int keyIndex = 0;
            while (entrySetIt.hasNext()) {
                Map.Entry curEntry = (Map.Entry) entrySetIt.next();
                int tmpMarkerIndex = Integer.parseInt(curEntry.getKey().toString());
                String markerName = (String) tmpMatrixIndexToNameMapping.get((new Integer(tmpMarkerIndex)));

                int markerIndex = ((Integer) nameToIndexMapping.get(markerName)).intValue();
                geneNames[markerIndex] = markerName;

                //                writer.write(markerName + ":" + markerIndex + "\t");
                //                keyIndex++;
                HashMap entryMap = (HashMap) curEntry.getValue();
                Set miSet = entryMap.entrySet();
                Iterator miSetIt = miSet.iterator();
                while (miSetIt.hasNext()) {
                    Map.Entry miEntry = (Map.Entry) miSetIt.next();
                    double miVal = Double.parseDouble(miEntry.getValue().toString());
                    if (miVal > 0.0) {
                        //                        writer.write(miEntry.getKey().toString() + "\t" +
                        //                                     miEntry.getValue().toString() + "\t");
                        int newTmpMarkerIndex = Integer.parseInt(miEntry.getKey().toString());
                        String newMarkerName = (String) tmpMatrixIndexToNameMapping.get((new Integer(newTmpMarkerIndex)));
                        int newMarkerIndex = ((Integer) nameToIndexMapping.get(newMarkerName)).intValue();

                        miMatrix[Math.min(markerIndex, newMarkerIndex)][Math.max(markerIndex, newMarkerIndex)] = miVal;
                        //                        writer.write(markerName + "\t" + mArraySet.getMarkerLabel(newMarkerIndex) + "\n");
                    }
                }
                //                writer.write("\n");
            }
            //            writer.close();

            //            FileUtil.printMatrix(geneNames, geneNames, miMatrix, writeFile);
            org.geworkbench.bison.util.FileUtil.printMatrix(miMatrix, writeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
