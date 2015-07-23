package org.geworkbench.engine.parsers;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @version 1.0
 * @xuegong wang
 */

public class ExpressionMatrixFileFormat extends DataSetFileFormat {

    String[] maExtensions = {"exp", "res"};
    ExpressionResource resource = new ExpressionResource();
    AffyFilter maFilter = null;

    public ExpressionMatrixFileFormat() {
        formatName = "Affymetrix File Matrix 1";
        maFilter = new AffyFilter();
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
        return (DSDataSet) getMArraySet(file);
    }

    public DSMicroarraySet getMArraySet(File file) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        try {
            maSet.read(file);
        } catch (Exception e) {
        }
        if (maSet.loadingCancelled)
            return null;
        return maSet;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
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
            return "Affymetrix File Matrix";
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
