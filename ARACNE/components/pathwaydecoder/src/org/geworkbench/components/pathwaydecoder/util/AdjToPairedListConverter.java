package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.*;


public class AdjToPairedListConverter {

    String expFileName = "Y:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp";
    String adjFileName = "Y:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/NetworkReconstruction/samples_1000/sigma_0.11/CleanedMatrices/pVal_1.0E-5/tolerance_0.0/0.adj";
    String writeFileName = "Y:/Simulations/Results_new/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/NetworkReconstruction/samples_1000/sigma_0.11/CleanedMatrices/pVal_1.0E-5/tolerance_0.0/SF-001_pairedEdgeList.txt";

    public AdjToPairedListConverter() {
    }

    public static void main(String[] args) {
        new AdjToPairedListConverter().doIt(args);
        //        new AdjToPairedListConverter().doMe();
    }

    void doMe() {
        String[] args = {expFileName, adjFileName, writeFileName};
        doIt(args);
    }

    void doIt(String[] args) {
        //        String expFileName = "C:/Simulations/Results/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/SF-001.exp";
        //        String adjFileName = "C:/Simulations/FullResults/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/NetworkReconstruction/samples_1000/sigma_0.15/CleanedMatrices/pVal_0.0010/tolerance_0.0/0.adj";
        //        String writeFileName = "C:/Simulations/Results_new/AGN-century/SF/SF-001/SF-001_true_pairedEdgeList.txt";

        String expFileName = args[0];
        String adjFileName = args[1];
        String writeFileName = args[2];

        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(new File(expFileName));

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFileName, mArraySet);

        convertMatrix(adjMatrix, mArraySet, new File(writeFileName));
    }

    Vector convertAdjFileToPairedList(AdjacencyMatrix adjMatrix, boolean upperTriangular) {
        Vector pairedList = new Vector();

        Set entrySet = adjMatrix.getGeneRows().entrySet();
        Iterator entrySetIt = entrySet.iterator();
        int keyIndex = 0;
        while (entrySetIt.hasNext()) {
            Map.Entry curEntry = (Map.Entry) entrySetIt.next();
            int markerIndex = Integer.parseInt(curEntry.getKey().toString());
            String markerName = adjMatrix.getMarkerName(markerIndex);

            HashMap entryMap = (HashMap) curEntry.getValue();
            Set miSet = entryMap.entrySet();
            Iterator miSetIt = miSet.iterator();
            while (miSetIt.hasNext()) {
                Map.Entry miEntry = (Map.Entry) miSetIt.next();
                double miVal = Double.parseDouble(miEntry.getValue().toString());
                if (miVal > 0.0) {
                    //                        writer.write(miEntry.getKey().toString() + "\t" +
                    //                                     miEntry.getValue().toString() + "\t");
                    int newMarkerIndex = Integer.parseInt(miEntry.getKey().toString());
                    if (!upperTriangular || newMarkerIndex > markerIndex) {
                        String newMarkerName = adjMatrix.getMarkerName(newMarkerIndex);
                        String[] pair = {markerName, newMarkerName};
                        pairedList.add(pair);
                    }
                }
            }
            //                writer.write("\n");
        }
        return pairedList;
    }

    void convertMatrix(AdjacencyMatrix adjMatrix, DSMicroarraySet<DSMicroarray> mArraySet, File writeFile) {
        try {
            FileWriter writer = new FileWriter(writeFile);

            Set entrySet = adjMatrix.getGeneRows().entrySet();
            Iterator entrySetIt = entrySet.iterator();
            int keyIndex = 0;
            while (entrySetIt.hasNext()) {
                Map.Entry curEntry = (Map.Entry) entrySetIt.next();
                int markerIndex = Integer.parseInt(curEntry.getKey().toString());
                String markerName = mArraySet.get(markerIndex).getLabel();
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
                        int newMarkerIndex = Integer.parseInt(miEntry.getKey().toString());
                        writer.write(markerName + "\t" + mArraySet.get(newMarkerIndex).getLabel() + "\n");
                    }
                }
                //                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
