package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * Broadcast event encapsulating results of Clustering algorithms
 */
public class ClusterEvent extends Event {
    private Object results = null;
    private String type = null;
    private DSMicroarraySetView maSet = null;

    public ClusterEvent(DSMicroarraySetView mASet, Object rs, String tp) {
        super(null);
        results = rs;
        type = tp;
        maSet = mASet;
    }

    /**
     * The <code>MicroarraySet</code> on which the Clustering was performed
     *
     * @return the input dataset
     */
    public DSMicroarraySetView getMicroarraySet() {
        return maSet;
    }

    /**
     * The <code>Object</code> results obtained from a Clustering analysis
     *
     * @return the Clustering results
     */
    public Object getResults() {
        return results;
    }

    /**
     * <code>String</code> that specifies the kind of Clustering algorithm used
     * for analysis
     *
     * @return
     */
    public String getType() {
        return type;
    }

}
