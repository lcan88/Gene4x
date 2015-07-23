package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class AdjacencyMatrix implements IAdjacencyMatrix, Serializable {
    
    static Log log = LogFactory.getLog(AdjacencyMatrix.class);
    
    static public boolean MIlibraryLoaded = false;
    static final public int HIGH = 1;
    static final public int LOW = 2;
    static final public int BOTH = 3;
    protected HashMap<Integer, HashMap<Integer, Float>> geneRows = new HashMap<Integer, HashMap<Integer, Float>>();
    protected HashMap geneInteractionRows = new HashMap();
    protected HashMap idToGeneMapper = new HashMap();
    protected HashMap<String, Integer> snToGeneMapper = new HashMap();
    
    protected Parameter parms = null;
    protected int[] histogram = new int[1024];
    protected DSMicroarraySet<DSMicroarray> maSet = null;
    
    private boolean bMI = false;
    
    private String adjName;
    
    private String interactionText = "";
    
    private int adjSource = 0;
    static public int fromGeneNetworkPanelNotTakenCareOf = 1;
    static public int fromGeneNetworkPanelTakenGoodCareOf = 2;
    static public int fromBindPanel = 3;
    
    static protected final double edgeScale = 1024.0 / 0.15;
    
    HashMap keyMapping = new HashMap();
    String[] keyMapArray = new String[7000];
    
    public AdjacencyMatrix() {
        super();
    }
    
    public AdjacencyMatrix(HashMap geneRows) {
        super();
        this.geneRows = (HashMap) geneRows.clone();
    }
    
    public void printGene(DSMicroarraySet mArraySet, File writeFile, int geneId) {
        printGene(mArraySet, writeFile, geneRows, 0.0, geneId);
    }
    
    public void print(DSMicroarraySet mArraySet, File writeFile) {
        print(mArraySet, writeFile, 0.0);
    }
    
    public void print(DSMicroarraySet mArraySet, File writeFile, double miThresh) {
        print(mArraySet, writeFile, geneRows, miThresh);
    }
    
    public void print(DSMicroarraySet mArraySet, File writeFile, double miThresh, int startIndex, int endIndex) {
        print(mArraySet, writeFile, geneRows, miThresh, startIndex, endIndex);
    }
    
    public void printGene(DSMicroarraySet<DSMicroarray> mArraySet, File writeFile, Map map, double miThresh, int markerIndex) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            
            HashMap geneRow = (HashMap) map.get(new Integer(markerIndex));
            String markerName = mArraySet.get(markerIndex).getLabel();
            writer.write(markerName + ":" + markerIndex + "\t");
            
            Set miSet = geneRow.entrySet();
            Iterator miSetIt = miSet.iterator();
            while (miSetIt.hasNext()) {
                Map.Entry miEntry = (Map.Entry) miSetIt.next();
                double miVal = Double.parseDouble(miEntry.getValue().toString());
                if (miVal > miThresh) {
                    writer.write(miEntry.getKey().toString() + "\t" + miEntry.getValue().toString() + "\t");
                }                
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void print(DSMicroarraySet<DSMicroarray> mArraySet, File writeFile, Map map, double miThresh, int startIndex, int endIndex) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            for (int geneRowCtr = startIndex; geneRowCtr < endIndex; geneRowCtr++) {
                int markerIndex = geneRowCtr;
                String markerName = null;
                if (markerIndex < mArraySet.size()) {
                    markerName = mArraySet.get(markerIndex).getLabel();
                } else {
                    continue;
                }                
                if (markerName == null) {
                    continue;
                }                
                writer.write(markerName + ":" + markerIndex + "\t");
                HashMap entryMap = (HashMap) map.get(new Integer(markerIndex));
                if (entryMap == null) {
                    writer.write("\n");
                    continue;
                }
                Set miSet = entryMap.entrySet();
                Iterator miSetIt = miSet.iterator();
                while (miSetIt.hasNext()) {
                    Map.Entry miEntry = (Map.Entry) miSetIt.next();
                    double miVal = Double.parseDouble(miEntry.getValue().toString());
                    if (miVal > miThresh) {
                        writer.write(miEntry.getKey().toString() + "\t" + miEntry.getValue().toString() + "\t");
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void print(DSMicroarraySet<DSMicroarray> mArraySet, File writeFile, Map map, double miThresh) {
        try {
            FileWriter writer = new FileWriter(writeFile);
            Set entrySet = map.entrySet();
            Iterator entrySetIt = entrySet.iterator();
            int keyIndex = 0;
            while (entrySetIt.hasNext()) {
                Map.Entry curEntry = (Map.Entry) entrySetIt.next();
                int markerIndex = Integer.parseInt(curEntry.getKey().toString());
                String markerAccession = mArraySet.get(markerIndex).getLabel();
                writer.write(markerAccession + ":" + markerIndex + "\t");
                keyIndex++;
                HashMap entryMap = (HashMap) curEntry.getValue();
                Set miSet = entryMap.entrySet();
                Iterator miSetIt = miSet.iterator();
                while (miSetIt.hasNext()) {
                    Map.Entry miEntry = (Map.Entry) miSetIt.next();
                    double miVal = Double.parseDouble(miEntry.getValue().toString());
                    if (miVal > miThresh) {
                        writer.write(miEntry.getKey().toString() + "\t" + miEntry.getValue().toString() + "\t");
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
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
                String markerName = (String) geneIdMap.get(new Integer(markerIndex));
                writer.write(markerName + ":" + markerIndex + "\t");
                keyIndex++;
                HashMap entryMap = (HashMap) curEntry.getValue();
                Set miSet = entryMap.entrySet();
                Iterator miSetIt = miSet.iterator();
                while (miSetIt.hasNext()) {
                    Map.Entry miEntry = (Map.Entry) miSetIt.next();
                    double miVal = Double.parseDouble(miEntry.getValue().toString());
                    if (miVal > 0.0) {
                        writer.write(miEntry.getKey().toString() + "\t" + miEntry.getValue().toString() + "\t");
                    }
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public HashMap getGeneRows() {
        return this.geneRows;
    }
    
    public HashMap getKeyMapping() {
        return keyMapping;
    }
    
    /**
     * returns the strength of the edge between geneId1 and geneId2 (0.0 == no edge)
     *
     * @param geneId1 int
     * @param geneId2 int
     * @return float
     */
    public float get(int geneId1, int geneId2) {
        float maxValue = 0;
        geneId1 = getMappedId(geneId1);
        if (geneId1 >= 0) {
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row != null) {
                geneId2 = getMappedId(geneId2);
                if (geneId2 >= 0) {
                    Float f = (Float) row.get(new Integer(geneId2));
                    if (f != null) {
                        maxValue = f.floatValue();
                    }
                }
            }
        }
        return maxValue;
    }
    
    public HashMap getInteractionMap() {
        return this.geneInteractionRows;
    }
    
    public HashMap getInteraction(int geneId) {
        HashMap map = new HashMap();
        DSGeneMarker gm = maSet.getMarkers().get(geneId);
        ArrayList markers = (ArrayList) locusLinkMap.get(new Integer(gm.getGeneId()));
        if (markers != null) {
            System.out.println("markers != null");
            for (Iterator iter = markers.iterator(); iter.hasNext();) {
                DSGeneMarker item = (DSGeneMarker) iter.next();
                HashMap m = (HashMap) this.geneInteractionRows.get(new Integer(item.getSerial()));
                if (m != null) {
                    map.putAll(m);
                }
            }
            return map;
        } else {
            return (HashMap) this.geneInteractionRows.get(new Integer(geneId));
        }
    }
    
    /**
     * Returns a map with all the edges to geneId
     *
     * @param geneId int
     * @return HashMap
     */
    public HashMap get(int geneId) {
        try {
            geneId = getMappedId(geneId);
            if (geneId > 0) {
                return (HashMap) geneRows.get(new Integer(geneId));
            }
        } catch (Exception ex) {
            System.out.println("Oh oh");
        }
        return null;
    }
    
    /**
     * @deprecated should use the function in the subclass EvdAdjacencyMatrix
     */
    public void setMIflag(boolean flg) {
    }
    
    /**
     * @deprecated should use the function in the subclass EvdAdjacencyMatrix
     */
    public void addInteractionType2(int geneId1, int geneId2, double mi) {
    }
    
    public int getSource() {
        return this.adjSource;
    }
    
    /**
     * register where the adjacency matrix comes from:<BR>
     *
     * @param src int
     *            1= GeneNetworkPanel
     */
    public void setSource(int src) {
        this.adjSource = src;
    }
    
    public void addGeneRow(int geneId) {
        HashMap row = (HashMap) geneRows.get(new Integer(geneId));
        if (row == null) {
            row = new HashMap();
            geneRows.put(new Integer(geneId), row);
        }
    }
    
    public String getLabel() {
        return this.adjName;
    }
    
    public void setLabel(String name) {
        this.adjName = name;
    }
    
    /**
     * Adds and edge between geneId1 and geneId2
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param edge    float
     */
    public void add(int geneId1, int geneId2, float edge) {
        geneId1 = getMappedId(geneId1);
        geneId2 = getMappedId(geneId2);
        if ((geneId1 >= 0) && (geneId2 >= 0)) {
            int bin = Math.min(1023, (int) (edge * edgeScale));
            if (bin >= 0) {
                histogram[bin]++;
            }
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId1), row);
            }
            row.put(new Integer(geneId2), new Float(edge));
            row = (HashMap) geneRows.get(new Integer(geneId2));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId2), row);
            }
            row.put(new Integer(geneId1), new Float(edge));
        }
    }
    
    public void addDirectional(int geneId1, int geneId2, float edge) {
        geneId1 = getMappedId(geneId1);
        geneId2 = getMappedId(geneId2);
        if ((geneId1 >= 0) && (geneId2 >= 0)) {
            int bin = Math.min(1023, (int) (edge * edgeScale));
            if (bin >= 0) {
                histogram[bin]++;
            }
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId1), row);
            }
            row.put(new Integer(geneId2), new Float(edge));
        }
    }
    
    /**
     * exp method, not tested!!!
     *
     * @param geneId1     int
     * @param geneId2     int
     * @param interaction String
     */
    public void addDirectional(int geneId1, int geneId2, String interaction) {
        geneId1 = getMappedId(geneId1);
        geneId2 = getMappedId(geneId2);
        if ((geneId1 >= 0) && (geneId2 >= 0)) {
            HashMap row = (HashMap) geneRows.get(new Integer(geneId1));
            if (row == null) {
                row = new HashMap();
                geneRows.put(new Integer(geneId1), row);
            }
            row.put(new Integer(geneId2), interaction);
        }
    }
    
    public String getMarkerNameGW(int index) {
        if (keyMapping.get(new Integer(index)) == null) {
            return maSet.getMarkers().get(index).getShortName();
        } else {
            return (String) keyMapping.get(new Integer(index));
        }
    }
    
    public String getMarkerName(int index) {
        if (keyMapping.get(new Integer(index)) == null) {
            return maSet.getMarkers().get(index).getShortName();
        }
        return (String) keyMapping.get(new Integer(index));
    }
    
    boolean geneNames = false;
    public void readGeneNameMappings(File file, DSMicroarraySet<DSMicroarray> maSet) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int ctr = 0;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")){
                    if (ctr++ % 100 == 0) {
                        System.out.println("reading mapping " + ctr++);
                    }
                    String[] arrLine2 = line.split("\t");
                    String val0 = new String(arrLine2[0]);
                    String val1 = val0;
                    if (geneNames) {
                        int i = 0;
                        while (maSet.getMarkers().get(val1.trim()) == null){
                            if (AnnotationParser.geneNameMap.get(val0.trim()) != null){
                                val1 = AnnotationParser.geneNameMap.get(val0.trim()).get(i++);
                            }
                        }
                    }
                    DSGeneMarker marker = maSet.getMarkers().get(val1.trim());
                    if (marker != null){
                        Integer num = new Integer(maSet.getMarkers().get(val1.trim()).getSerial());
                        keyMapping.put(num, val1);
                    }
                }
                else if (line.startsWith(">GENE_NAMES"))
                    geneNames = true;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void readMappings(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int ctr = 0;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")){
                    if (ctr++ % 100 == 0) {
                        System.out.println("reading mapping " + ctr++);
                    }
                    String[] arrLine2 = line.split("\t");
                    String[] arrLineStart = arrLine2[0].split(":");
                    String val = new String(arrLineStart[0]);
                    Integer num = new Integer(arrLineStart[arrLineStart.length - 1]);
                    keyMapping.put(num, val);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     *
     */
    public void addMarkerName(int pos, String gname) {
        keyMapping.put(new Integer(pos), gname);
    }
    
    /**
     * if we don't specify the gene name, use the annotation from the Affy array<BR>
     * serial = the serial number of the gene on the chip<BR>
     *
     * @param serial int
     */
    public void addMarkerName(int serial) {
        String tmp = maSet.getMarkers().get(serial).toString();
        int s1 = tmp.indexOf("(") + 1;
        int s2 = tmp.indexOf(")");
        String genename = tmp.substring(s1, s2);
        keyMapping.put(new Integer(serial), genename);
    }
    
    public HashMap readNameToIndexMappings(File file) {
        HashMap mapping = new HashMap();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                if (st.countTokens() > 0) {
                    String obj = st.nextToken();
                    StringTokenizer st2 = new StringTokenizer(obj, ":");
                    if (st2.countTokens() > 1) {
                        String val = st2.nextToken();
                        Integer num = new Integer(st2.nextToken());
                        mapping.put(val, num);
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapping;
    }
    
    public void read(String name, float miThresh) {
        int connectionsInstantiated = 0;
        int connectionsIgnored = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(name));
            try {
                String line;
                int ctr = 0;
                while ((line = br.readLine()) != null) {
                    System.gc();
                    if (ctr++ % 100 == 0) {
                        System.out.println("Reading line " + ctr);
                    }
                    if (line.length() > 0 && line.charAt(0) != '-') {
                        StringTokenizer tr = new StringTokenizer(line, "\t:");
                        String geneAccess = new String(tr.nextToken());
                        String strGeneId1 = new String(tr.nextToken());
                        int geneId1 = getMappedId(Integer.parseInt(strGeneId1));
                        if (geneId1 >= 0) {
                            while (tr.hasMoreTokens()) {
                                String strGeneId2 = new String(tr.nextToken());
                                int geneId2 = getMappedId(Integer.parseInt(strGeneId2));
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
        resolveGeneCollision(maSet);
    }
    
    /**
     * read only the genes that are listed in the geneNames vector
     */
    public void read(String name, HashMap accessionsMap, Vector geneNames, float miThresh) {
        int connectionsInstantiated = 0;
        int connectionsIgnored = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(name));
            try {
                String line = br.readLine();
                int ctr = 0;
                while (br.ready()) {
                    if (ctr++ % 100 == 0) {
                        System.out.println("Reading line " + ctr);
                    }
                    if (line.length() > 0 && line.charAt(0) != '-') {
                        StringTokenizer tr = new StringTokenizer(line, "\t:");
                        String geneAccess = new String(tr.nextToken());
                        String strGeneId1 = new String(tr.nextToken());
                        int geneId1 = Integer.parseInt(strGeneId1);
                        String geneName = (String) accessionsMap.get(geneAccess);
                        if (geneName != null && geneNames.contains(geneName) && geneId1 != -1) {
                            geneId1 = getMappedId(geneId1);
                            if (geneId1 >= 0) {
                                while (tr.hasMoreTokens()) {
                                    String strGeneId2 = new String(tr.nextToken());
                                    String strMi = new String(tr.nextToken());
                                    float mi = Float.parseFloat(strMi);
                                    int geneId2 = getMappedId(Integer.parseInt(strGeneId2));
                                    if (geneId2 >= 0) {
                                        if (mi > miThresh) {
                                            if (geneId1 != geneId2) {
                                                connectionsInstantiated++;
                                                add(geneId1, geneId2, mi);
                                                add(geneId2, geneId1, mi);
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
                    }
                    line = br.readLine();
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
        resolveGeneCollision(maSet);
    }
    
    public int getMappedId(int geneId) {
        if (geneId >= 0) {
            DSGeneMarker gm = maSet.getMarkers().get(geneId);
            String sn = gm.getShortName();
            if (sn == null) {
                return geneId;
            }
            if (sn.compareToIgnoreCase("ExoBCL6") == 0) {
                geneId = -1;
            } else if (gm.getLabel().compareToIgnoreCase("1827_s_at") == 0) {
                geneId = gm.getSerial();
            } else if (gm.getLabel().compareToIgnoreCase("1936_s_at") == 0) {
                geneId = gm.getSerial();
            } else {
                if (sn.compareToIgnoreCase("MYC") == 0) {
                    int xxx = 1;
                }
                if (gm.getLabel().compareToIgnoreCase("1936_s_at") == 0) {
                    sn = "MYC";
                    try {
                        gm = maSet.getMarkers().get("1973_s_at");
                    } catch (Exception ex) {
                        gm = null;
                    }
                }
                if (gm != null) {
                    Integer prevId = (Integer) idToGeneMapper.get(gm.getLabel());
                    if (prevId != null) {
                        geneId = prevId.intValue();
                    } else {
                        prevId = snToGeneMapper.get(sn);
                        if (prevId != null) {
                            snToGeneMapper.put(sn, prevId);
                            idToGeneMapper.put(gm.getLabel(), prevId);
                            geneId = prevId.intValue();
                        } else {
                            snToGeneMapper.put(sn, new Integer(geneId));
                            idToGeneMapper.put(gm.getLabel(), new Integer(geneId));
                        }
                    }
                }
            }
        }
        return geneId;
    }
    
    public void readGeneNames(String name, DSMicroarraySet<DSMicroarray> microarraySet) {
        maSet = microarraySet;
        int markerNo = microarraySet.size();
        BufferedReader br = null;
        try {
            readGeneNameMappings(new File(name), microarraySet);
            br = new BufferedReader(new FileReader(name));
            try {
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (line.length() > 0 && line.charAt(0) != '-' && !line.startsWith(">")) {
                        StringTokenizer tr = new StringTokenizer(line, "\t");
                        String geneAccess0 = new String(tr.nextToken());    
                        String geneAccess1 = geneAccess0;
                        if (geneNames) {
                            int i = 0;
                            while (maSet.getMarkers().get(geneAccess1.trim()) == null){
                                geneAccess1 = AnnotationParser.geneNameMap.get(geneAccess0.trim()).get(i++);
                            }
                        }
                        int geneId1 = maSet.getMarkers().get(geneAccess1.trim()).getSerial();
                        if (geneId1 > -1) {
                            geneId1 = getMappedId(geneId1);
                            if (geneId1 >= 0) {
                                while (tr.hasMoreTokens()) {
                                    String gene20 = new String(tr.nextToken());
                                    String gene21 = gene20;
                                    if (geneNames) {
                                        int i = 0;
                                        while (maSet.getMarkers().get(gene21.trim()) == null){
                                            gene21 = AnnotationParser.geneNameMap.get(gene20.trim()).get(i++);
                                        }
                                    }
                                    int geneId2 = maSet.getMarkers().get(gene21.trim()).getSerial();
                                    if (geneId2 > -1) {
                                        float mi = Float.parseFloat(tr.nextToken());
                                        geneId2 = getMappedId(geneId2);
                                        if (geneId2 >= 0) {
                                            if (geneId1 != geneId2) {
                                                add(geneId1, geneId2, mi);
                                                add(geneId2, geneId1, mi);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Exception: " + ex);
            } catch (IOException ex) {
                System.out.println("Exception: " + ex);
            }
        } catch (FileNotFoundException ex3) {
        }
        resolveGeneCollision(maSet);
    }
    
    public void read(String name, DSMicroarraySet<DSMicroarray> microarraySet) {
        maSet = microarraySet;
        int markerNo = microarraySet.size();
        BufferedReader br = null;
        try {
            readMappings(new File(name));
            br = new BufferedReader(new FileReader(name));
            try {
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (line.length() > 0 && line.charAt(0) != '-' && !line.startsWith(">")) {
                        StringTokenizer tr = new StringTokenizer(line, "\t: ");
                        String geneAccess = new String(tr.nextToken());
                        int geneId1 = Integer.parseInt(tr.nextToken());
                        String geneName = (String) keyMapping.get(new Integer(geneId1));
                        geneId1 = -1;
                        if (geneName != null) {
                            DSGeneMarker m = microarraySet.getMarkers().get(geneName);
                            if (m != null) {
                                geneId1 = m.getSerial();
                            } else {
                                tr.nextToken();
                            }
                        }
                        if (geneId1 > -1) {
                            geneId1 = getMappedId(geneId1);
                            if (geneId1 >= 0) {
                                while (tr.hasMoreTokens()) {
                                    int geneId2 = Integer.parseInt(tr.nextToken());
                                    String geneName2 = (String) keyMapping.get(new Integer(geneId2));
                                    if (geneName2 == null){
                                        geneName2 = microarraySet.getMarkers().get(geneId2).getLabel();
                                    }
                                    geneId2 = -1;
                                    if (geneName2 != null) {
                                        DSGeneMarker m = microarraySet.getMarkers().get(geneName2);
                                        if (m != null) {
                                            geneId2 = m.getSerial();
                                        } else {
                                            tr.nextToken();
                                        }
                                    }
                                    if (geneId2 > -1) {
                                        float mi = Float.parseFloat(tr.nextToken());
                                        geneId2 = getMappedId(geneId2);
                                        if (geneId2 >= 0) {
                                            if (geneId1 != geneId2) {
                                                add(geneId1, geneId2, mi);
                                                add(geneId2, geneId1, mi);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Exception: " + ex);
            } catch (IOException ex) {
                System.out.println("Exception: " + ex);
            }
        } catch (FileNotFoundException ex3) {
        }
        resolveGeneCollision(maSet);
    }
    
    public void clear() {
        geneRows = new HashMap();
    }
    
    public int size() {
        return geneRows.size();
    }
    
    public Set getKeys() {
        return geneRows.keySet();
    }
    
    public Object getValue(Object key) {
        return geneRows.get(key);
    }
    
    public int getEdgeNo(int bin) {
        int count = 0;
        for (int i = bin + 1; i < 1024; i++) {
            count += histogram[i];
        }
        return count;
    }
    
    public double getThreshold(int bin) {
        double threshold = (double) bin / edgeScale;
        return threshold;
    }
    
    public void setParms(Parameter _parms) {
        parms = _parms;
    }
    
    public int getConnectionNo() {
        int connectionNo = 0;
        Iterator valuesIt = geneRows.values().iterator();
        while (valuesIt.hasNext()) {
            HashMap geneRow = (HashMap) valuesIt.next();
            connectionNo += geneRow.size();
        }
        return connectionNo;
    }
    
    public int getConnectionNo(int geneId, double threshold) {
        geneId = getMappedId(geneId);
        if (geneId < 0) {
            return 0;
        }
        int connectionNo = 0;
        HashMap row = (HashMap) geneRows.get(new Integer(geneId));
        if (row != null) {
            for (Iterator iter = row.values().iterator(); iter.hasNext();) {
                Float item = (Float) iter.next();
                if (item.floatValue() > threshold) {
                    connectionNo++;
                }
            }
        } else {
            connectionNo = 0;
        }
        return connectionNo;
    }
    
    public void setMicroarraySet(DSMicroarraySet microarraySet) {
        maSet = microarraySet;
    }
    
    public DSMicroarraySet getMicroarraySet() {
        return maSet;
    }
    
    public void clean(DSMicroarraySet microarraySet, double threshold, double eps, int startId, int endId) {
        int edgesRemoved = 0;
        int edgesRetained = 0;
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        Set rowIds = geneRows.keySet();
        System.out.println("Reinstantiating negative MIs");
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB < 0) {
                    this.add(geneAId, geneBId, -miAB);
                }
                if (Math.abs(miAB) < threshold) {
                    this.add(geneAId, geneBId, -Math.abs(miAB));
                }
            }
        }
        
        for (int geneACtr = startId; geneACtr < endId; geneACtr++) {
            Integer geneAKey = new Integer(geneACtr);
            System.out.println("Processing gene row " + geneAKey);
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            if (geneRow == null) {
                System.out.println("Gene row " + geneAKey + " null");
                continue;
            }
            
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB > 0) {
                    boolean foundAB = false;
                    boolean foundBA = false;
                    for (Iterator geneC = colIds.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundAB = true;
                                break;
                            }
                        }
                    }
                    HashMap geneRowB = (HashMap) geneRows.get(geneBKey);
                    Set colIdsB = geneRowB.keySet();
                    for (Iterator geneC = colIdsB.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundBA = true;
                                break;
                            }
                        }
                    }
                    if (foundAB || foundBA) {
                        edgesRemoved++;
                        this.add(geneAId, geneBId, -miAB);
                    } else {
                        edgesRetained++;
                    }
                }
            }
        }
        System.out.println("Edges removed " + edgesRemoved);
        System.out.println("Edges retained " + edgesRetained);
        System.out.println("Edges processed " + (edgesRemoved + edgesRetained));
    }
    
    public void clean_new(DSMicroarraySet microarraySet, double threshold, double eps) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        Set rowIds = geneRows.keySet();
        
        System.out.println("Reinstantiating negative MIs");
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB < 0) {
                    this.add(geneAId, geneBId, -miAB);
                }
                if (Math.abs(miAB) < threshold) {
                    this.add(geneAId, geneBId, -Math.abs(miAB));
                }
            }
        }
        
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            System.out.println("Processing gene row " + geneAKey);
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB > 0) {
                    boolean foundAB = false;
                    boolean foundBA = false;
                    for (Iterator geneC = colIds.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundAB = true;
                                break;
                            }
                        }
                    }
                    HashMap geneRowB = (HashMap) geneRows.get(geneBKey);
                    Set colIdsB = geneRowB.keySet();
                    for (Iterator geneC = colIdsB.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundBA = true;
                                break;
                            }
                        }
                    }
                    if (foundAB || foundBA) {
                        this.add(geneAId, geneBId, -miAB);
                    }
                }
            }
        }
    }
    
    /**
     * the clean() function called from GeneNetworkPanel -> CreateNetwork()
     * this function is GW compatible
     *
     * @param microarraySet DSMicroarraySet
     * @param threshold     double
     * @param eps           double
     */
    public void clean(DSMicroarraySet microarraySet, double threshold, double eps) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        Set rowIds = geneRows.keySet();
        
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB < 0) {
                    this.add(geneAId, geneBId, -miAB);
                }
                if (Math.abs(miAB) < threshold) {
                    this.add(geneAId, geneBId, -Math.abs(miAB));
                }
            }
        }
        
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float miAB = ((Float) geneRow.get(geneBKey)).floatValue();
                if (miAB > 0) {
                    boolean foundAB = false;
                    boolean foundBA = false;
                    for (Iterator geneC = colIds.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundAB = true;
                                break;
                            }
                        }
                    }
                    
                    HashMap geneRowB = (HashMap) geneRows.get(geneBKey);
                    Set colIdsB = geneRowB.keySet();
                    for (Iterator geneC = colIdsB.iterator(); geneC.hasNext();) {
                        Integer geneCKey = (Integer) geneC.next();
                        if (geneCKey != geneBKey) {
                            int geneCId = geneCKey.intValue();
                            float miAC = Math.abs(this.get(geneAId, geneCId));
                            float miBC = Math.abs(this.get(geneBId, geneCId));
                            if ((miAB <= (miAC - (miAC * eps))) && (miAB <= (miBC - (miBC * eps)))) {
                                foundBA = true;
                                break;
                            }
                        }
                    }
                    if (foundAB || foundBA) {
                        this.add(geneAId, geneBId, -miAB);
                    }
                }
            }
        }
    }
    
    /**
     * This method parses the genes and adds them into a look up table where identical genes are resolved
     */
    HashMap locusLinkMap = new HashMap();
    
    public void resolveGeneCollision(DSMicroarraySet microarraySet) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
    }
    
    /**
     * cleanFirstNeighbors
     *
     * @param gm0 IGenericMarker
     */
    public void cleanFirstNeighbors(DSMicroarraySet microarraySet, DSGeneMarker gm0) {
        if (microarraySet != null) {
            maSet = microarraySet;
        }
        HashSet neighbors = new HashSet();
        HashSet completed = new HashSet();
        HashSet allGenes = new HashSet();
        int uniId = gm0.getUnigene().getUnigeneId();
        Set rowIds = geneRows.keySet();
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            int uniId1 = maSet.getMarkers().get(geneAId).getUnigene().getUnigeneId();
            if (uniId1 == uniId) {
                neighbors.add(new Integer(geneAId));
            }
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float mi = this.get(geneAId, geneBId);
                if (mi < 0) {
                    this.add(geneAId, geneBId, 0);
                }
            }
        }
        for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
            Integer geneAKey = (Integer) geneA.next();
            int geneAId = geneAKey.intValue();
            if (!neighbors.contains(geneAKey)) {
                allGenes.add(new Integer(geneAId));
            }
            HashMap geneRow = (HashMap) geneRows.get(geneAKey);
            Set colIds = geneRow.keySet();
            for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                Integer geneBKey = (Integer) geneB.next();
                int geneBId = geneBKey.intValue();
                float mi = this.get(geneAId, geneBId);
                log.debug("Setting interaction between "+geneAId+" and "+geneBId+" to "+(-Math.abs(mi)));
                this.add(geneAId, geneBId, -Math.abs(mi));
            }
        }
        ArrayList neighborSet = new ArrayList();
        int neighborId = 0;
        neighborSet.add(neighborId, neighbors);
        boolean decrease = true;
        while (!allGenes.isEmpty() && decrease) {
            decrease = false;
            HashSet newNeighbors = new HashSet();
            for (Iterator geneA = rowIds.iterator(); geneA.hasNext();) {
                Integer geneAKey = (Integer) geneA.next();
                if (allGenes.contains(geneAKey)) {
                    int geneAId = geneAKey.intValue();
                    DSGeneMarker gmA = maSet.getMarkers().get(geneAId);
                    if (!completed.contains(new Integer(gmA.getUnigene().getUnigeneId()))) {
                        HashMap geneRow = (HashMap) geneRows.get(geneAKey);
                        float maxMI = 0;
                        int maxId = -1;
                        Set colIds = geneRow.keySet();
                        for (Iterator geneB = colIds.iterator(); geneB.hasNext();) {
                            Integer geneBKey = (Integer) geneB.next();
                            int geneBId = geneBKey.intValue();
                            if (neighbors.contains(geneBKey)) {
                                float mi = Math.abs(get(geneAId, geneBId));
                                if (mi > maxMI) {
                                    maxMI = mi;
                                    maxId = geneBId;
                                }
                            }
                        }
                        if (maxId != -1) {
                            decrease = true;
                            newNeighbors.add(geneAKey);
                            add(geneAId, maxId, maxMI);
                            completed.add(new Integer(maSet.getMarkers().get(geneAId).getUnigene().getUnigeneId()));
                        }
                    }
                }
            }
            allGenes.removeAll(newNeighbors);
            neighborSet.add(newNeighbors);
            neighbors = newNeighbors;
        }
    }
    
    public void printGeneNamesAndAccessions(DSMicroarraySet<DSMicroarray> mArraySet, File writeFile) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        try {
            FileWriter writer = new FileWriter(writeFile);
            
            Set geneRowsEntrySet = geneRows.entrySet();
            Iterator geneRowsIt = geneRowsEntrySet.iterator();
            while (geneRowsIt.hasNext()) {
                Entry geneRowEntry = (Entry) geneRowsIt.next();
                int geneRowKey = ((Integer) geneRowEntry.getKey()).intValue();
                String geneRowAccession = mArraySet.get(geneRowKey).getLabel();
                String geneName = mArraySet.getMarkers().get(geneRowKey).getDescription();
                writer.write(geneRowKey + ":" + geneRowAccession + ":" + geneName);
                
                HashMap geneRowValues = (HashMap) geneRowEntry.getValue();
                Iterator targetsIterator = geneRowValues.entrySet().iterator();
                while (targetsIterator.hasNext()) {
                    Entry targetEntry = (Entry) targetsIterator.next();
                    int targetId = ((Integer) targetEntry.getKey()).intValue();
                    float targetMi = ((Float) targetEntry.getValue()).floatValue();
                    String targetAccession = mArraySet.get(targetId).getLabel();
                    String targetName = mArraySet.get(targetId).getLabel();
                    
                    String strMi = nf.format(targetMi);
                    writer.write("\t" + targetId + ":" + targetAccession + ":" + targetName + ":" + strMi);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void printTargetsOfGene(DSMicroarraySet<DSMicroarray> mArraySet, File writeFile, String centerAccession) {
        try {
            DSGeneMarker m = mArraySet.getMarkers().get(centerAccession);
            if (m != null) {
                FileWriter writer = new FileWriter(writeFile);
                int markerIndex = m.getSerial();
                HashMap geneRow = (HashMap) geneRows.get(new Integer(markerIndex));
                if (geneRow == null) {
                    System.out.println(centerAccession + " not found");
                    return;
                }
                writer.write(centerAccession + "\n");
                Set geneTargets = geneRow.keySet();
                Iterator targetsIt = geneTargets.iterator();
                while (targetsIt.hasNext()) {
                    int targetId = ((Integer) targetsIt.next()).intValue();
                    String targetAccession = mArraySet.get(targetId).getLabel();
                    writer.write(targetAccession + "\n");
                }
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void mergeDuplicateProbesToFile(DSMicroarraySet maSet, String filename){
        HashMap<String, HashMap<String, Float>> adjMat = new HashMap<String, HashMap<String, Float>>();
        for (Integer key : geneRows.keySet()){
            DSGeneMarker gm = (DSGeneMarker)maSet.getMarkers().get(key.intValue());
            String sn = gm.getShortName();
            if (sn != null && !sn.equals("---")){
                if (!adjMat.keySet().contains(sn.trim())){
                    adjMat.put(sn.trim(), new HashMap<String, Float>());
                }
                HashMap<Integer, Float> row = geneRows.get(key);
                for (Integer key2 : row.keySet()){
                    DSGeneMarker gm2 = (DSGeneMarker)maSet.getMarkers().get(key2.intValue());
                    String sn2 = gm2.getShortName();
                    if (sn2 != null && !sn2.equals("---")){
                        if (!adjMat.get(sn.trim()).containsKey(sn2.trim())){
                            adjMat.get(sn.trim()).put(sn2.trim(), row.get(key2));
                        }
                        else {
                            float prevMI = adjMat.get(sn.trim()).get(sn2.trim());
                            if (prevMI < row.get(key2)){
                                adjMat.get(sn.trim()).put(sn2.trim(), row.get(key2));
                            }
                        }
                    }
                }
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".fused.adj"));
            bw.write(">GENE_NAMES\n");
            for (String key : adjMat.keySet()){
                bw.write(key);
                for (String key2 : adjMat.get(key).keySet()){
                    bw.write("\t" + key2 + "\t" + adjMat.get(key).get(key2).toString().trim());
                }
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        }
        catch (IOException ioe){}
    }
}