package org.geworkbench.components.pathwaydecoder.networks;

import java.util.Vector;

public class CentricNetwork {
    GeneInfo geneCenter;
    Vector targets = new Vector();

    int numPredicted;
    boolean isComparable = false;

    public boolean isIsComparable() {
        return isComparable;
    }

    public Vector getTargets() {
        return targets;
    }

    public int getNumPredicted() {
        return numPredicted;
    }

    public void setGeneCenter(GeneInfo geneCenter) {
        this.geneCenter = geneCenter;
    }

    public void setIsComparable(boolean isComparable) {
        this.isComparable = isComparable;
    }

    public void setTargets(Vector targets) {
        this.targets = targets;
    }

    public void setNumPredicted(int numPredicted) {
        this.numPredicted = numPredicted;
    }

    public GeneInfo getGeneCenter() {
        return geneCenter;
    }

    public CentricNetwork(GeneInfo geneCenter) {
        this.geneCenter = geneCenter;
    }

    public void addTarget(GeneInfo target) {
        targets.add(target);
    }

    public boolean contains(GeneInfo geneInfo) {
        return targets.contains(geneInfo);
    }

    public void foundTarget() {
        numPredicted++;
    }

    public GeneInfo getTarget(GeneInfo geneInfo) {
        int index = targets.indexOf(geneInfo);
        return (GeneInfo) targets.get(index);
    }

    public float getPctPredicted() {
        return (float) numPredicted / (float) targets.size();
    }
}
