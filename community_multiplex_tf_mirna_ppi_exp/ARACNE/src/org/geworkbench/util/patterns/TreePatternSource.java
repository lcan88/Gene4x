package org.geworkbench.util.patterns;

import javax.swing.tree.DefaultTreeModel;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: This interface defines a source for getting patterns from a
 * tree.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface TreePatternSource extends org.geworkbench.util.patterns.DataSource {

    /**
     * Get the number of Nodes in the source.
     * Note: The result of this calls may vary with time.
     */

    public int getNumberOfNodes();


    /**
     * Attach childrens' root of the source to this root node.
     */
    public void getRoot(DefaultTreeModel visitorModel);
}
