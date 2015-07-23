package org.geworkbench.components.pathwaydecoder.networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ParseYoungFileMap_bak {
    public ParseYoungFileMap_bak() {
    }

    public HashMap parseYoungFileMap_bak(File networkFile) {
        HashMap centricNetworkMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(networkFile));
            String line;

            reader.readLine();
            String[] targetGenes = null;
            if ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\t");
                targetGenes = new String[((arrLine.length - 4) / 2) + 1];
                for (int i = 4; i < arrLine.length; i += 2) {
                    targetGenes[(i - 4) / 2] = arrLine[i];
                }
            }

            while ((line = reader.readLine()) != null) {

                String[] arrLine = line.split("\t");

                String centerAccession = arrLine[0];
                String centerName = arrLine[2];

                GeneInfo geneCenter = new GeneInfo(centerName);
                geneCenter.setAccession(centerAccession);

                CentricNetwork centricNetwork = new CentricNetwork(geneCenter);
                centricNetworkMap.put(geneCenter, centricNetwork);

                for (int targetCtr = 4; targetCtr < arrLine.length; targetCtr += 2) {
                    float pVal = Float.parseFloat(arrLine[targetCtr]);
                    String targetName = targetGenes[(targetCtr - 4) / 2];
                    GeneTarget target = new GeneTarget(targetName);
                    target.setValue(pVal);
                    centricNetwork.addTarget(target);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return centricNetworkMap;

    }

}
