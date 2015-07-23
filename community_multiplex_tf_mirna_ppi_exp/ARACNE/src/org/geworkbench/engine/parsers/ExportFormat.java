package org.geworkbench.engine.parsers;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Base class for exporting <code>MicroarraySet</code> objects to alternative
 * file formats.
 * <p/>
 * The mechanism that the application uses in order to display to the user the
 * available file export options is similar to that employed for the input
 * file formats (described in the class-level documentation for
 * <code>FileFormat</code>). The only difference is the name of the extension
 * point, which in this case is "export-formats".
 */
public abstract class ExportFormat {
    /**
     * The display name of the export format.
     */
    protected String formatName = null;

    /**
     * @return The format name.
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * The main method of this class. Implements the transformation and
     * storage of a microarray set to the export format encaplulated by this
     * <code>ExportFormat</code> object.
     *
     * @param maSet The microarray set to be exported.
     * @param file  The file where the results will be stored.
     * @return <code>true</code> if the export succeeded, <code>false</code>
     *         otherwise.
     */
    public abstract boolean save(DSMicroarraySet<DSMicroarray> maSet, File file);

    public abstract String[] getFileExtensions();

    /**
     * Rreturn an (optional) <code>FileFilter</code> to be used in gating the
     * files offered to the user for selection. Useful, e.g., when the
     * files of the format at hand have predefined extensions.
     */
    public FileFilter getFileFilter() {
        return null;
    }

}

