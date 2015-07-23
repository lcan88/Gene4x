package org.geworkbench.components.selectors;

import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.CSMarkerManager;
import org.geworkbench.bison.util.FileUtil;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectSelection;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.MarkerSelectedEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.visualproperties.PanelVisualProperties;
import org.geworkbench.util.visualproperties.PanelVisualPropertiesManager;
import org.geworkbench.util.visualproperties.VisualPropertiesDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 *          This class is a template to illustrate the requirements to build a component
 *          that subscribes to a given Interface. The interface MUST end in "Subscriber"
 *          to be automatically handled by the Plug&Play framework. For instance, in this
 *          case, the interface is IMarkerIdChangeSubscriber.
 */

public class GenericMarkerSelectorPanel implements VisualPlugin, MenuListener {
    private HashMap listeners = new HashMap();
    private JPanel mainPanel = new JPanel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JScrollPane jScrollPane2 = new JScrollPane();
    private JList geneList = new JList();
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private DefaultMutableTreeNode defaultPanel = null; //what's the use of this???
    private DefaultTreeModel panelTreeModel = new DefaultTreeModel(root);
    private JTree panelTree = new JTree(panelTreeModel);
    private JPopupMenu jGenePopup = new JPopupMenu();
    private JMenuItem jAddToPanel = new JMenuItem();
    private TreeSelectionModel panelTreeSelection = null;
    // This points to the selected genes. It is obtained and stored
    // by calling getObject("GenePanel") on the dataset
    private DSPanel<DSGeneMarker> markerPanel = null; //new CSPanel<DSMarker>("Selection");
    private DSItemList<DSGeneMarker> markerSet = null;
    // a specific subpanel of selectionPanel with single selections from the list
    private DSMicroarraySet maSet;

