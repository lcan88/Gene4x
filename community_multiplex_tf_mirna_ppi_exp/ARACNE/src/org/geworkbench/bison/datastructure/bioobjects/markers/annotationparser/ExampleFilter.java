package org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ExampleFilter extends FileFilter {
    ArrayList extensions = new ArrayList();
    String description = new String();

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        for (int i = 0; i < extensions.size(); i++) {
            if (f.getName().endsWith((String) extensions.get(i))) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public void addExtension(String ext) {
        extensions.add(ext);
    }
}
