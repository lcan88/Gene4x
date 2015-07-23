package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.model.clusters.SOMCluster;

import java.util.EventObject;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Models a JAVA event that would be thrown from a geaw application component
 * to the <code>SOMClusterViewWidget</code> when the data used in the widget
 * get modified.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class SOMClusterModelEvent extends EventObject {

    /**
     * The underlying micorarray set used in the SOM clustering analysis.
     */
    private DSMicroarraySetView mASet;

    /**
     * 2-dimensional array representing the SOM clusters and their grid positions.
     * The <code>SOMCluster</code> object at <code>somClusters[x][y]</code> should
     * have (x, y) as its SOM grid coordinates.
     */
    private SOMCluster[][] somClusters;

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>SOMClusterModelEvent</code>
     */
    public SOMClusterModelEvent(Object source) {
        super(source);
    }

    /**
     * Constructor
     *
     * @param source   <code>Object</code> generating this
     *                 <code>SOMClusterModelEvent</code>
     * @param mas      <code>MicroarraySet</code> used for SOM Analysis
     * @param clusters <code>SOMCluster</code> ararys resulting from
     *                 SOM Analysis representing the SOM Grid and clusters
     */
    public SOMClusterModelEvent(Object source, DSMicroarraySetView mas, SOMCluster[][] clusters) {
        super(source);
        mASet = mas;
        somClusters = clusters;
    }

    /**
     * Gets the <code>MicroarraySet</code> used for SOM Analysis
     *
     * @return <code>MicroarraySet</code> used for SOM Analsysis
     */
    public DSMicroarraySetView getMicroarraySet() {
        return mASet;
    }

    /**
     * Gets the <code>SOMCluster</code> ararys resulting from
     * SOM Analysis representing the SOM Grid and clusters
     *
     * @return <code>SOMCluster</code> ararys resulting from
     *         SOM Analysis representing the SOM Grid and clusters
     */
    public SOMCluster[][] getSOMClusters() {
        return somClusters;
    }
}
