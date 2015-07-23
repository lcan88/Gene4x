package org.geworkbench.util.patterns;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 *          <p/>
 *          $Id: PatternCellRenderer.java,v 1.1.1.1 2005/07/28 22:36:51 watkin Exp $
 */

public class PatternCellRenderer extends DefaultListCellRenderer {
    public PatternCellRenderer() {
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        c.setForeground(org.geworkbench.util.patterns.PatternOperations.getPatternColor(value.hashCode()));
        return c;
    }
}
