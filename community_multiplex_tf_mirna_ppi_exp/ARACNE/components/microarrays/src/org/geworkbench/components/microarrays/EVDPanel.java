package org.geworkbench.components.microarrays;


import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.model.analysis.AlgorithmExecutionResults;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.microarrayutils.MicroarrayViewEventBase;
import org.geworkbench.util.visualproperties.PanelVisualProperties;
import org.geworkbench.util.visualproperties.PanelVisualPropertiesManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Xiaoqing Zhang
 * @version 3.0
 */
@AcceptTypes({DSMicroarraySet.class}) public class EVDPanel extends MicroarrayViewEventBase implements MenuListener {

    private JFreeChart chart;
    public static int EVDMODE = 1;
    public static int TTESTMODE = -1;
    private double lowValue = 0.0d;
    private double highValue = 0.0d;
    private double maxValue = 0.0d;
    private double minValue = 0.0d;

    private Vector dataInBins;
    private JButton jPrintBttn = new JButton();
    ArrayList<PanelVisualProperties> propertiesList = new ArrayList<
            PanelVisualProperties>();
    HistogramPanel hs;
    JButton jAddBttn = new JButton();
    JToggleButton jTTestBttn;

    JPanel markerSelectionPanel;
    JSlider jLeftBoundarySlider;
    JSlider jRightBoundarySlider;
    JSlider jBinSizeSlider;
    JLabel jBinSizeLabel = new JLabel("100");
    JCheckBox colorCheck = new JCheckBox("One color per array");
    JSlider jDirectionSlider;
    JTextField leftBoundary;
    JTextField rightBoundary;
    JLabel selectedGeneNum;
    JLabel binNumLabel;
    public final static Shape baseShape = new Rectangle( -6, -6, 6, 6);
    public final static Color baseColor = Color.ORANGE;
    JSlider jMASlider;
    Component jSpacer;
    JToggleButton jEnabledBox;
    private static int DEFAULTBASKETNUM = 99;
    private int basketNum = DEFAULTBASKETNUM;
    private double binSize = 0.1d;
    private boolean isTTest;
    private boolean isToolTipEnabled = true;
    private boolean isColorChecked = false;
    private JButton imageSnapshotButton;
    private AlgorithmExecutionResults results = null;
    private DecimalFormat myFormatter = new DecimalFormat("0.000");
    private static int MAXBINNUM = 200;
    private static int RANGE = 2; //the left and right edge size beyound the min/max values.
    ChartPanel graph;

    public EVDPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void jbInit() throws Exception {
        super.jbInit();
        setupGUI();
        jLeftBoundarySlider.setToolTipText(
                "Move the slider to change the lower boundary of selected genes expression.");
        markerSelectionPanel.setBorder(BorderFactory.createLineBorder(Color.
                black));
        colorCheck.addActionListener(new EVDPanel_colorCheck_actionAdapter(this));

    }

    /**
     * Add more components to the GUI.
     */

