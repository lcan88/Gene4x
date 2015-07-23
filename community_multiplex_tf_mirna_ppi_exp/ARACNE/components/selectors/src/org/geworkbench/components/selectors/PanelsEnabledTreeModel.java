package org.geworkbench.components.selectors;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

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
public class PanelsEnabledTreeModel <T extends DSNamed> implements TreeModel {

    private DSPanel<T> panel;

    private List treeModelListeners = new ArrayList();

    public PanelsEnabledTreeModel(DSPanel<T> panel) {
        this.panel = panel;
    }

    void printPanelInfo() {
        int panelSize = getChildCount(panel);
        System.out.println("Panel Size " + panelSize);
        for (int i = 0; i < panelSize; i++) {
            System.out.println(getChild(panel, i));
        }
    }

    public DSPanel<T> getPanel() {
        return panel;
    }

    public void setPanel(DSPanel<T> panel) {
        this.panel = panel;
    }

    public Object getRoot() {
        return panel;
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof DSPanel) {
            DSPanel parentPanel = (DSPanel) parent;
            int subPanels = 0;
            if (parentPanel.panels() != null) {
                subPanels = parentPanel.panels().size() - 1;
            }
            boolean skipSelection = true;
            if ((parentPanel == panel) || (parentPanel == panel.getSelection())) {
                skipSelection = false;
                subPanels += 1;
            }
            if (index < subPanels) {
                return parentPanel.panels().get(skipSelection ? (index + 1) : index);
            } else {
                int newIndex = index - subPanels;
                return parentPanel.getProperItem(newIndex);
            }
        } else {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        if (parent instanceof DSPanel) {
            DSPanel parentPanel = (DSPanel) parent;
            if (parentPanel.panels() == null) {
                return parentPanel.getNumberOfProperItems();
            }
            if ((parentPanel == panel) || (parentPanel == panel.getSelection())) {
                return parentPanel.panels().size() + parentPanel.getNumberOfProperItems();
            } else {
                // Skip selection panel
                return parentPanel.panels().size() + parentPanel.getNumberOfProperItems() - 1;
            }
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
        //        DSItemList subPanels = ((DSPanel)node).panels();
        //        if(subPanels == null || subPanels.size() < 1){
        //            return true;
        //        }else{
        //            return false;
        //        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{(newValue)});
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesChanged(e);
        }
    }

    /**
     * The only event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    public void fireTreeStructureChanged() {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{panel});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(e);
        }
    }

    public void firePanelChildrenChanged(DSPanel<T> changedPanel) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{panel, changedPanel});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(e);
        }
    }

    public void firePanelChanged(DSPanel<T> changedPanel) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{panel}, new int[]{panel.panels().indexOf(changedPanel)}, new Object[]{changedPanel});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesChanged(e);
        }
    }

    public void firePanelAdded(DSPanel<T> addedPanel, int newIndex) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{panel}, new int[]{newIndex}, new Object[]{addedPanel});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesInserted(e);
        }
    }

    public void firePanelRemoved(DSPanel<T> removedPanel, int oldIndex) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{panel}, new int[]{oldIndex}, new Object[]{removedPanel});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesRemoved(e);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        DSPanel parentPanel = (DSPanel) parent;
        if (child instanceof DSPanel) {
            int index = parentPanel.panels().indexOf(child);
            return index;
        } else {
            int index = parentPanel.indexOf(child);
            if ((parentPanel == panel) || (parentPanel == panel.getSelection())) {
                return index + parentPanel.panels().size();
            } else {
                // Skip selection panel
                if (parentPanel.panels() != null) {
                    return index + parentPanel.panels().size() - 1;
                } else {
                    return index;
                }
            }
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

}
