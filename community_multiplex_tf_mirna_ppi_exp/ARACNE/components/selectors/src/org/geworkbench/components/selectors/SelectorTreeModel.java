package org.geworkbench.components.selectors;

import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSSequential;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John Watkinson
 */
public class SelectorTreeModel<T extends DSSequential> implements TreeModel {

    private DSAnnotationContext<T> context;
    private List treeModelListeners = new ArrayList();

    public SelectorTreeModel(DSAnnotationContext<T> context) {
        this.context = context;
    }

    public Object getRoot() {
        return context;
    }

    public DSAnnotationContext<T> getContext() {
        return context;
    }

    public void setContext(DSAnnotationContext<T> context) {
        this.context = context;
    }

    public Object getChild(Object parent, int index) {
        if (parent == context) {
            return context.getLabel(index);
        } else if (parent instanceof String) {
            String label = (String) parent;
            DSPanel<T> panel = context.getItemsWithLabel(label);
            return panel.get(index);
        } else {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        if (parent == context) {
            return context.getNumberOfLabels();
        } else if (parent instanceof String) {
            String label = (String) parent;
            DSPanel<T> panel = context.getItemsWithLabel(label);
            return panel.size();
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {
        if (node == context) {
            return (context.getNumberOfLabels() == 0);
        } else if (node instanceof String) {
            String label = (String) node;
            DSPanel<T> panel = context.getItemsWithLabel(label);
            return (panel.size() == 0);
        } else {
            return true;
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{(newValue)});
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesChanged(e);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == context) {
            return context.indexOfLabel((String) child);
        } else if (parent instanceof String) {
            String label = (String) parent;
            T item = (T) child;
            DSPanel<T> panel = context.getItemsWithLabel(label);
            return panel.indexOf(item);
        } else {
            return 0;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    // Notifications of change

    // Tree-wide change
    public void fireTreeStructureChanged() {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{context});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(e);
        }
    }

    // Contents of label changed
    public void fireLabelItemsChanged(String label) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{context, label});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeStructureChanged(e);
        }
    }

    // Label itself changed (say, due to activation)
    public void fireLabelChanged(String label) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{context}, new int[]{context.indexOfLabel(label)}, new Object[]{label});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesChanged(e);
        }
    }

    public void fireLabelAdded(String label, int newIndex) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{context}, new int[]{newIndex}, new Object[]{label});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesInserted(e);
        }
    }

    public void fireLabelRemoved(String label, int oldIndex) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{context}, new int[]{oldIndex}, new Object[]{label});
        int len = treeModelListeners.size();
        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.get(i)).treeNodesRemoved(e);
        }
    }

}
