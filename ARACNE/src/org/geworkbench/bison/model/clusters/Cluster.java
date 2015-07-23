package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.properties.DSIdentifiable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Definition of a notion of Clusters that are obtained from analyses methods
 * like Hierarchical Clustering and Self Organizing Maps
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface Cluster extends Iterator, DSIdentifiable {
    /**
     * Returns the <code>Cluster</code> node that is the parent of this
     * cluster.
     */
    public Cluster getParent();

    /**
     * Adds <code>newCode</code> as a child of this node.
     */
    public void addNode(Cluster newCluster);

    /**
     * Returns <code>true</code> if this node has no children.
     */
    public boolean isLeaf();

    /**
     * Returns the children nodes of this node.
     */
    public Cluster[] getChildrenNodes();

    /**
     * Returns the number of children for this node.
     */
    public int getNodesCount();

    /**
     * Returns only the children nodes that are leafs.
     */
    public List<Cluster> getLeafChildren();

    /**
     * Returns the number of leaf children for this node.
     */
    public int getLeafChildrenCount();

    /**
     * Returns a map of cluster to number of leaf children for that cluster.
     * Can be used to efficiently build a dendrogram or other representation without multiple tree traversals.
     * @return A map of Cluster to Integer representing the number of leaf children for that cluster.
     */
    public Map<Cluster, Integer> getLeafChildrenCountMap();
}
