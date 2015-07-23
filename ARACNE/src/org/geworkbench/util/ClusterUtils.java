package org.geworkbench.util;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.model.clusters.HierCluster;
import org.geworkbench.bison.model.clusters.MarkerHierCluster;
import org.geworkbench.bison.model.clusters.MicroarrayHierCluster;

/**
 * @author John Watkinson
 */
public class ClusterUtils {

    private static String getPad(int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static void printClusterHelper(int depth, HierCluster h) {
        if (h.isLeaf()) {
            System.out.print(getPad(depth * 2));
            if (h instanceof MarkerHierCluster) {
                System.out.println("" + h.getHeight() + " (" + ((MarkerHierCluster) h).getMarkerInfo().getLabel() + ")");
            } else if (h instanceof MicroarrayHierCluster) {
                System.out.println("" + h.getHeight() + " (" + ((MicroarrayHierCluster) h).getMicroarray().getLabel() + ")");
            }
        } else {
            printClusterHelper(depth + 1, h.getNode(0));
            System.out.print(getPad(depth * 2));
            System.out.println("" + h.getHeight());
            printClusterHelper(depth + 1, h.getNode(1));
        }
    }

    public static void printCluster(HierCluster h) {
        printClusterHelper(0, h);
    }

    public static boolean areClustersEqual(HierCluster h1, HierCluster h2) {
        if (h1.getDepth() == h2.getDepth()) {
            if (h1.isLeaf() && h2.isLeaf()) {
                if ((h1 instanceof MarkerHierCluster) && (h2 instanceof MarkerHierCluster)) {
                    if (h1.getHeight() == h2.getHeight()) {
                        DSGeneMarker marker1 = ((MarkerHierCluster) h1).getMarkerInfo();
                        DSGeneMarker marker2 = ((MarkerHierCluster) h2).getMarkerInfo();
                        if (marker1.getLabel().equals(marker2.getLabel())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else if ((h1 instanceof MicroarrayHierCluster) && (h2 instanceof MicroarrayHierCluster)) {
                    if (h1.getHeight() == h2.getHeight()) {
                        DSMicroarray microarray1 = ((MicroarrayHierCluster) h1).getMicroarray();
                        DSMicroarray microarray2 = ((MicroarrayHierCluster) h2).getMicroarray();
                        if (microarray1.getLabel().equals(microarray2.getLabel())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else if (!h1.isLeaf() && !h2.isLeaf()) {
                if (areClustersEqual(h1.getNode(0), h2.getNode(0)) && areClustersEqual(h1.getNode(1), h2.getNode(1))) {
                    return true;
                } else if (areClustersEqual(h1.getNode(1), h2.getNode(0)) && areClustersEqual(h1.getNode(0), h2.getNode(1))) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        HierCluster h1;
        HierCluster h2;
        {
            MarkerHierCluster m1 = new MarkerHierCluster();
            m1.setMarkerInfo(new CSGeneMarker("A"));
            m1.setHeight(1);
            MarkerHierCluster m2 = new MarkerHierCluster();
            m2.setMarkerInfo(new CSGeneMarker("B"));
            m2.setHeight(2);
            MarkerHierCluster m3 = new MarkerHierCluster();
            m3.setMarkerInfo(new CSGeneMarker("C"));
            m3.setHeight(3);
            MarkerHierCluster m4 = new MarkerHierCluster();
            m4.addNode(m1, 0);
            m4.addNode(m2, 1);
            m4.setHeight(5);
            MarkerHierCluster m5 = new MarkerHierCluster();
            m5.addNode(m3, 0);
            m5.addNode(m4, 1);
            m5.setHeight(7);
            h1 = m5;
        }
        {
            MarkerHierCluster m1 = new MarkerHierCluster();
            m1.setMarkerInfo(new CSGeneMarker("A"));
            m1.setHeight(1);
            MarkerHierCluster m2 = new MarkerHierCluster();
            m2.setMarkerInfo(new CSGeneMarker("B"));
            m2.setHeight(2);
            MarkerHierCluster m3 = new MarkerHierCluster();
            m3.setMarkerInfo(new CSGeneMarker("C"));
            m3.setHeight(3);
            MarkerHierCluster m4 = new MarkerHierCluster();
            m4.addNode(m2, 0);
            m4.addNode(m1, 1);
            m4.setHeight(5);
            MarkerHierCluster m5 = new MarkerHierCluster();
            m5.addNode(m4, 0);
            m5.addNode(m3, 1);
            m5.setHeight(7);
            h2 = m5;
        }
        System.out.println("Cluster 1:");
        printCluster(h1);
        System.out.println("");
        System.out.println("Cluster 2:");
        printCluster(h2);
        System.out.println("Equal? " + areClustersEqual(h1, h2));
    }
}
