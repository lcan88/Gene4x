package org.geworkbench.util.network;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * This class is used to represent a graph as a set of edges through a sparse matrix EM. If the entry EM(a, b) is not empty
 * then an edge exists between nodes a and b. This matrix structure can be used to implement several graph algorithms
 * <p/>
 * Note that while the matrix could be implemented as a triangular one to save space, as edge(a,b) == edge(b,a),
 * it is better to have two entries for each edge so that one can find all edges of a node by just exploring one
 * column instead of having to look in all the columns.
 *
 * @author not attributable
 * @version 1.0
 */

public class EdgeMatrix {
    public class MatrixCell {
        public double pValue = 1.0;

        public MatrixCell(double pv) {
            pValue = pv;
        }

        public boolean isActive() {
            return (pValue >= 0);
        }

        public double getPValue() {
            return Math.abs(pValue);
        }

        public void setPValue(double pv) {
            pValue = pv;
        }

        public void setActive(boolean status) {
            if (status) {
                pValue = Math.abs(pValue);
            } else {
                pValue = -1.0 * Math.abs(pValue);
            }
        }
    }

    HashMap edges = new HashMap();

    public EdgeMatrix() {
    }

    /**
     * Adds an edge to the network if one is not already present. This is the private
     * version of the method that assumes that a.getSerial() > b.getSerial()
     *
     * @param a
     * @param b
     * @param anEdge
     * @return
     */
    public boolean setEdge(DSGeneMarker aa, DSGeneMarker bb, GeneNetworkEdge edge) {
        Integer aInt = new Integer(aa.getSerial());
        Integer bInt = new Integer(bb.getSerial());
        MatrixCell cell = new MatrixCell(edge.getThreshold());
        HashMap map = (HashMap) edges.get(aInt);
        boolean result = false;
        // Insert the first entry edge(a,b)
        if (map != null) {
            MatrixCell prevNode = (MatrixCell) map.get(bInt);
            if (prevNode != null) {
                result = false;
            } else {
                map.put(bInt, cell);
                result = true;
            }
        } else {
            map = new HashMap();
            edges.put(aInt, map);
            map.put(bInt, cell);
            result = true;
        }
        if (result) {
            map = (HashMap) edges.get(bInt);
            // Insert the second entry edge(b,a)
            if (map != null) {
                // no need to check because it should be symmetric
                map.put(aInt, cell);
            } else {
                map = new HashMap();
                edges.put(bInt, map);
                map.put(aInt, cell);
            }
        }
        return result;
    }

    /**
     * This method returns the edge between a and b, it it exists.
     *
     * @param a
     * @param b
     * @return null if the edge does not exist, an IGeneNetworkEdge otherwise
     */
    public MatrixCell getEdge(DSGeneMarker a, DSGeneMarker b) {
        HashMap map = (HashMap) edges.get(new Integer(a.getSerial()));
        if (map != null) {
            return (MatrixCell) map.get(new Integer(b.getSerial()));
        } else {
            return null;
        }
    }

    /**
     * This method returns the edge between a and b, it it exists.
     *
     * @param a an Integer with the serial id of the first gene
     * @param b an Integer with the serial id of the second gene
     * @return null if the edge does not exist, an IGeneNetworkEdge otherwise
     */
    public MatrixCell getEdge(Integer a, Integer b) {
        HashMap map = (HashMap) edges.get(a);
        if (map != null) {
            return (MatrixCell) map.get(b);
        } else {
            return null;
        }
    }

    /**
     * This method serializes a column of the HashTable
     *
     * @param aNode the node to serialize
     */
    public void serializeNode(DSGeneMarker aNode) {
        File directory = new File("./temp");
        boolean success = directory.mkdir();
        Integer node1 = new Integer(aNode.getSerial());
        HashMap map = (HashMap) edges.get(node1);
        if (map != null) {
            try {
                String name = "./temp/" + aNode.getLabel().replace('/', '_').replace('\\', '_');
                FileOutputStream f = new FileOutputStream(name);
                ObjectOutput s = new ObjectOutputStream(f);
                Set nodes = map.keySet();
                Iterator it = nodes.iterator();
                while (it.hasNext()) {
                    Integer node2 = (Integer) it.next();
                    MatrixCell cell = (MatrixCell) map.get(node2);
                    s.writeInt(node1.intValue());
                    s.writeInt(node2.intValue());
                    s.writeDouble(cell.getPValue());
                }
                s.flush();
            } catch (IOException ex) {
                System.err.println("Error: " + ex);
            }
        }
    }

