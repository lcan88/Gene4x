package org.geworkbench.components.microarrays;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.util.microarrayutils.MicroarrayViewEventBase;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CorrelationsPanel extends MicroarrayViewEventBase {
    public CorrelationsPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JFreeChart chart;
    ChartPanel chartPanel;
    JList geneList;

    protected void jbInit() throws Exception {
        super.jbInit();


        JPanel correlationsPanel = new JPanel();
        BorderLayout borderLayout1 = new BorderLayout();
        JSplitPane jSplitPane1 = new JSplitPane();
        JScrollPane jScrollPane1 = new JScrollPane();
        geneList = new JList();
        JPanel geneListPanel = new JPanel();
        BorderLayout borderLayout2 = new BorderLayout();
        JPanel jPanel3 = new JPanel();
        JTextField jTextField1 = new JTextField();

        chart = ChartFactory.createXYLineChart(null, // Title
                "Experiment", // X-Axis label
                "Value", // Y-Axis label
                new XYSeriesCollection(), // Dataset
                PlotOrientation.VERTICAL, false, // Show legend
                true, true);
        chartPanel = new ChartPanel(chart);

        correlationsPanel.setLayout(borderLayout1);
        geneListPanel.setLayout(borderLayout2);
        jTextField1.setPreferredSize(new Dimension(150, 20));
        jTextField1.setText("jTextField1");
        geneList.addMouseListener(new CorrelationsPanel_geneList_mouseAdapter(this));
        correlationsPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);
        jSplitPane1.add(geneListPanel, JSplitPane.LEFT);
        jSplitPane1.add(chartPanel, JSplitPane.RIGHT);
        jPanel3.add(jTextField1);
        geneListPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        jScrollPane1.getViewport().add(geneList);
        mainPanel.add(correlationsPanel, BorderLayout.CENTER);
        geneListPanel.add(jPanel3, java.awt.BorderLayout.NORTH);
    }

    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {
        if (maSetView == null) {
            return;
        }
        DefaultListModel model = new DefaultListModel();
        DSItemList<DSGeneMarker> markers = maSetView.markers();
        for (int i = 0; i < markers.size(); i++) {
            model.addElement(markers.get(i));
        }
        //        for(DSMarker marker : markers){
        //            model.addElement(marker);
        //        }
        geneList.setModel(model);
    }

    public void geneList_mouseClicked(MouseEvent e) {
        Object[] selectedValues = geneList.getSelectedValues();
        if (selectedValues.length < 2) {
            return;
        }

        XYSeriesCollection plots = new XYSeriesCollection();
        DSGeneMarker marker1 = (DSGeneMarker) selectedValues[0];
        DSGeneMarker marker2 = (DSGeneMarker) selectedValues[1];

        XYSeries dataSeries = new XYSeries("");
        for (int maCtr = 0; maCtr < maSetView.size(); maCtr++) {
            double val1 = refMASet.get(maCtr).getMarkerValue(marker1).getValue();
            double val2 = refMASet.get(maCtr).getMarkerValue(marker2).getValue();
            //            double val1 = refMASet.getValue(marker1.getSerial(), maCtr);
            //            double val2 = refMASet.getValue(marker2.getSerial(), maCtr);
            dataSeries.add(val1, val2);
            //            dataSeries.add(microarray1.getMarkerValue(geneCtr).getValue(), microarray2.getMarkerValue(geneCtr).getValue());
        }
        plots.addSeries(dataSeries);
        //        System.out.println(microarray1.getLabel());
        chart = ChartFactory.createScatterPlot(null, // Title
                marker1.getGeneName(), // X-Axis label
                marker2.getGeneName(), // Y-Axis label
                plots, // Dataset
                PlotOrientation.VERTICAL, false, // Show legend
                true, true);

        chartPanel.setChart(chart);

    }

}

class CorrelationsPanel_geneList_mouseAdapter extends MouseAdapter {
    private CorrelationsPanel adaptee;

    CorrelationsPanel_geneList_mouseAdapter(CorrelationsPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.geneList_mouseClicked(e);
    }
}
