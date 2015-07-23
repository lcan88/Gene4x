package org.geworkbench.components.pathwaydecoder.remote;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

import java.io.*;
import java.util.*;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 * @todo - watkin - There are two AdjacencyMatrix classes, this one appears to be redundant and little-used. 
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class AdjacencyMatrix {
    protected HashMap geneRows = new HashMap();
    protected DSMicroarraySet maSet = null;
    protected HashMap idToGeneMapper = new HashMap();
    protected HashMap snToGeneMapper = new HashMap();

    public AdjacencyMatrix() {
    }
    public int getMappedId(int geneId) {
        if (geneId >= 0) {
            try {
                DSGeneMarker gm = (DSGeneMarker) maSet.getMarkers().get(geneId);
                String sn = gm.getShortName();
            if(sn == null){
                return geneId;
            }
            if (sn.compareToIgnoreCase("ExoBCL6") == 0) {
                geneId = -1;
            }
            else if (gm.getLabel().compareToIgnoreCase("1827_s_at") == 0) {
                geneId = gm.getSerial();
            }
            else if (gm.getLabel().compareToIgnoreCase("1936_s_at") == 0) {
                geneId = gm.getSerial();
            }
            else {
                if (sn.compareToIgnoreCase("MYC") == 0) {
                    int xxx = 1;
                }
                if (gm.getLabel().compareToIgnoreCase("1936_s_at") == 0) {
                    sn = "MYC";
                    gm = (DSGeneMarker) maSet.get("1973_s_at");
                }
                // Test if a gene with the same name was mapped before.
                Integer prevId = (Integer) idToGeneMapper.get(gm.getLabel());
                if (prevId != null) {
                    // This gene was mapped before. Replace with mapped one
                    geneId = prevId.intValue();
                }
                else {
                    // Test if a gene with the same name was reported before.
                    prevId = (Integer) snToGeneMapper.get(sn);
                    if (prevId != null) {
                        // There was a previous gene with the same name. Hence:
                        // replace the id, and add a new mapping to both idToGeneMapper
                        // and geneToIdMapper
                        snToGeneMapper.put(sn, prevId);
                        idToGeneMapper.put(gm.getLabel(), prevId);
                        geneId = prevId.intValue();
                    }
                    else {
                        snToGeneMapper.put(sn, new Integer(geneId));
                        idToGeneMapper.put(gm.getLabel(), new Integer(geneId));
                    }
                }
            }
        } catch (Exception exp) {
           return geneId;
       }
       }
        return geneId;
    }
    // read a line of Adjacency Matrix output, MIThreshold=0
     public void read(int geneId1, String line) {
         if (geneId1 >= 0 && line.length() > 0) {
             StringTokenizer tr = new StringTokenizer(line);
             while (tr.hasMoreTokens()) {
                 String strGeneId2 = new String(tr.nextToken());
                 int geneId2 = getMappedId(Integer.parseInt(strGeneId2));
                 if (geneId2 >= 0) {
                     String strMi = new String(tr.nextToken());
                     float mi = Float.parseFloat(strMi);
                     if (mi > 0) {
                         if (geneId1 != geneId2) {
                             add(geneId1, geneId2, mi);
                             // this.addInteractionType2(geneId1, geneId2, mi);
                         }
                     }
                 }
             }
         }
     }

    public void read(String name, float miThresh) {
        int connectionsInstantiated = 0;
        int connectionsIgnored = 0;
        double startTime = 0;
        double endTime = 0;

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(name));
            try {
                String line;
                int ctr = 0;
                while ((line = br.readLine()) != null) {
                    if (ctr++ % 100 == 0) {
                        endTime = System.currentTimeMillis();
                        double totalTime = endTime - startTime;
                        System.out.println("Time for iteration " + (totalTime / 1000.0));
                        System.out.println("Adjacency Matrix Reading line " + ctr);
                        startTime = System.currentTimeMillis();
                        double usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        System.out.println("used memory " + usedMemory);
                    }
                    if (ctr % 500 == 0) {
                        System.gc();
                    }

                    if (line.length() > 0 && line.charAt(0) != '-') {
                        StringTokenizer tr = new StringTokenizer(line, "\t:");

                        String geneAccess = new String(tr.nextToken());
                        String strGeneId1 = new String(tr.nextToken());
                        int geneId1 = Integer.parseInt(strGeneId1);
                        if (geneId1 >= 0) {
                            while (tr.hasMoreTokens()) {
                                String strGeneId2 = new String(tr.nextToken());
                                int geneId2 = Integer.parseInt(strGeneId2);
                                if (geneId2 >= 0) {
                                    String strMi = new String(tr.nextToken());
                                    float mi = Float.parseFloat(strMi);
                                    if (mi > miThresh) {
                                        if (geneId1 != geneId2) {
                                            connectionsInstantiated++;
                                            add(geneId1, geneId2, mi);
                                        } else {
                                            connectionsIgnored++;
                                        }
                                    } else {
                                        connectionsIgnored++;
                                    }
                                }
                            }
                        }

                    }
                    //                    line = br.readLine();
                }
                System.out.println("Connections instantiated " + connectionsInstantiated);
                System.out.println("Connections ignored " + connectionsIgnored);
                System.out.println("Total processed " + (connectionsInstantiated + connectionsIgnored));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex3) {
            ex3.printStackTrace();
        }

    }

    /**
     * Adds and edge between geneId1 and geneId2
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param edge    float
     */
    public void add(int geneId1, int geneId2, float edge) {
        if ((geneId1 >= 0) && (geneId2 >= 0)) {
            // adding the neighbor and edge for geneId1
            // gene1 -> (gene2, edge)
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId1), row);
            }
            row.put(new Integer(geneId2), new Float(edge));

            // doing it both ways; [gene2 -> (gene1, edge)]
            row = (HashMap) geneRows.get(new Integer(geneId2));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId2), row);
            }
            row.put(new Integer(geneId1), new Float(edge));
        }
    }
    public void print(DSMicroarraySet mArraySet, File writeFile) {
//        print(mArraySet, writeFile, geneRows);
        print(mArraySet, writeFile, 0.0);
    }

    public void print(DSMicroarraySet mArraySet, File writeFile, double miThresh) {
        print(mArraySet, writeFile, geneRows, miThresh);
    }

    public void print(DSMicroarraySet mArraySet, File writeFile, double miThresh,
                      int startIndex, int endIndex) {
        print(mArraySet, writeFile, geneRows, miThresh, startIndex, endIndex);
    }
    public void printGene(DSMicroarraySet mArraySet, File writeFile, Map map,
                           double miThresh, int markerIndex) {
         try {
             FileWriter writer = new FileWriter(writeFile);

             HashMap geneRow = (HashMap) map.get(new Integer(markerIndex));
//            String markerName = mArraySet.getMarkerLabel(markerIndex);

             DSGeneMarker dsGeneMarker = (DSGeneMarker) mArraySet.getMarkers().get(markerIndex);
             String markerName = dsGeneMarker.getLabel();
             writer.write(markerName + ":" + markerIndex + "\t");

             Set miSet = geneRow.entrySet();
             Iterator miSetIt = miSet.iterator();
             while (miSetIt.hasNext()) {
                 Map.Entry miEntry = (Map.Entry) miSetIt.next();
                 double miVal = Double.parseDouble(miEntry.getValue().
                                                   toString());
//                    if (miVal >= 0.0) {
                 if (miVal > miThresh) {
                     writer.write(miEntry.getKey().toString() + "\t" +
                                  miEntry.getValue().toString() + "\t");
                 }

             }
             writer.close();
         }
         catch (Exception e) {
             e.printStackTrace();
         }
     }

     public void print(DSMicroarraySet mArraySet, File writeFile, Map map,
                       double miThresh, int startIndex, int endIndex) {
         try {
             FileWriter writer = new FileWriter(writeFile);

//                Set entrySet = map.entrySet();
//                Iterator entrySetIt = entrySet.iterator();
//                int keyIndex = 0;
//                while (entrySetIt.hasNext()) {
//                    Map.Entry curEntry = (Map.Entry) entrySetIt.next();
             for (int geneRowCtr = startIndex; geneRowCtr < endIndex; geneRowCtr++) {

//                    int markerIndex = Integer.parseInt(curEntry.getKey().toString());
                 int markerIndex = geneRowCtr;
//                    if(markerIndex >= size()){
//                        continue;
//                    }

                 String markerName = null;
//                    String markerName = mArraySet.getMarkerLabel(markerIndex);
                 if (markerIndex < mArraySet.getMarkers().size()) {
                     markerName = ((DSGeneMarker) mArraySet.getMarkers().get(markerIndex)).getLabel();
                 }
                 else {
                     continue;
                 }

                 if (markerName == null) {
//                        writer.write("\n");
                     continue;
                 }

                 writer.write(markerName + ":" + markerIndex + "\t");
//                    keyIndex++;
//                    HashMap entryMap = (HashMap) curEntry.getValue();
                 HashMap entryMap = (HashMap) map.get(new Integer(markerIndex));
                 if (entryMap == null) {
                     writer.write("\n");
                     continue;
                 }
                 Set miSet = entryMap.entrySet();
                 Iterator miSetIt = miSet.iterator();
                 while (miSetIt.hasNext()) {
                     Map.Entry miEntry = (Map.Entry) miSetIt.next();
                     double miVal = Double.parseDouble(miEntry.getValue().
                         toString());
//                    if (miVal >= 0.0) {
                     if (miVal > miThresh) {
                         writer.write(miEntry.getKey().toString() + "\t" +
                                      miEntry.getValue().toString() + "\t");
                     }
                 }
                 writer.write("\n");
             }
             writer.close();
         }
         catch (Exception e) {
             e.printStackTrace();
         }

     }

     public void print(DSMicroarraySet mArraySet, File writeFile, Map map,
                       double miThresh) {
         try {
             FileWriter writer = new FileWriter(writeFile);

             Set entrySet = map.entrySet();
             Iterator entrySetIt = entrySet.iterator();
             int keyIndex = 0;
             while (entrySetIt.hasNext()) {
                 Map.Entry curEntry = (Map.Entry) entrySetIt.next();
                 int markerIndex = Integer.parseInt(curEntry.getKey().toString());
//                String markerName = mArraySet.getMarkerLabel(markerIndex);
                 String markerAccession = ((DSGeneMarker) mArraySet.getMarkers().get(markerIndex)).getLabel();
                 writer.write(markerAccession + ":" + markerIndex + "\t");
                 keyIndex++;
                 HashMap entryMap = (HashMap) curEntry.getValue();
                 Set miSet = entryMap.entrySet();
                 Iterator miSetIt = miSet.iterator();
                 while (miSetIt.hasNext()) {
                     Map.Entry miEntry = (Map.Entry) miSetIt.next();
                     double miVal = Double.parseDouble(miEntry.getValue().
                         toString());
//                    if (miVal >= 0.0) {
                     if (miVal > miThresh) {
                         writer.write(miEntry.getKey().toString() + "\t" +
                                      miEntry.getValue().toString() + "\t");
                     }
                 }
                 writer.write("\n");
             }
             writer.close();
         }
         catch (Exception e) {
             e.printStackTrace();
         }

     }

     public void print(HashMap geneIdMap, File writeFile) {
         try {
             FileWriter writer = new FileWriter(writeFile);

             Set entrySet = geneRows.entrySet();
             Iterator entrySetIt = entrySet.iterator();
             int keyIndex = 0;
             while (entrySetIt.hasNext()) {
                 Map.Entry curEntry = (Map.Entry) entrySetIt.next();
                 int markerIndex = Integer.parseInt(curEntry.getKey().toString());
                 String markerName = (String) geneIdMap.get(new Integer(
                     markerIndex));
                 writer.write(markerName + ":" + markerIndex + "\t");
                 keyIndex++;
                 HashMap entryMap = (HashMap) curEntry.getValue();
                 Set miSet = entryMap.entrySet();
                 Iterator miSetIt = miSet.iterator();
                 while (miSetIt.hasNext()) {
                     Map.Entry miEntry = (Map.Entry) miSetIt.next();
                     double miVal = Double.parseDouble(miEntry.getValue().
                         toString());
                     if (miVal > 0.0) {
                         writer.write(miEntry.getKey().toString() + "\t" +
                                      miEntry.getValue().toString() + "\t");
                     }
                 }
                 writer.write("\n");
             }
             writer.close();
         }
         catch (Exception e) {
             e.printStackTrace();
         }

     }

}