    public void setupGUI() {

        colorCheck = new JCheckBox("One color per array");
        jBinSizeLabel = new JLabel("Total bins: 100");
        jPrintBttn = new JButton();
        jPrintBttn.setText("Print");
        jPrintBttn.setToolTipText("Print The Chart.");
        jPrintBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                org.geworkbench.util.PrintUtils.printComponent(graph);
            }
        });

        jTTestBttn = new JToggleButton("T-Test");
        jTTestBttn.setToolTipText("Two-sample TTest Only.");
        jTTestBttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jTTestBttn_actionPerformed(e);
            }
        });
        jAddBttn = new JButton();
        jAddBttn.setText("Add to Set ");
        jAddBttn.setToolTipText("Add to Set.");
        jAddBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jAddBttn_actionPerformed(e);
            }
        });

        jEnabledBox = new JToggleButton();
        jEnabledBox.setIcon(new ImageIcon(this.getClass().getResource(
                "bulb_icon_grey.gif")));
        jEnabledBox.setSelectedIcon(new ImageIcon(this.getClass().getResource(
                "bulb_icon_gold.gif")));
        jEnabledBox.setSelected(true);
        jEnabledBox.setToolTipText(
                "Push down to view above graph details with mouse moveover");
        jEnabledBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isToolTipEnabled = jEnabledBox.isSelected();
                refresh();
            }
        });

        jMASlider = new JSlider();
        jMASlider.setValue(0);
        jMASlider.setMaximum(1);
        jMASlider.setMinimum(0);
        jMASlider.setSnapToTicks(true);
        jMASlider.setPaintTicks(true);
        jMASlider.setMinorTickSpacing(1);
        jMASlider.setMajorTickSpacing(5);
        jMASlider.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.
                HAND_CURSOR));
        jMASlider.setToolTipText("Move the slider to change base microarray");
        jMASlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chipSlider_stateChanged(e);
            }
        });
        jLeftBoundarySlider = new JSlider();
        jLeftBoundarySlider.setValue(0);

        jLeftBoundarySlider.setSnapToTicks(true);
        jLeftBoundarySlider.setPaintTicks(true);
        jLeftBoundarySlider.setMinorTickSpacing(1);
        jLeftBoundarySlider.setMajorTickSpacing(5);
        jLeftBoundarySlider.setCursor(java.awt.Cursor.getPredefinedCursor(java.
                awt.Cursor.HAND_CURSOR));
        jLeftBoundarySlider.addChangeListener(new javax.swing.event.
                                              ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jLeftBoundarySlider_stateChanged(e);
            }
        });
        jLeftBoundarySlider.setToolTipText(
                "Move the slider to change the low boundary of selected genes");

        jRightBoundarySlider = new JSlider();
        jRightBoundarySlider.setValue(0);

        jRightBoundarySlider.setSnapToTicks(true);
        jRightBoundarySlider.setPaintTicks(true);
        jRightBoundarySlider.setMinorTickSpacing(1);
        jRightBoundarySlider.setMajorTickSpacing(5);
        jRightBoundarySlider.setCursor(java.awt.Cursor.getPredefinedCursor(java.
                awt.Cursor.HAND_CURSOR));
        jRightBoundarySlider.setToolTipText(
                "Move the slider to change the upper boundary of selected genes");
        jRightBoundarySlider.addChangeListener(new javax.swing.event.
                                               ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jRightBoundarySlider_stateChanged(e);
            }
        });

        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        jBinSizeSlider = new JSlider(SwingConstants.VERTICAL, 0, MAXBINNUM, 100);
        jBinSizeSlider.setSnapToTicks(true);
        jBinSizeSlider.setPaintTicks(true);
        jBinSizeSlider.setMinorTickSpacing(1);
        jBinSizeSlider.setMajorTickSpacing(5);
        jBinSizeSlider.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.
                Cursor.HAND_CURSOR));
        jBinSizeSlider.setToolTipText("Move the slider to change the bin size");
        jBinSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                jBinSlider_stateChanged(e);
            }
        });
        controlPanel.add(jBinSizeSlider);
        leftBoundary = new JTextField("0.00");
        rightBoundary = new JTextField("0.00");
        selectedGeneNum = new JLabel("Selected genes: 0");
        binNumLabel = new JLabel("Bin size:   0.1");
        markerSelectionPanel = new JPanel();
        JPanel leftTopPanel = new JPanel();
        leftTopPanel.setLayout(new GridLayout(2, 1));
        JPanel middleTopPanel = new JPanel(new GridLayout(2, 3));
        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel();

        leftTopPanel.add(binNumLabel);
        leftTopPanel.add(jBinSizeLabel);
        buttonPanel.add(jAddBttn);
        middleTopPanel.add(buttonPanel);
        middleTopPanel.add(new JLabel("Select values from: "));
        middleTopPanel.add(leftBoundary);
        // middleTopPanel.add(colorCheck);
        middleTopPanel.add(selectedGeneNum);
        middleTopPanel.add(new JLabel("Select values to: "));
        middleTopPanel.add(rightBoundary);
        rightTopPanel.add(jLeftBoundarySlider);
        rightTopPanel.add(jRightBoundarySlider);
        //        leftTopPanel.setBorder(BorderFactory.createLineBorder(Color.
        //                black));
        markerSelectionPanel.setLayout(new BoxLayout(markerSelectionPanel,
                BoxLayout.X_AXIS));
        markerSelectionPanel.add(leftTopPanel);
        markerSelectionPanel.add(middleTopPanel);
        markerSelectionPanel.add(rightTopPanel);
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPane.setOneTouchExpandable(true);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(markerSelectionPanel, BorderLayout.NORTH);
        imageSnapshotButton = new JButton("Image Snapshot");
        imageSnapshotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createImageSnapshot();
            }
        });
        jToolBar3.add(colorCheck);
        jToolBar3.add(jMASlider);
        jToolBar3.add(jEnabledBox);
        jToolBar3.add(jTTestBttn);
        jToolBar3.add(jPrintBttn);
        jToolBar3.add(imageSnapshotButton);

        chart = ChartFactory.createXYLineChart("EVD", "Value", "Occurrences", null,
                                               PlotOrientation.VERTICAL, true,
                                               isToolTipEnabled, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend, show ToolTips

        graph = new ChartPanel(chart, true);
        XYPlot newPlot = (XYPlot) chart.getPlot();
        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) newPlot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        mainPanel.add(graph, BorderLayout.CENTER);
        chkAllMarkers.setSelected(true);
        this.onlyActivatedMarkers = false;
        chkAllArrays.setSelected(true);
        this.onlyActivatedArrays = false;

    }

    void jLeftBoundarySlider_stateChanged(ChangeEvent e) {
        int value = jLeftBoundarySlider.getValue();
        double maxValue = jLeftBoundarySlider.getMaximum();
        XYPlot plot = this.chart.getXYPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        Range range = domainAxis.getRange();
        //        double c = domainAxis.getLowerBound()
        //                   + (value / maxValue) * range.getLength();
        lowValue = minValue + value * binSize;
        plot.setDomainCrosshairValue(lowValue - 0.5 * binSize);
        String s = myFormatter.format(lowValue);
        leftBoundary.setText(s);
        if (hs != null) {
            selectedGeneNum.setText("Selected genes: " +
                                    hs.getGeneNumbers(lowValue, highValue));
        }

    }

    void jRightBoundarySlider_stateChanged(ChangeEvent e) {
        int value = jRightBoundarySlider.getValue();
        XYPlot plot = this.chart.getXYPlot();
        double maxValue = jRightBoundarySlider.getMaximum();
        ValueAxis domainAxis = plot.getDomainAxis();
        Range range = domainAxis.getRange();
        //        double c = domainAxis.getLowerBound()
        //                   + (value / maxValue) * range.getLength();
        highValue = minValue + value * binSize;
        plot.setDomainCrosshairValue(highValue - 0.5 * binSize);
        String highStr = myFormatter.format(highValue); //String.format("Bin size: %1$.2d", new Double(binSize));
        rightBoundary.setText(highStr);
        if (hs != null) {
            selectedGeneNum.setText("Selected genes: " +
                                    hs.getGeneNumbers(lowValue, highValue));
        }

    }


    void chipSlider_stateChanged(ChangeEvent e) {
        refresh();
    }

    void jBinSlider_stateChanged(ChangeEvent e) {
        basketNum = MAXBINNUM - jBinSizeSlider.getValue();
        jBinSizeLabel.setText("Total bins: " + (basketNum + 1));
        if (basketNum == 0) {
            binNumLabel.setText("Bin size: unlimited");
        } else {
            binSize = (maxValue - minValue) / basketNum;
            String bin = myFormatter.format(binSize);
            binNumLabel.setText("Bin size:  " + bin);
        }

        jLeftBoundarySlider.setMaximum(basketNum + 1);
        jLeftBoundarySlider.setValue(0);
        jRightBoundarySlider.setMaximum(basketNum + 1);
        jRightBoundarySlider.setValue(0);
        refresh();
    }

    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {
        super.fireModelChangedEvent(event);
        if(refMASet == null){
            // mainPanel.remove(graph);

            chart = ChartFactory.createXYLineChart("EVD", "Value", "Occurrences", null,
                                              PlotOrientation.VERTICAL, true,
                                              isToolTipEnabled, true);
                                      graph.setChart(chart);
            //graph = new ChartPanel(chart, true);
             //mainPanel.add(graph, BorderLayout.CENTER);


        }
        refresh();
    }

    @Publish public org.geworkbench.events.ImageSnapshotEvent
            createImageSnapshot() {
        Dimension panelSize = graph.getSize();
        BufferedImage image = new BufferedImage(panelSize.width,
                                                panelSize.height,
                                                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        graph.paint(g);
        ImageIcon icon = new ImageIcon(image, "EVD Plot");
        org.geworkbench.events.ImageSnapshotEvent event = new org.geworkbench.
                events.ImageSnapshotEvent("EVD Snapshot", icon,
                                          org.geworkbench.events.
                                          ImageSnapshotEvent.Action.SAVE);
        return event;
    }

    protected void createNewChart(final int selectedArrayId,
                                  final boolean showActive) {

        double max[] = getBoundaryValue();
        drawPlot(createCollection(max[0], max[1], selectedArrayId, showActive),
                 "EVD");

    }

    /**
     * Following the user case, the boundary is set up from all microarrayset not only activated set.
     * So it will not change between different base array.
     *
     * @return double[]
     */

    public double[] getBoundaryValue() {
        double max = 0.0d;
        double min = 0.0d;
        DSMicroarraySet<DSMicroarray>
                maSet = (DSMicroarraySet) maSetView.getDataSet();
        if (maSet != null && maSetView.markers() != null) {
            int numGenes = maSetView.markers().size();
            for (int geneCtr = 0; geneCtr < numGenes; geneCtr++) {
                for (int maCtr = 0; maCtr < maSet.size(); maCtr++) {
                    double value = maSet.getValue(geneCtr, maCtr);
                    if (Double.isNaN(value)) {
                        value = 0;
                    } else if (value > max) {
                        max = value;
                    } else if (value < min) {
                        min = value;
                    }
                }
            }
        }
        double values[] = {min, max};
        lowValue = highValue = min;
        leftBoundary.setText(myFormatter.format(min));
        rightBoundary.setText(myFormatter.format(min));
        jLeftBoundarySlider.setValue(0);
        jRightBoundarySlider.setValue(0);
        return values;
    }

    public XYSeriesCollection createCollection(double min, double max,
                                               double[] tValues,
                                               DSItemList<DSGeneMarker> item) {
        int[] basketValues = new int[basketNum + 1];
        maxValue = max;
        minValue = min;
        binSize = (maxValue - minValue) / basketNum;
        String bin = myFormatter.format(binSize);
        binNumLabel.setText("Bin size:  " + bin);
        XYSeriesCollection plots = new XYSeriesCollection();
        if (tValues == null) {
            return null;
        }
        hs = new HistogramPanel(basketNum);
        hs.process(min, max, tValues, item);
        basketValues = hs.getBasketvalues();
        XYSeries dataSeries = new XYSeries("Two-Sample TTest");
        if (basketNum == 0) {
            dataSeries.add(minValue, basketValues[0]);
        } else {
            for (int i = 0; i <= basketNum; i++) {

                //only add value > 0. is it right?
                if (basketValues[i] > 0) {
                    dataSeries.add(i * ((maxValue - minValue) / basketNum) +
                                   minValue, basketValues[i]);

                }

            }

        }

        plots.addSeries(dataSeries);
        return plots;

    }


    public XYSeriesCollection createCollection(double min, double max,
                                               int selectedId, boolean active) {
        PanelVisualPropertiesManager propertiesManager =
                PanelVisualPropertiesManager.getInstance();
        //Temp changed. Need move into a new method to update.
        maxValue = max;
        minValue = min;
        binSize = (maxValue - minValue) / basketNum;
        String bin = myFormatter.format(binSize);
        binNumLabel.setText("Bin size:  " + bin);
        XYSeriesCollection plots = new XYSeriesCollection();
        DSMicroarraySet<DSMicroarray>
                maSet = (DSMicroarraySet) maSetView.getDataSet();
        if (maSet == null) {
            return null;
        }
        try {

            //draw base array.
            DSMicroarray ma = maSet.get(selectedId);
            XYSeries dataSeries = new XYSeries(ma.getLabel() + "(base)");
            int[] basketValues = new int[basketNum + 1];
            for (int i = 0; i < basketNum; i++) {
                basketValues[i] = 0;
            }
            if (maSetView.markers() == null) {
                return null;
            }
            int numGenes = maSetView.markers().size();
            hs = new HistogramPanel(basketNum);
            hs.process(ma, minValue, maxValue);

            basketValues = hs.getBasketvalues();

            //get rid of 0s. Not inside but on boundary
            //Should not remove 0s. need more care review.
            int maxIndex = basketNum;
            int minIndex = 0;
            if (basketValues == null) {
                return null;
            }
            if (basketNum == 0) {
                dataSeries.add(minValue, basketValues[0]);
            } else {
                //            for (int zeroSpot = basketNum; zeroSpot > 0; zeroSpot--) {
                //                if (basketValues[zeroSpot] > 0) {
                //                    maxIndex = zeroSpot;
                //                    break;
                //
                //                }
                //
                //            }
                //            for (int zeroSpot = 0; zeroSpot <= maxIndex; zeroSpot++) {
                //                if (basketValues[zeroSpot] > 0) {
                //                    minIndex = zeroSpot;
                //                    break;
                //
                //                }

                //            }

                for (int i = minIndex; i <= maxIndex; i++) {

                    if (basketValues[i] > 0) {
                        dataSeries.add(i * ((maxValue - minValue) / basketNum) +
                                       minValue, basketValues[i]);

                    }

                }
            }
            plots.addSeries(dataSeries);
            //clear the old propertiesList.
            propertiesList.clear();

            //drew active phenotpye data.

            int panelIndex = 0;
            if (active && maSetView.size() > 0) {
                for (int pId = 0; pId < maSetView.getItemPanel().panels().size();
                               pId++) {
                    DSPanel<DSMicroarray>
                            panel = maSetView.getItemPanel().panels().get(pId);
                    PanelVisualProperties properties = propertiesManager.
                            getVisualProperties(panel);
                    panelIndex++;
                    if (properties == null) {
                        properties = propertiesManager.
                                     getDefaultVisualProperties(panelIndex);
                    }

                    int itemNo = panel.size();
                    if (itemNo > 0) {
                        for (int k = 0; k < itemNo; k++) {
                            int serial = panel.get(k).getSerial();

                            DSMicroarray currentMicroarray = panel.get(k);
                            propertiesList.add(properties);

                            hs = new HistogramPanel(basketNum);
                            hs.process(currentMicroarray, minValue, maxValue);

                        basketValues = hs.getBasketvalues();

//                            basketValues = new int[basketNum + 1];
//                            for (int i = 0; i < basketNum; i++) {
//                                basketValues[i] = 0;
//                            }
                            dataSeries = new XYSeries(currentMicroarray.
                                    getLabel());

//                            numGenes = maSetView.markers().size();
//                            for (int geneCtr = 0; geneCtr < numGenes; geneCtr++) {
//                                DSGeneMarker marker = maSetView.markers().get(
//                                        geneCtr);
//
//                                double value = currentMicroarray.getMarkerValue(
//                                        marker).getValue();
//
//                                if (Double.isNaN(value)) {
//                                    value = 0;
//                                } else {
//                                    int count = (int) ((value - minValue) /
//                                            ((maxValue - minValue) / basketNum));
//                                    if (count >= 0 && count <= basketNum) {
//                                        basketValues[(int) ((value - minValue) /
//                                                ((maxValue - minValue) /
//                                                 basketNum))]++;
//                                    } else {
//                                        System.err.println(value + " " +
//                                                geneCtr +
//                                                maSetView.markers().get(geneCtr).
//                                                getLabel());
//                                    }
//
//                                }
//
//                            }

                            maxIndex = basketNum;
                            minIndex = 0;
                            if (basketNum == 0) {
                                dataSeries.add(minValue, basketValues[0]);
                            } else {
                                //
                                //                            for (int zeroSpot = basketNum; zeroSpot > 0;
                                //                                                zeroSpot--) {
                                //                                if (basketValues[zeroSpot] > 0) {
                                //                                    maxIndex = zeroSpot;
                                //                                    break;
                                //
                                //                                }
                                //
                                //                            }
                                //                            for (int zeroSpot = 0; zeroSpot <= maxIndex;
                                //                                                zeroSpot++) {
                                //                                if (basketValues[zeroSpot] > 0) {
                                //                                    minIndex = zeroSpot;
                                //                                    break;
                                //
                                //                                }
                                //
                                //                            }

                                for (int i = minIndex; i <= maxIndex; i++) {
                                    if (basketValues[i] > 0) {
                                        dataSeries.add(i *
                                                ((maxValue - minValue) /
                                                 basketNum) + minValue,
                                                basketValues[i]);

                                    }

                                }

                                plots.addSeries(dataSeries);

                            }
                        }
                    }
                }

            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return plots;
    }

    public void drawPlot(final XYSeriesCollection plots, String title) {
        if (plots == null) {
            return;
        }
        chart = ChartFactory.createXYLineChart(title, "Value", "Occurrences",
                                               plots, PlotOrientation.VERTICAL, true,
                                               isToolTipEnabled, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        XYPlot newPlot = (XYPlot) chart.getPlot();
        Color c = UIManager.getColor("Panel.background");
        if (c != null) {
            newPlot.setBackgroundPaint(c);
        } else {
            c = Color.white;
        }
        newPlot.setBackgroundPaint(c);
        newPlot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        newPlot.setDomainGridlinePaint(Color.white);
        newPlot.setRangeGridlinePaint(Color.white);
        newPlot.setDomainCrosshairVisible(true);
        newPlot.setDomainCrosshairLockedOnData(true);
        //Set up fixed ranges.
        //        ValueAxis xaxis = new NumberAxis();
        //        xaxis.setRange(minValue, maxValue);
        //        newPlot.setRangeAxis(xaxis);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) newPlot.
                                          getRenderer();
        renderer.setShapesVisible(true);
        renderer.setShapesFilled(true);
        if (isToolTipEnabled) {

            renderer.setToolTipGenerator(new XYToolTipGenerator() {


                public String generateToolTip(XYDataset dataset, int series,
                                              int item) {
                    String resultStr = "";
                    Object[] result = new Object[3];
                    String label = (String) (plots.getSeries(series).getKey());
                    double x = dataset.getXValue(series, item);
                    if (Double.isNaN(x) && dataset.getX(series, item) == null) {
                        return resultStr;
                    }

                    double y = dataset.getYValue(series, item);
                    if (Double.isNaN(y) && dataset.getX(series, item) == null) {
                        return resultStr;
                    }
                    String xStr = myFormatter.format(x);

                    return resultStr = label + ": ([" + xStr + ", " +
                                       myFormatter.format(x + binSize) + "), " +
                                       (int) y + ")";
                }
            });
        }
        renderer.setSeriesLinesVisible(0, false);
        for (int i = 1; i < newPlot.getDatasetCount(); i++) {
            renderer.setSeriesLinesVisible(i, true);
        }

        //base color & shape
        // renderer.setSeriesPaint(0, baseColor);
        //        renderer.setSeriesShape(0, baseShape);

        if (!isColorChecked) {

            for (int i = 0; i < propertiesList.size(); i++) {
                PanelVisualProperties panelVisualProperties = propertiesList.
                        get(i);
                // Note: "i+1" because we did not define base.
                int index = i + 1;
                renderer.setSeriesPaint(index, panelVisualProperties.getColor());
                renderer.setSeriesShape(index, panelVisualProperties.getShape());

            }

        }

        newPlot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) newPlot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        ValueAxis xAxis = newPlot.getDomainAxis();

        //     xAxis.setAutoRange(false);
        //       xAxis.setRange(dateRange);
        if (minValue < maxValue) {
            xAxis.setRange(minValue - RANGE * binSize,
                           maxValue + RANGE * binSize);
        }

        // OPTIONAL CUSTOMISATION COMPLETED.


        graph.setChart(chart);


    }

//    /**
//     * receiveProjectSelection
//     *
//     * @param e ProjectEvent
//     */
//    @Subscribe public void receive(ProjectEvent e, Object source) {
//
//        super.receive(e, source);
//        jMASlider.setMaximum(refMASet != null ? refMASet.size() - 1 : 1);
//
//    }


    public ActionListener getActionListener(String var) {
        return null;
    }

    private void reset() {

    }

    private void jAddBttn_actionPerformed(ActionEvent e) {
        if (lowValue > highValue) {
            double temp = highValue;
            highValue = lowValue;
            lowValue = temp;
        }
        DSPanel panel = hs.getPanel(lowValue, highValue);
        if (panel != null) {
            publishSubpanelChangedEvent(new SubpanelChangedEvent(DSGeneMarker.class, panel, org.geworkbench.events.SubpanelChangedEvent.NEW));
        } else {
            JOptionPane.showMessageDialog(null, "No gene is selected",
                                          "Please check",
                                          JOptionPane.ERROR_MESSAGE);
        }

    }

    public void refresh(int mode) {

        if (mode == EVDMODE) {
            jMASlider.setMaximum(refMASet != null ? refMASet.size() - 1 : 1);
            jMASlider.setEnabled(true);
            if (refMASet == null) {
                chart = ChartFactory.createXYLineChart("EVD", "Value",
                        "Occurrences", null, PlotOrientation.VERTICAL, true,
                        isToolTipEnabled, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend, show ToolTips

                graph.setChart(chart);
                mainPanel.repaint();

                return;
            }

            int mArrayId = jMASlider.getValue();
            if (mArrayId >= 0) {
                createNewChart(mArrayId, onlyActivatedArrays);
                  mainPanel.repaint();
            }

        } else if (mode == TTESTMODE) {
            jMASlider.setEnabled(false);
            doTTest();

        }

    }

    public void refresh() {
        int mode = jTTestBttn.isSelected() ? -1 : 1;
        refresh(mode);
    }

    /**
     * doTTest
     */
    public void doTTest() {
        SimpleTTest simpleTTest = new SimpleTTest();
        //  int num = maSetView
        double values[] = simpleTTest.execute(maSetView, onlyActivatedMarkers);
        double minT = simpleTTest.getMinT();
        double maxT = simpleTTest.getMaxT();
        DSItemList<DSGeneMarker> item = simpleTTest.getItem();
        drawPlot(createCollection(minT, maxT, values, item), "T-Test");
    }

    private void jTTestBttn_actionPerformed(ActionEvent e) {

        refresh();

    }

    /**
     * Check whether allow one color per array.
     *
     * @param e ActionEvent
     */
    public void colorCheck_actionPerformed(ActionEvent e) {
        isColorChecked = colorCheck.isSelected();
        refresh();
    }

    /**
     * Inner class to represent histogram.
     */
    private class HistogramPanel {
        private int nbins; // number of bins.

        int[] basketValues; //the bin values
        ArrayList al[]; //assicated gene markers.
        private double maxValue;
        private double minValue;

        public HistogramPanel(int size) {
            nbins = size;
            clear();

        }

        /**
         * clear
         */
        private void clear() {
            al = new ArrayList[nbins + 1];
            for (int i = 0; i < nbins + 1; i++) {
                al[i] = new ArrayList();
            }
            basketValues = new int[nbins + 1];

        }

        /**
         * process data to create histogram. Result is saved at basketValues.
         *
         * @param min     double
         * @param max     double
         * @param tValues double[]
         * @param item    DSItemList
         */
        public void process(double min, double max, double[] tValues,
                            DSItemList<DSGeneMarker> item) {
            maxValue = max;
            minValue = min;
            for (int geneCtr = 0; geneCtr < tValues.length; geneCtr++) {

                double value = tValues[geneCtr];
                DSGeneMarker marker = item.get(geneCtr);
                if (Double.isNaN(value)) {
                    value = 0;
                } else {

                    int count = (int) ((value - minValue) /
                                       ((maxValue - minValue) / nbins));
                    if (count >= 0 && count <= nbins) {
                        basketValues[count]++;
                        al[count].add(marker);
                    } else {
                        System.err.println(value + " " + geneCtr +
                                           maSetView.markers().get(geneCtr).
                                           getLabel());
                    }

                }

            }

        }

        public void process(DSMicroarray ma, double min, double max) {

            maxValue = max;
            minValue = min;

            for (int i = 0; i < nbins + 1; i++) {
                basketValues[i] = 0;
            }

            DSPanel<DSGeneMarker> genes = new CSPanel<DSGeneMarker>("");
            genes.addAll(maSetView.markers());

            int numGenes = genes.size();
            for (int geneCtr = 0; geneCtr < numGenes; geneCtr++) {

                DSGeneMarker marker = genes.get(geneCtr);

                double value = ma.getMarkerValue(marker).getValue();
                if (Double.isNaN(value) || value < minValue || value > maxValue) {
                    value = 0;
                } else {
                    int column = (int) ((value - minValue) /
                                        ((maxValue - minValue) / nbins));
                    if (column >= 0 && column < nbins + 1) {
                        basketValues[column]++;
                        al[column].add(marker);

                    } else {
                        System.err.println("IN  process " + marker + value +
                                           "Column=" + column);

                    }

                }
            }

        }

        public String get(int binNumber) {
            return al[binNumber].size() + " " + basketValues[binNumber];
        }

        public int[] getBasketvalues() {
            return basketValues;
        }

        private int getBinPosition(double cuValue) {
            if (cuValue > maxValue) {
                return nbins + 1;
            }
            if (cuValue < minValue) {
                return 0;
            }

            return (int) Math.round((cuValue - minValue) /
                                    ((maxValue - minValue) / nbins));
        }

        DSPanel getPanel(double leftValue, double rightValue) {
            int leftBin = getBinPosition(leftValue);
            int rightBin = getBinPosition(rightValue);
            DSPanel<DSGeneMarker>
                    panel = new CSPanel<DSGeneMarker>("Selected from EVD");
            if (leftBin < 0 || rightBin < 0) {

                return null;
            } else {
                for (int i = leftBin; i < rightBin; i++) {
                    for (int k = 0; k < al[i].size(); k++) {
                        panel.add((DSGeneMarker) (al[i].get(k)));
                    }
                }
            }
            return panel;
        }

        int getGeneNumbers(double leftValue, double rightValue) {
            int leftBin = getBinPosition(leftValue);
            int rightBin = getBinPosition(rightValue);
            int total = 0;
            if (leftBin < 0 || rightBin < 0) {

                return 0;
            } else {
                for (int i = leftBin; i < rightBin; i++) {
                    total += al[i].size();
                }
            }
            return total;

        }
    }


}


class EVDPanel_colorCheck_actionAdapter implements ActionListener {
    private EVDPanel adaptee;

    EVDPanel_colorCheck_actionAdapter(EVDPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.colorCheck_actionPerformed(e);
    }

}
