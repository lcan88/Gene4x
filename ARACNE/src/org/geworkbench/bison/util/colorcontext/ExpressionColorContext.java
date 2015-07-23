package org.geworkbench.bison.util.colorcontext;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.util.Range;

import java.awt.*;

public class ExpressionColorContext implements ColorContext {

    public ExpressionColorContext() {
    }

    /**
     * @param mv        The <code>MarkerValue</code> that needs to be drawn.
     * @param intensity color intensity to be used
     * @return The <code>Color</code> to use for drawing.
     */
    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        if (mv == null || mInfo == null)
            return Color.black;
        double value = mv.getValue();
        Range range = ((CSExpressionMarker) mInfo).getRange();
        double avg = Math.abs(range.max + range.min) / 2.0;
        double norm = Math.abs(range.max - range.min) / 2.0;
        double val = (value - avg);
        if (val > 0) {
            val = Math.min(intensity * val * 256 / norm, 1.0);
            return new Color((float) val, 0F, 0F);
        } else {
            val = Math.max(intensity * val * 256 / norm, -1.0);
            return new Color(0F, -(float) val, 0F);
        }
    }

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        return null;
    }

    public void updateContext(DSMicroarraySetView view) {
        ColorContextUtils.computeRange(view);
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
