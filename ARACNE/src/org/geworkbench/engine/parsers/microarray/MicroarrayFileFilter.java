package org.geworkbench.engine.parsers.microarray;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class MicroarrayFileFilter extends FileFilter {
    protected Vector extensions = new Vector();

    public MicroarrayFileFilter() {
        add(".res");
        add(".");
    }

    public boolean accept(File f) {
        boolean result = false;
        for (Enumeration e = extensions.elements(); e.hasMoreElements();) {
            String extension = (String) e.nextElement();
            if (f.isDirectory() || f.getName().endsWith(extension)) {
                return true;
            }
        }

        return result;
    }

    public String getDescription() {
        return "Gene Expression Array Files";
    }

    public void add(String extension) {
        extensions.add(extension);
    }
}
