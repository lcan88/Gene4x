package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class ProjectNode extends ProjectTreeNode implements Serializable {
    DataSetNode dataSetNodeSelection = null;
    DataSetSubNode dataSetSubNodeSelection = null;

    /**
     * current <code>MicroarraySetNode</code> selection
     */
    MicroarraySetNode microarraySetNodeSelection = null;

    ProjectNode(Object nodeName) {
        setUserObject(nodeName);
    }

    public DSDataSet getSelectedDataSet() {
        return dataSetNodeSelection.dataFile;
    }

    public void addNode(TreeNode node, Object object) {
        //this.add((MutableTreeNode)node);
        DataSetNode maNode = new DataSetNode((DSDataSet) object);
        int childCount = dataSetNodeSelection.getChildCount();
        // System.out.println(childCount);
        try {
            dataSetNodeSelection.insert((MutableTreeNode) node, childCount);
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            aiobe.printStackTrace();
        }
    }

    /**
     * Gets the <code>MicroarraySet</code> in the current
     * <code>MicroarraySetNode</code> selection
     *
     * @return <code>MicroarraySet</code> in the current
     *         <code>MicroarraySetNode</code> selection
     */
    public DSMicroarraySet getSelectedMicroarray() {
        return microarraySetNodeSelection.getMicroarraySet();
    }

    /**
     * To keep a track of the current <code>MicroarraySetNode</code> selection
     */
    public class JMAProject implements Serializable {
        MicroarraySetNode microarraySetSelection = null;
    }
}
