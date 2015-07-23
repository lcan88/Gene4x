package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.events.ProjectEvent;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ProjectSelection {
    private ProjectNode selectedProjectNode = null;
    private DataSetNode selectedDataSetNode = null;
    private DataSetSubNode selectedDataSetSubNode = null;
    private ProjectTreeNode selectedNode = null;
    private ProjectTreeNode menuNode = null; // The node that a popup menu is invoked on
    private ProjectPanel panel;

    public ProjectSelection(ProjectPanel panel) {
        this.panel = panel;
    }

    /**
     * Returns whether the selections have all been cleared
     *
     * @return
     */
    public boolean areNodeSelectionsCleared() {
        return (selectedProjectNode == null);
    }

    /**
     * Clears the selections and broadcasts the event
     */
    public void clearNodeSelections() {
        selectedProjectNode = null;
        selectedDataSetNode = null;
        selectedDataSetSubNode = null;
        selectedNode = null;
        menuNode = null;
        throwEvent("receiveProjectSelection", "Project Cleared");
    }


    // Access to various selection variables
    public ProjectNode getSelectedProjectNode() {
        return selectedProjectNode;
    }

    public DataSetNode getSelectedDataSetNode() {
        return selectedDataSetNode;
    }

    public DataSetSubNode getSelectedDataSetSubNode() {
        return selectedDataSetSubNode;
    }

    public ProjectTreeNode getSelectedNode() {
        return selectedNode;
    }

    public ProjectTreeNode getMenuNode() {
        return menuNode;
    }

    public void setMenuNode(ProjectTreeNode node) {
        menuNode = node;
    }

    public DSDataSet getDataSet() {
        if (selectedDataSetNode != null) {
            return selectedDataSetNode.dataFile;
        } else {
            return null;
        }
    }

    public DSAncillaryDataSet getDataSubSet() {
        if (selectedDataSetSubNode != null) {
            return selectedDataSetSubNode._aDataSet;
        } else {
            return null;
        }
    }

    /**
     * Finds the project node associated with this node of the given class
     *
     * @param parentPath
     * @return
     */
    public ProjectTreeNode getNodeOfClass(ProjectTreeNode node, Class aClass) {
        while (!(aClass.isInstance(node))) {
            node = (ProjectTreeNode) node.getParent();
            if (node == null) {
                return null;
            }
        }
        return (ProjectTreeNode) node;
    }

    /**
     * Assigns a new selection node
     *
     * @param pNode
     * @param node
     */
    public void setNodeSelection(ProjectTreeNode node) {
        if (selectedNode != node) {
            selectedNode = node;
            menuNode = node;
            selectedProjectNode = (ProjectNode) getNodeOfClass(node, ProjectNode.class);
            boolean subNode = false;
            if (node instanceof DataSetNode) {
                selectedDataSetNode = (DataSetNode) node;
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetNode.dataFile);
            } else if (node instanceof DataSetSubNode) {
                selectedDataSetSubNode = (DataSetSubNode) node;
                selectedDataSetNode = (DataSetNode) getNodeOfClass(node, DataSetNode.class);
                GeawConfigObject.getGuiWindow().setVisualizationType(selectedDataSetSubNode._aDataSet);
                subNode = true;
            } else if (node instanceof ProjectNode && node.getChildCount() == 0) {
                selectedDataSetNode = null;
                selectedDataSetSubNode = null;
                GeawConfigObject.getGuiWindow().setVisualizationType(null);
                throwEvent("receiveProjectSelection", ProjectEvent.CLEARED);
            }

            checkProjectNode();
            if (subNode) {
                throwSubNodeEvent("receiveProjectSelection");
            } else {
                throwEvent("receiveProjectSelection", ProjectEvent.SELECTED);
            }
        }
    }

    /**
     * Checks that the selections are correctly assigned at the project level
     */
    private void checkProjectNode() {
        if (selectedProjectNode == null) {
            selectedDataSetNode = null;
            selectedDataSetSubNode = null;
        } else {
            checkDataSetNode();
        }
    }

    /**
     * Checks that the selections are correctly assigned at the DataSet level
     */
    private void checkDataSetNode() {
        if (selectedDataSetNode == null) {
            selectedDataSetSubNode = null;
        } else {
            if (selectedDataSetNode.getParent() != selectedProjectNode) {
                selectedDataSetNode = null;
                selectedDataSetSubNode = null;
            } else {
                checkDataSetSubNode();
            }
        }
    }

    /**
     * Checks that the selections are correctly assigned at the DataSetSub level
     */
    private void checkDataSetSubNode() {
        if (selectedDataSetSubNode != null) {
            if (selectedDataSetSubNode.getParent() != selectedDataSetNode) {
                selectedDataSetSubNode = null;
            }
        }
    }

    /**
     * Throws the corresponding message event
     *
     * @param message
     */
    public void throwEvent(String method, String message) {
        // Notify all listeners of the change in selection
        DSMicroarraySet maSet = null;
        if (selectedDataSetNode != null) {
            if (selectedDataSetNode.dataFile instanceof DSMicroarraySet) {
                maSet = (DSMicroarraySet) selectedDataSetNode.dataFile;
                panel.publishProjectEvent(new ProjectEvent(message, maSet, selectedDataSetNode));
            } else {
                panel.publishProjectEvent(new ProjectEvent(message, selectedDataSetNode.dataFile, selectedDataSetNode));
            }
            panel.sendCommentsEvent(selectedDataSetNode);
        }
    }

    public void throwSubNodeEvent(String message) {
        if ((selectedDataSetSubNode != null) && (selectedDataSetSubNode._aDataSet != null)) {
            panel.publishProjectEvent(new ProjectEvent(message, selectedDataSetSubNode._aDataSet, selectedDataSetSubNode));
        }
    }
}
