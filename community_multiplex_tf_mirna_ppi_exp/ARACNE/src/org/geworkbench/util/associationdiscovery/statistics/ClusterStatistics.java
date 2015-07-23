package org.geworkbench.util.associationdiscovery.statistics;

import distributions.ChiSquareDistribution;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;

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

public class ClusterStatistics {
    /**
     * Variables needed for the statistical functions
     */
    public static final int ITMAX = 300;
    public static final double FPMIN = 1.0E-50;
    public static final double EPSILON = 1.0E-20;
    public static final double DELTA = 1.0E-13;
    public static final double LANCZ_CUTOFF = 700.0;
    public static final int MAXPOINTS = 8000;
    public static final double DFLT_AXIS_RATIO = 0.93;
    public static final double DFLT_TOP_RATIO = 0.05;
    public static final double DFLT_BOT_RATIO = 0.12;
    public static final double MAX_BOT_RATIO = 0.19;
    public static final boolean DFLT_SHOWARGS = true;
    public static final boolean DFLT_SHOWRESULT = true;
    public static final boolean DFLT_SHOW_PDF = false;
    public static final int TICK_HEIGHT = 3;

    static public HashMap clusterStatistics = new HashMap();
    static public final int binNo = 10000;
    static public final double binSize = 1.0 / (double) binNo;
    static private final double coeff = 20.0;
    static private final double partNo = 100.0;
    static private final int maxK = 100;
    static private final int steps = 10000;

    private KProbabilityDensity kpb = null;
    private double[] q = null;
    private int chipNo = 0;
    private int geneNo = 0;
    private DSMicroarraySet<DSMicroarray> microarraySet = null;

    public ClusterStatistics(DSMicroarraySet mArraySet) {
        microarraySet = mArraySet;
        chipNo = microarraySet.size();
        geneNo = microarraySet.getMarkers().size();
        init();
        for (int i = 0; i < 101; i++) {
            factCache[i] = 0;
        }
    }

    public static final int STEPS = 100;
    public static double[] psi = new double[STEPS];

    private boolean badBetacf = true;
    static private double factCache[] = new double[101];

    static private void error(String errorText) {
        System.out.println(errorText);
    }

    private double power(double x, double y) {
        return Math.exp(y * Math.log(x));
    }

    private double binomialDistribution(int n, int k, double p) {
        return Math.log(logBinomialDistribution(n, k, p));
    }

    private double maximalP(int nm, int j, int k, double p) {
        return Math.exp(logMaximalP(nm, j, k, p));
    }

    private double binomialCoeff(int n, int k) {
        return Math.exp(logBinomialCoeff(n, k));
    }

