package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.apache.commons.math.stat.StatUtils;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.*;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.*;
import java.util.*;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */

public class CSMicroarraySet<T extends DSMicroarray> extends CSDataSet<T> implements DSMicroarraySet<T> {
    /**
     * Constant designating the Affymetrix platform. This is the value return by
     * calls to method <code>getPlatforType()</code>.
     */
    public static final int AFFYMETRIX_PLATFORM = 0;

    /**
     * Constant designating the Genepix platform. This is the value return by
     * calls to method <code>getPlatforType()</code>.
     */
    public static final int GENEPIX_PLATFORM = 1;

    /**
     * Constant designating the Genotypic platform. This is the value return by
     * calls to method <code>getPlatforType()</code>.
     */
    public static final int GENOTYPE_PLATFORM = 2;

    protected CSMarkerVector markerVector = new CSMarkerVector();

    protected Date timeStamp = new java.util.Date();
    protected HashMap mArrayProperties = new HashMap();
    protected HashMap markerProperties = new HashMap();
    protected int maskedSpots = 0;
    protected int type = -1;

    public CSMicroarraySet(String id, String name) {
        setID(id);
        setLabel(name);
    }

    public CSMicroarraySet() {
        setID(RandomNumberGenerator.getID());
        setLabel("");
    }

    /**
     * This get a little tricky. We need to be able to match up equivalent markers but this class needs to
     * use its specific markers because they contain the serials used to get the values. When life was simple
     * we just provided a get method that returned the marker in this MicroarraySet if it found any that matched
     * the parameter. However, there may be several spots on an array that match the same gene name, for example.
     * So we need to figure out how to handle this. -- AM
     */
    public Vector<DSGeneMarker> getEquivalentMarkers(DSGeneMarker marker) {
        return markerVector.getMatchingMarkers(marker);
    }

    public double getValue(DSGeneMarker marker, int maIndex) {
        //If we get a marker that is on this array -- i.e. it has a unique identifier, then
        //just return the value for that marker and don't waste time searching by other identifiers
        DSGeneMarker maMarker = markerVector.getMarkerByUniqueIdentifier(marker.getLabel());
        if (maMarker != null) {
            double value = get(maIndex).getMarkerValue(maMarker.getSerial()).getValue();
            return value;
        } else {
            //If we don't find the unique identifier then the caller wants to match one something else,
            //not guaranteed to be unique, so by default we should return the mean of all the matching
            //markers
            return getMeanValue(marker, maIndex);
        }
    }

    public double[] getValues(DSGeneMarker marker, int maIndex) {
        Vector<DSGeneMarker> matchingMarkers = markerVector.getMatchingMarkers(marker);
        if (matchingMarkers != null && matchingMarkers.size() > 0) {
            int[] serials = new int[matchingMarkers.size()];
            for (int markerCtr = 0; markerCtr < matchingMarkers.size(); markerCtr++) {
                serials[markerCtr] = matchingMarkers.get(markerCtr).getSerial();
            }
            return getValues(serials, maIndex);
        } else {
            return null;
        }
    }

    public double getMeanValue(DSGeneMarker marker, int maIndex) {
        double values[] = getValues(marker, maIndex);
        if (values == null || values.length < 1) {
            return Double.NaN;
        } else {
            return StatUtils.mean(values);
        }
    }

    public double getMeanValue(int[] rows, int maIndex) {
        double[] values = getValues(rows, maIndex);
        return StatUtils.mean(values);
    }

    public double[] getValues(int[] rows, int maIndex) {
        double[] values = new double[rows.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = getValue(rows[i], maIndex);
        }
        return values;
    }

    public double getConfidence(DSGeneMarker marker, int maIndex) {
        DSGeneMarker maMarker = markerVector.get(marker);
        if (maMarker != null) {
            double value = get(maIndex).getMarkerValue(maMarker.getSerial()).getConfidence();
            return value;
        } else {
            return Double.NaN;
        }
    }

    /**
     * @param markerIndex
     * @param maIndex
     * @return
     * @todo - watkin - This seems to violate the contract for {@link DSMatrixDataSet#getValue(int, int)}. This
     * method passes markerIndex and microarray index, whereas the interface refers to a row and a column. Confusing.
     * Same thing.... this is the implementation of the method. You can change markerIndex and maIndex to row/column
     * if you want but it's just makes it more clear if people can know what the row and column refer to.
     * The getExpressionProfile can be changed to getRow(int markerIndex) which should also be in the DSMatrixDataSet
     * interface. This is all legacy from before DSMatrixDataSet existed. Likewise, DSMatrixDataSet can have
     * double getValue(T row, int column) and double[] getRow(T row) methods. -- AM
     */
    public double getValue(int markerIndex, int maIndex) {
        double value = get(maIndex).getMarkerValue(markerIndex).getValue();
        return value;
    }

