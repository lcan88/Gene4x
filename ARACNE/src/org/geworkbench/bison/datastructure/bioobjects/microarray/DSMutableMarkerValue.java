package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.Serializable;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia University</p>
 * @author Califano Lab
 * @version 1.0
 */

/**
 * Model of mutable behavior of data collected for a single spot (feature) on
 * a microarray.
 */

public interface DSMutableMarkerValue extends DSMarkerValue, Serializable {

    /**
     * Set the spot's value.
     *
     * @param value the spot's value
     */
    void setValue(double value);

    /**
     * Set the confidence level.
     *
     * @param confidence confidence of spot's measument
     */
    void setConfidence(double confidence);

    /**
     * Set the status of this spot measurement. The measurement could be be any
     * spot specific information. eg: Missing, Present, Valid and/or others
     *
     * @param status status of this spot's measurement
     */
    void setMissing(boolean missing);

    /**
     * Sets the mask bit to be temporarily ignored by analyses methods
     */
    void mask();

    /**
     * Resets the mask bit to be used by analyses methods
     */
    public void unmask();
}
