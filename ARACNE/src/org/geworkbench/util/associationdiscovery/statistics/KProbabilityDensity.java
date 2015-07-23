package org.geworkbench.util.associationdiscovery.statistics;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class KProbabilityDensity {
    /**
     * The bin center is computed as exp(bin/binMax - 1.0) * A
     * The bin size   is computed as [exp((bin+1)/binMax - 1.0) - exp((bin+1)/binMax - 1.0)] * A
     */

    static final private double binNo = 1000;
    static final private int maxK = 1000;
    static final private double A = 25;

    private double geneNo = 0;
    private double mArrayNo = 0;
    public HashMap[] markerP = null;
    public int[] markerPNo = null;
    public boolean[] identical = null;
    public int identNo = 0;
    private int lastK = 0;
    private double[][] density = new double[maxK][];

    public KProbabilityDensity(int _geneNo, int _mArrayNo) {
        reset(_geneNo, _mArrayNo);
    }

    public void reset(int _geneNo, int _mArrayNo) {
        geneNo = _geneNo;
        mArrayNo = _mArrayNo;
        identNo = 0;
        lastK = 1;
        for (int k = 1; k < lastK; k++) {
            density[k] = null;
        }
        density[1] = new double[(int) binNo + 1];
        density[0] = new double[(int) binNo + 1];
        markerP = new HashMap[(int) geneNo];
        markerPNo = new int[(int) geneNo];
        identical = new boolean[(int) geneNo];
        for (int i = 0; i < geneNo; i++) {
            markerP[i] = new HashMap();//double[(int)mArrayNo];
            markerPNo[i] = 0;
            identical[i] = false;
        }
    }

    public double getP(int k, int bin) {
        if (bin == binNo) {
            return 1.0;
        } else if (bin == 0) {
            return 0.0;
        } else {
            //double p = Math.exp(((double)bin / binNo - 1.0) * (A + k));
            //double p = Math.exp(((double)bin / binNo - 1.0) * (A));
            double p = (double) bin / binNo;
            return p;
        }
    }

    public double getBinSize(int k, int bin) {
        if (bin == binNo) {
            return 1.0 - getP(k, bin - 1);
        } else {
            double dp = getP(k, bin + 1) - getP(k, bin);
            return dp;
        }
    }

    public int getBin(int k, double p) {
        int bin = 0;
        if (p > 0) {
            //bin = (int)((Math.log(p) / (A + k) + 1.0) * binNo);
            //bin = (int)((Math.log(p) / (A) + 1.0) * binNo);
            bin = (int) (p * (double) binNo);
        }
        return Math.max(0, bin);
    }

    void addCount(int markerId, double value, double p, double weigh) {
        if (p == 1) {
            double size = getBinSize(1, (int) binNo);
            density[1][(int) binNo] += weigh / size / geneNo;
            identNo++;
            identical[markerId] = true;
        } else {
            int bin = getBin(1, p);
            double size = getBinSize(1, bin);
            density[1][bin] += weigh / size / geneNo;
            if (p > 0) {
                Double d = new Double(p);
                Double v = new Double(value);
                markerP[markerId].put(v, d);//[markerPNo[markerId]] = p;
                markerPNo[markerId]++;
            }
        }
    }

    double getMarkerP(int markerId, double value) {
        Object d = markerP[markerId].get(new Double(value));
        if (d != null) {
            return ((Double) d).doubleValue();
        } else {
            return 0.0;
        }
    }

    public void initialize(int _K) {
        _K = Math.min(_K, maxK - 1);
        for (int k = lastK + 1; k <= _K; k++) {
            density[k] = new double[(int) binNo + 1];
            for (int bin = 1; bin < binNo; bin++) {
                density[k][bin] = 0.0;
            }
            for (int bin1 = 0; bin1 < binNo; bin1++) {
                double wk_1 = density[k - 1][bin1];
                if (wk_1 > 0) {
                    for (int bin2 = 0; bin2 <= binNo; bin2++) {
                        double w1 = density[1][bin2];
                        if (w1 > 0) {
                            double p1Lo = getP(k - 1, bin1);
                            double p1Hi = getP(k - 1, bin1 + 1);
                            double p2Lo = getP(1, bin2);
                            double p2Hi = getP(1, bin2 + 1);
                            double minP = p1Lo * p2Lo;
                            double maxP = p1Hi * p2Hi;
                            minP = (minP + maxP) / 2.0;
                            maxP = minP;
                            double weigh = 0;
                            if (bin1 == bin2) {
                                double p1 = wk_1 * (p1Hi - p1Lo);
                                double p2 = w1 * (p2Hi - p2Lo) - p1Lo / geneNo;
                                weigh = p1 * p2;
                            } else {
                                weigh = wk_1 * (p1Hi - p1Lo) * w1 * (p2Hi - p2Lo);
                            }
                            if (weigh > 0) {
                                int from = getBin(k, minP);
                                int to = getBin(k, maxP);
                                double step = (double) (to - from + 1);
                                for (int bin = from; bin <= to; bin++) {
                                    double dp = getBinSize(k, bin);
                                    density[k][bin] += weigh / step / dp;
                                }
                            }
                        }
                    }
                }
            }
            density[k][(int) binNo] = Math.max(0.0, density[1][(int) binNo] * getBinSize(1, (int) binNo - 1) - 1 / geneNo) * density[k - 1][(int) binNo] * getBinSize(k - 1, (int) binNo - 1) / getBinSize(k, (int) binNo - 1);
            double totP = 0.0;
            for (int bin = 0; bin <= binNo; bin++) {
                double size = getBinSize(k, bin);
                totP += density[k][bin] * size;
            }
            /*
                         double correction = 1.0/totP;
                         for (int bin = 0; bin <= binNo; bin++) {
                density[k][bin] *= correction;
                         }
                         //System.out.println("KPD TotP: " + totP);
                         //density[k][0] = Math.max(0.0, 1.0 - totP);
             */
        }
        if (_K > lastK) {
            lastK = _K;
        }
    }

    public double getPDensity(int k, double p) {
        if (k < maxK) {
            if (k > lastK) {
                initialize(k);
            }
            int bin = getBin(k, p);
            return density[k][bin];
        } else {
            return 0.0;
        }
    }

    public double getPDensity(int k, int bin) {
        if (k < maxK) {
            if (k > lastK) {
                initialize(k);
            }
            return density[k][bin]; ///(1 + Math.log((double)k)/500.0);
        } else {
            return 0.0;
        }
    }

    public double[] getPDensity(int k) {
        if (k < maxK) {
            return density[k];
        }
        return null;
    }

    public void resetHistogram() {
        for (int i = 0; i <= binNo; i++) {
            density[0][i] = 0.0;
        }
    }

    public void histogram(double p) {
        int bin = getBin(1, p);
        density[0][bin]++;
    }

    public void printHistogram() {
        for (int bin = 0; bin <= binNo; bin++) {
            double p = getP(1, bin);
            double w = getPDensity(0, bin);
            System.out.println(p + "\t" + w);
        }
    }

    public void print() {
        double totw1 = 0.0;
        double totw2 = 0.0;
        double totw3 = 0.0;
        double totw4 = 0.0;
        double totw5 = 0.0;
        initialize(2);
        initialize(3);
        initialize(4);
        initialize(5);
        for (int i = 0; i <= binNo; i++) {
            double p = getP(1, i);
            double dp = getBinSize(1, i);
            double ww1 = getPDensity(1, p);
            double w1 = density[1][i];
            double dp1 = getBinSize(1, i);
            double w2 = density[2][i];
            double ww2 = getPDensity(2, p);
            double dp2 = getBinSize(2, i);
            double w3 = density[3][i];
            double ww3 = getPDensity(3, p);
            double dp3 = getBinSize(3, i);
            double w4 = density[4][i];
            double ww4 = getPDensity(4, p);
            double dp4 = getBinSize(4, i);
            double w5 = density[5][i];
            double ww5 = getPDensity(5, p);
            double dp5 = getBinSize(5, i);
            //*
            System.out.println(i + "\t" + p + "\t" + dp + "\t" + w1 * dp1 + "\t" + w2 * dp2 + "\t" + w3 * dp3 + "\t" + w4 * dp4 + "\t" + w5 * dp5);
            //*/
            totw1 += ww1 * dp1;
            totw2 += ww2 * dp2;
            totw3 += ww3 * dp3;
            totw4 += ww4 * dp4;
            totw5 += ww5 * dp5;
        }
        System.err.println("Total:\t" + totw1 + "\t" + totw2 + "\t" + totw3 + "\t" + totw4 + "\t" + totw5);
    }

    public double getLogQj(int j) {
        double result = 0.0;
        for (int i = 0; i < geneNo; i++) {
            if (!identical[i]) {
                int pNo = markerPNo[i];
                double pj = 1.0;
                //for (int l = 0; l < pNo; l++) {
                HashMap map = markerP[i];
                for (Iterator it = map.values().iterator(); it.hasNext();) {
                    double p = ((Double) it.next()).doubleValue();
                    pj -= Math.pow(p, (double) j);
                }
                if (pj > 0) {
                    result += Math.log(pj);
                } else {
                    return Double.MIN_VALUE;
                }
            }
        }
        return result;
    }

    public int getIdentNo() {
        return identNo;
    }

    public int getBinNo() {
        return (int) binNo;
    }
}
