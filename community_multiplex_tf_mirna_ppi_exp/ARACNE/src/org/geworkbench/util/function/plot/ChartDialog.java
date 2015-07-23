package org.geworkbench.util.function.plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class ChartDialog extends JDialog {
    private JFreeChart chartFunction = ChartFactory.createXYLineChart(null, // Title
            "Score", // X-Axis label
            "Probability", // Y-Axis label
            new XYSeriesCollection(), // Dataset
            PlotOrientation.VERTICAL, false, // Show legend
            true, true);

    ChartPanel jPanel2 = new ChartPanel(chartFunction);

    public ChartDialog() {
    }
}
