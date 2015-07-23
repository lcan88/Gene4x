package org.geworkbench.util.function;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FunctionPlotPanel extends javax.swing.JPanel {
    private JFreeChart chartFunction = ChartFactory.createXYLineChart(null, // Title
            "Score", // X-Axis label
            "Probability", // Y-Axis label
            new XYSeriesCollection(), // Dataset
            PlotOrientation.VERTICAL, false, // Show legend
            true, true);

    ChartPanel jPanel2 = new ChartPanel(chartFunction);
    JLabel jLabel1 = new JLabel();
    JComboBox cmbFunctionType = new JComboBox();
    JLabel jLabel2 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JButton jButton1 = new JButton();
    JLabel jLabel3 = new JLabel();
    JTextField jTextField2 = new JTextField();
    JLabel jLabel4 = new JLabel();
    JTextField jTextField3 = new JTextField();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    public FunctionPlotPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        jLabel1.setText("Function");
        jLabel2.setText("Parameters");
        jTextField1.setText("");
        jButton1.setText("Plot");
        jButton1.addActionListener(new FunctionPlotPanel_jButton1_actionAdapter(this));
        jLabel3.setText("Min");
        jLabel4.setText("Max");
        jTextField2.setText("");
        jTextField3.setText("");
        cmbFunctionType.setMaximumSize(new Dimension(24, 19));
        this.setMinimumSize(new Dimension(728, 171));
        this.add(jPanel2, new GridBagConstraints(0, 3, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(34, 4, 20, 21), 27, -168));
        this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(13, 12, 0, 0), 13, 5));
        this.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 13, 0, 0), 0, 0));
        this.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(21, 14, 0, 36), 0, 0));
        this.add(jTextField3, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(19, 9, 0, 394), 108, 0));
        this.add(jTextField1, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(17, 21, 0, 379), 255, 1));
        this.add(jTextField2, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(18, 19, 0, 22), 72, 0));
        this.add(jLabel4, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(22, 0, 0, 0), 0, 0));
        this.add(cmbFunctionType, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(13, 21, 0, 0), 79, 0));
        this.add(jButton1, new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(13, 7, 0, 448), 38, 3));


        cmbFunctionType.addItem("Uniform");
        cmbFunctionType.addItem("Multivariate Gaussian");
        cmbFunctionType.addItem("Multivariate Gaussian Bimodal");
    }

    void plotData(double[][] data) {

        XYSeriesCollection plots = new XYSeriesCollection();

        XYSeries dataSeries = new XYSeries("Data");
        plots.addSeries(dataSeries);

        double[] xSeries = data[0];
        double[] ySeries = data[1];

        for (int i = 0; i < xSeries.length; i++) {
            dataSeries.add(xSeries[i], ySeries[i]);
        }

        chartFunction = ChartFactory.createScatterPlot("Function Plot", // Title
                "X", //(, // X-Axis label
                "Y", // Y-Axis label
                plots, // Dataset
                PlotOrientation.VERTICAL, true, // Show legend
                true, false);
        jPanel2.setChart(chartFunction);
    }

    void jButton1_actionPerformed(ActionEvent e) {
        IFunction f = FunctionFactory.getFunction(cmbFunctionType.getSelectedItem().toString());
        f.setRange(0, 1);
        f.setNumDataPoints(5000);

        double[][] data = f.getData();

        plotData(data);
    }
}

class FunctionPlotPanel_jButton1_actionAdapter implements java.awt.event.ActionListener {
    FunctionPlotPanel adaptee;

    FunctionPlotPanel_jButton1_actionAdapter(FunctionPlotPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}
