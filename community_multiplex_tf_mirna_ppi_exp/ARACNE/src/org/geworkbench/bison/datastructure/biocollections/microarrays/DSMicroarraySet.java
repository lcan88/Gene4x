package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSMatrixDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.io.File;

/**
 * Defines a generic set of Microarrays.
 * <p/>
 * DSDataSet. The objects in a matrix dataset will themselves contain vectors of values. -- AM
 * todo - watkin - this should have two generic type variables, one for microarray, one for marker.
 * Currently, it is just generic for microarrays.
 */
public interface DSMicroarraySet <T extends DSMicroarray> extends DSMatrixDataSet<T>, DSDataSet<T> {

    /**
     * The type of the array can be gene expression or genotype
     * This should be replaced by two separate classes with the same interface
     */
    public int getType();

    public final static int geneExpType = 0; // Probably needs to be removed
    public final static int snpType = 1; // Probably needs to be removed
    public final static int alleleType = 2; // Probably needs to be removed
    public final static int expPvalueType = 3; // added by xuegong
    public final static int chipChipType = 4; // added by xuegong
    public final static int genepixGPRType = 5;
    public final static int affyTxtType = 6; // Txt MAS 4.0/5.0 file type

    // Copy the array into a new array with (potentially) new number of microarrays
    // or markers
    DSMicroarraySet<T> clone(String newLabel, int newMarkerNo, int newChipNo);

    //    public double getValue(int markerIndex, int maIndex);
    public double getValue(DSGeneMarker marker, int maIndex);

    public double getMeanValue(DSGeneMarker marker, int maIndex);

    public double[] getRow(DSGeneMarker marker);

    /**
     * @todo - watkin - This method has no-op implementations in all derived classes.
     */
    public void resetStatistics();

    public void parse(DSMutableMarkerValue marker, String value);

    /**
     * Merges another <code>MicroarraySet</code> into this one
     *
     * @param newMaSet MicroarraySet
     */
    void mergeMicroarraySet(DSMicroarraySet<T> newMaSet) throws Exception;

    /**
     * get the marker list
     *
     * @return DSItemList
     * todo - watkin - this should not be DSGeneMarker, but a generic variable.
     */
    public DSItemList<DSGeneMarker> getMarkers();

    /**
     * Creates a deep copy of the current microarray set (creating fresh
     * copies for all markers, marker values and microarrays in the microarray
     * set).
     *
     * @return The new microarray set copy.
     */
    DSMicroarraySet<T> deepCopy();

    /**
     * Return the platform type (Affy, Genepix, etc) of the arrays contained
     * in this array set.
     *
     * @return
     */
    int getPlatformType();

    void setCompatibilityLabel(String compatibilityLabel);

    void readFromFile(File file);

    // It is defined in the classes. In CSMicroarraySet (AC)
    public void initialize(int maNo, int mrkNo);
}
