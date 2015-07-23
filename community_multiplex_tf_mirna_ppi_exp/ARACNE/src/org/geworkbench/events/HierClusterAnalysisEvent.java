package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.model.clusters.MarkerHierCluster;
import org.geworkbench.bison.model.clusters.MicroarrayHierCluster;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class HierClusterAnalysisEvent extends Event {
    String value = null;
    /**
     * The underlying micorarray set used in the hierarchical clustering
     * analysis.
     */
    private DSMicroarraySet mASet;
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

    public HierClusterAnalysisEvent(EventSource source, String message, DSMicroarraySet microarraySet, MarkerHierCluster mhc, MicroarrayHierCluster mahc) {
        super(source);
        value = message;
        mASet = microarraySet;
        markerCluster = mhc;
        arrayCluster = mahc;
    }

    public String getMessage() {
        return value;
    }

    public DSMicroarraySet getMicroarraySet() {
        return mASet;
    }

    public MarkerHierCluster getMarkerCluster() {
        return markerCluster;
    }

    public MicroarrayHierCluster getMicroarrayCluster() {
        return arrayCluster;
    }

}
