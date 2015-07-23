package org.geworkbench.util.associationdiscovery.cluster;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.CSPValued;
import org.geworkbench.bison.util.DSPValue;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSMatrixPattern implements DSMatrixPattern {
    protected String patternString = new String("TBD");
    protected DSMarkerValue[] values = null;
    protected DSGeneMarker[] markers = null;
    double[] minValue = null;
    double[] maxValue = null;

    /**
     * Standard method to determine the pValue of matching a
     * pattern to an object.
     *
     * @param array DSMicroarray
     * @return double
     */
    public DSPValue match(DSMicroarray array) {
        DSPValue r = new CSPValued();
        r.setPValue(0.0);
        boolean isMatch = true;
        for (int k = 0; k < markers.length; k++) {
            DSGeneMarker marker = markers[k];
            double m1 = array.getMarkerValue(marker).getValue();
            if (m1 < minValue[k] || m1 > maxValue[k]) {
                r.setPValue(1.0);
            }
        }
        return r;
        //        DSPValue r = new CSPValued();
        //        r.setPValue(0.0);
        //        boolean isMatch = true;
        //        for (int k = 0; k < markers.length; k++) {
        //            DSMarker marker = markers[k];
        //            double m1 = array.getMarkerValue(marker).getValue();
        //            double m2 = values[k].getValue();
        //            double dm = Math.max(m1, m2) * 0.4;
        //            if (m1 < m2 - dm || m1 > m2 + dm) {
        //                r.setPValue(1);
        //            }
        //        }
        //        return r;
    }

    public List<DSPatternMatch<DSMicroarray, DSPValue>> match(DSMicroarray array, double p) {
        ArrayList<DSPatternMatch<DSMicroarray, DSPValue>> matches = new ArrayList<DSPatternMatch<DSMicroarray, DSPValue>>();
        double pValue = match(array).getPValue();
        if (pValue < p) {
            DSPatternMatch<DSMicroarray, DSPValue> match = new CSPatternMatch<DSMicroarray, DSPValue>(array);
            match.getRegistration().setPValue(pValue);
            matches.add(match);
        }
        return matches;
    }

    public String toString(DSMicroarray object, DSPValue registration) {
        return patternString;
    }

    public DSGeneMarker[] markers() {
        return markers;
    }

    //    public DSMarkerValue[] markerValues() {
    //        return values;
    //    }

    public void init(int size) {
        minValue = new double[size];
        maxValue = new double[size];
        values = new DSMarkerValue[size];
        markers = new DSGeneMarker[size];
        for (int k = 0; k < size; k++) {
            minValue[k] = 999999;
            maxValue[k] = -999999;
        }
    }

    public boolean containsMarkers(CSMatrixPattern pat) {
        int j = 0;
        int i = 0;
        int shared = 0;
        for (DSGeneMarker marker : markers) {
            if (marker.getSerial() == pat.markers()[i++].getSerial()) {
                shared++;
            } else if (markers()[j].getSerial() < pat.markers()[i].getSerial()) {
                i--;
            }
            j++;
        }
        System.out.println("Marker Shared: " + shared + ", No: " + pat.markers.length);
        if ((double) shared / (double) pat.markers().length >= 0.8) {
            return true;
        }
        return false;
    }

    public boolean containsMarker(DSGeneMarker testMarker) {
        for (DSGeneMarker marker : markers) {
            if (marker == testMarker) {
                return true;
            }
        }
        return false;
    }

    public void add(DSMicroarray array) {
        int k = 0;
        for (DSGeneMarker marker : markers()) {
            double value = array.getMarkerValue(marker).getValue();
            if (value < minValue[k]) minValue[k] = value;
            if (value > maxValue[k]) maxValue[k] = value;
            k++;
        }
    }

    //    public void add(DSMicroarray array) {
    //        int i = 0;
    //        for (DSMarker marker: markers()) {
    //            DSMarkerValue value = array.getMarkerValue(marker);
    //            values[i++] = value.deepCopy();
    //        }
    //    }
}
