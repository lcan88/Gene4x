package org.geworkbench.engine.parsers;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * <p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Exports files into a spreadsheet-like format, appropriate for use by Eisen's
 * Cluster software.
 */
public class ClusterExportFormat extends ExportFormat {
    /**
     * The file extensions expected for Cluster files.
     */
    String[] clusterExtensions = {"txt"};
    /**
     * <code>FileFilter</code> for gating Cluster files, based on their extension.
     */
    ClusterFileFilter clusterFileFilter = null;

    /**
     * Default constructor. Will be invoked by the framework when the
     * <code>&lt;plugin&gt;</code> line for this export transformer is encountered
     * in the application configuration file.
     */
    public ClusterExportFormat() {
        formatName = "Cluster Files";   // Setup the display name for the format.
        clusterFileFilter = new ClusterFileFilter();
    }

    /**
     * @return The file extensions defined for Cluster data files.
     */
    public String[] getFileExtensions() {
        return clusterExtensions;
    }

    /**
     * @return The <code>FileFilter</code> defined for Cluster files.
     */
    public FileFilter getFileFilter() {
        return clusterFileFilter;
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
            String outLine = "MarkerID";
            for (int i = 0; i < maSet.size(); ++i)
                outLine = outLine.concat("\t" + maSet.get(i).getLabel());
            writer.write(outLine);
            writer.newLine();
            // Proceed to write one marker at a time
            for (int i = 0; i < maSet.size(); ++i) {
                outLine = maSet.get(i).getLabel();
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
     * to export to the Cluster format. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #clusterExtensions}.
     */
    class ClusterFileFilter extends FileFilter {
        public String getDescription() {
            return formatName;
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < clusterExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(clusterExtensions[i])) {
                    return true;
                }

            return returnVal;
        }

    }

}

