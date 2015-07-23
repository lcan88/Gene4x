package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.CSSequentialItemList;
import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.datastructure.properties.CSExtendable;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.util.DefaultIdentifiable;

import java.io.File;
import java.util.HashMap;

/**
 * An abstract implementation of {@link DSDataSet}.
 */
public class CSDataSet <T extends DSBioObject> extends CSSequentialItemList<T> implements DSDataSet<T> {
    protected boolean dirty = true;
    protected File file = null;
    protected String label = new String("Unnamed Data Set");
    protected String absPath = null;
    protected String compatibilityLabel = null; //default
    protected Resource maResource = null;

    /**
     * Used for the implementation of <code>Identifiable</code>.
     */
    protected org.geworkbench.bison.util.DefaultIdentifiable arraySetId = new DefaultIdentifiable();

    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    protected CSDescribable descriptions = new CSDescribable();

    /**
     * Used in the implementation of the <code>Extendable</code> interface.
     */
    protected CSExtendable extend = new CSExtendable();

    /**
     * Stores the experiment information (if any) associated with the microarray
     * set.
     */
    protected String experimentInfo = null;

    protected HashMap dataSetProperties = new HashMap();

    /**
     * Creates a new data set.
     */
    public CSDataSet() {
    }

    /**
     * Adds an object by tag to this data set.
     *
     * @param tag    the tag for the object.
     * @param object
     */
    public void addObject(Object tag, Object object) {
        dataSetProperties.remove(tag);
        dataSetProperties.put(tag, object);
        // Ensures that the value stored is not null
        dataSetProperties.get(tag);
    }

    /**
     * Gets the label for this data set.
     *
     * @return the data set's label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label for this data set.
     *
     * @param label the new label.
     */
    public void setLabel(String label) {
        this.label = new String(label);
    }

    /**
     * Gets a string representation for this data set.
     *
     * @return the same result as {@link #getLabel()}.
     */
    public String toString() {
        return label;
    }

    /**
     * Gets an object by the provided tag. If the object is not found <b>and</b> tag is a <code>Class</code> object,
     * then this method attempts to create a new instance of the given Class via a no-arg constructor. It then
     * associates this new object with the tag.
     *
     * @param tag the tag for the object sought.
     * @return the associated object.
     */
    public Object getObject(Object tag) {
        Object object = dataSetProperties.get(tag);
        if (object == null && tag instanceof Class) {
            try {
                object = ((Class) tag).newInstance();
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            }
            dataSetProperties.put(tag, object);
        }
        return object;
    }

    /**
     * Sets an annotation label.
     *
     * @param property the property to set.
     * @todo - watkin - Not sure what this does.
     * watkin - phased out
     */
//    public void setSelectedProperty(DSAnnotLabel property) {
//        DSCriteria<DSBioObject> criteria = CSCriterionManager.getCriteria(this);
//        DSClassCriteria classCriteria = CSCriterionManager.getClassCriteria(this);
//        criteria.setSelectedCriterion(property);
//        classCriteria.setSelectedCriterion(property);
//    }

    /**
     * Gets the activation status of the data set.
     *
     * @return <code>true</code> if active, <code>true</code> otherwise.
     */
    public boolean isActive() {
        return true;
    }

    /**
     * Gets a string describing the compatibility of this data set.
     *
     * @return the compatibility label.
     */
    public String getCompatibilityLabel() {
        return compatibilityLabel;
    }

    /**
     * Gets the path to the file associated with this data set.
     *
     * @return the path as a String.
     * @todo - watkin - This should be phased out and replaced with the resource-management methods.
     */
    public String getPath() {
        return absPath;
    }

    /**
     * The underlying file associated with this data set.
     *
     * @return the file for this data set.
     * @todo - watkin - This should be phased out and replaced with the resource-management methods.
     */
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * The name of data set.
     *
     * @return the data set name.
     */
    public String getDataSetName() {
        return label;
    }

    /**
     * Associates the given resource with this data set.
     *
     * @param resource the resource to associate with this data set.
     */
    public void setResource(Resource resource) {
        maResource = resource;
    }

