package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.model.clusters.MarkerHierCluster;
import org.geworkbench.bison.model.clusters.MicroarrayHierCluster;

import java.util.EventObject;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Models a JAVA event that would be thrown from a geaw application component
 * when the data used in the <code>HierClusterViewWidget</code> get modified.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class HierClusterModelEvent extends EventObject {
    /**
     * The underlying micorarray set used in the hierarchical clustering
     * analysis.
     */
    private DSMicroarraySetView mASet;
    /**
     * The top-level marker cluster produced by the hierarchical clustering
     * analysis.
     */
    private MarkerHierCluster markerCluster;
    /**
     * The top-level microarray cluster produced by the hierarchical clustering
     * analysis.
     */
    private MicroarrayHierCluster arrayCluster;

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>HierClusterModelEvent</code>
     */
    public HierClusterModelEvent(Object source) {
        super(source);
    }

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>HierClusterModelEvent</code>
     * @param mas    <code>MicroarraySet</code> used for
     *               Hierarchical Clustering Analysis
     * @param mrkhc  <code>MarkerHierCluster</code> node resulting from
     *               Hierarchical Clustering Analysis representing Genetic Marker clusters
     * @param mrahc  <code>MicroarrayHierCluster</code> node resulting from
     *               Hierarchical Clustering Analysis representing Microarray clusters
     */
    public HierClusterModelEvent(Object source, DSMicroarraySetView mas, MarkerHierCluster mrkhc, MicroarrayHierCluster mrahc) {
        super(source);
        mASet = mas;
        markerCluster = mrkhc;
        arrayCluster = mrahc;
    }

    /**
     * Gets the <code>MicroarraySet</code> used for Hierarchical Clustering
     * Analysis
     *
     * @return <code>MicroarraySet</code> used for Hierarchical Clustering
     *         Analysis
     */
    public DSMicroarraySetView getMicroarraySet() {
        return mASet;
    }

    /**
     * Gets the <code>MarkerHierCluster</code> node resulting from
     * Hierarchical Clustering Analysis representing Genetic Marker clusters
     *
     * @return <code>MarkerHierCluster</code>
     */
    public MarkerHierCluster getMarkerCluster() {
        return markerCluster;
    }

    /**
     * Gets the <code>MicroarrayHierCluster</code> node resulting from
     * Hierarchical Clustering Analysis representing Microarray clusters
     *
     * @return <code>MicroarrayHierCluster</code>
     */
    public MicroarrayHierCluster getMicroarrayCluster() {
        return arrayCluster;
    }

}
