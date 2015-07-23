package org.geworkbench.bison.model.clusters;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.views.DSDataSetView;

import java.io.File;

/**
 * @author John Watkinson
 */
public class CSHierClusterDataSet extends CSAncillaryDataSet implements DSHierClusterDataSet {

    private HierCluster[] clusters;
    private DSDataSetView parentSet;

    public CSHierClusterDataSet(HierCluster[] clusters, String name, DSDataSetView dataSetView) {
        super(dataSetView.getDataSet(), name);
        this.clusters = clusters;
        this.parentSet = dataSetView;
    }

    public void writeToFile(String fileName) {
        // No-op
    }

    public DSDataSetView getDataSetView() {
        return parentSet;
    }

    public HierCluster getCluster(int index) {
        return clusters[index];
    }

    public int getNumberOfClusters() {
        return clusters.length;
    }

    public File getDataSetFile() {
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }
}
