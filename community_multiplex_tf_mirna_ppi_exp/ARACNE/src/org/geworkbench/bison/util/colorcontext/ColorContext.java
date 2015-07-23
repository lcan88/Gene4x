package org.geworkbench.bison.util.colorcontext;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;

import java.awt.*;
import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Decides what color should be used when dispaying a color mosaic representation
 * of a marker value.
 *
 * Extending classes must be provide a no-arg constructor.
 */
public interface ColorContext extends Serializable {

    public Color getMarkerValueColor(DSMicroarraySetView maSet, DSMarkerValue mv, DSGeneMarker mInfo, float intensity);

    /**
     * @param mv        The <code>MarkerValue</code> that needs to be drawn.
     * @param mInfo     The <code>MarkerInfo</code> corresponding to the
     *                  <code>MarkerValue</code>
     * @param intensity to be used
     * @return The <code>Color</code> to use for drawing.
     */
    public Color getMarkerValueColor(DSMarkerValue mv, DSGeneMarker mInfo, float intensity);

    /**
     * Called whenever the view changes so that the ColorContext can (optionally) update itself.
     */
    public void updateContext(DSMicroarraySetView view);

    /**
     * Returns the color corresponding to the maxiumum value possible at this intensity.
     * @param intensity
     * @return
     */
    public Color getMaxColorValue(float intensity);

    /**
     * Returns the color corresponding to the minimum value possible at this intensity.
     * @param intensity
     * @return
     */
    public Color getMinColorValue(float intensity);

    /**
     * Returns the color that indicates a value between the minimum and maximum values. Usually white or black. 
     * @param intensity
     * @return
     */
    public Color getMiddleColorValue(float intensity);
}
