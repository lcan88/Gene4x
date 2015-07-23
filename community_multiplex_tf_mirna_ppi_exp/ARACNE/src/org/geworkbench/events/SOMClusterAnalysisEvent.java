package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.model.clusters.SOMCluster;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class SOMClusterAnalysisEvent extends Event {
    String value = null;
    /**
     * The underlying micorarray set used in the hierarchical clustering
     * analysis.
     */
    private DSMicroarraySet mASet;
    /**
     * The top-level SOM cluster produced by the hierarchical clustering
     * analysis.
     */
    private SOMCluster[][] somClusters;

    public SOMClusterAnalysisEvent(EventSource source, String message, DSMicroarraySet microarraySet, SOMCluster[][] sc) {
        super(source);
        value = message;
        mASet = microarraySet;
        somClusters = sc;
    }

    public String getMessage() {
        return value;
    }

    public DSMicroarraySet getMicroarraySet() {
        return mASet;
    }

    public SOMCluster[][] getSOMClusters() {
        return somClusters;
    }

}
