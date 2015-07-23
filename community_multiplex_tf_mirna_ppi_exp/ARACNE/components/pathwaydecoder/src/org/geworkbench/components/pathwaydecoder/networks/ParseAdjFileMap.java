package org.geworkbench.components.pathwaydecoder.networks;

import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ParseAdjFileMap {
    public ParseAdjFileMap() {
    }

    public HashMap parseAdjFileMap(File adjFile, File expFile, File accessionsMapFile, float miThresh) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        maSet.readFromFile(expFile);

        AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
        adjMatrix.read(adjFile.getAbsolutePath(), miThresh);

        HashMap accessionsMap = org.geworkbench.bison.util.FileUtil.readHashMap(accessionsMapFile);

        return parseAdjFileMap(adjMatrix, maSet, accessionsMap);
    }

    public HashMap parseAdjFileMap(AdjacencyMatrix adjMatrix, DSMicroarraySet<DSMicroarray> mArraySet, HashMap accessionsMap) {
        HashMap centricNetworkMap = new HashMap();

        try {
            HashMap geneRows = adjMatrix.getGeneRows();
            Set geneRowsEntrySet = geneRows.entrySet();
            Iterator geneRowsIt = geneRowsEntrySet.iterator();
            while (geneRowsIt.hasNext()) {
                Entry geneRowEntry = (Entry) geneRowsIt.next();
                int geneRowKey = ((Integer) geneRowEntry.getKey()).intValue();
                String geneRowAccession = mArraySet.getMarkers().get(geneRowKey).getLabel();
                GeneInfo geneCenter = new GeneInfo();
                geneCenter.setAccession(geneRowAccession);
                //                String[] arrGeneRowName = mArraySet.getMarkerLabel(geneRowKey).
                //                    split(",");

                //                if (arrGeneRowName != null && arrGeneRowName.length > 0) {
                //                    geneCenter.setGeneName(arrGeneRowName[0]);
                //                }
                String geneRowName = (String) accessionsMap.get(geneRowAccession);
                geneCenter.setGeneName(geneRowName);

                CentricNetwork centricNetwork = new CentricNetwork(geneCenter);
                centricNetworkMap.put(geneCenter, centricNetwork);

                HashMap geneRowValues = (HashMap) geneRowEntry.getValue();
                Iterator targetsIterator = geneRowValues.entrySet().iterator();
                while (targetsIterator.hasNext()) {
                    Entry targetEntry = (Entry) targetsIterator.next();
                    int targetId = ((Integer) targetEntry.getKey()).intValue();
                    float targetMi = ((Float) targetEntry.getValue()).floatValue();

                    GeneTarget target = new GeneTarget();

                    String targetAccession = mArraySet.getMarkers().get(targetId).getLabel();
                    target.setAccession(targetAccession);

                    String targetName = (String) accessionsMap.get(targetAccession);
                    target.setGeneName(targetName);
                    target.setValue(targetMi);
                    //                    String[] arrTargetName = mArraySet.getMarkerLabel(targetId).
                    //                        split(",");

                    //                    if (arrTargetName != null && arrTargetName.length > 0) {
                    //                        target.setGeneName(arrGeneRowName[0]);
                    //                    }
                    centricNetwork.addTarget(target);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return centricNetworkMap;

    }

}
