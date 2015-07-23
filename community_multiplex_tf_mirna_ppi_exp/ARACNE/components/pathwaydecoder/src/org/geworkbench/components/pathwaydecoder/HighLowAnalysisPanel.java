package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;
import org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil;
import org.geworkbench.util.pathwaydecoder.RankSorter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class HighLowAnalysisPanel extends JPanel {
    /**
     * includes all markers selected for the analysis
     */
    private org.geworkbench.util.microarrayutils.MicroarrayVisualizer maViz = null;

    /**
     * includes all the microarrys selected by the phenotypic criteria
     */
    private DSItemList<DSMicroarray> mArrayVector = null;

    /**
     * the subset of array used for the analyisis, based on the expression contraint
     */
    private DSMicroarraySet<DSMicroarray> mArraySet = null;
    private int constrainedGene = 0;
    private int geneSelection = 0;
    //private boolean                  computeBkd     = true;
    private IntersectionCellRenderer iRenderer = new IntersectionCellRenderer();
    private int maLowNo = 0;
    private int maHigNo = 0;
    private int maNo = 0;

    class ArrayDataList extends AbstractListModel {
        ArrayList list = null;

        ArrayDataList(ArrayList aList) {
            list = aList;
        }

        public int getSize() {
            return list.size();
        }

        public Object getElementAt(int index) {
            //final DecimalFormat format = new DecimalFormat("#.####");
            //GeneNetworkEdge edge = (GeneNetworkEdge)list.get(index);
            //return "MI: " + format.format(edge.getPValue()) + "  " + edge.m2.getAccession() + " " + edge.m2.toString(); }
            Object obj = list.get(index);
            return obj;
        }
    }

    /*
     HashSet mapH1  = new HashSet();
     HashSet mapL1  = new HashSet();
     HashSet mapA1  = new HashSet();
     HashSet mapH2  = new HashSet();
     HashSet mapL2  = new HashSet();
     HashSet mapA2  = new HashSet();
     */
    HashMap mapH1 = new HashMap();
    HashMap mapL1 = new HashMap();
    HashMap mapA1 = new HashMap();
    HashMap mapH2 = new HashMap();
    HashMap mapL2 = new HashMap();
    HashMap mapA2 = new HashMap();
    JList jListL = new JList();
    JList jListH = new JList();
    JList jListA = new JList();
    JList jListHL = new JList();
    JList jListLH = new JList();
    JList jListHA = new JList();
    JList jListLA = new JList();
    JList jListLHA = new JList();

    ArrayList listL = new ArrayList();
    ArrayList listH = new ArrayList();
    ArrayList listA = new ArrayList();
    ArrayList listHL = new ArrayList();
    ArrayList listLH = new ArrayList();
    ArrayList listHA = new ArrayList();
    ArrayList listLA = new ArrayList();
    ArrayList listLHA = new ArrayList();

    ArrayDataList alL = new ArrayDataList(listL);
    ArrayDataList alH = new ArrayDataList(listH);
    ArrayDataList alA = new ArrayDataList(listA);
    ArrayDataList alLH = new ArrayDataList(listLH);
    ArrayDataList alLA = new ArrayDataList(listLA);
    ArrayDataList alHA = new ArrayDataList(listHA);
    ArrayDataList alLHA = new ArrayDataList(listLHA);

    BorderLayout borderLayout1 = new BorderLayout();
    JTextField jGene2Box = new JTextField();
    JPanel jPanel10 = new JPanel();
    JTextField jAllOnlyBox = new JTextField();
    JLabel jLabel14 = new JLabel();
    GridBagLayout gridBagLayout4 = new GridBagLayout();
    JLabel jLabel9 = new JLabel();
    JTextField jThr1Box = new JTextField();
    JTextField jLowHighAllBox = new JTextField();
    JTextField jGene1Box = new JTextField();
    JLabel jLabel18 = new JLabel();
    JLabel jLabel17 = new JLabel();
    JButton jButton16 = new JButton();
    JPanel jPanel7 = new JPanel();
    Component component1;
    JTextField jHighAllBox = new JTextField();
    JTextField jTextField2 = new JTextField();
    JTextField jLowHighBox = new JTextField();
    JLabel jLabel6 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JTextField jToleranceBox = new JTextField();
    JScrollPane jScrollPaneLH = new JScrollPane();
    JRadioButton jIntersectingCheck = new JRadioButton();
    JLabel jLabel8 = new JLabel();
    GridBagLayout gridBagLayout8 = new GridBagLayout();
    JLabel jLabel12 = new JLabel();
    JLabel jLabel20 = new JLabel();
    JLabel jLabel13 = new JLabel();
    JTextField jLowOnlyBox = new JTextField();
    JScrollPane jScrollPaneHL = new JScrollPane();
    JScrollPane jScrollPaneHA = new JScrollPane();
    JTextField jHighOnlyBox = new JTextField();
    JPanel jPanel11 = new JPanel();
    GridBagLayout gridBagLayout7 = new GridBagLayout();
    GridBagLayout gridBagLayout6 = new GridBagLayout();
    JLabel jLabel19 = new JLabel();
    JPanel jPanel8 = new JPanel();
    JRadioButton jUniqueCheck = new JRadioButton();
    JScrollPane jScrollPaneLA = new JScrollPane();
    JLabel jLabel11 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JTextField jLowAllBox = new JTextField();
    JButton jButton17 = new JButton();
    GridBagLayout gridBagLayout5 = new GridBagLayout();
    JLabel jLabel15 = new JLabel();
    JPanel jPanel9 = new JPanel();
    JLabel jLabel10 = new JLabel();
    JLabel jList1Header = new JLabel();
    JLabel jList2Header = new JLabel();
    JLabel jList3Header = new JLabel();
    JLabel jList4Header = new JLabel();
    ButtonGroup radioButtonBox = new ButtonGroup();
    JTextField jLowValue = new JTextField();
    JTextField jHighValue = new JTextField();

    public HighLowAnalysisPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        component1 = Box.createVerticalStrut(8);
        jPanel10.setLayout(gridBagLayout6);
        jPanel10.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setLayout(borderLayout1);
        jAllOnlyBox.setText("");
        jLabel14.setText("Low and All:");
        jLabel14.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel14.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel9.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel9.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel9.setText("Only in High:");
        if (GeneNetworkEdgeImpl.usePValue) {
            jThr1Box.setText("15");
        } else {
            jThr1Box.setText("0.04");
        }
        jLowHighAllBox.setText("");
        jGene1Box.setText("1973_s_at");
        jGene2Box.setText("40091_at");
        jLabel18.setText("Gene 2");
        jLabel17.setText("Gene 1");
        jButton16.setText("Compute");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton16_actionPerformed(e);
            }
        });
        jPanel7.setLayout(gridBagLayout5);
        jHighAllBox.setText("");
        jTextField2.setText("0.333");
        jLowHighBox.setText("");
        jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel6.setText("This Panel is used to run differential network analysis against all " + "selected gene-pairs");
        jTextField1.setText("0.333");
        jToleranceBox.setText("0.3");
        jIntersectingCheck.setText("Intersecting");
        jIntersectingCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jIntersectingCheck_actionPerformed(e);
            }
        });
        jLabel8.setText("High %");
        jLabel12.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel12.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel12.setText("Only in All:");
        jLabel20.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel20.setText("Tol.");
        jLabel13.setText("High and All:");
        jLabel13.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel13.setHorizontalAlignment(SwingConstants.TRAILING);
        jLowOnlyBox.setText("");
        jHighOnlyBox.setText("");
        jPanel11.setLayout(gridBagLayout7);
        jPanel11.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel19.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel19.setText("Thr 1:");
        jPanel8.setLayout(gridBagLayout8);
        jPanel8.setBorder(BorderFactory.createEtchedBorder());
        jUniqueCheck.setText("Unique");
        jUniqueCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jUniqueCheck_actionPerformed(e);
            }
        });
        jLowValue.setMinimumSize(new Dimension(4, 20));
        jLowValue.setPreferredSize(new Dimension(4, 20));
        jLowValue.setText("");
        jHighValue.setText("");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton17_actionPerformed(e);
            }
        });
        radioButtonBox.add(jUniqueCheck);
        radioButtonBox.add(jIntersectingCheck);
        jUniqueCheck.setSelected(true);
        jLabel11.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel11.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel11.setText("Low and High:");
        jLabel7.setText("Low %");
        jLowAllBox.setText("");
        jButton17.setText("Save");
        jLabel15.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel15.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel15.setText("Low, High, All:");
        jPanel9.setBorder(BorderFactory.createLineBorder(Color.black));
        jPanel9.setLayout(gridBagLayout4);
        jLabel10.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel10.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel10.setText("Only in Low:");
        jPanel7.add(jLabel6, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        jPanel7.add(jPanel8, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jThr1Box, new GridBagConstraints(1, 5, 2, 2, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jLabel18, new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(3, 6, 0, 0), 0, 0));
        jPanel8.add(jToleranceBox, new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jLabel20, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(jLabel19, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(component1, new GridBagConstraints(1, 13, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        jPanel8.add(jGene2Box, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jLabel17, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(jGene1Box, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jPanel11, new GridBagConstraints(0, 11, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel11.add(jLabel14, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel11.add(jLabel13, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel11.add(jHighAllBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel11.add(jLowAllBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel11.add(jLowHighAllBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel11.add(jLowHighBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel11.add(jLabel15, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel11.add(jLabel11, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(jTextField1, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jLabel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(jLabel8, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 0, 0));
        jPanel8.add(jTextField2, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jButton17, new GridBagConstraints(0, 9, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 96, 0));
        jPanel8.add(jButton16, new GridBagConstraints(0, 8, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 2, 2, 2), 77, 0));
        jPanel8.add(jPanel9, new GridBagConstraints(0, 10, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(jLabel10, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 9, 0));
        jPanel9.add(jLabel9, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 9, 0));
        jPanel9.add(jLabel12, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 2), 9, 0));
        jPanel9.add(jAllOnlyBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(jHighOnlyBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(jLowOnlyBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jPanel10, new GridBagConstraints(0, 12, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel10.add(jUniqueCheck, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel10.add(jIntersectingCheck, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel8.add(jLowValue, new GridBagConstraints(2, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel7.add(jScrollPaneHL, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel7.add(jScrollPaneLH, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel7.add(jScrollPaneLA, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel7.add(jScrollPaneHA, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jScrollPaneHA.add(jListHA, null);
        jScrollPaneLA.add(jListLA, null);
        jScrollPaneLH.add(jListLH, null);
        jScrollPaneHL.add(jListHL, null);
        this.add(jPanel7, BorderLayout.CENTER);
        jScrollPaneHL.getViewport().add(jListHL, null);
        jScrollPaneLH.getViewport().add(jListLH, null);
        jScrollPaneHA.getViewport().add(jListHA, null);
        jScrollPaneLA.getViewport().add(jListLA, null);
        jPanel8.add(jHighValue, new GridBagConstraints(2, 2, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jListHL.setCellRenderer(iRenderer);
        jListLH.setCellRenderer(iRenderer);
        jListHA.setCellRenderer(iRenderer);
        jListLA.setCellRenderer(iRenderer);
        jScrollPaneHL.setColumnHeaderView(jList1Header);
        jScrollPaneLH.setColumnHeaderView(jList2Header);
        jScrollPaneHA.setColumnHeaderView(jList3Header);
        jScrollPaneLA.setColumnHeaderView(jList4Header);
        jList1Header.setFont(new java.awt.Font("Serif", 2, 11));
        jList1Header.setBorder(BorderFactory.createEtchedBorder());
        jList1Header.setHorizontalAlignment(SwingConstants.CENTER);
        jList2Header.setFont(new java.awt.Font("Serif", 2, 11));
        jList2Header.setBorder(BorderFactory.createEtchedBorder());
        jList2Header.setHorizontalAlignment(SwingConstants.CENTER);
        jList3Header.setFont(new java.awt.Font("Serif", 2, 11));
        jList3Header.setBorder(BorderFactory.createEtchedBorder());
        jList3Header.setHorizontalAlignment(SwingConstants.CENTER);
        jList4Header.setFont(new java.awt.Font("Serif", 2, 11));
        jList4Header.setBorder(BorderFactory.createEtchedBorder());
        jList4Header.setHorizontalAlignment(SwingConstants.CENTER);
        setListHeaders();
    }

    void jButton16_actionPerformed(ActionEvent e) {
        if (maViz != null){
            String gene1 = jGene1Box.getText();
            DSItemList<DSGeneMarker>
                    markers = PathwayDecoderUtil.
                              matchingMarkers(gene1, maViz.getDataSetView());
            if (markers.size() > 0) {
                constrainedGene = markers.get(0).getSerial();
                markers = PathwayDecoderUtil.matchingMarkers(jGene2Box.getText(),
                        maViz.getDataSetView());
                if (markers.size() > 0) {
                    int gene2Selection = markers.get(0).getSerial();
                    //geneCorrelation(10741, 6960);
                    geneCorrelation(constrainedGene, gene2Selection);
                }
            }
        }
    }

    void jIntersectingCheck_actionPerformed(ActionEvent e) {
        setListHeaders();
    }

    void jUniqueCheck_actionPerformed(ActionEvent e) {
        setListHeaders();
    }

    public void set(DSMicroarraySet _maSet, org.geworkbench.util.microarrayutils.MicroarrayVisualizer _mrkV, DSItemList<DSMicroarray> _marV) {
        maViz = _mrkV;
        mArrayVector = _marV;
        mArraySet = _maSet;
    }

    void geneCorrelation(int gene1, int gene2) {
        PathwayDecoderUtil pdUtil = new PathwayDecoderUtil(maViz.getDataSetView());
        //This computes the gene correlation
        // First determine the bottom and top microarrays for gene 1
        RankSorter[] ranks = null;
        double threshold = Double.parseDouble(jThr1Box.getText());
        double tolerance = Double.parseDouble(jToleranceBox.getText());
        double minThresh = threshold * (1.0 - tolerance);
        maNo = mArrayVector.size();
        int maNo1 = mArraySet.size();
        double lowerPerc = Double.parseDouble(jTextField1.getText());
        double upperPerc = Double.parseDouble(jTextField2.getText());
        maLowNo = (int) ((double) maNo * lowerPerc);
        maHigNo = (int) ((double) maNo * upperPerc);

        DSGeneMarker mi1 = mArraySet.getMarkers().get(gene1);
        DSGeneMarker mi2 = mArraySet.getMarkers().get(gene2);
        ranks = new org.geworkbench.util.pathwaydecoder.RankSorter[maNo];
        for (int i = 0; i < maNo; i++) {
            int serial = mArrayVector.get(i).getSerial();
            DSMicroarray ma = mArraySet.get(serial);
            ranks[i] = new RankSorter();
            ranks[i].x = ma.getMarkerValue(gene1).getValue();
            ranks[i].id = serial;
        }
        Arrays.sort(ranks, RankSorter.SORT_X);
        // select all
        DSItemList<DSMicroarray> mArraysAll = new CSItemList<DSMicroarray>();
        for (int maId = 0; maId < maNo; maId++) {
            mArraysAll.add(maId, mArraySet.get(ranks[maId].id));
        }
        ArrayList listAll = pdUtil.relatedMarkers(mi2, null, mArraysAll, 0.01, GeneGeneRelationship.RANK_MI, false);

        // select lower %
        DSItemList<DSMicroarray> mArraysLow = new CSItemList<DSMicroarray>();
        for (int maId = 0; maId < maLowNo; maId++) {
            mArraysLow.add(maId, mArraySet.get(ranks[maId].id));
        }
        double minGene1Value = mArraySet.get(ranks[maLowNo].id).getMarkerValue(gene1).getValue();
        ArrayList listLow = pdUtil.relatedMarkers(mi2, null, mArraysLow, 0.01, GeneGeneRelationship.RANK_MI, false);

        // select upper %
        DSItemList<DSMicroarray> mArraysHig = new CSItemList<DSMicroarray>();
        for (int maId = 0; maId < maHigNo; maId++) {
            int a = maId;
            int b = maNo - maId - 1;
            mArraysHig.add(a, mArraySet.get(ranks[b].id));
        }
        double maxGene1Value = mArraySet.get(ranks[maNo - maHigNo - 1].id).getMarkerValue(gene1).getValue();
        DecimalFormat df = new DecimalFormat("####");
        jLowValue.setText(df.format(minGene1Value));
        jHighValue.setText(df.format(maxGene1Value));
        ArrayList listHig = pdUtil.relatedMarkers(mi2, null, mArraysHig, 0.01, GeneGeneRelationship.RANK_MI, false);
        Collections.sort(listLow);
        Collections.sort(listHig);
        Collections.sort(listAll);

        mapA1.clear();
        mapA2.clear();
        mapL1.clear();
        mapL2.clear();
        mapH1.clear();
        mapH2.clear();
        listL.clear();
        listH.clear();
        listA.clear();
        listHA.clear();
        listLA.clear();
        listLH.clear();
        listLHA.clear();

        // This function computes the probability of the top two hundred genes
        //  being shared across the two lists
        double pValue = 0.0;
        for (int i = 0; i < listLow.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) listLow.get(i);
            if (edge.getThreshold() < threshold) {
                break;
            }
            boolean found = false;
            for (int j = 0; j < listHig.size(); j++) {
                GeneNetworkEdgeImpl edge1 = (GeneNetworkEdgeImpl) listHig.get(j);
                if (edge.equals(edge1)) {
                    double p0 = edge.getPValue(); // ggr.getPValue(edge.getPValue());
                    double p1 = edge1.getPValue(); //ggr.getPValue(edge1.getPValue());
                    pValue += Math.pow(10.0, p0 + p1);
                    found = true;
                    break;
                }
            }
        }

        for (int i = 0; i < listAll.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) listAll.get(i);
            if (edge.getThreshold() >= threshold) {
                //mapA1.add(edge);
                mapA1.put(edge.getMarker2(), edge);
            } else if (edge.getThreshold() >= minThresh) {
                //mapA2.add(edge);
                mapA2.put(edge.getMarker2(), edge);
            } else {
                break;
            }
        }
        for (int i = 0; i < listLow.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) listLow.get(i);
            if (edge.getThreshold() >= threshold) {
                //mapL1.add(edge);
                mapL1.put(edge.getMarker2(), edge);
            } else if (edge.getThreshold() >= minThresh) {
                //mapL2.add(edge);
                mapL2.put(edge.getMarker2(), edge);
            } else {
                break;
            }
        }
        for (int i = 0; i < listHig.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) listHig.get(i);
            if (edge.getThreshold() >= threshold) {
                //mapH1.add(edge);
                mapH1.put(edge.getMarker2(), edge);
            } else if (edge.getThreshold() >= minThresh) {
                //mapH2.add(edge);
                mapH2.put(edge.getMarker2(), edge);
            } else {
                break;
            }
        }
        Iterator it = mapL1.values().iterator();
        while (it.hasNext()) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) it.next();
            double tL = edge.getThreshold();
            double tA = 0.0;
            double tH = 0.0;
            //boolean inAll = mapA1.contains(edge) || mapA2.contains(edge);
            //boolean inHig = mapH1.contains(edge) || mapH2.contains(edge);
            GeneNetworkEdgeImpl edgeAll = (GeneNetworkEdgeImpl) mapA1.get(edge.getMarker2());
            if (edgeAll == null) {
                edgeAll = (GeneNetworkEdgeImpl) mapA2.get(edge.getMarker2());
            }
            if (edgeAll != null) {
                tA = edgeAll.getThreshold();
            }
            GeneNetworkEdgeImpl edgeHig = (GeneNetworkEdgeImpl) mapH1.get(edge.getMarker2());
            if (edgeHig == null) {
                edgeHig = (GeneNetworkEdgeImpl) mapH2.get(edge.getMarker2());
            }
            if (edgeHig != null) {
                tH = edgeHig.getThreshold();
            }
            double tMax = Math.max(Math.max(tL, tH), tA);
            if (tMax == tA) {
                edge = edgeAll;
            } else if (tMax == tH) {
                edge = edgeHig;
            }
            double thr = tMax * (1.0 - tolerance);
            //if(inAll && inHig) {
            if (tL >= thr) {
                if ((tA >= thr) && (tH >= thr)) {
                    listLHA.add(edge);
                } else if (tA >= thr) {
                    listLA.add(edge);
                } else if (tH >= thr) {
                    listLH.add(edge);
                } else {
                    listL.add(edge);
                }
            }
        }
        it = mapH1.values().iterator();
        while (it.hasNext()) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) it.next();
            double tH = edge.getThreshold();
            double tA = 0.0;
            double tL = 0.0;
            GeneNetworkEdgeImpl edgeAll = (GeneNetworkEdgeImpl) mapA1.get(edge.getMarker2());
            if (edgeAll == null) {
                edgeAll = (GeneNetworkEdgeImpl) mapA2.get(edge.getMarker2());
            }
            if (edgeAll != null) {
                tA = edgeAll.getThreshold();
            }
            GeneNetworkEdgeImpl edgeLow = (GeneNetworkEdgeImpl) mapL1.get(edge.getMarker2());
            if (edgeLow == null) {
                edgeLow = (GeneNetworkEdgeImpl) mapL2.get(edge.getMarker2());
            }
            if (edgeLow != null) {
                tL = edgeLow.getThreshold();
            }
            double tMax = Math.max(Math.max(tL, tH), tA);
            double thr = tMax * (1.0 - tolerance);
            if (tMax == tA) {
                edge = edgeAll;
            } else if (tMax == tL) {
                edge = edgeLow;
            }
            if (tH >= thr) {
                if ((tA >= thr) && (tL >= thr)) {
                    listLHA.add(edge);
                } else if (tA >= thr) {
                    listLA.add(edge);
                } else if (tL >= thr) {
                    listLH.add(edge);
                } else {
                    listH.add(edge);
                }
            }
        }
        it = mapA1.values().iterator();
        while (it.hasNext()) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) it.next();
            double tA = edge.getThreshold();
            double tL = 0.0;
            double tH = 0.0;
            GeneNetworkEdgeImpl edgeLow = (GeneNetworkEdgeImpl) mapL1.get(edge.getMarker2());
            if (edgeLow == null) {
                edgeLow = (GeneNetworkEdgeImpl) mapL2.get(edge.getMarker2());
            }
            if (edgeLow != null) {
                tL = edgeLow.getThreshold();
            }
            GeneNetworkEdgeImpl edgeHig = (GeneNetworkEdgeImpl) mapH1.get(edge.getMarker2());
            if (edgeHig == null) {
                edgeHig = (GeneNetworkEdgeImpl) mapH2.get(edge.getMarker2());
            }
            if (edgeHig != null) {
                tH = edgeHig.getThreshold();
            }
            double tMax = Math.max(Math.max(tL, tH), tA);
            double thr = tMax * (1.0 - tolerance);
            if (tMax == tL) {
                edge = edgeLow;
            } else if (tMax == tH) {
                edge = edgeHig;
            }
            if (tA >= thr) {
                if ((tL >= thr) && (tH >= thr)) {
                    listLHA.add(edge);
                } else if (tL >= thr) {
                    listLA.add(edge);
                } else if (tH >= thr) {
                    listHA.add(edge);
                } else {
                    listA.add(edge);
                }
            }
        }
        /*
           it = mapH1.iterator();
           while(it.hasNext()) {
                 GeneNetworkEdge edge = (GeneNetworkEdge)it.next();
                 boolean inAll = mapA1.contains(edge) || mapA2.contains(edge);
                 boolean inLow = mapL1.contains(edge) || mapL2.contains(edge);
                 if(inAll && inLow) {
          if(!listLHA.contains(edge)) listLHA.add(edge);
                 } else if(inAll) {
          listHA.add(edge);
                 } else if(inLow) {
          if(!listLH.contains(edge)) listLH.add(edge);
                 } else {
          listH.add(edge);
                 }
           }
           it = mapA1.iterator();
           while(it.hasNext()) {
                 GeneNetworkEdge edge = (GeneNetworkEdge)it.next();
                 boolean inLow = mapL1.contains(edge) || mapL2.contains(edge);
                 boolean inHig = mapH1.contains(edge) || mapH2.contains(edge);
                 if(inHig && inLow) {
          if(!listLHA.contains(edge)) listLHA.add(edge);
                 } else if(inLow) {
          if(!listLA.contains(edge)) listLA.add(edge);
                 } else if(inHig) {
          if(!listHA.contains(edge)) listHA.add(edge);
                 } else {
          listA.add(edge);
                 }
           }
         */
        Collections.sort(listL);
        Collections.sort(listH);
        Collections.sort(listA);
        Collections.sort(listLH);
        Collections.sort(listLA);
        Collections.sort(listHA);
        Collections.sort(listLHA);

        jHighOnlyBox.setText(Integer.toString(listH.size()));
        jLowOnlyBox.setText(Integer.toString(listL.size()));
        jAllOnlyBox.setText(Integer.toString(listA.size()));
        jLowHighBox.setText(Integer.toString(listLH.size()));
        jHighAllBox.setText(Integer.toString(listHA.size()));
        jLowAllBox.setText(Integer.toString(listLA.size()));
        jLowHighAllBox.setText(Integer.toString(listLHA.size()));

        System.out.println("Score: " + pValue);
        setListHeaders();
    }

    void setListHeaders() {
        jListHL.setFixedCellHeight(15);
        jListLH.setFixedCellHeight(15);
        jListHA.setFixedCellHeight(15);
        jListLA.setFixedCellHeight(15);
        jListHL.setFixedCellWidth(300);
        jListLH.setFixedCellWidth(300);
        jListHA.setFixedCellWidth(300);
        jListLA.setFixedCellWidth(300);
        if (mArraySet != null) {
            String[] markerName = null;
            DSGeneMarker marker = mArraySet.getMarkers().get(constrainedGene);
            try {
                markerName = AnnotationParser.getInfo(marker.getLabel(), AnnotationParser.DESCRIPTION);
            } catch (Exception ex) {
            }
            DefaultListModel model = new DefaultListModel();
            jListHL.setModel(model);
            jListLH.setModel(model);
            jListHA.setModel(model);
            jListLA.setModel(model);
            if (jUniqueCheck.isSelected()) {
                jList1Header.setText("Only In High " + marker.toString());
                jList2Header.setText("Only In Low " + marker.toString());
                jList3Header.setText("Only In All " + marker.toString());
                jList4Header.setText("In Low, High, and All " + marker.toString());
                jListHL.setModel(alH);
                jListLH.setModel(alL);
                jListHA.setModel(alA);
                jListLA.setModel(alLHA);
            } else {
                jList1Header.setText("In High and in Low " + marker.toString());
                jList2Header.setText("In Low and in All " + marker.toString());
                jList3Header.setText("In High and in All " + marker.toString());
                jList4Header.setText("In Low, High, and All " + marker.toString());
                jListHL.setModel(alLH);
                jListLH.setModel(alLA);
                jListHA.setModel(alHA);
                jListLA.setModel(alLHA);
            }
        }
    }

    public void geneSelected(DSGeneMarker marker) {
        if (jGene1Box.hasFocus()) {
            jGene1Box.setText(marker.getLabel());
        } else {
            jGene2Box.setText(marker.getLabel());
        }
    }

    /**
     * This action should save all the entries in the various lists
     *
     * @param e
     */
    void jButton17_actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser("venn.txt");
        ExampleFilter filter = new ExampleFilter();
        filter.addExtension("txt");
        filter.setDescription("Venn Diagram Files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(chooser.getSelectedFile()));
                PathwayDecoderUtil.writeEdgeList(writer, "Only in High", listH);
                PathwayDecoderUtil.writeEdgeList(writer, "Only in Low", listL);
                PathwayDecoderUtil.writeEdgeList(writer, "Only In All", listA);
                PathwayDecoderUtil.writeEdgeList(writer, "In Low and High", listLH);
                PathwayDecoderUtil.writeEdgeList(writer, "In Low and All", listLA);
                PathwayDecoderUtil.writeEdgeList(writer, "In High and All", listHA);
                PathwayDecoderUtil.writeEdgeList(writer, "In Low, High, and All", listLHA);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }
        }
    }
}
