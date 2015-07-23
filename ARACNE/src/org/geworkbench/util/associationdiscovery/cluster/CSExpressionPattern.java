package org.geworkbench.util.associationdiscovery.cluster;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.pattern.matrix.CSPValued;
import org.geworkbench.bison.util.DSPValue;
import org.geworkbench.bison.util.Normal;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */
public class CSExpressionPattern extends CSMatrixPattern {
    org.geworkbench.bison.util.Normal[] normal = null;

    public CSExpressionPattern() {
    }

    public void add(DSMicroarray array) {
        int k = 0;
        for (DSGeneMarker marker : markers()) {
            double value = array.getMarkerValue(marker).getValue();
            normal[k].add(value);
            k++;
        }
    }

    public void init(int size) {
        markers = new DSGeneMarker[size];
        normal = new org.geworkbench.bison.util.Normal[size];
        for (int i = 0; i < size; i++) {
            normal[i] = new Normal();
        }
        // Do not call super.init(size) as this would allocate additional, unrequired
        // memory
    }

    public DSPValue match(DSMicroarray array) {
        DSPValue r = new CSPValued();
        r.setPValue(0.0);
        boolean isMatch = true;
        for (int k = 0; k < markers.length; k++) {
            DSGeneMarker marker = markers[k];
            double m1 = array.getMarkerValue(marker).getValue();
            double mean = normal[k].getMean();
            double sigma = normal[k].getSigma();
            if (Math.abs(m1 - mean) > sigma * 2.0) {
                r.setPValue(1.0);
            }
        }
        return r;
    }

}