    /**
     * Note-- changes to the returned row will not have any effect on the underlying MicroarraySet.
     * @param markerIndex
     * @return
     */
    public double[] getRow(int markerIndex) {
        double[] expressionProfile = new double[size()];
        for (int i = 0; i < expressionProfile.length; i++) {
            expressionProfile[i] = getValue(markerIndex, i);
        }
        return expressionProfile;
    }

    public double[] getRow(DSGeneMarker marker) {
        double[] expressionProfile = new double[size()];
        for (int i = 0; i < expressionProfile.length; i++) {
            expressionProfile[i] = getValue(marker, i);
        }
        return expressionProfile;
    }

    public void setCompatibilityLabel(String compatibilityLabel) {
        this.compatibilityLabel = compatibilityLabel;
    }

    protected void writePhenotypeValueArray(BufferedWriter writer) throws IOException {
        // DSCriteria<DSBioObject> criteria = CSCriterionManager.getCriteria(this);
        DSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
        DSAnnotationContext<T>[] contexts = manager.getAllContexts(this);
        for (int j = 0; j < contexts.length; j++) {
            DSAnnotationContext<T> context = contexts[j];
            writer.write("Description\t" + context.getName());
            for (int i = 0; i < size(); i++) {
                DSMicroarray mArray = get(i);
                String[] labels = context.getLabelsForItem((T)mArray);
                // todo - watkin - this file format does not support multiple labellings per item
                if (labels.length > 0) {
                    writer.write("\t" + labels[0]);
                } else {
                    writer.write("\tUndefined");
                }
            }
            writer.write('\n');
        }
    }

