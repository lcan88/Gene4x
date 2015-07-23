package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Model of the data collected for a single spot (feature) on a microarray.
 * This is the "minimum" set of data expected, regardless of the underlying
 * technology. Technology-specific data points can be added by extending this
 * class.
 */
public interface DSMarkerValue extends Comparable, Serializable {

    /**
     * Return the signal measurement for this spot. For 2-channel data, this is
     * usually the ratio of the 2 channels.
     *
     * @return marker value
     */
    double getValue();

    /**
     * A value indicating our confidence in the measured signal.
     *
     * @return confidence
     */
    double getConfidence();

    /**
     * Check if the mesurement for this spot is classified as "missing".
     *
     * @return presence
     */
    boolean isMissing();

    /**
     * This method returns whether this marker should be temporarily ignored by
     * analytical tools
     *
     * @return
     */
    boolean isMasked();

    /**
     * This method returns whether the marker is both unmasked and not undefined.
     * As a result, there is no corresponding setValid method. Should likely be
     * moved to MarkerValue
     *
     * @return
     */
    boolean isValid();

    /**
     * Tests if two markers are equal
     *
     * @param m marker to be compared
     * @return boolean equality
     */
    boolean equals(Object m);

    /**
     * This method returns the dimensionality of the marker. E.g., Genotype markers are 2-dimensional
     * while Allele/Haplotype markers are 1-dimensional
     *
     * @return int the dimensionality of the marker.
     */
    int getDimensionality();

    /**
     * Make a deep copy of the measurements associated with this spot.
     *
     * @return <code>MarkerValue</code> clone of this marker
     */
    DSMarkerValue deepCopy();

}
