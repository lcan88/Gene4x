package org.geworkbench.bison.model.clusters;

/**
 * @author John Watkinson
 */
public interface DSSOMClusterDataSet extends DSClusterDataSet {

    public int getRows();
    public int getColumns();

    public SOMCluster getCluster(int row, int column);

    public SOMCluster[][] getClusters();

}
