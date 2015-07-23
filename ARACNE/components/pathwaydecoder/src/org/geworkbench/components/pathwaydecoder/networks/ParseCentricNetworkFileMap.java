package org.geworkbench.components.pathwaydecoder.networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ParseCentricNetworkFileMap {
    public ParseCentricNetworkFileMap() {
    }

    public HashMap parseCentricNetworkFileMap(File networkFile) {
        HashMap centricNetworkMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(networkFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Center:")) {
                    String[] arrLine = line.split("[:\\t]");

                    if (arrLine.length < 4) {
                        continue;
                    }

                    String centerAccession = arrLine[1];
                    String centerName = arrLine[3];

                    GeneInfo geneCenter = new GeneInfo(centerName);
                    geneCenter.setAccession(centerAccession);

                    CentricNetwork centricNetwork = new CentricNetwork(geneCenter);
                    centricNetworkMap.put(geneCenter, centricNetwork);

                    String targetLine;
                    while (!"".equals(targetLine = reader.readLine())) {
                        String[] arrTargetLine = targetLine.split("\t");
                        if (arrTargetLine.length < 2) {
                            //                            System.out.println("hh");
                            break;
                        }
                        String targetName = arrTargetLine[1];
                        if (!"---".equals(targetName)) {
                            GeneInfo target = new GeneInfo(targetName);
                            target.setAccession(arrTargetLine[0]);

                            centricNetwork.addTarget(target);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return centricNetworkMap;

    }

}
