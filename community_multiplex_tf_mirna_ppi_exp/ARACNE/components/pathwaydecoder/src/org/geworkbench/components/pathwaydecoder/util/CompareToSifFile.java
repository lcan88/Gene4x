package org.geworkbench.components.pathwaydecoder.util;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class CompareToSifFile {
    public CompareToSifFile() {
    }

    public static void main(String[] args) {
        new CompareToSifFile().doIt(args);
    }

    void doIt(String[] args) {
        //        String file1 = "C:/Simulations/FullResults/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/hr.txt";
        //        String file2 = "C:/Simulations/FullResults/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0/arcs_tab.txt";
        //        compareFiles(new File(file1), new File(file2));
        //        String dirBase = "Y:/Simulations_test/Results_new_method/AGN-century/SF/SF-001/reactNoise_1.0/langNoise_0.0";
        //        File expFile = new File(dirBase + "/SF-001.exp");
        //        File adjFile = new File(dirBase + "/CleanedMatrices/samples_1000/sigma_0.11/pVal_20.0/tolerance_0.0/0.adj");
        //        File sifFile = new File("Y:/Simulations/Visual/Cytoscape/SF-001.sif");
        //        File writeFile = new File("Y:/Simulations/Visual/Cytoscape/SF-001_recovery.sif");
        File expFile = new File(args[0]);
        File adjFile = new File(args[1]);
        File sifFile = new File(args[2]);
        File writeFile = new File(args[3]);


        Vector comparedEdges = compareFiles(adjFile, sifFile, expFile);

        try {
            FileWriter writer = new FileWriter(writeFile);

            for (int i = 0; i < comparedEdges.size(); i++) {
                String[] edge = (String[]) comparedEdges.get(i);
                for (int j = 0; j < edge.length; j++) {
                    writer.write(edge[j] + "\t");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Vector compareFiles(File adjFile, File sifFile, File expFile) {
        CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
        mArraySet.readFromFile(expFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFile.getAbsolutePath(), mArraySet);

        AdjToPairedListConverter adjConverter = new AdjToPairedListConverter();
        Vector adjPairedList = adjConverter.convertAdjFileToPairedList(adjMatrix, true);

        Vector sifFileData = FileUtil.readFile(sifFile);


        Vector comparedEdges = new Vector();

        //First add all pairs that were not recovered
        for (int i = 0; i < sifFileData.size(); i++) {
            String[] sifEdge = (String[]) sifFileData.get(i);

            boolean foundEdge = false;
            for (int j = 0; j < adjPairedList.size(); j++) {
                String[] adjPair = (String[]) adjPairedList.get(j);
                if (adjPair[0].equals(sifEdge[0]) && adjPair[1].equals(sifEdge[2])) {
                    foundEdge = true;
                }
                if (adjPair[0].equals(sifEdge[2]) && adjPair[1].equals(sifEdge[0])) {
                    foundEdge = true;
                }
            }
            if (!foundEdge) {
                comparedEdges.add(sifEdge);
            }
        }

        //Now compared all recovered edges
        for (int j = 0; j < adjPairedList.size(); j++) {
            String[] adjPair = (String[]) adjPairedList.get(j);
            boolean foundEdge = false;
            String[] recoveredSifEdge = null;
            for (int i = 0; i < sifFileData.size(); i++) {
                String[] sifEdge = (String[]) sifFileData.get(i);

                if (adjPair[0].equals(sifEdge[0]) && adjPair[1].equals(sifEdge[2])) {
                    foundEdge = true;
                    recoveredSifEdge = sifEdge;
                }
                if (adjPair[0].equals(sifEdge[2]) && adjPair[1].equals(sifEdge[0])) {
                    foundEdge = true;
                    recoveredSifEdge = sifEdge;
                }
            }

            String edgeStatus = "";
            if (foundEdge) {
                edgeStatus = recoveredSifEdge[1] + "_recovered";
            } else {
                edgeStatus = "false";
            }

            String[] newSifEdge = new String[3];
            newSifEdge[0] = adjPair[0];
            newSifEdge[1] = edgeStatus;
            newSifEdge[2] = adjPair[1];
            comparedEdges.add(newSifEdge);
        }

        return comparedEdges;
    }

    void compareFiles_2(File file1, File file2) {
        HashSet set = new HashSet();
        Vector file1Data = FileUtil.readFile(file1);
        Vector file2Data = FileUtil.readFile(file2);

        Iterator it = file1Data.iterator();
        while (it.hasNext()) {
            String[] data1Line = (String[]) it.next();
            if (data1Line == null || data1Line.length < 2) {
                continue;
            }

            //            for(int i = 0; i < data1Line.length; i++){
            //                System.out.print(data1Line[i] + "\t");
            //            }
            //            System.out.println();

            Iterator it2 = file2Data.iterator();
            while (it2.hasNext()) {
                String[] data2Line = (String[]) it2.next();
                if (data1Line[0].equals("G" + data2Line[0]) && data1Line[1].equals("G" + data2Line[1])) {
                    set.add(data2Line);
                    //                    for(int i = 0; i < data2Line.length; i++){
                    //                        System.out.print(data2Line[i] + "\t");
                    //                    }
                    //                    System.out.println();
                }
            }

        }

        Iterator setIt = set.iterator();
        while (setIt.hasNext()) {
            String[] data2Line = (String[]) setIt.next();
            for (int i = 0; i < data2Line.length; i++) {
                System.out.print(data2Line[i] + "\t");
            }
            System.out.println();
        }
    }
}
