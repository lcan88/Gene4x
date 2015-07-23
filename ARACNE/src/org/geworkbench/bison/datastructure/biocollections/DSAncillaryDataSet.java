package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

import java.io.File;

/**
 * Extends the notion of a data set by adding access to an ancillary data set file.
 *
 * @todo - watkin - What is the difference between these methods and {@link DSDataSet#getFile()} and {@link DSDataSet#setAbsPath(String)}? Can they be merged?
 * Also, do ancillary data sets have to be files or can the be resources of other types (such as a URL).
 */
public interface DSAncillaryDataSet<T extends DSBioObject> extends DSDataSet<T> {

    /**
     * Gets the data set file.
     *
     * @return
     */
    public File getDataSetFile();

    /**
     * Sets the data set file.
     *
     * @param file the file to associate with this data set.
     */
    public void setDataSetFile(File file);

    /**
     * Gets the parent data set for this ancillary data set.
     * @return
     */
    public DSDataSet<T> getParentDataSet();
}
