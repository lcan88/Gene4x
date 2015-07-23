package org.geworkbench.components.pathwaydecoder.networks;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class ReadNetworkFiles {
    public ReadNetworkFiles() {
    }

    public static void main(String[] args) {
        new ReadNetworkFiles().scoreNila();
    }

    void scoreNila() {
        File nilaFile = new File("y:/RevEng/Yeast/Nila/promoter_order_by_name.txt");
        File aracneFile = new File("y:/RevEng/Yeast/Nila/Rosetta_tol_15.adj");
        //        File aracneFile = new File("Y:/RevEng/Yeast/Rosetta/CleanedMatrices/samples_300/sigma_0.16/pValue_1.0e-6/tolerance_0.15/0.adj");

        ParseNilaFileMap nilaParser = new ParseNilaFileMap();
        ParseCentricNetworkFileMap networkParser = new ParseCentricNetworkFileMap();

        System.out.println("Parsing nila file");
        HashMap nilaMap = nilaParser.parseNilaFileMap(nilaFile);

        System.out.println("Parsing aracne file");
        HashMap aracneMap = networkParser.parseCentricNetworkFileMap(aracneFile);


        System.out.println("nila size " + nilaMap.size());
        System.out.println("aracne size " + aracneMap.size());

        Collection nilaNetworks = nilaMap.values();
        Iterator nilaIt = nilaNetworks.iterator();
        while (nilaIt.hasNext()) {
            CentricNetwork nilaNetwork = (CentricNetwork) nilaIt.next();
            GeneInfo nilaCenter = nilaNetwork.getGeneCenter();

            CentricNetwork aracneCenter = (CentricNetwork) aracneMap.get(nilaCenter);
            if (aracneCenter != null) {
                Vector aracneTargets = aracneCenter.getTargets();
                Iterator aracneIt = aracneTargets.iterator();
                while (aracneIt.hasNext()) {
                    GeneInfo gi = (GeneInfo) aracneIt.next();
                    if (nilaNetwork.contains(gi)) {
                        nilaNetwork.foundTarget();
                    }
                }
                System.out.println(nilaCenter.getGeneName() + "\t" + nilaNetwork.getNumPredicted() + "\t" + nilaNetwork.getPctPredicted());
            } else {
                //                System.out.println("null");
            }
        }
    }
}
