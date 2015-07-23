package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * The microarray abstraction. Every microarray contains values for each one
 * of its associated set of markers. Every microarray belongs to a microarray
 * set, along with other microarrays. In the context of its host
 * microarray set, a microarray has an index position (or "serial") that
 * distinguishes it from the other microarrays in the set.
 */
public interface DSMicroarray extends DSBioObject {

    /**
     * Returns the number of features in the chip
     *
     * @return int
     */
    public int getMarkerNo();

    /**
     * Convenience method to access the valid status of the i-th feature directly
     *
     * @param i int
     * @return boolean
     */
    public boolean isMarkerValid(int i);

    /**
     * Convenience method to access the undefined status of the i-th feature directly
     *
     * @param i int
     * @return boolean
     */
    public boolean isMarkerUndefined(int i);

    /**
     *
     * @return The host microarray set for this microarray.
     */
    //DSMicroarraySet  getMicroarraySet();

    /**
     * Set the host microarray set for this microarray.
     * @param maSet Tne host microarray set.
     */
    //void setMicroarraySet(DSMicroarraySet maSet);

    /**
     * Add the designated marker value at the specified index
     *
     * @param index       int at which marker vale is to be added
     * @param markerValue MarkerValue to be added
     */
    void setMarkerValue(int index, DSMarkerValue markerValue);

    /**
     * @return an array of microarray features.
     */
    DSMutableMarkerValue[] getMarkerValues();

    /**
     * @param markerInfo A marker whose value is requested.
     * @return The <code>MarkerValue</code> for the designated marker
     *         within this microarray, if the marker exists in the
     *         microarray. Null, otherwise.
     */
    DSMutableMarkerValue getMarkerValue(DSGeneMarker markerInfo);

    /**
     * @param index The relative position (index) of a marker within the microarray.
     * @return The marker value for the desiganted index position, if
     *         <code>index</code> is non-negative and no larger than the
     *         microarray size. <code>null</code>, otherwise.
     */
    DSMutableMarkerValue getMarkerValue(int index);

    /**
     * @return A deep copy of the microarray.
     */
    DSMicroarray deepCopy();

    /**
     * Return an iterator over the markers
     *
     * @return ArrayIterator
     */
    java.util.Iterator<DSMutableMarkerValue> iterator();

    /**
     * Resize the number of features to a new value. This destroys all previous values
     */
    void resize(int size);

    float[] getRawMarkerData();
}
