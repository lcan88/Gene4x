package org.geworkbench.bison.datastructure.bioobjects.structure;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.io.File;

/**
 * User: mhall
 * Date: Mar 13, 2006
 * Time: 11:43:22 AM
 */
public class CSProteinStructure extends CSAncillaryDataSet implements DSProteinStructure {

    File dataFile = null;

    public CSProteinStructure(DSDataSet parent, String label) {
        super(parent, label);
    }

    public File getDataSetFile() {
        return dataFile;
    }

    public void setDataSetFile(File file) {
        dataFile = file;
    }
}
