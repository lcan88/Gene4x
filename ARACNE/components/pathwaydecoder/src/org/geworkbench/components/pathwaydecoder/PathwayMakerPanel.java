package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.algorithms.BWAbstractAlgorithm;
import org.geworkbench.bison.algorithm.AlgorithmEvent;
import org.geworkbench.bison.algorithm.AlgorithmEventListener;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.ExampleFilter;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.components.pathwaydecoder.remote.SOAPCommunicator;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.Parameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PathwayMakerPanel extends JPanel implements Serializable {

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    Parameter parms = new Parameter();
    DSMicroarraySet<DSMicroarray> maSet = null;
    JPanel jPanel1 = new JPanel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JButton jButton1 = new JButton();
    JLabel jLabel6 = new JLabel();
    JTextField AffyIdBox = new JTextField();
    JTextField UpperRangeBox = new JTextField();
    JLabel jLabel5 = new JLabel();
    JPanel jPanel2 = new JPanel();
    JButton jButton2 = new JButton();
    JTextField MIErrorPercentBox = new JTextField();
    JCheckBox jCheckBox2 = new JCheckBox();
    JLabel jLabel1 = new JLabel();
    JCheckBox jCheckBox1 = new JCheckBox();
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    JPanel jPanel3 = new JPanel();
    GridBagLayout gridBagLayout4 = new GridBagLayout();
    JLabel jLabel2 = new JLabel();
    JTextField MinSigmaPercentBox = new JTextField();
    JTextField MinMeanBox = new JTextField();
    JCheckBox ReduceBox = new JCheckBox();
    JTextField miThresholdBox = new JTextField();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JButton StopBtn = new JButton();
    JProgressBar progressBar = new JProgressBar();
    JButton CreateMatrixBtn = new JButton();
    BWAbstractAlgorithm pathwayAlgorithm = null;
    JPanel jPanel4 = new JPanel();
    JTextField host = new JTextField();
    JTextField port = new JTextField();
    JLabel denoteHost = new JLabel("Host");
    JLabel denotePort = new JLabel("Port");
    static final String configFile = System.getProperty(
        "temporary.files.directory") + "config.ini";
    double noisePercent = 0.05;
    int steps = 10000;
    int maNo = 200;

    ButtonGroup grpSimulationType = new ButtonGroup();
    Component component1;
    JCheckBox runRemotely = new JCheckBox();
    JComboBox jComboBox1 = new JComboBox();
    String type = "gaussian";
    JLabel jLabel4 = new JLabel();
    JLabel jLabel8 = new JLabel();
    JTextField adjMatrixFileName = new JTextField();

    public PathwayMakerPanel() {
        org.geworkbench.util.PropertiesMonitor props = new org.geworkbench.util.PropertiesMonitor(configFile);
        noisePercent = Double.parseDouble(props.get("Simulation.NoisePercent", Double.toString(noisePercent)));
        steps = Integer.parseInt(props.get("Simulation.Steps", Integer.toString(steps)));
        maNo = Integer.parseInt(props.get("Simulation.MicroarrayNo", Integer.toString(maNo)));
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        jComboBox1.addItem("gaussian");
        jComboBox1.addItem("histogram");
        jComboBox1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jComboBox1_actionPerformed(e);
            }
        });
        component1 = Box.createVerticalStrut(8);
        this.setLayout(gridBagLayout1);
        jPanel1.setLayout(gridBagLayout2);
        jButton1.setSize(new Dimension(100, 25));
        jButton1.setMaximumSize(new Dimension(120, 25));
        jButton1.setMinimumSize(new Dimension(120, 25));
        jButton1.setPreferredSize(new Dimension(120, 25));
        jButton1.setText("Create with Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAndReduce(e);
            }
        });
        jLabel6.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel6.setText("Affy Id:");
        AffyIdBox.setMaximumSize(new Dimension(80, 22));
        AffyIdBox.setMinimumSize(new Dimension(80, 22));
        AffyIdBox.setPreferredSize(new Dimension(80, 22));
        AffyIdBox.setSelectionEnd(3);
        AffyIdBox.setText("");
        AffyIdBox.setHorizontalAlignment(SwingConstants.TRAILING);
        UpperRangeBox.setMaximumSize(new Dimension(80, 22));
        UpperRangeBox.setMinimumSize(new Dimension(80, 22));
        UpperRangeBox.setPreferredSize(new Dimension(80, 22));
        UpperRangeBox.setSelectionEnd(3);
        UpperRangeBox.setText("0.3");
        UpperRangeBox.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel5.setText("Low/High %:");
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jButton2.setSize(new Dimension(100, 25));
        jButton2.setMaximumSize(new Dimension(120, 25));
        jButton2.setMinimumSize(new Dimension(120, 25));
        jButton2.setPreferredSize(new Dimension(120, 25));
        jButton2.setText("Clean Matrix");
        MIErrorPercentBox.setMaximumSize(new Dimension(80, 22));
        MIErrorPercentBox.setMinimumSize(new Dimension(80, 22));
        MIErrorPercentBox.setPreferredSize(new Dimension(80, 22));
        MIErrorPercentBox.setSelectionEnd(3);
        MIErrorPercentBox.setText("0.1");
        MIErrorPercentBox.setHorizontalAlignment(SwingConstants.TRAILING);
        jCheckBox2.setBackground(SystemColor.activeCaptionBorder);
        jCheckBox2.setHorizontalAlignment(SwingConstants.TRAILING);
        jCheckBox2.setHorizontalTextPosition(SwingConstants.LEFT);
        jCheckBox2.setText("Missing Genes");
        jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel1.setText("MI Error %:");
        jCheckBox1.setBackground(SystemColor.activeCaptionBorder);
        jCheckBox1.setHorizontalAlignment(SwingConstants.TRAILING);
        jCheckBox1.setHorizontalTextPosition(SwingConstants.LEFT);
        jCheckBox1.setText("Removed Edges");
        jPanel2.setLayout(gridBagLayout3);
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel3.setLayout(gridBagLayout4);
        jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel2.setText("Min. Mean:");
        MinSigmaPercentBox.setMaximumSize(new Dimension(80, 22));
        MinSigmaPercentBox.setMinimumSize(new Dimension(80, 22));
        MinSigmaPercentBox.setPreferredSize(new Dimension(80, 22));
        MinSigmaPercentBox.setSelectionEnd(3);
        MinSigmaPercentBox.setText("0.3");
        MinSigmaPercentBox.setHorizontalAlignment(SwingConstants.TRAILING);
        MinMeanBox.setMaximumSize(new Dimension(80, 22));
        MinMeanBox.setMinimumSize(new Dimension(80, 22));
        MinMeanBox.setPreferredSize(new Dimension(80, 22));
        MinMeanBox.setSelectionEnd(3);
        MinMeanBox.setText("50");
        MinMeanBox.setHorizontalAlignment(SwingConstants.TRAILING);
        ReduceBox.setBackground(SystemColor.activeCaptionBorder);
        ReduceBox.setHorizontalAlignment(SwingConstants.TRAILING);
        ReduceBox.setHorizontalTextPosition(SwingConstants.LEFT);
        ReduceBox.setSelected(true);
        ReduceBox.setText("Clean Matrix");
        miThresholdBox.setMaximumSize(new Dimension(80, 22));
        miThresholdBox.setMinimumSize(new Dimension(80, 22));
        miThresholdBox.setPreferredSize(new Dimension(80, 22));
        miThresholdBox.setText("0.01");
        miThresholdBox.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel3.setText("Min Variance%:");
        jLabel7.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel7.setHorizontalTextPosition(SwingConstants.TRAILING);
        jLabel7.setText("MI Threshold:");
        StopBtn.setSize(new Dimension(100, 25));
        StopBtn.setMaximumSize(new Dimension(120, 25));
        StopBtn.setMinimumSize(new Dimension(120, 25));
        StopBtn.setPreferredSize(new Dimension(120, 25));
        StopBtn.setVerifyInputWhenFocusTarget(true);
        StopBtn.setSelectedIcon(null);
        StopBtn.setText("Stop");
        StopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StopBtn_actionPerformed(e);
            }
        });
        jPanel3.setBorder(BorderFactory.createEtchedBorder());
        progressBar.setBorder(BorderFactory.createEtchedBorder());
        progressBar.setPreferredSize(new Dimension(150, 20));
        CreateMatrixBtn.setText("Create");
        CreateMatrixBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateMatrixBtn_actionPerformed(e);
            }
        });
        CreateMatrixBtn.setSelectedIcon(null);
        CreateMatrixBtn.setVerifyInputWhenFocusTarget(true);
        CreateMatrixBtn.setMnemonic('0');
        CreateMatrixBtn.setPreferredSize(new Dimension(120, 25));
        CreateMatrixBtn.setMinimumSize(new Dimension(120, 25));
        CreateMatrixBtn.setMaximumSize(new Dimension(120, 25));
        CreateMatrixBtn.setSize(new Dimension(100, 25));
        runRemotely.setText("Run remotely");
        runRemotely.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (runRemotely.isSelected()) {
                    jPanel4.setVisible(true);
                }
                else {
                    jPanel4.setVisible(false);
                }
            }
        });
        jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel4.setText("Method");
        jLabel8.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel8.setText("Adjacency Matrix File Name: ");
        adjMatrixFileName.setAlignmentX( (float) 0.9);
        adjMatrixFileName.setAlignmentY( (float) 0.9);
        adjMatrixFileName.setMaximumSize(new Dimension(80, 22));
        adjMatrixFileName.setMinimumSize(new Dimension(80, 22));
        adjMatrixFileName.setPreferredSize(new Dimension(80, 22));
        adjMatrixFileName.setText(".adj");
        adjMatrixFileName.setHorizontalAlignment(SwingConstants.TRAILING);
        jPanel1.add(jButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
        jPanel1.add(jLabel5, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            , GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel1.add(AffyIdBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel1.add(UpperRangeBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel1.add(jLabel6, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel2.add(jButton2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
        jPanel2.add(MIErrorPercentBox,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 4, 2, 4), 0, 0));
        jPanel2.add(jCheckBox2, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel2.add(jCheckBox1, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel2.add(jLabel1, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        host.setMaximumSize(new Dimension(190, 22));
        host.setMinimumSize(new Dimension(190, 22));
        host.setPreferredSize(new Dimension(190, 22));
        host.setSelectionEnd(3);
        host.setText(System.getProperty("aracne.nonmpi.host"));
        this.add(jPanel3, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(0, 0, 2, 2), 0, 0));
        host.setHorizontalAlignment(SwingConstants.TRAILING);

        port.setMaximumSize(new Dimension(80, 22));
        port.setMinimumSize(new Dimension(50, 22));
        port.setPreferredSize(new Dimension(50, 22));
        port.setSelectionEnd(3);
        port.setText("80");
        port.setHorizontalAlignment(SwingConstants.TRAILING);

        jPanel4.setBorder(BorderFactory.createEtchedBorder());
        jPanel4.add(denoteHost);
        jPanel4.add(host);
        jPanel4.add(denotePort);
        jPanel4.add(port);
        jPanel4.setVisible(false);

        jPanel3.add(ReduceBox, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0
            , GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            new Insets(0, 4, 2, 4), 0, 0));
        jPanel3.add(CreateMatrixBtn,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(StopBtn, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            , GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
        jPanel3.add(runRemotely, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(jLabel4, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
            , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
            new Insets(5, 0, 0, 0), 260, 0));
        jPanel3.add(jLabel8, new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0
            , GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 73, 0));
        jPanel3.add(jPanel4, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(jPanel4, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(jLabel7, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(jLabel3, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(jLabel2, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(2, 4, 2, 4), 0, 0));
        this.add(jPanel1, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.BOTH,
                                                 new Insets(2, 2, 2, 2), 0, 0));
        this.add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(2, 2, 2, 2), 0, 0));
        this.add(progressBar, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                                     , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
        jPanel4.setVisible(false);

        this.add(progressBar, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
        this.add(component1, new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0
            , GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(jPanel3, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                                                 , GridBagConstraints.NORTH,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(5, 0, 0, 2), 0, 0));
        jPanel3.add(jLabel7, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(miThresholdBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(jLabel3, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(MinMeanBox, new GridBagConstraints(3, 1, 1, 2, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(jComboBox1, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
            new Insets(0, 6, 0, 0), 74, 0));
        jPanel3.add(ReduceBox, new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0
            , GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(CreateMatrixBtn,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(StopBtn, new GridBagConstraints(0, 1, 1, 2, 0.0, 0.0
            , GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
        jPanel3.add(runRemotely, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(miThresholdBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(MinMeanBox, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(MinSigmaPercentBox,
                    new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(2, 4, 2, 4), 0, 0));
        jPanel3.add(MinSigmaPercentBox,
                    new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(2, 4, 2, 5), 0, 0));
        jPanel3.add(adjMatrixFileName,
                    new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 5, 0, 4), 65, 0));
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
    }

    void jComboBox1_actionPerformed(ActionEvent e){
        type = (String)jComboBox1.getSelectedItem();
    }

    private void setBaseParameters() {
        // Set default parameters
        parms.setType(Parameter.BOTH);
        parms.setMArray(0, new int[0]);
        // Set the Mutual Information threshold
        double miThreshold = 0.01;
        parms.method = type;
        try {
            miThreshold = Double.parseDouble(miThresholdBox.getText());
        } catch (NumberFormatException ex) {
        }
        parms.setMIThreshold(miThreshold);
        // Set the MI Error Percent
        double miErrorPercent = 0.1;
        try {
            miErrorPercent = Double.parseDouble(MIErrorPercentBox.getText());
        } catch (NumberFormatException ex) {
        }
        parms.setMIErrorPercent(miErrorPercent);
        // Set if matrix should be pruned
        boolean reduce = ReduceBox.isSelected();
        parms.setReduce(reduce);
        // Set mean and standard deviation
        double minMean = 50;
        try {
            minMean = Double.parseDouble(MinMeanBox.getText());
        } catch (NumberFormatException ex) {
        }
        double minSigma = 0.3;
        try {
            minSigma = Double.parseDouble(MinSigmaPercentBox.getText());
        } catch (NumberFormatException ex) {
        }
        parms.setParams(minMean, minSigma);
    }

    void createMatrix(ActionEvent e) {
        setBaseParameters();
        if (runRemotely.isSelected()) {
            //run on the server
            parms.adjMatrixName = adjMatrixFileName.getText();
            pathwayAlgorithm = new SOAPCommunicator(maSet, parms, host.getText().trim(),
                port.getText().trim());
            pathwayAlgorithm.start();
        } else {
        }
    }

    void createAndReduce(ActionEvent e) {
        setBaseParameters();
        String affyId = AffyIdBox.getText();
        try {
            DSGeneMarker gm = maSet.getMarkers().get(affyId);
            if (gm != null) {
                int id = gm.getSerial();
                parms.setControlId(id);
            }
        } catch (Exception ex) {
        }
    }

    public void setMicroarraySet(DSMicroarraySet set) {
        maSet = set;
        String adjFileName = maSet.getPath();
        if (adjFileName == null) {
            adjFileName = "adjMatrix.adj";
        } else if (adjFileName.endsWith(".exp")) {
            adjFileName = adjFileName.substring(0,adjFileName.length()-4)+".adj";
        }
        adjMatrixFileName.setText(adjFileName);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }


    void StopBtn_actionPerformed(ActionEvent e) {
        if (pathwayAlgorithm != null)
            pathwayAlgorithm.stop();
    }

    void CreateMatrixBtn_actionPerformed(ActionEvent e) {
        setBaseParameters();
        if (runRemotely.isSelected()) {
            //run on the server
            parms.adjMatrixName = adjMatrixFileName.getText();
            pathwayAlgorithm = new SOAPCommunicator(maSet, parms, host.getText(),
                port.getText());
            pathwayAlgorithm.start();

        }
        else {
        }
    }
}
