package org.geworkbench.bison.datastructure.bioobjects.markers.genotype;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenotypicMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.util.colorcontext.ColorContext;

import java.awt.*;

public class GenotypeColorContext implements ColorContext {

    public GenotypeColorContext() {
    }

    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        if (mv == null || mInfo == null)
            return Color.black;
        CSGenotypeMarker gtStats = (CSGenotypeMarker) mInfo;
        org.geworkbench.bison.util.Range range = gtStats.getRange();
        int allele1 = ((CSGenotypicMarkerValue) mv).getAllele(0);
        int allele2 = ((CSGenotypicMarkerValue) mv).getAllele(1);
        if (allele1 == allele2) {
            if (allele1 == 1) {
                return Color.yellow;
            } else if (allele1 == 2) {
                return Color.yellow.darker();
            }
            float v = (float) allele1 / (float) range.max * intensity;
            v = Math.max(v, +0.0F);
            v = Math.min(v, +1.0F);
            return new Color(v, 0F, 0F);
        } else {
            float v1 = (float) allele1 / (float) range.max * intensity;
            v1 = Math.max(v1, +0.0F);
            v1 = Math.min(v1, +1.0F);
            float v2 = (float) allele2 / (float) range.max * intensity;
            v2 = Math.max(v2, +0.0F);
            v2 = Math.min(v2, +1.0F);
            return new Color(v1, v2, v2);
        }
    }

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        return null;
    }

    public void updateContext(DSMicroarraySetView view) {
        // no-op
    }

    public Color getMaxColorValue(float intensity) {
        return new Color(1f, 0, 0);
    }

    public Color getMinColorValue(float intensity) {
        return new Color(0, 1f, 0);
    }

    public Color getMiddleColorValue(float intensity) {
        return Color.black;
    }
}
