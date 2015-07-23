package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.util.pathwaydecoder.RankSorter;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GenePairPlot extends JPanel {
    public class MyXYToolTip extends StandardXYToolTipGenerator {
        public String generateToolTip(XYDataset data, int series, int item) {
            ArrayList list = (ArrayList) xyPoints.get(series);
            RankSorter rs = (RankSorter) list.get(item);
            DSMicroarraySet<DSMicroarray> maSet = getMicroarraySet();
            if (maSet != null) {
                DSAnnotationContext context = CSAnnotationContextManager.getInstance().getCurrentContext(maSet);
                DSMicroarray ma = maSet.get(rs.id);
                String[] values = context.getLabelsForItem(ma);
                if (values.length > 0) {
                    return ma.getLabel() + ": " + values[0] + " [" + rs.x + "," + rs.y + "]";
                } else {
                    return ma.getLabel() + ": Unknown " + " [" + rs.x + "," + rs.y + "]";
                }
            } else {
                System.out.println("Must load a microarray set before drawing a network");
            }
            return "Unknown";
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

    public GenePairPlot(DSMicroarraySet _maSet) {
        maSet = _maSet;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BorderLayout borderLayout1 = new BorderLayout();
    private JFreeChart mainChart = null;
    private ChartPanel graph1 = null;
    private JPanel panel = new JPanel();
    private DSMicroarraySet maSet = null;
    private ArrayList xyPoints = null;
    private RankSorter[] xyValues = null;
    private boolean filter = false;
    private DSPanel<DSMicroarray> selectedPanel = null;

    private DSMicroarraySet<DSMicroarray> getMicroarraySet() {
        return maSet;
    }

    BorderLayout borderLayout2 = new BorderLayout();
    JToolBar jToolBar1 = new JToolBar();
    JCheckBox showAllMABox = new JCheckBox();
    JCheckBox rankPlotChkBox = new JCheckBox();

    private void jbInit() throws Exception {
        mainChart = ChartFactory.createXYLineChart(null, "Score", "Probability", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, true, true); // Title,  X-Axis label,  Y-Axis label,  Dataset,  Show legend
        graph1 = new ChartPanel(mainChart);
        graph1.setPreferredSize(new Dimension(200, 4));
        graph1.setMinimumSize(new Dimension(200, 200));
        graph1.setMaximumSize(new Dimension(2000, 1000));
        graph1.setDoubleBuffered(false);
        graph1.setDebugGraphicsOptions(0);
        graph1.setBorder(BorderFactory.createEtchedBorder());
        graph1.setBackground(UIManager.getColor("OptionPane.warningDialog.titlePane.background"));
        this.setLayout(borderLayout1);
        graph1.addChartMouseListener(new MyChartMouseListener());
        panel.setLayout(borderLayout2);
        panel.setMinimumSize(new Dimension(200, 200));
        showAllMABox.setText("jCheckBox1");
        rankPlotChkBox.setText("Rank Order");
        this.add(panel, BorderLayout.CENTER);
        panel.add(graph1, BorderLayout.CENTER);
        panel.add(jToolBar1, BorderLayout.NORTH);
        jToolBar1.add(showAllMABox, null);
        jToolBar1.add(rankPlotChkBox, null);
    }

    void graph_mouseClicked(ChartMouseEvent e) {
        //    ChartEntity x = e.getEntity();
        //    if (x instanceof XYItemEntity) {
        //      XYItemEntity xy = (XYItemEntity) x;
        //      int listId = xy.getSeries();
        //      int itemId = xy.getItem();
        //      ArrayList list = (ArrayList) xyPoints.get(listId);
        //      RankSorter rs = (RankSorter) list.get(itemId);
        //      IMicroarray ma = getMicroarraySet().getIMicroarray(rs.id);
        //      /*
        //             ArrayList  list = (ArrayList)xyPoints.get(xy.getSeries());
        //             RankSorter rs   = (RankSorter)list.get(xy.getItem());
        //             IMicroarray ma = getMicroarraySet().getIMicroarray(rs.id);
        //             PhenoProperty ph = getMicroarraySet().getClassificationManager().getSelectedPhenoProperty();
        //             String result = ma.getLabel() + ": " + ma.getPropertyValue(ph) + " [" + rs.x + "," + rs.y + "]";
        //       */
        //      System.out.println("Entity: " + listId + ", Item: " + itemId + ", " +
        //                         ma.getLabel());
        //    }
        //    else {
        //      System.out.println("Entity: " + x);
        //    }
    }

    void graph_mouseMoved(ChartMouseEvent e) {
        //    ChartEntity x = e.getEntity();
        //    //System.out.println("Entity: " + x);
    }

    public void drawChart(int id1, int id2) throws SeriesException {
        StandardXYToolTipGenerator tooltips = new MyXYToolTip();
        StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES, tooltips);
        XYSeriesCollection plots = new XYSeriesCollection();
        ArrayList seriesList = new ArrayList();
        boolean showAll = showAllMABox.isSelected() || (!filter && ((selectedPanel == null) || (selectedPanel.panels().size() < 2) || (selectedPanel.getLabel().compareToIgnoreCase("Unsupervised") == 0)));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("temporary.files.directory") + "plot.txt"));
            boolean rankPlot = rankPlotChkBox.isSelected();
            HashMap map = new HashMap();
            HashSet set = new HashSet();
            int microarrayNo = getMicroarraySet().size();
            // First put all the gene pairs in the xyValues array
            xyValues = new RankSorter[microarrayNo];
            if (xyPoints == null) {
                xyPoints = new ArrayList();
            } else {
                xyPoints.clear();
            }
            for (int i = 0; i < microarrayNo; i++) {
                DSMicroarray ma = getMicroarraySet().get(i);
                xyValues[i] = new org.geworkbench.util.pathwaydecoder.RankSorter();
                xyValues[i].x = ma.getMarkerValue(id1).getValue();
                xyValues[i].y = ma.getMarkerValue(id2).getValue();
                xyValues[i].id = i;
                map.put(new Integer(i), xyValues[i]);
            }
            // Now filter according to the filtering criteria
            if (filter) {
                filterXYValues();
            }
            if (rankPlot && !showAll) {
                // Must first activate all valid points
                if ((selectedPanel != null) && (selectedPanel.getLabel().compareToIgnoreCase("Unsupervised") != 0) && (selectedPanel.panels().size() > 1)) {
                    for (int pId = 0; pId < selectedPanel.panels().size(); pId++) {
                        DSPanel<DSMicroarray> panel = selectedPanel.panels().get(pId);
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
            for (int j = 0; j < xyValues.length; j++) {
                if (showAll || xyValues[j].isActive() || xyValues[j].isFiltered()) {
                    xyValues[j].iy = rank++;
                }
            }
            double maxY = xyValues[xyValues.length - 1].y;

            rank = 0;
            Arrays.sort(xyValues, RankSorter.SORT_X);
            for (int j = 0; j < xyValues.length; j++) {
                if (showAll || xyValues[j].isActive() || xyValues[j].isFiltered()) {
                    xyValues[j].ix = rank++;
                }
            }
            double maxX = xyValues[xyValues.length - 1].x;

            if (filter) {
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
                            //if ( (x < 4000) && (y < 4000)) {
                            x = xyValues[serial].x;
                            y = xyValues[serial].y;
                            series.add(x, y);
                            //}
                        }
                        writer.write(x + "\t" + y + "\n");
                    }
                }
                // plots.addSeries(series);
                seriesList.add(series);
                xyPoints.add(list);
            }
            /**
             * If phenotypic panels have been selected
             */
            if ((selectedPanel != null) && (selectedPanel.getLabel().compareToIgnoreCase("Unsupervised") != 0) && (selectedPanel.panels().size() > 1)) {
                for (int pId = 0; pId < selectedPanel.panels().size(); pId++) {
                    ArrayList list = new ArrayList();
                    DSPanel<DSMicroarray> panel = selectedPanel.panels().get(pId);
                    int itemNo = panel.size();
                    if (itemNo > 0) {
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
                                //if ( (x < 4000) && (y < 4000)) {
                                x = xy.x;
                                y = xy.y;
                                series.add(x, y);
                                //}
                            }
                            writer.write(x + "\t" + y + "\n");
                        }
                        //plots.addSeries(series);
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
                            //if ( (x < 4000) && (y < 4000)) {
                            x = xyValues[serial].x;
                            y = xyValues[serial].y;
                            series.add(x, y);
                            writer.write(x + "\t" + y + "\n");
                            //}
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

            label1 = AnnotationParser.getInfo(getMicroarraySet().getMarkers().get(id1).getLabel(), AnnotationParser.DESCRIPTION)[0];
            label2 = AnnotationParser.getInfo(getMicroarraySet().getMarkers().get(id2).getLabel(), AnnotationParser.DESCRIPTION)[0];
        } catch (Exception ex1) {
            label1 = getMicroarraySet().getMarkers().get(id1).toString();
            label2 = getMicroarraySet().getMarkers().get(id2).toString();
        }
        mainChart = ChartFactory.createScatterPlot("Motif Location Histogram", label1, label2, plots, PlotOrientation.VERTICAL, true, true, false); // Title, (, // X-Axis label,  Y-Axis label,  Dataset,  Show legend
        mainChart.getXYPlot().setRenderer(renderer);
        graph1.setChart(mainChart);
    }

    private void filterXYValues() {
        if (xyValues != null) {
            //      String geneLabel = secondMarkerBox.getText().toLowerCase();
            //      double expFrom = 0.0;
            //      double expTo = 99999.0;
            //      try {
            //        expFrom = Double.parseDouble(fromExpBox.getText());
            //      }
            //      catch (NumberFormatException ex) {
            //        fromExpBox.setText(String.valueOf(expFrom));
            //      }
            //      try {
            //        expTo = Double.parseDouble(toExpBox.getText());
            //      }
            //      catch (NumberFormatException ex) {
            //        toExpBox.setText(String.valueOf(expTo));
            //      }
            //      if (geneLabel.length() > 0) {
            //        IMarkerInfo[] markers = matchingMarkers(geneLabel);
            //        for (int i = 0; i < xyValues.length; i++) {
            //          int serial = xyValues[i].id;
            //          IMicroarray ma = getMicroarraySet().getIMicroarray(serial);
            //          double signal = ma.getIMarker(markers[0].getMarkerId()).getSignal();
            //          boolean filtered = ( (signal >= expFrom) && (signal <= expTo));
            //          xyValues[i].setFilter(filtered);
            //        }
            //      }
        }
    }
}
