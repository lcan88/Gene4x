package org.geworkbench.util.pathwaydecoder.mutualinformation;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AdjacencyMatrixDense extends AdjacencyMatrix {
    HashMap keyMapping = new HashMap();
    float[][] matrix;

    public AdjacencyMatrixDense() {
    }

    /**
     * Adds and edge between geneId1 and geneId2
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param edge    float
     */
    public void add(int geneId1, int geneId2, float edge) {
        matrix[geneId1][geneId2] = edge;
        matrix[geneId2][geneId1] = edge;
    }

    public void read(String name, DSMicroarraySet<DSMicroarray> microarraySet, JProgressBar bar) {
        maSet = microarraySet;
        int markerNo = microarraySet.size();
        if (bar != null) {
            bar.setForeground(Color.red);
            bar.setStringPainted(true);
            bar.setMaximum(markerNo);
            bar.setValue(0);
        }

        BufferedReader br = null;
        try {
            System.out.println("Reading mappings");
            readMappings(new File(name));
            System.out.println("Done Reading mappings");
            matrix = new float[6152][6152];
            br = new BufferedReader(new FileReader(name));
            try {
                String line = br.readLine();
                while (br.ready()) {
                    if (line.length() > 0 && line.charAt(0) != '-') {
                        StringTokenizer tr = new StringTokenizer(line, "\t: ");
                        String geneAccess = tr.nextToken();
                        int geneId1 = Integer.parseInt(tr.nextToken());
                        System.out.println("Processing gene " + geneId1);
                        //HACK
                        String geneName = (String) keyMapping.get(new Integer(geneId1));
                        geneId1 = -1;
                        if (geneName != null) {
                            DSGeneMarker m = microarraySet.getMarkers().get(geneName);
                            if (m != null) {
                                geneId1 = m.getSerial();
                            }
                        }
                        if (geneId1 > -1) {
                            //xxx
                            while (tr.hasMoreTokens()) {
                                if (bar != null) {
                                    bar.setValue(geneId1);
                                }
                                int geneId2 = Integer.parseInt(tr.nextToken());

                                //HACK
                                String geneName2 = (String) keyMapping.get(new Integer(geneId2));
                                geneId2 = -1;
                                if (geneName2 != null) {
                                    DSGeneMarker m = microarraySet.getMarkers().get(geneName2);
                                    if (m != null) {
                                        geneId2 = m.getSerial();
                                    }
                                }
                                if (geneId2 > -1) {
                                    float mi = Float.parseFloat(tr.nextToken());
                                    if (geneId2 != -1) {
                                        add(geneId1, geneId2, mi);
                                    }
                                }
                            }
                        }
                        if (bar != null) {
                            bar.setForeground(Color.green);
                        }
                    }
                    line = br.readLine();
                }
            } catch (NumberFormatException ex) {
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex3) {
        }
        resolveGeneCollision(maSet);
    }

}
