package org.geworkbench.bison.datastructure.bioobjects.microarray;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Defines a Affymetrix Gene Marker generalization of
 * {@link org.geworkbench.bison.model.microarray.MarkerValue}
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface DSAffyMarkerValue extends DSMutableMarkerValue {
    /**
     * Tests the Detection Status of this <code>MarkerValue</code>
     * in the containing <code>Microarray</code>
     *
     * @return presence
     */
    boolean isPresent();

    /**
     * Sets the Detection Status of this <code>MarkerValue</code> to either true
     * or false
     *
     * @param present detection status
     */
    void setPresent();

    /**
     * Tests the Marginality of this <code>MarkerValue</code>
     * in the containing <code>Microarray</code>
     *
     * @return marginality
     */
    boolean isMarginal();

    /**
     * Sets the Marginality of this <code>MarkerValue</code> to either true
     * or false
     *
     * @param marginality marginality
     */
    void setMarginal();

    /**
     * Utility method converse of {@link AffyMarkerValue#isPresent}
     *
     * @return detection status
     */
    boolean isAbsent();

    /**
     * Utility method converse of {@link AffyMarkerValue#setPresent}
     *
     * @param absence detection status
     */
    void setAbsent();
}
