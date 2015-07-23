package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>JTree</code> renderer to be used to render the Project Tree
 * @todo Phase out.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class TreeNodeRenderer extends DefaultTreeCellRenderer {
    /**
     * Current <code>MicroarraySetNode</code> selection
     */
    public MicroarraySetNode microarraySetNodeSelection = null;
    /**
     * Current <code>ProjectNodeOld</code> selection
     */
    public ProjectNode projectNodeSelection = null;
    /**
     * Current <code>ImageNode</code> selection
     */
    public ImageNode imageNodeSelection = null;
    /**
     * <code>ImageIcon</code> for display on Workspace Nodes
     */
    ImageIcon workspaceIcon = null;
    /**
     * <code>ImageIcon</code> for display on Project Nodes
     */
    ImageIcon projectIcon = null;
    /**
     * <code>ImageIcon</code> for display on Microarray Nodes
     */
    ImageIcon microarrayIcon = null;
    /**
     * <code>ImageIcon</code> for display on Phenotype Nodes
     */
    ImageIcon phenotypeIcon = null;
    /**
     * <code>ImageIcon</code> for display on Image Nodes
     */
    ImageIcon imageIcon = null;

    /**
     * Default Constructor
     */
    ProjectSelection selection = null;

    public TreeNodeRenderer(ProjectSelection selection) {
        this.selection = selection;
        workspaceIcon = new ImageIcon(Icons.class.getResource("workspace16x16.gif"));
        projectIcon = new ImageIcon(Icons.class.getResource("project16x16.gif"));
        microarrayIcon = new ImageIcon(Icons.class.getResource("chip16x16.gif"));
        phenotypeIcon = new ImageIcon(Icons.class.getResource("Phenotype16x16.gif"));
        imageIcon = new ImageIcon(Icons.class.getResource("image16x16.gif"));
    }

    /**
     * Tests if all Node selections are null
     *
     * @return
     */
    public boolean areNodeSelectionsCleared() {
        if ((microarraySetNodeSelection == null) && (projectNodeSelection == null) && (imageNodeSelection == null))
            return true;
        return false;
    }

    /**
     * Clears all Node selections
     */
    public void clearNodeSelections() {
        microarraySetNodeSelection = null;
        projectNodeSelection = null;
        imageNodeSelection = null;
    }

    /**
     * <code>Component</code> used for remdering on the <code>ProjectTree</code>
     * based on type and selection
     *
     * @param tree     the <code>ProjectTree</code>
     * @param value    the node to be rendered
     * @param sel      if selected
     * @param expanded if node expanded
     * @param leaf     if leaf
     * @param row      row in the tree model
     * @param hasFocus if the node has focus
     * @return the <code>Component</code> to be used for rendering
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setForeground(Color.black);
        if (value == tree.getModel().getRoot()) {
            setIcon(Icons.WORKSPACE_ICON);
            setToolTipText("This is the workspace.");
        } else if (value.getClass() == ProjectNode.class) {
            setIcon(Icons.PROJECT_ICON);
            setToolTipText("This is a project folder.");
        } else {
            if (value.getClass() == DataSetNode.class) {
                DSDataSet df = ((DataSetNode) value).dataFile;
                ImageIcon icon = ProjectPanel.getIconForType(df.getClass());
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(Icons.MICROARRAYS_ICON);
                }
                String[] descriptions = df.getDescriptions();
                if (df != null && (df instanceof DSMicroarraySet)){
                    setToolTipText("# of microarrays: " +
                            ((DSMicroarraySet) df).size() + ",   " +
                            "# of markers: " +
                            ((DSMicroarraySet) df).getMarkers().size() + "\n");
                }
                else if (descriptions.length > 0) {
                    setToolTipText(descriptions[0]);
                } else {
                    setToolTipText("This is an undefined Data set");
                }
            } else if (value.getClass() == DataSetSubNode.class) {
                DSAncillaryDataSet adf = ((DataSetSubNode) value)._aDataSet;
                ImageIcon icon = ProjectPanel.getIconForType(adf.getClass());
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(Icons.DATASUBSET_ICON);
                }
                String[] descriptions = adf.getDescriptions();
                if (descriptions.length > 0) {
                    setToolTipText("This is a Ancillary Data set: " + descriptions[0]);
                } else {
                    setToolTipText("This is an undefined Ancillary Data set");
                }
            } else if (value.getClass() == ImageNode.class) {
                setIcon(Icons.IMAGE_ICON);
            }
        }
        // watkin - Using a light gray is confusing and hard to read, using black for all nodes.
//        if (value == selection.getSelectedNode()) {
//            setForeground(Color.black);
//        }
        return this;
    }

}
