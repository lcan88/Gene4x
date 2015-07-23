package org.geworkbench.components.selectors;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LabelledMutableTreeNode extends DefaultMutableTreeNode {
    private String label = null;

    public LabelledMutableTreeNode(Object obj) {
        super(obj);
    }

    public LabelledMutableTreeNode(Object obj, String label) {
        super(obj);
        this.label = label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        if (label != null) {
            return label;
        } else {
            return super.toString();
        }
    }

}
