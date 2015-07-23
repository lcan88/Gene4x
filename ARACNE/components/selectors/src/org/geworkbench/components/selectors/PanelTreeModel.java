package org.geworkbench.components.selectors;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author John Watkinson
 */
public class PanelTreeModel <T extends DSNamed> implements TreeModel {

    DSPanel<T> panel;

    public PanelTreeModel(DSPanel<T> panel) {
        this.panel = panel;
    }

    public PanelTreeModel() {
    }

    public DSPanel<T> getPanel() {
        return panel;
    }

    public void setPanel(DSPanel<T> panel) {
        this.panel = panel;
    }

    //// BEGIN TreeModel Interface

    public Object getRoot() {
        return panel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getChild(Object parent, int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getChildCount(Object parent) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isLeaf(Object node) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIndexOfChild(Object parent, Object child) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    //// END TreeModel Interface
}
