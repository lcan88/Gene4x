package org.geworkbench.util;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class LogStats {
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

    public static final int STEPS = 100;
    public static double[] psi = new double[STEPS];

    private static boolean badBetacf = true;
    private static double factCache[] = new double[101];

    static {
        for (int i = 0; i < 101; i++) {
            factCache[i] = 0;
        }
    }

    private static void error(String errorText) {
        System.out.println(errorText);
    }

    private static double power(double x, double y) {
        return Math.exp(y * Math.log(x));
    }

    private static double binomialDistribution(int n, int k, double p) {
        return Math.log(logBinomialDistribution(n, k, p));
    }

    private static double maximalP(int nm, int j, int k, double p) {
        return Math.exp(logMaximalP(nm, j, k, p));
    }

    private static double binomialCoeff(int n, int k) {
        return Math.exp(logBinomialCoeff(n, k));
    }

    public static double betaCF(double a, double b, double x) {
        int m, m2;
        double retval, aa, c, d, del, qab, qam, qap;

        badBetacf = false;
        qab = a + b;
        qap = a + 1.0;
        qam = a - 1.0;
        c = 1.0;
        d = 1.0 - qab * x / qap;
        if (Math.abs(d) < FPMIN)
            d = FPMIN;
        d = 1.0 / d;
        retval = d;
        for (m = 1; m <= ITMAX; m++) {
            m2 = m + m;
            aa = (b - m) * m * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN)
                d = FPMIN;

            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN)
                c = FPMIN;
            d = 1.0 / d;
            retval *= d * c;
            aa = 0.0 - (a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN)
                d = FPMIN;
            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN)
                c = FPMIN;
            d = 1.0 / d;
            del = d * c;
            retval *= del;
            if (Math.abs(del - 1.0) < EPSILON)
                break;
        }
        if (m > ITMAX)
            badBetacf = true;

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

    public static double gammaLN(double p) {
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

    public static double logMaximalP(int nm, int j, int k, double p) {
        double pTot = Math.exp((double) (j - 1) * Math.log(p));
        double logMax = (double) (nm - k) * Math.log(1.0 - pTot);
        return logMax;
    }

    public static double logBinomialDistribution(int nj, int j, double p) {
        double logBico = logBinomialCoeff(nj, j);
        double result = 0.0;
        if (p < 1.0) {
            result = logBico + (double) j * Math.log(p) + (double) (nj - j) * Math.log(1 - p);
        }
        return result;
    }

    public static double factLN(double n) {
        if (n < 0)
            error("Negative factorial in routine FACTLN");
        if (n <= 1)
            return 0.0;
        if (n <= 100)
            return (factCache[(int) n] != 0) ? factCache[(int) n] : (factCache[(int) n] = gammaLN(n + 1.0));
        else
            return gammaLN(n + 1.0);
    }

    public static double logBinomialCoeff(int n, int k) {
        double bico = factLN(n) - factLN(k) - factLN(n - k);
        return bico;
    }
}
