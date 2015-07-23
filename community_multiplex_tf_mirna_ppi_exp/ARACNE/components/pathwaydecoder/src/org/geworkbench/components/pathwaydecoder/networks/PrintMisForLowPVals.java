package org.geworkbench.components.pathwaydecoder.networks;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class PrintMisForLowPVals {
    public PrintMisForLowPVals() {
    }

    public static void main(String[] args) {
        new PrintMisForLowPVals().printMis();
    }

    void printMis() {
        try {
            float miThresh = 0.0f;
            //            String dirBase = "/users/aam2110";
            String dirBase = "c:";
            File youngFile = new File(dirBase + "/RevEng/Yeast/Young/binding_by_gene.tsv");
            File regulatorGenesFile = new File(dirBase + "/RevEng/Yeast/Young/YoungRegulatorGenes.txt");

            //        File adjFile = new File("c:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/0.adj");
            File adjFile = new File(dirBase + "/RevEng/Yeast/Rosetta/NoThreshold/samples_300/sigma_0.16/0.adj");
            File expFile = new File(dirBase + "/RevEng/Yeast/Rosetta/processedRosetta1-300.exp");
            File accessionsMapFile = new File(dirBase + "/RevEng/Yeast/Nila/AccessionNames.txt");
            File lowPValMisFile = new File(dirBase + "/RevEng/Yeast/Rosetta/MiDist/MisForLowPVals.txt");

            File highPValMisFile = new File(dirBase + "/RevEng/Yeast/Rosetta/MiDist/MisForHighPVals.txt");
            FileWriter lowPValWriter = new FileWriter(lowPValMisFile);
            FileWriter highPValWriter = new FileWriter(highPValMisFile);

            System.out.println("Parsing young file");
            ParseYoungFileMap youngParser = new ParseYoungFileMap();
            HashMap youngMap = youngParser.parseYoungFileMap(youngFile);

            System.out.println("Parsing aracne file");
            Vector regulatorGenes = org.geworkbench.bison.util.FileUtil.readVector(regulatorGenesFile);
            HashMap accessionsMap = FileUtil.readHashMap(accessionsMapFile);
            AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
            adjMatrix.read(adjFile.getAbsolutePath(), accessionsMap, regulatorGenes, miThresh);
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

                    int targetsLowPValFound = 0;
                    int targetsLowPValNotFound = 0;
                    //                System.out.println(aracneCenter.getGeneName() + " found");
                    Vector youngTargets = youngNetwork.getTargets();
                    Iterator youngTargetsIt = youngTargets.iterator();

                    System.out.println(youngCenter.getGeneName());
                    //                    writer.write(youngCenter.getGeneName() + "\n");

                    while (youngTargetsIt.hasNext()) {
                        GeneTarget gt = (GeneTarget) youngTargetsIt.next();
                        float pVal = gt.getValue();
                        if (pVal < .001) {
                            if (aracneNetwork.contains(gt)) {
                                targetsLowPValFound++;
                                GeneTarget aracneTarget = (GeneTarget) aracneNetwork.getTarget(gt);
                                lowPValWriter.write(aracneTarget.getValue() + "\n");
                                //                                System.out.println("\t" + gt.getAccession() +
                                //                                    ":" + gt.getGeneName() + "\t" + aracneTarget.getValue());
                                //                 writer.write("\t" + gt.getAccession() +
                                //                                    ":" + gt.getGeneName() + "\t" + aracneTarget.getValue() + "\n");
                            } else {
                                targetsLowPValNotFound++;

                                System.out.println("\t" + gt.getAccession() + ":" + gt.getGeneName() + "NOT FOUND");
                                //                 writer.write("\t" + gt.getAccession() +
                                //                                    ":" + gt.getGeneName() + "\t" + "NOT FOUND" + "\n");
                            }
                        } else {
                            if (aracneNetwork.contains(gt)) {
                                GeneTarget aracneTarget = (GeneTarget) aracneNetwork.getTarget(gt);
                                highPValWriter.write(aracneTarget.getValue() + "\n");
                            }
                        }
                    }
                    //                    System.out.println("\n");
                    //                    writer.write("\n");

                    //                System.out.println(youngCenter.getGeneName() + "\tFound\t" + targetsLowPValFound +
                    //                                   "\tNot Found\t" + targetsLowPValNotFound);
                    //                System.out.println(nilaCenter.getGeneName() + "\t" +
                    //                                   nilaNetwork.getNumPredicted() + "\t" + nilaNetwork.getPctPredicted());
                } else {
                    //                System.out.println("null");
                    //                System.out.println(aracneCenter.getGeneName() + "NOT found");
                }
            }
            System.out.println("Done");
            lowPValWriter.close();
            highPValWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
