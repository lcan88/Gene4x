package org.geworkbench.components.goterms;

import distributions.ChiSquareDistribution;
import gov.nih.nci.caBIO.util.ManagerException;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.CSMarkerManager;
import org.geworkbench.components.goterms.annotation.GoTerm;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Asynchronous;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.LogStats;
import org.geworkbench.util.ProgressBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalSliderUI;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title: caWorkbench</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 3.0
 */

@AcceptTypes({DSMicroarraySet.class}) 
public class GOPanel implements VisualPlugin, Observer {
    public GOPanel() {
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        panel.panels().add(geneSelection);
    }

    Set<String> hash = Collections.synchronizedSet(new HashSet<String>());

    String chipType = "Unknown";

    private DSPanel<DSGeneMarker> geneSelection = new CSPanel<DSGeneMarker>(""); // selected markerset
    private DSPanel<DSGeneMarker> panel = new CSPanel<DSGeneMarker>("Selection"); //the marker set

    private JPanel treeView = new JPanel();
    private JPanel mainPanel = new JPanel();
    private JTabbedPane jTabbedPane1 = new JTabbedPane();
    private JScrollPane tableScrollPane = new JScrollPane();
    private JPanel tableView = new JPanel();
    private JToolBar jToolBar = new JToolBar();
    private JButton refList = new JButton();

    private DefaultMutableTreeNode goRootProcess = new DefaultMutableTreeNode();
    DefaultTreeModel goTreeModelProcess = new DefaultTreeModel(goRootProcess);
    private JTree jGoTreeProcess = new JTree(goTreeModelProcess);

    private DefaultMutableTreeNode goRootFunction = new DefaultMutableTreeNode();
    DefaultTreeModel goTreeModelFunction = new DefaultTreeModel(goRootFunction);
    private JTree jGoTreeFunction = new JTree(goTreeModelFunction);

    private DefaultMutableTreeNode goRootComponent = new DefaultMutableTreeNode();
    DefaultTreeModel goTreeModelComponent = new DefaultTreeModel(goRootComponent);
    private JTree jGoTreeComponent = new JTree(goTreeModelComponent);

    private JTree selectedTree = jGoTreeComponent;
    private DefaultTreeModel selectedTreeModel = goTreeModelComponent;
    private DefaultMutableTreeNode selectedRoot = goRootComponent;

    private HashMap<String, GoTerm> processNodes = new HashMap<String, GoTerm>();
    private HashMap<String, GoTerm> functionNodes = new HashMap<String, GoTerm>();
    private HashMap<String, GoTerm> componentNodes = new HashMap<String, GoTerm>();
    private HashMap<String, GoTerm> goNodes = componentNodes;

    private Vector<String> referenceList = new Vector<String>();

    JMenuItem viewPathway = new JMenuItem();
    JMenuItem viewPathway1 = new JMenuItem();

    JComboBox jChipTypeComboBox = new JComboBox(); //popupmenu for go
    private JPopupMenu jGenePopup = new JPopupMenu();
    private JMenuItem referenceListMappings = new JMenuItem("Reference List");
    private JMenuItem referenceListMappingsRecursive = new JMenuItem("Reference List (including descendants)");

    private JMenuItem selectedListMappings = new JMenuItem("Selected List");
    private JMenuItem selectedListMappingsRecursive = new JMenuItem("Selected List (including descendants)");

    private JMenuItem addSelectedList = new JMenuItem("Selected List");
    private JMenuItem addSelectedListRecursive = new JMenuItem("Selected List (including descendants)");

    private JMenuItem addReferenceList = new JMenuItem("Reference list");
    private JMenuItem addReferenceListRecursive = new JMenuItem("Reference List (including descendants)");

    private JMenu showMappings = new JMenu("Show Mappings");
    private JMenu addToPanel = new JMenu("Add to Set");

    private JList geneList = new JList();

    private JScrollPane jScrollPaneGenes = new JScrollPane();
    private DSMicroarraySet geneListSet = null; //here the handler to the microarryset

    boolean chiSquaredOrHypergeometric = true;
    boolean bonferroniCorrection = false;
    int threshold = 0;

