package org.geworkbench.bison.model.clusters;

/*
 * The geworkbench_3.0 project
 * 
 * Copyright (c) 2006 Columbia University 
 *
 */

/**
 * Defines a Generalization of a <code>Cluster</code> that would be obtained from a Hierarchical Clustering Analysis
 * 
 * @author First Genetic Trust
 * @version $Id: HierCluster.java,v 1.2 2006/03/03 18:00:58 mhall Exp $
 */
public interface HierCluster extends Cluster, Comparable {
    /**
     * Sets the height of the node based on Hierarchical Analysis
     * 
     * @param d height
     */
    public void setHeight( double d );

    /**
     * Returns the Height of this node
     * 
     * @return height
     */
    public double getHeight();

    /**
     * Maximum height that any leaf posseses in the tree
     * 
     * @return maximum height
     */
    public double getMaxHeight();

    /**
     * Returns the combinatorial depth of this node in the Hierachical cluster tree i.e. the number of nodes including
     * this node to be traversed to get to a leaf node
     * 
     * @return order
     */
    public int getDepth();

    /**
     * Returns the combinatorial depth of this node in the Hierachical cluster tree i.e. the number of nodes including
     * this node to be traversed to get to a leaf node
     * 
     * @param depth tree depth
     */
    public void setDepth( int depth );

    /**
     * Add the designated node hc in the current cluster, at the designated index position.
     * 
     * @param hc node to be added
     * @param index index at which to be added
     */
    public void addNode( HierCluster hc, int index );

    /**
     * Return the node at the designated index position.
     * 
     * @param index index of the node to be returned
     * @return node that is returned
     */
    public HierCluster getNode( int index );

    public void removeAll();
}
