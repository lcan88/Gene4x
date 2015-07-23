package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.parsers.AffyParser;
import org.geworkbench.bison.parsers.resources.AffyResource;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Implementation of <code>MicroarraySet</code> to accommodate the storage
 * of Affymetrix marker data.
 */
public class CSAffyMicroarraySet extends CSMicroarraySet<DSMicroarray> implements Serializable {

    public CSAffyMicroarraySet() {
        super(RandomNumberGenerator.getID(), "");
    }

    /**
     * Instantiates an Affymetrix-type micorarray set from local file following
     * the Affy MAS 4.0/5.0 format.
     *
     * @param ar
     * @throws Exception
     */
    public CSAffyMicroarraySet(AffyResource ar) throws Exception {
        super(RandomNumberGenerator.getID(), ar.getInputFile().getName());
        List ctu = new ArrayList();
        ctu.add("Probe Set Name");
        ctu.add("Avg Diff");
        ctu.add("Signal");
        ctu.add("Log2(ratio)");
        ctu.add("Detection");
        ctu.add("Detection p-value");
        ctu.add("Abs Call");
        AffyParser parser = new org.geworkbench.bison.parsers.AffyParser(ctu);
        BufferedReader reader = new BufferedReader(new FileReader(ar.getInputFile()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().equals("")) {
                parser.process(line);
            }
        }

        Vector v = parser.getAccessions();
        int count = 0;
        DSGeneMarker mInfo = null;
        for (Iterator it = v.iterator(); it.hasNext();) {
            mInfo = new CSGeneMarker((String) it.next());
            mInfo.setSerial(count++);
            markerVector.add(mInfo);
        }
        reader.close();
        reader = new BufferedReader(new FileReader(ar.getInputFile()));
        parser.reset();
        while ((line = reader.readLine()) != null) {
            if (!line.trim().equals("")) {
                parser.parseLine(line);
            }
        }

        reader.close();
        parser.getMicroarray().setLabel(ar.getInputFile().getName());
        this.add(parser.getMicroarray());
        this.experimentInfo = parser.getExperimentInfo();
    }

    /**
     * For debugging purposes only.
     *
     * @param args
     */
    public static void main(String[] args) {
        AffyResource affyResource = new AffyResource();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(args[0])));
        } catch (IOException ioe) {
        }

        affyResource.setReader(reader);
        System.out.println("Parsing file: ");
        try {
            //      AffyMicroarraySetImpl ma = new AffyMicroarraySetImpl(affyResource);
            //      System.out.println("MicroarraySet hasNext: " + ma.hasNext());
            //      MicroarrayVectorImpl microarrayVector = new MicroarrayVectorImpl(ma.
            //          getName(), "", ma, true);
            //      ma.reset();
            //      System.out.println("Microarray Vector size: " +
            //                         microarrayVector.getMicroarrayNo());
            //      DSMicroarray microarray = null;
            //      if (ma.hasNext()) {
            //        DSItemList<DSMarker> mInfo = ma.markers();
            //        System.out.println("DSMarker 0: " + mInfo.get(0).getLabel());
            //        microarray = (DSMicroarray) ma.next();
            //        System.out.println("Microarray ID: " + microarray.getID());
            //      }

            //      if (microarray.hasNext()) {
            //        microarray.next();
            //        microarray.remove();
            //      }

            //      System.out.println("Microarray hasNext: " + microarray.hasNext());
            //      while (microarray.hasNext()) {
            //        microarray.next();
            //        microarray.remove();
            //      }
            //
            //      System.out.println("Microarray hasNext: " + microarray.hasNext());
            //      ma.remove();
            //      System.out.println("MicroarraySet hasNext: " + ma.hasNext());
            //      System.out.println("Microarray Set: " + ma.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPlatformType() {
        return AFFYMETRIX_PLATFORM;
    }

    /**
     * @todo Check these empty methods
     */
    public void writeToFile(String fileName) {
    }

    public int getType() {
        return 0;
    }

    public DSMicroarraySet clone(String newLabel, int newMarkerNo, int newChipNo) {
        return null;
    }

    public DSMicroarraySet toHaplotype() {
        return null;
    }

    public void resetStatistics() {
    }

    public void parse(DSMutableMarkerValue marker, String value) {
    }

    public void readFromFile(File file) {
    }
}
