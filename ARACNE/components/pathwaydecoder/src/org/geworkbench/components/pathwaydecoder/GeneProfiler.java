package org.geworkbench.components.pathwaydecoder;


import distributions.BetaDistribution;
import org.geworkbench.bison.algorithm.AlgorithmEvent;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectSelection;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Script;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.*;
import org.geworkbench.util.SwingWorker;
import org.geworkbench.util.network.EdgeMatrix;
import org.geworkbench.util.network.GeneNetworkEdge;
import org.geworkbench.util.network.GeneNetworkEdgeImpl;
import org.geworkbench.util.pathwaydecoder.GeneGeneRelationship;
import org.geworkbench.util.pathwaydecoder.GeneNetworkManager;
import org.geworkbench.util.pathwaydecoder.PathwayDecoderUtil;
import org.geworkbench.util.pathwaydecoder.RankSorter;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.jfree.chart.*;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jgraph.graph.DefaultGraphModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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

@AcceptTypes({DSMicroarraySet.class}) public class GeneProfiler extends org.geworkbench.util.microarrayutils.MicroarrayVisualizer implements VisualPlugin {

    static Log log = LogFactory.getLog(GeneProfiler.class);

    public class MyXYToolTip extends StandardXYToolTipGenerator {
        public String generateToolTip(XYDataset data, int series, int item) {
            String result = "Unknown: ";
            try {
                ArrayList list = (ArrayList) xyPoints.get(series);
                RankSorter rs = (RankSorter) list.get(item);
                DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet) getDataSet();
                if (maSet != null) {
                    if (rs.id < maSet.size()) {
                        DSAnnotationContext context = CSAnnotationContextManager.getInstance().getCurrentContext(maSet);
                        DSMicroarray ma = maSet.get(rs.id);
                        String[] values = context.getLabelsForItem(ma);
                        if (values.length > 0) {
                            return ma.getLabel() + ": " + values[0] + " [" + rs.x + "," + rs.y + "]";
                        } else {
                            return ma.getLabel() + ": Unknown " + " [" + rs.x + "," + rs.y + "]";
                        }
                    } else {
                        return "";
                    }
                } else {
                    System.out.println("Must load a microarray set before drawing a network");
                    return null;
                }
            } catch (Exception e) {
                log.error(e);
                return result;
            }
        }
    }


    public class MyChartMouseListener implements ChartMouseListener {
        public void chartMouseClicked(ChartMouseEvent e) {
            graph_mouseClicked(e);
        }

        public void chartMouseMoved(ChartMouseEvent e) {
            graph_mouseMoved(e);
        }
    }


    static private final int mycSeriesNo = 3;

    //  private DSPanel<DSMicroarray> selectedPanel = null;
    private DefaultGraphModel networkModel = new DefaultGraphModel();
    private GeneNetworkManager gnm = new GeneNetworkManager(networkModel);
    private EdgeMatrix matrix = new EdgeMatrix();
    private AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
    private int lev1 = 0;
    private int lev2 = 0;
    //    private int type = GeneGeneRelationship.RANK_MI;
    private int type = CSInformationTheory.RANK_MI;
    private boolean deserializing = false;
    private boolean serializing = false;
    private boolean reducing = false;
    private boolean creating = false;
    private final int BASICPANEL =0;
    private final int CONDITIONALPANEL = 1;
    //private GeneGeneRelationship ggr = null;
    private JFreeChart mainChart = ChartFactory.createXYLineChart(null, "Score", "Probability", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
    private JFreeChart bkgChart = ChartFactory.createXYLineChart(null, "Score", "Probability", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend

    //private boolean computeBkd = true;
    JPanel mainPanel = new JPanel();
    JTabbedPane tabbedPanels = new JTabbedPane();
    JPanel profilerPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JButton jButton1 = new JButton();
    JLabel jLabel2 = new JLabel();
    JTextField thresholdBox = new JTextField();
    JLabel jLabel3 = new JLabel();
    JTextField geneLabelBox = new JTextField();
    JLabel jLabel4 = new JLabel();
    JScrollPane jScrollPane1 = new JScrollPane();
    DefaultListModel model = new DefaultListModel();
    JList jList1 = new JList(model);
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    int markerNo = 0;
    ArrayList affected = new ArrayList();
    ChartPanel graph1 = new ChartPanel(mainChart);
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    JCheckBox rankPlotChkBox = new JCheckBox();
    BorderLayout borderLayout3 = new BorderLayout();
    JProgressBar progressBar = new JProgressBar();
    JButton jButton12 = new JButton();
    SpinnerNumberModel spinModel = new SpinnerNumberModel();
    JCheckBox showAllMABox = new JCheckBox();
    ButtonGroup rankCriteriaGroup = new ButtonGroup();
    JPanel jPanel3 = new JPanel();
    JRadioButton jRadioButton1 = new JRadioButton();
    JRadioButton jRadioButton2 = new JRadioButton();
    JRadioButton jRadioButton3 = new JRadioButton();
    JRadioButton jRadioButton4 = new JRadioButton();
    ChartPanel histogramGraph = new ChartPanel(mainChart);
    JCheckBox secondGeneBox = new JCheckBox();
    JCheckBox mycBox = new JCheckBox();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JTextField fromExpBox = new JTextField();
    JTextField secondMarkerBox = new JTextField();
    JTextField toExpBox = new JTextField();
    JToggleButton filterBtn = new JToggleButton();
    JCheckBox pvalueCheckBox = new JCheckBox();
    RankSorter[] xyValues = null;
    ArrayList xyPoints = null;
    int maNo = 1;
    CSInformationTheory info = null;

    private JPopupMenu jGenePopup = new JPopupMenu();

    class ArrayDataList extends AbstractListModel {
        ArrayList list = null;

        ArrayDataList(ArrayList aList) {
            list = aList;
        }

        public int getSize() {
            return list.size();
        }

        public Object getElementAt(int index) {
            return list.get(index);
        }
    }


    HighLowAnalysisPanel conditionalAnalysisPanel = new HighLowAnalysisPanel();
    GeneNetworkPanel geneNetworkPanel = new GeneNetworkPanel(this);
    GridBagLayout gridBagLayout4 = new GridBagLayout();
    JPanel profilerSubPanel = new JPanel();
    BorderLayout borderLayout5 = new BorderLayout();
    JPanel statisticsPanel = new JPanel();
    ChartPanel graph2 = new ChartPanel(mainChart);
    JPanel jPanel7 = new JPanel();
    BorderLayout borderLayout6 = new BorderLayout();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JTextField jTextField2 = new JTextField();
    JTextField jTextField3 = new JTextField();
    JButton jButton8 = new JButton();
    ChartPanel backgroundGraph = new ChartPanel(bkgChart);
    JCheckBox jCheckBox1 = new JCheckBox();
    JLabel jLabel9 = new JLabel();
    JTextField txtIterations = new JTextField();
    JButton jButton9 = new JButton();

    public GeneProfiler() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        component1 = Box.createVerticalStrut(8);
        mainPanel.setLayout(borderLayout1);
        profilerSubPanel.setLayout(gridBagLayout1);
        jButton1.setMaximumSize(new Dimension(100, 27));
        jButton1.setMinimumSize(new Dimension(100, 27));
        jButton1.setPreferredSize(new Dimension(100, 27));
        jButton1.setText("Bootstrapping");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jLabel2.setToolTipText("");
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("Mutual Info Thresh.");
        thresholdBox.setMaximumSize(new Dimension(200, 2147483647));
        thresholdBox.setMinimumSize(new Dimension(100, 20));
        thresholdBox.setPreferredSize(new Dimension(38, 22));
        thresholdBox.setText("0.2");
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText("Gene Label:");
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel4.setText(" Search Box: ");
        geneLabelBox.setMaximumSize(new Dimension(200, 22));
        geneLabelBox.setMinimumSize(new Dimension(38, 22));
        geneLabelBox.setPreferredSize(new Dimension(38, 22));
        geneLabelBox.setText("");
        graph1.addChartMouseListener(new MyChartMouseListener());

        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jList1_mouseClicked(e);
            }
        });

        jList1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int index = jList1.getSelectedIndex();
                    if (index == -1) {
                    } else {
                        displaySelection1(index);
                    }
                }
            }
        });
        graph1.setBackground(UIManager.getColor("OptionPane.warningDialog.titlePane.background"));
        graph1.setBorder(BorderFactory.createEtchedBorder());
        graph1.setDebugGraphicsOptions(0);
        graph1.setDoubleBuffered(false);
        graph1.setMaximumSize(new Dimension(2000, 1000));
        graph1.setMinimumSize(new Dimension(200, 200));
        graph1.setPreferredSize(new Dimension(200, 4));
        graph1.setChart(mainChart);
        jList1.setMaximumSize(new Dimension(200, 2147483647));
        jList1.setMinimumSize(new Dimension(100, 0));
        jList1.setFixedCellHeight(15);
        jList1.setFixedCellWidth(300);

        jButton2.setMaximumSize(new Dimension(100, 27));
        jButton2.setMinimumSize(new Dimension(100, 27));
        jButton2.setPreferredSize(new Dimension(100, 27));
        jButton2.setActionCommand("Analyze");
        jButton2.setText("Analyze (2D)");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        jButton3.setMaximumSize(new Dimension(100, 27));
        jButton3.setMinimumSize(new Dimension(100, 27));
        jButton3.setPreferredSize(new Dimension(100, 27));
        jButton3.setText("Analyze (3D)");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton3_actionPerformed(e);
            }
        });
        rankPlotChkBox.setText("Rank Statistics Plot");
        rankPlotChkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rankPlotChkBox_actionPerformed(e);
            }
        });

        jButton12.setMaximumSize(new Dimension(100, 27));
        jButton12.setMinimumSize(new Dimension(100, 27));
        jButton12.setPreferredSize(new Dimension(100, 27));
        jButton12.setText("Create Network");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton12_actionPerformed(e);
            }
        });
        progressBar.setMaximumSize(new Dimension(32767, 26));
        progressBar.setMinimumSize(new Dimension(10, 30));
        progressBar.setPreferredSize(new Dimension(148, 30));
        jScrollPane1.setMinimumSize(new Dimension(200, 22));
        showAllMABox.setSelected(true);
        showAllMABox.setText("All Arrays");
        showAllMABox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllMABox_actionPerformed(e);
            }
        });

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cytoscape_actionPerformed(e);
            }
        };
        cytoscape.addActionListener(listener);
        geneNetworkPanel.cytoscapeBtn.addActionListener(listener);

        jPanel3.setBorder(BorderFactory.createEtchedBorder());
        jPanel3.setLayout(gridBagLayout4);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Mutual Information (Fast)");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRadioButton1_actionPerformed(e);
            }
        });
        jRadioButton2.setText("Mutual Information (Accurate)");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRadioButton2_actionPerformed(e);
            }
        });
        jRadioButton3.setText("Linear");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRadioButton3_actionPerformed(e);
            }
        });
        jRadioButton4.setText("Pearson");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRadioButton4_actionPerformed(e);
            }
        });
        histogramGraph.setBorder(BorderFactory.createEtchedBorder());
        secondGeneBox.setText("2nd Mrk");
        mycBox.setText("Myc");
        fromExpBox.setMinimumSize(new Dimension(50, 20));
        fromExpBox.setPreferredSize(new Dimension(11, 20));
        fromExpBox.setText("0");
        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterBtn_actionPerformed(e);
            }
        });
        toExpBox.setMinimumSize(new Dimension(50, 20));
        toExpBox.setText("0");
        secondMarkerBox.setText("");
        pvalueCheckBox.setSelected(true);
        pvalueCheckBox.setText("p-Value Distribution");
        pvalueCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pvalueCheckBox_actionPerformed(e);
            }
        });
        profilerPanel.setLayout(borderLayout5);
        graph2.setPreferredSize(new Dimension(200, 4));
        graph2.setMinimumSize(new Dimension(200, 200));
        graph2.setMaximumSize(new Dimension(2000, 1000));
        graph2.setDoubleBuffered(false);
        graph2.setDebugGraphicsOptions(0);
        graph2.setBorder(BorderFactory.createEtchedBorder());
        graph2.setBackground(UIManager.getColor("OptionPane.warningDialog.titlePane.background"));
        statisticsPanel.setLayout(borderLayout6);
        jPanel7.setLayout(gridBagLayout5);
        jLabel6.setText("Alpha*sqrt(N)");
        jLabel7.setText("Sigma");
        jLabel8.setText("Microarray No");
        jButton8.setText("Plot");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton8_actionPerformed(e);
            }
        });
        jTextField1.setText("0.004");
        jTextField3.setText("100");
        jCheckBox1.setText("Log Scale");
        jLabel9.setText("Iterations");
        jButton9.setText("Beta Plot");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton9_actionPerformed(e);
            }
        });
        printGraphBtn.setMaximumSize(new Dimension(100, 27));
        printGraphBtn.setMinimumSize(new Dimension(100, 27));
        printGraphBtn.setPreferredSize(new Dimension(100, 27));
        printGraphBtn.setText("Print Graph");
        printGraphBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printGraphBtn_actionPerformed(e);
            }
        });
        jButton15.setMaximumSize(new Dimension(100, 27));
        jButton15.setMinimumSize(new Dimension(100, 27));
        jButton15.setPreferredSize(new Dimension(100, 27));
        jButton15.setText("Export Genes");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton15_actionPerformed(e);
            }
        });
        jButton14.setMaximumSize(new Dimension(100, 27));
        jButton14.setMinimumSize(new Dimension(100, 27));
        jButton14.setPreferredSize(new Dimension(100, 27));
        jButton14.setText("Export Graph");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton14_actionPerformed(e);
            }
        });
        printListBtn.setMaximumSize(new Dimension(100, 27));
        printListBtn.setMinimumSize(new Dimension(100, 27));
        printListBtn.setPreferredSize(new Dimension(100, 27));
        printListBtn.setText("Print Genes");
        printListBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printListBtn_actionPerformed(e);
            }
        });
        thresholdIsMICheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                thresholdIsMICheckBox_actionPerformed(e);
            }
        });
        thresholdIsMICheckBox.setText("Mutual Information Distribution");
        thresholdIsMICheckBox.setSelected(true);
        markerBox.setBackground(SystemColor.control);
        markerBox.setText("");
        basicPanel.setLayout(gridBagLayout8);
        conditionalPanel.setLayout(gridBagLayout7);
        jLabel1.setRequestFocusEnabled(true);
        jLabel1.setText("Condition Gene:");
        jLabel10.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel9.setBorder(BorderFactory.createEtchedBorder());
        jPanel9.setLayout(gridBagLayout6);
        jLabel10.setText("Exp. Range From:");
        jLabel11.setText("Exp. Range To:");
        jPanel10.setLayout(borderLayout8);

        jPanel6.setLayout(gridBagLayout3);
        jPanel10.setBorder(BorderFactory.createEtchedBorder());
        jPanel11.setLayout(gridBagLayout9);
        jPanel12.setLayout(borderLayout9);
        txtIterations.setText("100");
        chkHistogram.setText("histogram");
        cytoscape.setToolTipText("");

        cytoscape.setSelected(true);
        cytoscape.setText("Cytoscape");
        jMenuAdd2panel.setText("Add To Set");
        jMenuAdd2panel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuAdd2panel_actionPerformed(e);
            }
        });
        mainPanel.add(tabbedPanels, BorderLayout.CENTER);
        profilerPanel.add(profilerSubPanel, BorderLayout.CENTER);
        tabbedPanels.add(profilerPanel, "Profiler");
        profilerSubPanel.add(jScrollPane1, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jScrollPane1.getViewport().add(jList1, null);
        jPanel3.add(jRadioButton2, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel3.add(jRadioButton1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel3.add(jRadioButton3, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel3.add(jRadioButton4, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        profilerSubPanel.add(jTabbedPane1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        jTabbedPane1.add(basicPanel, "Basic");
        basicPanel.add(jLabel3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        basicPanel.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        basicPanel.add(geneLabelBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        basicPanel.add(thresholdBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        basicPanel.add(jPanel3, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        jTabbedPane1.add(conditionalPanel, "Conditional");
        conditionalPanel.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        conditionalPanel.add(secondMarkerBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        conditionalPanel.add(jPanel9, new GridBagConstraints(2, 0, 1, 2, 0.4, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(fromExpBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(toExpBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(jLabel10, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        jPanel9.add(jLabel11, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        conditionalPanel.add(filterBtn, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
        conditionalPanel.add(component1, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        conditionalPanel.add(secondGeneBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        conditionalPanel.add(mycBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        profilerPanel.add(jToolBar2, BorderLayout.SOUTH);
        jToolBar2.add(printListBtn, null);
        jToolBar2.add(jButton15, null);
        jToolBar2.add(printGraphBtn, null);
        jToolBar2.add(jButton14, null);
        jToolBar2.add(progressBar, null);
        jPanel10.add(histogramGraph, BorderLayout.CENTER);
        jPanel10.add(jPanel6, BorderLayout.NORTH);
        jPanel6.add(thresholdIsMICheckBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        jPanel6.add(pvalueCheckBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        jPanel11.add(rankPlotChkBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        profilerSubPanel.add(jPanel12, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 2, 2, 2), 0, 0));
        jPanel12.add(jLabel4, BorderLayout.WEST);
        jPanel12.add(markerBox, BorderLayout.CENTER);
        profilerSubPanel.add(jToolBar1, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        jToolBar1.add(jButton2, null);
        jToolBar1.add(jButton3, null);
        jToolBar1.add(jButton12, null);
        jToolBar1.add(showAllMABox, null);
        jToolBar1.add(cytoscape, null);
        profilerSubPanel.add(jPanel11, new GridBagConstraints(2, 1, 1, 3, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0)); //ToolTipManager.sharedInstance().registerComponent(network);
        spinModel.setMinimum(new Integer(1));
        spinModel.setMaximum(new Integer(8));
        spinModel.setValue(new Integer(1));
        rankCriteriaGroup.add(jRadioButton1);
        rankCriteriaGroup.add(jRadioButton2);
        rankCriteriaGroup.add(jRadioButton3);
        rankCriteriaGroup.add(jRadioButton4);
        tabbedPanels.add(conditionalAnalysisPanel, "Conditional Analysis");
        tabbedPanels.add(geneNetworkPanel, "Network Browser");
        statisticsPanel.add(jPanel7, BorderLayout.CENTER);
        jPanel7.add(jLabel6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 35, 0));
        jPanel7.add(jLabel7, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(11, 2, 0, 0), 72, 0));
        jPanel7.add(jLabel8, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 2, 0, 0), 33, 0));
        jPanel7.add(jTextField2, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(8, 8, 0, 0), 335, 0));
        jPanel7.add(jTextField1, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 8, 0, 0), 308, 0));
        jPanel7.add(jTextField3, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(9, 8, 0, 0), 317, 0));
        jPanel7.add(jButton8, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(7, 6, 0, 3), 200, 0));
        jPanel7.add(backgroundGraph, new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 2, 3), 32, 31));
        jPanel7.add(jCheckBox1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 94, 0, 91), 0, 0));
        jPanel7.add(jLabel9, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 2, 0, 0), 57, 0));
        jPanel7.add(txtIterations, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(8, 8, 0, 0), 317, 0));
        jPanel7.add(jButton9, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 6, 0, 3), 174, 0));
        jPanel7.add(chkHistogram, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 20, 0, 32), 133, -1));
        tabbedPanels.add(functionPlotPanel, "Function Plot");
        jGenePopup.add(jMenuAdd2panel);
        jPanel11.add(jPanel10, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        jPanel11.add(graph1, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        markerBox.setText("");
        markerBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                markerBox_keyTyped(e);
            }
        });
    }

    public Component getComponent() {
        return geneNetworkPanel;
    }

    public void setMicroarraySet(DSMicroarraySet set) {
        if (set != null) {
            markerNo = set.size();
            reset();
        } else {
            markerNo = 0;
        }
    }

    void jButton1_actionPerformed(ActionEvent e) {
        // This function computes mutual information using subsets of the data going asymptotically
        // To the number of available data points
        int maNo = dataSetView.items().size();
        double[] ratios = {.33, .55, .75, .875, 1};

        info = new CSInformationTheory(dataSetView, -1, false, type);
        DSGeneMarker m1 = dataSetView.allMarkers().get(271);
        DSGeneMarker m2 = dataSetView.allMarkers().get(7457);

        for (int i = 0; i < ratios.length; i++) {
            for (int k = 0; k < 10; k++) {
                HashSet found = new HashSet();
                double ratio = ratios[i];
                int minMANo = (int) (ratio * (double) maNo);
                DSItemList<DSMicroarray> mArrays = new CSPanel<DSMicroarray>();
                int j = 0;
                while (j < minMANo) {
                    int id = (int) (Math.random() * (double) maNo);
                    Integer key = new Integer(id);
                    if (!found.contains(key)) {
                        mArrays.add(j++, dataSetView.items().get(id));
                        found.add(key);
                    }
                }
                double threshold = 0.0;
                double score = info.getScore(m1, m2);
                System.out.println("Score [" + ratio + "]:\t" + score);
            }
        }
    }

    private RankSorter[] getMycArrays() {
        RankSorter[] ranks = null;
        String secondGeneId = secondMarkerBox.getText();
        DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(secondGeneId, dataSetView);
        if (markers.size() > 0) {
            int maNo = dataSetView.items().size();
            ranks = new RankSorter[maNo];
            for (int i = 0; i < maNo; i++) {
                DSMicroarray ma = dataSetView.items().get(i);
                ranks[i] = new RankSorter();
                ranks[i].x = ma.getMarkerValue(markers.get(0).getSerial()).getValue();
                ranks[i].id = i;
            }
            Arrays.sort(ranks, RankSorter.SORT_X);
        }
        return ranks;
    }

    /**
     * procedure:
     * -> Analyze 2D -> calls "regression"
     */
    private void regression(DSItemList<DSMicroarray> mArrays) throws NumberFormatException {
        if (dataSetView.items().size() > 0) {
            String geneLabel = geneLabelBox.getText().toLowerCase();
            DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(geneLabel, dataSetView);
            for (int i = 0; i < markers.size(); i++) {
                sortAndShow(relatedMarkers(markers.get(i), mArrays));
            }
        }
    }

    private ArrayList relatedMarkers(DSGeneMarker m1, DSItemList<DSMicroarray> ma) {
        int secondGeneId = -1;
        ArrayList coefficients = new ArrayList();
        double threshold = getThreshold();
        if (secondGeneBox.isSelected() && (secondGeneBox.getText().length() > 0)) {
            String secondGeneLabel = secondMarkerBox.getText();
            DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(secondGeneLabel, dataSetView);
            for (int i = 0; i < markers.size(); i++) {
                DSGeneMarker m2 = markers.get(i);
                coefficients.addAll(info.regression(m1, m2, threshold));
            }
        } else {
            coefficients.addAll(info.regression(m1, null, threshold));
        }

        if (ma == null) {
            maNo = dataSetView.items().size();
        } else {
            maNo = ma.size();
        }
        return coefficients;
    }

    private void plotStatistics(int _maNo) throws SeriesException {
        maNo = _maNo;
        CSInformationTheory info = new CSInformationTheory(dataSetView, -1, true, type);
        if (info != null) {
            XYSeries dataset = new XYSeries("Signal");
            XYSeries backset = new XYSeries("Randomized");
            XYSeriesCollection plots = new XYSeriesCollection();
            double count1 = (double) markerNo;
            double count2 = (double) 1.000000000001;
            double y2;
            for (double x = 0; x < Math.min(info.getMaxFg() * 1.2, 1.0); x += info.getStep()) {
                count2 = PathwayDecoderUtil.getPValue(x, maNo);
                if (pvalueCheckBox.isSelected()) {
                    y2 = Math.log(count2) / Math.log(10);
                } else {
                    y2 = PathwayDecoderUtil.getP(x, maNo);
                }
            }
            plots.addSeries(dataset);
            plots.addSeries(backset);
            JFreeChart chart = ChartFactory.createXYLineChart(null, "Score", "Probability", plots, PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
            histogramGraph.setChart(chart);
        }
    }

    /**
     * add sorted coefficients to model
     * while model is linked to jList1 for display
     */
    private void sortAndShow(ArrayList coefficients) {
        model.clear();
        Collections.sort(coefficients);
        for (int i = 0; i < coefficients.size(); i++) {
            model.addElement(coefficients.get(i));
        }
    }

    void displayGraph(ChartPanel panel, GeneNetworkEdgeImpl rc) {
        JFreeChart chart = getChart(rc, panel.getWidth(), panel.getHeight());
        panel.setChart(chart);
    }

    private JFreeChart getChart(GeneNetworkEdgeImpl rc, int width, int height) throws SeriesException {
        StandardXYToolTipGenerator tooltips = new MyXYToolTip();
        StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES, tooltips);
        XYSeriesCollection plots = new XYSeriesCollection();
        ArrayList seriesList = new ArrayList();
        boolean showAll = showAllMABox.isSelected();
        DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet) dataSetView.getDataSet();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("temporary.files.directory") + "plot.txt"));
            boolean rankPlot = rankPlotChkBox.isSelected();
            HashMap map = new HashMap();
            HashSet set = new HashSet();
            int microarrayNo = maSet.size();
            // First put all the gene pairs in the xyValues array
            xyValues = new org.geworkbench.util.pathwaydecoder.RankSorter[microarrayNo];
            if (xyPoints == null) {
                xyPoints = new ArrayList();
            } else {
                xyPoints.clear();
            }
            for (int i = 0; i < microarrayNo; i++) {
                DSMicroarray ma = maSet.get(i);
                xyValues[i] = new RankSorter();
                xyValues[i].x = ma.getMarkerValue(rc.getId1()).getValue();
                xyValues[i].y = ma.getMarkerValue(rc.getMarker2().getSerial()).getValue();
                xyValues[i].id = i;
                map.put(new Integer(i), xyValues[i]);
            }
            // Now filter according to the filtering criteria
            if (filterBtn.isSelected()) {
                filterXYValues();
            }
            if (rankPlot && !showAll) {
                // Must first activate all valid points
                if ((dataSetView.getItemPanel().size() > 0) && (dataSetView.getItemPanel().getLabel().compareToIgnoreCase("Unsupervised") != 0) && (dataSetView.getItemPanel().panels().size() > 1)) {
                    for (int pId = 0; pId < dataSetView.getItemPanel().panels().size(); pId++) {
                        DSPanel<DSMicroarray> panel = dataSetView.getItemPanel().panels().get(pId);
                        int itemNo = panel.size();
                        if (itemNo > 0) {
                            for (int i = 0; i < itemNo; i++) {
                                int serial = panel.get(i).getSerial();
                                xyValues[serial].setActive(true);
                            }
                        }
                    }
                }
            }
            // Perform rank sorting if required
            int rank = 0;
            Arrays.sort(xyValues, RankSorter.SORT_Y);
            for (int j = 1; j < xyValues.length; j++) {
                if (xyValues[j].y == xyValues[j - 1].y) {
                    xyValues[j - 1].y += Math.random() * 0.1 - 0.05;
                }
            }
            Arrays.sort(xyValues, RankSorter.SORT_Y);
            for (int j = 0; j < xyValues.length; j++) {
                if (showAll || xyValues[j].isActive() || xyValues[j].isFiltered()) {
                    xyValues[j].iy = rank++;
                }
            }
            double maxY = xyValues[xyValues.length - 1].y;

            rank = 0;
            Arrays.sort(xyValues, RankSorter.SORT_X);
            for (int j = 1; j < xyValues.length; j++) {
                if (xyValues[j].x == xyValues[j - 1].x) {
                    xyValues[j - 1].x += Math.random() * 0.1 - 0.05;
                }
            }
            Arrays.sort(xyValues, RankSorter.SORT_X);
            for (int j = 0; j < xyValues.length; j++) {
                if (showAll || xyValues[j].isActive() || xyValues[j].isFiltered()) {
                    xyValues[j].ix = rank++;
                }
            }
            double maxX = xyValues[xyValues.length - 1].x;

            if (filterBtn.isSelected()) {
                ArrayList list = new ArrayList();
                XYSeries series = new XYSeries("Filtered");
                for (int serial = 0; serial < xyValues.length; serial++) {
                    if (xyValues[serial].isFiltered()) {
                        xyValues[serial].setPlotted();
                        list.add(xyValues[serial]);
                        double x = 0;
                        double y = 0;
                        if (rankPlot) {
                            x = xyValues[serial].ix;
                            y = xyValues[serial].iy;
                            series.add(x, y);
                        } else {
                            x = xyValues[serial].x;
                            y = xyValues[serial].y;
                            series.add(x, y);
                        }
                        writer.write(x + "\t" + y + "\n");
                    }
                }
                seriesList.add(series);
                xyPoints.add(list);
            }
            /**
             * If phenotypic panels have been selected
             */
            if ((dataSetView.getItemPanel().size() > 0) && (dataSetView.getItemPanel().getLabel().compareToIgnoreCase("Unsupervised") != 0) && (dataSetView.getItemPanel().panels().size() > 1)) {
                for (int pId = 0; pId < dataSetView.getItemPanel().panels().size(); pId++) {
                    ArrayList list = new ArrayList();
                    DSPanel<DSMicroarray> panel = dataSetView.getItemPanel().panels().get(pId);
                    int itemNo = panel.size();
                    if (panel.isActive() && itemNo > 0) {
                        writer.write(panel.getLabel());
                        writer.write("\n");
                        XYSeries series = new XYSeries(panel.getLabel());
                        for (int i = 0; i < itemNo; i++) {
                            int serial = panel.get(i).getSerial();
                            RankSorter xy = (RankSorter) map.get(new Integer(serial));
                            xy.setPlotted();
                            list.add(xy);
                            double x = 0;
                            double y = 0;
                            if (rankPlot) {
                                x = xy.ix;
                                y = xy.iy;
                                series.add(x, y);
                            } else {
                                x = xy.x;
                                y = xy.y;
                                series.add(x, y);
                            }
                            writer.write(x + "\t" + y + "\n");
                        }
                        seriesList.add(series);
                        Collections.sort(list, RankSorter.SORT_X);
                        xyPoints.add(list);
                    }
                }
            }
            /**
             * finally if all the others must be shown as well
             */
            if (showAll) {
                ArrayList list = new ArrayList();
                XYSeries series = new XYSeries("All/Other Experiments");
                writer.write("All others\n");
                for (int serial = 0; serial < xyValues.length; serial++) {
                    if (!xyValues[serial].isPlotted()) {
                        list.add(xyValues[serial]);
                        double x = 0;
                        double y = 0;
                        if (rankPlot) {
                            x = xyValues[serial].ix;
                            y = xyValues[serial].iy;
                            series.add(x, y);
                            writer.write(x + "\t" + y + "\n");
                        } else {
                            x = xyValues[serial].x;
                            y = xyValues[serial].y;
                            series.add(x, y);
                            writer.write(x + "\t" + y + "\n");
                        }
                    }
                }
                xyPoints.add(0, list);
                plots.addSeries(series);
            }
            for (int i = 0; i < seriesList.size(); i++) {
                XYSeries series = (XYSeries) seriesList.get(i);
                plots.addSeries(series);
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }
        String label1 = "";
        String label2 = "";
        try {

            label1 = AnnotationParser.getInfo(maSet.get(rc.getId1()).getLabel(), AnnotationParser.DESCRIPTION)[0];
            label2 = AnnotationParser.getInfo(rc.getMarker2().getLabel(), AnnotationParser.DESCRIPTION)[0];
        } catch (Exception ex1) {
            label1 = maSet.getMarkers().get(rc.getId1()).toString();
            label2 = maSet.getMarkers().get(rc.getMarker2().getSerial()).toString();
        }
        mainChart = ChartFactory.createScatterPlot("Motif Location Histogram", label1, label2, plots, PlotOrientation.VERTICAL, true, true, false); // Title, (, // X-Axis label,  Y-Axis label,  Dataset,  Show legend
        mainChart.getXYPlot().setRenderer(renderer);
        return mainChart;
    }

    void displayMycGraph(ChartPanel panel, GeneNetworkEdgeImpl rc) {
        boolean rankPlot = rankPlotChkBox.isSelected();
        XYSeriesCollection plots = new XYSeriesCollection();
        RankSorter[] ranks = null;
        RankSorter[] mycArrays = getMycArrays();
        int maNo = mycArrays.length / mycSeriesNo;
        ranks = new RankSorter[maNo];
        for (int i = 0; i < maNo; i++) {
            ranks[i] = new RankSorter();
        }
        for (int it = 0; it < mycSeriesNo; it++) {
            String[] seriesTitle = {"0", "1", "2", "3", "4", "5"};
            XYSeries series = new XYSeries(seriesTitle[it]);
            if (rankPlot) {
                for (int i = 0; i < maNo; i++) {
                    int maId = mycArrays[i + it * maNo].id;
                    DSMicroarray ma = dataSetView.getDataSet().get(maId);
                    ranks[i].x = ma.getMarkerValue(rc.getId1()).getValue();
                    ranks[i].y = ma.getMarkerValue(rc.getMarker2().getSerial()).getValue();
                }
                Arrays.sort(ranks, RankSorter.SORT_X);
                for (int j = 0; j < ranks.length; j++) {
                    ranks[j].ix = j;
                }
                Arrays.sort(ranks, RankSorter.SORT_Y);
                for (int j = 0; j < ranks.length; j++) {
                    ranks[j].iy = j;
                }
                for (int i = 0; i < maNo; i++) {
                    series.add((double) ranks[i].ix, (double) ranks[i].iy);
                }
            } else {
                for (int i = 0; i < maNo; i++) {
                    int maId = mycArrays[i + it * maNo].id;
                    DSMicroarray ma = dataSetView.getDataSet().get(maId);
                    series.add(ma.getMarkerValue(rc.getId1()).getValue(), ma.getMarkerValue(rc.getMarker2().getSerial()).getValue());
                }
            }
            plots.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createScatterPlot("Motif Location Histogram", dataSetView.getDataSet().get(rc.getId1()).toString(), dataSetView.getDataSet().get(rc.getMarker2().getSerial()).toString(), plots, PlotOrientation.VERTICAL, true, false, false); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
        panel.setChart(chart);
    }

    void jList1_mouseClicked(MouseEvent e) {

        if (e.isMetaDown()) {
            // System.out.println("right button clicked");
            // brings up the popup menu that sends event to BIND panel
            jGenePopup.show(e.getComponent(), e.getX(), e.getY());

        } else {
            // left button clicked
            int index = jList1.getSelectedIndex();
            if (index >= 0) {
                displaySelection1(index); // updates "motif location histogram"
            }
        }
    }

    /**
     * display the motif histogram???
     */
    private void displaySelection1(int index) {
        GeneNetworkEdgeImpl rc = (GeneNetworkEdgeImpl) model.get(index);
        if (mycBox.isSelected()) {
            displayMycGraph(graph1, rc);
        } else {
            displayGraph(graph1, rc);
        }
    }

    /**
     * Checks if the relationship between rc.id1 and rc.id2 is direct
     * or rather it occurs through another element of the list.
     *
     * @param relations the set of edges to explore
     * @param rc        the relation for which the optimal neighbor must be found
     * @return the optimal neighbor
     */
    private GeneNetworkEdge findBestNeighbor(Object[] relations, GeneNetworkEdgeImpl rc) {
        GeneNetworkEdge bestRC = rc;
        for (int i = 0; i < relations.length; i++) {
            /**
             * Checks all connections rc1 with higher affinity to see if rc could
             * occur as rc1 -> rc rather than directly
             */
            GeneNetworkEdgeImpl rc1 = (GeneNetworkEdgeImpl) relations[i];
            if (rc1 == rc) {
                /**
                 * Break because the list is score sorted so that all subsequent rcs will
                 * have worst score
                 */
                break;
            }
            if (rc1.getActive()) {
                //GeneNetworkEdgeImpl betterRC = ggr.getScore(rc.getMarker2().getSerial(), rc1.getMarker2().getSerial(), bestRC.getThreshold(), null);
                double score = info.getScore(rc.getMarker2(), rc1.getMarker2());
                if (score > bestRC.getMI()) {
                    /**
                     * rc1 -> rc is more likely than rc1 and rc
                     */
                    GeneNetworkEdgeImpl newRC = new GeneNetworkEdgeImpl();
                    newRC.setId1(rc.getMarker2().getSerial());
                    newRC.setMarker2(rc1.getMarker2());
                    newRC.setA(1);
                    newRC.setMI(score);
                    newRC.setPValue(PathwayDecoderUtil.getLogP(score, dataSetView.items().size()));
                    bestRC = newRC;
                }
            }
        }
        return bestRC;
    }

    void jButton2_actionPerformed(ActionEvent e) {
        /**
         * Analyze (2D)
         * This computes the correlation coefficient and linear regression parameters for each marker that
         * matches the specifications agains each other marker. However, rather than using the actual expression
         * values, these are replaced by the rank.
         */
        info = new CSInformationTheory(dataSetView, -1, false, type);
        DSItemList<DSMicroarray> mArrays = new CSItemList<DSMicroarray>();
        DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet) dataSetView.getDataSet();

        if (mycBox.isSelected()) {
            RankSorter[] ranks = getMycArrays();
            for (int maId = 0; maId < ranks.length / mycSeriesNo; maId++) {
                mArrays.add(maId, maSet.get(ranks[maId].id));
            }
        } else {
            if (filterBtn.isSelected()) {
                mArrays = getFilteredArrays();
            } else {

                boolean showAll = showAllMABox.isSelected() || (!filterBtn.isSelected() && ((dataSetView.getItemPanel().size() > 0) || (dataSetView.getItemPanel().panels().size() < 2) || (dataSetView.getItemPanel().getLabel().compareToIgnoreCase("Unsupervised") == 0)));
                if (!showAll) {

                    int maNo = dataSetView.items().size();
                    int maId = 0;
                    for (DSMicroarray ma : dataSetView.items()) {
                        mArrays.add(ma);
                    }
                }
            }
        }
        regression(mArrays); // so as to put something in model and display in jList1
    }

    @Subscribe public void receive(GeneSelectorEvent e, Object source) {
        DSGeneMarker marker = e.getGenericMarker(); // GeneselectorEvent can be a panel event therefore won't work here,
        if (marker != null) { //so added this check point--xuegong
            if (secondMarkerBox.hasFocus()) {
                secondMarkerBox.setText(marker.getLabel());
            } else {
                geneLabelBox.setText(marker.getLabel());
            }
            conditionalAnalysisPanel.geneSelected(marker);
        }
    }

    void jButton3_actionPerformed(ActionEvent e) {
        // Analyze (3D)
    }

    void rankPlotChkBox_actionPerformed(ActionEvent e) {
        int index = jList1.getSelectedIndex();
        if (index >= 0) {
            GeneNetworkEdgeImpl rc = (GeneNetworkEdgeImpl) model.get(index);
            displayGraph(graph1, rc);
        }
    }

    @Subscribe public void receive(PhenotypeSelectorEvent e, Object source) {
        dataSetView.setItemPanel((DSPanel) e.getTaggedItemSetTree());
        int index = jList1.getSelectedIndex();
        if (index >= 0) {
            displaySelection1(index);
        }
        geneNetworkPanel.receive(e, source);
    }

    void reduceMatrix() {
        // This function finds all edges (a,b) that can be better modeled through a
        // third node c as ((a,c)(c,b)), because the likelihood of (a,c) and (c,b)
        // is higher than that of (a,b)
        if (!(deserializing || serializing || reducing)) {
            final SwingWorker worker = new org.geworkbench.util.SwingWorker() {
                public Object construct() {
                    reducing = true;
                    try {
                        DSMicroarraySet maSet = (DSMicroarraySet) getDataSet();
                        matrix.reduce(maSet, progressBar);
                    } catch (Exception ex) {
                        System.out.println("Exception: " + ex);
                    } finally {
                        reducing = false;
                    }
                    return matrix;
                }
            };
            worker.start();
        }
    }

    /**
     * creating the network based on the selected gene subset
     */
    void jButton12_actionPerformed(ActionEvent e) {
        // the Create Network button
        String geneLabel = geneLabelBox.getText().toLowerCase();
        DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(geneLabel, dataSetView);

        if (markers != null) {
            for (int i = 0; i < markers.size(); i++) {
                if (model.getSize() > 0) {
                    //edges contain IGenericMarkers
                    Object[] edges = jList1.getSelectedValues();

                    if (edges.length <= 1) {

                        ArrayList selection = new ArrayList();

                        // what is this model???
                        for (int j = 0; j < Math.min(100, model.size()); j++) {
                            GeneNetworkEdgeImpl rc = (GeneNetworkEdgeImpl) model.get(j);
                            if (rc.getThreshold() < getThreshold()) {
                                // testing if the edge "strength" is above the threshold
                                break;
                            }
                            selection.add(rc);
                        }
                        edges = selection.toArray();
                    }

                    // if there's enough edges then create the network layout directly...
                    // System.out.println("do NetworkLayout.start()");
                    NetworkLayout nl = new NetworkLayout(geneNetworkPanel, markers.get(i), edges, getThreshold());
                    nl.execute();
                }
            }
        }
    }

    void showAllMABox_actionPerformed(ActionEvent e) {
        showAllMArrays(showAllMABox.isSelected());
        int index = jList1.getSelectedIndex();
        if (index >= 0) {
            displaySelection1(index);
        }
    }

    protected GeneProfiler getThis() {
        return this;
    }

    void jRadioButton1_actionPerformed(ActionEvent e) {
        type = GeneGeneRelationship.RANK_MI;
    }

    void jRadioButton2_actionPerformed(ActionEvent e) {
        type = GeneGeneRelationship.RANK_CHI2;
    }

    void jRadioButton3_actionPerformed(ActionEvent e) {
        type = GeneGeneRelationship.STD_REGRESSION;
    }

    void jRadioButton4_actionPerformed(ActionEvent e) {
        type = GeneGeneRelationship.PEARSON;
    }

    private DSItemList<DSMicroarray> getFilteredArrays() {
        DSItemList<DSMicroarray> microarrays = new CSItemList<DSMicroarray>();
        ArrayList<DSMicroarray> tmpRanks = new ArrayList<DSMicroarray>();
        String geneLabel = secondMarkerBox.getText().toLowerCase();
        double expFrom = 0.0;
        double expTo = 99999.0;
        try {
            expFrom = Double.parseDouble(fromExpBox.getText());
        } catch (NumberFormatException ex) {
            fromExpBox.setText(String.valueOf(expFrom));
        }
        try {
            expTo = Double.parseDouble(toExpBox.getText());
        } catch (NumberFormatException ex) {
            toExpBox.setText(String.valueOf(expTo));
        }
        if (geneLabel.length() > 0) {
            DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(geneLabel, dataSetView);
            for (int i = 0; i < dataSetView.items().size(); i++) {
                for (int markerId = 0; markerId < markers.size(); markerId++) {
                    DSMicroarray ma = dataSetView.items().get(i);
                    double signal = ma.getMarkerValue(markers.get(markerId).getSerial()).getValue();
                    if ((signal >= expFrom) && (signal <= expTo)) {
                        tmpRanks.add(ma);
                        break;
                    }
                }
            }
            for (int i = 0; i < tmpRanks.size(); i++) {
                microarrays.add(i, tmpRanks.get(i));
            }
        }
        return microarrays;
    }

    private void filterXYValues() {
        if (xyValues != null) {
            String geneLabel = secondMarkerBox.getText().toLowerCase();
            double expFrom = 0.0;
            double expTo = 99999.0;
            try {
                expFrom = Double.parseDouble(fromExpBox.getText());
            } catch (NumberFormatException ex) {
                fromExpBox.setText(String.valueOf(expFrom));
            }
            try {
                expTo = Double.parseDouble(toExpBox.getText());
            } catch (NumberFormatException ex) {
                toExpBox.setText(String.valueOf(expTo));
            }
            if (geneLabel.length() > 0) {
                DSDataSet<DSMicroarray> maSet = dataSetView.getDataSet();
                DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(geneLabel, dataSetView);
                for (int i = 0; i < xyValues.length; i++) {
                    int serial = xyValues[i].id;
                    DSMicroarray ma = maSet.get(serial);
                    double signal = ma.getMarkerValue(markers.get(0).getSerial()).getValue();
                    boolean filtered = ((signal >= expFrom) && (signal <= expTo));
                    xyValues[i].setFilter(filtered);
                }
            }
        }
    }
    /**
       * validifyConditions()
       * To fix bug 110. Check the text at secondMarkerBox is valid.
       *
       * At same time, show the valid range of expression value of selected gene.
       */
      public boolean validateConditions(int i, String secondGeneId) {

           DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(secondGeneId, dataSetView);
          if (markers != null && markers.size() > 0) {

              return true;
          }
          else {
              if (i==BASICPANEL){
                  geneLabelBox.setBackground(Color.red);
                  reportError("The gene: \"" + secondGeneId +
                              "\" does not exist!");
                  geneLabelBox.setBackground(Color.white);

              }else{


                  secondMarkerBox.setBackground(Color.red);
                  reportError("The condition gene: \"" + secondGeneId +
                              "\" does not exist!");
                  secondMarkerBox.setBackground(Color.white);
              }
          }
          return false;
      }

      void reportError(String errorMessage) {
          JOptionPane.showMessageDialog(null, errorMessage, "Error",
                                        JOptionPane.INFORMATION_MESSAGE);
      }


    void filterBtn_actionPerformed(ActionEvent e) {
          String geneLabel = secondMarkerBox.getText().trim();
          double minExp = 99999;
          double maxExp = 0;
          if (filterBtn.isSelected() && !validateConditions(CONDITIONALPANEL, geneLabel)) {

              return;
          }
          if(!filterBtn.isSelected()){
              fromExpBox.setText("0");
          toExpBox.setText( "0");

          return;

          }

          DSItemList<DSGeneMarker> markers = PathwayDecoderUtil.matchingMarkers(geneLabel, dataSetView);

          if(dataSetView==null || markers == null||markers.size()==0){
              return;
          }
//          System.out.println(microarrayVector.getTaggedItemNo());
//          System.out.println( markers.length + "marker.length");
          for (int i = 0; i < dataSetView.items().size(); i++) {
              for (int markerId = 0; markerId < markers.size(); markerId++) {
//                  int serial = microarrayVector.getTaggedItem(i).getSerial();
//                  IMicroarray ma = getMicroarraySet().getIMicroarray(serial);
//                  double signal = ma.getMarker(markers[markerId].getSerial()).
//                      getValue();

                  double signal = dataSetView.getValue(markers.get(markerId), i);
                  if (signal >= maxExp) {
                      maxExp = signal;
                  }
                  if (signal <= minExp) {
                      minExp = signal;
                  }

              }
          }

          maxExp = Math.ceil(maxExp);
          minExp = Math.floor(minExp);
          String displayMax =   new Double (maxExp).toString();
         // int length = displayMax.length()>6?5:displayMax.length();
         toExpBox.setText( displayMax );
            //toExpBox.setText( displayMax.substring(0, length));
            String displayMin = new Double (minExp).toString();
         // length = displayMin.length()>6?5:displayMax.length();
          fromExpBox.setText(displayMin);



          fromExpBox.setToolTipText("Min_Value = " + minExp);
          toExpBox.setToolTipText("Max_Value = " + maxExp);


    }




    void pvalueCheckBox_actionPerformed(ActionEvent e) {
        plotStatistics(maNo);
    }

    void printListBtn_actionPerformed(ActionEvent e) {
        double threshold = getThreshold();
        int[] selection = jList1.getSelectedIndices();
        boolean useSelection = false;
        DSGeneMarker gm = null;
        GeneNetworkEdgeImpl edge = null;
        // Get a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        // Ask user for page format (e.g., portrait/landscape)
        PageFormat pf = job.pageDialog(job.defaultPage());
        // Specify the Printable is an instance of
        // PrintListingPainter; also provide given PageFormat
        job.setPrintable(new PrintListingPainter(), pf);
        // Print 1 copy
        job.setCopies(1);
        // Put up the dialog box
        if (job.printDialog()) {
            // Print the job if the user didn't cancel printing
            try {
                job.print();
            } catch (Exception pe) {
                /* handle exception */
            }
        }
    }

    class PrintListingPainter implements Printable {
        private Font fnt = new Font("Helvetica", Font.PLAIN, 10);
        private int rememberedPageIndex = -1;
        private long rememberedFilePointer = -1;
        private boolean rememberedEOF = false;
        private int index = 0;
        private int lastIndex = 0;
        DSGeneMarker gm = null;
        GeneNetworkEdgeImpl edge = null;

        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            DecimalFormat format = new DecimalFormat("#.####");
            try {
                int[] selection = jList1.getSelectedIndices();
                boolean useSelection = (selection.length > 1);
                int itemNo = Math.min(model.getSize(), 500);
                if (useSelection) {
                    itemNo = selection.length;
                }
                double threshold = getThreshold();
                // For catching IOException
                if (pageIndex != rememberedPageIndex) {
                    // First time we've visited this page
                    rememberedPageIndex = pageIndex;
                    lastIndex = index;
                    // If encountered EOF on previous page, done
                    if (rememberedEOF) {
                        return Printable.NO_SUCH_PAGE;
                    }
                    // Save current position in input file
                } else {
                    index = lastIndex;
                }
                g.setColor(Color.black);
                g.setFont(fnt);
                int x = (int) pf.getImageableX() + 10;
                int y = (int) pf.getImageableY() + 12;
                // Generate as many lines as will fit in imageable area
                y += 36;
                while (y + 12 < pf.getImageableY() + pf.getImageableHeight()) {
                    if (index >= itemNo) {
                        rememberedEOF = true;
                        break;
                    }
                    if (useSelection) {
                        edge = (GeneNetworkEdgeImpl) model.get(selection[index]);
                    } else {
                        edge = (GeneNetworkEdgeImpl) model.get(index);
                    }
                    if (!useSelection && (edge.getThreshold() < threshold)) {
                        rememberedEOF = true;
                        break;
                    }
                    gm = edge.getMarker2();
                    String line = "[" + gm.getSerial() + "]";
                    g.drawString(line, x, y);
                    g.drawString(format.format(edge.getThreshold()), x + 45, y);
                    g.drawString(gm.getLabel(), x + 90, y);
                    g.drawString(gm.toString(), x + 150, y);
                    y += 12;
                    index++;
                }
                return Printable.PAGE_EXISTS;
            } catch (Exception e) {
                return Printable.NO_SUCH_PAGE;
            }
        }
    }

    class PrintGraphPainter implements Printable, ImageObserver {
        private Font fnt = new Font("Helvetica", Font.PLAIN, 10);
        private int rememberedPageIndex = -1;
        private long rememberedFilePointer = -1;
        private boolean rememberedEOF = false;
        private int gutter = 10;
        private int header = 12;
        GeneNetworkEdgeImpl edge = null;

        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            try {
                int[] selection = jList1.getSelectedIndices();
                boolean useSelection = (selection.length > 1);
                int itemNo = Math.min(model.getSize(), 500);
                if (useSelection) {
                    itemNo = selection.length;
                }
                double threshold = getThreshold();
                // For catching IOException
                if (pageIndex != rememberedPageIndex) {
                    // First time we've visited this page
                    rememberedPageIndex = pageIndex;
                    // If encountered EOF on previous page, done
                    if (rememberedEOF) {
                        return Printable.NO_SUCH_PAGE;
                    }
                    // Save current position in input file
                }
                int firstIndex = pageIndex * 6;
                g.setColor(Color.black);
                g.setFont(fnt);
                int x = (int) pf.getImageableX() + gutter;
                int y = (int) pf.getImageableY() + header;

                int width = (int) (pf.getImageableWidth() - gutter * 2) / 3;
                int heigh = (int) (pf.getImageableHeight() - header * 2) / 2;

                if (pageIndex > (itemNo - 1) / 6) {
                    rememberedEOF = true;
                } else {
                    for (int i = 0; i < 6; i++) {
                        int index = firstIndex + i;
                        if (index < itemNo) {
                            if (useSelection) {
                                edge = (GeneNetworkEdgeImpl) model.get(selection[index]);
                            } else {
                                edge = (GeneNetworkEdgeImpl) model.get(index);
                            }
                            if (!useSelection && (edge.getThreshold() < threshold)) {
                                rememberedEOF = true;
                            } else {
                                JFreeChart chart = getChart(edge, width * 3, heigh * 3);
                                BufferedImage image = chart.createBufferedImage(width * 3, heigh * 3);
                                int offsetX = (index % 3) * width;
                                int offsetY = ((index / 3) % 2) * heigh;
                                g.drawImage(image, x + offsetX, y + offsetY, width - gutter, heigh - header, Color.white, this);
                            }
                        } else {
                            rememberedEOF = true;
                            break;
                        }
                    }
                }
                return Printable.PAGE_EXISTS;
            } catch (Exception e) {
                return Printable.NO_SUCH_PAGE;
            }
        }
        public boolean imageUpdate(Image image, int int1, int int2, int int3, int int4, int int5) {
            return false;
        }
    }

    void printGraphBtn_actionPerformed(ActionEvent e) {
        // Get a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        // Ask user for page format (e.g., portrait/landscape)
        PageFormat pf = job.pageDialog(job.defaultPage());
        // Specify the Printable is an instance of
        // PrintListingPainter; also provide given PageFormat
        job.setPrintable(new PrintGraphPainter(), pf);
        // Print 1 copy
        job.setCopies(1);
        // Put up the dialog box
        if (job.printDialog()) {
            // Print the job if the user didn't cancel printing
            try {
                job.print();
            } catch (Exception pe) {
                /* handle exception */
            }
        }
    }

    void jButton14_actionPerformed(ActionEvent e) {
        /**
         * Saves the current graph as a JPEG file
         */
        if (mainChart != null) {
            JFileChooser chooser = new JFileChooser("chart.jpg");
            ExampleFilter filter = new ExampleFilter();
            filter.addExtension("jpg");
            filter.setDescription("JPEG Images");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    ChartUtilities.saveChartAsJPEG(chooser.getSelectedFile().getAbsoluteFile(), mainChart, 800, 500);
                } catch (IOException ex) {
                }
            }
        }
    }

    void jButton15_actionPerformed(ActionEvent e) {
        BufferedWriter writer;
        int[] selection = jList1.getSelectedIndices();
        boolean useSelection = (selection.length >= 1);
        DecimalFormat format = new DecimalFormat("#.####");
        DSGeneMarker gm = null;
        int itemNo = Math.min(model.getSize(), 500);
        GeneNetworkEdgeImpl edge = null;
        if (useSelection) {
            itemNo = selection.length;
        }
        double threshold = getThreshold();
        try {
            writer = new BufferedWriter(new FileWriter("genes.txt"));
            for (int index = 0; index < itemNo; index++) {
                if (useSelection) {
                    edge = (GeneNetworkEdgeImpl) model.get(selection[index]);
                } else {
                    edge = (GeneNetworkEdgeImpl) model.get(index);
                }
                if (edge.getThreshold() < threshold) {
                    break;
                }
                gm = edge.getMarker2();
                String shortName = "";
                shortName = gm.getShortName();
                String line = gm.getSerial() + "\t" + format.format(edge.getThreshold()) + "\t" + gm.getLabel() + "\t" + gm.getUnigene() + "\t" + gm.getGeneId() + "\t" + shortName + "\t" + gm.toString();
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }
    }

    private void writeEdgeList(BufferedWriter writer, String description, ArrayList list, GeneGeneRelationship ggr) throws IOException {
        writer.write(description);
        writer.newLine();
        for (int i = 0; i < list.size(); i++) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) list.get(i);
            writer.write(edge.getMI() + "\t" + edge.getPValue() + "\t" + edge.getMarker2().getLabel() + "\t" + edge.getMarker2().toString());
            writer.newLine();
        }
    }

    void graph_mouseClicked(ChartMouseEvent e) {
        ChartEntity x = e.getEntity();
        if (x instanceof XYItemEntity) {
            XYItemEntity xy = (XYItemEntity) x;
            int listId = xy.getSeriesIndex();
            int itemId = xy.getItem();
            ArrayList list = (ArrayList) xyPoints.get(listId);
            RankSorter rs = (RankSorter) list.get(itemId);
            DSMicroarray ma = dataSetView.getDataSet().get(rs.id);
            System.out.println("Entity: " + listId + ", Item: " + itemId + ", " + ma.getLabel());
        } else {
            System.out.println("Entity: " + x);
        }
    }

    void graph_mouseMoved(ChartMouseEvent e) {
        ChartEntity x = e.getEntity();
    }

    @Subscribe @Override public void receive(ProjectEvent projectEvent, Object source) {
        super.receive(projectEvent, source);
        ProjectSelection selection = ((ProjectPanel) source).getSelection();
        DSDataSet dataFile = selection.getDataSet();
        if (dataFile instanceof DSMicroarraySet) {
            DSMicroarraySet set = (DSMicroarraySet) dataFile;
            if (set != null) {
                reset();
            }
        }
    }

    @Script //loads AdjacencyMatrix from the filesystem, resolve 2nd parameter in read call
    public AdjacencyMatrix getAdjacencyMatrix(String filename) {
        AdjacencyMatrix am = new AdjacencyMatrix();
        am.read(filename, (DSMicroarraySet)dataSetView.getDataSet());
        return am;
    }
    
    private double getThreshold() {
        double threshold = Double.parseDouble(thresholdBox.getText());
        if (!GeneNetworkEdgeImpl.usePValue) {
            threshold = threshold / 100.0;
        }
        return threshold;
    }

    void parametersBox_actionPerformed(ActionEvent e) {

    }

    JButton printGraphBtn = new JButton();
    JButton jButton15 = new JButton();
    JButton jButton14 = new JButton();
    JToolBar jToolBar2 = new JToolBar();
    JButton printListBtn = new JButton();
    JCheckBox thresholdIsMICheckBox = new JCheckBox();
    JTextField markerBox = new JTextField();
    BorderLayout borderLayout7 = new BorderLayout();
    JTabbedPane jTabbedPane1 = new JTabbedPane();
    JPanel basicPanel = new JPanel();
    JPanel conditionalPanel = new JPanel();
    JPanel multiGenePanel = new JPanel();
    JLabel jLabel1 = new JLabel();
    JPanel jPanel9 = new JPanel();
    JLabel jLabel10 = new JLabel();
    JLabel jLabel11 = new JLabel();
    GridBagLayout gridBagLayout6 = new GridBagLayout();
    GridBagLayout gridBagLayout7 = new GridBagLayout();
    GridBagLayout gridBagLayout8 = new GridBagLayout();
    Component component1;
    JPanel jPanel10 = new JPanel();
    BorderLayout borderLayout8 = new BorderLayout();

    JToolBar jToolBar1 = new JToolBar();
    JPanel jPanel6 = new JPanel();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    JPanel jPanel11 = new JPanel();
    GridBagLayout gridBagLayout9 = new GridBagLayout();
    JPanel jPanel12 = new JPanel();
    BorderLayout borderLayout9 = new BorderLayout();
    org.geworkbench.util.function.FunctionPlotPanel functionPlotPanel = new org.geworkbench.util.function.FunctionPlotPanel();
    JCheckBox chkHistogram = new JCheckBox();
    GridBagLayout gridBagLayout5 = new GridBagLayout();
    JCheckBox cytoscape = new JCheckBox();
    JMenuItem jMenuAdd2panel = new JMenuItem();

    void jButton8_actionPerformed(ActionEvent e) {
        int numIterations = Integer.parseInt(txtIterations.getText());
        double sigma = Double.parseDouble(jTextField2.getText());
        int numSamples = Integer.parseInt(jTextField3.getText());

        info = new CSInformationTheory(dataSetView, sigma, false, type);
        info.computeBackground(numIterations, numSamples, GeneGeneRelationship.RANK_CHI2, GeneGeneRelationship.BG_RANDOM);

        double alpha = .4;
        double beta = .01;
        plotStatisticsNew(numSamples);
    }

    void jButton9_actionPerformed(ActionEvent e) {
        int numSamples = Integer.parseInt(jTextField3.getText());
        plotStatisticsNew(numSamples);
    }

    private double getBeta(int maNo, double beta) {
        try {
            beta = Double.parseDouble(jTextField2.getText());
        } catch (NumberFormatException ex1) {
            beta = 6.8;
            jTextField2.setText("6.8");
        }
        beta = beta * (double) maNo;
        return beta;
    }

    private double getAlpha(double alpha, int maNo) {
        try {
            alpha = Double.parseDouble(jTextField1.getText());
        } catch (NumberFormatException ex) {
            alpha = 0.004;
            jTextField1.setText("0.004");
        }
        alpha = 1.0 / (0.139456 + Math.sqrt((double) maNo) * alpha);
        return alpha;
    }

    private int getMaNo() {
        int maNo;
        try {
            maNo = Integer.parseInt(jTextField3.getText());
        } catch (NumberFormatException ex) {
            maNo = 100;
            jTextField3.setText("100");
        }
        return maNo;
    }

    void plotStatisticsNew(int maNo) {
        double log10 = Math.log(10);

        XYSeries fgd = new XYSeries("Foreground");
        XYSeries histogram = new XYSeries("Histogram");
        XYSeriesCollection plots = new XYSeriesCollection();

        double maxX = .5;

        if (info != null) {
            double tot = 1.0;
            for (int bin = 0; bin < maxX / info.getStep(); bin++) {
                double y = info.getFg(bin);
                tot -= y; // / 1000.0;
                if (jCheckBox1.isSelected()) {
                    if (tot > 0) {
                        fgd.add(bin * info.getStep(), Math.log(tot) / log10);
                    }
                } else {
                    fgd.add(bin * info.getStep(), tot);
                    histogram.add(bin * info.getStep(), y);
                }
            }
            plots.addSeries(fgd);
            plots.addSeries(histogram);
            JFreeChart chart = ChartFactory.createXYLineChart(null, "Mutual Information Threshold", "PValue", plots, PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
            backgroundGraph.setChart(chart);
        }
    }

    private void plotStatistics(double alpha, double beta, int maNo) throws SeriesException {
        double log10 = Math.log(10);
        XYSeries bkg = new XYSeries("Background");
        XYSeries fgd = new XYSeries("Foreground");
        XYSeriesCollection plots = new XYSeriesCollection();
        BetaDistribution bd = new BetaDistribution();
        bd.setParameters(alpha, beta);
        double maxX = 0.0;
        if (info != null) {
            for (double x = 0; x < 1; x += info.getStep()) {
                double y = PathwayDecoderUtil.getPValue(x, maNo); //1.0 - bd.getCDF(x); //
                if (y < 1E-10) {
                    break;
                }
                if (jCheckBox1.isSelected()) {
                    if (y != 0) {
                        bkg.add(x, Math.log(y) / log10);
                    }
                } else {
                    bkg.add(x, y);
                }
                maxX = x;
            }
            double tot = 1.0;
            for (int bin = 0; bin < maxX / info.getStep(); bin++) {
                double y = info.getFg(bin);
                tot -= y; // / 1000.0;
                if (jCheckBox1.isSelected()) {
                    if (tot > 0) {
                        fgd.add(bin * info.getStep(), Math.log(tot) / log10);
                    }
                } else {
                    fgd.add(bin * info.getStep(), tot);
                }
            }
            plots.addSeries(fgd);
            JFreeChart chart = ChartFactory.createXYLineChart(null, "Mutual Information Threshold", "PValue", plots, PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
            backgroundGraph.setChart(chart);
        }
    }

    public void reset() {
        if (dataSetView.items() != null && dataSetView.getDataSet() != null) {
            DSMicroarraySet set = (DSMicroarraySet) dataSetView.getDataSet();
            conditionalAnalysisPanel.set(set, this, dataSetView.items());
            geneNetworkPanel.set(set, this, dataSetView.items());
        }
    }

    void thresholdIsMICheckBox_actionPerformed(ActionEvent e) {
        if (thresholdIsMICheckBox.isSelected()) {
            jLabel2.setText("Mutual Info Thresh.");
        } else {
            jLabel2.setText("PValue Threshold");
        }
        GeneNetworkEdgeImpl.setUsePValue(!thresholdIsMICheckBox.isSelected());
        jList1.invalidate();
        jList1.repaint();
    }

    void markerBox_keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        boolean ctl = e.isControlDown();
        if (e.isControlDown() && (e.getKeyChar() == '\u000E')) {
            findNext(1, c);
        } else if (e.isControlDown() && (e.getKeyChar() == '\u0002')) {
            findNext(-1, c);
        } else {
            findNext(0, c);
        }
    }

    private void findNext(int offset, char c) {
        int index = jList1.getSelectedIndex();
        boolean useLC = false;
        if (index < 0) {
            jList1.setSelectedIndex(0);
            index = 0;
        }
        String markerString = markerBox.getText().toLowerCase();
        String lcMarkerString = markerString.toLowerCase();
        if (lcMarkerString.equals(markerString)) {
            useLC = true;
        }
        if (Character.isLetterOrDigit(c)) {
            markerString += Character.toLowerCase(c);
        }
        int geneNo = jList1.getModel().getSize();
        for (int idx = Math.abs(offset); idx < geneNo; idx++) {
            int i = 0;
            if (offset < 0) {
                i = (index + geneNo - idx) % geneNo;
            } else {
                i = (index + idx) % geneNo;
            }
            Object o = jList1.getModel().getElementAt(i);
            if (o instanceof GeneNetworkEdgeImpl) {
                GeneNetworkEdgeImpl gne = (GeneNetworkEdgeImpl) o;
                String name = gne.toString();
                if (useLC) {
                    name = name.toLowerCase();
                }
                if (name.indexOf(markerString) >= 0) {
                    jList1.setSelectedIndex(i);
                    jList1.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    void cytoscape_actionPerformed(ActionEvent e) {
        // System.out.println("cytoscape_actionPerformed ");// + e.getSource());
        if (e.getSource() == cytoscape) {
            // just to syncronize the two buttons
            geneNetworkPanel.cytoscapeBtn.setSelected(cytoscape.isSelected());
        } else if (e.getSource() == geneNetworkPanel.cytoscapeBtn) {
            cytoscape.setSelected(geneNetworkPanel.cytoscapeBtn.isSelected());
        }
    }

    void jMenuAdd2panel_actionPerformed(ActionEvent e) {
        // System.out.println("adding to subselection panel");
        String tmpSetLabel = JOptionPane.showInputDialog("Panel Set Label:", "Reverse Engineering");
        String tmpLabel = JOptionPane.showInputDialog("Set Label:", "");
        if (tmpSetLabel.equals("") || tmpSetLabel == null) {
            tmpSetLabel = "Reverse Engineering";
        }
        if (tmpLabel.equals("") || tmpLabel == null) {
            tmpLabel = "Selected Genes";
        }

        DSPanel<DSGeneMarker> selectedMarkers = new CSPanel<DSGeneMarker>(tmpLabel, tmpSetLabel);
        int[] indices = jList1.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            selectedMarkers.add(((GeneNetworkEdgeImpl) model.getElementAt(i)).getMarker2());
        }
        selectedMarkers.setActive(true);
        publishSubpanelChangedEvent(new SubpanelChangedEvent(DSGeneMarker.class, selectedMarkers, SubpanelChangedEvent.SET_CONTENTS));
    }

    @Publish public ProjectNodeAddedEvent publishProjectNodeAddedEvent(ProjectNodeAddedEvent event) {
        return event;
    }

    @Publish public org.geworkbench.events.AdjacencyMatrixEvent publishAdjacencyMatrixEvent(org.geworkbench.events.AdjacencyMatrixEvent event) {
        return event;
    }

    @Publish public GeneSelectorEvent publishGeneSelectorEvent(GeneSelectorEvent event) {
        return event;
    }
}
