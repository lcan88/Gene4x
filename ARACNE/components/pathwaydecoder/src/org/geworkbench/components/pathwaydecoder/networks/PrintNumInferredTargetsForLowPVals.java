package org.geworkbench.components.pathwaydecoder.networks;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class PrintNumInferredTargetsForLowPVals {
    public PrintNumInferredTargetsForLowPVals() {
    }

    public static void main(String[] args) {
        new PrintNumInferredTargetsForLowPVals().doIt(args);
    }

    void doIt(String[] args) {
        File youngFile = new File(args[0]);
        File adjFile = new File(args[1]);
        File expFile = new File(args[2]);
        File accessionsMapFile = new File(args[3]);
        File regulatorGenesFile = new File(args[4]);
        File writeFile = new File(args[5]);
        countAracneTargets(youngFile, adjFile, expFile, accessionsMapFile, regulatorGenesFile, writeFile);
    }

    void countAracneTargets(File youngFile, File adjFile, File expFile, File accessionsMapFile, File regulatorGenesFile, File writeFile) {
        try {
            int targetsFound = 0;
            int targetsNotFound = 0;
            int nonTargetsFound = 0;
            FileWriter writer = new FileWriter(writeFile);

            System.out.println("Parsing young file");
            ParseYoungFileMap youngParser = new ParseYoungFileMap();
            HashMap youngMap = youngParser.parseYoungFileMap(youngFile);

            System.out.println("Parsing aracne file");
            Vector regulatorGenes = org.geworkbench.bison.util.FileUtil.readVector(regulatorGenesFile);
            HashMap accessionsMap = org.geworkbench.bison.util.FileUtil.readHashMap(accessionsMapFile);
            AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
            adjMatrix.read(adjFile.getAbsolutePath(), accessionsMap, regulatorGenes, 0.0f);
            ParseAdjFileMap adjParser = new ParseAdjFileMap();

            CSExprMicroarraySet mArraySet = new CSExprMicroarraySet();
            mArraySet.readFromFile(expFile);
            HashMap aracneMap = adjParser.parseAdjFileMap(adjMatrix, mArraySet, accessionsMap);

            System.out.println("young size " + youngMap.size());
            System.out.println("aracne size " + aracneMap.size());

            Collection youngNetworks = youngMap.values();
            Iterator youngIt = youngNetworks.iterator();
            while (youngIt.hasNext()) {
                CentricNetwork youngNetwork = (CentricNetwork) youngIt.next();
                GeneInfo youngCenter = youngNetwork.getGeneCenter();

                CentricNetwork aracneNetwork = (CentricNetwork) aracneMap.get(youngCenter);
                if (aracneNetwork != null) {

                    Vector youngTargets = youngNetwork.getTargets();
                    Iterator youngTargetsIt = youngTargets.iterator();

                    System.out.println(youngCenter.getGeneName());

                    while (youngTargetsIt.hasNext()) {
                        GeneTarget gt = (GeneTarget) youngTargetsIt.next();
                        float pVal = gt.getValue();
                        if (pVal < .001) {
                            if (aracneNetwork.contains(gt)) {
                                targetsFound++;
                            } else {
                                targetsNotFound++;

                            }
                        } else {
                            if (aracneNetwork.contains(gt)) {
                                nonTargetsFound++;
                            }
                        }
                    }

                } else {
                    //                System.out.println("null");
                    //                System.out.println(aracneCenter.getGeneName() + "NOT found");
                }
            }
            System.out.println("Done");
            writer.write(targetsFound + "\t" + targetsNotFound + "\t" + nonTargetsFound);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
