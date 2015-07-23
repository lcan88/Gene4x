package org.geworkbench.bison.model.clusters;

/*
 * The geworkbench_3.0 project
 * 
 * Copyright (c) 2006 Columbia University 
 *
 */

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * Generalization of <code>DefaultHierCluster</code> that contains a
 * {@link org.geworkbench.bison.model.microarray.DSMarker} that contains a reference to a Genetic Marker defined by
 * {@link org.geworkbench.bison.model.microarray.MarkerValue}. Clusters of this type are used to create a Marker
 * Dendrogram representing Genetic Marker Clustering as obtained from the Hierachical Clustering Analysis
 * 
 * @author First Genetic Trust
 * @version $Id: MarkerHierCluster.java,v 1.2 2006/03/03 18:00:58 mhall Exp $
 */
public class MarkerHierCluster extends DefaultHierCluster {

    private static final long serialVersionUID = 8296278482310224904L;
    /**
     * Stores the marker associated with this cluster. For hierarchical clusters, only leaf nodes have a non-null value
     * in this field.
     */
    protected DSGeneMarker mInfo = null;

    /**
     * Sets the <code>DSMarker</code> associated with this node
     * 
     * @param mi <code>DSMarker</code> associated with this node
     */
    public void setMarkerInfo( DSGeneMarker mi ) {
        mInfo = mi;
    }

    /**
     * Gets the <code>DSMarker</code> associated with this node
     * 
     * @return <code>DSMarker</code> associated with this node
     */
    public DSGeneMarker getMarkerInfo() {
        return mInfo;
    }

}
