package org.geworkbench.bison.model.clusters;

/*
 * The geworkbench_3.0 project
 * 
 * Copyright (c) 2006 Columbia University 
 *
 */

import java.io.Serializable;

/**
 * <p/> Generalization of the <code>Cluster</code> contract that encapsulates
 * functionality to handle Clusters generated from the Hierarchical Clustering
 * analysis method
 * 
 * @author First Genetic Trust
 */
public class DefaultHierCluster extends AbstractCluster implements HierCluster,
		Serializable {

	private static final long serialVersionUID = 756372128582359072L;

	private double height = 0.0;
	private int depth = 0;
	private double maxHeight = 0.0;

	// private int order = 0;

	/**
	 * Sets the Algorithmic Height of this <code>Cluster</code>
	 * 
	 * @param d
	 *            height of this node
	 */
	public void setHeight(double d) {
		height = d;
		maxHeight = Math.max(maxHeight, height);
	}

	/**
	 * Gets the Algorithmic Height of this <code>Cluster</code>
	 * 
	 * @return height of this node
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns the Maximum Algorithmic Height of any <code>Cluster</code> in
	 * the Hierarchical Cluster Tree that this <code>Cluster</code> is a part
	 * of
	 * 
	 * @return maximum height
	 */
	public double getMaxHeight() {
		return maxHeight;
	}

	/**
	 * Gets the Combinatorial Depth of this <code>Cluster</code>
	 * 
	 * @return depth of this node
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Sets the Combinatorial Depth of this <code>Cluster</code>
	 * 
	 * @param d
	 *            depth of this node
	 */
	public void setDepth(int d) {
		depth = d;
	}

	/**
	 * Adds <code>Cluster</code> as a child of this node
	 * 
	 * @param hc
	 *            child to be added
	 * @param index
	 *            index at which the child is to be added
	 */
	public void addNode(HierCluster hc, int index) {
		if (hc != null && index >= 0) {
			children.add(index, hc);
			assert children.size() <= 2;
			maxHeight = Math.max(maxHeight, hc.getMaxHeight());
			((AbstractCluster) hc).parent = this;
		}

	}

	/**
	 * Gets a particular child from a known index
	 * 
	 * @param index
	 *            child with the index that has be retrievd
	 * @return the child node or null if no child with the given index exists
	 */
	public HierCluster getNode(int index) {
		if (children.size() < index + 1)
			return null;
		else
			return (HierCluster) children.get(index);
	}

	/**
	 * {@link java.lang.Comparable} method that compares this
	 * <code>Cluster</code> to another Hierarchical CLuster node. The basis of
	 * comparison is the number of children bothe the nodes contain
	 * 
	 * @param o
	 *            <code>HierCluster</code> to be compared to
	 * @return
	 *            <li> 1 if o contains more children </li>
	 *            <li> -1 if this
	 *            <code>HierCluster</li> contains more children </li>
	 *         <li> 0 if both have same number of children
	 */
	public int compareTo(Object o) {
		assert o instanceof HierCluster;
		if (((HierCluster) o).getHeight() < height)
			return -1;
		else if (((HierCluster) o).getHeight() > height)
			return 1;
		return 0;
	}

	public String toString() {
		return getID();
	}
}