    public double betaCF(double a, double b, double x) {
        int m, m2;
        double retval, aa, c, d, del, qab, qam, qap;

        badBetacf = false;
        qab = a + b;
        qap = a + 1.0;
        qam = a - 1.0;
        c = 1.0;
        d = 1.0 - qab * x / qap;
        if (Math.abs(d) < FPMIN) {
            d = FPMIN;
        }
        d = 1.0 / d;
        retval = d;
        for (m = 1; m <= ITMAX; m++) {
            m2 = m + m;
            aa = (b - m) * m * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN) {
                d = FPMIN;
            }

            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN) {
                c = FPMIN;
            }
            d = 1.0 / d;
            retval *= d * c;
            aa = 0.0 - (a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN) {
                d = FPMIN;
            }
            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN) {
                c = FPMIN;
            }
            d = 1.0 / d;
            del = d * c;
            retval *= del;
            if (Math.abs(del - 1.0) < EPSILON) {
                break;
            }
        }
        if (m > ITMAX) {
            badBetacf = true;
        }

        return (retval);
    }

    public double betaI(double a, double b, double x) {
        double bt;
        if (x < 0.0 || x > 1.0) {
            error("Bad x in routine\n");
        }
        if (x == 0.0 || x == 1.0) {
            bt = 0.0;
        } else {
            bt = Math.exp(gammaLN(a + b) - gammaLN(a) - gammaLN(b) + a * Math.log(x) + b * Math.log(1.0 - x));
        }
        if (x < (a + 1.0) / (a + b + 2.0)) {
            return bt * betaCF(a, b, x) / a;
        } else {
            return 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
        }
    }

    static public double gammaLN(double p) {
        int j;
        double x, tmp, ser;

        x = p;
        tmp = x + 5.5;
        tmp = tmp - (x + .5) * Math.log(tmp);
        ser = 1.000000000190015 + 76.18009172947146 / (p + 1.0);
        ser -= 86.50532032941678 / (p + 2.0);
        ser += 24.01409824083091 / (p + 3.0);
        ser -= 1.231739572450155 / (p + 4.0);
        ser += .001208650973866179 / (p + 5.0);
        ser -= 5.395239384953E-06 / (p + 6.0);
        return (Math.log(2.506628274631001 * ser / x) - tmp);
    }

    public double logMaximalP(int nm, int j, int k, double p) {
        double pTot = Math.exp((double) (j - 1) * Math.log(p));
        double logMax = (double) (nm - k) * Math.log(1.0 - pTot);
        return logMax;
    }

    static public double logBinomialDistribution(int nj, int j, double p) {
        double logBico = logBinomialCoeff(nj, j);
        double result = 0.0;
        if (p < 1.0) {
            result = logBico + (double) j * Math.log(p) + (double) (nj - j) * Math.log(1 - p);
        }
        return result;
    }

    static public double factLN(double n) {
        if (n < 0) {
            error("Negative factorial in routine FACTLN");
        }
        if (n <= 1) {
            return 0.0;
        }
        if (n <= 100) {
            return (factCache[(int) n] != 0) ? factCache[(int) n] : (factCache[(int) n] = gammaLN(n + 1.0));
        } else {
            return gammaLN(n + 1.0);
        }
    }

    public double patternPValue(int j, int k, int nj, int nk, int j0, double p) {
        double patP = power(p, k);
        double bigN = nj * binomialCoeff(nk, k);
        double pCumulant = 0.0;
        for (int i = j; i <= nj; i++) {
            double logPPat = logBinomialDistribution(nj - 1, i - 1, patP);
            double logPMax = logMaximalP(nk, i, k, p);
            double bigPjk = Math.exp(logPPat + logPMax);
            pCumulant += bigPjk;
        }
        double png0jk;
        if (pCumulant < 1 / (12.0 * bigN)) {
            png0jk = 1.0 - bigN * pCumulant;
        } else {
            png0jk = 1.0 - Math.exp(bigN * Math.log(1.0 - pCumulant));
        }
        return png0jk;
    }

    static public double logBinomialCoeff(int n, int k) {
        double bico = factLN(n) - factLN(k) - factLN(n - k);
        return bico;
    }

    public double avgPatternNo(int j, int k, int nj, int nk, double p) {
        /* This function computes the average number of jk-patterns discovered
           In a dataset with nj experiments, nk markers, and marker-value probability p  */
        double patP = power(p, k);
        double bigN = nj * binomialCoeff(nk, k);
        double logPPat = logBinomialDistribution(nj - 1, j - 1, patP);
        double logPMax = logMaximalP(nk, j, k, p);
        double bigPjk = Math.exp(logPPat + logPMax);
        double avgNoPatterns = bigPjk * bigN / j;
        return avgNoPatterns;
    }

    public void reset() {
        chipNo = microarraySet.size();
        geneNo = microarraySet.getMarkers().size();
        if (kpb == null) {
            kpb = new KProbabilityDensity(geneNo, chipNo);
        } else {
            kpb.reset(geneNo, chipNo);
        }
        if (q == null) {
            q = new double[chipNo + 1];
        }
        for (int i = 0; i <= chipNo; i++) {
            q[i] = -1.0;
        }
    }

    public void init() {
        reset();
        double increment = 1 / (double) geneNo / binSize;
        double totP = 0.0;

        for (int geneId = 0; geneId < geneNo; geneId++) {
            HashMap map = new HashMap();
            for (int expId = 0; expId < chipNo; expId++) {
                DSMutableMarkerValue marker = (DSMutableMarkerValue) microarraySet.get(expId).getMarkerValue(geneId);
                Double value = new Double(marker.getValue());
                Object obj = map.get(value);
                if (obj == null) {
                    int count = 1;
                    map.put(value, value);
                    for (int expId1 = expId + 1; expId1 < chipNo; expId1++) {
                        DSMutableMarkerValue marker1 = (DSMutableMarkerValue) microarraySet.get(expId1).getMarkerValue(geneId);
                        if (marker.getValue() == marker1.getValue()) {
                            count++;
                        }
                    }
                    if (!marker.isMissing()) {
                        double p = (double) count / (double) chipNo;
                        int bin = (int) (p / binSize);
                        addCount(geneId, value.doubleValue(), p, p);
                    } else {
                        double p = (double) count / (double) chipNo;
                        int bin = 0;
                        addCount(geneId, value.doubleValue(), 0.0, p);
                    }
                }
            }
        }
        kpb.print();
    }

    public void addCount(int geneId, double value, double p, double w) {
        kpb.addCount(geneId, value, p, w);
    }

    private double getLogQjk(int j, int k) {
        if (q[j] < 0.0) {
            q[j] = kpb.getLogQj(j);
        }
        // This is the best we can do without knowing the actual composition of the pattern
        return (1.0 - (double) k / (double) geneNo) * q[j];
    }

    public void tabulateQjk() {
        for (int i = 2; i < chipNo; i++) {
            System.out.println("[" + i + "]\t" + getLogQjk(i, 1) + "\t" + getLogQjk(i, 2) + "\t" + getLogQjk(i, 3));
        }
    }

    private double getRealK(CSMatchedMatrixPattern pattern, double p) {
        double realK = 0;
        DSGeneMarker[] markers = pattern.getPattern().markers();
        int maxK = markers.length;
        DSMicroarray array = pattern.get(0).getObject();
        double p_IID = 1.0;
        for (int k = 0; k < maxK; k++) {
            realK++;
            DSMutableMarkerValue m = array.getMarkerValue(markers[k]);
            double logP = kpb.getMarkerP(markers[k].getSerial(), m.getValue());
            p_IID *= logP;
            if (p_IID < p) {
                break;
            }
        }
        return realK + Math.log((double) maxK);
    }

    public double getExpectedNo(CSMatchedMatrixPattern pattern) {
        int j = pattern.getSupport();
        int k = pattern.getPattern().markers().length;
        double p_IID = getP_IID(pattern);
        double p_Act = getP_Actual(pattern);
        double realK = getRealK(pattern, p_Act);
        //double realK   = (double)k * Math.log(p_Act)/Math.log(p_IID);
        k = (int) Math.rint(realK) + 1;
        return getExpectedNo(j, k);
    }

    public double getExpectedNo(int j, int k) {
        int identNo = kpb.getIdentNo();
        if (k < identNo) {
            return 0.0;
        } else if ((k == identNo) && (j == chipNo)) {
            return 1.0;
        } else {
            int realK = k - identNo;
            int realNk = geneNo - identNo;
            if ((geneNo != microarraySet.getMarkers().size()) || (chipNo != microarraySet.size())) {
                init();
            }
            kpb.initialize(realK);
            double logBinExp = logBinomialCoeff(realNk, realK);
            double logQjk = getLogQjk(j, realK);
            double integral = 0.0;
            double totP = 0.0;
            for (int bin = 0; bin <= kpb.getBinNo(); bin++) {
                double p = kpb.getP(realK, bin);
                double weigh = kpb.getPDensity(realK, bin);
                double dp = kpb.getBinSize(realK, bin);
                if (weigh > 0.0) {
                    double pjk = Math.exp(logBinomialDistribution(chipNo - 1, j - 1, p));
                    integral += weigh * pjk * dp;
                }
                totP += weigh * dp;
            }
            double a1 = Math.log((double) chipNo / (double) j);
            double a2 = Math.log(integral);
            double logExpectedNo = a1 + logBinExp + a2 + logQjk;
            return Math.exp(logExpectedNo);
        }
    }

    public static double scorePattern(DSMicroarraySet<DSMicroarray> mArraySet, CSMatchedMatrixPattern pattern) {
        double score = 0.0;
        DSAnnotationContext context = CSAnnotationContextManager.getInstance().getCurrentContext(mArraySet);
        if (!context.isUnsupervised()) {
            DSPanel<DSMicroarray> cases = context.getItemsForClass(CSAnnotationContext.CLASS_CASE);
            DSPanel<DSMicroarray> controls = context.getItemsForClass(CSAnnotationContext.CLASS_CONTROL);
            //CSMatchedPSSMMatrixPattern pssm = new CSMatchedPSSMMatrixPattern(pattern, mArraySet);
            double countPh = 0;
            double countBk = 0;
            double phNo = cases.size();
            double bkNo = controls.size();
            for (DSMicroarray ma : cases) {
                if (pattern.getPattern().match(ma).getPValue() < 1.0) {
                    countPh++;
                }
            }
            for (DSMicroarray ma : controls) {
                if (pattern.getPattern().match(ma).getPValue() < 1.0) {
                    countBk++;
                }
            }
            double ph1 = phNo - countPh;
            double bk1 = bkNo - countBk;
            double expectedPh = (countPh + countBk) * phNo / (phNo + bkNo);
            double expectedBk = (countPh + countBk) * bkNo / (phNo + bkNo);
            double expectedPh1 = (ph1 + bk1) * phNo / (phNo + bkNo);
            double expectedBk1 = (ph1 + bk1) * bkNo / (phNo + bkNo);
            ChiSquareDistribution distribution = new ChiSquareDistribution(1);
            double chi2 = Math.pow(countPh - expectedPh, 2.0) / expectedPh + Math.pow(countBk - expectedBk, 2.0) / expectedBk + Math.pow(ph1 - expectedPh1, 2.0) / expectedPh1 + Math.pow(bk1 - expectedBk1, 2.0) / expectedBk1;
            //score = ((countPh+1)/(phNo+1))/((countBk+1)/(bkNo+1));
            score = 1 - distribution.getCDF(chi2);
            pattern.bkMatches = (int) countBk;
        }
        return score;
    }
    /*
        public double scorePattern(DSMicroarraySet mArray, Pattern pattern) {
            double score = 0.0;
            CSClassCriteria classCriteria = (CSClassCriteria)microarraySet.getObject(CSClassCriteria.class);
            if(!classCriteria.isUnsupervised()) {
                //PSSM pssm = new PSSM(pattern, mArray);
                double countPh = 0;
                double countBk = 0;
                double phNo = 0;
                double bkNo = 0;
                for(DSMicroarray ma: mArray) {
                    DSAnnotValue value = classCriteria.getValue(ma);
                    if (value == CSClassCriteria.cases) {
                        phNo++;
                        if (PatternOps.matchesPattern(pattern, ma, mArray)) {
                            //if (pssm.isAStrictMatch(chip)) {
                            countPh++;
                        }
                    } else if (value == CSClassCriteria.controls) {
                        bkNo++;
                        if (PatternOps.matchesPattern(pattern, ma, mArray)) {
                            //if (pssm.isAStrictMatch(chip)) {
                            countBk++;
                        }
                    }
                }
                double ph1 = phNo - countPh;
                double bk1 = bkNo - countBk;
                double expectedPh = (countPh + countBk) * phNo / (phNo + bkNo);
                double expectedBk = (countPh + countBk) * bkNo / (phNo + bkNo);
                double expectedPh1 = (ph1 + bk1) * phNo / (phNo + bkNo);
                double expectedBk1 = (ph1 + bk1) * bkNo / (phNo + bkNo);
                ChiSquareDistribution distribution = new ChiSquareDistribution(1);
                double chi2 = Math.pow(countPh - expectedPh, 2.0) / expectedPh + Math.pow(countBk - expectedBk, 2.0) / expectedBk + Math.pow(ph1 - expectedPh1, 2.0) / expectedPh1 + Math.pow(bk1 - expectedBk1, 2.0) / expectedBk1;
                //score = ((countPh+1)/(phNo+1))/((countBk+1)/(bkNo+1));
                score = 1 - distribution.getCDF(chi2);
    //            pattern.bkMatches = (int)countBk;
            }
            return score;
        }
    */

    static public double getExpectedNo(DSMicroarraySet microarraySet, CSMatchedMatrixPattern pattern) {
        ClusterStatistics cs = getClusterStatistics(microarraySet);
        return cs.getExpectedNo(pattern);
    }

    static public ClusterStatistics getClusterStatistics(DSMicroarraySet microarraySet) {
        Object obj = clusterStatistics.get(microarraySet);
        if (obj == null) {
            obj = new ClusterStatistics(microarraySet);
            clusterStatistics.put(microarraySet, obj);
        }
        ClusterStatistics cs = (ClusterStatistics) obj;
        return cs;
    }

    public double[] getH(int k) {
        return kpb.getPDensity(k);
    }

    public void resetHistogram() {
        kpb.resetHistogram();
    }

    public void histogram(double p) {
        kpb.histogram(p);
    }

    public void printHistogram() {
        kpb.printHistogram();
    }

    public double maxEntropy(CSMatchedMatrixPattern pattern) {
        DSGeneMarker[] markers = pattern.getPattern().markers();
        double entropy = 0.0;
        for (int m = 0; m < markers.length; m++) {
            int markerId = markers[m].getSerial();
            for (Iterator it = kpb.markerP[markerId].values().iterator(); it.hasNext();) {
                double p = ((Double) it.next()).doubleValue();
                entropy -= p * Math.log(p);
            }
        }
        return entropy;
    }

    public double getP_IID(CSMatchedMatrixPattern pattern) {
        DSGeneMarker[] markers = pattern.getPattern().markers();
        DSMicroarray array = pattern.get(0).getObject();
        double p_IID = 0.0;
        for (int k = 0; k < markers.length; k++) {
            int markerId = markers[k].getSerial();
            DSMutableMarkerValue m = array.getMarkerValue(markerId);
            double logP = Math.log(kpb.getMarkerP(markerId, m.getValue()));
            p_IID += logP;
        }
        return Math.exp(p_IID);
    }

    public double getP_Actual(CSMatchedMatrixPattern pattern) {
        DSGeneMarker[] markers = pattern.getPattern().markers();
        HashMap map = new HashMap();
        double count = 0.0;
        for (int i = 0; i < chipNo; i++) {
            if (pattern.getPattern().match(microarraySet.get(i)).getPValue() < 1.0) {
                count++;
            }
        }
        return count / (double) chipNo;
    }

    public double entropyRatio(CSMatchedMatrixPattern pattern) {
        DSGeneMarker[] markers = pattern.getPattern().markers();
        HashMap map = new HashMap();
        double entropy = 0.0;
        for (int i = 0; i < chipNo; i++) {
            String test = "";
            for (int j = 0; j < markers.length; j++) {
                test += microarraySet.get(i).getMarkerValue(markers[j]).getValue() + ':';
            }
            if (!map.containsValue(test)) {
                double count = 1.0;
                map.put(test, test);
                for (int k = i + 1; k < chipNo; k++) {
                    String test1 = "";
                    for (int j = 0; j < markers.length; j++) {
                        test1 += microarraySet.get(i).getMarkerValue(markers[j]).getValue() + ':';
                    }
                    if (test.equalsIgnoreCase(test1)) {
                        count++;
                    }
                }
                double p = count / (double) chipNo;
                entropy -= p * Math.log(p);
            }
        }
        return entropy;
    }
}
