package org.geworkbench.components.selectors;

import cern.colt.matrix.DoubleMatrix2D;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */
public class SelectionTreeRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DoubleMatrix2D) {
            value = "Double Matrix";

        }
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        //            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        //            Object object = node.getUserObject();
        if (value instanceof DSPanel) {
            // This is one of the criterion values (a final panel)
            DSPanel panel = (DSPanel) value;

            if (panel.isActive()) {
                c.setForeground(Color.red);
            } else {
                c.setForeground(Color.black);
            }
        }
        return c;
    }

}
