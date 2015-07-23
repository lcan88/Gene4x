package org.geworkbench.engine.parsers;


import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MatrixExportFormat extends org.geworkbench.engine.parsers.ExportFormat {

    /**
     * The file extensions expected for Microarray matrix files.
     */
    String[] matrixExtensions = {"exp"};

    /**
     * <code>FileFilter</code> for gating Matrix files, based on their extension.
     */
    MatrixFileFilter matrixFileFilter = null;

    public MatrixExportFormat() {
        formatName = "Matrix Files";
        matrixFileFilter = new MatrixFileFilter();
    }

    /**
     * @return The file extensions defined for Cluster data files.
     */
    public String[] getFileExtensions() {
        return matrixExtensions;
    }

    /**
     * @return The <code>FileFilter</code> defined for Cluster files.
     */
    public FileFilter getFileFilter() {
        return matrixFileFilter;
    }


    public boolean save(DSMicroarraySet<DSMicroarray> maSet, File file) {
        if (maSet == null || file == null)
            return false;
        // Make sure that the file exists and we can write to it
        try {
            file.createNewFile();
            if (!file.canWrite())
                return false;
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // start processing the data.
            // Start with the header line, comprising the array names.
            String outLine = "AffyID" + "\t" + "Annotation";
            for (int i = 0; i < maSet.size(); ++i)
                outLine = outLine.concat("\t" + maSet.get(i).getLabel());
            writer.write(outLine);
            writer.newLine();
            // Proceed to write one marker at a time
            for (int i = 0; i < maSet.size(); ++i) {
                outLine = maSet.get(i).getLabel();
                outLine = outLine.concat("\t" + maSet.get(i).getLabel());
                for (int j = 0; j < maSet.size(); ++j) {
                    DSMarkerValue mv = maSet.get(j).getMarkerValue(i);
                    outLine = outLine.concat("\t" + (mv.isMissing() ? "NA" : String.valueOf(mv.getValue())));
                }

                writer.write(outLine);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to export to the Matrix format. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #matrixExtensions}.
     */
    class MatrixFileFilter extends FileFilter {

        public String getDescription() {
            return formatName;
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < matrixExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(matrixExtensions[i])) {
                    return true;
                }

            return returnVal;
        }
    }
}
