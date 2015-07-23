package org.geworkbench.engine.parsers.genotype;

import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.config.PluginRegistry;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GenotypicFileFilter extends FileFilter {
    Vector fileExt = new Vector();

    public GenotypicFileFilter() throws Exception {
        //System.out.println("@@@@@@@LoadData==>inner class constructor SequenceFileFilter");
        PluginDescriptor descriptor = PluginRegistry.getPluginDescriptor("genotypicFilter");
        if (descriptor == null) {
            throw new Exception();
        }
        GenotypicFileFormat format = (GenotypicFileFormat) descriptor.getPlugin();
        if (format != null) {
            String[] extensions = format.getFileExtensions();
            int length = extensions.length;
            for (int i = 0; i < length; i++) {
                fileExt.add(extensions[i]);
                //System.out.println("@@@@@@@LoadData==>added" + extensions[i] + "SequenceFileFilter");
            }
        }
    }

    public String getDescription() {
        // System.out.println("@@@@@@@LoadData==>inner class SequenceFileFilter: getDescription()");
        return "Genotypic Data Files";
    }

    public boolean accept(File f) {
        boolean returnVal = false;
        for (Enumeration e = fileExt.elements(); e.hasMoreElements();) {
            String extension = (String) e.nextElement();
            if (f.isDirectory() || f.getName().endsWith(extension)) {
                return true;
            }
        }
        return returnVal;
    }
}
