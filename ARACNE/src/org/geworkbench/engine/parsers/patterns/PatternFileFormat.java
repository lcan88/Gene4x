package org.geworkbench.engine.parsers.patterns;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.engine.parsers.sequences.SequenceResource;
import org.geworkbench.util.patterns.PatternDB;

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
 * @author not attributable
 * @version 1.0
 */

public class PatternFileFormat extends DataSetFileFormat {

    String[] patExtensions = {"pattern", "pat", "txt"};
    SequenceResource resource = new SequenceResource();
    PatternFilter patternFilter = null;

    public PatternFileFormat() {
        formatName = "Pattern(s) File";
        patternFilter = new PatternFilter();
        Arrays.sort(patExtensions);
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
        return patExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet getDataFile(File file) {
        org.geworkbench.util.patterns.PatternDB patternDB = new PatternDB(file, null);
        if (patternDB.read(file)) {
            return patternDB;
        }
        return null;
    }

    public DSDataSet getDataFile(File[] files) {
        return null;
    }


    public DSMicroarraySet getMArraySet(File file) {
        return null;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
    }

    public FileFilter getFileFilter() {
        return patternFilter;
    }

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    public class PatternFilter extends FileFilter {

        public String getDescription() {
            return "Pattern Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < patExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(patExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
