package org.geworkbench.events;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * An event for updating hiearachical analysis.
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $Author: watkin $
 * @version 1.0
 */

public class HierarchicalProgressEvent extends ProgressChangeEvent {
    DefaultMutableTreeNode parent = null;
    DefaultMutableTreeNode child = null;

    public HierarchicalProgressEvent(int patternFound, DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        super(patternFound);
        this.parent = parent;
        this.child = child;
    }

    public DefaultMutableTreeNode getParent() {
        return this.parent;
    }

    public DefaultMutableTreeNode getChild() {
        return this.child;
    }

}
