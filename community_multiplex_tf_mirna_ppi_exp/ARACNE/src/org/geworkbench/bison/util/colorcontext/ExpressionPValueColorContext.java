package org.geworkbench.bison.util.colorcontext;

import org.apache.commons.math.stat.StatUtils;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.awt.*;
import java.io.Serializable;

public class ExpressionPValueColorContext implements org.geworkbench.bison.util.colorcontext.ColorContext, Serializable {

    public ExpressionPValueColorContext() {
    }

    // @todo - watkin - This is not used, but if it is was it would be horribly efficient.
    // Stats should be calculated in updateContext.
    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {
        if (mv == null || mInfo == null) {
            int i = 0;
            return Color.black;
        }
        double[] expressionProfile = maSet.getRow(mInfo);
        double mean = StatUtils.mean(expressionProfile);
        double sd = Math.sqrt(StatUtils.variance(expressionProfile));

        double value = mv.getValue();

        double deviationFromMean = value - mean;

        double sdsFromMean = deviationFromMean / sd;

        if (sdsFromMean < -intensity) {
            sdsFromMean = -intensity;
        }
        if (sdsFromMean > intensity) {
            sdsFromMean = intensity;
        }

        double colVal = sdsFromMean / intensity;
        if (sdsFromMean > 0) {
            return new Color(0F, (float) colVal, 0F);
        } else {
            return new Color(-(float) colVal, 0F, 0F);
        }
    }


    /**
     * @param mv        The <code>MarkerValue</code> that needs to be drawn.
     * @param intensity color intensity to be used
     * @return The <code>Color</code> to use for drawing.
     */
    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity) {

//        intensity *= 2;
        intensity = 2 / intensity; 
        double value = mv.getValue();
        org.geworkbench.bison.util.Range range = ((DSRangeMarker) mInfo).getRange();
        double mean = range.norm.getMean(); //(range.max + range.min) / 2.0;
        double foldChange = (value - mean) / (range.norm.getSigma() + 0.00001); //Math.log(change) / Math.log(2.0);
        if (foldChange < -intensity) {
            foldChange = -intensity;
        }
        if (foldChange > intensity) {
            foldChange = intensity;
        }

        double colVal = foldChange / intensity;
        if (foldChange > 0) {
            return new Color(1.0F, (float) (1 - colVal), (float) (1 - colVal));
        } else {
            return new Color((float) (1 + colVal), (float) (1 + colVal), 1.0F);
        }


        //        Range range = ((CSExpressionMarker)mInfo).getRange();
        //        double avg  = Math.log(range.max + range.min) / 1.5;
        //        double val  = (Math.log(value) - avg);
        //        double norm = Math.log(range.max - range.min) / 2.0;
        //        if (val > 0) {
        //            val = Math.min(intensity * val / norm, 1.0);
        //            if(val < 0){
        //                val = 0;
        //            }
        //            return new Color( (float) val, 0F, 0F);
        //        }
        //        else {
        //            val = Math.max(intensity * val / norm, -1.0);
        //            if(val > 0){
        //                val = 0;
        //            }
        //
        //            return new Color(0F, - (float) val, 0F);
        //        }
    }

    public void updateContext(DSMicroarraySetView view) {
        ColorContextUtils.computeRange(view);
    }

    public Color getMaxColorValue(float intensity) {
        return Color.red;
    }

    public Color getMinColorValue(float intensity) {
        return Color.blue;
    }

    public Color getMiddleColorValue(float intensity) {
        return Color.white;
    }
}
