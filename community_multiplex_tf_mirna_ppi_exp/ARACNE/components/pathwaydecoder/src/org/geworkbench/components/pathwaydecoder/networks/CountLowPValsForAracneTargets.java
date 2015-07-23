package org.geworkbench.components.pathwaydecoder.networks;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class CountLowPValsForAracneTargets {
    public CountLowPValsForAracneTargets() {
    }

    public static void main(String[] args) {
        new CountLowPValsForAracneTargets().scoreYoungFile();
    }

    void scoreYoungFile() {
        File youngFile = new File("c:/RevEng/Yeast/Young/binding_by_gene.tsv");
        File aracneFile = new File("c:/RevEng/Yeast/Nila/StressResponse_FN_GS.adj");

        System.out.println("Parsing young file");
        ParseYoungFileMap youngParser = new ParseYoungFileMap();
        HashMap youngMap = youngParser.parseYoungFileMap(youngFile);

        System.out.println("Parsing aracne file");
        ParseCentricNetworkFileMap networkParser = new ParseCentricNetworkFileMap();
        HashMap aracneMap = networkParser.parseCentricNetworkFileMap(aracneFile);

        System.out.println("young size " + youngMap.size());
        System.out.println("aracne size " + aracneMap.size());

        Collection aracneNetworks = aracneMap.values();
        Iterator aracneIt = aracneNetworks.iterator();
        while (aracneIt.hasNext()) {
            CentricNetwork aracneNetwork = (CentricNetwork) aracneIt.next();
            GeneInfo aracneCenter = aracneNetwork.getGeneCenter();

            CentricNetwork youngNetwork = (CentricNetwork) youngMap.get(aracneCenter);
            if (youngNetwork != null) {
                int targetsRepresented = 0;
                int targetsNotRepresented = 0;
                int targetsLowPVal = 0;
                int targetsHighPVal = 0;
                //                System.out.println(aracneCenter.getGeneName() + " found");
                Vector aracneTargets = aracneNetwork.getTargets();
                Iterator aracneTargetsIt = aracneTargets.iterator();
                while (aracneTargetsIt.hasNext()) {
                    GeneInfo gi = (GeneInfo) aracneTargetsIt.next();
                    if (youngNetwork.contains(gi)) {
                        //                        nilaNetwork.foundTarget();
                        targetsRepresented++;
                        GeneTarget youngTarget = (GeneTarget) youngNetwork.getTarget(gi);
                        float value = youngTarget.getValue();
                        if (value < .001) {
                            targetsLowPVal++;
                        } else {
                            targetsHighPVal++;
                        }
                    } else {
                        targetsNotRepresented++;
                    }
                    DSGeneMarker mi;
                    CSExprMicroarraySet maSet;

                }
                System.out.println(aracneCenter.getGeneName() + "\tLowPVal\t" + targetsLowPVal + "\tHighPVal\t" + targetsHighPVal);
                //                System.out.println(nilaCenter.getGeneName() + "\t" +
                //                                   nilaNetwork.getNumPredicted() + "\t" + nilaNetwork.getPctPredicted());
            } else {
                //                System.out.println("null");
                //                System.out.println(aracneCenter.getGeneName() + "NOT found");
            }
        }


    }
}
