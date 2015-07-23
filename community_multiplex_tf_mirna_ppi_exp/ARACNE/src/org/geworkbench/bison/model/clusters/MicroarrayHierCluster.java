package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generalization of <code>DefaultHierCluster</code> that contains a
 * {@link org.geworkbench.bison.model.microarray.Microarray} that defines an individual
 * Experiment in the dataset being analysed. Clusters
 * of this type are used to create a Microarray Dendrogram representing
 * Phenotype Clustering as obtained from the Hierachical Clustering Analysis
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MicroarrayHierCluster extends DefaultHierCluster {
    /**
     * Stores the microarray associated with this cluster. For hierarchical
     * clusters, only leaf nodes have a non-null value in this field.
     */
    protected DSMicroarray microarray = null;

    /**
     * Sets the <code>Microarray</code> associated with this node
     *
     * @param ma
     */
    public void setMicroarray(DSMicroarray ma) {
        microarray = ma;
    }

    /**
     * Gets the <code>Microarray</code> associated with this node
     *
     * @return
     */
    public DSMicroarray getMicroarray() {
        return microarray;
    }
}
