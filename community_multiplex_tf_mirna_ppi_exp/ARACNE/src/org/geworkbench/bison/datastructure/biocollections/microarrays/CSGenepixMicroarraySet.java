package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.parsers.GenepixParser2;
import org.geworkbench.bison.parsers.resources.GenepixResource;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
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
 * of  Genepix marker data.
 */
public class CSGenepixMicroarraySet extends CSMicroarraySet<DSMicroarray> implements Serializable {

    /**
     * Instantiates a Genepix-type micorarray set from local file following
     * the Genepix .gpr format.
     *
     * @param gr
     * @throws Exception
     */
    public CSGenepixMicroarraySet(GenepixResource gr) throws Exception {
        super(RandomNumberGenerator.getID(), gr.getInputFile().getName());
        List ctu = new ArrayList();
        ctu.add("Block");
        ctu.add("Column");
        ctu.add("Row");
        ctu.add("ID");
        ctu.add("X");
        ctu.add("Y");
        ctu.add("Dia");
        ctu.add("F635 Median");
        ctu.add("F635 Mean");
        ctu.add("B635 Median");
        ctu.add("B635 Mean");
        ctu.add("F532 Median");
        ctu.add("F532 Mean");
        ctu.add("B532 Median");
        ctu.add("B532 Mean");
        ctu.add("Ratio of Means");
        GenepixParser2 parser = new GenepixParser2(ctu);
        BufferedReader reader = new BufferedReader(new FileReader(gr.getInputFile()));
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
        reader = new BufferedReader(new FileReader(gr.getInputFile()));
        parser.reset();
        while ((line = reader.readLine()) != null) {
            if (!line.trim().equals("")) {
                parser.parseLine(line);
            }
        }

        reader.close();
        parser.getMicroarray().setLabel(gr.getInputFile().getName());
        this.add(parser.getMicroarray());
        experimentInfo = parser.getExperimentInfo();
    }

    public CSGenepixMicroarraySet() {
        super(RandomNumberGenerator.getID(), "");
    }

    public int getPlatformType() {
        return GENEPIX_PLATFORM;
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
