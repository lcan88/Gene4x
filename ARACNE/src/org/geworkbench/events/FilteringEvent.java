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
public class FilteringEvent extends Event {
    /**
     * The <code>microarraySet</code> that was the input to the filtering
     * operation.
     */
    private DSMicroarraySet sourceMA = null;
    /**
     * The <code>microarraySet</code> that was the result of the filtering
     * operation.
     */
    private DSMicroarraySet resultMA = null;
    /**
     * Information about the filter used.
     */
    private String filterInfo = null;

    public FilteringEvent(DSMicroarraySet s, DSMicroarraySet r, String info) {
        super(null);
        sourceMA = s;
        resultMA = r;
        filterInfo = info;
    }

    /**
     * Gets the <code>microarraySet</code> that was the input to the filtering
     * operation.
     *
     * @return the input dataset
     */
    public DSMicroarraySet getOriginalMASet() {
        return sourceMA;
    }

    /**
     * Gets the <code>microarraySet</code> that was the result of the filtering
     * operation.
     *
     * @return the filtered dataset
     */
    public DSMicroarraySet getFilteredMASet() {
        return resultMA;
    }

    /**
     * Gets the information about the filter used
     *
     * @return
     */
    public String getInformation() {
        return filterInfo;
    }

}
