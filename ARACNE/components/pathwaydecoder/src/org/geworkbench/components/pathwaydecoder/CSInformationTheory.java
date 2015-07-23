package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.util.function.gaussian.TruncatedGaussianBivariate;
import org.geworkbench.util.function.gaussian.TruncatedGaussianUnivariate;
import org.geworkbench.util.network.GeneNetworkEdge;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil;
import org.geworkbench.util.pathwaydecoder.RankSorter;
import org.geworkbench.util.pathwaydecoder.Regression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSInformationTheory <T extends DSGeneMarker,Q extends DSMicroarray> {
    static public final int STD_REGRESSION = 1;
    static public final int PEARSON = 2;
    static public final int RANK_CHI2 = 3;
    static public final int RANK_MI = 4;
    static public final int BG_RANDOM = 0;
    static public final int BG_HARTEMINK_CLIPPED = 1;
    //static final int backgroundType = BG_HARTEMINK_CLIPPED;

    static final int backgroundType = BG_RANDOM;
    private int normalBin = 100000;
    private double[] normal = new double[normalBin];
    private final int miSteps = 6;
    private final int miBlocks = 2;
    private final int size = 5;
    private final int iterationDefault = 15000;
    private final int binNo = 10000;
    private double sigma = -1.0;
    private double maPerMIStep = 1;
    private RankSorter[] ranks = null;
    private TruncatedGaussianUnivariate uniGauss = new TruncatedGaussianUnivariate(sigma);
    private TruncatedGaussianBivariate biGauss = new TruncatedGaussianBivariate(sigma);
    private boolean copulaTransform = true;
    private double[][] probMatrix;
    private int[][] miSpace = null;
    private double[][] miProb = null;
    private DSMicroarraySetView<T, Q> view;
    private int type = RANK_MI;
    private double d = 1;
    private double d2 = 1;
    private double maxD2 = 1;
    private double ratio = 1;
    private boolean computeBackground = false;
    private double[] background = new double[binNo];


    public CSInformationTheory(DSMicroarraySetView<T, Q> view, double sigma, boolean computeBackground, int type) {
        // The number of microarray is strictly determined by the phenotypic selection
        this.computeBackground = computeBackground;
        this.type = type;
        this.view = view;
        this.sigma = sigma;
        initialize();
    }

    private void initialize() {
        if (view != null) {
            miSpace = new int[miSteps][];
            miProb = new double[miSteps][];
            for (int i = 0; i < miSteps; i++) {
                miSpace[i] = new int[miSteps];
                miProb[i] = new double[miSteps];
            }
            if (view.items().size() > 0) {
                ranks = new RankSorter[view.items().size()];
                for (int i = 0; i < view.items().size(); i++) {
                    ranks[i] = new RankSorter();
                }
                maPerMIStep = (double) ranks.length / (double) miSteps;
                if (computeBackground) {
                    computeBackground(iterationDefault, ranks.length, type);
                }
                initializeNormal(ranks.length);
            }
            this.sigma = sigma;
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public ArrayList<GeneNetworkEdge> regression(DSGeneMarker m1, DSGeneMarker m2, double threshold) {
        // Get the number of markers that we should use.
        // This allows to subselect markers of interest
        int markerNo = view.markers().size();
        ArrayList<GeneNetworkEdge> edges = new ArrayList<GeneNetworkEdge>();
        if (m2 != null) {
            // both genes are defined. In this case it is one-to-one
            double score = getScore(m1, m2);
            if (score > threshold) {
                // If we found any edge, store it in the edges.
                GeneNetworkEdgeImpl rc = new GeneNetworkEdgeImpl();
                rc.setMI(score);
                rc.setPValue(PathwayDecoderUtil.getLogP(rc.getMI(), view.items().size()));
                edges.add(rc);
            }
        } else {
            // We must perform the analysis using the first gene against all other genes
            for (DSGeneMarker m3 : view.markers()) {
                if (m1.getLabel().equalsIgnoreCase("ExoBCL6")) {
                    continue;
                }
                if (m3.getLabel().equalsIgnoreCase("1936_s_at")) {
                    int x = 2;
                }
                if (m1 != m3) {
                    double score = getScore(m1, m3);
                    if (score > threshold) {
                        GeneNetworkEdgeImpl rc = new GeneNetworkEdgeImpl();
                        rc.setMI(score);
                        rc.setPValue(PathwayDecoderUtil.getLogP(rc.getMI(), view.items().size()));
                        rc.setId1(m1.getSerial());
                        rc.setMarker2(m3);
                        edges.add(rc);
                    }
                }
            }
        }
        return edges;
    }

    public double getScore(DSGeneMarker m1, DSGeneMarker m2) {
        switch (type) {
            case PEARSON:
                //return stdRegression(m1, m2, view);
                return rankRegression(m1, m2);
            case RANK_MI:
                return miScore(m1, m2);
            case RANK_CHI2:
            default:
                return newMIScore(m1, m2);
        }
    }

    private double stdRegression(DSGeneMarker m1, DSGeneMarker m2) {
        double[][] pairs = new double[view.items().size()][];
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i); // visualizer.microarrays().get(i);
            pairs[i] = new double[2];
            pairs[i][0] = ma.getMarkerValue(m1).getValue();
            pairs[i][1] = ma.getMarkerValue(m2).getValue();
        }
        Regression r = new Regression(pairs);
        r.calculate();
        return r.getCoefCorrel();
    }

    private double rankRegression(DSGeneMarker m1, DSGeneMarker m2) {
        double rho = getPearsonCorrelation(m1, m2);
        return rho;
    }

    private double getPearsonCorrelation(DSGeneMarker m1, DSGeneMarker m2) {
        //int maNo = mArraySet.getPhenoGroup().size();
        int maNo = view.items().size();
        if (ranks.length != maNo) {
            ranks = new RankSorter[maNo];
        }
        for (int i = 0; i < maNo; i++) {
            DSMicroarray ma = view.items().get(i);
            ranks[i] = new RankSorter();
            ranks[i].x = ma.getMarkerValue(m1).getValue();
            ranks[i].y = ma.getMarkerValue(m2).getValue();
        }
        sortRanks();

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

    private double newMIScore(DSGeneMarker m1, DSGeneMarker m2) {
        double mi = 0.0;
        if (view.items().size() != ranks.length) {
            RankSorter[] ranks = new RankSorter[view.items().size()];
            for (int i = 0; i < ranks.length; i++) {
                ranks[i] = new RankSorter();
            }
        }
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i);
            ranks[i].x = ma.getMarkerValue(m1).getValue();
            ranks[i].y = ma.getMarkerValue(m2).getValue();
        }

        mi = getNewMutualInformationXY();
        return mi;
    }

    double getNewMutualInformationXY() {
        sortRanks();
        double sum = computeNewMIFromArrays();
        return sum;
    }

    private void sortRanks() {
        Arrays.sort(ranks, RankSorter.SORT_X);
        for (int j = 1; j < ranks.length; j++) {
            if (ranks[j].x == ranks[j - 1].x) {
                ranks[j - 1].x += 0.1 * Math.random() - 0.05;
            }
        }
        Arrays.sort(ranks, org.geworkbench.util.pathwaydecoder.RankSorter.SORT_X);
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
    }

    private double computeNewMIFromArrays() {
        double[][] probMatrix = new double[ranks.length][ranks.length];
        for (int i = 0; i < ranks.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                probMatrix[i][j] = -1;
            }
        }

        double sum = 0;
        for (int i = 0; i < ranks.length; i++) {
            //          double v = getFxNew( (double) rankSorter[i].ix, (double) rankSorter[i].iy);
            double v = getFxNew(ranks[i].ix, ranks[i].iy);
            sum += Math.log(v);
        }

        double mi = sum / (double) ranks.length;
        if (mi <= 0) {
            System.out.println(mi + "");
        }
        return Math.max(mi, 0);
    }

    double getFxNew(int x, int y) {
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

        }

        return ((ranks.length * fxy) / (fx * fy));
    }

    private double miScore(DSGeneMarker m1, DSGeneMarker m2) {
        if (view.items().size() != ranks.length) {
            ranks = new RankSorter[view.items().size()];
            for (int i = 0; i < ranks.length; i++) {
                ranks[i] = new RankSorter();
            }
            maPerMIStep = (double) ranks.length / (double) miSteps;
        }
        for (int i = 0; i < view.items().size(); i++) {
            DSMicroarray ma = view.items().get(i);
            ranks[i].x = ma.getMarkerValue(m1).getValue();
            ranks[i].y = ma.getMarkerValue(m2).getValue();
        }

        sortRanks();
        double mi = computeMIFromArrays();
        //double p = PathwayDecoderUtil.getLogP(mi, ranks.length);
        return mi;
    }

    private double computeMIFromArrays() {
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
        for (int i = 0; i < ranks.length; i++) {
            int x = (int) ((double) ranks[i].ix / maPerMIStep);
            int y = (int) ((double) ranks[i].iy / maPerMIStep);
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
                double p = (double) count1 / ((double) ranks.length * (double) miBlocks * (double) miBlocks);
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

    private void checkView(DSMicroarraySetView<T, Q> view) {
        if ((this.view.markers().getID() != view.markers().getID()) || (this.view.items().getID() != view.items().getID()) || (this.view.getDataSet() != view.getDataSet())) {
            this.view = view;
        }
    }

    private void initializeNormal(int size) {
        if (copulaTransform) {
            initializeNormalCopula();
        } else {
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
        d = sigma;
        d2 = d * d * 2;
        maxD2 = (d * 3) * (d * 3);
        ratio = (double) normalBin / maxD2;

        for (int i = 0; i < normalBin; i++) {
            double x = (double) i / ratio;
            normal[i] = Math.exp(-x / d2);
        }
    }

    public void computeBackground(int iterationNo, int size, int type) {
        computeBackground(iterationNo, size, type, 36.0);
    }

    public void computeBackground(int iterationNo, int size, int type, double noise) {
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
            switch (backgroundType) {
                case BG_RANDOM:
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
                case BG_HARTEMINK_CLIPPED:
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
                randMi = computeNewMIFromArrays();
            } else {
                randMi = computeMIFromArrays();
            }
            int bin = (int) (randMi * binNo);

            if (bin >= background.length - 1) {
                bin = background.length - 1;
            }

            background[bin] += increment;
            sum += increment;
        }
    }

    public double getStep() {
        return 1.0 / (double) binNo;
    }

    public double getMaxFg() {
        return getStep() * 999;
    }

    public double getFg(int bin) {
        //int bin = (int)(x * binNo);
        return background[bin];
    }
}
