package org.geworkbench.util.pathwaydecoder.mutualinformation;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.File;

/**
 * @author John Watkinson
 */
public class AdjacencyMatrixDataSet extends CSAncillaryDataSet implements DSAncillaryDataSet {

    private AdjacencyMatrix matrix;
    private int geneId;
    private double threshold;
    private int depth;
    private String networkName;

    public AdjacencyMatrixDataSet(AdjacencyMatrix matrix, int geneId, double threshold, int depth, String name, String networkName, DSMicroarraySet parent) {
        super(parent, name);
        setID(RandomNumberGenerator.getID());
        this.matrix = matrix;
        this.geneId = geneId;
        this.threshold = threshold;
        this.depth = depth;
        this.networkName = networkName;
    }

    public void writeToFile(String fileName) {
        // no-op
    }

    public AdjacencyMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(AdjacencyMatrix matrix) {
        this.matrix = matrix;
    }

    public int getGeneId() {
        return geneId;
    }

    public void setGeneId(int geneId) {
        this.geneId = geneId;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public File getDataSetFile() {
        // no-op
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
}
