package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generalization of the <code>DefaultSOMCluster</code> that contain reference
 * to a Genetic Marker that this <code>SOMCluster</code> node stands for. A node
 * of this type is obtained from the Self Organizing Map Analysis
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class LeafSOMCluster extends DefaultSOMCluster {
    /**
     * {@link DSMarker} that contains a reference to a
     * Genetic Marker defined by {@link org.geworkbench.bison.model.microarray.MarkerValue} and
     * referred to by this <code>LeafSOMCluster</code>
     */
    private DSGeneMarker mInfo = null;

    /**
     * The <code>DSMarker</code> that this <code>SOMCluster</code> represents
     *
     * @param mInfo marker info
     */
    public LeafSOMCluster(DSGeneMarker mInfo) {
        this.mInfo = mInfo;
    }

    /**
     * Gets the <code>DSMarker</code> contained in this <code>LeafSOMCluster
     * </code>
     *
     * @return <code>DSMarker</code> contained in this
     *         <code>LeafSOMCluster</code>
     */
    public DSGeneMarker getMarkerInfo() {
        return mInfo;
    }

    /**
     * Sets the <code>DSMarker</code> to be contained in this
     * <code>LeafSOMCluster</code>
     *
     * @return <code>DSMarker</code> to be contained in this
     *         <code>LeafSOMCluster</code>
     */
    public void setMarkerInfo(DSGeneMarker mInfo) {
        this.mInfo = mInfo;
    }

}
