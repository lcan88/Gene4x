package org.geworkbench.util.associationdiscovery.clusterlogic;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenotypicMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.pattern.CSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.util.DSPValue;
import org.geworkbench.util.associationdiscovery.PSSM.CSMatchedPSSMMatrixPattern;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Title:        Plug And Play
 * Description:  Dynamic Proxy Implementation of enGenious
 * Copyright:    Copyright (c) 2002
 * Company:      First Genetic Trust Inc.
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class ClusterLogicImpl extends JPanel implements ClusterLogic {
    private JLabel lblChart = new JLabel();
    private JFreeChart entropyChart = null;
    private JFreeChart scoreChart = null;
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JToolBar jToolBar1 = new JToolBar();
    JButton jButton1 = new JButton();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel lblScore = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    Component component1;
    Component component2;
    JTextField jTextField1 = new JTextField();
    Component component3;
    JLabel jLabel4 = new JLabel();
    Component component4;
    JTextField jTextField2 = new JTextField();
    JLabel jLabel5 = new JLabel();
    JButton jButton2 = new JButton();

    class AlleleCount {
        public int count = 0;
    }

    public ClusterLogicImpl() {
        this.setName(getComponentName());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getComponentName() {
        return "Cluster Logic Service";
    }

    public JComponent[] getVisualComponents(String areaName) {
        JComponent[] panels = null;
        if (areaName.equalsIgnoreCase("CommandArea")) {
            panels = new JComponent[1];
            panels[0] = this;
        }
        return panels;
    }
    /*
        public void pssmScoreProfile1(JPSSM pssm, DSMicroarraySet mArraySet) {
            // scores all the microarrays using the current model
            // then determines if a microarray can be added to the model
            boolean found = false;
            double thr = 0.0;
            ArrayList scoresBg = new ArrayList();
            ArrayList scoresFg = new ArrayList();
            do {
                found = false;
                scoresBg.clear();
                scoresFg.clear();
                for (int arrayId = 0; arrayId < mArraySet.getMicroarrayNo(); arrayId++) {
                    IMicroarray mArray = mArraySet.get(arrayId);
                    JPSSM.GeneScore score = pssm.score(mArray);
                    score.microarrayId = arrayId;
                    if (pssm.containsMArray(arrayId)) {
                        scoresFg.add(score);
                    } else {
                        scoresBg.add(score);
                    }
                }
                Collections.sort(scoresFg);
                Collections.sort(scoresBg);
                JNormal normal = new JNormal();
                for (int id = 0; id < scoresBg.size(); id++) {
                    JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresBg.get(id));
                    if (id > scoresBg.size() - 8) {
                        double mean = normal.getMean();
                        double sigma = normal.getSigma();
                        thr = mean + 3.2 * sigma;
                        if (thr < score.score) {
                            pssm.setThreshold(thr);
                            found = true;
                            break;
                        }
                    }
                    normal.add(score.score);
                }
                if (found) {
                    JPSSM.GeneScore score = (JPSSM.GeneScore)scoresBg.get(scoresBg.size() - 1);
                    pssm.addMicroArrayId(score.microarrayId);
                    pssm.trainPSSM(mArraySet);
                }
            } while (found);
            scoresBg.clear();
            scoresFg.clear();
            for (int arrayId = 0; arrayId < mArraySet.getMicroarrayNo(); arrayId++) {
                IMicroarray mArray = mArraySet.get(arrayId);
                JPSSM.GeneScore score = pssm.score(mArray);
                score.microarrayId = arrayId;
                if (pssm.containsMArray(arrayId)) {
                    scoresFg.add(score);
                } else {
                    scoresBg.add(score);
                }
            }
            Collections.sort(scoresFg);
            Collections.sort(scoresBg);
            JNormal normal = new JNormal();
            System.out.println("PSSM Threshold: " + thr);
            for (int id = 0; id < scoresBg.size(); id++) {
                JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresBg.get(id));
                System.out.println(score.microarrayId + "\t" + score.score);
            }
            System.out.println("====");
            for (int id = 0; id < scoresFg.size(); id++) {
                JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresFg.get(id));
                System.out.println(score.microarrayId + "\t" + score.score);
            }
        }
    */

    public void pssmScoreProfile(CSMatchedPSSMMatrixPattern pssm, DSMicroarraySet<DSMicroarray> mArraySet) {
        double threshold = Double.parseDouble(jTextField2.getText());
        for (int maId = 0; maId < mArraySet.size(); maId++) {
            DSMicroarray mArray = mArraySet.get(maId);
            CSMatchedPSSMMatrixPattern.GeneScore score = pssm.score(mArray);
            score.microarrayId = maId;
            if (score.score > threshold) {
                DSPatternMatch<DSMicroarray, DSPValue> match = new CSPatternMatch<DSMicroarray, DSPValue>(mArray);
                pssm.add(match);
            }
        }
    }
    /*
        public void pssmScoreProfile(JPSSM pssm, DSMicroarraySet mArraySet) {
            // scores all the microarrays using the current model
            // then determines if a microarray can be added to the model
            // Two normal distributions are created. One with all the sorted points
            // scring up to n and one for all the sorted points > n.
            // The threshold is determined based on the equation N1+a*std1 = N2-a*st2
            // where N1 and N2 are respectively the averages for the first and second
            // distribution and st1 and st2 are respectively the standard deviations
            // for the two distributions.
            boolean found = false;
            double thr = 0.0;
            int selId = 0;
            JPSSM trainPSSM = new JPSSM(pssm, mArraySet);
            ArrayList scoresBg = new ArrayList();
            ArrayList scoresFg = new ArrayList();
            int bestId = 0;
            double bestSeparation = 999999;
            int noStep = 0;
            ArrayList tmpStack = new ArrayList();
            do {
                found = false;
                scoresBg.clear();
                scoresFg.clear();
                for (int arrayId = 0; arrayId < mArraySet.getMicroarrayNo(); arrayId++) {
                    IMicroarray mArray = mArraySet.get(arrayId);
                    JPSSM.GeneScore score = trainPSSM.score(mArray);
                    score.microarrayId = arrayId;
                    if (trainPSSM.containsMArray(arrayId)) {
                        scoresFg.add(score);
                    } else {
                        scoresBg.add(score);
                    }
                }
                Collections.sort(scoresFg);
                Collections.sort(scoresBg);
                JNormal normal1 = new JNormal();
                for (int id1 = 0; id1 < scoresBg.size(); id1++) {
                    JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresBg.get(id1));
                    normal1.add(score.score);
                }
                double mean1 = normal1.getMean();
                double sigma1 = normal1.getSigma();
                double mean2 = trainPSSM.mean;
                double sigma2 = trainPSSM.sigma;
                double threshold = JPSSM.optimalIntersection(mean1, sigma1, mean2, sigma2);
                NormalDistribution normal = new NormalDistribution(normal1.getMean(), normal1.getSigma());
                double separation = normal.getDensity(threshold);
                if (separation < bestSeparation) {
                    bestSeparation = separation;
                    bestId = scoresBg.size() - 1;
                    double thr1 = ((JPSSM.GeneScore)scoresFg.get(0)).score;
                    double thr2 = ((JPSSM.GeneScore)scoresBg.get(bestId)).score;
                    pssm.setThreshold((thr1 + thr2) / 2);
                    noStep = 0;
                    if (tmpStack.size() > 0) {
                        Iterator it = tmpStack.iterator();
                        while (it.hasNext()) {
                            JPSSM.GeneScore score = (JPSSM.GeneScore)it.next();
                            pssm.addMicroArrayId(score.microarrayId);
                        }
                        tmpStack.clear();
                    }
                } else {
                    noStep++;
                }
                JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresBg.get(scoresBg.size() - 1));
                tmpStack.add(score);
                trainPSSM.addMicroArrayId(score.microarrayId);
                trainPSSM.trainPSSM(mArraySet);
            } while ((noStep < 10) && (scoresBg.size() > (mArraySet.getMicroarrayNo() - pssm.getMicroArrayIdNo()) / 2));
            int idNo = scoresBg.size() - 1;
            for (int id1 = idNo; id1 > bestId; id1--) {
            }
            pssm.trainPSSM(mArraySet);
            scoresBg.clear();
            scoresFg.clear();
            for (int arrayId = 0; arrayId < mArraySet.getMicroarrayNo(); arrayId++) {
                IMicroarray mArray = mArraySet.get(arrayId);
                JPSSM.GeneScore score = pssm.score(mArray);
                score.microarrayId = arrayId;
                if (pssm.containsMArray(arrayId)) {
                    scoresFg.add(score);
                } else {
                    scoresBg.add(score);
                }
            }
            Collections.sort(scoresFg);
            Collections.sort(scoresBg);
            System.out.println("PSSM Threshold: " + pssm.getThreshold());
            for (int id1 = 0; id1 < scoresBg.size(); id1++) {
                JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresBg.get(id1));
                System.out.println(score.microarrayId + "\t" + score.score);
            }
            System.out.println("====");
            for (int id1 = 0; id1 < scoresFg.size(); id1++) {
                JPSSM.GeneScore score = ((JPSSM.GeneScore)scoresFg.get(id1));
                System.out.println(score.microarrayId + "\t" + score.score);
            }
        }
    */

    public void maskSupport(DSMicroarraySet mArraySet, CSMatchedMatrixPattern pattern) {
        for (int j = 0; j < pattern.getSupport(); j++) {
            DSMicroarray ma = pattern.get(j).getObject();
            ma.enable(false);
        }
    }

    private double getEntropy(CSMatchedMatrixPattern pattern, int markerId, DSMicroarraySet<DSMicroarray> set) {
        // For this marker put all the alleles in a hash map so that we can compute the prob.
        HashMap mapPat1 = new HashMap();
        HashMap mapSet1 = new HashMap();
        HashMap mapPat2 = new HashMap();
        HashMap mapSet2 = new HashMap();
        int countPat = 0;
        int countSet = 0;
        for (DSPatternMatch<DSMicroarray, ? extends DSPValue> match : pattern.matches()) {
            DSMutableMarkerValue m = match.getObject().getMarkerValue(markerId);
            countPat += updateProb(mapPat1, mapPat2, m);
        }
        for (int maId = 0; maId < set.size(); maId++) {
            DSMutableMarkerValue m = (DSMutableMarkerValue) set.get(maId).getMarkerValue(markerId);
            countSet += updateProb(mapSet1, mapSet2, m);
        }
        // Computing the entropy over the pattern
        double ePat = 0;
        if (countPat > 0) {
            Iterator it = mapPat1.values().iterator();
            while (it.hasNext()) {
                AlleleCount ac = (AlleleCount) it.next();
                double p = (double) ac.count / (double) countPat;
                ePat -= p * Math.log(p);
            }
            it = mapPat2.values().iterator();
            while (it.hasNext()) {
                AlleleCount ac = (AlleleCount) it.next();
                double p = (double) ac.count / (double) countPat;
                ePat -= p * Math.log(p);
            }
        } else {
            ePat = 9999;
        }
        // Computing the entropy over the full set
        double eSet = 0;
        if (countSet > 0) {
            Iterator it = mapSet1.values().iterator();
            while (it.hasNext()) {
                AlleleCount ac = (AlleleCount) it.next();
                double p = (double) ac.count / (double) countSet;
                eSet -= p * Math.log(p);
            }
            it = mapSet2.values().iterator();
            while (it.hasNext()) {
                AlleleCount ac = (AlleleCount) it.next();
                double p = (double) ac.count / (double) countSet;
                eSet -= p * Math.log(p);
            }
        } else {
            eSet = 9999;
        }
        double entropyRatio = ePat / eSet;
        return entropyRatio;
    }

    private int updateProb(HashMap map1, HashMap map2, DSMutableMarkerValue m) {
        int count = 0;
        if (m instanceof CSGenotypicMarkerValue) {
            CSGenotypicMarkerValue gt = (CSGenotypicMarkerValue) m;
            // If this is not a valid genotype, do not include it in the count
            //if(gt.isValid()) {
            count++;
            // we increase the total count by two because the two alleles
            // are treated separately
            int a1 = gt.getAllele(0);
            int a2 = gt.getAllele(1);
            if (a1 == 0)
                a1 = (int) (Math.random() * 1024);
            if (a2 == 0)
                a2 = (int) (Math.random() * 1024);
            Integer allele1 = new Integer(a1);
            Integer allele2 = new Integer(a2);

            AlleleCount ac11 = (AlleleCount) map1.get(allele1);
            AlleleCount ac22 = (AlleleCount) map2.get(allele2);
            AlleleCount ac12 = (AlleleCount) map2.get(allele1);
            AlleleCount ac21 = (AlleleCount) map1.get(allele2);
            if ((ac11 != null) && (ac22 != null)) {
                // Both have been seen before in this configuration
                ac11.count++;
                ac22.count++;
            } else if ((ac12 != null) && (ac21 != null)) { // Try the reverse
                // Both have been seen before in the reverse configuration
                ac12.count++;
                ac21.count++;
            } else { // Now we must try the combinations
                int maxCount = 0;
                AlleleCount bestAC1 = null;
                AlleleCount bestAC2 = null;
                boolean normalOrder = true;
                if ((ac11 != null) && (ac11.count > maxCount)) {
                    bestAC1 = ac11;
                    bestAC2 = ac22;
                    maxCount = ac11.count;
                }
                if ((ac22 != null) && (ac22.count > maxCount)) {
                    bestAC1 = ac11;
                    bestAC2 = ac22;
                    maxCount = ac22.count;
                }
                if ((ac12 != null) && (ac12.count > maxCount)) {
                    bestAC1 = ac12;
                    bestAC2 = ac21;
                    maxCount = ac12.count;
                    normalOrder = false;
                }
                if ((ac21 != null) && (ac21.count > maxCount)) {
                    bestAC1 = ac12;
                    bestAC2 = ac21;
                    maxCount = ac21.count;
                    normalOrder = false;
                }
                // If we found something, let's go with that
                if (bestAC1 == null) {
                    bestAC1 = new AlleleCount();
                    if (normalOrder) {
                        map1.put(allele1, bestAC1);
                    } else {
                        map2.put(allele1, bestAC1);
                    }
                }
                bestAC1.count++;
                if (bestAC2 == null) {
                    bestAC2 = new AlleleCount();
                    if (normalOrder) {
                        map2.put(allele2, bestAC2);
                    } else {
                        map1.put(allele2, bestAC2);
                    }
                }
                bestAC2.count++;
            }
            //}
        }
        return count;
    }

    public CSMatchedMatrixPattern extendChips(CSMatchedMatrixPattern pattern, DSMicroarraySet<DSMicroarray> set) {
        if (pattern instanceof CSMatchedPSSMMatrixPattern) {
            int maNo = set.size();
            double[] scores = new double[maNo];
            for (int i = 0; i < maNo; i++) {
                scores[i] = ((CSMatchedPSSMMatrixPattern) pattern).score(set.get(i)).score;
            }
            Arrays.sort(scores);
            int w = jPanel1.getWidth() / 2 - 20;
            int h = lblScore.getHeight();
            XYSeriesCollection plots = new XYSeriesCollection();
            XYSeries series = new XYSeries("Pattern Score");
            for (int i = 0; i < maNo; i++) {
                series.add((double) i, (double) scores[i]);
            }
            plots.addSeries(series);
            scoreChart = ChartFactory.createXYLineChart("Score Histogram", // Title
                    "Chip no", // X-Axis label
                    "Score", // Y-Axis label
                    plots, // Dataset
                    PlotOrientation.VERTICAL, true, // Show legend
                    false, false);
            BufferedImage image = scoreChart.createBufferedImage(w, h);
            lblScore.setIcon(new ImageIcon(image));
        }
        return pattern;
    }

    public void extendGenes(CSMatchedMatrixPattern[] patterns, DSMicroarraySet<DSMicroarray> set) {
        // for each marker, compute the contribution to the entropy
        int w = jPanel1.getWidth() - 20;
        int h = lblChart.getHeight();
        double threshold = Double.parseDouble(jTextField1.getText());
        XYSeriesCollection plots = new XYSeriesCollection();
        for (int pId = 0; pId < patterns.length; pId++) {
            try {
                int markerNo = set.size();
                ArrayList markerList = new ArrayList();
                double[] eRatio = new double[markerNo];
                for (int i = 0; i < markerNo; i++) {
                    eRatio[i] = getEntropy(patterns[pId], i, set);
                    if (eRatio[i] < threshold) {
                        markerList.add(new Integer(i));
                    }
                }
                Arrays.sort(eRatio);
                plotData(plots, eRatio, pId);
                patterns[pId].getPattern().init(markerList.size());
                for (int i = 0; i < markerList.size(); i++) {
                    DSGeneMarker marker = set.getMarkers().get(((Integer) markerList.get(i)).intValue());
                    patterns[pId].getPattern().markers()[i] = marker;
                }
            } catch (Exception ex) {
                System.out.println("Exception: " + ex);
            } finally {
                entropyChart = ChartFactory.createXYLineChart("Entropy Histogram", // Title
                        "gene no", // X-Axis label
                        "Entropy", // Y-Axis label
                        plots, // Dataset
                        PlotOrientation.VERTICAL, true, // Show legend
                        false, false);
                BufferedImage image = entropyChart.createBufferedImage(w, h);
                lblChart.setIcon(new ImageIcon(image));
            }
        }
    }

    void plotData(XYSeriesCollection plots, double[] e, int markerId) {
        XYSeries series = new XYSeries("Pattern: " + markerId);
        for (int i = 0; i < e.length; i++) {
            series.add((double) i, (double) e[i]);
        }
        plots.addSeries(series);
    }

    private void jbInit() throws Exception {
        component1 = Box.createHorizontalStrut(8);
        component2 = Box.createHorizontalStrut(8);
        component3 = Box.createHorizontalStrut(8);
        component4 = Box.createHorizontalStrut(8);
        this.setLayout(borderLayout1);
        jPanel1.setLayout(gridBagLayout1);
        lblChart.setBorder(BorderFactory.createEtchedBorder());
        lblChart.setIcon(null);
        lblChart.setText("");
        jButton1.setText("Print Entropy");
        jButton1.addActionListener(new JClusterLogic_jButton1_actionAdapter(this));
        lblScore.setBorder(BorderFactory.createEtchedBorder());
        lblScore.setText("");
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Entropy Graph");
        jLabel3.setText("Entropy Thresh:");
        jTextField1.setText("0.3");
        jLabel4.setRequestFocusEnabled(true);
        jLabel4.setText("Match Thresh:");
        jTextField2.setText("1.0");
        jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel5.setText("Match Score Graph");
        jButton2.setText("Print Score");
        jButton2.addActionListener(new JClusterLogic_jButton2_actionAdapter(this));
        this.add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(lblChart, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jPanel1.add(jToolBar1, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 329, 0));
        jPanel1.add(lblScore, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jPanel1.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        jPanel1.add(jLabel5, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        jToolBar1.add(jButton1, null);
        jToolBar1.add(component2, null);
        jToolBar1.add(jLabel3, null);
        jToolBar1.add(component1, null);
        jToolBar1.add(jTextField1, null);
        jToolBar1.add(component3, null);
        jToolBar1.add(jLabel4, null);
        jToolBar1.add(component4, null);
        jToolBar1.add(jTextField2, null);
        jToolBar1.add(jButton2, null);
    }

    void jButton1_actionPerformed(ActionEvent e) {
        if (entropyChart != null) {
            JFileChooser chooser = new JFileChooser();
            ExampleFilter filter = new ExampleFilter();
            filter.addExtension("jpg");
            filter.setDescription("JPG Images");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    ChartUtilities.saveChartAsJPEG(chooser.getSelectedFile().getAbsoluteFile(), entropyChart, 500, 300);
                } catch (IOException ex) {
                }
            }
        }
    }

    void jButton2_actionPerformed(ActionEvent e) {
        if (scoreChart != null) {
            JFileChooser chooser = new JFileChooser();
            ExampleFilter filter = new ExampleFilter();
            filter.addExtension("jpg");
            filter.setDescription("JPG Images");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    ChartUtilities.saveChartAsJPEG(chooser.getSelectedFile().getAbsoluteFile(), scoreChart, 500, 300);
                } catch (IOException ex) {
                }
            }
        }
    }
}

class JClusterLogic_jButton1_actionAdapter implements java.awt.event.ActionListener {
    ClusterLogicImpl adaptee;

    JClusterLogic_jButton1_actionAdapter(ClusterLogicImpl adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}

class JClusterLogic_jButton2_actionAdapter implements java.awt.event.ActionListener {
    ClusterLogicImpl adaptee;

    JClusterLogic_jButton2_actionAdapter(ClusterLogicImpl adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton2_actionPerformed(e);
    }
}
