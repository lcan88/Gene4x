package org.geworkbench.bison.model.clusters;

import java.util.Vector;
import java.io.Serializable;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generalization of <code>Cluster</code> to characterize Clusters obtained
 * from the Self Organizing Map Analysis
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class DefaultSOMCluster extends AbstractCluster implements SOMCluster, Serializable {
    /**
     * Contains the Grid Representative cof the Grid this <code>SOMCluster</code>
     * represents
     */
    protected Vector representative = null;
    /**
     * The X Grid coordinate of the Grid this <code>SOMCluster</code> represents
     */
    protected int x = -1;
    /**
     * The Y Grid coordinate of the Grid this <code>SOMCluster</code> represents
     */
    protected int y = -1;

    /**
     * Sets the Grid representative of the Grid this <code>SOMCluster</code>
     * represents
     *
     * @param centroid Grid representative
     */
    public void setGridRepresentative(Vector centroid) {
        representative = centroid;
    }

    /**
     * Returns the Grid representative
     *
     * @return Grid representative
     */
    public Vector getRepresentative() {
        return representative;
    }

    /**
     * Sets the Grid Coordinates of the Grid this <code>SOMCluster</code>
     * represents
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setGridCoordinates(int x, int y) {
        if (x < 0 || y < 0)
            return;
        this.x = x;
        this.y = y;
    }

    /**
     * Clear the Grid Coordinates
     */
    public void clearGridCoordinates() {
        x = y = -1;
    }

    /**
     * Gets the X Coordinate of the Grid this <code>SOMCluster</code> represents
     *
     * @return x coordinate
     */
    public int getXCoordinate() {
        return x;
    }

    /**
     * Gets the Y Coordinate of the Grid this <code>SOMCluster</code> represents
     *
     * @return y coordinate
     */
    public int getYCoordinate() {
        return y;
    }

}