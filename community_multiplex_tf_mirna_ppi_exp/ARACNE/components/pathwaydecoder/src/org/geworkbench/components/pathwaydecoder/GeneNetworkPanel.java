package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.Normal;
import org.geworkbench.builtin.projects.LoadData;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.AdjacencyMatrixEvent;
import org.geworkbench.events.ProjectNodeAddedEvent;
import org.geworkbench.util.SwingWorker;
import org.geworkbench.util.microarrayutils.MicroarrayVisualizer;
import org.geworkbench.util.network.EdgeMatrix;
import org.geworkbench.util.network.GeneNetworkEdge;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;
import org.geworkbench.util.pathwaydecoder.GeneNetworkManager;
import org.geworkbench.util.pathwaydecoder.GenericMarkerNode;
import org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrixDataSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.layout.AnnealingLayoutController;
import org.jgraph.layout.GEMLayoutController;
import org.jgraph.layout.LayoutAlgorithm;
import org.jgraph.layout.LayoutController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneNetworkPanel extends JPanel implements NetworkVisualizer {

    class NetworkLayoutAlgorithm extends org.geworkbench.algorithms.BWAbstractAlgorithm {
        private int iterationNo = 1;
        private boolean running = false;
        private boolean stop = false;
        private long time = 0;

        /**
         * execute
         */
        public void setIterationNo(int no) {
            iterationNo = no;
        }

        public boolean isRunning() {
            Date date = new Date();
            long newTime = date.getTime();
            long diff = newTime - time;
            if (diff < 4000) {
                return true;
            }
            time = newTime;
            return running;
        }

        protected void execute() {
            running = true;
            LayoutController controller = null;
            String layoutType = (String) layoutTypeBox.getSelectedItem();

            for (int i = 0; i < iterationNo; i++) {
                if (layoutType.equalsIgnoreCase("GEM")) {
                    boolean optimize = optimizeCheckBox.isSelected();
                    double gravity = (double) gravitySlider.getValue() / 100.0;
                    int edgeLength = edgeLengthSlider.getValue();
                    controller = new GEMLayoutController();
                    controller.getConfiguration().setProperty(GEMLayoutController.KEY_OPTIMIZE_ALGORITHM_ENABLED, Boolean.toString(optimize));
                    controller.getConfiguration().setProperty(GEMLayoutController.KEY_PREF_EDGE_LENGTH, Integer.toString(edgeLength));
                    controller.getConfiguration().setProperty(GEMLayoutController.KEY_GRAVITATION, Double.toString(gravity));
                    controller.getConfiguration().setProperty(GEMLayoutController.KEY_AVOID_OVERLAPPING, "true");
                } else {
                    running = false;
                    return;
                }
                LayoutAlgorithm algorithm = controller.getLayoutAlgorithm();
                Properties properties = controller.getConfiguration();
                algorithm.perform(network, true, properties);
                if (stop) {
                    break;
                }
            }
            iterationNo = 1;
            running = false;
        }
    }

    JButton jButton8 = new JButton();
    JLabel functLabel = new JLabel();
    JCheckBox parametersBox = new JCheckBox();
    JToolBar jToolBar1 = new JToolBar();
    JLabel nameLabel = new JLabel();
    JTextArea processText = new JTextArea();
    JButton jButton11 = new JButton();
    JPanel jPanel5 = new JPanel();
    JScrollPane jScrollPane3 = new JScrollPane();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JScrollPane jScrollPane2 = new JScrollPane();
    JButton jButton13 = new JButton();
    private DefaultGraphModel networkModel = new DefaultGraphModel();
    private DSPanel<DSMicroarray> panel = null;
    GeneNetwork network = null;
    BorderLayout borderLayout1 = new BorderLayout();
    private float lineWidthRatio = 50;
    private MicroarrayVisualizer markerVector = null;
    private DSItemList<DSMicroarray> mArrayVector = null;
    private DSMicroarraySet<DSMicroarray> mArraySet = null;
    private GeneNetworkManager gnm = new GeneNetworkManager(networkModel);
    private EdgeMatrix matrix = new EdgeMatrix();
    private int lev1 = 0;
    private int lev2 = 0;
    private PathwayDecoderUtil decoder = null;
    private double threshold = 0.1;
    private AdjacencyMatrix globalMatrix = new AdjacencyMatrix();
    private AdjacencyMatrix partialMatrix = new AdjacencyMatrix();
    private AdjacencyMatrix adjMatrix = globalMatrix;
    private ArrayList caseSet = new ArrayList<DSMicroarray>();
    private ArrayList cntrSet = new ArrayList<DSMicroarray>();
    private int offset = 0;
    private boolean isStopped = false;
    //private boolean singleNode = false;
    private DSGeneMarker singleMark = null;
    //private HashMap singleMap = null;
    private NetworkLayoutAlgorithm nla = new NetworkLayoutAlgorithm();
    JScrollPane jScrollPane1 = new JScrollPane();
    JLabel jLabel1 = new JLabel();
    JTextArea functionText = new JTextArea();
    JLabel jLabel2 = new JLabel();
    JTextField jAffyIdBox = new JTextField();
    JLabel jLabel3 = new JLabel();
    Component component1;
    JTextField geneLabelBox = new JTextField();
    Component component2;
    Component component3;
    JLabel jLabel4 = new JLabel();
    Component component4;
    JSpinner jDepthSpin = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    JComboBox layoutTypeBox = new JComboBox();
    JSlider jSlider1 = new JSlider();
    int sliderValue = 50;
    JToolBar jToolBar2 = new JToolBar();
    JToolBar jToolBar3 = new JToolBar();
    JLabel jLabel5 = new JLabel();
    Component component5;
    JTextField thresholdBox = new JTextField();
    Component component6;
    Component component7;
    JLabel jLabel6 = new JLabel();
    Component component8;
    JTextField edgeNoBox = new JTextField();
    JSlider thresholdSlider = new JSlider();
    JButton jButton4 = new JButton();
    JPanel jPanel1 = new JPanel();
    JSlider edgeLengthSlider = new JSlider();
    BorderLayout borderLayout2 = new BorderLayout();
    JSlider gravitySlider = new JSlider();
    Component component9;
    JCheckBox optimizeCheckBox = new JCheckBox();
    JButton jButton5 = new JButton();
    JButton btnCompare = new JButton();
    JButton jButton6 = new JButton();
    JButton jButton7 = new JButton();
    JCheckBox cytoscapeBtn = new JCheckBox();
    BufferedWriter out = null;
    JCheckBox jCheckBox1 = new JCheckBox();
    HashSet visitedNodes = new HashSet();
    JButton jButton9 = new JButton();

    private GeneProfiler profiler;

    public GeneNetworkPanel(GeneProfiler profiler) {
        this.profiler = profiler;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        component1 = Box.createHorizontalStrut(8);
        component2 = Box.createHorizontalStrut(8);
        component3 = Box.createHorizontalStrut(8);
        component4 = Box.createHorizontalStrut(8);
        component5 = Box.createHorizontalStrut(8);
        component6 = Box.createHorizontalStrut(8);
        component7 = Box.createHorizontalStrut(8);
        component8 = Box.createHorizontalStrut(8);
        component9 = Box.createVerticalStrut(8);
        functLabel.setBorder(BorderFactory.createEtchedBorder());
        functLabel.setHorizontalAlignment(SwingConstants.CENTER);
        functLabel.setText("Function");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton8_actionPerformed(e);
            }
        });
        jButton8.setMaximumSize(new Dimension(100, 26));
        jButton8.setMinimumSize(new Dimension(100, 26));
        jButton8.setPreferredSize(new Dimension(51, 26));
        jButton8.setText("Layout");
        nameLabel.setText("Name: ");
        jButton11.setMaximumSize(new Dimension(100, 26));
        jButton11.setMinimumSize(new Dimension(100, 26));
        jButton11.setPreferredSize(new Dimension(51, 26));
        jButton11.setBorderPainted(true);
        jButton11.setText("Clear");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton11_actionPerformed(e);
            }
        });
        jPanel5.setLayout(gridBagLayout2);
        jPanel5.setMaximumSize(new Dimension(2147483647, 100));
        jButton13.setMaximumSize(new Dimension(100, 26));
        jButton13.setMinimumSize(new Dimension(100, 26));
        jButton13.setVerifyInputWhenFocusTarget(true);
        jButton13.setText("Print");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton13_actionPerformed(e);
            }
        });
        this.setLayout(borderLayout1);
        jLabel1.setBorder(BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Process");
        functionText.setText("");
        functionText.setLineWrap(true);
        functionText.setWrapStyleWord(true);
        processText.setText("");
        processText.setLineWrap(true);
        processText.setWrapStyleWord(true);
        jScrollPane3.setMaximumSize(new Dimension(32767, 60));
        jScrollPane3.setPreferredSize(new Dimension(2, 60));
        jScrollPane1.setMaximumSize(new Dimension(32767, 60));
        jScrollPane1.setPreferredSize(new Dimension(3, 60));
        jLabel2.setText("Affymetrix Id: ");
        jLabel3.setText("Gene:");
        geneLabelBox.setText("");
        jLabel4.setText("Depth:");
        jDepthSpin.setToolTipText("");
        jButton1.setAlignmentX((float) 0.0);
        jButton1.setAlignmentY((float) 0.5);
        jButton1.setDebugGraphicsOptions(0);
        jButton1.setMaximumSize(new Dimension(100, 26));
        jButton1.setMinimumSize(new Dimension(100, 26));
        jButton1.setOpaque(true);
        jButton1.setPreferredSize(new Dimension(51, 26));
        jButton1.setContentAreaFilled(true);
        jButton1.setHorizontalAlignment(SwingConstants.CENTER);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setMnemonic('0');
        jButton1.setText("Draw");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jButton2.setMaximumSize(new Dimension(100, 26));
        jButton2.setMinimumSize(new Dimension(100, 26));
        jButton2.setText("Load");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        jSlider1.setOrientation(JSlider.VERTICAL);
        jSlider1.setMajorTickSpacing(10);
        jSlider1.setMaximum(100);
        jSlider1.setMinorTickSpacing(0);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jSlider1_stateChanged(e);
            }
        });
        jToolBar3.setOrientation(JToolBar.VERTICAL);
        jToolBar3.setAlignmentX((float) 0.0);
        jToolBar3.setToolTipText("");
        jToolBar3.setVerifyInputWhenFocusTarget(true);
        jLabel5.setText("Thresh:");
        thresholdBox.setText("0.04");
        jLabel6.setText("Edge No:");
        edgeNoBox.setText("");
        thresholdSlider.setOrientation(JSlider.VERTICAL);
        thresholdSlider.setMajorTickSpacing(32);
        thresholdSlider.setMaximum(1024);
        thresholdSlider.setMinorTickSpacing(1);
        thresholdSlider.setRequestFocusEnabled(false);
        thresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                thresholdSlider_stateChanged(e);
            }
        });
        jToolBar2.setOrientation(JToolBar.VERTICAL);
        jButton4.setMaximumSize(new Dimension(100, 26));
        jButton4.setMinimumSize(new Dimension(100, 26));
        jButton4.setText("Stop");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton4_actionPerformed(e);
            }
        });
        edgeLengthSlider.setOrientation(JSlider.VERTICAL);
        edgeLengthSlider.setToolTipText("Edge Length");
        edgeLengthSlider.setValue(60);
        jPanel1.setLayout(borderLayout2);
        gravitySlider.setOrientation(JSlider.VERTICAL);
        gravitySlider.setMaximum(100);
        gravitySlider.setMinorTickSpacing(0);
        gravitySlider.setToolTipText("Gravity Value");
        gravitySlider.setValue(100);
        optimizeCheckBox.setText("Optim.");
        cytoscapeBtn.setSelected(true);
        cytoscapeBtn.setText("Cytoscape");
        jCheckBox1.setText("Constant Line Width");
        jToolBar2.add(thresholdSlider, null);
        this.add(jToolBar1, BorderLayout.NORTH);
        jToolBar1.add(jCheckBox1, null);
        jToolBar1.add(cytoscapeBtn, null);
        jToolBar1.add(component1, null);
        jToolBar1.add(jLabel3, null);
        jToolBar1.add(component3, null);
        jToolBar1.add(geneLabelBox, null);
        jToolBar1.add(component2, null);
        jToolBar1.add(jLabel5, null);
        jToolBar1.add(component6, null);
        jToolBar1.add(thresholdBox, null);
        jToolBar1.add(component7, null);
        jToolBar1.add(jLabel6, null);
        jToolBar1.add(component8, null);
        jToolBar1.add(edgeNoBox, null);
        jToolBar1.add(component5, null);
        jToolBar1.add(jLabel4, null);
        jToolBar1.add(component4, null);
        jToolBar1.add(jDepthSpin, null);
        this.add(jPanel5, BorderLayout.SOUTH);
        jPanel5.add(jScrollPane3, new GridBagConstraints(2, 2, 2, 2, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jScrollPane3.getViewport().add(processText, null);
        jPanel5.add(functLabel, new GridBagConstraints(0, 1, 2, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel5.add(nameLabel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jPanel5.add(jScrollPane1, new GridBagConstraints(0, 3, 2, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jScrollPane1.getViewport().add(functionText, null);
        jPanel5.add(jLabel1, new GridBagConstraints(2, 1, 2, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel5.add(jLabel2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jPanel5.add(jAffyIdBox, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(jScrollPane2, BorderLayout.CENTER);
        this.add(jToolBar2, BorderLayout.EAST);
        this.add(jToolBar3, BorderLayout.WEST);
        jToolBar3.add(jButton8, null);
        jToolBar3.add(jButton11, null);
        jToolBar3.add(jButton13, null);
        jToolBar3.add(jButton2, null);
        jToolBar3.add(jButton1, null);
        jToolBar3.add(jButton4, null);
        jToolBar3.add(jPanel1, null);
        jPanel1.add(edgeLengthSlider, BorderLayout.EAST);
        jPanel1.add(gravitySlider, BorderLayout.WEST);
        jToolBar2.add(jSlider1, null);
        try {
            network = new GeneNetwork(networkModel, this, this);
        } catch (Exception ex) {
            System.out.println("Illegal parms");
        }
        jScrollPane2.getViewport().add(network, null);
        jToolBar3.add(component9, null);
        jPanel1.add(optimizeCheckBox, BorderLayout.SOUTH);
        ToolTipManager.sharedInstance().registerComponent(network);
        thresholdSlider.setValue(1024);
    }

    void jButton8_actionPerformed(ActionEvent e) {
        started();
        nla.setIterationNo(1);
        synchronized (nla) {
            nla.start();
        }
    }

    void jButton10_actionPerformed(ActionEvent e) {
        LayoutController controller = new AnnealingLayoutController();
        LayoutAlgorithm algorithm = controller.getLayoutAlgorithm();
        Properties properties = controller.getConfiguration();
        algorithm.perform(network, true, properties);
    }

    void jButton11_actionPerformed(ActionEvent e) {
        networkModel = new DefaultGraphModel();
        gnm = new GeneNetworkManager(networkModel);
        network = new GeneNetwork(networkModel, this, this);
        jScrollPane2.getViewport().add(network, null);
        adjMatrix = partialMatrix = new AdjacencyMatrix();
    }

    void jButton13_actionPerformed(ActionEvent e) {
        network.print();
    }

    public void showName(String name) {
        nameLabel.setText("Name: " + name);
    }

    public void showAffyId(String name) {
        jAffyIdBox.setText(name);
    }

    public void showProcess(String process) {
        processText.setLineWrap(true);
        processText.setText(process);
    }

    public void showFunction(String function) {
        functionText.setLineWrap(true);
        functionText.setText(function);
    }

    public void buildSubGraph(DSGeneMarker marker) {
        double threshold = Double.parseDouble(thresholdBox.getText());
        createNetwork(adjMatrix, marker.getSerial(), threshold, false);
    }

    /**
     * the createNetwork function called from NetworkLayout
     *
     * @param edges   Object[]
     * @param gm      IGenericMarker
     * @param _offset int
     * @param thr     double
     */
    public void createNetwork(Object[] edges, DSGeneMarker gm, int _offset, double thr) {
        offset = _offset;
        if (adjMatrix != partialMatrix) {
            // Check that the matrix is already the partial one and otherwise clean the graph
            gnm.clear();
        }
        adjMatrix = partialMatrix = new AdjacencyMatrix();
        GeneGeneRelationship ggr = new GeneGeneRelationship(markerVector.getDataSetView(), GeneGeneRelationship.RANK_MI, false);
        DSGeneMarker gm0 = null;
        if (mArraySet != null) {
            threshold = thr;
            adjMatrix.setMicroarraySet(mArraySet);
            if (edges != null) {
                for (int i = 0; i < edges.length; i++) {
                    GeneNetworkEdge rc1 = (GeneNetworkEdge) edges[i];
                    gm0 = mArraySet.getMarkers().get(rc1.getId1());
                    DSGeneMarker gm1 = rc1.getMarker2();

                    // First of all, add the MI with respect to the main gene
                    adjMatrix.add(gm0.getSerial(), gm1.getSerial(), (float) rc1.getMI());

                    // *** NEW *** add same interaction but with label for the EvidInt panel...
                    adjMatrix.addInteractionType2(gm0.getSerial(), gm1.getSerial(), rc1.getMI());
                    adjMatrix.addMarkerName(gm0.getSerial());
                    adjMatrix.addMarkerName(gm1.getSerial());

                    for (int j = i + 1; j < edges.length; j++) {
                        GeneNetworkEdge rc2 = (GeneNetworkEdge) edges[j];
                        DSGeneMarker gm2 = rc2.getMarker2();
                        // Now compute mutual information between gm1 and gm2 and add entry to matrix
                        GeneNetworkEdge newRC = ggr.getScore(gm1.getSerial(), gm2.getSerial(), 0, null);
                        adjMatrix.add(gm1.getSerial(), gm2.getSerial(), (float) newRC.getMI());

                        // *** NEW *** add same interaction along with label for the BIND panel...
                        adjMatrix.addInteractionType2(gm1.getSerial(), gm2.getSerial(), newRC.getMI());
                        adjMatrix.addMarkerName(gm1.getSerial());
                        adjMatrix.addMarkerName(gm2.getSerial());
                    }
                }
            }
            // do some cleaning for DPI ???
            // so not every edge exists in the final netwrok.... :P
            adjMatrix.clean(mArraySet, threshold, 0.10);
            adjMatrix.cleanFirstNeighbors(mArraySet, gm0);
            adjMatrix.setMIflag(true); // we tell the adjmtx that the network was built with MI
            createNetwork(adjMatrix, -1, threshold, false);
        } else {
            System.out.println("Must load a microarray set before drawing a network");
        }
    }

    public void createNetwork(AdjacencyMatrix matrix, int geneId, double threshold, boolean clear) {
        if (markerVector != null){
            // New Dataset implementation for Cytoscape
            if (cytoscapeBtn.isSelected()) {
                int depth = ((Integer) jDepthSpin.getValue()).intValue();
                AdjacencyMatrixDataSet dataSet = new AdjacencyMatrixDataSet(matrix, geneId, threshold, depth, "Adjacency Matrix", mArraySet.getLabel(), mArraySet);
                profiler.publishProjectNodeAddedEvent(new ProjectNodeAddedEvent("Adjacency Matrix Added", null, dataSet));
                return;
            }
            offset = 0;
            started();
            visitedNodes.clear();
            GeneGeneRelationship ggr = new GeneGeneRelationship(markerVector.
                    getDataSetView(),
                    org.geworkbench.util.pathwaydecoder.GeneGeneRelationship.
                                              PEARSON, false);
            if (mArraySet != null) {
                if (adjMatrix != matrix) {
                    adjMatrix = matrix;
                }
                lev1 = lev2 = 1;
                int nodeNo = 0;
                if (clear) {
                    gnm.clear();
                }

                // for the case of sending the adjacency matrix to Evidence Integration
                // the code follows:
//            {
                /* making Evidence Integration Panel-compatible*/
                // modify according to 'drawing the network'
                // but this is a huge problem to work on
                // AdjacencyMatrix tmpadj = this.addGWInteractionFromMIAdj(matrix, geneId, threshold, clear);
                // tmpadj.setSource(AdjacencyMatrix.fromGeneNetworkPanelTakenGoodCareOf);
                // tmpadj.setMicroarraySet(mArraySet);
                // now we send only the 'surrogate' adjmatrix
                // and we don't draw the network until we press the 'draw button' in the EvdInt panel
//                AdjacencyMatrixEvent ae = new AdjacencyMatrixEvent(matrix, "Initiate Adjacency Matrix transfer", geneId, lev1, threshold, AdjacencyMatrixEvent.Action.RECEIVE);
//                profiler.publishAdjacencyMatrixEvent(ae);
//            }

                if (cytoscapeBtn.isSelected()) {
                    // saying that this adjmtx comes from GeneNetworkPanel
                    // so the receivedAdjMtx event in BindPanel does not register it and only keeps the
                    // adj matrix sent in the EvidenceIntegration compatible bracket

                    adjMatrix.setSource(AdjacencyMatrix.
                                        fromGeneNetworkPanelNotTakenCareOf);
                    AdjacencyMatrixEvent ae = new AdjacencyMatrixEvent(
                            adjMatrix, "Initiate Adjacency Matrix transfer",
                            geneId, lev1, threshold,
                            AdjacencyMatrixEvent.Action.RECEIVE);
                    profiler.publishAdjacencyMatrixEvent(ae);
                }
                if (geneId < 0) {
                    // when does the geneID (serial) become < 0???
                    // -> that just means we draw the whole network???
                    for (Iterator iter = matrix.getKeys().iterator();
                                         iter.hasNext(); ) {
                        // here we draw the network over and over again until all nodes have been drawn
                        Integer item = (Integer) iter.next();
                        if (cytoscapeBtn.isSelected()) {
                            // drawing in cytoscape, just draw...
                            AdjacencyMatrixEvent ae = new org.geworkbench.
                                    events.AdjacencyMatrixEvent(adjMatrix,
                                    "Draw Network", item.intValue(), lev1,
                                    threshold,
                                    AdjacencyMatrixEvent.Action.DRAW_NETWORK);
                            profiler.publishAdjacencyMatrixEvent(ae);
                        } else {
                            createSubNetwork(item.intValue(), threshold, 1, ggr);
                        }
                    }
                } else {
                    // here the serial number of the gene is normal (>=0)
                    // we get the depth from the dropbox
                    // and then draw the network
                    int depth = ((Integer) jDepthSpin.getValue()).intValue();
                    if (cytoscapeBtn.isSelected()) {
                        AdjacencyMatrixEvent ae = new AdjacencyMatrixEvent(
                                adjMatrix, "Draw Network", geneId, depth,
                                threshold,
                                AdjacencyMatrixEvent.Action.DRAW_NETWORK);
                        profiler.publishAdjacencyMatrixEvent(ae);
                    } else {
                        createSubNetwork(geneId, threshold, depth, ggr);
                    }
                }
                if (cytoscapeBtn.isSelected()) {
                    // finishing touches for cytoscape
                    AdjacencyMatrixEvent ae = new AdjacencyMatrixEvent(
                            adjMatrix, "Post processing", geneId, lev1,
                            threshold, AdjacencyMatrixEvent.Action.FINISH);
                    profiler.publishAdjacencyMatrixEvent(ae);
                }
                if (!cytoscapeBtn.isSelected()) {
                    gnm.layout();
                    updateExpression();
                }
            } else {
                System.out.println(
                        "Must load a microarray set before drawing a network");
            }
            synchronized (nla) {
                nla.start();
            }
        }
    }

    private void createSubNetwork(int geneId, double threshold, int level, GeneGeneRelationship ggr) {
        //        if (level == 0) {
        //            if (!cytoscapeBtn.isSelected()) {
        //                synchronized (nla) { nla.start(); }
        //            }
        //            return;
        //        }
        visitedNodes.add(new Integer(geneId));
        HashMap map = adjMatrix.get(geneId);
        if (map != null) {
            DSGeneMarker gm1 = mArraySet.getMarkers().get(geneId);
            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                Integer id2 = (Integer) it.next();
                if ((id2.intValue() < mArraySet.getMarkers().size()) && (id2.intValue() != 12600)) {
                    Float v12 = (Float) map.get(id2);
                    boolean thresholdTest = false;
                    thresholdTest = v12.doubleValue() > threshold;
                    if (thresholdTest) {
                        boolean cellCreated = false;
                        DSGeneMarker gm2 = mArraySet.getMarkers().get(id2.intValue());
                        String geneName1 = gm1.getShortName();
                        DSGeneMarker gmDup = gnm.getGene(geneName1);
                        if (gmDup != null) {
                            gm1 = gmDup;
                        } else {
                            gnm.addGene(geneName1, gm1);
                        }
                        String geneName2 = gm2.getShortName();
                        DSGeneMarker gmDup2 = gnm.getGene(geneName2);
                        if (gmDup2 != null) {
                            gm2 = gmDup2;
                        } else {
                            gnm.addGene(geneName2, gm2);
                        }
                        //                        if (gm1.getSerial() < gm2.getSerial()) {
                        if (!geneName2.equalsIgnoreCase(geneName1)) {
                            DefaultGraphCell cell1 = (DefaultGraphCell) gnm.getNode(gm1);
                            if (cell1 == null) {
                                int connectionNo = adjMatrix.getConnectionNo(gm1.getSerial(), threshold);
                                if (!cytoscapeBtn.isSelected()) {
                                    gnm.createCell(gm1, new Point(20, 100 + offset), false, connectionNo);
                                }
                                cellCreated = true;
                                //                                if (stopped()) {
                                //                                    if (!cytoscapeBtn.isSelected()) {
                                //                                        gnm.layout();
                                //                                        if (!nla.isRunning()) {
                                //                                            nla.start();
                                //                                        }
                                //                                    }
                                //                                    return;
                                //                                }
                            }
                            DefaultGraphCell cell2 = (DefaultGraphCell) gnm.getNode(gm2);
                            if (cell2 == null) {
                                int connectionNo = adjMatrix.getConnectionNo(gm2.getSerial(), threshold);
                                if (!cytoscapeBtn.isSelected()) {
                                    gnm.createCell(gm2, new Point(320, 20 + offset), true, connectionNo);
                                }
                                cellCreated = true;
                                offset += 30;
                                //                                if (stopped()) {
                                //                                    if (!cytoscapeBtn.isSelected()) {
                                //                                        gnm.layout();
                                //                                        if (!nla.isRunning()) {
                                //                                            nla.start();
                                //                                        }
                                //                                    }
                                //                                    return;
                                //                                }
                            }
                            double rho = ggr.getPearsonCorrelation(gm1.getSerial(), gm2.getSerial());
                            if (jCheckBox1.isSelected()) {
                                gnm.connect(gm1, gm2, false, 2.0f, rho);
                            } else {
                                gnm.connect(gm1, gm2, false, (float) (v12.floatValue() * lineWidthRatio), rho);

                            }
                            if ((level > 1) && !visitedNodes.contains(id2)) {
                                createSubNetwork(gm2.getSerial(), threshold, level - 1, ggr);
                            }
                            //                            if (stopped()) {
                            //                                if (!cytoscapeBtn.isSelected()) {
                            //                                    if (!nla.isRunning()) {
                            //                                        nla.start();
                            //                                    }
                            //                                }
                            //                                return;
                            //                            }
                        } else {
                            gnm.addGene(geneName1, gm2);
                        }
                        //                        }
                    }
                }
            }
            //            if (!cytoscapeBtn.isSelected()) {
            //                if (!nla.isRunning()) {
            //                    nla.start();
            //                }
            //            }
        }
    }

    private void updateExpression() {
        for (Iterator iter = gnm.getNodes().keySet().iterator(); iter.hasNext();) {
            DSGeneMarker item = (DSGeneMarker) iter.next();
            checkExpression(item);
        }
        network.getGraphLayoutCache().edit(gnm.getAttributes(), null, null, null);
    }

    public void set(DSMicroarraySet _maSet, org.geworkbench.util.microarrayutils.MicroarrayVisualizer _maViz, DSItemList<DSMicroarray> _marV) {
        markerVector = _maViz;
        mArrayVector = _marV;
        mArraySet = _maSet;
        decoder = new PathwayDecoderUtil(markerVector.getDataSetView());
    }

    private GeneNetworkEdge findBestNeighbor(Object[] relations, GeneNetworkEdge rc) {
        if (mArraySet == null) {
            System.out.println("Must load a microarray set before drawing a network");
            return null;
        }

        GeneGeneRelationship ggr = new GeneGeneRelationship(markerVector.getDataSetView(), GeneGeneRelationship.RANK_MI, false);
        GeneNetworkEdge bestRC = rc;
        double miAB = rc.getThreshold();
        double minMI = miAB * 1.15;
        double maxMI = 0;
        boolean active = false;
        for (int i = 0; i < relations.length; i++) {
            /**
             * Checks all connections rc1 with higher affinity to see if rc could
             * occur as rc1 -> rc rather than directly
             */
            GeneNetworkEdge rc1 = (GeneNetworkEdge) relations[i];
            double miAC = rc1.getThreshold();
            if (miAC <= miAB) {
                /**
                 * Break because the list is score sorted so that all subsequent rcs will
                 * have worst score
                 */
                break;
            }
            GeneNetworkEdge betterRC = ggr.getScore(rc.getMarker2().getSerial(), rc1.getMarker2().getSerial(), 0, null);
            double miBC = betterRC.getThreshold();
            if (miBC > miAB) {
                // We are sure that this indirect is a bit better
                double mi = miAC + miBC;
                if (!active) {
                    active = (miAC > minMI) && (miBC > minMI);
                    if (mi > maxMI) {
                        maxMI = mi;
                        bestRC = betterRC;
                        bestRC.setActive(active);
                    }
                } else {
                    boolean tmpactive = (miAC > minMI) && (miBC > minMI);
                    if ((tmpactive) && (mi > maxMI)) {
                        maxMI = mi;
                        bestRC = betterRC;
                        bestRC.setActive(tmpactive);
                    }
                }
            }
        }

        return bestRC;
    }

    /**
     * Load the adj matrix
     */
    void jButton2_actionPerformed(ActionEvent e) {
        if (mArraySet == null) {
            System.out.println("Must load a microarray set before drawing a network");
            return;
        }
        String dir = LoadData.getLastDataDirectory();
        JFileChooser chooser = new JFileChooser(dir);
        ExampleFilter filter = new ExampleFilter();
        filter.addExtension("adj");
        filter.setDescription("Adjacency Matrix");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            adjMatrix = globalMatrix = new AdjacencyMatrix();
            String filename = chooser.getSelectedFile().getAbsolutePath();
            String line = null; 
            boolean namesAlone = false;
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                while ((line = br.readLine()) != null){
                    if (!line.startsWith(">")){
                        if (!line.contains(":")){
                            namesAlone = true;
                            break;
                        }
                    }
                }
                br.close();
            } catch (IOException ioe){}
            if (namesAlone)
                adjMatrix.readGeneNames(filename, mArraySet);
            else
                adjMatrix.read(filename, mArraySet);
        }
    }

    /**
     * Draw the network in cytoscape / network browser
     */
    void jButton1_actionPerformed(ActionEvent e) {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    if ((out == null) && cytoscapeBtn.isSelected()) {
                        try {
                            // create a buffered writer
                            out = new BufferedWriter(new FileWriter(System.getProperty("temporary.files.directory") + "mydata/cytoscape.sif"));
                        } catch (IOException ex1) {

                        }
                    }

                    if (adjMatrix != globalMatrix) {
                        // Check that the matrix is already the global one and otherwise clean the graph
                        gnm.clear();
                        adjMatrix = globalMatrix;
                    }

                    String geneLabel = geneLabelBox.getText().toLowerCase();
                    double threshold = Double.parseDouble(thresholdBox.getText());
                    // Checks if we should show the entire network or just the subnetwork of a gene.
                    if (geneLabel.equalsIgnoreCase("")) {
                        // Must show the whole network
                        //singleNode = false;
                        singleMark = null;
                        //singleMap = null;
                        createNetwork(adjMatrix, -1, threshold, false);
                    } else {
                        // Must show only specific markers
                        // here we show all the markers matching "geneLable"
                        DSItemList<DSGeneMarker> markers = decoder.matchingMarkers(geneLabel);
                        for (int i = 0; i < markers.size(); i++) {
                            //singleNode = true;
                            singleMark = markers.get(i);
                            //singleMap = adjMatrix.get(singleMark.getSerial());
                            createNetwork(adjMatrix, markers.get(i).getSerial(), threshold, false);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex);
                    ex.printStackTrace();
                } finally {
                    if (out != null) {
                        if (!cytoscapeBtn.isSelected()) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException ex2) {
                            }
                            out = null;
                        }
                    }
                }
                return adjMatrix;
            }
        };
        worker.start();
    }

    void jButton3_actionPerformed(ActionEvent e) {
        class SortedGene implements Comparable {
            public DSGeneMarker gm = null;
            public int count = 0;
            HashSet ids = new HashSet();

            public int compareTo(Object o) {
                SortedGene sg = (SortedGene) o;
                if (this.ids.size() < sg.ids.size()) {
                    return -1;
                } else if (this.ids.size() > sg.ids.size()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        if (mArraySet == null) {
            System.out.println("Must load a microarray set before drawing a network");
            return;
        }
        // Get the threshold used to produce the results
        double threshold = Double.parseDouble(thresholdBox.getText());

        ArrayList sortedGeneList = new ArrayList();
        HashMap sortedGeneMap = new HashMap();
        int[] nodes = new int[10000];
        int maxNode = 0;
        BufferedWriter writer = null;
        try {
            System.out.println("Results in: " + System.getProperty("temporary.files.directory") + "ConnectedGenes.txt");
            writer = new BufferedWriter(new FileWriter(System.getProperty("temporary.files.directory") + "ConnectedGenes.txt"));
            //            writer = new BufferedWriter(new FileWriter(System.getProperty(
            //                "temporary.files.directory") + "ConnectedGenes.txt"));
            for (int i = 0; i < mArraySet.getMarkers().size(); i++) {
                int n = 0;
                int geneId = adjMatrix.getMappedId(i);
                DSGeneMarker gm = mArraySet.getMarkers().get(geneId);
                String shortName = gm.getShortName();
                HashSet ids = new HashSet();
                HashMap row = (HashMap) adjMatrix.get(geneId);
                if (row != null) {
                    for (Iterator iter = row.keySet().iterator(); iter.hasNext();) {
                        Integer item = (Integer) iter.next();
                        Float value = (Float) row.get(item);
                        if (value.floatValue() >= threshold) {
                            DSGeneMarker tmpGM = mArraySet.getMarkers().get(item.intValue());
                            String sn = tmpGM.getShortName();
                            if ((sn.compareToIgnoreCase(shortName) != 0) && (sn.compareToIgnoreCase("ExoBCL6") != 0)) {
                                if ((shortName.compareToIgnoreCase("MYC") == 0) && (tmpGM.getLabel().compareToIgnoreCase("1936_s_at")) == 0) {

                                } else {
                                    n++;
                                    ids.add(sn);
                                }
                            }
                        }
                    }
                } else {
                    n = 0;
                }

                SortedGene prev = (SortedGene) sortedGeneMap.get(new Integer(geneId));
                if (prev != null) {
                    //                    int c0 = prev.ids.size();
                    //                    int c1 = ids.size();
                    //                    prev.ids.addAll(ids);
                    //                    int c2 = prev.ids.size();
                    //                    if(shortName.compareToIgnoreCase("MYC") == 0) {
                    //                        System.out.println(ids);
                    //                    }
                } else {
                    prev = new SortedGene();
                    prev.gm = gm;
                    int c0 = prev.ids.size();
                    int c1 = ids.size();
                    prev.ids.addAll(ids);
                    int c2 = prev.ids.size();
                    //                    if(shortName.compareToIgnoreCase("MYC") == 0) {
                    //                        System.out.println(ids);
                    //                    }
                    sortedGeneMap.put(new Integer(geneId), prev);
                    sortedGeneList.add(prev);
                }
                if (n < 1000) {
                    nodes[n]++;
                    maxNode = Math.max(n + 1, maxNode);
                }
            }
            //            writer.flush();
            //            writer.close();
        }
                //        catch (IOException ex) {
        catch (Exception ex) {
        }
        try {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File writeFile = chooser.getSelectedFile();

            writer = new BufferedWriter(new FileWriter(writeFile));
            //            writer = new BufferedWriter(new FileWriter(System.getProperty(
            //                "temporary.files.directory") + "SFNetwork.txt"));
            //            for (int i = 0; i < maxNode; i++) {
            //                writer.write(Integer.toString(i) + "\t" +
            //                             Integer.toString(nodes[i]) +
            //                             "\n");
            Object[] objects = sortedGeneList.toArray();
            Arrays.sort(objects);
            for (int i = 1; i < maxNode; i++) {
                if (nodes[i] != 0) {
                    writer.write(Integer.toString(i) + "\t" + Integer.toString(nodes[i]) + "\n");
                }

            }
            for (int i = 0; i < objects.length; i++) {
                SortedGene sg = (SortedGene) objects[i];
                writer.write(sg.gm.getLabel() + "\t" + sg.gm.getShortName() + "\t" + sg.gm.getGeneId() + "\t" + sg.gm.getUnigene() + "\t" + sg.ids.size() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }
    }

    void setPhenotype() {
        if (mArraySet == null) {
            System.out.println("Must load a microarray set before drawing a network");
            return;
        }
        int n = panel.size();
        ArrayList cases = new ArrayList();
        caseSet.clear();
        cntrSet.clear();
        gnm.getAttributes().clear();
        DSAnnotationContext context = CSAnnotationContextManager.getInstance().getCurrentContext(mArraySet);
        CSMicroarraySetView<DSGeneMarker, DSMicroarray> view = new CSMicroarraySetView<DSGeneMarker, DSMicroarray>();
        view.setDataSet(mArraySet);
        view.setItemPanel(panel);
        view.useItemPanel(true);
        for (DSMicroarray ma : view.items()) {
            String value = context.getClassForItem(ma);
            if (value.equals(CSAnnotationContext.CLASS_CASE)) {
                caseSet.add(ma);
            } else if (value.equals(CSAnnotationContext.CLASS_CONTROL)) {
                cntrSet.add(ma);
            }
        }
        updateExpression();
    }

    void checkExpression(DSGeneMarker gm) {
        Color white = new Color(255, 255, 255);
        org.geworkbench.bison.util.Normal cases = new org.geworkbench.bison.util.Normal();
        org.geworkbench.bison.util.Normal controls = new Normal();

        if ((caseSet.size() == 0) || (cntrSet.size() == 0)) {
            changeColor(gm, white);
        } else {
            for (Iterator<DSMicroarray> iter = caseSet.iterator(); iter.hasNext();) {
                DSMicroarray item = iter.next();
                DSMarkerValue value = item.getMarkerValue(gm.getSerial());
                cases.add(value.getValue());
            }
            for (Iterator<DSMicroarray> iter = cntrSet.iterator(); iter.hasNext();) {
                DSMicroarray item = iter.next();
                DSMarkerValue value = item.getMarkerValue(gm.getSerial());
                controls.add(value.getValue());
            }
            cases.compute();
            controls.compute();
            // The slider should return a number between 0.5 and 2;
            double ratio = (double) (sliderValue + 50) / 100.0;
            double ttest = Normal.tTest(cases, controls);
            int intensity = Math.min(255, (int) Math.abs(ratio * ttest * 255 / 10));
            if (intensity > 50) {
                if (ttest > 0) {
                    changeColor(gm, new Color(255, 255 - intensity, 255 - intensity));
                } else {
                    changeColor(gm, new Color(255 - intensity, 255, 255 - intensity));
                }
            } else {
                changeColor(gm, white);
            }
        }
    }

    /**
     * phenotypeSelectorAction
     *
     * @param e PhenotypeSelectorEvent
     */
    @Subscribe public void receive(org.geworkbench.events.PhenotypeSelectorEvent e, Object source) {
        if (mArraySet != null) {
            panel = e.getTaggedItemSetTree();
            setPhenotype();
            invalidate();
            repaint();
        }
    }

    public void changeColor(DSGeneMarker gm, Color c) {
        DefaultGraphCell cell = (DefaultGraphCell) gnm.getNode(gm);
        if (cell != null) {
            Map map = GraphConstants.createMap();
            if (c == Color.white) {
                network.getGraphLayoutCache().setVisible(cell, false);
                Map m = network.getGraphLayoutCache().getHiddenSet();
                Object[] cells = {cell};
                Set edges = gnm.getEdges(cells);
            } else {
                GraphConstants.setBackground(map, c);
                network.getGraphLayoutCache().setVisible(cell, false);
            }
            gnm.getAttributes().put(cell, map);
        }
    }

    void jSlider1_stateChanged(ChangeEvent e) {
        sliderValue = jSlider1.getValue();
        setPhenotype();
    }

    void thresholdSlider_stateChanged(ChangeEvent e) {
        int bin = thresholdSlider.getValue();
        int edgeNo = adjMatrix.getEdgeNo(bin);
        double threshold = adjMatrix.getThreshold(bin);
        edgeNoBox.setText(String.valueOf(edgeNo));
        thresholdBox.setText(String.valueOf(threshold));

        Object[] roots = this.networkModel.getRoots(this.networkModel);
        List allNodes = this.networkModel.getDescendantList(networkModel, roots);
        for (Iterator iter = allNodes.iterator(); iter.hasNext();) {
            DefaultGraphCell item = (DefaultGraphCell) iter.next();
            if (item.getUserObject() instanceof GenericMarkerNode) {
                GenericMarkerNode node = (GenericMarkerNode) item.getUserObject();
                int connectionNo = adjMatrix.getConnectionNo(node.getSerial(), threshold);
                node.setConnectionNo(connectionNo);
            }
        }
        network.repaint();
    }

    boolean stopped() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;
    }

    public void started() {
        isStopped = false;
    }

    void jButton4_actionPerformed(ActionEvent e) {
        stop();
    }

    public void showGraph(int x, int y, int gene1, int gene2) {
        final JFrame window = new JFrame();
        GenePairPlot plot = new GenePairPlot(mArraySet);
        window.addKeyListener(new KeyListener() {
            /**
             * keyPressed
             *
             * @param e KeyEvent
             */
            public void keyPressed(KeyEvent e) {
                window.setVisible(false);
            }

            /**
             * keyReleased
             *
             * @param e KeyEvent
             */
            public void keyReleased(KeyEvent e) {
            }

            /**
             * keyTyped
             *
             * @param e KeyEvent
             */
            public void keyTyped(KeyEvent e) {
            }
        });
        plot.drawChart(gene1, gene2);
        window.getContentPane().add(plot);
        window.setBounds(x, y, 200, 200);
        window.setVisible(true);
    }

    void jButton5_actionPerformed(ActionEvent e) {
        double threshold = Double.parseDouble(thresholdBox.getText());
        adjMatrix = globalMatrix = new AdjacencyMatrix();
    }

    void getNumInferredConnections() {
        int[] numSamplesArr = {500};
        int numIterations = 1000;
        for (int numSamplesCtr = 0; numSamplesCtr < numSamplesArr.length; numSamplesCtr++) {
            int numSamples = numSamplesArr[numSamplesCtr];

            GeneGeneRelationship bgGgr = new GeneGeneRelationship(null, 0, true);
            double noise = 35.0;
            bgGgr.computeBackground(numIterations, numSamples, GeneGeneRelationship.RANK_MI, 1, noise);

            double pValue = 0.2;
            double miThresh = bgGgr.getMiByPValue(pValue);

            DSItemList<DSMicroarray> microarrays = new CSItemList<DSMicroarray>();
            for (int maCtr = 0; maCtr < numSamples; maCtr++) {
                DSMicroarray curMArray = mArraySet.get(maCtr);
                microarrays.add(maCtr, curMArray);
            }

            int numConnections[] = bgGgr.getNumConnections(mArraySet, miThresh, microarrays);

            System.out.println("Noise " + noise + "\tNum Samples " + numSamples + "\tMi Thresh " + miThresh + "\tP Value " + pValue + "\tNum Connections " + numConnections[0] + "\tNum Non Connections " + numConnections[1]);
        }
    }


    public GeneProfiler getGeneProfiler() {
        return profiler;
    }

    public static void main(String[] args) {
        GeneNetworkPanel p = new GeneNetworkPanel(null);
    }
}
