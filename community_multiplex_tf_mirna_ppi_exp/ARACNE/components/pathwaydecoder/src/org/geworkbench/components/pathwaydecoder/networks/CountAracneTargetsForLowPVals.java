package org.geworkbench.components.pathwaydecoder.networks;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class CountAracneTargetsForLowPVals {
    public CountAracneTargetsForLowPVals() {
    }

    public static void main(String[] args) {
        new CountAracneTargetsForLowPVals().countAracneTargets();
    }

    void countAracneTargets() {
        try {
            float miThresh = 0.055112f;
            String dirBase = "/users/aam2110";
            File youngFile = new File(dirBase + "/RevEng/Yeast/Young/binding_by_gene.tsv");
            //        File aracneFile = new File(dirBase + "/RevEng/Yeast/Nila/Rosetta_tol_15.adj");
            //        File adjFile = new File("c:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/0.adj");
            File adjFile = new File(dirBase + "/RevEng/Yeast/Rosetta/NoThreshold/samples_300/sigma_0.16/0.adj");
            File expFile = new File(dirBase + "/RevEng/Yeast/Rosetta/processedRosetta1-300.exp");
            File accessionsMapFile = new File(dirBase + "/RevEng/Yeast/Nila/AccessionNames.txt");
            File writeFile = new File(dirBase + "/RevEng/Yeast/Rosetta/NoThreshold/samples_300/sigma_0.16/MisForLowPVals_miThresh_0.055.txt");
            FileWriter writer = new FileWriter(writeFile);

            System.out.println("Parsing young file");
            ParseYoungFileMap youngParser = new ParseYoungFileMap();
            HashMap youngMap = youngParser.parseYoungFileMap(youngFile);

            System.out.println("Parsing aracne file");
            //        ParseCentricNetworkFileMap networkParser = new ParseCentricNetworkFileMap();
            //        HashMap aracneMap = networkParser.parseCentricNetworkFileMap(aracneFile);
            ParseAdjFileMap adjParser = new ParseAdjFileMap();
            HashMap aracneMap = adjParser.parseAdjFileMap(adjFile, expFile, accessionsMapFile, miThresh);

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

                    //                    System.out.println(youngCenter.getGeneName());
                    writer.write(youngCenter.getGeneName() + "\n");

                    while (youngTargetsIt.hasNext()) {
                        GeneTarget gt = (GeneTarget) youngTargetsIt.next();
                        float pVal = gt.getValue();
                        if (pVal < .001) {
                            if (aracneNetwork.contains(gt)) {
                                targetsLowPValFound++;
                                GeneTarget aracneTarget = (GeneTarget) aracneNetwork.getTarget(gt);
                                //                                System.out.println("\t" + gt.getAccession() +
                                //                                    ":" + gt.getGeneName() + gt.getValue());
                                writer.write("\t" + gt.getAccession() + ":" + gt.getGeneName() + "\t" + aracneTarget.getValue() + "\n");
                            } else {
                                targetsLowPValNotFound++;

                                //                                System.out.println("\t" + gt.getAccession() +
                                //                                    ":" + gt.getGeneName() + "NOT FOUND");
                                writer.write("\t" + gt.getAccession() + ":" + gt.getGeneName() + "\t" + "NOT FOUND" + "\n");
                            }
                        }
                    }
                    //                    System.out.println("\n");
                    writer.write("\n");

                    //                System.out.println(youngCenter.getGeneName() + "\tFound\t" + targetsLowPValFound +
                    //                                   "\tNot Found\t" + targetsLowPValNotFound);
                    //                System.out.println(nilaCenter.getGeneName() + "\t" +
                    //                                   nilaNetwork.getNumPredicted() + "\t" + nilaNetwork.getPctPredicted());
                } else {
                    //                System.out.println("null");
                    //                System.out.println(aracneCenter.getGeneName() + "NOT found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