    /**
     * Disassociates the specified resource from this data set.
     *
     * @param resource the resource to remove.
     */
    public void removeResource(Resource resource) {
        maResource = null;
    }

    /**
     * Reads from the resource associated with this data set.
     */
    public void readFromResource() {
        // @todo - watkin - not implemented anywhere in the class heirarchy!
    }

    /**
     * Writes to the resource associated with this data set.
     */
    public void writeToResource() {
        // @todo - watkin - not implemented anywhere in the class heirarchy!
    }

    /**
     * Return the dirty flag.
     *
     * @return <code>true</code> if dirty, <code>false</code> otherwise.
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set the underlying object's dirty flag to the designated value.
     *
     * @param flag
     */
    public void setDirty(boolean flag) {
        dirty = flag;
    }

    /**
     * Sets the path to the absolute path given.
     *
     * @param absPath the absolute (non-relative) path.
     * @todo - watkin - This should be phased out and replaced with the resource-management methods.
     */
    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }


    public String getID() {
        return arraySetId.getID();
    }

    public void setID(String id) {
        // @todo - watkin - the use of "MicroarraySet" may be harmless, but is misleading.
        arraySetId.setID(id, "MicroarraySet");
    }

    /**
     * Append a new description to the list of existing descriptions.
     *
     * @param description the new description to be added.
     */
    public void addDescription(String description) {
        descriptions.addDescription(description);
    }

    /**
     * Get all available descriptions in the form of an array of strings.
     *
     * @return an array containing all descriptions.
     */
    public String[] getDescriptions() {
        return descriptions.getDescriptions();
    }

    /**
     * Remove the designated description.
     *
     * @param description The description to be removed.
     */
    public void removeDescription(String description) {
        descriptions.removeDescription(description);
    }

    /**
     * Adds a new name-value mapping.
     *
     * @param name
     * @param value
     */
    public void addNameValuePair(String name, Object value) {
        extend.addNameValuePair(name, value);
    }

    /**
     * Returns all values mapped to the designated name.
     *
     * @param name
     * @return All values associated with the argument <code>name</code>.
     */
    public Object[] getValuesForName(String name) {
        return extend.getValuesForName(name);
    }

    /**
     * Forces that the <code>name</code> be associated with at most one value.
     * Attempting to add a name-value pair for a 'name' that already has an
     * associate value, results in old name-value association be replaced by the
     * new one.
     * <p/>
     * Calling this method for an existing 'name' results in all the associated
     * name-value pairs to be cleared.
     *
     * @param name The 'name' upon which uniqueness of value will be enforced.
     */
    public void forceUniqueValue(String name) {
        extend.forceUniqueValue(name);
    }

    /**
     * Conjugate method for <code>forceUniqueValue</code>. Enables a 'name' to
     * accept mulptiple name-value asscociations.
     *
     * @param name The 'name' for which multiplicity of value is enabled.
     */
    public void allowMultipleValues(String name) {
        extend.allowMultipleValues(name);
    }

    /**
     * Check if <code>forceUniqueValue()</code> is in effect for the designated
     * name.
     *
     * @param name
     * @return
     */
    public boolean isUniqueValue(String name) {
        return extend.isUniqueValue(name);
    }

    /**
     * Removes all values associated with the given <code>name</code>.
     *
     * @param name
     */
    public void clearName(String name) {
        extend.clearName(name);
    }

    /**
     * Returns the experiment information associated with the microarray set.
     * What constitutes "experiment information" varies, depending on the
     * input source where the microarray set came from. E.g., for Affy input
     * files it is the file preamble (the part just before the data lines which
     * provides various experiment execution parameters) that becomes the experiment
     * information.
     *
     * @return
     */
    public String getExperimentInformation() {
        return experimentInfo;
    }

    /**
     * Set the experiment information.
     *
     * @param experimentInformation
     */
    public void setExperimentInformation(String experimentInformation) {
        experimentInfo = experimentInformation;
    }

    public void writeToFile(String fileName) {
    }
}