    private DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                if (markerPanel.getSelection().contains(value)) {
                    c.setBackground(Color.yellow);
                }
            } else {
                if (markerPanel.getSelection().contains(value)) {
                    c.setBackground(Color.orange);
                } else {
                    c.setBackground(Color.white);
                }
            }
            return c;
        }
    };

    private ListModel markerListModel = new AbstractListModel() {
        public int getSize() {
            if (markerSet == null) {
                return 0;
            }
            int n = markerSet.size();
            return n;
        }

        public Object getElementAt(int index) {
            if ((markerSet == null) || (index < 0)) {
                return null;
            }
            return markerSet.get(index);
        }
    };

    private DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer() {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (sel) {
                Object object = node.getUserObject();
                if ((markerPanel != null) && (object != null) && (object instanceof DSPanel)) {
                    if (markerPanel.panels().contains((DSPanel) node.getUserObject())) {
                        c.setForeground(Color.yellow);
                    }
                }
            } else {
                Object object = node.getUserObject();
                if ((markerPanel != null) && (object != null) && (object instanceof DSPanel)) {
                    if (markerPanel.panels().contains((DSPanel) object)) {
                        c.setForeground(Color.blue);
                    } else {
                        c.setForeground(Color.black);
                    }
                } else {
                    c.setForeground(Color.black);
                }
            }
            return c;
        }
    };

    JPopupMenu jPanelMenu = new JPopupMenu();
    JMenuItem jActivateItem = new JMenuItem();
    JMenuItem jDeactivateItem = new JMenuItem();
    JMenuItem jDeleteItem = new JMenuItem();
    JMenuItem jMergeItem = new JMenuItem();
    JMenuItem jRenameItem = new JMenuItem();
    JMenuItem jMenuItem1 = new JMenuItem();
    JMenuItem viewPathway = new JMenuItem();
    JMenuItem viewPathway1 = new JMenuItem();
    JMenuItem printItem = new JMenuItem();
    JMenuItem exportItem = new JMenuItem();
    JMenuItem visualPropertiesItem = new JMenuItem();
    JTextField markerBox = new JTextField();
    JToggleButton jToggleButton1 = new JToggleButton();
    JToolBar jToolBar1 = new JToolBar();
    JButton jButton1 = new JButton();
    Component component1;
    JButton jButton2 = new JButton();
    MouseListener panelTreeListener = null;
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JSplitPane jSplitPane1 = new JSplitPane();
    GridBagLayout gridBagLayout2 = new GridBagLayout();

    JButton btnLoadList = new JButton();
    Component component2 = Box.createHorizontalStrut(8);

    /**
     * Standard constructor
     */
    public GenericMarkerSelectorPanel() {
        //geneSelection.setActive(true);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the component name that gets displayed in the Tabbed interface
     *
     * @return a string with the component name
     */
    public String getComponentName() {
        return "Gene List";
    }

    /**
     * Default initialization method
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        panelTreeSelection = panelTree.getSelectionModel();
        component1 = Box.createHorizontalStrut(8);
        mainPanel.setLayout(borderLayout1);
        jAddToPanel.setText("Add to Set");

        ActionListener listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jAddToPanel_actionPerformed(e);
            }
        };
        listeners.put("Commands.Panels.Add to Set", listener);
        jAddToPanel.addActionListener(listener);

        geneList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                jList1_mouseReleased(e);
                super.mouseReleased(e);
            }

            public void mouseClicked(MouseEvent e) {
                geneList_mouseClicked(e);
                super.mouseReleased(e);
            }
        });

        panelTreeListener = new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                panelTree_mouseReleased(e);
            }
        };

        panelTree.addMouseListener(panelTreeListener);

        printItem.setText("Print");
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printItem_actionPerformed(e);
            }
        };
        printItem.addActionListener(listener);

        exportItem.setText("Export");
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportItem_actionPerformed(e);
            }
        };
        exportItem.addActionListener(listener);

        visualPropertiesItem.setText("Change Visual Properties");
        visualPropertiesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visualPropertiesItem_actionPerformed(e);
            }
        });

        jActivateItem.setText("Activate");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jActivateItem_actionPerformed(e);
            }
        };
        listeners.put("Commands.Panels.Activate", listener);
        jActivateItem.addActionListener(listener);

        jDeactivateItem.setText("Deactivate");
        jDeleteItem.setText("Delete");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDeleteItem_actionPerformed(e);
            }
        };
        listeners.put("Commands.Panels.Delete", listener);
        jDeleteItem.addActionListener(listener);

        jMergeItem.setText("Merge");
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMergeItem_actionPerformed(e);
            }
        };
        //    listeners.put("Commands.Panels.Merge", listener);
        jMergeItem.addActionListener(listener);

        panelTree.setEditable(false);
        jRenameItem.setText("Rename");

        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRenameItem_actionPerformed(e);
            }
        };
        listeners.put("Commands.Panels.Rename", listener);
        jRenameItem.addActionListener(listener);

        jMenuItem1.setText("Clear Selection");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem1_actionPerformed(e);
            }
        };
        listeners.put("View.Clear Selection", listener);
        jMenuItem1.addActionListener(listener);

        jToggleButton1.setText("Find Next");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToggleButton1_actionPerformed(e);
            }
        });
        markerBox.setMinimumSize(new Dimension(100, 20));
        markerBox.setPreferredSize(new Dimension(200, 20));
        markerBox.setText("");
        markerBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                markerBox_keyTyped(e);
            }
        });
        jButton1.setMaximumSize(new Dimension(60, 24));
        jButton1.setMinimumSize(new Dimension(60, 24));
        jButton1.setPreferredSize(new Dimension(60, 24));
        jButton1.setMargin(new Insets(2, 14, 2, 14));
        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jButton2.setMaximumSize(new Dimension(60, 24));
        jButton2.setMinimumSize(new Dimension(60, 24));
        jButton2.setPreferredSize(new Dimension(60, 24));
        jButton2.setText("Load");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        //queryDataSet1.setStoreName("");
        jPanel1.setLayout(gridBagLayout2);
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setResizeWeight(0.5);
        jPanel2.setLayout(new BoxLayout(jPanel2,BoxLayout.X_AXIS));
        btnLoadList.setPreferredSize(new Dimension(60, 24));
        btnLoadList.setText("Load List");
        btnLoadList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnLoadList_actionPerformed(e);
            }
        });
        mainPanel.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(jToolBar1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 262, 0));
        jToolBar1.add(jButton1, null);
        jToolBar1.add(component1, null);
        jToolBar1.add(jButton2, null);
        jToolBar1.add(component2);
        jToolBar1.add(btnLoadList);
        jPanel1.add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
        jSplitPane1.add(jScrollPane2, JSplitPane.RIGHT);
        jScrollPane2.getViewport().add(panelTree, null);
        jScrollPane1.getViewport().add(geneList, null);
        geneList.setFixedCellHeight(15);
        geneList.setFixedCellWidth(300);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jGenePopup.add(jAddToPanel);
        jGenePopup.add(jMenuItem1);
        jPanelMenu.add(jRenameItem);
        jPanelMenu.addSeparator();
        jPanelMenu.add(jActivateItem);
        jPanelMenu.add(jDeactivateItem);
        jPanelMenu.add(jDeleteItem);
        //    jPanelMenu.add(jMergeItem);
        jPanelMenu.add(printItem);
        jPanelMenu.add(exportItem);
        jPanelMenu.add(visualPropertiesItem);
        panelTreeSelection.setSelectionMode(panelTreeSelection.DISCONTIGUOUS_TREE_SELECTION);

        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDeactivateItem_actionPerformed(e);
            }
        };
        listeners.put("Commands.Panels.Deactivate", listener);
        jDeactivateItem.addActionListener(listener);

        geneList.setCellRenderer(renderer);
        panelTree.setCellRenderer(treeRenderer);

        jGenePopup.add(viewPathway1);
        jPanelMenu.add(viewPathway);
        jPanel2.add(jToggleButton1);
        jPanel2.add(markerBox);
    }

    public void notifyMAChange(DSDataSet ds) {
        if (ds != null) {
            markerPanel = CSMarkerManager.getMarkerPanel(ds);
            if (markerPanel == null) {
                markerPanel = new CSPanel<DSGeneMarker>("Marker Panel");
                CSMarkerManager.setMarkerPanel(ds, markerPanel);
            }
            // itemPanel.getSelection().setActive(true);
            if (ds instanceof DSMicroarraySet) {
                markerSet = ((DSMicroarraySet) ds).getMarkers();
                geneList.setModel(new DefaultListModel());
                geneList.setModel(markerListModel);
            } else if (ds instanceof DSDataSet) {
                markerSet = ds;
                geneList.setModel(new DefaultListModel());
                geneList.setModel(markerListModel);
            }
        } else {
            geneList.setModel(new DefaultListModel());
        }
        geneList.repaint();
        regenerateTree();
    }

    /**
     * adds the selected genes to the selection panel
     */
    void jAddToPanel_actionPerformed(ActionEvent e) {
        Object[] selection = geneList.getSelectedValues();
        addMarkersToPanel(selection);
    }

    void addMarkersToPanel(Object[] selection) {
        //        String panelSetLabel = JOptionPane.showInputDialog("Panel Set Label:", "Phenotype");
        //        if (panelSetLabel == null) {
        //            return;
        //        }
        String panelLabel = null;
        DSPanel<DSGeneMarker> markerVector = null;
        DefaultMutableTreeNode panelNode = null;
        TreePath[] selectionPath = panelTree.getSelectionPaths();

        if ((selectionPath != null) && (panelTree.getSelectionPaths().length == 1) && (panelTree.getSelectionPath().getPathCount() > 1)) {
            panelNode = (DefaultMutableTreeNode) panelTree.getSelectionPath().getPathComponent(1);
            markerVector = (DSPanel) panelNode.getUserObject();
            panelLabel = markerVector.getLabel();
        }

        panelLabel = JOptionPane.showInputDialog("Set Label:", panelLabel);
        if (panelLabel == null) {
            return;
        }
        addMarkersToPanel(selection, panelLabel);
    }

    void addMarkersToPanel(Object[] selection, String panelLabel) {
        String panelSetLabel = "Phenotype";

        if (selection.length > 0) {

            DSPanel<DSGeneMarker> markerVector = null;
            DefaultMutableTreeNode panelNode = null;
            TreePath[] selectionPath = panelTree.getSelectionPaths();

            if ((selectionPath != null) && (panelTree.getSelectionPaths().length == 1) && (panelTree.getSelectionPath().getPathCount() > 1)) {
                panelNode = (DefaultMutableTreeNode) panelTree.getSelectionPath().getPathComponent(1);
                markerVector = (DSPanel) panelNode.getUserObject();
            }

            if (markerVector == null || !markerVector.getLabel().equalsIgnoreCase(panelLabel)) {
                if (defaultPanel != null) {
                    panelNode = defaultPanel;
                    markerVector = (DSPanel) panelNode.getUserObject();
                } else {
                    markerVector = new CSPanel<DSGeneMarker>(panelLabel, panelSetLabel);
                    panelNode = new DefaultMutableTreeNode(markerVector);
                    panelTreeModel.insertNodeInto(panelNode, root, root.getChildCount());
                }
            }
            String minorLabel = markerVector.getSubLabel();
            if (!minorLabel.equalsIgnoreCase("GO Term")) {
                for (int i = 0; i < selection.length; i++) {
                    if (markerVector.contains((DSGeneMarker) selection[i])) {
                        //if the marker already exist, do nothing.
                    } else {
                        markerVector.add((DSGeneMarker) selection[i]);
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode((DSGeneMarker) selection[i]);
                        panelTreeModel.insertNodeInto(node, panelNode, panelNode.getChildCount());
                        panelTree.scrollPathToVisible(new TreePath(node.getPath()));
                    }
                }
                panelTree.setSelectionPath(new TreePath(panelNode.getPath()));
                panelTree.scrollPathToVisible(new TreePath(panelNode.getPath()));

                throwEvent(GeneSelectorEvent.PANEL_SELECTION); // tells some other panels to pay attention

            } else {
                JOptionPane.showMessageDialog(null, "Can't add to a " + minorLabel + " panel");
            }
        }
    }

    private void regenerateTree() {
        // Remove all items from the tree
        root.removeAllChildren();
        panelTreeModel.reload(root);
        if (markerPanel != null) {
            for (int i = 0; i < markerPanel.panels().size(); i++) {
                DSPanel<DSGeneMarker> value = markerPanel.panels().get(i);
                DefaultMutableTreeNode panelNode = new DefaultMutableTreeNode(value);
                panelTreeModel.insertNodeInto(panelNode, root, root.getChildCount());
                for (int id = 0; id < value.size(); id++) {
                    DSGeneMarker marker = value.get(id);
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(marker);
                    panelTreeModel.insertNodeInto(node, panelNode, panelNode.getChildCount());
                }
                panelTree.scrollPathToVisible(new TreePath(panelNode.getPath()));
            }
        }
        panelTree.repaint();
    }

    void panelModified() {
        throwEvent(org.geworkbench.events.GeneSelectorEvent.PANEL_SELECTION);
    }

    private boolean isListItemSelected(int index) {
        int[] selectedIndices = geneList.getSelectedIndices();
        if (selectedIndices == null) {
            return false;
        } else {
            for (int i = 0; i < selectedIndices.length; i++) {
                if (index == selectedIndices[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    void jList1_mouseReleased(MouseEvent e) {
        if (e.isMetaDown()) { // if right click performed
            int index = geneList.locationToIndex(new Point(e.getX(), e.getY()));
            if (!isListItemSelected(index)) {
                geneList.setSelectedIndex(index);
            }
            jGenePopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    void jActivateItem_actionPerformed(ActionEvent e) {
        TreePath[] paths = panelTree.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getPathComponent(1);
            if (node != null) {
                DSPanel markerVector = (DSPanel) node.getUserObject();
                markerVector.setActive(true);
                markerPanel.panels().add(markerVector);
            }
        }
        panelModified();
    }

    void printItem_actionPerformed(ActionEvent e) {
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
                pe.printStackTrace();
            }
        }
    }

    void visualPropertiesItem_actionPerformed(ActionEvent e) {
        DSPanel<DSGeneMarker> markerVector = null;
        TreePath[] paths = panelTree.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getPathComponent(1);
            if (node != null) {
                markerVector = (DSPanel) node.getUserObject();
            }
        }
        if (markerVector != null) {
            // Get current active index of panel for default visual properties
            int index = 0;
            if (markerVector.isActive()) {
                for (int i = 0; i < markerPanel.panels().size(); i++) {
                    DSPanel<DSGeneMarker> subPanel = markerPanel.panels().get(i);
                    if (subPanel.isActive()) {
                        index++;
                    }
                    if (subPanel.equals(markerVector)) {
                        index--;
                        break;
                    }
                }
            }
            VisualPropertiesDialog dialog = new VisualPropertiesDialog(null, "Change Visual Properties", markerVector, index);
            dialog.pack();
            dialog.setSize(600, 600);
            dialog.setVisible(true);
            if (dialog.isPropertiesChanged()) {
                PanelVisualPropertiesManager manager = PanelVisualPropertiesManager.getInstance();
                PanelVisualProperties visualProperties = dialog.getVisualProperties();
                if (visualProperties == null) {
                    manager.clearVisualProperties(markerVector);
                } else {
                    manager.setVisualProperties(markerVector, visualProperties);
                }
                throwEvent(org.geworkbench.events.GeneSelectorEvent.PANEL_SELECTION);
            }
        }

    }

    void exportItem_actionPerformed(ActionEvent e) {
        BufferedWriter writer;
        DecimalFormat format = new DecimalFormat("#.####");

        DSPanel<DSGeneMarker> markerVector = null;
        TreePath[] paths = panelTree.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getPathComponent(1);
            if (node != null) {
                markerVector = (DSPanel) node.getUserObject();
            }
        }
        if (markerVector != null) {
            try {
                writer = new BufferedWriter(new FileWriter("genes.txt"));
                int itemNo = markerVector.size();
                for (int i = 0; i < itemNo; i++) {
                    DSGeneMarker gm = markerVector.get(i);
                    String shortName = gm.getShortName();
                    String line = gm.getSerial() + "\t" + gm.getLabel() + "\t" + gm.getUnigene() + "\t" + gm.getGeneId() + "\t" + shortName + "\t" + gm.toString();
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }
        }
    }

    private boolean isPathSelected(TreePath path) {
        TreePath[] selectedPaths = panelTree.getSelectionPaths();
        if (selectedPaths == null) {
            return false;
        }
        for (int i = 0; i < selectedPaths.length; i++) {
            TreePath selectedPath = selectedPaths[i];
            if (path == selectedPath) {
                return true;
            }
        }
        return false;
    }

    void panelTree_mouseReleased(MouseEvent e) {
        if (e.isMetaDown()) {
            int x = e.getX();
            int y = e.getY();
            TreePath[] paths = panelTree.getSelectionPaths();
            TreePath selectedPath = panelTree.getPathForLocation(x, y);
            if (!isPathSelected(selectedPath)) {
                panelTree.setSelectionPath(selectedPath);
            }
            if (selectedPath != null) {
                if (paths != null && selectedPath.getLastPathComponent() != null) {
                    Object obj = ((DefaultMutableTreeNode) selectedPath.getLastPathComponent()).getUserObject();
                    if (obj != null) {
                        if (obj instanceof DSPanel) {
                            jPanelMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        }
    }

    void jMergeItem_actionPerformed(ActionEvent e) {

    }

    void jRenameItem_actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) panelTree.getSelectionPath().getLastPathComponent();
        Object obj = node.getUserObject();
        if (obj instanceof DSPanel) {
            String inputValue = JOptionPane.showInputDialog("Please input a value", ((DSPanel) obj).getLabel());
            if (inputValue != null) {
                ((DSPanel) obj).setLabel(inputValue);
                //node.setUserObject(obj);
                panelTreeModel.nodeChanged(node);
                panelModified();
            }
        }
    }

    void jDeactivateItem_actionPerformed(ActionEvent e) {
        TreePath[] paths = panelTree.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getPathComponent(1);
            if (node != null) {
                DSPanel subPanel = (DSPanel) node.getUserObject();
                subPanel.setActive(false);
                markerPanel.panels().remove(subPanel);
            }
        }
        panelModified();
    }

    void jDeleteItem_actionPerformed(ActionEvent e) {
        TreePath[] paths = panelTree.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            if (node != null) {
                Object obj = node.getUserObject();
                if (obj instanceof DSPanel) {
                    DSPanel markerVector = (DSPanel) obj;
                    markerPanel.panels().remove(markerVector);
                    if (node.getParent() != null) {
                        panelTreeModel.removeNodeFromParent(node);
                    }
                } else {
                    markerPanel.clear();
                    root.removeAllChildren();
                }
            }
        }
        panelModified();
    }

    public void notifyMarkerClicked(int markerId) {
        geneList.setSelectedIndex(markerId);
        geneList.ensureIndexIsVisible(markerId);
    }

    void geneList_mouseClicked(MouseEvent e) {
        int index = geneList.locationToIndex(e.getPoint());
        if (index != -1) {
            if (e.getClickCount() == 2) { // double clicked
                DSGeneMarker value = markerSet.get(index);
                if (markerPanel.getSelection().contains(value)) {
                    markerPanel.getSelection().remove(value);
                } else {
                    markerPanel.getSelection().add(value);
                }
                regenerateTree();
                panelModified();
            } else {
                throwEvent(org.geworkbench.events.GeneSelectorEvent.MARKER_SELECTION);
            }
        }
        geneList.repaint();
    }

    void jMenuItem1_actionPerformed(ActionEvent e) {
        markerPanel.getSelection().clear();
        geneList.clearSelection();
        panelModified();
    }

    public Component getComponent() {
        return mainPanel;
    }

    @Subscribe public void receive(org.geworkbench.events.ProjectEvent projectEvent, Object source) {
        maSet = (DSMicroarraySet) projectEvent.getDataSet();
        if (projectEvent.getMessage().equals(org.geworkbench.events.ProjectEvent.CLEARED)) {
            notifyMAChange(null);
        }
            ProjectSelection selection = ((ProjectPanel) source).getSelection();
            DSDataSet dataFile = selection.getDataSet();
            if (dataFile instanceof DSDataSet) {
                if (selection.getSelectedNode() != selection.getSelectedProjectNode()) {
                    notifyMAChange(dataFile);
                }
            } else {
                notifyMAChange(null);
            }
    }

    void throwEvent(int type) {
        GeneSelectorEvent event = null;
        switch (type) {
            case org.geworkbench.events.GeneSelectorEvent.PANEL_SELECTION:
                event = new org.geworkbench.events.GeneSelectorEvent(markerPanel);
                break;
            case org.geworkbench.events.GeneSelectorEvent.MARKER_SELECTION:
                int index = this.geneList.getSelectedIndex();
                if (index != -1) {
                    if (markerSet != null) {
                        event = new GeneSelectorEvent(markerSet.get(index));
                    }
                }
                break;
        }
        if (event != null) {
            publishGeneSelectorEvent(event);
        }
    }

    @Publish public org.geworkbench.events.GeneSelectorEvent publishGeneSelectorEvent(GeneSelectorEvent event) {
        return event;
    }

    @Subscribe public void receive(MarkerSelectedEvent event, Object source) {
        geneList.setSelectedValue(event.getMarker(), true);
    }

    void jToggleButton1_actionPerformed(ActionEvent e) {
        findNext(1, '\u000E');
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
        int index = geneList.getSelectedIndex();
        if (index < 0) {
            geneList.setSelectedIndex(0);
            index = 0;
        }
        String markerString = markerBox.getText().toLowerCase();
        if (Character.isLetterOrDigit(c)) {
            markerString += Character.toLowerCase(c);
        }
        if (markerPanel != null) {
            int geneNo = markerSet.size();
            for (int idx = Math.abs(offset); idx < geneNo; idx++) {
                int i = 0;
                if (offset < 0) {
                    i = (index + geneNo - idx) % geneNo;
                } else {
                    i = (index + idx) % geneNo;
                }
                DSGeneMarker marker = markerSet.get(i);
                String name = marker.toString().toLowerCase();
                if (name.indexOf(markerString) >= 0) {
                    geneList.setSelectedIndex(i);
                    geneList.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    void jButton1_actionPerformed(ActionEvent e) {
        savePanelSet();
    }

    void jButton2_actionPerformed(ActionEvent e) {
        loadPanelSet();
    }

    /**
     * implements the method in subpaneladdedlistenser
     *
     * @param spe
     */
    @Subscribe public void receive(SubpanelChangedEvent spe, Object source) {
        DSPanel<DSGeneMarker> pan = spe.getPanel();
        if (spe.getMode() == SubpanelChangedEvent.NEW) {
            DefaultMutableTreeNode panelNode = new DefaultMutableTreeNode(pan);
            panelTreeModel.insertNodeInto(panelNode, root, root.getChildCount());
            for (int i = 0; i < pan.size(); i++) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode((DSGeneMarker) pan.get(i));
                panelTreeModel.insertNodeInto(node, panelNode, panelNode.getChildCount());
                panelTree.scrollPathToVisible(new TreePath(node.getPath()));
            }
            panelTree.setSelectionPath(new TreePath(panelNode.getPath()));
            panelTree.scrollPathToVisible(new TreePath(panelNode.getPath()));
            if (pan.isActive()) {
                markerPanel.panels().add(pan);
            }
        } else if (spe.getMode() == SubpanelChangedEvent.SET_CONTENTS) {
            if (pan == null) {
                int j = root.getChildCount();
                for (int i = j - 1; i >= 0; i--) {
                    panelTreeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(i));
                }
            } else {
                Enumeration kids = root.children();
                while (kids.hasMoreElements()) {
                    DefaultMutableTreeNode kid = (DefaultMutableTreeNode) kids.nextElement();
                    if (pan.equals(kid.getUserObject())) {
                        markerPanel.panels().remove((DSPanel) kid.getUserObject());
                        panelTreeModel.removeNodeFromParent(kid);
                    }
                }
                DefaultMutableTreeNode panelNode = new DefaultMutableTreeNode(pan);
                panelTreeModel.insertNodeInto(panelNode, root, root.getChildCount());
                if (pan.isActive()) {
                    markerPanel.panels().add(pan);
                }
                for (int i = 0; i < pan.size(); i++) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode((DSGeneMarker) pan.get(i));
                    panelTreeModel.insertNodeInto(node, panelNode, panelNode.getChildCount());
                    panelTree.scrollPathToVisible(new TreePath(node.getPath()));
                }
            }
        } else if (spe.getMode() == SubpanelChangedEvent.DELETE) {
            if (pan == null) {
                int j = root.getChildCount();
                for (int i = j - 1; i >= 0; i--) {
                    panelTreeModel.removeNodeFromParent((DefaultMutableTreeNode) root.getChildAt(i));
                }
            } else {
                Enumeration kids = root.children();
                while (kids.hasMoreElements()) {
                    DefaultMutableTreeNode kid = (DefaultMutableTreeNode) kids.nextElement();
                    if (pan.toString().equalsIgnoreCase(kid.getUserObject().toString())) {
                        markerPanel.panels().remove((DSPanel) kid.getUserObject());
                        panelTreeModel.removeNodeFromParent(kid);
                    }
                }
            }
        }
        panelModified();
    }

    /**
     * getActionListener
     *
     * @param var String
     * @return ActionListener
     */
    public ActionListener getActionListener(String var) {
        return (ActionListener) listeners.get(var);
    }

    /**
     * Saves a panel set to the filesystem
     */
    private void savePanelSet() {
        TreePath[] selectionPath = panelTree.getSelectionPaths();
        if ((selectionPath != null) && (panelTree.getSelectionPaths().length == 1) && (panelTree.getSelectionPath().getPathCount() > 1)) {
            JFileChooser fc = new JFileChooser(".");
            String psFilename = null;
            FileFilter filter = new MarkerPanelSetFileFilter();
            fc.setFileFilter(filter);
            fc.setDialogTitle("Save Marker Panel");
            String extension = ((MarkerPanelSetFileFilter) filter).getExtension();
            int choice = fc.showSaveDialog(mainPanel.getParent());
            if (choice == JFileChooser.APPROVE_OPTION) {
                psFilename = fc.getSelectedFile().getAbsolutePath();
                if (!psFilename.endsWith(extension)) {
                    psFilename += extension;
                }
                serialize(psFilename);
            }
        } else {
            JOptionPane.showMessageDialog(getComponent(), "Please select a panel to be saved");
        }
    }

    /**
     * Utility to save a panel set to the filesystem
     *
     * @param filename filename in which the current panel set has to be saved
     */
    private void serialize(String filename) {
        DSPanel<DSGeneMarker> markerVector = null;
        DefaultMutableTreeNode panelNode = null;
        panelNode = (DefaultMutableTreeNode) panelTree.getSelectionPath().getPathComponent(1);
        markerVector = (DSPanel<DSGeneMarker>) panelNode.getUserObject();
        if (markerVector != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
                String line = null;
                if (markerVector != null && markerVector.size() > 0) {
                    line = "Label : " + markerVector.getLabel();
                    writer.write(line);
                    writer.newLine();
                    line = "MinorLabel\t" + markerVector.getSubLabel();
                    writer.write(line);
                    writer.newLine();
                    line = "MarkerType\t" + markerVector.get(0).getClass().getName();
                    writer.write(line);
                    writer.newLine();
                    for (int i = 0; i < markerVector.size(); i++) {
                        DSGeneMarker marker = (DSGeneMarker) markerVector.get(i);
                        line = marker.getSerial() + "\t" + marker.getLabel() + "\t" + marker.getDescription();
                        writer.write(line);
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Loads a saved Panel set from the file system
     */
    private void loadPanelSet() {
        JFileChooser fc = new JFileChooser(".");
        String psFilename = null;
        FileFilter filter = new MarkerPanelSetFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Open Marker Panel");
        int choice = fc.showOpenDialog(mainPanel.getParent());
        if (choice == JFileChooser.APPROVE_OPTION) {
            psFilename = fc.getSelectedFile().getAbsolutePath();
            deserialize(psFilename);
        }
    }

    /**
     * Utility to obtain the stored panel sets from the filesystem
     *
     * @param filename filename which contains the stored panel set
     */
    private void deserialize(final String filename) {
        Thread t = new Thread() {
            public void run() {
                BufferedReader stream = null;
                ;
                try {
                    stream = new BufferedReader(new InputStreamReader(new ProgressMonitorInputStream(getComponent(), "Loading probes " + filename, new FileInputStream(filename))));
                    String line = null;
                    DSPanel<DSGeneMarker> msp = new CSPanel<DSGeneMarker>();
                    Class type = null;
                    while ((line = stream.readLine()) != null) {
                        String[] tokens = line.split("\t");
                        if (tokens != null && tokens.length == 2) {
                            if (tokens[0].trim().equalsIgnoreCase("Label")) {
                                msp.setLabel(new String(tokens[1].trim()));
                            } else if (tokens[0].trim().equalsIgnoreCase("MinorLabel")) {
                                msp.setSubLabel(new String(tokens[1].trim()));
                            } else if (tokens[0].trim().equalsIgnoreCase("MarkerType")) {
                                type = Class.forName(tokens[1].trim());
                            } else {
                            }
                        }
                        if (tokens != null && tokens.length == 3) {
                            if (type != null) {
                                DSGeneMarker marker = (DSGeneMarker) type.newInstance();
                                if (marker != null) {
                                    marker.setSerial(Integer.parseInt(tokens[0].trim()));
                                    marker.setLabel(new String(tokens[1].trim()));
                                    marker.setDescription(new String(tokens[2].trim()));
                                    msp.add(marker);
                                }
                            }
                        }
                    }
                    publishSubpanelChangedEvent(new SubpanelChangedEvent(DSGeneMarker.class, msp, SubpanelChangedEvent.NEW));
                    throwEvent(org.geworkbench.events.GeneSelectorEvent.PANEL_SELECTION);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                } catch (IllegalAccessException iae) {
                    iae.printStackTrace();
                } catch (InstantiationException ie) {
                    ie.printStackTrace();
                }
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Publish public SubpanelChangedEvent publishSubpanelChangedEvent(SubpanelChangedEvent event) {
        return event;
    }

    public void btnLoadList_actionPerformed(ActionEvent e) {
        loadFromList();
        //        loadPanelsFromList();
    }

    void loadPanelsFromList() {
        JFileChooser fc = new JFileChooser("C:/Research/MicroarrayData/BCells/Analysis");
        int choice = fc.showOpenDialog(mainPanel.getParent());
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        CSMarkerVector markers = (CSMarkerVector) maSet.getMarkers();

        File selectedFile = fc.getSelectedFile();
        Vector<String> fileData = FileUtil.readFileData(selectedFile);
        for (int i = 0; i < fileData.size(); i++) {
            DSPanel<DSGeneMarker> selectedMarkerPanel = new CSPanel<DSGeneMarker>("Markers " + i);
            String[] markerLabels = new String(fileData.get(i)).split("\t");
            for (int j = 0; j < markerLabels.length; j++) {
                DSGeneMarker selectedMarker = null;
                selectedMarker = markers.get(markerLabels[j]);
                if (selectedMarker != null) {
                    selectedMarkerPanel.add(selectedMarker);
                }
            }

            //don't call the method b/c it throws an event each time a panel is added
            //for now just copy the code here.
            Object[] selection = selectedMarkerPanel.toArray();

            String panelLabel = selectedMarkerPanel.getLabel();

            String panelSetLabel = "Phenotype";

            if (selection.length > 0) {

                DSPanel<DSGeneMarker> markerVector = null;
                DefaultMutableTreeNode panelNode = null;
                TreePath[] selectionPath = panelTree.getSelectionPaths();

                if ((selectionPath != null) && (panelTree.getSelectionPaths().length == 1) && (panelTree.getSelectionPath().getPathCount() > 1)) {
                    panelNode = (DefaultMutableTreeNode) panelTree.getSelectionPath().getPathComponent(1);
                    markerVector = (DSPanel) panelNode.getUserObject();
                }

                if (markerVector == null || !markerVector.getLabel().equalsIgnoreCase(panelLabel)) {
                    if (defaultPanel != null) {
                        panelNode = defaultPanel;
                        markerVector = (DSPanel) panelNode.getUserObject();
                    } else {
                        markerVector = new CSPanel<DSGeneMarker>(panelLabel, panelSetLabel);
                        panelNode = new DefaultMutableTreeNode(markerVector);
                        panelTreeModel.insertNodeInto(panelNode, root, root.getChildCount());
                    }
                }
                String minorLabel = markerVector.getSubLabel();
                if (!minorLabel.equalsIgnoreCase("GO Term")) {
                    for (int j = 0; j < selection.length; j++) {
                        if (markerVector.contains((DSGeneMarker) selection[j])) {
                            //if the marker already exist, do nothing.
                        } else {
                            markerVector.add((DSGeneMarker) selection[j]);
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode((DSGeneMarker) selection[j]);
                            panelTreeModel.insertNodeInto(node, panelNode, panelNode.getChildCount());
                            panelTree.scrollPathToVisible(new TreePath(node.getPath()));
                        }
                    }
                    panelTree.setSelectionPath(new TreePath(panelNode.getPath()));
                    panelTree.scrollPathToVisible(new TreePath(panelNode.getPath()));


                } else {
                    JOptionPane.showMessageDialog(null, "Can't add to a " + minorLabel + " panel");
                }
            }

        }
        throwEvent(GeneSelectorEvent.PANEL_SELECTION); // tells some other panels to pay attention
    }

    void loadFromList() {
        JFileChooser fc = new JFileChooser();
        int choice = fc.showOpenDialog(mainPanel.getParent());
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fc.getSelectedFile();
        Vector<String> geneNames = org.geworkbench.bison.util.FileUtil.readVector(selectedFile);
        //        DSItemList<DSGeneMarker> markers = maSet.markers();
        CSMarkerVector markers = (CSMarkerVector) maSet.getMarkers();
        Vector selectedMarkers = new Vector();
        String panelName = null;
        for (String geneName : geneNames) {
            if (geneName.contains("Panel:")) {
                if (!selectedMarkers.isEmpty() && panelName != null) {
                    addMarkersToPanel(selectedMarkers.toArray(), panelName);
                    selectedMarkers.clear();
                }
                StringTokenizer tokenizer = new StringTokenizer(geneName, " \t");
                tokenizer.nextToken();
                panelName = new String(tokenizer.nextToken());
            } else {
                DSGeneMarker selectedMarker = null;

                selectedMarker = markers.get(geneName);
                if (selectedMarker != null) {
                    selectedMarkers.add(selectedMarker);
                }
            }
        }
        if (panelName != null) {
            if (!selectedMarkers.isEmpty()) {
                addMarkersToPanel(selectedMarkers.toArray(), panelName);
                selectedMarkers.clear();
            }
        } else {
            addMarkersToPanel(selectedMarkers.toArray());
        }
    }

    /**
     * <code>FileFilter</code> that is used by the <code>JFileChoose</code> to
     * show just panel set files on the filesystem
     */
    class MarkerPanelSetFileFilter extends FileFilter {
        String fileExt;

        MarkerPanelSetFileFilter() {
            fileExt = ".mps";
        }

        public String getExtension() {
            return fileExt;
        }

        public String getDescription() {
            return "Marker Panel Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            if (f.isDirectory() || f.getName().endsWith(fileExt)) {
                return true;
            }
            return returnVal;
        }
    }


    class PrintListingPainter implements Printable {
        private Font fnt = new Font("Helvetica", Font.PLAIN, 8);
        private int rememberedPageIndex = -1;
        private long rememberedFilePointer = -1;
        private boolean rememberedEOF = false;
        private int index = 0;
        private int lastIndex = 0;
        DSGeneMarker gm = null;

        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            DecimalFormat format = new DecimalFormat("#.####");
            try {
                DSPanel<DSGeneMarker> markerVector = null;
                TreePath[] paths = panelTree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getPathComponent(1);
                    if (node != null) {
                        markerVector = (DSPanel<DSGeneMarker>) node.getUserObject();
                    }
                }
                if (markerVector != null) {
                    int itemNo = Math.min(markerVector.size(), 500);
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
                    y += 36;
                    while (y + 12 < pf.getImageableY() + pf.getImageableHeight()) {
                        if (index >= itemNo) {
                            rememberedEOF = true;
                            break;
                        }
                        gm = markerVector.get(index);
                        String line = "[" + gm.getSerial() + "]";
                        g.drawString(line, x, y);
                        g.drawString(gm.getLabel(), x + 30, y);
                        g.drawString(gm.toString(), x + 160, y);
                        y += 12;
                        index++;
                    }
                    return Printable.PAGE_EXISTS;
                }
                return Printable.NO_SUCH_PAGE;
            } catch (Exception e) {
                return Printable.NO_SUCH_PAGE;
            }
        }
    }
}
