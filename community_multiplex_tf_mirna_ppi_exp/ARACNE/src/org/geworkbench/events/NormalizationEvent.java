package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Application event thrown in order to communicate the results of a normalization
 * operation.
 */
public class NormalizationEvent extends Event {
    /**
     * The microarray that was the input to the normalization operation.
     */
    private DSMicroarraySet sourceMA = null;
    /**
     * The microarray that was the result of the normalization operation.
     */
    private DSMicroarraySet resultMA = null;
    /**
     * Information about the normalizer used.
     */
    private String normalizerInfo = null;

    public NormalizationEvent(DSMicroarraySet s, DSMicroarraySet r, String info) {
        super(null);
        sourceMA = s;
        resultMA = r;
        normalizerInfo = info;
    }

    public DSMicroarraySet getOriginalMASet() {
        return sourceMA;
    }

    public DSMicroarraySet getNormalizedMASet() {
        return resultMA;
    }

    public String getInformation() {
        return normalizerInfo;
    }

}
