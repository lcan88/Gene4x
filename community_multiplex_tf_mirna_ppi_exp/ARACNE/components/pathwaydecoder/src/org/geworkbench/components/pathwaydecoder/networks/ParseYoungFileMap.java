package org.geworkbench.components.pathwaydecoder.networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ParseYoungFileMap {
    public ParseYoungFileMap() {
    }

    public HashMap parseYoungFileMap(File networkFile) {
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
                    targetGenes[(i - 4) / 2] = new String(arrLine[i]);
                    String geneName = new String(arrLine[i]);
                    GeneInfo geneCenter = new GeneInfo(geneName);
                    CentricNetwork centricNetwork = new CentricNetwork(geneCenter);
                    centricNetworkMap.put(geneCenter, centricNetwork);
                }
            }

            while ((line = reader.readLine()) != null) {

                String[] arrLine = line.split("\t");

                String targetAccession = new String(arrLine[0]);
                String targetName = new String(arrLine[2]);


                for (int targetCtr = 4; targetCtr < arrLine.length; targetCtr += 2) {
                    float pVal = Float.parseFloat(arrLine[targetCtr]);
                    String centerName = new String(targetGenes[(targetCtr - 4) / 2]);
                    CentricNetwork network = (CentricNetwork) centricNetworkMap.get(new GeneInfo(centerName));

                    if (network == null) {
                        System.out.println("Parse young file map null network");
                        continue;
                    }

                    GeneTarget geneTarget = new GeneTarget(targetName);
                    geneTarget.setAccession(targetAccession);
                    geneTarget.setValue(pVal);

                    network.addTarget(geneTarget);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return centricNetworkMap;

    }

}
