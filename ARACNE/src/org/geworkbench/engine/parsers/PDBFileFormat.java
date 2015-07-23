package org.geworkbench.engine.parsers;

import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.structure.DSProteinStructure;
import org.geworkbench.bison.datastructure.bioobjects.structure.CSProteinStructure;

import javax.swing.filechooser.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads PDB structure format.
 */
public class PDBFileFormat extends DataSetFileFormat {

    String[] maExtensions = {"pdb"};
    ExpressionResource resource = new ExpressionResource();
    PDBFileFormat.AffyFilter maFilter = null;

    public PDBFileFormat() {
        formatName = "PDB Structure File";
        maFilter = new PDBFileFormat.AffyFilter();
        Arrays.sort(maExtensions);
    }

    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFileName(file.getName());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }

    public String[] getFileExtensions() {
        return maExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet getDataFile(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if (index != -1) {
            name = name.substring(0, index);
        }
        DSProteinStructure dataSet = new CSProteinStructure(null, name);
        dataSet.setFile(file);
        return dataSet;
    }

    public DSMicroarraySet getMArraySet(File file) {
        return null;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new UnsupportedOperationException("Method getOptions() not yet implemented.");
    }

    public FileFilter getFileFilter() {
        return maFilter;
    }

    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet getDataFile(File[] files) {
        return null;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class AffyFilter extends FileFilter {

        public String getDescription() {
            return "PDB File Format";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
