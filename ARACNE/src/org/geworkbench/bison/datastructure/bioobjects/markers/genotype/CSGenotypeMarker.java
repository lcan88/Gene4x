package org.geworkbench.bison.datastructure.bioobjects.markers.genotype;

import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Range;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSGenotypeMarker extends CSGeneMarker implements Serializable, DSRangeMarker {
    protected org.geworkbench.bison.util.Range range = new org.geworkbench.bison.util.Range();
    protected boolean isSNP = true;
    protected boolean isGT = true;
    protected double phX = 0;
    protected double bkX = 0;
    protected double phXX = 0;
    protected double bkXX = 0;
    protected int phN = 0;
    protected int bkN = 0;
    protected boolean ready = false;
    protected double[] phSet = null;
    protected double[] bgSet = null;
    protected HashMap phDistribution = new HashMap();
    protected HashMap bgDistribution = new HashMap();
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("isSNP", boolean.class), new ObjectStreamField("isGT", boolean.class)};

    @Override public Object clone() {
        CSGenotypeMarker clone = (CSGenotypeMarker) super.clone();
        clone.range = new Range();
        return clone;
    }

    public CSGenotypeMarker(int markerId) {
        range.min = 0;
        this.markerId = markerId;
    }

    public void setGenotype(boolean isGenotype) {
        isGT = isGenotype;
    }

    public void check(DSMutableMarkerValue marker, boolean isPh) {
        range.min = Math.min(range.min, marker.getValue());
        range.max = Math.max(range.max, marker.getValue());
        range.norm.add(marker.getValue());
    }

//    public void check(DSMutableMarkerValue marker, boolean isPh) {
//        CSGenotypicMarkerValue gt = (CSGenotypicMarkerValue) marker;
//        getRange().max = Math.max(getRange().max, gt.getAllele(0));
//        getRange().max = Math.max(getRange().max, gt.getAllele(1));
//        HashMap table = phDistribution;
//        if (!isPh) {
//            table = bgDistribution;
//            bkN++;
//        } else {
//            phN++;
//        }
//        Integer key0 = new Integer(gt.getAllele(0));
//        Integer count = (Integer) table.get(key0);
//        if (count != null) {
//            table.put(key0, new Integer(count.intValue() + 1));
//            ;
//        } else {
//            count = new Integer(1);
//            table.put(key0, count);
//        }
//        key0 = new Integer(gt.getAllele(1));
//        count = (Integer) table.get(key0);
//        if (count != null) {
//            table.put(key0, new Integer(count.intValue() + 1));
//            ;
//        } else {
//            count = new Integer(1);
//            table.put(key0, count);
//        }
//    }

    public void reset(int id, int phNo, int bgNo) {
        setSerial(id);
        phN = 0;
        bkN = 0;
        range.max = Double.MIN_VALUE;
        range.min = Double.MAX_VALUE;
        range.norm = new org.geworkbench.bison.util.Normal();
    }

    public void addPh(DSMarkerValue marker) {
        phN++;
    }

    public void addBk(DSMarkerValue marker) {
        bkN++;
    }

    public void addSNP(int v, boolean isPh) {
        if (v == -1) {
            v = -2;
        }
        HashMap table = phDistribution;
        if (!isPh) {
            table = bgDistribution;
            bkN++;
        } else {
            phN++;
        }
        Integer key = new Integer(v);
        Integer count = (Integer) table.get(key);
        if (count != null) {
            table.put(key, new Integer(count.intValue() + 1));
        } else {
            count = new Integer(1);
            table.put(key, count);
        }
    }

    public HashMap getPhDistribution() {
        return phDistribution;
    }

    public HashMap getBgDistribution() {
        return bgDistribution;
    }

    public double getSNPFrequency(int v) {
        double count = 0.0;
        Integer key = new Integer(v);
        Integer cnt = null;
        cnt = (Integer) phDistribution.get(key);
        if (cnt != null) {
            count += cnt.doubleValue();
        }
        cnt = (Integer) bgDistribution.get(key);
        if (cnt != null) {
            count += cnt.doubleValue();
        }
        return (count / (bkN + phN));
    }

    private void compute() {
        phX = phX / phN;
        phXX = phXX / (double) phN - phX * phX;
        bkX = bkX / bkN;
        bkXX = bkXX / (double) bkN - bkX * bkX;
        ready = true;
    }

    public double phMean() {
        if (!ready) {
            compute();
            Arrays.sort(phSet);
            Arrays.sort(bgSet);
        }
        return phX;
    }

    public double phSigma() {
        if (!ready) {
            compute();
        }
        return phXX;
    }

    public double bkMean() {
        if (!ready) {
            compute();
        }
        return bkX;
    }

    public double bkSigma() {
        if (!ready) {
            compute();
        }
        return bkXX;
    }

    private double errfc(double x) {
        double t, z, ans;
        z = Math.abs(x);
        t = 1.0 / (1.0 + 0.5 * z);
        ans = t * Math.exp(-z * z - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * 0.17087277)))))))));
        return x >= 0.0 ? ans / 2.0 : 1.0 - ans / 2.0;
    }

    public int getPhN() {
        return phN;
    }

    public int getBkN() {
        return bkN;
    }

    public Range getRange() {
        return range;
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(label);
        writer.write('\t');
        writer.write(description);
    }

    public String toString() {
        return label;
    }

    protected void writeObject(ObjectOutputStream oos) throws IOException {
        super.writeObject(oos);
        oos.defaultWriteObject();
    }

    protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        super.readObject(ois);
        ois.defaultReadObject();
        phDistribution = new HashMap();
        bgDistribution = new HashMap();
    }

    public DSGeneMarker deepCopy() {
        CSGenotypeMarker gi = new CSGenotypeMarker(markerId);
        gi.range = new org.geworkbench.bison.util.Range();
        gi.range.min = this.range.min;
        gi.range.max = this.range.max;
        gi.isSNP = isSNP;
        gi.isGT = isGT;
        gi.label = label;
        gi.phX = phX;
        gi.bkX = bkX;
        gi.phXX = phXX;
        gi.bkXX = bkXX;
        gi.phN = phN;
        gi.bkN = bkN;
        gi.ready = ready;
        gi.phSet = (double[]) phSet.clone();
        gi.bgSet = (double[]) bgSet.clone();
        gi.phDistribution = (HashMap) phDistribution.clone();
        gi.bgDistribution = (HashMap) bgDistribution.clone();
        return gi;
    }

    //  public Object[] getRelatedInfoObject(int x) {
    //    return null;
    //  } // to quit the complaining about not implementing this method

    //  public String[] getRelatedInfoText(int x) {
    //    return null;
    //  } // to quit the complaining about not implementing this method

    //  public String getSubID() {
    //    return label;
    //  }
}
