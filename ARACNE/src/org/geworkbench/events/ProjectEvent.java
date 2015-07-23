package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.builtin.projects.ProjectTreeNode;
import org.geworkbench.builtin.projects.DataSetSubNode;
import org.geworkbench.builtin.projects.DataSetNode;

import javax.swing.tree.TreeNode;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ProjectEvent extends Event {

    public static final String CLEARED = "Project Cleared";
    public static final String SELECTED = "Node Selected";

    private String value = null;
    private DSDataSet dataSet = null;
    private ProjectTreeNode node;

    public ProjectEvent(String message, DSDataSet dataSet, ProjectTreeNode node) {
        super(null);
        this.value = message;
        this.dataSet = dataSet;
        this.node = node;
    }

    public String getMessage() {
        return value;
    }

    public DSDataSet getDataSet() {
        return dataSet;
    }

    public DSDataSet getParent() {
        if (node != null) {
            if (node instanceof DataSetSubNode) {
                DataSetSubNode subNode = (DataSetSubNode) node;
                TreeNode parent = subNode.getParent();
                if (parent instanceof DataSetNode) {
                    return ((DataSetNode)parent).dataFile;
                }
            }
        }
        return null;
    }

}
