package org.geworkbench.util.associationdiscovery.cluster;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.pattern.CSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.util.DSPValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) Andrea Califano</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class CSMatchedMatrixPattern extends CSMatchedPattern<DSMicroarray, DSPValue> implements DSMatchedPattern<DSMicroarray, DSPValue> {
    protected DSMatrixPattern pattern = null;
    public int bkMatches;

    public CSMatchedMatrixPattern(DSMatrixPattern aPattern) {
        pattern = aPattern;
        //        if(pattern == null) {
        //            pattern = new CSMatrixPattern();
        //            pattern.init(markerNo);
        //        }
    }

    //    public void setMarkerIdNo(int no) {
    //        pattern.init(no);
    //    }

    public DSMicroarray getItem(int i) {
        if ((i < 0) || (i >= matches.size())) {
            return null;
        }
        return matches.get(i).getObject();
    }

    public String toString() {
        String pString = new String();
        pString = "[" + String.valueOf(matches.size()) + ", " + String.valueOf(pattern.markers().length) + "," + String.valueOf(zScore) + "]";
        return pString;
    }

    public void writeToFile(DSMicroarraySet chips, File patFile) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(patFile));
            writer.write("Label:\tPanel 1");
            writer.newLine();
            for (DSGeneMarker marker : pattern.markers()) {
                String label = marker.getLabel();
                System.out.println("Access: " + label);
                writer.write(label);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public DSMatrixPattern getPattern() {
        return pattern;
    }
}
