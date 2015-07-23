package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.util.RandomNumberGenerator;

import javax.swing.*;
import java.io.File;
import java.util.Vector;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public class ImageData extends CSAncillaryDataSet {

    private File imageFile = null;
    private ImageIcon image = null;
    private Vector descriptions = new Vector();
    private String name = null;
    private String id = null;
    private boolean isDirty = false;

    public ImageData(File image) {
        super(null, "Image");
        imageFile = image;
        id = RandomNumberGenerator.getID();
    }

    public File getDataSetFile() {
        return imageFile;
    }

    public void setImageIcon(ImageIcon icon) {
        image = icon;
    }

    public ImageIcon getImageIcon() {
        return image;
    }

    public void setDataSetFile(File image) {
        imageFile = image;
    }

    public boolean equals(Object ads) {
        if (ads instanceof DSAncillaryDataSet) {
            return getID().equalsIgnoreCase(((DSAncillaryDataSet) ads).getID());
        } else {
            return false;
        }
    }

    public String getDataSetName() {
        return imageFile.getName();
    }

    public File getFile() {
        return imageFile;
    }

    public void writeToFile(String fileName) {
        /**@todo Implement this org.geworkbench.builtin.projects.DataSet method*/
        throw new java.lang.UnsupportedOperationException("Method writeToFile() not yet implemented.");
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

}