    // Serializes a microarray set
    private void writeMArrays() {
        String name = System.getProperty("temporary.files.directory") + System.getProperty("file.separator") + "MA" + timeStamp.getTime() + ".jds";
        try {
            FileOutputStream f = new FileOutputStream(name);
            ObjectOutput s = new ObjectOutputStream(f);
            for (int i = 0; i < size(); i++) {
                s.writeObject(get(i));
            }
            s.flush();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Write/save additional fields
        oos.writeObject(new java.util.Date());
        if (get(0) != null) {
            writeMArrays();
        }
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    public CSMarkerVector getMarkers() {
        return markerVector;
    }

    /**
     * Restores non-serialized instance variables.
     *
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public DSMicroarraySet deepCopy() {
        return null;
    }

    public void mergeMicroarraySet(DSMicroarraySet<T> newMaSet) {
        /**
         * Stores the markers of the microarray set (the same markers that are also
         * found in {@link org.geworkbench.bison.model.microarray.AbstractMicroarraySet#markerInfoVector}).
         * The efficient searching afforded by using <code>TreeMap</code> enhances
         * a number of operations that require searching for markers.
         */
        TreeMap markerInfoIndices = new TreeMap();
        DSItemList<DSGeneMarker> markerInfos = newMaSet.getMarkers();
        T microarray = null;
        int oldIndex = 0, newIndex = 0;
        if (markerInfos != null) {
            for (int i = 0; i < markerInfos.size(); i++) {
                if (!markerVector.contains(markerInfos.get(i))) {
                    oldIndex = markerInfos.get(i).getSerial();
                    markerInfos.get(i).setSerial(markerVector.size() - 1);
                    markerVector.add(markerInfos.get(i));
                    newIndex = markerInfos.get(i).getSerial();
                    markerInfoIndices.put(new Integer(oldIndex), new Integer(newIndex));
                } else {
                    oldIndex = markerInfos.get(i).getSerial();
                    markerInfoIndices.put(new Integer(oldIndex), new Integer(oldIndex));
                }
            }
        }

        int count = size();
        for (int ac = 0; ac < count; ac++) {
            microarray = get(ac);
            DSMarkerValue[] values = microarray.getMarkerValues();
            DSMutableMarkerValue refValue = null;
            DSMutableMarkerValue missingValue = null;
            int mvLength = values.length;
            int size = markerVector.size();
            if (mvLength < size) {
                if (mvLength > 0) {
                    refValue = (DSMutableMarkerValue) values[0];
                } else {
                    refValue = new CSAffyMarkerValue();
                }
                microarray.resize(size);
                for (int i = 0; i < size; ++i) {
                    if (microarray.getMarkerValue(i) == null) {
                        missingValue = (DSMutableMarkerValue) refValue.deepCopy();
                        missingValue.setMissing(true);
                        microarray.setMarkerValue(i, missingValue);
                    }
                }
            }
        }

        count = newMaSet.size();
        for (int ac = 0; ac < count; ac++) {
            microarray = (T)newMaSet.get(ac).deepCopy();
            int size = 0;
            DSMutableMarkerValue refValue = null;
            DSMutableMarkerValue missingValue = null;
            DSMarkerValue[] newValues = microarray.getMarkerValues();
            size = markerVector.size();
            microarray.resize(size);
            for (int i = 0; i < newValues.length; i++) {
                Integer key = (Integer) markerInfoIndices.get(new Integer(i));
                if (key != null) {
                    newIndex = key.intValue();
                    if (newIndex < microarray.getMarkerNo()) {
                        microarray.setMarkerValue(newIndex, newValues[i]);
                    }
                }
            }
            if (newValues.length != 0) {
                refValue = (DSMutableMarkerValue) newValues[0];
            } else {
                refValue = new CSAffyMarkerValue();
            }
            for (int i = 0; i < size; i++) {
                if (microarray.getMarkerValue(i) == null) {
                    missingValue = (DSMutableMarkerValue) refValue.deepCopy();
                    missingValue.setMissing(true);
                    microarray.setMarkerValue(i, missingValue);
                }
            }
            add(size(), microarray);
        }
    }

    public DSMicroarray getMicroarrayWithId(String string) {
        for (DSMicroarray ma : this) {
            if (ma.getLabel().equalsIgnoreCase(string)) {
                return ma;
            }
        }
        return null;
    }

    public void resetStatistics() {
        // to be filled as needed by subclasses
    }

    /**
     * Should be moved to microarray utility file
     *
     * @param aType int
     * @param id    int
     */
    public void randomize(int aType, int id) {
        this.label = new String("Random Set: ") + id;
        this.type = aType;
        for (int j = 0; j < getMarkers().size(); j++) {
            ArrayList<DSMarkerValue> set = new ArrayList<DSMarkerValue>();
            for (DSMicroarray ma : this) {
                set.add(ma.getMarkerValue(j));
            }
            for (DSMicroarray ma : this) {
                int index = (int) (Math.random() * set.size());
                ma.setMarkerValue(j, set.get(index));
                set.remove(index);
            }
        }
    }

    public void writeToFile(String file) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("PDFModel\tAutoNormal\n");
            switch (type) {
                case snpType:
                    writer.write("SNPData\n");
                    break;
                case alleleType:
                    writer.write("AlleleData\n");
                    break;
                default:
            }
            writer.write("Description\tAccession");
            for (DSMicroarray ma : this) {
                writer.write('\t');
                writer.write(ma.getLabel());
            }
            writer.write('\n');
            // Write the Phenotypes Definitions
            writePhenotypeValueArray(writer);
            for (DSGeneMarker m : getMarkers()) {
                ((DSGeneMarker) m).write(writer);
                for (DSMicroarray ma : this) {
                    writer.write('\t' + ma.getMarkerValue(m.getSerial()).toString());
                }
                writer.write('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Exception in JMicroarrays.Write(): " + e);
        }
    }

    public boolean isSNPMicroarrays() {
        return ((type == snpType) || (type == alleleType));
    }

    public DSMicroarraySet clone(String newLabel, int newMarkerNo, int newMArrayNo) {
        CSMicroarraySet<DSMicroarray> newMicroarrays = null;
        newMicroarrays.setLabel(newLabel);
        newMicroarrays.setAbsPath(absPath);
        newMicroarrays.maskedSpots = 0;
        // Initialize the Marker statistics
        for (DSMicroarray ma : newMicroarrays) {
            CSMicroarray mArray = new CSMicroarray(ma.getSerial(), newMarkerNo, ma.getLabel(), null, null, false, type);
            newMicroarrays.add(ma.getSerial(), mArray);
        }
        // DSCriteria<DSBioObject> criteria = CSCriterionManager.getCriteria(this);
        // CSCriterionManager.setCriteria(this, criteria);
        CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
        manager.copyContexts((DSMicroarraySet<DSMicroarray>)this, newMicroarrays);
        // todo - watkin - This is not a deep copy, so changes to this vector will affect original and cloned sets.
        newMicroarrays.markerVector = markerVector;
        // todo - watkin - return null after all that work!?!?
        return null;
    }

    public int getType() {
        return type;
    }

    public CSMarkerVector getMarkerVector() {
        return markerVector;
    }

    public void initialize(int maNo, int mrkNo) {
    }

    public void parse(DSMutableMarkerValue marker, String value) {
    }

    public int getPlatformType() {
        return 0;
    }

    public void readFromFile(File file) {
    }

}
