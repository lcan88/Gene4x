package org.geworkbench.engine.parsers;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Saroja Hanasoge
 * @version 1.0
 */

public class ExampleFileFilter extends FileFilter {
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
