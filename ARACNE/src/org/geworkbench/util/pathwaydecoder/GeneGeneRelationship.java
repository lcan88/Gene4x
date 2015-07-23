package org.geworkbench.util.pathwaydecoder;

import distributions.ChiSquareDistribution;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.util.function.gaussian.TruncatedGaussianBivariate;
import org.geworkbench.util.function.gaussian.TruncatedGaussianUnivariate;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneGeneRelationship <T extends DSGeneMarker,Q extends DSMicroarray> {
    static {
        try {
            System.loadLibrary("mutualinfo");
            System.out.println("Mutual Info Library Loaded");
        } catch (UnsatisfiedLinkError ex) {
            System.out.println("Exception: " + ex);
        }
    }

    /**
     * This variable determines the number of axis segments used to run the Chi^2 test
     * miSteps:  the number of segments each axis is divided into
     * miBlock:  the number of consecutive segments to average on
     */
    static private int normalBin = 100000;
    static private double[] normal = new double[normalBin];
    static private final int miSteps = 6;
    static private final int miBlocks = 2;
    static private final int size = 5;
    static public final int STD_REGRESSION = 1;
    static public final int PEARSON = 2;
    static public final int RANK_CHI2 = 3;
    static public final int RANK_MI = 4;
    static private final int iterationDefault = 15000;
    static private final int binNo = 10000;

    public static final int BG_RANDOM = 0;
    public static final int BG_HARTEMINK_CLIPPED = 1;

    public static final int BINS = 0;
    public static final int GAUSSIAN_KERNEL = 1;

    int backgroundType = BG_HARTEMINK_CLIPPED;
    //int backgroundType = BG_RANDOM;

    //private JMicroarrayVisualizer maVisualizer = null;
    private int type = STD_REGRESSION;
    private int[][] miSpace = null;
    private double[][] miProb = null;
    private int[][][] mi3DSpace = null;
    private double[][][] mi3DProb = null;
    private double maPerMIStep = 1;
    private double[] histogram = new double[binNo];
    private double[] background = new double[binNo];
    private DSMicroarraySetView<T, Q> view = new CSMicroarraySetView<T, Q>();
    //    private DSMicroarraySet mArraySet = null;
    //    private DSItemList<DSMicroarray> mArrayVector = null;
    private org.geworkbench.util.pathwaydecoder.RankSorter[] ranks = null;
    private double d = 1;
    private double d2 = 1;
    private double maxD2 = 1;
    private double ratio = 1;

    boolean copulaTransform = false;
    double sigma = -1.0;

    double[][] probMatrix;

    public void setCopulaTransform(boolean copulaTransform) {
        this.copulaTransform = copulaTransform;
    }

    public boolean isCopulaTransform() {
        return copulaTransform;
    }

    private void initializeNormal(int size) {
        if (copulaTransform) {
            initializeNormalCopula();
        } else {
            //      initializeNormal(.04);
            d = Math.pow((double) size, 4.0 / 5.0) * 0.25406; ///4.57305/18;
            d2 = d * d * 2;
            maxD2 = (d * 3) * (d * 3);
            ratio = (double) normalBin / maxD2;

            for (int i = 0; i < normalBin; i++) {
                double x = (double) i / ratio;
                normal[i] = Math.exp(-x / d2);
            }
        }
    }

    private void initializeNormalCopula() {
        if (sigma > 0) {
            initializeNormalCopula(sigma);
        } else {
            initializeNormalCopula(0.04);
        }
    }

    private void initializeNormalCopula(double sigma) {
        //    d = Math.pow( (double) size, 4.0 / 5.0) * 0.25406; ///4.57305/18;
        d = sigma;
        d2 = d * d * 2;
        maxD2 = (d * 3) * (d * 3);
        ratio = (double) normalBin / maxD2;

        for (int i = 0; i < normalBin; i++) {
            double x = (double) i / ratio;
            normal[i] = Math.exp(-x / d2);
        }
    }

    //public GeneGeneRelationship(JMicroarrayVisualizer mav, int t, boolean computeBackground ) {
    public GeneGeneRelationship(DSMicroarraySetView<T, Q> view, int t, boolean computeBackground) {
        checkView(view);
        // The number of microarray is strictly determined by the phenotypic selection
        miSpace = new int[miSteps][];
        miProb = new double[miSteps][];
        mi3DSpace = new int[miSteps][][];
        mi3DProb = new double[miSteps][][];
        for (int i = 0; i < miSteps; i++) {
            miSpace[i] = new int[miSteps];
            miProb[i] = new double[miSteps];
            mi3DSpace[i] = new int[miSteps][];
            mi3DProb[i] = new double[miSteps][];
            for (int j = 0; j < miSteps; j++) {
                mi3DSpace[i][j] = new int[miSteps];
                mi3DProb[i][j] = new double[miSteps];
            }
        }
        if (view.items().size() > 0) {
            type = t;
            ranks = new RankSorter[view.items().size()];
            for (int i = 0; i < view.items().size(); i++) {
                ranks[i] = new RankSorter();
            }
            if (type == RANK_MI) {
                maPerMIStep = (double) view.items().size() / (double) miSteps;
            }
            if (computeBackground) {
                computeBackground(iterationDefault, ranks.length, type, backgroundType);
            }
            if (type == RANK_CHI2) {
                //          initializeNormal(.04);
                //      }else{
                initializeNormal(ranks.length);
            }
        }
    }

    private void checkView(DSMicroarraySetView<T, Q> view) {
        if ((this.view.markers().getID() != view.markers().getID()) || (this.view.items().getID() != view.items().getID()) || (this.view.getDataSet() != view.getDataSet())) {
            this.view = view;
            //            setMArrayVector(marV);
        }
    }

    public GeneGeneRelationship(int maNo, int t, boolean computeBackground, double sigma) {
        // The number of microarray is strictly determined by the phenotypic selection
        miSpace = new int[miSteps][];
        miProb = new double[miSteps][];
        mi3DSpace = new int[miSteps][][];
        mi3DProb = new double[miSteps][][];
        for (int i = 0; i < miSteps; i++) {
            miSpace[i] = new int[miSteps];
            miProb[i] = new double[miSteps];
            mi3DSpace[i] = new int[miSteps][];
            mi3DProb[i] = new double[miSteps][];
            for (int j = 0; j < miSteps; j++) {
                mi3DSpace[i][j] = new int[miSteps];
                mi3DProb[i][j] = new double[miSteps];
            }
        }

        type = t;
        ranks = new RankSorter[maNo];
        for (int i = 0; i < maNo; i++) {
            ranks[i] = new RankSorter();
        }
        if (type == RANK_MI) {
            maPerMIStep = (double) maNo / (double) miSteps;
        }
        if (computeBackground) {
            computeBackground(iterationDefault, ranks.length, type, backgroundType);
        }
        initializeNormalCopula(sigma);
        this.sigma = sigma;
    }

    public GeneGeneRelationship(DSMicroarraySetView<T, Q> view, int t, boolean computeBackground, double sigma) {
        checkView(view);
        // The number of microarray is strictly determined by the phenotypic selection
        miSpace = new int[miSteps][];
        miProb = new double[miSteps][];
        mi3DSpace = new int[miSteps][][];
        mi3DProb = new double[miSteps][][];
        for (int i = 0; i < miSteps; i++) {
            miSpace[i] = new int[miSteps];
            miProb[i] = new double[miSteps];
            mi3DSpace[i] = new int[miSteps][];
            mi3DProb[i] = new double[miSteps][];
            for (int j = 0; j < miSteps; j++) {
                mi3DSpace[i][j] = new int[miSteps];
                mi3DProb[i][j] = new double[miSteps];
            }
        }

        type = t;
        ranks = new RankSorter[view.items().size()];
        for (int i = 0; i < view.items().size(); i++) {
            ranks[i] = new RankSorter();
        }
        if (type == RANK_MI) {
            maPerMIStep = (double) view.items().size() / (double) miSteps;
        }
        if (computeBackground) {
            computeBackground(iterationDefault, ranks.length, type, backgroundType);
        }
        initializeNormalCopula(sigma);
        this.sigma = sigma;
    }

    //    private void setMArrayVector(DSItemList<DSMicroarray> marV) {
    //        if((marV != null) && (marV.size() > 0)) {
    //            mArrayVector = marV;
    //        } else {
    //            mArrayVector = mArraySet;
    //        }
    //    }

    public GeneGeneRelationship(DSMicroarraySet maSet, DSItemList<DSMicroarray> marV, int microarrayNo, int t, boolean computeBackground) {
        checkView(view);
        // The number of microarray is strictly determined by the phenotypic selection
        miSpace = new int[miSteps][];
        miProb = new double[miSteps][];
        mi3DSpace = new int[miSteps][][];
        mi3DProb = new double[miSteps][][];
        for (int i = 0; i < miSteps; i++) {
            miSpace[i] = new int[miSteps];
            miProb[i] = new double[miSteps];
            mi3DSpace[i] = new int[miSteps][];
            mi3DProb[i] = new double[miSteps][];
            for (int j = 0; j < miSteps; j++) {
                mi3DSpace[i][j] = new int[miSteps];
                mi3DProb[i][j] = new double[miSteps];
            }
        }

        type = t;
        ranks = new RankSorter[microarrayNo];
        for (int i = 0; i < microarrayNo; i++) {
            ranks[i] = new RankSorter();
        }
        if (type == RANK_MI) {
            maPerMIStep = (double) microarrayNo / (double) miSteps;
        }
        if (computeBackground) {
            computeBackground(iterationDefault, ranks.length, type, backgroundType);
        }
        initializeNormal(ranks.length);
    }

    public ArrayList regression(int firstGeneId, int secondGeneId, double threshold, DSItemList<DSMicroarray> mArrays) {
        int markerNo = view.markers().size(); // visualizer.getMarkerNo();
        int mArrayNo = 0;
        if ((mArrays != null) && (mArrays.size() > 0)) {
            mArrayNo = mArrays.size();
        } else {
            mArrayNo = view.items().size();
        }
        ArrayList coefficients = new ArrayList();
        if (secondGeneId >= 0) {
            GeneNetworkEdgeImpl rc = getScore(firstGeneId, secondGeneId, threshold, mArrays);
            if (rc != null) {
                rc.setPValue(org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil.getLogP(rc.getMI(), mArrayNo));
                coefficients.add(rc);
            }
        } else {
            for (secondGeneId = 0; secondGeneId < markerNo; secondGeneId++) {
                if (view.markers().get(secondGeneId).getLabel().equalsIgnoreCase("ExoBCL6")) {
                    break;
                }
                if (secondGeneId != firstGeneId) {
                    if (view.markers().get(secondGeneId).getLabel().equalsIgnoreCase("1936_s_at")) {
                        int x = 2;
                    }
                    GeneNetworkEdgeImpl rc = getScore(firstGeneId, secondGeneId, threshold, mArrays);
                    if (rc != null) {
                        rc.setPValue(org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil.getLogP(rc.getMI(), mArrayNo));
                        coefficients.add(rc);
                    }
                }
            }
        }
        return coefficients;
    }

    public GeneNetworkEdgeImpl getScore(int firstGeneId, int secondGeneId, double threshold, DSItemList<DSMicroarray> mArrays) {
        switch (type) {
            case STD_REGRESSION:
                return stdRegression(firstGeneId, secondGeneId, threshold);
            case PEARSON:
                return rankRegression(firstGeneId, secondGeneId, threshold);
            case RANK_MI:
                return miScore(firstGeneId, secondGeneId, threshold, mArrays);
            case RANK_CHI2:
            default:
                return newMIScore(firstGeneId, secondGeneId, threshold, mArrays); //rankChi2Score(firstGeneId, secondGeneId, threshold);
        }
    }

    private GeneNetworkEdgeImpl rankChi2Score(int firstGeneId, int secondGeneId, double threshold) {
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i); // visualizer.microarrays().get(i);
            ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
            ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
        }
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].ix = j;
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iy = j;
        }
        ChiSquareDistribution chiSquare = new ChiSquareDistribution(size * size);
        int[][] quadrant = new int[size][];
        for (int qid = 0; qid < size; qid++) {
            quadrant[qid] = new int[size];
            for (int qid1 = 0; qid1 < size; qid1++) {
                quadrant[qid][qid1] = 0;
            }
        }
        for (int i = 0; i < view.items().size(); i++) {
            double dx = (double) (view.items().size() + 1) / (double) size;
            int qx = (int) ((double) ranks[i].ix / dx);
            int qy = (int) ((double) ranks[i].iy / dx);
            quadrant[qx][qy]++;
        }
        double chi2 = 0;
        double expected = (double) view.items().size() / (double) size / (double) size;
        for (int qx = 0; qx < size; qx++) {
            for (int qy = 0; qy < size; qy++) {
                chi2 += Math.pow((double) quadrant[qx][qy] - expected, 2.0) / expected;
            }
        }
        double cdf = chiSquare.getCDF(chi2);
        double score = 1.0 - cdf;
        double v = Math.exp(-chi2);
        GeneNetworkEdgeImpl rc = null;
        if (v <= threshold) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(firstGeneId);
            rc.setMarker2(view.markers().get(secondGeneId)); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId);
            rc.setA(0);
            rc.setB(0);
            rc.setMI(v);
        }
        return rc;
    }

    public double getPearsonCorrelation(int firstGeneId, int secondGeneId) {
        //int maNo = mArraySet.getPhenoGroup().size();
        int maNo = view.items().size();
        ranks = new RankSorter[maNo];
        for (int i = 0; i < maNo; i++) {
            //IMicroarray ma = mArraySet.getPhenoGroup().getItem(i);
            DSMicroarray ma = view.items().get(i);
            ranks[i] = new RankSorter();
            ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
            ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
        }
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].ix = j;
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iy = j;
        }

        //    int countPos = 0;
        //    int countNeg = 0;
        //    int t1 = (int) ( (double) microarrayNo / 3.0);
        //    int t2 = microarrayNo - t1;
        //    for(int i = 0; i < microarrayNo; i++) {
        //      if(ranks[i].ix < t1) {
        //        if(ranks[i].iy <t1) {
        //          countPos++;
        //        } else if(ranks[i].iy > t2) {
        //          countNeg++;
        //        }
        //      } else if(ranks[i].ix > t2) {
        //        if(ranks[i].iy > t2) {
        //          countPos++;
        //        } else if(ranks[i].iy < t1) {
        //          countNeg++;
        //        }
        //      }
        //    }
        //    double score = (double)Math.max(countPos, countNeg)/(double)microarrayNo*3.0/2.0;
        //    return score;
        //    double[][] pairs = new double[microarrayNo][];

        double Sxx = 0;
        double Sxy = 0;
        double Syy = 0;
        double Sx = 0;
        double Sy = 0;
        double n = (double) maNo;
        for (int i = 0; i < maNo; i++) {
            double x = ranks[i].ix;
            double y = ranks[i].iy;
            Sx += x;
            Sy += y;
            Sxx += x * x;
            Sxy += x * y;
            Syy += y * y;
        }
        double rho = (Sxy - Sx * Sy / n) / Math.sqrt((Sxx - Sx * Sx / n) * (Syy - Sy * Sy / n));
        return rho;
    }

    private GeneNetworkEdgeImpl rankRegression(int firstGeneId, int secondGeneId, double threshold) {
        double rho = getPearsonCorrelation(firstGeneId, secondGeneId);
        GeneNetworkEdgeImpl rc = null;
        if (Math.abs(rho) >= threshold) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(firstGeneId);
            rc.setMarker2(view.markers().get(secondGeneId)); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId);
            rc.setA(rho);
            rc.setB(0);
            rc.setMI(Math.abs(rho));
        }
        int bin = (int) ((rho + 1.0) * (binNo / 2));
        if (bin < binNo - 1) {
            histogram[bin]++;
            histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
        }

        return rc;
    }

    /**
     * Computes the mutual information of the two genes
     *
     * @param firstGeneId
     * @param secondGeneId
     * @param threshold
     * @return
     */
    public GeneNetworkEdgeImpl miScore(int firstGeneId, int secondGeneId, double threshold, DSItemList<DSMicroarray> mArrays) {
        if ((mArrays != null) && (mArrays.size() > 0)) {
            if (mArrays.size() != ranks.length) {
                ranks = new RankSorter[mArrays.size()];
                for (int i = 0; i < ranks.length; i++) {
                    ranks[i] = new RankSorter();
                }
                maPerMIStep = (double) ranks.length / (double) miSteps;
            }
            for (int i = 0; i < mArrays.size(); i++) {
                DSMicroarray ma = mArrays.get(i);
                ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
                ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
            }
        } else {
            if (view.items().size() != ranks.length) {
                ranks = new RankSorter[view.items().size()];
            }
            for (int i = 0; i < view.items().size(); i++) {
                DSMicroarray ma = view.items().get(i);
                ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
                ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
            }
        }

        double mi = getMutualInformationXY();
        double p = org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil.getLogP(mi, ranks.length);
        int bin = Math.min((int) (mi * binNo), binNo - 1);
        histogram[bin]++;
        histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
        //double mi = getMIScore(firstGeneId, secondGeneId, threshold);//getMutualInformationXY(ranks);
        GeneNetworkEdgeImpl rc = null;
        //if (mi >= threshold) {
        if ((GeneNetworkEdgeImpl.usePValue && (p >= threshold)) || (!GeneNetworkEdgeImpl.usePValue && (mi >= threshold))) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(firstGeneId);
            rc.setMarker2(view.markers().get(secondGeneId)); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId));
            rc.setA(1);
            rc.setMI(mi);
            rc.setPValue(p);
        }
        return rc;
    }

    public GeneNetworkEdgeImpl miScore(double[] xData, double[] yData, double threshold) {
        ranks = new RankSorter[xData.length];
        for (int i = 0; i < ranks.length; i++) {
            ranks[i] = new RankSorter();
        }

        for (int i = 0; i < ranks.length; i++) {
            ranks[i].x = xData[i];
            ranks[i].y = yData[i];
        }

        double mi = getMutualInformationXY();
        GeneNetworkEdgeImpl rc = new GeneNetworkEdgeImpl();
        rc.setMI(mi);
        return rc;
        /*
             double p = PathwayDecoderUtil.getLogP(mi, microarrayNo);
             int bin = (int) (mi * binNo);
             histogram[bin]++;
             histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
             //double mi = getMIScore(firstGeneId, secondGeneId, threshold);//getMutualInformationXY(ranks);
             GeneNetworkEdgeImpl rc = null;
             //if (mi >= threshold) {
             if ( (GeneNetworkEdgeImpl.usePValue && (p >= threshold)) ||
            (!GeneNetworkEdgeImpl.usePValue && (mi >= threshold))) {
          rc = new GeneNetworkEdgeImpl();
          rc.setId1(0);
          //rc.setMarker2(0); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId));
          rc.setA(1);
          rc.setMI(mi);
          rc.setPValue(p);
             }
             return rc;
         */
    }

    public GeneNetworkEdgeImpl newMIScore(double[] xData, double[] yData, double threshold) {
        ranks = new RankSorter[xData.length];
        for (int i = 0; i < ranks.length; i++) {
            ranks[i] = new RankSorter();
        }

        for (int i = 0; i < ranks.length; i++) {
            ranks[i].x = xData[i];
            ranks[i].y = yData[i];
        }

        double mi = getNewMutualInformationXY();
        double p = org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil.getLogP(mi, xData.length);
        int bin = (int) (mi * binNo);
        if ((bin >= 0) && (bin < binNo)) {
            histogram[bin]++;
            histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
        }
        //double mi = getMIScore(firstGeneId, secondGeneId, threshold);//getMutualInformationXY(ranks);
        GeneNetworkEdgeImpl rc = null;
        //if (mi >= threshold) {
        if ((GeneNetworkEdgeImpl.usePValue && (p >= threshold)) || (!GeneNetworkEdgeImpl.usePValue && (mi >= threshold))) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(0);
            //rc.setMarker2(0); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId));
            rc.setA(1);
            rc.setMI(mi);
            rc.setPValue(p);
        }
        return rc;
    }

    public GeneNetworkEdgeImpl newMIScore(int firstGeneId, int secondGeneId, double threshold, DSItemList<DSMicroarray> mArrays) {
        if ((mArrays != null) && (mArrays.size() > 0)) {
            if (mArrays.size() != ranks.length) {
                ranks = new RankSorter[mArrays.size()];
                for (int i = 0; i < ranks.length; i++) {
                    ranks[i] = new RankSorter();
                }
            }
            for (int i = 0; i < mArrays.size(); i++) {
                DSMicroarray ma = mArrays.get(i);
                ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
                ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
            }
        } else {
            if (view.items().size() != ranks.length) {
                ranks = new RankSorter[view.items().size()];
            }
            for (int i = 0; i < view.items().size(); i++) {
                //IMicroarray ma = visualizer.microarrays().get(i);
                DSMicroarray ma = view.items().get(i);
                ranks[i].x = ma.getMarkerValue(firstGeneId).getValue();
                ranks[i].y = ma.getMarkerValue(secondGeneId).getValue();
            }
        }

        double mi = getNewMutualInformationXY();
        double p = org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil.getLogP(mi, view.items().size());
        int bin = (int) (mi * binNo);
        if ((bin >= 0) && (bin < binNo)) {
            histogram[bin]++;
            histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
        }
        //double mi = getMIScore(firstGeneId, secondGeneId, threshold);//getMutualInformationXY(ranks);
        GeneNetworkEdgeImpl rc = null;
        //if (mi >= threshold) {
        if ((GeneNetworkEdgeImpl.usePValue && (p >= threshold)) || (!GeneNetworkEdgeImpl.usePValue && (mi >= threshold))) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(firstGeneId);
            rc.setMarker2(view.markers().get(secondGeneId)); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId));
            rc.setA(1);
            rc.setMI(mi);
            rc.setPValue(p);
        }
        return rc;
    }

    double ansatz(double dx2, double h2) {
        int i = (int) (ratio * dx2);
        return normal[i];
        //    return Math.exp(-dx2/h2);
    }

    double getFxy(double x, double y) {
        TruncatedGaussianBivariate biGauss = new TruncatedGaussianBivariate(sigma);
        double fxy = 0;

        for (int i = 0; i < ranks.length; i++) {

            double dx = (double) ranks[i].ix - x;
            double dy = (double) ranks[i].iy - y;

            if (copulaTransform) {
                dx = dx / (double) ranks.length;
                dy = dy / (double) ranks.length;
            }

            fxy += biGauss.getProbability(dx, dy);
        }

        return fxy / (double) ranks.length;

    }

    double getFx(double x) {
        TruncatedGaussianUnivariate uniGauss = new TruncatedGaussianUnivariate(sigma);

        double fx = 0;

        for (int i = 0; i < ranks.length; i++) {

            double dx = (double) ranks[i].ix - x;

            if (copulaTransform) {
                dx = dx / (double) ranks.length;
            }

            fx += uniGauss.getProbability(dx);
        }

        return fx / (double) ranks.length;
    }

    double getFy(double y) {
        TruncatedGaussianUnivariate uniGauss = new TruncatedGaussianUnivariate(sigma);

        double fy = 0;

        for (int i = 0; i < ranks.length; i++) {

            double dy = (double) ranks[i].iy - y;

            if (copulaTransform) {
                dy = dy / (double) ranks.length;
            }

            fy += uniGauss.getProbability(dy);
        }

        return fy / (double) ranks.length;
    }

    double getFxNew(int x, int y) {
        TruncatedGaussianUnivariate uniGauss = new TruncatedGaussianUnivariate(sigma);
        TruncatedGaussianBivariate biGauss = new TruncatedGaussianBivariate(sigma);

        //      double sigma2x2 = 2 * Math.pow(sigma, 2.0);

        double fxy = 0;
        double fx = 0;
        double fy = 0;

        for (int i = 0; i < ranks.length; i++) {

            int dxRank = Math.abs(ranks[i].ix - x);
            int dyRank = Math.abs(ranks[i].iy - y);

            double dx = dxRank;
            double dy = dyRank;

            if (copulaTransform) {
                dx = dxRank / (double) ranks.length;
                dy = dyRank / (double) ranks.length;
            }

            int maxRank = Math.max(dxRank, dyRank);
            int minRank = Math.min(dxRank, dyRank);
            if (probMatrix[minRank][maxRank] == -1) {
                probMatrix[minRank][maxRank] = biGauss.getProbability(dx, dy);
                if (probMatrix[0][dxRank] == -1) {
                    probMatrix[0][dxRank] = uniGauss.getProbability(dx);
                }
                if (probMatrix[0][dyRank] == -1) {
                    probMatrix[0][dyRank] = uniGauss.getProbability(dy);
                }
            }

            fxy += probMatrix[minRank][maxRank];
            fx += probMatrix[0][dxRank];
            fy += probMatrix[0][dyRank];

            //          fxy += Math.exp(- dxy2 / sigma2x2);
            //          fx += Math.exp(- dx2 / sigma2x2);
            //          fy += Math.exp(- dy2 / sigma2x2);

            //          fxy += biGauss.getProbability(dx, dy);
            //          fx += uniGauss.getProbability(dx);
            //          fy += uniGauss.getProbability(dy);

        }

        return ((ranks.length * fxy) / (fx * fy));
    }

    double getFx(double x, double y) {

        double fxy = 0;
        double fx = 0;
        double fy = 0;
        double sigma2 = 2 * Math.pow(sigma, 2.0);
        for (int i = 0; i < ranks.length; i++) {

            double dx = (double) ranks[i].ix - x;
            double dy = (double) ranks[i].iy - y;

            if (copulaTransform) {
                dx = dx / (double) ranks.length;
                dy = dy / (double) ranks.length;
            }

            //      if(dx > ranks.length/2) {
            //        dx = ranks.length - dx;
            //      }
            //      if(dy > ranks.length/2) {
            //        dy = ranks.length - dy;
            //      }
            double vxy2 = (dx * dx + dy * dy);
            double vx2 = dx * dx;
            double vy2 = dy * dy;
            if (vxy2 < maxD2) {
                fxy += ansatz(vxy2, d2);
                //do it the slow way
                //          fxy += Math.exp(- vxy2 / sigma2);
            }
            //      if(Math.abs(dx) < d) {
            //        if(dx > 0) {
            //          nx0++;
            //        } else {
            //          nx1++;
            //        }
            //      }
            //      if(Math.abs(dy) < d) {
            //        if(dy > 0) {
            //          ny0++;
            //        } else {
            //          ny1++;
            //        }
            //      }
            if (vx2 < maxD2) {
                fx += ansatz(vx2, d2); //norm.getDensity(v);
                //        fx += Math.exp(- vx2 / sigma2);
            }
            if (vy2 < maxD2) {
                fy += ansatz(vy2, d2); //norm.getDensity(v);
                //        fy += Math.exp(- vy2 / sigma2);
            }
        }
        //return fxy/(double)ranks.length/Math.sqrt(2 * Math.PI)/d*1.5;
        //    double ncx = 0.5 + Math.min(nx0,nx1)/Math.max(nx0,nx1)/2;
        //    double ncy = 0.5 + Math.min(ny0,ny1)/Math.max(ny0,ny1)/2;
        //    fx = Math.sqrt(2*Math.PI)*d;
        //    return fxy/fx/fx/ncx/ncy * ranks.length*1.05;

        //    return fxy / fx / fy * ranks.length;
        //    return fxy / fx / fy * ranks.length /
        //        Math.pow(Math.log( (double) ranks.length), 0.004);
        return fxy / fx / fy * ranks.length;
    }

    double getNewMutualInformationXY() {
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 1; j < ranks.length; j++) {
            if (ranks[j].x == ranks[j - 1].x) {
                ranks[j - 1].x += 0.1 * Math.random() - 0.05;
            }
        }
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].ix = j;
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 1; j < ranks.length; j++) {
            if (ranks[j].y == ranks[j - 1].y) {
                ranks[j - 1].y += 0.1 * Math.random() - 0.05;
            }
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iy = j;
        }
        //    double sum = computeNewMIFromArrays(ranks);
        double sum = computeNewMIFromArrays(ranks);
        return sum;
    }

    private double computeNewMIFromArrays(RankSorter[] rankSorter) {
        probMatrix = new double[rankSorter.length][rankSorter.length];
        for (int i = 0; i < rankSorter.length; i++) {
            for (int j = 0; j < rankSorter.length; j++) {
                probMatrix[i][j] = -1;
            }
        }

        ranks = rankSorter;

        double sum = 0;
        for (int i = 0; i < rankSorter.length; i++) {
            //          double v = getFxNew( (double) rankSorter[i].ix, (double) rankSorter[i].iy);
            double v = getFxNew(rankSorter[i].ix, rankSorter[i].iy);
            sum += Math.log(v);
        }

        double mi = sum / (double) ranks.length;
        if (mi <= 0) {
            System.out.println(mi + "");
        }
        return Math.max(mi, 0);
    }

    //  private double computeNewMIFromArrays(RankSorter[] rankSorter) {
    //    double sum = 0.0;
    //    ranks = rankSorter;
    //    for (int i = 0; i < rankSorter.length; i++) {
    //      double v = getFx( (double) rankSorter[i].ix, (double) rankSorter[i].iy);
    //      sum += Math.log(v);
    //    }
    //    double entropy = -Math.log(1 / (double) ranks.length);
    // //    double correction = (Math.log((double)ranks.length)/8833 );
    //    double correction = 0; //0.0065;
    //    return Math.max(sum / (double) ranks.length / 2 / entropy + correction, 0);
    //
    //    //Normalzed
    // //    return Math.max(sum / (double) ranks.length / 2 / Math.log((double)ranks.length), 0);
    //  }


    double getMutualInformationXY() {
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 1; j < ranks.length; j++) {
            if (ranks[j].x == ranks[j - 1].x) {
                ranks[j - 1].x += 0.1 * Math.random() - 0.05;
            }
        }
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].ix = j;
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 1; j < ranks.length; j++) {
            if (ranks[j].y == ranks[j - 1].y) {
                ranks[j - 1].y += 0.1 * Math.random() - 0.05;
            }
        }
        Arrays.sort(ranks, RankSorter.SORT_Y);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iy = j;
        }
        double mi = computeMIFromArrays(ranks);
        return mi;
    }

    private double computeMIFromArrays(RankSorter[] rankSorter) {
        double H_xy = 0;
        double H_x = -Math.log(1 / (double) miSteps);
        double H_y = H_x;
        /* Ok 1 */
        for (int i = 0; i < miSteps; i++) {
            for (int j = 0; j < miSteps; j++) {
                miSpace[i][j] = 0;
                ;
            }
        }
        /* Ok 2 */
        for (int i = 0; i < rankSorter.length; i++) {
            int x = (int) ((double) rankSorter[i].ix / maPerMIStep);
            int y = (int) ((double) rankSorter[i].iy / maPerMIStep);
            miSpace[x][y]++;
        }
        /* Ok 3 */
        // Compute the value for the initial block
        int count = 0;
        for (int di = 0; di < miBlocks; di++) {
            for (int dj = 0; dj < miBlocks; dj++) {
                count += miSpace[di][dj];
            }
        }
        /* Ok 4 */
        double sumP = 0;
        for (int i = 0; i < miSteps; i++) {
            int count1 = count;
            for (int j = 0; j < miSteps; j++) {
                double p = (double) count1 / ((double) rankSorter.length * (double) miBlocks * (double) miBlocks);
                sumP += p;
                if (p > 0) {
                    H_xy -= p * Math.log(p);
                }
                miProb[i][j] = (double) count1;
                for (int off = 0; off < miBlocks; off++) {
                    /**
                     * Wrap around the torus
                     */
                    int x = (i + off) % miSteps;
                    int y = (j + miBlocks) % miSteps;
                    count1 -= miSpace[x][j];
                    count1 += miSpace[x][y];
                }
            }
            for (int off = 0; off < miBlocks; off++) {
                int x = (i + miBlocks) % miSteps;
                count -= miSpace[i][off];
                count += miSpace[x][off];
            }
        }
        double mi = (H_x + H_y - H_xy) / (H_x + H_y);
        return mi;
    }

    private GeneNetworkEdgeImpl stdRegression(int firstGeneId, int secondGeneId, double threshold) {
        GeneNetworkEdgeImpl rc = null;
        double[][] pairs = new double[view.items().size()][];
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i); // visualizer.microarrays().get(i);
            pairs[i] = new double[2];
            pairs[i][0] = ma.getMarkerValue(firstGeneId).getValue();
            pairs[i][1] = ma.getMarkerValue(secondGeneId).getValue();
        }
        org.geworkbench.util.pathwaydecoder.Regression r = new org.geworkbench.util.pathwaydecoder.Regression(pairs);
        r.calculate();
        if (r.getCoefCorrel() > threshold) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(firstGeneId);
            rc.setMarker2(view.markers().get(secondGeneId)); //visualizer.getmicroarraySet().getGenericMarker(secondGeneId);
            rc.setA(r.getA());
            rc.setB(r.getB());
            rc.setMI(r.getCoefCorrel());
        }
        return rc;
    }

    public GeneNetworkEdgeImpl regression3D(int id1, int id2, int id3, double threshold) {
        //double norm = (1.0 - ((double)blockNo - 1)/(double)miSteps * 2) * (1.0 - ((double)blockNo - 1)/(double)miSteps * 2);
        RankSorter3D[] ranks = new RankSorter3D[view.items().size()];
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i); // visualizer.microarrays().get(i);
            ranks[i] = new RankSorter3D();
            ranks[i].x = ma.getMarkerValue(id1).getValue();
            ranks[i].y = ma.getMarkerValue(id2).getValue();
            ranks[i].z = ma.getMarkerValue(id3).getValue();
        }
        Arrays.sort(ranks, org.geworkbench.util.pathwaydecoder.RankSorter3D.SORT_X);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].ix = j;
        }
        Arrays.sort(ranks, RankSorter3D.SORT_Y);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iy = j;
        }
        Arrays.sort(ranks, org.geworkbench.util.pathwaydecoder.RankSorter3D.SORT_Z);
        for (int j = 0; j < ranks.length; j++) {
            ranks[j].iz = j;
        }
        for (int i = 0; i < miSteps; i++) {
            for (int j = 0; j < miSteps; j++) {
                for (int k = 0; k < miSteps; k++) {
                    mi3DSpace[i][j][k] = 0;
                }
            }
        }
        for (int i = 0; i < ranks.length; i++) {
            int x = (int) ((double) ranks[i].ix / maPerMIStep);
            int y = (int) ((double) ranks[i].iy / maPerMIStep);
            int z = (int) ((double) ranks[i].iz / maPerMIStep);
            mi3DSpace[x][y][z]++;
        }

        double H_xyz = 0;
        double H_x = -Math.log(1 / (double) miSteps);
        double H_y = H_x;
        double H_z = H_x;
        /**
         * Compute the value for the initial block
         */
        int count = 0;
        for (int di = 0; di < miBlocks; di++) {
            for (int dj = 0; dj < miBlocks; dj++) {
                for (int dk = 0; dk < miBlocks; dk++) {
                    count += mi3DSpace[di][dj][dk];
                }
            }
        }
        double sumP = 0;
        for (int i = 0; i < miSteps; i++) {
            int count1 = count;
            for (int j = 0; j < miSteps; j++) {
                int count2 = count1;
                for (int k = 0; k < miSteps; k++) {
                    double p = (double) count2 / (double) view.items().size() / (double) miBlocks / (double) miBlocks / miBlocks;
                    sumP += p;
                    if (p > 0) {
                        H_xyz -= p * Math.log(p);
                    }
                    mi3DProb[i][j][k] = (double) count2;
                    for (int offX = 0; offX < miBlocks; offX++) {
                        for (int offY = 0; offY < miBlocks; offY++) {

                            /**
                             * Wrap around the torus in the Z direction
                             */
                            int x = (i + offX) % miSteps;
                            int y = (j + offY) % miSteps;
                            int z = (k + miBlocks) % miSteps;
                            count2 -= mi3DSpace[x][y][k];
                            count2 += mi3DSpace[x][y][z];
                        }
                    }
                }
                for (int offX = 0; offX < miBlocks; offX++) {
                    for (int offZ = 0; offZ < miBlocks; offZ++) {
                        int x = (i + offX) % miSteps;
                        int y = (j + miBlocks) % miSteps;
                        int z = offZ;
                        count1 -= mi3DSpace[x][j][z];
                        count1 += mi3DSpace[x][y][z];
                    }
                }
            }
            for (int offY = 0; offY < miBlocks; offY++) {
                for (int offZ = 0; offZ < miBlocks; offZ++) {
                    int x = (i + miBlocks) % miSteps;
                    int y = offY;
                    int z = offZ;
                    count -= mi3DSpace[i][y][z];
                    count += mi3DSpace[x][y][z];
                }
            }
        }
        GeneNetworkEdgeImpl rc = null;
        double mi = (H_x + H_y + H_z - H_xyz) / (H_x + H_y + H_z);
        int bin = (int) (mi * binNo);
        histogram[bin]++;
        histogram[binNo - 1] = Math.max(histogram[binNo - 1], bin);
        if (mi >= threshold) {
            rc = new GeneNetworkEdgeImpl();
            rc.setId1(id1);
            rc.setMarker2(view.markers().get(id3)); //visualizer.getmicroarraySet().getGenericMarker(id3);
            rc.setA(1);
            rc.setMI(mi);
        }
        return rc;
    }

    //  double[] getHistogram() {
    //    return histogram;
    //  }

    //  double[] getBackground() {
    //    return background;
    //  }

    public void computeBackground(int iterationNo, int size, int type) {
        computeBackground(iterationNo, size, type, backgroundType);
    }

    public void computeBackground(int iterationNo, int size, int type, int model) {
        computeBackground(iterationNo, size, type, model, 36.0);
    }

    public void computeBackground(int iterationNo, int size, int type, int model, double noise) {
        // If model is 0, then simply randomize the data.
        // If model is 1, then use Hartemink clipped random walk model

        maPerMIStep = (double) size / (double) miSteps;
        for (int i = 0; i < background.length; i++) {
            background[i] = 0.0;
        }
        RankSorter[] ranks = new RankSorter[size];
        for (int i = 0; i < size; i++) {
            ranks[i] = new RankSorter();
            ranks[i].id = i;
            ranks[i].ix = i;
        }
        //    initializeNormal(ranks.length);
        double increment = 1.0 / (double) iterationNo;
        double randMi = 0.0;
        for (int i = 0; i < binNo; i++) {
            background[i] = 0.0;
        }
        double sum = 0;
        for (int iteration = 0; iteration < iterationNo; iteration++) {
            if (iteration % 1000 == 0) {
                System.out.println("Computed background for iteration " + iteration);
            }
            // Generate the Null Model
            switch (model) {
                case 0:
                    {
                        for (int i = 0; i < size; i++) {
                            ranks[i].x = Math.random();
                        }
                        Arrays.sort(ranks, RankSorter.SORT_X);
                        for (int i = 0; i < size; i++) {
                            ranks[i].ix = i;
                        }
                        for (int i = 0; i < size; i++) {
                            ranks[i].y = Math.random();
                        }
                        Arrays.sort(ranks, RankSorter.SORT_Y);
                        for (int i = 0; i < size; i++) {
                            ranks[i].iy = i;
                        }
                    }
                    break;
                case 1:
                    {

                        double value = Math.random() * (double) 100;
                        for (int i = 0; i < size; i++) {
                            ranks[i].x = value;
                            for (int j = 0; j < 5; j++) {
                                //value += (Math.random() * 20.0) - 15;
                                //value += (Math.random() * 20.0) - 10;
                                value += (Math.random() * noise) - (noise / 2.0);
                                if (value > 100) {
                                    value = 100;
                                }
                                if (value < 0) {
                                    value = 0;
                                }
                            }
                        }
                        Arrays.sort(ranks, RankSorter.SORT_X);
                        for (int i = 0; i < size; i++) {
                            ranks[i].ix = i;
                        }
                        value = Math.random() * (double) 100;
                        for (int i = 0; i < size; i++) {
                            ranks[i].y = value;
                            for (int j = 0; j < 5; j++) {
                                //value += (Math.random() * 30.0) - 15;
                                //value += (Math.random() * 20.0) - 10;
                                value += (Math.random() * noise) - (noise / 2.0);
                                if (value > 100) {
                                    value = 100;
                                }
                                if (value < 0) {
                                    value = 0;
                                }
                            }
                        }
                        Arrays.sort(ranks, RankSorter.SORT_Y);
                        for (int i = 0; i < size; i++) {
                            ranks[i].iy = i;
                        }
                    }
                    break;
            }
            if (type == RANK_CHI2) {
                randMi = computeNewMIFromArrays(ranks);
            } else {
                randMi = computeMIFromArrays(ranks);
            }
            int bin = (int) (randMi * binNo);

            if (bin >= background.length - 1) {
                bin = background.length - 1;
            }

            background[bin] += increment;
            sum += increment;
        }
        //    try {
        //      BufferedWriter writer = new BufferedWriter(new FileWriter("background"
        //          + size + ".txt"));
        //      for (int i = 0; i < binNo; i++) {
        //        writer.write(i + "\t" + background[i]);
        //        writer.newLine();
        //      }
        //      writer.flush();
        //      writer.close();
        //    }
        //    catch (Exception ex) {
        //    }
    }

    public int[] getNumConnections(DSMicroarraySet microarraySet, double threshold, DSItemList<DSMicroarray> mArrays) {
        int[] indices = {1, 6, 12, 13, 14, 15, 16, 17, 18, 19};
        int numConnections[] = {0, 0};
        if ((microarraySet != null) && (microarraySet.size() > 0)) {
            int markerNo = microarraySet.size();
            //GeneGeneRelationship ggr = new GeneGeneRelationship(microarraySet, microarraySet, microarraySet, GeneGeneRelationship.RANK_MI, false);
            GeneGeneRelationship ggr = new GeneGeneRelationship(microarraySet, microarraySet, mArrays.size(), GeneGeneRelationship.RANK_CHI2, false);
            for (int i = 0; i < indices.length; i++) {
                for (int j = i + 1; j < indices.length; j++) {
                    GeneNetworkEdgeImpl edge = ggr.newMIScore(indices[i], indices[j], 0.0, mArrays);
                    //          double pvalue = PathwayDecoderUtil.getPValue(edge.getMI(), microarraySet.size());
                    //if ((edge != null) && (edge.getMI() > threshold)) {
                    //          if(pvalue < 0.000001) {
                    //          GeneNetworkEdgeImpl edge = ggr.getScore(i, j, 0.0, null);
                    //          if (edge.getMI() > 0.004) {
                    double mi = edge.getMI();
                    if (mi >= threshold) {
                        numConnections[0]++;
                    } else {
                        numConnections[1]++;
                    }
                    //          }
                }

            }
        }
        return numConnections;
    }

    public int getNumNonConnections(DSMicroarraySet microarraySet, double threshold, DSItemList<DSMicroarray> mArrays) {
        int[] indices = {1, 6, 12, 13, 14, 15, 16, 17, 18, 19};
        int numConnections = 0;
        if ((microarraySet != null) && (microarraySet.size() > 0)) {
            int markerNo = microarraySet.size();
            //GeneGeneRelationship ggr = new GeneGeneRelationship(microarraySet, microarraySet, microarraySet, GeneGeneRelationship.RANK_MI, false);
            GeneGeneRelationship ggr = new GeneGeneRelationship(microarraySet, microarraySet, mArrays.size(), GeneGeneRelationship.RANK_MI, false);
            for (int i = 0; i < indices.length; i++) {
                for (int j = i + 1; j < indices.length; j++) {
                    GeneNetworkEdgeImpl edge = ggr.newMIScore(indices[i], indices[j], 0.0, mArrays);
                    //          double pvalue = PathwayDecoderUtil.getPValue(edge.getMI(), microarraySet.size());
                    //if ((edge != null) && (edge.getMI() > threshold)) {
                    //          if(pvalue < 0.000001) {
                    //          GeneNetworkEdgeImpl edge = ggr.getScore(i, j, 0.0, null);
                    //          if (edge.getMI() > 0.004) {
                    double mi = edge.getMI();
                    if (mi < threshold) {
                        numConnections++;
                    }
                    //          }
                }

            }
        }
        return numConnections;
    }

    private double getPValue(double mi) {
        final double a = 1500;
        final double b = 10.5;
        if (mi <= 0.005) {
            return background[(int) (mi * binNo)];
        } else {
            double p = Math.exp(-a * mi + b) / a;
            return p;
        }
    }

    public double getMiByPValue(double pValue) {
        double tot = 1.0;
        int bin = 0;
        for (bin = 0; bin < 1.0 / getStep(); bin++) {
            double y = getFg(bin);
            tot -= y; // / 1000.0;
            if (tot <= pValue) {
                break;
            }
        }

        return bin * getStep();
        /*
             double totPVal = 1.0;
             int bin = -1;
             while(totPVal > pValue){
          totPVal -= background[++bin];
             }
         */
    }

    native double getMIScore(int g1, int g2, double threshold);

    native void loadMatrix(String name);

    native void baseline(double[] histogram);

    //native void   setMatrix(DSMicroarraySet ma);
    native void addMicroarray(int i);

    native void resetMicroarrays();

    public double getFg(int bin) {
        //int bin = (int)(x * binNo);
        return background[bin];
    }

    public double getMaxFg() {
        if (histogram != null) {
            return histogram[binNo - 1] / (double) binNo;
        } else {
            return 0.0;
        }
    }

    public double getStep() {
        return 1.0 / (double) binNo;
    }
}
