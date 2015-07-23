package org.geworkbench.components.microarrays;

/*
 * The geworkbench project
 *
 * Copyright (c) 2006 Columbia University
 *
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.events.MarkerSelectedEvent;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.events.PhenotypeSelectedEvent;
import org.geworkbench.util.BusySwingWorker;
import org.geworkbench.util.microarrayutils.MicroarrayViewEventBase;
import org.jfree.chart.*;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 * Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis
 *
 * @author Adam Margolin
 * @see MenuListener, VisualPlugin
 */
@AcceptTypes({DSMicroarraySet.class})
public class ExpressionProfilePanel extends MicroarrayViewEventBase implements
        MenuListener, VisualPlugin {

    Log log = LogFactory.getLog(this.getClass());

    private JPanel graphPanel;
    private ChartPanel graph;
    private ChartPanel chartPanel;
    private JFreeChart chart;

    public ExpressionProfilePanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     */
    protected void jbInit() throws Exception {

        super.jbInit();
        graphPanel = new JPanel(new BorderLayout());
        chart = ChartFactory.createXYLineChart(null, // Title
                "Experiment", // X-Axis label
                "Value", // Y-Axis label
                new XYSeriesCollection(), // Dataset
                PlotOrientation.VERTICAL, false, // Show legend
                true, true);
        graph = new ChartPanel(chart, true);
        graphPanel.add(graph, BorderLayout.CENTER);
        mainPanel.add(graphPanel, BorderLayout.CENTER);

        plotButton
                .addActionListener(new ExpressionProfilePanel_plotButton_actionAdapter(
                        this));
        jToolBar3.add(plotButton);
        jToolBar3.add(numMarkersSelectedLabel);

        chkAllMarkers.setSelected(true);
        this.onlyActivatedMarkers = false;

        chkAllArrays.setSelected(true);
        this.onlyActivatedArrays = false;
    }

    /**
     * @param event
     */
    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {

        log.debug("Event is " + event);

        if (maSetView == null)
            return;

        if (isPlotRefresh()) {

            graphPanel.removeAll();

            setPlotRefresh(false);

            BusySwingWorker worker = new BusySwingWorker() {
                public Object construct() {
                    setBusy(graphPanel);
                    DSPanel<DSGeneMarker> genes = new CSPanel<DSGeneMarker>("");
                    genes.addAll(maSetView.markers());
                    DSPanel<DSMicroarray> arrays = new CSPanel<DSMicroarray>("");
                    arrays.addAll(maSetView.items());
                    XYSeriesCollection plots = new XYSeriesCollection();
                    int numGenes = (genes.size() > 500) ? 500 : genes.size();
                    for (int geneCtr = 0; geneCtr < numGenes; geneCtr++) {
                        XYSeries dataSeries = new XYSeries(genes.get(geneCtr)
                                .getLabel());
                        for (int maCtr = 0; maCtr < arrays.size(); maCtr++) {
                            double value = arrays.get(maCtr).getMarkerValue(
                                    genes.get(geneCtr)).getValue();
                            if (Double.isNaN(value)) {
                                dataSeries.add(maCtr, 0);
                            } else {
                                dataSeries.add(maCtr, value);
                            }
                        }
                        plots.addSeries(dataSeries);
                    }
                    StandardXYItemRenderer renderer = new StandardXYItemRenderer(
                            StandardXYItemRenderer.LINES,
                            new ExpressionXYToolTip());

                    JFreeChart ch = ChartFactory.createXYLineChart(null, // Title
                            "Experiment", // X-Axis label
                            "Value", // Y-Axis label
                            plots, // Dataset
                            PlotOrientation.VERTICAL, false, // Show legend
                            true, true);

                    ch.getXYPlot().setRenderer(renderer);

                    chartPanel = new ChartPanel(ch);
                    return null;

                }

                public void finished() {
                    graphPanel.removeAll();
                    chartPanel
                            .addChartMouseListener(new MicroarrayChartMouseListener());
                    graphPanel.add(chartPanel, BorderLayout.CENTER);
                    graphPanel.revalidate();

                    graphPanel.repaint();

                }

            };

            worker.start();
        }
    }

    /**
     * @return
     */
    public JPanel getGraphPanel() {
        assert graphPanel != null : "Null widget a " + graphPanel;

        return graphPanel;
    }

    /**
     * @return
     */
    public ChartPanel getGraph() {
        assert graphPanel != null : "Null widget a " + graph;

        return graph;
    }

    /**
     * @return
     */
    public JFreeChart getChart() {
        assert graphPanel != null : "Null widget a " + chart;

        return chart;
    }

    /**
     * @return
     */
    public JFreeChart getChartPanel() {
        assert graphPanel != null : "Null widget a " + chart;

        return chart;
    }

    /**
     * Responsible for handling marker selection in a microarray scatter plot.
     *
     * @author unattributable
     */
    private class MicroarrayChartMouseListener implements ChartMouseListener {

        public void chartMouseClicked(ChartMouseEvent event) {
            ChartEntity entity = event.getEntity();
            if ((entity != null) && (entity instanceof XYItemEntity)) {
                XYItemEntity xyEntity = (XYItemEntity) entity;
                int series = xyEntity.getSeriesIndex();
                int item = xyEntity.getItem();
                DSGeneMarker marker = maSetView.markers().get(series);
                if (marker != null) {
                    MarkerSelectedEvent mse = new org.geworkbench.events.MarkerSelectedEvent(
                            marker);
                    publishMarkerSelectedEvent(mse);
                }
                DSMicroarray array = maSetView.items().get(item);
                if (array != null) {
                    PhenotypeSelectedEvent pse = new PhenotypeSelectedEvent(
                            array);
                    publishPhenotypeSelectedEvent(pse);
                }
            }
        }

        public void chartMouseMoved(ChartMouseEvent event) {
            // No-op
        }
    }

    /**
     * Tool-tip renderer for gene charts.
     *
     * @author unattributable
     */
    private class ExpressionXYToolTip extends StandardXYToolTipGenerator {

        /**
         * @param data
         * @param series
         * @param item
         * @return String
         */
        public String generateToolTip(XYDataset data, int series, int item) {

            if (!isPlotRefresh())
                return null;

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);

            DSGeneMarker marker = maSetView.markers().get(series);
            DSMicroarray array = maSetView.items().get(item);
            String tooltip = "";
            if (marker != null) {
                tooltip += "Marker: " + marker.getLabel();
            }
            if (array != null) {
                tooltip += " Array: " + array.getLabel();
            }
            return tooltip;
        }
    }

    /**
     * @param var
     * @return ActionListener
     */
    public ActionListener getActionListener(String var) {
        return null;
    }

    /**
     * @param e
     */
    void plotButton_actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(plotButton.getText())) {
            setPlotRefresh(true);
            refreshMaSetView();
        }

    }

    @Publish
    public org.geworkbench.events.MarkerSelectedEvent publishMarkerSelectedEvent(
            MarkerSelectedEvent event) {
        return event;
    }

    @Publish
    public PhenotypeSelectedEvent publishPhenotypeSelectedEvent(
            PhenotypeSelectedEvent event) {
        return event;
    }

}

/**
 * @author keshav
 */
class ExpressionProfilePanel_plotButton_actionAdapter implements
        java.awt.event.ActionListener {

    private Log log = LogFactory.getLog(this.getClass());

    ExpressionProfilePanel adaptee;

    ExpressionProfilePanel_plotButton_actionAdapter(
            ExpressionProfilePanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        log.debug("actionPerformed " + e);

        adaptee.plotButton_actionPerformed(e);
        adaptee.getComponent().repaint();
    }
}