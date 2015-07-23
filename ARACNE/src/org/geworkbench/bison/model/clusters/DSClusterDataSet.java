package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;

/**
 * Represents
 *
 * @author John Watkinson
 */
public interface DSClusterDataSet extends DSAncillaryDataSet {

    public int getNumberOfClusters();

    public Cluster getCluster(int index);

    public DSDataSetView getDataSetView();

}
