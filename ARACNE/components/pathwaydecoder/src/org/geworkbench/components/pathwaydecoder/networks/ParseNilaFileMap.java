package org.geworkbench.components.pathwaydecoder.networks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ParseNilaFileMap {
    public ParseNilaFileMap() {
    }

    public HashMap parseNilaFileMap(File nilaFile) {
        HashMap centricNetworkMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(nilaFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arrLine = line.split("\\*");
                String strFactor = arrLine[2];
                String target = arrLine[1];

                String[] arrFactors = strFactor.split(",;");
                for (int i = 0; i < arrFactors.length; i++) {
                    GeneInfo factor = new GeneInfo(arrFactors[i]);
                    CentricNetwork centricNetwork = (CentricNetwork) centricNetworkMap.get(factor);
                    if (centricNetwork == null) {
                        centricNetwork = new CentricNetwork(factor);
                        centricNetworkMap.put(factor, centricNetwork);
                    }
                    centricNetwork.addTarget(new GeneInfo(target));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return centricNetworkMap;
    }
}