    /**
     * Serializes the whole edge matrix
     */
    public void serialize(DSMicroarraySet<DSMicroarray> microarraySet, int cpuId, int cpuNo) {
        Set keys = edges.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            int key = ((Integer) it.next()).intValue();
            if ((key % cpuNo) == cpuId) {
                DSGeneMarker gm = microarraySet.getMarkers().get(key);
                serializeNode(gm);
            }
        }
    }

    public void deserialize(DSMicroarraySet<DSMicroarray> microarraySet, JProgressBar bar) {

        // We should probably reset and clear the matrix
        /*
        int microarrayNo = microarraySet.size();
        int markerNo     = microarraySet.size();
        for(int i = 0; i < markerNo; i++) {
            IGenericMarker gm = microarraySet.getGenericMarker(i);
            String name = "./tmp/" + gm.getAccession().replace('/','_').replace('\\','_');
            try {
                FileInputStream f = new FileInputStream(name);
                ObjectInput s = new ObjectInputStream(f);

                try {
                    HashMap map = (HashMap) s.readObject();
                    Set nodes = map.keySet();
                    HashMap newMap = new HashMap();
                    Iterator it = nodes.iterator();
                    while (it.hasNext()) {
                        IGenericMarker  gm1  = (IGenericMarker) it.next();
                        GeneNetworkEdge edge = (GeneNetworkEdge) map.get(gm1);
                        newMap.put(new Integer(gm1.getSerial()), new MatrixCell(edge.getPValue()));
                    }
                    edges.put(new Integer(gm.getSerial()), newMap);
                    s.close();
                    serializeNode(gm);
                } catch (IOException ex1) {
                    System.err.println("Error 1: " + ex1);
                } catch (ClassNotFoundException ex1) {
                    System.err.println("Error 2: " + ex1);
                }
            } catch (IOException ex) {
                System.err.println("Error 0: " + ex);
            }
        }
            */
        int markerNo = microarraySet.size();
        if (bar != null) {
            bar.setForeground(Color.red);
            bar.setStringPainted(true);
            bar.setMaximum(markerNo);
            bar.setValue(0);
        }
        for (int i = 0; i < markerNo; i++) {
            if (bar != null) {
                bar.setValue(i);
            }
            DSGeneMarker gm = microarraySet.getMarkers().get(i);
            String name = "./temp/" + gm.getLabel().replace('/', '_').replace('\\', '_');
            try {
                FileInputStream f = new FileInputStream(name);
                ObjectInput s = new ObjectInputStream(f);
                try {
                    HashMap newMap = new HashMap();
                    try {
                        while (s.available() > 0) {
                            int n1 = s.readInt();
                            int n2 = s.readInt();
                            double p = s.readDouble();
                            MatrixCell cell = new MatrixCell(p);
                            if (n1 == gm.getSerial()) {
                                newMap.put(new Integer(n2), cell);
                            } else {
                                newMap.put(new Integer(n1), cell);
                            }
                        }
                    } catch (IOException ex2) {
                        System.out.println("Done: " + gm);
                    }
                    edges.put(new Integer(gm.getSerial()), newMap);
                    s.close();
                    //serializeNode(gm);
                } catch (IOException ex1) {
                    // System.err.println("Error 1: " + ex1);
                }
            } catch (IOException ex) {
                // System.err.println("Error 0: " + ex);
            }
        }
        bar.setForeground(Color.green);
    }

    public void setActive(DSGeneMarker a, DSGeneMarker b, boolean status) {
        MatrixCell edge = getEdge(a, b);
        if (edge != null) {
            edge.setActive(status);
            getEdge(b, a).setActive(status);
        }
    }

    public boolean remove(DSGeneMarker aa, DSGeneMarker bb) {
        boolean result = false;
        Integer aInt = new Integer(aa.getSerial());
        Integer bInt = new Integer(bb.getSerial());
        HashMap map = (HashMap) edges.get(aInt);
        if (map != null) {
            Object o = map.remove(bInt);
            if (o != null) {
                map = (HashMap) edges.get(bInt);
                map.remove(aInt);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public Collection getEdges(DSGeneMarker aa) {
        HashMap map = (HashMap) edges.get(new Integer(aa.getSerial()));
        if (map != null) {
            return map.values();
        }
        return null;
    }

    public Set getConnectedNodes(DSGeneMarker aa) {
        HashMap map = (HashMap) edges.get(new Integer(aa.getSerial()));
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    public void clear() {
        edges.clear();
    }

    public void reduce(DSMicroarraySet microarraySet, JProgressBar bar) {
        // This function finds all edges (a,b) that can be better modeled through a
        // third node c as ((a,c)(c,b)), because the likelihood of (a,c) and (c,b)
        // is higher than that of (a,b)
        Set keys = edges.keySet();
        Iterator nodes0 = keys.iterator();
        int count = keys.size();
        if (bar != null) {
            bar.setMaximum(count);
            bar.setForeground(Color.red);
            bar.setStringPainted(true);
            bar.setValue(0);
        }
        int iteration = 0;
        while (nodes0.hasNext()) {
            if (bar != null) {
                bar.setValue(iteration++);
            }
            Integer node0 = (Integer) nodes0.next();
            HashMap map = (HashMap) edges.get(node0);
            Set keys1 = map.keySet();
            Iterator nodes1 = keys1.iterator();
            int count1 = keys.size();
            while (nodes1.hasNext()) {
                count1--;
                Integer node1 = (Integer) nodes1.next();
                MatrixCell edge0 = (MatrixCell) map.get(node1);
                if (edge0.isActive()) {
                    // Now we loop on all edges different from edge0 to see if edge0 could go through
                    // another edge.
                    Iterator nodes2 = map.keySet().iterator();
                    while (nodes2.hasNext()) {
                        Integer node2 = (Integer) nodes2.next();
                        MatrixCell edge1 = (MatrixCell) map.get(node2);
                        if (edge1.isActive()) {
                            if (edge1 != edge0) {
                                // first the edge has to have better pValue than edge0
                                if (edge1.getPValue() < edge0.getPValue()) {
                                    // check if another edge exists that links edge0.id2 to edge1.id2
                                    MatrixCell edge3 = getEdge(node1, node2);
                                    if ((edge3 != null) && (edge3.isActive())) {
                                        if (edge3.getPValue() < edge0.getPValue()) {
                                            edge0.setActive(false);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        bar.setForeground(Color.green);
    }
}
