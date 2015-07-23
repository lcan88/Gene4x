package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.algorithms.BWAbstractAlgorithm;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class NetworkLayout extends BWAbstractAlgorithm {
    boolean layoutOnly = false;
    Object[] edges = null;
    double threshold = 0.0;
    GeneNetworkPanel geneNetworkPanel = null;
    DSGeneMarker marker = null;

    /**
     * setting up the parameters...
     *
     * @param _gnp       GeneNetworkPanel
     * @param _mrk       IGenericMarker
     * @param _edges     Object[]
     * @param _threshold double
     */
    public NetworkLayout(GeneNetworkPanel _gnp, DSGeneMarker _mrk, Object[] _edges, double _threshold) {
        geneNetworkPanel = _gnp;
        threshold = _threshold;
        edges = _edges;
        marker = _mrk;
    }

    /**
     * execute
     */
    protected void execute() {
        if (edges.length > 0) {
            geneNetworkPanel.createNetwork(edges, marker, 0, threshold);
        }
    }

    public void doLayoutOnly() {
        layoutOnly = true;
    }

    public void createAndLayout() {
        layoutOnly = false;
    }
}