    private DefaultListCellRenderer markerRenderer = new
        DefaultListCellRenderer() {
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
            DSGeneMarker stats = (DSGeneMarker) value;

            stats.setDisPlayType(0);

            if (isSelected) {
                if (geneSelection != null) {
                    if (geneSelection.contains(stats)) {
                        c.setBackground(Color.yellow);
                    }
                }
            }
            else {
                if (geneSelection != null) {
                    if (geneSelection.contains(stats)) {
                        c.setBackground(Color.PINK);
                    }
                    else {
                        c.setBackground(Color.white);
                    }
                }
            }
            return c;
        }
    };

    JFreeChart chart;
    ChartPanel graph;
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JSplitPane jPanel2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    BorderLayout borderLayout3 = new BorderLayout();
    GridLayout gridLayout1 = new GridLayout();
    JButton jButton1 = new JButton();
    JButton jButton4 = new JButton();
    GridLayout gridLayout2 = new GridLayout();

    JTable reportTable = new JTable();
    ReportTableModel rt = null;
    JTableHeader reportTableHeader = null;

    JScrollPane scrollpane = null;
    BorderLayout borderLayout2 = new BorderLayout();
    JToolBar jToolBar1 = new JToolBar();
    JToolBar jToolBar2 = new JToolBar();
    JPanel chartPanel = new JPanel();
    JCheckBox jCheckBox1 = new JCheckBox();
    JCheckBox refListUse = new JCheckBox();
    Box refListPanel = Box.createHorizontalBox();
    JSpinner jSpinner1 = new JSpinner();
    JLabel jLabel1 = new JLabel();
    Box minInCat = Box.createHorizontalBox();

    JSpinner jSpinner2 = new JSpinner();
    SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel();
    JLabel jLabel3 = new JLabel();
    Box stepSizePanel = Box.createHorizontalBox();

    Component component1 = Box.createHorizontalStrut(8);
    Component component3 = Box.createHorizontalStrut(8);
    Component component5 = Box.createHorizontalStrut(8);
    Component component6 = Box.createHorizontalStrut(8);
    Component component7 = Box.createHorizontalStrut(8);
    Component component8 = Box.createHorizontalStrut(8);
    Component component9 = Box.createHorizontalStrut(8);
    Component component10 = Box.createHorizontalStrut(8);
    Component component11 = Box.createHorizontalStrut(8);
    Component component12 = Box.createHorizontalStrut(8);
    Component component13 = Box.createHorizontalStrut(8);
    Component component14 = Box.createVerticalStrut(8);
    Component component15 = Box.createVerticalStrut(8);
    Component component16 = Box.createVerticalStrut(8);
    Component component17 = Box.createVerticalStrut(8);

    Box pValuePanel = Box.createVerticalBox();
    JSlider pValueSlider = new JSlider();
    JTextField pValueBox = new JTextField();

    SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
    JLabel jLabel2 = new JLabel();
    JRadioButton jRadioButton1 = new JRadioButton();
    JRadioButton jRadioButton2 = new JRadioButton();
    JPanel jPanel3 = new JPanel();
    GridLayout gridLayout3 = new GridLayout();
    ButtonGroup buttonGroup1 = new ButtonGroup();
    Component component2 = Box.createHorizontalStrut(8);
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    JButton saveButton = new JButton();
    JButton plotButton = new JButton();
    int t2 = 0;
    ProgressBar pm = ProgressBar.create(ProgressBar.BOUNDED_TYPE);

    JTabbedPane jTabbedPane2 = new JTabbedPane();
    JPanel processPanel = new JPanel();
    JPanel functionPanel = new JPanel();
    JPanel componentPanel = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JScrollPane jScrollPane4 = new JScrollPane();
    JScrollPane jScrollPane3 = new JScrollPane();
    BorderLayout borderLayout4 = new BorderLayout();

    JPopupMenu selectionMenu = new JPopupMenu();
    JMenuItem disable = new JMenuItem();
    JMenuItem enable = new JMenuItem();
    JMenuItem disableAll = new JMenuItem();
    JMenuItem enableAll = new JMenuItem();

    class ReportTableRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String name = (String)rt.getValueAt(row, 1);
            GoTerm term = goNodes.get(name);
            if (term != null && !term.isEnabled()){
                component.setForeground(Color.lightGray);
            }
            else
                component.setForeground(Color.black);
            return component;
        }
    }

    class ReportTableModel
        extends AbstractTableModel {
        private final String[] columnNames = {
            "ID", "Go Term", "Enriched", "Selected", "Total related",
            "p value", "Corrected p value"};
        private Object[] data = null; //same as before...

        public ReportTableModel() {
        }

        public ReportTableModel(Object[] datas) {
            data = datas;
        }

        public int getColumnCount() {
            if (bonferroniCorrection)
                return columnNames.length;
            return columnNames.length - 1;
        }

        public int getRowCount() {
            if (data != null)
                return data.length;
            return 0;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            if (data != null){
                switch (col) {
                case 0:
                    return ((GoDataElement) data[row]).go.getId();
                case 1:
                    return ((GoDataElement) data[row]).go.getName();
                case 2:
                    return ((GoDataElement) data[row]).enriched;
                case 3:
                    return ((GoDataElement) data[row]).go.
                            getDescendantSelectedListCount() + "";
                case 4:
                    return ((GoDataElement) data[row]).go.getDescendantCount() +
                            "";
                case 5:
                    return ((GoDataElement) data[row]).pvalue + "";
                case 6:
                    return ((GoDataElement) data[row]).correctedPValue + "";
                }
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void add(Object value) {
            this.fireTableStructureChanged();
            fireTableRowsInserted(data.length, data.length);
        }

        public void clear() {
            data = null;
            fireTableDataChanged();
        }
    }

    void jbInit() throws Exception {
        component1 = Box.createHorizontalStrut(8);

        mainPanel.setLayout(gridLayout2);
        jCheckBox1.setText("Bonferroni Correction (Enriched only)");
        jCheckBox1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jCheckBox1_actionPerformed(e);
            }
        });
        jCheckBox1.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel1.setText("Min # in Category:");
        jSpinner1.setMaximumSize(new Dimension(70, 25));
        jSpinner1.setMinimumSize(new Dimension(70, 25));
        jSpinner1.setPreferredSize(new Dimension(70, 25));
        spinnerModel.setMinimum(0);
        jSpinner1.setModel(spinnerModel);
        jSpinner1.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                jSpinner1_stateChanged(ce);
            }
        });

        minInCat.setBorder(BorderFactory.createLineBorder(Color.black));
        minInCat.add(jLabel1);
        minInCat.add(component11);
        minInCat.add(jSpinner1);

        jLabel2.setText("p-value metric:");
        jRadioButton1.setText("Chi-Squared");
        jRadioButton2.setText("Fisher's exact");
        jButton2.setText("Recompute");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                recompute_actionPerformed(e);
            }
        });
        jButton3.setText("Save Profiles..");
        jButton3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                saveProfiles_actionPerformed(e);
            }
        });

        plotButton.setText("Plot");
        plotButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                plot_actionPerformed(e);
            }
        });

        processPanel.setLayout(borderLayout4);
        functionPanel.setLayout(new BorderLayout());
        componentPanel.setLayout(new BorderLayout());

        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        jRadioButton1.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                jRadioButton1_stateChanged(ce);
            }
        });
        jRadioButton1.setSelected(true);
        jPanel3.setLayout(gridLayout3);
        gridLayout3.setColumns(3);
        jPanel3.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPanel.add(jTabbedPane1);
        jTabbedPane1.add("TreeView", treeView);

        rt = new ReportTableModel();
        reportTable.setDefaultRenderer(Object.class, new ReportTableRenderer());
        reportTableHeader = reportTable.getTableHeader();
        reportTableHeader.setUpdateTableInRealTime(true);
        reportTableHeader.setReorderingAllowed(true);
        reportTableHeader.addMouseListener(new ColumnListener());

        scrollpane = new JScrollPane(reportTable);

        jTabbedPane1.add("TableView", tableScrollPane);
        tableView.setLayout(borderLayout2);

        jScrollPaneGenes.getViewport().setBackground(UIManager.getColor(
            "Viewport.background"));
        File tmpDir = new File(".");

        FilenameFilter csvFilter = new
            FilenameFilter() {

            public String getDescription() {
                return "CSV file";
            }

            public boolean accept(File f, String name) {
                boolean returnVal = false;

                if (name.endsWith("csv")) {
                    return true;
                }
                return returnVal;
            }
        };
        String[] fileNames = tmpDir.list(csvFilter);

        if (fileNames != null && fileNames.length > 0){
            for (String fileName : fileNames) {
                addChipType(fileName);
            }
        }
        else {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    AnnotationParser.class.getResourceAsStream("chiptypeMap.txt")));
            try {
                String str = null;
                while ((str = br.readLine()) != null) {
                    String[] data = str.split(",");
                    addChipType(data[1]);
                }
                br.close();
            }
            catch (IOException ioe){}
        }

        jChipTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jChipTypeComboBox_actionPerformed(e);
            }
        });
        treeView.setEnabled(false);
        jPanel1.setLayout(gridLayout1);

        treeView.setLayout(borderLayout1);

        jScrollPaneGenes.setBorder(BorderFactory.createLineBorder(Color.black));
        geneList.setFont(new java.awt.Font("MS Sans Serif", 0, 11));
        geneList.setForeground(SystemColor.activeCaption);

        geneList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
            public void mouseClicked(MouseEvent e) {
                geneList_mouseClicked(e);
                super.mouseReleased(e);
            }
        });

        treeView.add(jToolBar, BorderLayout.NORTH);

        refList.setText("Reference List..");
        refList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                refList_actionPerformed(e);
            }
        });

        jButton4.setText("Map List(s)");
        jButton4.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mapList_actionPerformed(e);
            }
        });

        refListUse.setText("");
        refListUse.setSelected(true);
        refListPanel.add(refListUse);
        refListPanel.add(component9);
        refListPanel.add(refList);
        refListPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        jChipTypeComboBox.setMaximumSize(new Dimension(90, 25));
        jChipTypeComboBox.setMinimumSize(new Dimension(90, 25));
        jChipTypeComboBox.setPreferredSize(new Dimension(90, 25));
        jToolBar.add(component5);
        jToolBar.add(jChipTypeComboBox, null);
        jToolBar.add(component7);
        jToolBar.add(refListPanel);
        jToolBar.add(component8);
        jToolBar.add(jButton4, null);
        jPanel1.add(jScrollPaneGenes, null);
        jPanel2.setDoubleBuffered(true);
        jPanel2.setContinuousLayout(true);
        jPanel2.setDividerSize(8);
        jPanel2.setResizeWeight(0);
        jPanel2.setDividerLocation(0.5);
        jPanel2.setTopComponent(jTabbedPane2);
        jTabbedPane2.add(componentPanel, "Component");
        jTabbedPane2.add(functionPanel, "Function");
        jTabbedPane2.add(processPanel, "Process");
        jTabbedPane2.addChangeListener(new ChangeListener(){
             public void stateChanged(ChangeEvent ce){
                 jTabbedPane2_stateChanged(ce);
             }
        });
        jScrollPane1.getViewport().add(jGoTreeProcess);
        jScrollPane3.getViewport().add(jGoTreeFunction);
        jScrollPane4.getViewport().add(jGoTreeComponent);
        treeView.add(jPanel2, BorderLayout.CENTER);
        jPanel2.setBottomComponent(jPanel1);

        addReferenceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addToPanel(true, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addReferenceListRecursive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addToPanel(true, true);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addSelectedList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addToPanel(false, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addSelectedListRecursive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addToPanel(false, true);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        referenceListMappings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                  (selectedTree).getLastSelectedPathComponent();
                    if (node != null) {
                        final GoTerm selected = (GoTerm) node.getUserObject();
                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    notifyAffySelection(selected, true, false);
                                } catch (ManagerException ex1) {
                                    ex1.printStackTrace();
                                } catch (Exception ex1) {
                                    ex1.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        referenceListMappingsRecursive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                  (selectedTree).getLastSelectedPathComponent();
                    if (node != null) {
                        final GoTerm selected = (GoTerm) node.getUserObject();
                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    notifyAffySelection(selected, true, true);
                                } catch (ManagerException ex1) {
                                    ex1.printStackTrace();
                                } catch (Exception ex1) {
                                    ex1.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        selectedListMappings.addActionListener(new java.awt.event.
                                                ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                  (selectedTree).getLastSelectedPathComponent();
                    if (node != null) {
                        final GoTerm selected = (GoTerm) node.getUserObject();
                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    notifyAffySelection(selected, false, false);
                                } catch (ManagerException ex1) {
                                    ex1.printStackTrace();
                                } catch (Exception ex1) {
                                    ex1.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        selectedListMappingsRecursive.addActionListener(new java.awt.event.
                                                ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                                  (selectedTree).getLastSelectedPathComponent();
                    if (node != null) {
                        final GoTerm selected = (GoTerm) node.getUserObject();
                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    notifyAffySelection(selected, false, true);
                                } catch (ManagerException ex1) {
                                    ex1.printStackTrace();
                                } catch (Exception ex1) {
                                    ex1.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        showMappings.add(referenceListMappings);
        showMappings.add(referenceListMappingsRecursive);
        showMappings.addSeparator();
        showMappings.add(selectedListMappings);
        showMappings.add(selectedListMappingsRecursive);

        addToPanel.add(addReferenceList);
        addToPanel.add(addReferenceListRecursive);
        addToPanel.addSeparator();
        addToPanel.add(addSelectedList);
        addToPanel.add(addSelectedListRecursive);

        jGenePopup.add(showMappings);
        jGenePopup.addSeparator();
        jGenePopup.add(addToPanel);

        geneList.setCellRenderer(markerRenderer);
        jScrollPaneGenes.getViewport().add(geneList, null);
        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof JTree){
                    int selRow = ((JTree) e.getSource()).getRowForLocation(e.getX(), e.getY());
                    if (selRow != -1) {
                        if (e.isMetaDown()) {
                            jGenePopup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        };
        jGoTreeProcess.addMouseListener(ml);
        jGoTreeFunction.addMouseListener(ml);
        jGoTreeComponent.addMouseListener(ml);

        tableView.add(scrollpane, java.awt.BorderLayout.CENTER);
        tableView.add(jToolBar1, java.awt.BorderLayout.NORTH);
        tableScrollPane.getViewport().add(tableView);

        saveButton.setText("Save..");
        saveButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                saveButton_actionPerformed(e);
            }
        });

        jPanel3.add(jLabel2);
        jPanel3.add(jRadioButton1);
        jPanel3.add(jRadioButton2);

        jToolBar1.add(jPanel3);
        jToolBar1.add(jCheckBox1);
        jToolBar1.add(component1);
        jToolBar1.add(minInCat);
        jToolBar1.add(component2);
        jToolBar1.add(jButton2);
        jToolBar1.add(component17);
        jToolBar1.add(saveButton);

        processPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        functionPanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);
        componentPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);
        geneList.setModel(new DefaultListModel());
        chart = ChartFactory.createXYLineChart(
            "P-Value Trends", // Title
            "Number of Markers", // X-Axis label
            "Value", // Y-Axis label
            new XYSeriesCollection(), // Dataset
            PlotOrientation.VERTICAL,
            false, // Show legend
            true,
            true);
        graph = new ChartPanel(chart, true);

        stepSizePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        spinnerModel1.setMinimum(new Integer(1));
        jSpinner2.setModel(spinnerModel1);
        jSpinner2.setValue(new Integer(1));
        jSpinner2.setMaximumSize(new Dimension(70, 25));
        jSpinner2.setMinimumSize(new Dimension(70, 25));
        jSpinner2.setPreferredSize(new Dimension(70, 25));
        jSpinner2.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                jSpinner2_stateChanged(ce);
            }
        });

        jLabel3.setText("Step-size:");
        stepSizePanel.add(jLabel3);
        stepSizePanel.add(component13);
        stepSizePanel.add(jSpinner2);

        jToolBar2.add(plotButton);
        jToolBar2.add(component12);
        jToolBar2.add(stepSizePanel);
        jToolBar2.add(component3);
        jToolBar2.add(jButton3);

        pValueSlider.setMinimum(1);
        pValueSlider.setMaximum(10000);
        pValueSlider.setValue(1);
        pValueSlider.setInverted(true);

        pValueSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent ce){
                pValueSlider_stateChanged(ce);
            }
        });
        pValueSlider.setOrientation(JSlider.VERTICAL);
        pValueSlider.setUI(new PValueSliderUI());

        pValueBox.setMaximumSize(new Dimension(58, 25));
        pValueBox.setMinimumSize(new Dimension(58, 25));
        pValueBox.setPreferredSize(new Dimension(58, 25));
        pValueBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pValueBox_actionPerformed(e);
            }
        });
        pValueBox.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me){
                pValueBox.setSelectionStart(0);
                pValueBox.setSelectionEnd(pValueBox.getText().length());
            }
        });
        pValueBox.setText("1");

        pValuePanel.add(component15);
        pValuePanel.add(pValueSlider);
        pValuePanel.add(component14);
        pValuePanel.add(pValueBox);
        pValuePanel.add(component16);

        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(jToolBar2, BorderLayout.NORTH);
        chartPanel.add(graph, BorderLayout.CENTER);
        chartPanel.add(pValuePanel, BorderLayout.EAST);
        jTabbedPane1.add("P-Value Trends", chartPanel);

        enable.setText("Enable");
        enable.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                enable_actionPerformed(e);
            }
        });

        disable.setText("Disable");
        disable.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                disable_actionPerformed(e);
            }
        });

        enableAll.setText("Enable All");
        enableAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                enableAll_actionPerformed(e);
            }
        });

        disableAll.setText("Disable All");
        disableAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                disableAll_actionPerformed(e);
            }
        });

        selectionMenu.add(enable);
        selectionMenu.add(disable);
        selectionMenu.addSeparator();
        selectionMenu.add(enableAll);
        selectionMenu.add(disableAll);

        reportTable.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent me){
                if (me.isMetaDown()) {
                    selectionMenu.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });
    }

    int stepSize = 1;
    double pValueThreshold = 1d;

    private DecimalFormat format = new DecimalFormat("0.###E00");

    void saveButton_actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser(".");
        String psFilename = null;
        FileFilter filter = new CategoryFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Save Categories");
        String extension = ((CategoryFileFilter) filter).getExtension();
        int choice = fc.showSaveDialog(getComponent());
        if (choice == JFileChooser.APPROVE_OPTION) {
          psFilename = fc.getSelectedFile().getAbsolutePath();
          if (!psFilename.endsWith(extension)) {
            psFilename += extension;
          }
          saveCategories(psFilename);
      }
    }

    void saveCategories(String filename){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new
                File(filename)));
            String line = null;
            int rows = rt.getRowCount();
            int columns = rt.getColumnCount();
            for (int row = 0; row < rows; row++){
                line = (String)rt.getValueAt(row, 0);
                for (int column = 1; column < columns; column++){
                    line += "\t" + rt.getValueAt(row, column);
                }
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (IOException ioe){ioe.printStackTrace();}
    }

    void disableAll_actionPerformed(ActionEvent e){
        int rows = reportTable.getRowCount();
        for (int row = 0; row < rows; row++){
            goNodes.get(rt.getValueAt(row, 1)).setEnabled(false);
        }
        reportTable.repaint();
    }

    void enableAll_actionPerformed(ActionEvent e){
        int rows = reportTable.getRowCount();
        for (int row = 0; row < rows; row++){
            goNodes.get(rt.getValueAt(row, 1)).setEnabled(true);
        }
        reportTable.repaint();
    }

    void disable_actionPerformed(ActionEvent e){
        int[] selectedRows = reportTable.getSelectedRows();
        for (int row : selectedRows){
            goNodes.get(rt.getValueAt(row, 1)).setEnabled(false);
        }
        reportTable.repaint();
    }

    void enable_actionPerformed(ActionEvent e){
        int[] selectedRows = reportTable.getSelectedRows();
        for (int row : selectedRows){
            goNodes.get(rt.getValueAt(row, 1)).setEnabled(true);
        }
        reportTable.repaint();
    }

    void pValueBox_actionPerformed(ActionEvent e){
        String text = pValueBox.getText();
        try {
            double value = Double.parseDouble(text);
            if (value < 0){
                pValueBox.setText("0");
                return;
            }
            if (value > 1){
                pValueBox.setText("1");
                return;
            }
            pValueBox.setText(format.format(value));
            pValueSlider.setValue((int)Math.pow(10, (4 * (1 - value))));
        }
        catch (NumberFormatException nfe){
            pValueBox.setText("0");
        }
    }

    void pValueSlider_stateChanged(ChangeEvent ce){
        int value = pValueSlider.getValue();
        pValueThreshold = 1 - (Math.log10(value) / 4);
        pValueBox.setText(format.format(pValueThreshold));
    }

    void jSpinner2_stateChanged(ChangeEvent ce){
        stepSize = (Integer)jSpinner2.getValue();
    }

    void refreshPlot(){
        Thread t = new Thread() {
            public void run() {
                Hashtable<String, Stack> pValues = computePValueTrends();
                System.gc();
                Object[] categories = pValues.keySet().toArray();
                XYSeriesCollection plots = new XYSeriesCollection();
                for (int i = 0; i < categories.length; i++) {
                    XYSeries dataSeries = new XYSeries((String)categories[i]);
                    Stack<PValueItem> data = (Stack<PValueItem>)pValues.get((String)categories[i]);
                    for (PValueItem value : data)
                        dataSeries.add(value);
                    plots.addSeries(dataSeries);
                }
                chart = ChartFactory.createXYLineChart(
                        "P-Value Trends", // Title
                        "Number of Genes", // X-Axis label
                        "P-Value", // Y-Axis label
                        plots, // Dataset
                        PlotOrientation.VERTICAL,
                        false, // Show legend
                        true,
                        true);
                LogarithmicAxis rangeAxis = new LogarithmicAxis("P-Value");
                rangeAxis.autoAdjustRange();
                rangeAxis.setAllowNegativesFlag(true);
                ((XYPlot)chart.getPlot()).setRangeAxis(rangeAxis);
                graph.setChart(chart);
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    void plot_actionPerformed(ActionEvent e){
        refreshPlot();
    }

    void addChipType(String fileName){
        String chip = fileName.split("_annot.csv")[0];
        jChipTypeComboBox.addItem(fileName.split("_annot.csv")[0]);
        if (chip.equalsIgnoreCase("HG_U95Av2")) {
            jChipTypeComboBox.setSelectedItem("HG_U95Av2");
        }
    }

    void jTabbedPane2_stateChanged(ChangeEvent ce){
        int index = jTabbedPane2.getSelectedIndex();
        switch (index) {
        case 0:
            selectedTree = jGoTreeComponent;
            selectedTreeModel = goTreeModelComponent;
            selectedRoot = goRootComponent;
            currentData = componentData;
            goNodes = componentNodes;
            numNodes = 9091;
            rebuildTable();
            break;
        case 1:
            selectedTree = jGoTreeFunction;
            selectedTreeModel = goTreeModelFunction;
            selectedRoot = goRootFunction;
            currentData = functionData;
            goNodes = functionNodes;
            numNodes = 11980;
            rebuildTable();
            break;
        case 2:
            selectedTree = jGoTreeProcess;
            selectedTreeModel = goTreeModelProcess;
            selectedRoot = goRootProcess;
            currentData = processData;
            goNodes = processNodes;
            numNodes = 239090;
            rebuildTable();
            break;
        }
    }

    HashMap buildStatus = new HashMap();
    int currentOntology = 0;
    public static final int PROCESS_ONTOLOGY = 2;
    public static final int FUNCTION_ONTOLOGY = 4;
    public static final int COMPONENT_ONTOLOGY = 8;

    public static final String PROCESS_ONTOLOGY_FILE = "process.ontology";
    public static final String FUNCTION_ONTOLOGY_FILE = "function.ontology";
    public static final String COMPONENT_ONTOLOGY_FILE = "component.ontology";

    void buildAndMapTree() {
        t2 = 0;
        pm.addObserver(this);
        pm.setTitle("Building Tree");
        pm.setMessage("Building Tree");
        Thread t = new Thread() {
            public void run() {
                try {
                    if (selectedRoot.getChildCount() < 2){
                        int category = jTabbedPane2.getSelectedIndex();
                        switch (category) {
                        case 0:
                            currentOntology = COMPONENT_ONTOLOGY;
                            numNodes = 9091;
                            break; //component
                        case 1:
                            currentOntology = FUNCTION_ONTOLOGY;
                            numNodes = 11980;
                            break; //function
                        case 2:
                            currentOntology = PROCESS_ONTOLOGY;
                            numNodes = 239090;
                            break; //process
                        }
                        pm.setBounds(new ProgressBar.IncrementModel(0, numNodes, 0, numNodes, 1));
                        pm.start();
                        stopAlgorithm = false;
                        buildTree(currentOntology);
                        selectedTreeModel.reload(selectedRoot);
                        jTabbedPane2.setEnabled(true);
                        jButton4.setEnabled(true);
                        pm.reset();
                        pm.stop();
                        System.gc();
                    }
                    int count = 0;
                    t2 = 0;
                    if (refListUse.isSelected() && referenceList.size() > 0)
                        count = referenceList.size();
                    else
                        count = AnnotationParser.affyIDs.size();
                    pm.reset();
                    pm.setTitle("Mapping Lists");
                    pm.setMessage("Mapping Lists");
                    pm.setBounds(new ProgressBar.IncrementModel(0, count, 0, count, 1));
                    jButton4.setEnabled(false);
                    jTabbedPane2.setEnabled(false);
                    clearTree();
                    if (refListUse.isSelected() && referenceList.size() > 0)
                        mapReferenceList();
                    else
                        mapTree();
                    mapSelections();
                    computeCumulative();
                    jButton4.setEnabled(true);
                    jTabbedPane2.setEnabled(true);
                    selectedTreeModel.reload(selectedRoot);
                    selectedRoot = (DefaultMutableTreeNode) selectedTreeModel.getRoot();
                    stopAlgorithm = false;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private void addToPanel(final boolean reference, final boolean recursive) {
        DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) selectedTree.
            getLastSelectedPathComponent();
        final GoTerm selected = (GoTerm) node2.getUserObject();
        Thread t = new Thread() {
            public void run() {
                addGotermPanel(selected, reference, recursive);
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private void addGotermPanel(GoTerm selected, boolean reference, boolean recursive) {
        DSGeneMarker[] selection = null;
        if (reference){
            if (recursive)
                selection = selected.getAllRelatedAffyGenes();
            else
                selection = selected.getRelatedAffyGenes();
        }
        else {
            if (recursive)
                selection = selected.getAllSelectedAffyGenes();
            else
                selection = selected.getSelectedAffyGenes();
        }
        if (selection != null) {
            DefaultListModel ls = new DefaultListModel();
            int display = 0;

            for (DSGeneMarker marker : selection) {
                marker.setDisPlayType(display);
                if (geneListSet != null) {
                    DSGeneMarker ig = (DSGeneMarker) geneListSet.getMarkers().
                                      get(marker.getLabel());
                    if (ig != null) {
                        ig.setDisPlayType(display);
                        ls.addElement(ig);
                    }
                } else {
                    ls.addElement(marker);
                }
            }
            geneList.setModel(ls);
            jScrollPaneGenes.getViewport().add(geneList, null);

            for (DSGeneMarker marker : selection) {
                marker.setDisPlayType(display);
            }

            String minorLabel = null;
            String panelLabel = null;
            if (selection.length > 0) {
                DSPanel<DSGeneMarker> markerVector = null;
                DefaultMutableTreeNode panelNode = null;
                minorLabel = "GO Term";
                panelLabel = selected.getName();
                if (reference){
                    if (recursive)
                        panelLabel += "[All from Reference]";
                    else
                        panelLabel += "[Exact from Reference]";
                }
                else {
                    if (recursive)
                        panelLabel += "[All from Selected]";
                    else
                        panelLabel += "[Exact from Selected]";
                }
                markerVector = new CSPanel<DSGeneMarker>(panelLabel,
                        minorLabel);
                for (DSGeneMarker marker : selection) {
                    if (marker != null) {
                        String id = AnnotationParser.geneNameMap.get(marker.
                                getLabel()).get(0);
                        if (geneListSet != null) {
                            DSGeneMarker markerFromList = (DSGeneMarker)
                                    geneListSet.getMarkers().get(id);
                            if (markerFromList != null) {
                                markerVector.add(markerFromList);
                            }
                        } else {
                            DSGeneMarker mk = new CSGeneMarker(id);
                            markerVector.add(mk);
                        }
                    }
                }
                markerVector.setActive(false);
                SubpanelChangedEvent se = new SubpanelChangedEvent(
                        DSGeneMarker.class, markerVector, SubpanelChangedEvent.NEW);
                publishSubpanelChangedEvent(se);
            }
        }
    }

    private void notifyAffySelection(GoTerm selected, boolean reference, boolean recursive) throws Exception {

        DSGeneMarker[] result = null;
        if (reference) {
            if (recursive)
                result = selected.getAllRelatedAffyGenes();
            else
                result = selected.getRelatedAffyGenes();
        }
        else {
            if (recursive)
                result = selected.getAllSelectedAffyGenes();
            else
                result = selected.getSelectedAffyGenes();
        }
        if (result != null) {
            DefaultListModel ls = new DefaultListModel();

            for (DSGeneMarker marker : result) {
                ls.addElement(marker);
            }
            geneList.setModel(ls);
            jScrollPaneGenes.getViewport().add(geneList, null);
        }
        else {
            geneList.setModel(new DefaultListModel());
        }
    }

    /**
     * <code>VisualMenu</code> implementation
     * @return Component the visual representation of this plugin
     */
    public Component getComponent() {
        return mainPanel;
    }

    /**
     *this method will trigger some action,
     * when doubleclick:
     *
     * @param e
     */
    void geneList_mouseClicked(MouseEvent e) {
        int index = geneList.locationToIndex(e.getPoint());
        if (e.getClickCount() == 2) {
            if (geneList.getModel().getSize() > 0) {
                DSGeneMarker marker = (DSGeneMarker) geneList.getModel().
                    getElementAt(index);
                String id = AnnotationParser.geneNameMap.get(marker.getLabel()).get(0);
                DSGeneMarker value = null;
                if (geneListSet != null)
                    value = (DSGeneMarker)geneListSet.getMarkers().get(id);
                else {
                    value = new CSGeneMarker(id);
                }
                if (geneSelection != null) {
                    if (geneSelection.contains(value)) {
                        geneSelection.remove(value);
                        geneList.revalidate();
                        geneList.repaint();
                    }
                    else {
                        geneSelection.add(value);
                        geneList.revalidate();
                        geneList.repaint();
                        if (!panel.contains(geneSelection)) {
                            panel.panels().add(geneSelection);
                        }
                    }
                    panelModified();
                }
            }
            else {
                throwEvent(GeneSelectorEvent.MARKER_SELECTION);
            }
        }
    }

    void panelModified() {
        throwEvent(GeneSelectorEvent.PANEL_SELECTION);
    }

    void throwEvent(int type) {
        GeneSelectorEvent event = null;
        switch (type) {
            case GeneSelectorEvent.PANEL_SELECTION:
                event = new GeneSelectorEvent(panel);
                break;
            case GeneSelectorEvent.MARKER_SELECTION:
                int index = this.geneList.getSelectedIndex();
                Object valueObject = geneList.getModel().getElementAt(index);
                if (valueObject instanceof DSGeneMarker) {
                    DSGeneMarker value = (DSGeneMarker) valueObject;
                    event = new GeneSelectorEvent(value);
                }
                break;
        }
        publishGeneSelectorEvent(event);
    }

    /**
     * implemented from projectlistener,
     * @param projectEvent
     */
    @Subscribe(Asynchronous.class) public void receive(ProjectEvent projectEvent, Object publisher) {
        if (projectEvent != null) {
            DSDataSet dataFile = projectEvent.getDataSet();
            if (dataFile instanceof CSExprMicroarraySet) {
                notifyMAChange(dataFile);
            }
        }
    }

        boolean  stopAlgorithm = false;

        public void update(java.util.Observable ob, Object o) {
        stopAlgorithm = true;
    }

    void notifyMAChange(DSDataSet gl) {
        if ( (gl != null) && (gl != geneListSet) && gl instanceof DSMicroarraySet) {
            geneListSet = (DSMicroarraySet)gl;
            chipType = geneListSet.getCompatibilityLabel();
            jChipTypeComboBox.setSelectedItem(chipType);
            panel = CSMarkerManager.getMarkerPanel(geneListSet);
            if (panel == null) {
                panel = new CSPanel<DSGeneMarker>("Selection");
                CSMarkerManager.setMarkerPanel(geneListSet, panel);
                panel.clear();
                panel.panels().add(geneSelection);
            }
            geneSelection = panel.panels().get("");
        }
    }

    private void clearTree(){
        if (stopAlgorithm){
            return;
        }
        t2 = 0;
        pm.reset();
        pm.setBounds(new ProgressBar.IncrementModel(0, numNodes, 0, numNodes, 1));
        pm.setMessage("Clearing Mappings");
        pm.start();
        Enumeration en2 = selectedRoot.depthFirstEnumeration();
        while (en2.hasMoreElements()){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en2.
                                          nextElement();
            if (node.isLeaf())
                pm.updateTo(t2++);
            if (stopAlgorithm){
                return;
            }
            GoTerm currentTerm = (GoTerm) node.getUserObject();
            if (currentTerm != null){
                currentTerm.reset();
            }
        }
        pm.stop();
    }

    private void mapReferenceList(){
        if (stopAlgorithm){
            return;
        }
        t2 = 0;
        pm.setTitle("Mapping Reference List");
        pm.setMessage("Mapping Reference List");
        pm.start();
        Enumeration en2 = selectedRoot.depthFirstEnumeration();
        HashMap affyhash = AnnotationParser.getGotable();
        if (affyhash != null && referenceList.size() > 0) {
            while (en2.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en2.
                                              nextElement();
                pm.updateTo(t2++);
                if (stopAlgorithm){
                    return;
                }
                GoTerm currentTerm = (GoTerm) node.getUserObject();
                if (currentTerm != null) {
                    //currentTerm.clear();
                    Vector<String> ids = (Vector<String>) affyhash.get(currentTerm.getId());
                    currentTerm.addGeneNameIfInList(ids, referenceList);
                    if (currentTerm.hasAlternateIds()) {
                        String[] gids = currentTerm.getAlternateIds();
                        for (String gid : gids) {
                            ids = (Vector<String>) affyhash.get(gid);
                            if (ids != null) {
                                for (String aid : ids){
                                    if (referenceList.contains(aid.trim())){
                                        currentTerm.addGeneName(aid.trim());
                                        if (stopAlgorithm)
                                            return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        pm.stop();
    }

    /**
     * Populates GO Tree with corresponding Affy Ids at every node in the tree
     */
    private void mapTree(){
        if (stopAlgorithm){
            return;
        }
        t2 = 0;
        pm.updateTo(t2);
        pm.setMessage("Mapping Tree");
        pm.setTitle("Mapping Tree");
        pm.start();
        Enumeration en2 = selectedRoot.depthFirstEnumeration();
        HashMap affyhash=AnnotationParser.getGotable();
        if (affyhash != null) {
            while (en2.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en2.
                                              nextElement();
                if (node.isLeaf())
                    pm.updateTo(t2++);
                if (stopAlgorithm){
                    return;
                }
                GoTerm currentTerm = (GoTerm) node.getUserObject();
                if (currentTerm != null){
                    Vector<String> ids = (Vector<String>) affyhash.get(currentTerm.getId());
                    currentTerm.addGeneNamesFromList(ids);
                    if (currentTerm.hasAlternateIds()) {
                        String[] gids = currentTerm.getAlternateIds();
                        for (String gid : gids) {
                            ids = (Vector<String>) affyhash.get(gid);
                            if (ids != null) {
                                for (String aid : ids){
                                    currentTerm.addGeneName(aid.trim());
                                    if (stopAlgorithm)
                                        return;
                                }
                            }
                        }
                    }
                }
            }
        }
        pm.stop();
    }

    void computeCumulative(){
        if (stopAlgorithm){
            return;
        }
        t2 = 0;
        pm.reset();
        pm.setTitle("Computing Cumulative Mappings");
        pm.setMessage("Computing Cumulative Mappings");
        pm.setBounds(new ProgressBar.IncrementModel(0, numNodes, 0, numNodes, 1));
        pm.start();
        if (stopAlgorithm){
            return;
        }
        computeCumulative(selectedRoot);
        pm.stop();
    }

    Vector<String>[] computeCumulative(DefaultMutableTreeNode node){
        if (node.isLeaf()){
            pm.updateTo(t2++);
            return new Vector[]{((GoTerm) node.getUserObject()).getDescendants(),((GoTerm) node.getUserObject()).getDescendantSelectedList()};
        }
        Enumeration children = node.children();
        while (children.hasMoreElements()){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
            if (stopAlgorithm){
                break;
            }
            Vector<String>[] lists = computeCumulative(child);
            ((GoTerm)node.getUserObject()).addDescendantIDs(lists[0]);
            ((GoTerm)node.getUserObject()).addDescendantSelectedList(lists[1]);
            if (stopAlgorithm){
                break;
            }
        }
        return new Vector[]{((GoTerm) node.getUserObject()).getDescendants(),((GoTerm) node.getUserObject()).getDescendantSelectedList()};
    }

    int numNodes = 0;

    /**
     * Build a Tree representation of appropriate GO category
     * @param type GO category for which tree is to be created
     */
    public void buildTree(int type) throws IOException {
        if (stopAlgorithm){
            return;
        }
        if (type != 0){
            String tmpDir = System.getProperty("temporary.files.directory");
            File treeFile = null;
            String data = null;
            switch (type){
                case PROCESS_ONTOLOGY:
                    data = PROCESS_ONTOLOGY_FILE;
                    treeFile = new File(tmpDir + File.separator + PROCESS_ONTOLOGY_FILE + ".tree");
                    break;
                case FUNCTION_ONTOLOGY:
                    data = FUNCTION_ONTOLOGY_FILE;
                    treeFile = new File(tmpDir + File.separator + FUNCTION_ONTOLOGY_FILE + ".tree");
                    break;
                case COMPONENT_ONTOLOGY:
                    data = COMPONENT_ONTOLOGY_FILE;
                    treeFile = new File(tmpDir + File.separator + COMPONENT_ONTOLOGY_FILE + ".tree");
                    break;
            }
            if (treeFile != null && treeFile.exists()){
                pm.reset();
                pm.setMessage("Reading Treefile");
                pm.setTitle("Reading Treefile");
                pm.setBounds(new ProgressBar.IncrementModel(0, 1, 0, 1, 1));
                pm.start();
                try {
                    ObjectInputStream ob = new ObjectInputStream(new
                        FileInputStream(treeFile.getAbsolutePath()));
                    selectedTreeModel = (DefaultTreeModel) ob.readObject();
                    ob.close();
                    switch (type) {
                       case PROCESS_ONTOLOGY :
                           goTreeModelProcess = selectedTreeModel;
                           jGoTreeProcess = new JTree(goTreeModelProcess);
                           goRootProcess = (DefaultMutableTreeNode)goTreeModelProcess.getRoot();
                           pm.updateTo(++t2);
                           break;
                       case FUNCTION_ONTOLOGY :
                           goTreeModelFunction = selectedTreeModel;
                           jGoTreeFunction = new JTree(goTreeModelFunction);
                           goRootFunction = (DefaultMutableTreeNode)goTreeModelFunction.getRoot();
                           pm.updateTo(++t2);
                           break;
                       case COMPONENT_ONTOLOGY :
                           goTreeModelComponent = selectedTreeModel;
                           jGoTreeComponent = new JTree(goTreeModelComponent);
                           goRootComponent = (DefaultMutableTreeNode)goTreeModelComponent.getRoot();
                           pm.updateTo(++t2);
                           break;
                       }
                       if (stopAlgorithm){
                           return;
                       }
                    selectedRoot.removeAllChildren();
                    selectedTreeModel.reload(selectedRoot);
                    selectedTree.setModel(selectedTreeModel);
                    selectedRoot = (DefaultMutableTreeNode)selectedTreeModel.getRoot();
                    getComponent().validate();
                    pm.updateTo(++t2);
                    pm.stop();
                    return;
                }
                catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                catch (IOException ex) {
                    if (treeFile.delete())
                        System.out.println("Cached Treefile deleted: " + treeFile.getAbsolutePath());
                    ex.printStackTrace();
                }
            }
            pm.reset();
            pm.setMessage("Building Tree");
            pm.setTitle("Building Tree");
            pm.setBounds(new ProgressBar.IncrementModel(0, numNodes, 0, numNodes, 1));
            pm.start();
            if (stopAlgorithm){
                return;
            }
            Reader reader = null;
            InputStream input = GOPanel.class.getResourceAsStream(data);
            if (input == null){
                String url = System.getProperty("data.download.site");
                if (url.endsWith("/"))
                    url += data;
                else
                    url += "/" + data;
                URL resource = new URL(url);
                input = resource.openStream();
            }
            reader = new InputStreamReader(input);
            selectedRoot.removeAllChildren();
            selectedTreeModel.reload(selectedRoot);
            selectedRoot = (DefaultMutableTreeNode)selectedTreeModel.getRoot();
            DefaultMutableTreeNode node = selectedRoot;
            Stack st = new Stack();
            BufferedReader br = null;
            String line = null;
            try {
                br = new BufferedReader(reader);
                line = br.readLine();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
            HashMap hash = new HashMap();
            t2= 0;
            while ((line != null)) {
                if (stopAlgorithm){
                    treeFile.delete();
                    return;
                }
                pm.updateTo(t2++);
                if (line.charAt(0) == ' ') {
                    int indents = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) != ' ') {
                            indents = i;
                            break;
                        }
                    }
                    String[] goes = line.split("[%<$]");
                    String[] cols = goes[1].trim().split(" ; ");
                    String golabel = cols[0].trim();

                    GoTerm currentgo = new GoTerm();

                    if (cols[1].trim().indexOf(",") != -1) {
                        String[] goIds = cols[1].trim().split(",");
                        currentgo.setId(goIds[0].trim());
                        for (int i = 1; i < goIds.length; i++)
                            currentgo.addAlternateId(goIds[i].trim());
                    } else
                        currentgo.setId(cols[1].trim());
                    currentgo.setName(golabel.trim());

                    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
                    currentNode.setUserObject(currentgo);
                    DefaultMutableTreeNode go = new DefaultMutableTreeNode();
                    int previousIndents = 0;
                    if (!st.empty()) {
                        go = (DefaultMutableTreeNode) st.peek();
                        String nom = (String) hash.get(go);
                        previousIndents = Integer.parseInt(nom);
                    } while ((indents <= previousIndents) && (!st.empty())) {
                        go = (DefaultMutableTreeNode) st.pop();
                        String nom = (String) hash.get(go);
                        previousIndents = Integer.parseInt(nom);
                    }
                    if (previousIndents == 0) {
                        node.setUserObject(currentgo);
                        st.push(node);
                        hash.put(node, indents + "");
                    } else {
                        selectedTreeModel.insertNodeInto(currentNode, go,
                                go.getChildCount());
                        if (indents <= 2) {
                            selectedTree.scrollPathToVisible(new TreePath(currentNode.
                                    getPath()));

                        }
                        st.push(go);
                        st.push(currentNode);
                        hash.put(currentNode, indents + "");
                    }
                }
                try {
                    line = br.readLine();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            pm.stop();
            t2 = 0;
            pm.reset();
            pm.setTitle("Persisting Tree");
            pm.setMessage("Persisting Tree");
            pm.setBounds(new ProgressBar.IncrementModel(0, 1, 0, 1, 1));
            if (!treeFile.exists()){
                try {
                    ObjectOutputStream ob = new ObjectOutputStream(new
                        FileOutputStream(treeFile.getAbsolutePath()));
                    ob.writeObject(selectedTreeModel);
                    pm.updateTo(++t2);
                    ob.flush();
                    ob.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    void jChipTypeComboBox_actionPerformed(ActionEvent e) {
        String chiptype = (String) jChipTypeComboBox.getSelectedItem();
        if (AnnotationParser.getChipType() != null && !AnnotationParser.getChipType().equals(chipType))
            AnnotationParser.setChipType(chiptype);
        ( (DefaultListModel) geneList.getModel()).removeAllElements();
        geneList.revalidate();
        geneList.repaint();
    }

    /**
     * geneSelectorAction
     *
     * @param e GeneSelectorEvent
     */
    @Subscribe public void receive(GeneSelectorEvent e, Object publisher) {
        if (publisher != this){
            if (e.getPanel() != null)
                panels = e.getPanel();
        }
    }

    DSPanel<DSGeneMarker> panels = null;

    private void mapSelections(){
        try {
            if (panels != null) {
                hash.clear();
                boolean panelsExist = false;
                for (DSPanel<DSGeneMarker> panel : panels.panels()) {
                    panelsExist = true;
                    if (panel.isActive()) {
                        for (DSGeneMarker marker : panel) {
                            hash.add(marker.getLabel());
                            if (stopAlgorithm)
                                return;
                        }
                    }
                }
                if (panelsExist){
                    spinnerModel1.setMaximum(new Integer(hash.size()));
                    rebuildTreeAndTable();
                }
            }
        }
        finally {
        }
    }

    @Publish public GeneSelectorEvent publishGeneSelectorEvent(GeneSelectorEvent event) {
        return event;
    }

    @Publish public SubpanelChangedEvent publishSubpanelChangedEvent(SubpanelChangedEvent event) {
        return event;
    }

    private void rebuildTreeAndTable(){
        if (stopAlgorithm){
            return;
        }
        t2 = 0;
        pm.reset();
        pm.setTitle("Rebuilding Tree and Table");
        pm.setMessage("Rebuilding Tree and Table");
        pm.setBounds(new ProgressBar.IncrementModel(0, numNodes, 0, numNodes, 1));
        pm.start();

        totalSelected = getSelectedUniqueGenesCount();
        totalGenes = getTotalUniqueGenesCount();

        //this will map all the activated genes and show in tree and table view.
        currentData.clear();
        goNodes.clear();
        mapNode(selectedRoot, currentData);
        if (bonferroniCorrection){
            for (Iterator it = currentData.iterator(); it.hasNext();){
                GoDataElement gde = (GoDataElement)it.next();
                if (gde.enriched.trim().equals("DN") || gde.go.getRelatedNo() < threshold) {
                    it.remove();
                }
                if (stopAlgorithm){
                    return;
                }
            }
            computeCorrectedPValues(currentData);
        }
        rebuildTable();
        selectedTreeModel.reload(selectedRoot);
        pm.stop();
    }

    Vector<GoDataElement> componentData = new Vector<GoDataElement>();
    Vector<GoDataElement> functionData = new Vector<GoDataElement>();
    Vector<GoDataElement> processData = new Vector<GoDataElement>();
    Vector<GoDataElement> currentData = componentData;

    void rebuildTable(){
        if (selectedRoot.getUserObject() != null){
            Object[] keys = currentData.toArray();
            Arrays.sort(keys); //sort data according to the chi square value
            Object[] all = new Object[keys.length + 1];
            int k = 1;
            for (int i = keys.length - 1; i >= 0; i--) {
                all[k++] = keys[i];
            }
            all[0] = new GoDataElement((GoTerm) selectedRoot.getUserObject());
            rt = new ReportTableModel(all);
            reportTable.setModel(new DefaultTableModel());
            reportTable.setModel(rt);
        }
    }

    void jRadioButton1_stateChanged(ChangeEvent ce) {
        chiSquaredOrHypergeometric = (jRadioButton1.isSelected());
    }

    void jCheckBox1_actionPerformed(ActionEvent e){
        bonferroniCorrection = jCheckBox1.isSelected();
    }

    void jSpinner1_stateChanged(ChangeEvent ce){
        threshold = (Integer)jSpinner1.getValue();
    }

    void recompute_actionPerformed(ActionEvent e){
        if (selectedRoot.getUserObject() != null)
            rebuildTreeAndTable();
    }

    void saveProfiles_actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser(".");
        String psFilename = null;
        FileFilter filter = new PValueFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Save P-Values");
        String extension = ( (PValueFileFilter) filter).getExtension();
        int choice = fc.showSaveDialog(getComponent());
        if (choice == JFileChooser.APPROVE_OPTION) {
          psFilename = fc.getSelectedFile().getAbsolutePath();
          if (!psFilename.endsWith(extension)) {
            psFilename += extension;
          }
          serialize(psFilename);
      }
    }

    private Hashtable<String, Stack> computePValueTrends(){
        int rows = rt.getRowCount();
        Hashtable<String, Stack> pValues = new Hashtable<String, Stack>();
        rows = rt.getRowCount();
        for (int i = 0; i < rows; i++){
            pValues.put((String)rt.getValueAt(i, 1), new Stack<PValueItem>());
        }
        for (int i = 0; i < rows; i++){
            boolean profileToBeUsed = false;
            int pos = 0;
            int count = 0;
            String name = (String)rt.getValueAt(i, 1);
            GoTerm go = goNodes.get(name);
            if (go.isEnabled()){
                for (String affyid : hash) {
                    Vector selectedList = go.getSelectedList();
                    double pv = 0d;
                    Stack<PValueItem> v = pValues.get(name);
                    if (go.getSelected() > 0 && selectedList.contains(AnnotationParser.getGeneName(affyid).trim())) {
                        count++;
                    }
                    if (pos % stepSize == 0) {
                        pv = getPValue(totalGenes, go.getRelatedNo(), pos + 1, count);
                        v.push(new PValueItem(pos, pv));
                        if (!profileToBeUsed && pv < pValueThreshold) {
                            profileToBeUsed = true;
                        }
                    }
                    pos++;
                }
                if (!profileToBeUsed)
                    pValues.remove(name);
            }
        }
        return pValues;
    }

    private void serialize(String filename){
        try {
            Hashtable<String, Stack> pValues = computePValueTrends();
            System.gc();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new
                File(filename)));
            String line = null;
            for (Enumeration keys = pValues.keys(); keys.hasMoreElements(); ){
                Object key = keys.nextElement();
                Stack<PValueItem> v = (Stack<PValueItem>)pValues.get(key);
                line = key.toString();
                for (PValueItem val : v){
                    if (!val.toString().equals(""))
                        line += "\t" + val.toString();
                    else
                        line += "\t" + 1.0d;
                }
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (IOException ioe){ioe.printStackTrace();}
    }

    int totalSelected = 0;
    int totalGenes = 0;

    void mapList_actionPerformed(ActionEvent e){
        jTabbedPane2.setEnabled(false);
        jButton4.setEnabled(false);
        buildAndMapTree();
    }

    void refList_actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser(".");
        String psFilename = null;
        fc.setDialogTitle("Open Reference List");
        int choice = fc.showOpenDialog(getComponent());
        if (choice == JFileChooser.APPROVE_OPTION) {
          psFilename = fc.getSelectedFile().getAbsolutePath();
          setReferenceList(psFilename);
      }
    }

    void setReferenceList(String filename){
        try {
            FileReader reader = new FileReader(new File(filename));
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            referenceList.clear();
            while ((line = br.readLine()) != null){
                referenceList.add(line.trim());
            }
        }
        catch (IOException ioe){ioe.printStackTrace();}
    }

    /**
     * Should be called after mapNode
     * @param vec Vector
     */
    private void computeCorrectedPValues(Vector vec){
        int countAboveThreshold = 0;
        for (Enumeration e = vec.elements(); e.hasMoreElements();){
            GoDataElement gde = (GoDataElement)e.nextElement();
            if (gde.go.getRelatedNo() >= threshold)
                countAboveThreshold++;
        }
        for (Enumeration e = vec.elements(); e.hasMoreElements();){
            GoDataElement goData = (GoDataElement)e.nextElement();
            goData.correctedPValue = 1 - Math.pow((1 - goData.pvalue), countAboveThreshold);
        }
    }

    private int getTotalUniqueGenesCount(){
        Vector<String> geneNames = new Vector<String>();
        if (refListUse.isSelected() && referenceList.size() > 0){
            for (String affyid : referenceList){
                String geneName = AnnotationParser.getGeneName(affyid.trim());
                if (geneName != null && !geneNames.contains(geneName))
                    geneNames.add(geneName);
            }
        }
        else {
            Set<String> affyids = AnnotationParser.affyIDs.keySet();
            for (String affyid : affyids){
                String geneName = AnnotationParser.getGeneName(affyid.trim());
                if (geneName != null && !geneNames.contains(geneName))
                    geneNames.add(geneName);
            }
        }
        return geneNames.size();
    }

    private int getSelectedUniqueGenesCount(){
        Vector<String> geneNames = new Vector<String>();
        if (hash.size() > 0) {
            if (refListUse.isSelected() && referenceList.size() > 0) {
                for (String affyid : hash) {
                    String geneName = AnnotationParser.getGeneName(affyid.trim());
                    if (geneName != null && !geneNames.contains(geneName) && referenceList.contains(affyid))
                        geneNames.add(geneName);
                }
            } else {
                Set<String> affyids = AnnotationParser.affyIDs.keySet();
                for (String affyid : hash) {
                    String geneName = AnnotationParser.getGeneName(affyid.trim());
                    if (geneName != null && !geneNames.contains(geneName) && affyids.contains(affyid))
                        geneNames.add(geneName);
                }
            }
        }
        return geneNames.size();
    }

    private void mapNode(DefaultMutableTreeNode node, Vector vec) {
        pm.updateTo(t2++);
        if (stopAlgorithm){
            return;
        }
        GoTerm go = (GoTerm) node.getUserObject();
        if (go != null && !goNodes.keySet().contains(go.getName())) {
            goNodes.put(go.getName(), go);
            int count = 0;
            Vector<String> aids = (Vector<String>)AnnotationParser.getGotable().get(go.getId());
            if (aids != null) {
                for (String aid : aids) {
                    if (refListUse.isSelected() && referenceList.size() > 0){
                        if (hash.contains(aid.trim()) && referenceList.contains(aid.trim())) {
                            go.addSelected(aid.trim());
                            count++;
                        }
                    }
                    else {
                        if (hash.contains(aid.trim())) {
                            go.addSelected(aid.trim());
                            count++;
                        }
                    }
                }
                if (count > 0) {
                    if (!node.isRoot()) {
                        int s_in = go.getDescendantSelectedListCount();
                        if (s_in > 0){
                            int s_all = totalSelected;
                            int all_in = go.getDescendantCount();
                            int all = totalGenes;
                            int x1 = s_in;
                            int x2 = s_all - s_in;
                            double ratio = (double) all_in / (double) all;
                            double exp_x1 = (double) s_all * ratio;
                            double exp_x2 = (double) s_all - exp_x1;
                            double chi = (x1 - exp_x1) * (x1 - exp_x1) / exp_x1 +
                                         (x2 - exp_x2) * (x2 - exp_x2) / exp_x2;
                            double pvalue = getPValue(all, all_in, s_all, s_in);
                            String enriched = "UP";
                            if (x1 < exp_x1) {
                                enriched = "DN";
                            }
                            GoDataElement godata = new GoDataElement(go);
                            godata.chi = chi;
                            godata.enriched = enriched;
                            godata.pvalue = pvalue;
                            vec.add(godata);
                        }
                    }
                    else {
                    }
                }
            }
        }
        for (int k = 0; k < node.getChildCount(); k++) {
            mapNode( (DefaultMutableTreeNode) node.getChildAt(k), vec);
        }
    }

    double getPValue(int all, int all_in, int s_all, int s_in){
        double pvalue = 0;
        if (chiSquaredOrHypergeometric) {
            int x1 = s_in;
            int x2 = s_all - s_in;
            double ratio = (double) all_in / (double) all;
            double exp_x1 = (double) s_all * ratio;
            double exp_x2 = (double) s_all - exp_x1;
            double chi = (x1 - exp_x1) * (x1 - exp_x1) / exp_x1 +
                         (x2 - exp_x2) * (x2 - exp_x2) / exp_x2;
            ChiSquareDistribution cd = new
                    ChiSquareDistribution(1);
            pvalue = 1.0 - cd.getCDF(chi);
        } else {
            pvalue = computeHypergeometric(all, all_in, s_all, s_in);
        }
        return pvalue;
    }

    double computeHypergeometric(int N, int M, int n, int x){
       double cdf = 0d;
       for (int i = x; i < x + 3; i++){
           if (i < n && n <= N && i <= M && (n - i) <= (N - M)){
               double num1 = LogStats.logBinomialCoeff(M, i);
               double num2 = LogStats.logBinomialCoeff(N - M, n - i);
               double den = LogStats.logBinomialCoeff(N, n);
               cdf += Math.exp(num1 + num2 - den);
           }
       }
       return cdf;
    }

    void display_actionPerformed(ActionEvent e) {
        ( (DefaultListModel) geneList.getModel()).removeAllElements();
        geneList.revalidate();
        geneList.repaint();
    }
    /**
     * <code>FileFilter</code> that is used by the <code>JFileChoose</code> to
     * show just panel set files on the filesystem
     */
    class PValueFileFilter extends FileFilter{
      String fileExt;
      PValueFileFilter(){
        fileExt = ".mps";
      }

      public String getExtension(){
        return fileExt;
      }

      public String getDescription() {
        return "P-Value Files";
      }

      public boolean accept(File f) {
        boolean returnVal = false;
        if(f.isDirectory() || f.getName().endsWith(fileExt)) {
          return true;
        }
        return returnVal;
      }
    }

    /**
     * <code>FileFilter</code> that is used by the <code>JFileChoose</code> to
     * show just panel set files on the filesystem
     */
    class CategoryFileFilter extends FileFilter{
      String fileExt;
      CategoryFileFilter(){
        fileExt = ".dat";
      }

      public String getExtension(){
        return fileExt;
      }

      public String getDescription() {
        return "GO Category Files";
      }

      public boolean accept(File f) {
        boolean returnVal = false;
        if(f.isDirectory() || f.getName().endsWith(fileExt)) {
          return true;
        }
        return returnVal;
      }
    }

    class PValueItem extends XYDataItem {
        public PValueItem(Number x, Number y) {
            super(x, y);
        }

        public PValueItem(double x, double y) {
            super(x, y);
        }

    }

    class PValueSliderUI extends MetalSliderUI {
        public void paintThumb(Graphics g) {
            Rectangle knobBounds = thumbRect;
            int h = knobBounds.height;
            int w = knobBounds.width;
            g.translate(knobBounds.x, knobBounds.y);

            if ( slider.isEnabled() ) {
                g.setColor(slider.getBackground());
            }
            else {
                g.setColor(slider.getBackground().darker());
            }

            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                horizThumbIcon.paintIcon(slider, g, 0, 0);
            } else {
                ((Graphics2D) g).rotate( -Math.PI,
                                         w / 2,
                                         h / 2);

                int cw = h / 2;
                g.fillRect(0, 0, w-1-cw, h-1);
                Polygon p = new Polygon();
                p.addPoint(w-cw-1, 0);
                p.addPoint(w-1, cw);
                p.addPoint(w-1-cw, h-2);
                g.fillPolygon(p);

                g.setColor(Color.black);
                g.drawLine(0, 0, 0, h - 2);                  // left
                g.drawLine(1, 0, w-1-cw, 0);                 // top
                g.drawLine(w-cw-1, 0, w-1, cw);              // top slant
                g.drawLine(1, 0, 1, h-1);                    // right
                g.drawLine(0, h-1, w-2-cw, h-1);             // bottom
                g.drawLine(w-1-cw, h-1, w-1, h-1-cw);        // bottom slant

                ((Graphics2D) g).rotate( Math.PI,
                                         w / 2,
                                         h / 2);
            }

            g.translate( -knobBounds.x, -knobBounds.y);
        }
    }

    boolean sortAscending = true;
    int sortColumn = 0;

    class ColumnListener extends MouseAdapter{
      public ColumnListener() {
      }

      public void mouseClicked(MouseEvent e) {
        TableColumnModel colModel =
                      reportTable.getColumnModel();
        int columnModelIndex =
          colModel.getColumnIndexAtX(e.getX());
        int modelIndex =
             colModel.getColumn(
               columnModelIndex).getModelIndex();

        if (modelIndex < 0)
          return;
        if (sortColumn==modelIndex)
          sortAscending = !sortAscending;
        else
          sortColumn = modelIndex;

        for (int i=0; i < colModel.getColumnCount(); i++) {
          TableColumn column = colModel.getColumn(i);
          column.setHeaderValue(reportTable.getColumnName(column.getModelIndex()));
        }
        reportTable.getTableHeader().repaint();

        Arrays.sort(rt.data, new GoDataElementComparator(modelIndex, sortAscending));
        reportTable.tableChanged(new TableModelEvent(rt));
        reportTable.repaint();
      }
    }

    class GoDataElementComparator implements Comparator{
      protected int     m_sortcol;
      protected boolean m_sortasc;

      public GoDataElementComparator(int sortcol, boolean sortasc) {
        m_sortcol = sortcol;
        m_sortasc = sortasc;
      }

      public int compare(Object o1, Object o2) {
        if(!(o1 instanceof GoDataElement) || !(o2 instanceof GoDataElement))
          return 0;
        GoDataElement s1 = (GoDataElement)o1;
        GoDataElement s2 = (GoDataElement)o2;
        int result = 0;
        int i1, i2;
        double d1,d2;
        switch (m_sortcol) {
          case 0:    // symbol
            String str1 =
               (String)s1.go.getName();
            String str2 =
               (String)s2.go.getName();
            result = str1.compareTo(str2);
            break;
          case 1:    // enriched
            result =
             s1.enriched.compareTo(s2.enriched);
            break;
          case 2:    // selected
            i1 = s1.go.getSelected();
            i2 = s2.go.getSelected();
            result = i1<i2 ? -1 : (i1>i2 ? 1 : 0);
            break;
          case 3:    // related
            i1 = s1.go.getRelatedNo();
            i2 = s2.go.getRelatedNo();
            result = i1<i2 ? -1 : (i1>i2 ? 1 : 0);
            break;
          case 4:    // p-value
            d1 = s1.pvalue;
            d2 = s2.pvalue;
            result = d1<d2 ? -1 : (d1>d2 ? 1 : 0);
            break;
          case 5:    // corrected p-value
            d1 = s1.correctedPValue;
            d2 = s2.correctedPValue;
            result = d1<d2 ? -1 : (d1>d2 ? 1 : 0);
            break;
        }
        if (!m_sortasc)
          result = -result;
        return result;
      }

      public boolean equals(Object obj) {
        if (obj instanceof GoDataElementComparator) {
          GoDataElementComparator compobj = (GoDataElementComparator)obj;
          return (compobj.m_sortcol==m_sortcol) && (compobj.m_sortasc==m_sortasc);
        }
        return false;
      }
  }
}
