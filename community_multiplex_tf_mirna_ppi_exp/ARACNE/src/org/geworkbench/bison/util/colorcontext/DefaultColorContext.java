package org.geworkbench.bison.util.colorcontext;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Default implementation of a color context. Assigns positive marker values
 * in the red spectrum and negative values to the green spectrum.
 */

public class DefaultColorContext implements org.geworkbench.bison.util.colorcontext.ColorContext {

    private final Color MISSING_VALUE_COLOR = Color.GRAY;

    private double magnitude;

    public DefaultColorContext() {
    }

    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        if (mv == null || mv.isMissing())
            return MISSING_VALUE_COLOR;
        double value = mv.getValue();
        Color color = null;
        float v = (float) (value * intensity / magnitude);
        if (v > 0) {
            v = (float)Math.min(1.0, v);
            // color = new Color(1.0f, (1 - v), (1 - v));
            color = new Color(v, 0, 0);
        } else {
            v = -v;
            v = (float)Math.min(1.0, v);
            // color = new Color((1- v), (1 - v), 1.0f);
            color = new Color(0, v, 0);
        }
        return color;
    }

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        return null;
    }

    public void updateContext(DSMicroarraySetView view) {
        // Use entire set
        DSMicroarraySet set = view.getMicroarraySet();
        magnitude = 0.0;
        for (int i = 0; i < set.size(); i++) {
            for (int j = 0; j < set.getMarkers().size(); j++) {
                double value = Math.abs(set.getValue(j, i));
                if (value > magnitude) {
                    magnitude = value;
                }
            }
        }
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
