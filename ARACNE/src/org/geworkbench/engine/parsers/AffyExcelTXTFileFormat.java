package org.geworkbench.engine.parsers;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AffyExcelTXTFileFormat

        extends DataSetFileFormat {

    String[] maExtensions = {"xls", "txt"};
    AffyExcelTXTResource resource = new AffyExcelTXTResource();
    AffyFilter maFilter = null;

    public AffyExcelTXTFileFormat() {
        formatName = "Affymetrix Excel or txt format";
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
        File[] fs = new File[1];
        fs[0] = file;
        return getDataFile(fs);
    }

    public DSMicroarraySet getMArraySet(File file) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        try {
            maSet.read(file);
        } catch (Exception e) {

        }
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
    public DSDataSet getDataFile(final File[] files) {
        DSDataSet data = null;
        int option = 0;
        if (files[0].getName().endsWith(".xls")) {
            option = JOptionPane.showConfirmDialog(null, "Excel file input will be very slow and requires more resource.\n" + "It is highly recommanded that you use tab delimated text format.\n"

                    + "Continue anyway?", "Recommendation", JOptionPane.CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        }
        if (option == 0) {

            String filename = files[0].getName();// JOptionPane.showInputDialog(null, "Please enter the name of the dataset:");
            if (filename != null) {
                File datafile = new File(filename);
                org.geworkbench.util.microarrayutils.MatrixCreater.readFile(files, datafile);
                data = (DSDataSet) getMArraySet(datafile);
            }

        }

        return data;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class AffyFilter extends FileFilter {

        public String getDescription() {
            return "Affy Excel or txt data File";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i) {
                if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
                    return true;
                }
            }
            return returnVal;
        }
    }
}
