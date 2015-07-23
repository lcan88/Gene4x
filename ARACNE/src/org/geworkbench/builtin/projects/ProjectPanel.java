package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.
        DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.
        CSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.bison.util.colorcontext.ColorContext;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Script;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.engine.management.TypeMap;
import org.geworkbench.engine.parsers.FileFormat;
import org.geworkbench.engine.parsers.InputFileFormatException;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;
import org.geworkbench.engine.parsers.patterns.PatternFileFormat;
import org.geworkbench.engine.preferences.GlobalPreferences;
import org.geworkbench.events.*;
import org.geworkbench.util.PropertiesMonitor;
import org.geworkbench.util.SaveImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import org.geworkbench.bison.datastructure.biocollections.microarrays.
        CSMicroarraySet;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class ProjectPanel implements VisualPlugin, MenuListener {

    private static TypeMap<ImageIcon> iconMap = new TypeMap<ImageIcon>();

    static {
        DefaultIconAssignments.initializeDefaultIconAssignments();
    }

    // Initialize default icons

    protected LoadData loadData = new LoadData(this);

    private ProjectSelection selection = new ProjectSelection(this);

    // The undo buffer
    ProjectTreeNode undoNode = null;
    ProjectTreeNode undoParent = null;

    /**
     * Additional Menu related instance variables that do not exist in parent
     */
    /**
     * XQ uses dataSetMenu to save/modify the new generated/old Fasta file
     * dataSetSubMenu to save sequence alignment result.
     */
    private JPopupMenu dataSetMenu = new JPopupMenu();
    private JPopupMenu dataSetSubMenu = new JPopupMenu();

    /**
     * Add MenuItem Listeners here;
     */

    private JProgressBar progressBar = new JProgressBar();
    private JMenuItem jMenuItem1 = new JMenuItem();
    private JMenuItem jRemoveProjectItem = new JMenuItem();
    private JMenuItem jRemoveDatasetItem = new JMenuItem();
    private JMenuItem jRemoveSubItem = new JMenuItem();
    private JMenuItem jRenameSubItem = new JMenuItem();
    private JMenuItem jEditItem = new JMenuItem();

    /**
     * added by XQ 4/7/04
     */
    private JMenuItem jSaveMenuItem = new JMenuItem();
    private JMenuItem jRenameMenuItem = new JMenuItem();
    private JFileChooser jFileChooser1;

    /**
     * Constructor. Intialize GUI and selection variables
     */

    public ProjectPanel() {
        //Initializes Random number generator to generate unique ID's
        //because of the unique seed
        RandomNumberGenerator.setSeed(System.currentTimeMillis());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            jbInit1();
            // Checks if a default workspace exists and loads it
            File defaultWS = new File("./default.wsp");
            if (defaultWS.exists()) {
                deserialize(defaultWS.getName());
                Enumeration children = root.children();
                while (children.hasMoreElements()) {
                    TreeNode node = (TreeNode) children.nextElement();
                    projectTree.expandPath(new TreePath(node));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Standard Initialization routing needed by JBuilder
     *
     * @throws java.lang.Exception
     */
    protected void jbInit1() throws Exception {
        //deserialize();

        jMenuItem1.setText("Load Patterns");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem1_actionPerformed(e);
            }
        });
        jMArrayMenu.add(jMenuItem1);

        jDataSetPanel.add(progressBar, BorderLayout.SOUTH);
        /**
         * added by XQ
         */
        jFileChooser1 = new JFileChooser();
        jSaveMenuItem.setText("Save");
        jRenameMenuItem.setText("Rename");
        jSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jSaveMenuItem_actionPerformed(e);
            }
        });

        jRenameMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRenameDataset_actionPerformed(e);
            }
        });

        jRemoveDatasetItem.setText("Remove");
        jRemoveDatasetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileRemove_actionPerformed(e);
            }
        });

        jRenameSubItem.setText("Rename");
        jRenameSubItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRenameDataset_actionPerformed(e);
            }
        });

        jRemoveSubItem.setText("Remove");
        jRemoveSubItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileRemove_actionPerformed(e);
            }
        });
        jEditItem.setText("View in Editor");
        jEditItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selection.getSelectedNode() instanceof DataSetNode) {
                    DSDataSet ds = selection.getDataSet();

                    GlobalPreferences prefs = GlobalPreferences.getInstance();
                    String editor = prefs.getTextEditor();
                    if (editor == null) {
                        System.out.println("No editor configured.");
                    } else {
                        if (ds.getFile() == null) {
                            JOptionPane.showMessageDialog(null,
                                    "There is no local file for this data set.",
                                    "Unable to Edit",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String[] args = {editor,
                                            ds.getFile().getAbsolutePath()};
                            try {
                                Runtime.getRuntime().exec(args);
                            } catch (IOException e1) {
                                System.out.println("Error opening editor:");
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        dataSetMenu.add(jSaveMenuItem);
        dataSetMenu.addSeparator();
        dataSetMenu.add(jRenameMenuItem);
        dataSetMenu.add(jRemoveDatasetItem);
        dataSetMenu.add(jEditItem);

        dataSetSubMenu.add(jRenameSubItem);
        dataSetSubMenu.add(jRemoveSubItem);

        jRemoveProjectItem.setText("Remove Project");
        jRemoveProjectItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                projectRemove_actionPerformed(e);
            }
        });

        jProjectMenu.addSeparator();
        jProjectMenu.add(jRemoveProjectItem);
    }

    /**
     * jSaveMenuItem_actionPerformed
     */
    public void jSaveMenuItem_actionPerformed(ActionEvent e) {
        saveAsFile();
    }

    public static ImageIcon getIconForType(Class<? extends DSNamed> type) {
        ImageIcon icon = iconMap.get(type);
        if (icon == null) {
            return Icons.GENERIC_ICON;
        } else {
            return icon;
        }
    }

    public static void setIconForType(Class<? extends DSNamed> type,
                                      ImageIcon icon) {
        iconMap.put(type, icon);
    }

    boolean saveAsFile() {
        Object mSetSelected = projectTree.getSelectionPath().
                              getLastPathComponent();
        if (mSetSelected != null && mSetSelected instanceof DataSetNode) {
            DSDataSet ds = ((DataSetNode) mSetSelected).dataFile;
            File f = ds.getFile();
            jFileChooser1 = new JFileChooser(f);
            jFileChooser1.setSelectedFile(f);

            // Use the SAVE version of the dialog, test return for Approve/Cancel
            if (JFileChooser.APPROVE_OPTION ==
                jFileChooser1.showSaveDialog(jSaveMenuItem)) {
                // Set the current file name to the user's selection,
                // then do a regular saveFile
                String newFileName = jFileChooser1.getSelectedFile().getPath();
                //repaints menu after item is selected
                System.out.println(newFileName);
                //        if(f != null) {
                //          return saveFile(f, newFileName);
                //        } else {
                ds.writeToFile(newFileName);
                //        }
            } else {
                // this.repaint();
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null,
                                          "This node contains no Dataset.",
                                          "Save Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Save current file; handle not yet having a filename; report to statusBar.
    //  boolean saveFile(File f, String currFileName) {
    //    try {
    //      File file = new File(currFileName);
    //      FileWriter out = new FileWriter(file);
    //      if (f.getName().equals(currFileName)) {
    //        return false;
    //      } else {
    //        // Open a file of the current name.
    //        FileReader in = new FileReader(f);
    //        int c;
    //
    //        while ( (c = in.read()) != -1) {
    //          out.write(c);
    //
    //        }
    //        in.close();
    //        out.close();
    //
    //        return true;
    //      }
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //      return false;
    //    }
    //  }

    /**
     * Change the comment text
     *
     * @param ce
     */
    @Subscribe public void receive(CommentsEvent ce, Object source) {
        ProjectTreeNode selectedNode = selection.getSelectedNode();
        selectedNode.setDescription(ce.getText());
    }

    /**
     * Retrieve the associated descriptions
     * @return
         public String getUserComments() {
         ProjectTreeNode selectedNode = selection.getSelectedNode();
         String text = "";
         String[] descriptions = null;
         if (selectedNode instanceof DataSetNode) {
     descriptions = ( (DataSetNode) selectedNode).dataFile.getDescriptions();
         for (int i = 0; i < descriptions.length; i++) {
         text += descriptions[i];
         }
         }
         return text;
         }
     */

    /**
     * Inserts a new data set as a new node in the project tree.
     * The node is a child of the curently selected project
     *
     * @param _dataSet
     */
    public void addDataSetNode(DSDataSet _dataSet, boolean select) {
        // Retrieve the project node for this node
        ProjectNode pNode = selection.getSelectedProjectNode();
        if (pNode == null) {
        }
        if (pNode != null) {
            // Inserts the new node and sets the menuNode and other variables to point to it
            DataSetNode node = new DataSetNode(_dataSet);
            projectTreeModel.insertNodeInto(node, pNode, pNode.getChildCount());
            if (select) {
                // Make sure the user can see the lovely new node.
                projectTree.scrollPathToVisible(new TreePath(node));
                //serialize("default.ws");
                projectTree.setSelectionPath(new TreePath(node.getPath()));
                selection.setNodeSelection(node);
            }
        }
    }


    /**
     * getMatchNode
     *
     * @param root        ProjectTreeNode
     * @param _ancDataSet AncillaryDataSet
     * @return DataSetNode
     */
    public DataSetNode getMatchNode(ProjectTreeNode pnode,
                                    File fastaFilename) {

        //if (_ancDataSet instanceof BlastDataSet) {
        //    fastaFilename = ( (BlastDataSet) _ancDataSet).getFastaFile();
        if ((pnode instanceof DataSetNode) &&
            ((((DataSetNode) pnode).dataFile.getFile()).equals(
                    fastaFilename))) {
            return (DataSetNode) pnode;
        } else if (pnode != null) {
            Enumeration children = pnode.children();
            while (children.hasMoreElements()) {
                Object obj = children.nextElement();
                if (getMatchNode((ProjectTreeNode) obj, fastaFilename) != null) {

                    return getMatchNode((ProjectTreeNode) obj, fastaFilename);
                }
            }

        }

        return null;
    }


    /**
     * Inserts a new ancillary data set  as a new node in the project tree.
     * The node is a child of the curently selected data set
     *
     * @param _ancDataSet
     */
    private void addDataSetSubNode(DSAncillaryDataSet _ancDataSet) {
        DataSetNode dNode = selection.getSelectedDataSetNode();
        DataSetNode matchedDNode = null;
        File fastaFile = _ancDataSet.getDataSetFile();
        if (fastaFile != null) {
            if (dNode != null) {

                File dNodeFile = dNode.dataFile.getFile();

                if (dNodeFile.equals(fastaFile)) {
                    _ancDataSet.setDataSetFile(dNode.dataFile.getFile());
                } else {
                    //get the matched node in case the node selected changed.
                    matchedDNode = getMatchNode(root, fastaFile);
                }

            } else {
                matchedDNode = getMatchNode(root, fastaFile);
            }
        }
        if (matchedDNode != null) {
            dNode = matchedDNode;
        }
        if (dNode == null) {
            System.out.println("There is no node at project panel!");
            return;
        }

        _ancDataSet.setDataSetFile(dNode.dataFile.getFile());
        // Makes sure that we do not already have an exact instance of this ancillary file
        Enumeration children = dNode.children();
        while (children.hasMoreElements()) {
            Object obj = children.nextElement();
            if (obj instanceof DataSetSubNode) {
                DSAncillaryDataSet ads = ((DataSetSubNode) obj)._aDataSet;
                if (_ancDataSet.equals(ads)) {
                    return;
                }
            }
        }

        // Inserts the new node and sets the menuNode and other variables to point to it
        DataSetSubNode node = new DataSetSubNode(_ancDataSet);
        projectTreeModel.insertNodeInto(node, dNode, dNode.getChildCount());
        // Make sure the user can see the lovely new node.
        projectTree.scrollPathToVisible(new TreePath(node));
        //      serialize("default.ws");
        projectTree.setSelectionPath(new TreePath(node.getPath()));
        selection.setNodeSelection(node);
    }

    /**
     * Stores to a datafile
     * @param filename
         void serialize(String filename) {
         try {
         for(int i = 0; i < projectTree.getRowCount(); i++) {
         TreePath path = projectTree.getPathForRow(i);
         ProjectTreeNode node = (ProjectTreeNode)path.getLastPathComponent();
         if(node instanceof DataSetSubNode) {
         DataSetSubNode dNode = (DataSetSubNode)node;
         if(dNode._aDataSet instanceof PatternDB) {
         PatternDB patternDB = (PatternDB)dNode._aDataSet;
         if(patternDB.getFile() == null) {
         patternDB.write();
         }
         }
         }
         }
         FileOutputStream f = new FileOutputStream(filename);
         ObjectOutput s = new ObjectOutputStream(f);
         s.writeObject(root);
         s.flush();
         } catch (IOException ex) {
         System.err.println("Error: " + ex);
         }
         }
     */

    /**
     * Reads from a datafile
     *
     * @param filename
     */
    void deserialize(String filename) {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream s = new ObjectInputStream(in);
            root = (ProjectTreeNode) s.readObject();
            selection.clearNodeSelections();
            projectTreeModel = new DefaultTreeModel(root);
            projectTree.setModel(projectTreeModel);
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: " + ex);
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }

    /**
     * Action listener responding to the selection of a project tree node.
     *
     * @param e
     */
    protected void jProjectTree_mouseClicked(MouseEvent e) {

        TreePath path = projectTree.getSelectionPath();
        if (path != null) {
            path.getLastPathComponent();
            selectedNode = selection.getSelectedNode();
            ProjectTreeNode clickedNode = (ProjectTreeNode) path.
                                          getLastPathComponent();
            // Take action only if a new node is selected.
            if (path != null && selectedNode != clickedNode) {
                setNodeSelection(clickedNode);
            }
            if ((clickedNode != null) && clickedNode instanceof DataSetSubNode) {
                // DSAncillaryDataSet ds = ((DataSetSubNode) clickedNode)._aDataSet;
                // publishProjectEvent(new ProjectEvent("ProjectNode", ds));
            }
            if ((clickedNode != null) && clickedNode instanceof ImageNode) {
                if (e.getClickCount() == 1) {
                    publishImageSnapshot(new ImageSnapshotEvent(
                            "Image Node Selected",
                            ((ImageNode) clickedNode).image,
                            ImageSnapshotEvent.Action.SHOW));
                    sendCommentsEvent(clickedNode);
                }
            }
            sendCommentsEvent(clickedNode);
        }
    }

    private boolean isPathSelected(TreePath path) {
        TreePath[] selectedPaths = projectTree.getSelectionPaths();
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

    /**
     * Mouse release event. Used to popup menus
     *
     * @param e
     */
    protected void jProjectTree_mouseReleased(MouseEvent e) {
        TreePath path = projectTree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            ProjectTreeNode mNode = (ProjectTreeNode) path.getLastPathComponent();
            selection.setMenuNode(mNode);
            if (e.isMetaDown() || e.getClickCount() >= 2) {
                if (!isPathSelected(path)) {
                    // Force selection of this path
                    projectTree.setSelectionPath(path);
                    jProjectTree_mouseClicked(e);
                }
                // Make the jPopupMenu visible relative to the current mouse position in the container.
                if ((mNode == null) || (mNode == root)) {
                    jRootMenu.show(projectTree, e.getX(), e.getY());
                } else if (mNode instanceof ProjectNode) {
                    jProjectMenu.show(projectTree, e.getX(), e.getY());

                } else if (mNode instanceof DataSetNode) {
                    dataSetMenu.show(projectTree, e.getX(), e.getY());
                } else if (mNode instanceof DataSetSubNode) {
                    dataSetSubMenu.show(projectTree, e.getX(), e.getY());
                }
            }
        }
    }

    void jMenuItem1_actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(PropertiesMonitor.
                                                getPropertiesMonitor().
                                                getDefPath());
        PatternFileFormat format = new PatternFileFormat();
        FileFilter filter = format.getFileFilter();
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            DSDataSet ds = selection.getDataSet();
            if (ds != null) {
                org.geworkbench.util.patterns.PatternDB patternDB = new org.
                        geworkbench.util.patterns.PatternDB(ds.getFile(), null);
                if (patternDB.read(chooser.getSelectedFile())) {
                    addDataSetSubNode(patternDB);
                }
            }
        }
    }

    @Subscribe
            public void receive(org.geworkbench.events.ProjectNodeAddedEvent
                                pnae, Object source) {
        DSDataSet dataSet = pnae.getDataSet();
        DSAncillaryDataSet ancillaryDataSet = pnae.getAncillaryDataSet();
        if (dataSet != null) {
            addDataSetNode(dataSet, true);
        } else if (ancillaryDataSet != null) {
            addDataSetSubNode(ancillaryDataSet);
        }
    }

    /**
     * Interface <code>ImageSnapshotListener</code> method for receiving
     * <code>ImageSnapshotEvent</code> from Visual Plugins. These events contain
     * <code>ImageIcon</code> representing visual state of the plugins throwing
     * this event.
     *
     * @param event <code>ImageSnapshotEvent</code>
     */
    public void saveImage(ImageSnapshotEvent event) {
        ImageData node = null;
        node = new ImageData(null);
        node.setImageIcon(event.getImage());
        node.addDescription(event.getImage().getDescription());
        addDataSetSubNode(node);
    }

    /**
     * Interface <code>ImageSnapshotListener</code> method for showing Images
     *
     * @param event <code>ImageSnapshotEvent</code>
     */
    public void showImage(ImageSnapshotEvent event) {
    }


    /**
     * Action listener handling user requests for opening a file containing
     * microarray set data.
     *
     * @param e
     */
    protected void jLoadMArrayItem_actionPerformed(ActionEvent e) {
        // Proceed only if there is a single node selected and that node
        // is a project node.
        if (projectTree.getSelectionCount() != 1 ||
            !(projectTree.getSelectionPath().getLastPathComponent() instanceof
              ProjectNode)) {
            JOptionPane.showMessageDialog(null, "Select a project node.",
                                          "Open File Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dir = LoadData.getLastDataDirectory();
        String format = LoadData.getLastDataFormat();
        loadData.setDirectory(dir);
        loadData.setFormat(format);
        // setupInputFormats() is called at every invocation of the "Open File"
        // dialog. This guarantees that any dynamically loaded file format plugins
        // will be taken into account.
        loadData.setupInputFormats();
        loadData.validate();
        loadData.setVisible(true);
    }

    /**
     * Action listener handling user requests to merge 2 or more microarray sets
     * into 1.
     *
     * @param e <code>ActionEvent</code>
     */
    protected void jMergeDatasets_actionPerformed(ActionEvent e) {
        TreePath[] selections;
        DSMicroarraySet[] sets = null;
        MutableTreeNode node = null;
        Object parentProject = null;
        TreePath sibling = null;
        int count = projectTree.getSelectionCount();
        int i;
        // Obtain the selected project tree nodes.
        selections = projectTree.getSelectionPaths();
        sets = new DSMicroarraySet[count];
        // Check that the user has designated only microarray set nodes and that
        // all microarray sets are from the same project.
        // Also, identify the node that will become the parent of the new, merged
        // microarray set node.
        for (i = 0; i < count; i++) {
            node = (MutableTreeNode) selections[i].getLastPathComponent();
            if (node instanceof DataSetNode) {
                sets[i] = (DSMicroarraySet) ((DataSetNode) node).dataFile;
                if (sibling == null ||
                    sibling.getPathCount() > selections[i].getPathCount()) {
                    sibling = selections[i];
                }
                if (parentProject == null) {
                    parentProject = selections[i].getPath()[1];
                } else if (parentProject != selections[i].getPath()[1]) {
                    JOptionPane.showMessageDialog(null,
                            "Select nodes from 1 project only.", "Merge Error",
                                                  JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Select microarray set nodes only.", "Merge Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // Verify that at least 2 microarray sets have been selected for merging
        if (i < 2) {
            JOptionPane.showMessageDialog(null, "Select 2 or more data nodes.",
                                          "Merge Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Verify that all microarrays are of the same base type.
        for (i = 0; i < count; ++i) {
            if (!sets[0].getClass().isAssignableFrom(sets[i].getClass())) {
                JOptionPane.showMessageDialog(null,
                        "Only microarray sets of the same" +
                        " underlying platform can be merged.", "Merge Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        doMergeSets(sets);
    }

    private void doMergeSets(DSMicroarraySet[] sets) {
        DSMicroarraySet mergedSet = null;
        int i;
        DSMicroarraySet<DSMicroarray> set;
        if (sets != null) {
            String desc = "Merged DataSet: ";
            for (i = 0; i < sets.length; i++) {
                set = sets[i];
                if (mergedSet == null) {
                    try {
                        mergedSet = set.getClass().newInstance();
                        mergedSet.addObject(ColorContext.class,
                                            set.getObject(ColorContext.class));
                        //mergedSet.setMarkerNo(set.size());
                        //mergedSet.setMicroarrayNo(set.size());

                        ((DSMicroarraySet<DSMicroarray>) mergedSet).
                                setCompatibilityLabel(set.getCompatibilityLabel());
                        ((DSMicroarraySet<DSMicroarray>) mergedSet).getMarkers().
                                addAll(set.getMarkers());
                        DSItemList<DSGeneMarker> markerList = set.getMarkers();
                        for (int j = 0; j < markerList.size(); j++) {
                            DSGeneMarker dsGeneMarker = markerList.get(j);
                            ((DSMicroarraySet<DSMicroarray>) mergedSet).
                                    getMarkers().add(dsGeneMarker.deepCopy());
                        }
                        for (int k = 0; k < set.size(); k++) {
                            mergedSet.add(set.get(k).deepCopy());
                        }
                        desc += set.getLabel() + " ";
                    } catch (InstantiationException ie) {
                        ie.printStackTrace();
                    } catch (IllegalAccessException iae) {
                        iae.printStackTrace();
                    }
                } else {
                    desc += set.getLabel() + " ";
                    try {
                        mergedSet.mergeMicroarraySet(set);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "Only microarray sets created" +
                                " from the same chip set can be merged",
                                "Merge Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (mergedSet != null) {
                mergedSet.setLabel("Merged array set");
                mergedSet.setLabel(desc);
                mergedSet.addDescription(desc);
            }
            // Add color context
            addColorContext(mergedSet);

            // Add the new dataset to the project tree.
            addDataSetNode((DSDataSet) mergedSet, true);
        }
    }

    /**
     * Invoked from the "Open File" dialog box to handle opening a local
     * dataset.
     *
     * @param dataSetFiles The file containing the data to be parsed.
     * @param inputFormat  The format that the file is expected to conform to.
     * @throws org.geworkbench.engine.parsers.InputFileFormatException
     *
     */
    public void fileOpenAction(final File[] dataSetFiles,
                               final org.geworkbench.engine.parsers.FileFormat
                               inputFormat, boolean merge) throws org.
            geworkbench.engine.parsers.InputFileFormatException {

        final boolean mergeFiles = dataSetFiles.length == 1 ? false : merge;
        if (inputFormat instanceof DataSetFileFormat) {

            //       super.fileOpenAction(dataSetFiles, inputFormat);
            progressBar.setStringPainted(true);
            progressBar.setString("Loading");
            progressBar.setIndeterminate(true);
            jDataSetPanel.setCursor(Cursor.getPredefinedCursor(Cursor.
                    WAIT_CURSOR));
            Runnable dataLoader = new Runnable() {
                public void run() {
                    int n = dataSetFiles.length;
                    DSDataSet[] dataSets = new DSDataSet[n];
                    DSMicroarraySet mergedSet = null;
                    String mergedName = null;
                    if (dataSetFiles.length == 1) {
                        try {
                            dataSets[0] = ((DataSetFileFormat) inputFormat).
                                          getDataFile(dataSetFiles[0]);
                        } catch (InputFileFormatException iffe) {
                            // Let the user know that there was a problem parsing the file.
                            JOptionPane.showMessageDialog(null,
                                    "The input file does not comply with the designated format.",
                                    "Parsing Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        // watkin - none of the file filters implement the multiple getDataFile method.
                        // dataSets[0] = ((DataSetFileFormat)inputFormat).getDataFile(dataSetFiles);
                        // If the data sets are microarray sets, then merge them if "merge files" is checked
                        boolean cancelled = false;
//                        if (mergeFiles) {
//                            mergedName = JOptionPane.showInputDialog("Please enter the name of the merged Microarray Set");
//                            if (mergedName == null) {
//                                // Cancelled
//                                cancelled = true;
//                            }
//                        }
//                        if (!cancelled) {
                        for (int i = 0; i < dataSetFiles.length; i++) {
                            File dataSetFile = dataSetFiles[i];
                            try {
                                dataSets[i] = ((DataSetFileFormat) inputFormat).
                                              getDataFile(dataSetFile);
                            } catch (InputFileFormatException iffe) {
                                // Let the user know that there was a problem parsing the file.
                                JOptionPane.showMessageDialog(null,
                                        "The input file does not comply with the designated format.",
                                        "Parsing Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        }
                        if (mergeFiles) {
                            if (dataSets[0] instanceof DSMicroarraySet) {
                                DSMicroarraySet[] maSets = new DSMicroarraySet[
                                        dataSets.length];
                                for (int i = 0; i < dataSets.length; i++) {
                                    maSets[i] = (DSMicroarraySet) dataSets[i];
                                }
                                doMergeSets(maSets);
                            }
                        }
//                        }
                    }
                    progressBar.setString("");
                    progressBar.setIndeterminate(false);
                    jDataSetPanel.setCursor(Cursor.getPredefinedCursor(Cursor.
                            DEFAULT_CURSOR));
                    if (mergeFiles) {
                        // We're done
                        return;
                    }

                    if (mergedSet != null) {
                        mergedSet.setLabel(mergedName);
                        dataSets = new DSDataSet[] {mergedSet};
                    }
                    // If everything went OK, register the newly created microarray set.
                    //String directory = dataSetFile.getPath();
                    //System.setProperty("data.files.dir", directory);
                    boolean selected = false;
                    for (int i = 0; i < dataSets.length; i++) {
                        DSDataSet set = dataSets[i];

                        if (set != null) {
                            // Do intial color context update if it is a microarray
                            if (set instanceof DSMicroarraySet) {
                                DSMicroarraySet maSet = (DSMicroarraySet) set;
                                // Add color context
                                addColorContext(maSet);
                            }
                            //String directory = dataSetFile.getPath();
                            //System.setProperty("data.files.dir", directory);
                            if (!selected) {
                                addDataSetNode(set, true);
                                selected = true;
                            } else {
                                addDataSetNode(set, false);
                            }
                        } else {
                            System.out.println("Datafile not loaded");
                        }
                    }

                }
            };
            Thread t = new Thread(dataLoader);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();

        } else {
            // The call to getMArraySet() may result in an InputFileFormatException
            // which is expected to be handled by the calling function.
            if (dataSetFiles.length == 1) {
                DSMicroarraySet microarrays = inputFormat.getMArraySet(
                        dataSetFiles[0]);
                // If everything went OK, register the newly created microarray set.
                addMicroarrays(microarrays,
                               projectRenderer.projectNodeSelection);
            } else {
                String arrayName = JOptionPane.showInputDialog(
                        "Please enter the name of the Microarry Set");
                DSMicroarraySet mic = inputFormat.getMArraySet(dataSetFiles[0]);
                for (int i = 1; i < dataSetFiles.length; i++) {
                    DSMicroarraySet microarrays = inputFormat.getMArraySet(
                            dataSetFiles[i]);
                    try {
                        mic.mergeMicroarraySet(microarrays);
                    } catch (Exception e) {
                    }
                }
                mic.setLabel(arrayName);
            }
        }
    }

    private void addColorContext(DSMicroarraySet maSet) {
        GlobalPreferences prefs = GlobalPreferences.getInstance();
        Class<? extends ColorContext> type = prefs.getColorContextClass();
        try {
            ColorContext context = type.newInstance();
            maSet.addObject(ColorContext.class, context);
            updateColorContext(maSet);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void fileOpenAction(File dataSetFile,
                               org.geworkbench.engine.parsers.FileFormat
                               inputFormat) throws InputFileFormatException {

        // The call to getMArraySet() may result in an InputFileFormatException
        // which is expected to be handled by the calling function.
        if (inputFormat instanceof DataSetFileFormat) {
            DSDataSet dataSet = ((DataSetFileFormat) inputFormat).getDataFile(
                    dataSetFile);
            // If everything went OK, register the newly created microarray set.
            if (dataSet != null) {
                //String directory = dataSetFile.getPath();
                //System.setProperty("data.files.dir", directory);
                addDataSetNode(dataSet, true);
            } else {
                System.out.println("Could not load file: " + dataSetFile);
            }
        } else {
        }
    }

    /**
     * Action listener handling user requests for renaming a project.
     *
     * @param e
     */
    protected void jRenameProjectItem_actionPerformed(ActionEvent e) {
        if (projectTree == null || selection == null ||
            (selection.areNodeSelectionsCleared()) ||
            selection.getSelectedProjectNode() == null) {
            JOptionPane.showMessageDialog(null, "Select a project node.",
                                          "Rename Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProjectNode project = selection.getSelectedProjectNode();
        String inputValue = JOptionPane.showInputDialog("Project Name:",
                project.toString());
        if (inputValue != null) {
            project.setUserObject(inputValue);
            projectTreeModel.nodeChanged(project);
        }
    }

    /**
     * Action listener handling user requests for removing a dataset.
     *
     * @param e
     */
    protected void fileRemove_actionPerformed(ActionEvent e) {
        if (projectTree == null || selection == null ||
            !((selection.getSelectedNode() instanceof DataSetNode) ||
              (selection.getSelectedNode() instanceof DataSetSubNode))) {
            JOptionPane.showMessageDialog(null, "Select a microarray set.",
                                          "Delete Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProjectTreeNode node = selection.getSelectedNode();
        ProjectTreeNode parentNode = (ProjectTreeNode) node.getParent();

        projectTreeModel.removeNodeFromParent(node);
        if (parentNode.getChildCount() == 0 &&
            parentNode instanceof ProjectNode) {
            if (parentNode.getSiblingCount() > 0) {
                DefaultMutableTreeNode dmtn = null;
                while ((dmtn = parentNode.getNextSibling()) != null) {
                    if (((ProjectNode) dmtn).getChildCount() > 0) {
                        setNodeSelection((DataSetNode) parentNode.getChildAt(0));
                        return;
                    }
                }
            }
            setNodeSelection(parentNode);
            publishProjectEvent(new ProjectEvent(ProjectEvent.CLEARED, null,
                                                 parentNode));
            return;
        } else if (parentNode.getChildCount() > 0 &&
                   parentNode instanceof ProjectNode) {
            setNodeSelection((DataSetNode) parentNode.getChildAt(0));
        } else if (parentNode.getChildCount() > 0 &&
                   parentNode instanceof DataSetNode) {
            setNodeSelection((DataSetSubNode) parentNode.getChildAt(0));
        } else {
            setNodeSelection(parentNode);
        }
    }

    /**
     * Action listener handling user requests for removing a project.
     *
     * @param e
     */
    protected void projectRemove_actionPerformed(ActionEvent e) {
        if (projectTree == null || selection == null ||
            !(selection.getSelectedNode() instanceof ProjectNode)) {
            JOptionPane.showMessageDialog(null, "Select a project node.",
                                          "Delete Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProjectNode project = selection.getSelectedProjectNode();

        projectTreeModel.removeNodeFromParent(project);
        // If there are any remaining projects, select the first of them to
        // be the next one to get the focus.
        if (root.getChildCount() > 0) {
            ProjectTreeNode pNode = (ProjectTreeNode) root.getChildAt(0);
            if (pNode.getChildCount() > 0) {
                setNodeSelection((DataSetNode) pNode.getChildAt(0));
            }
        } else {
            clear();
        }
    }

    /**
     * Action listener handling user requests for renaming a dataset.
     *
     * @param e
     */
    protected void jRenameDataset_actionPerformed(ActionEvent e) {
        if (projectTree == null || selection == null ||
            (selection.areNodeSelectionsCleared())) {
            JOptionPane.showMessageDialog(null,
                    "Select a dataset or ancillary dataset.", "Rename Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        DSDataSet ds = null;
        ProjectTreeNode dsNode = null;

        if (selection.getSelectedNode() instanceof DataSetNode) {
            ds = selection.getDataSet();
            dsNode = selection.getSelectedDataSetNode();
        } else if (selection.getSelectedNode() instanceof DataSetSubNode) {
            dsNode = selection.getSelectedDataSetSubNode();
            ds = selection.getDataSubSet();
        }
        if (ds != null && dsNode != null) {
            String inputValue = JOptionPane.showInputDialog("Dataset Name:",
                    dsNode.toString());
            if (inputValue != null) {
                dsNode.setUserObject(inputValue);
                ds.setLabel(inputValue);
                projectTreeModel.nodeChanged(dsNode);
            }
        }
    }

    protected void export_actionPerformed(ActionEvent e) {
        // Save an image
        ProjectTreeNode ds = selection.getSelectedNode();
        if (ds != null) {
            if (ds instanceof ImageNode) {
                Image currentImage = ((ImageNode) ds).image.getImage();
                SaveImage si = new SaveImage(currentImage);
                JFileChooser fc = new JFileChooser(".");
                String imageFilename = null;
                String filename = null, ext = null;
                FileFilter bitmapFilter = new BitmapFileFilter();
                FileFilter jpegFilter = new JPEGFileFilter();
                FileFilter pngFilter = new PNGFileFilter();
                FileFilter tiffFilter = new TIFFFileFilter();
                fc.setFileFilter(tiffFilter);
                fc.setFileFilter(pngFilter);
                fc.setFileFilter(jpegFilter);
                fc.setFileFilter(bitmapFilter);
                int choice = fc.showSaveDialog(jProjectPanel);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    imageFilename = fc.getSelectedFile().getAbsolutePath();
                    filename = fc.getSelectedFile().getName();
                    int i = filename.lastIndexOf('.');
                    if (i > 0 && i < filename.length() - 1) {
                        ext = filename.substring(i + 1).toLowerCase();
                    } else {
                        ImageFileFilter selectedFilter = null;
                        FileFilter filter = fc.getFileFilter();
                        if (filter instanceof ImageFileFilter) {
                            selectedFilter = (ImageFileFilter) fc.getFileFilter();
                            ext = selectedFilter.getExtension();
                            System.out.println("File extension: " + ext);
                        }
                    }
                }
                if (imageFilename != null) {
                    si.save(imageFilename, ext);
                }
            }
        }
    }

    /**
     * Action listener handling user requests for the creation of new projects in
     * the workspace.
     *
     * @param e
     */
    protected void jNewProjectItem_actionPerformed(ActionEvent e) {
        ProjectNode childNode = new ProjectNode("Project");
        addToProject(childNode, true);
    }

    /**
     * Used to add a new node to a project tree
     *
     * @param child           The node to be added
     * @param shouldBeVisible wether it should be visible or not
     * @return
     */
    public ProjectNode addToProject(ProjectNode child, boolean shouldBeVisible) {
        //ProjectNodeOld childNode = new ProjectNodeOld(child);
        projectTreeModel.insertNodeInto(child, root, root.getChildCount());
        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            projectTree.scrollPathToVisible(new TreePath(child.getPath()));
            projectTree.setSelectionPath(new TreePath(child.getPath()));
            selection.setNodeSelection(child);
        }
        //    serialize("default.wsp");
        return child;
    }

    /**
     * Sets the currently selected node within the project tree.
     *
     * @param node The project tree node to show up as selected.
     */
    protected void setNodeSelection(ProjectTreeNode node) {

        if (node == null) {

            return;
        }
        selection.setNodeSelection(node);
        projectTree.setSelectionPath(new TreePath(node.getPath()));
        // todo - watkin - replace with a more appropriate event firing
        // projectTreeModel.nodeStructureChanged(node);
    }

    public ProjectSelection getSelection() {
        return selection;
    }

    //----------------------------------------------------------------------

    protected ProjectTreeNode selectedNode = null;
    protected MicroarraySetNode previousMANode = null;

    @Publish public ProjectEvent publishProjectEvent(ProjectEvent event) {
        return event;
    }

    @Publish public CommentsEvent publishCommentsEvent(CommentsEvent event) {
        return event;
    }

    /**
     * Throws an application event that designates the selection of a
     * project or microarray node in the project window.
     *
     * @param node
     */
    protected void fireNodeSelectionEvent(ProjectTreeNode node) {
        if (node != null && node != root) {
            publishProjectEvent(new ProjectEvent(node instanceof ProjectNode ?
                                                 "Project Node Selected" :
                                                 "Microarray Node Selected",
                                                 projectRenderer.
                                                 microarraySetNodeSelection == null ? null :
                                                 projectRenderer.
                                                 microarraySetNodeSelection.
                                                 getMicroarraySet(),
                                                 projectRenderer.
                                                 microarraySetNodeSelection));
            sendCommentsEvent(node);
        }
    }

    public void sendCommentsEvent(ProjectTreeNode forNode) {
        String description = forNode.getDescription();
        if (description == null) {
            description = "";
        }
        publishCommentsEvent(new CommentsEvent(description));
    }

    /**
     * Adds the desiganted microarray set <code>maSet</code> as a child node to
     * the tree node <code>parent</code>. Makes the newly added node the one
     * currently selected.
     *
     * @param maSet  The microarray set to add.
     * @param parent The parent tree node.
     */
    protected void addMicroarrays(DSMicroarraySet maSet, ProjectTreeNode parent) {
        if (projectTree == null || parent == null) {
            return;
        }
        MicroarraySetNode node = new MicroarraySetNode(maSet);
        projectTreeModel.insertNodeInto(node, parent, parent.getChildCount());
        setNodeSelection(node);
        fireNodeSelectionEvent(node);
    }

    private void updateColorContext(DSMicroarraySet maSet) {
        ColorContext colorContext = (ColorContext) maSet.getObject(ColorContext.class);
        if (colorContext != null) {
            CSMicroarraySetView view = new CSMicroarraySetView(maSet);
            colorContext.updateContext(view);
        }
    }

    /**
     * Interface <code>ImageSnapshotListener</code> method for receiving
     * <code>ImageSnapshotEvent</code> from Visual Plugins. These events contain
     * <code>ImageIcon</code> representing visual state of the plugins throwing
     * this event.
     *
     * @param event <code>ImageSnapshotEvent</code>
     */
    @Subscribe
            public void receive(ImageSnapshotEvent event, Object source) {
        if (event.getAction() == ImageSnapshotEvent.Action.SAVE) {
            TreePath path = projectTree.getSelectionPath();
            if (path != null) {
                ImageNode imageNode = new ImageNode(event.getImage());
                ProjectTreeNode node = (ProjectTreeNode) path.
                                       getLastPathComponent();
                projectRenderer.imageNodeSelection = imageNode;
                if (node instanceof DataSetNode) {
                    projectTreeModel.insertNodeInto(imageNode, node,
                            node.getChildCount());
                } else if (node instanceof ImageNode) {
                    node = projectRenderer.microarraySetNodeSelection;
                    projectTreeModel.insertNodeInto(imageNode, node,
                            node.getChildCount());
                } else if (node instanceof DataSetSubNode) {
                    DataSetSubNode subNode = (DataSetSubNode) node;
                    node = (ProjectTreeNode) subNode.getParent();
                    if (node instanceof DataSetNode) {
                        projectTreeModel.insertNodeInto(imageNode, node,
                                node.getChildCount());
                    }
                }

            }
        } else {
            // Ignore all other actions.
        }
    }

    @Subscribe
            public void receive(org.geworkbench.events.PhenotypeSelectorEvent e,
                                Object source) {
        if (e.getDataSet() instanceof DSMicroarraySet) {
            DSMicroarraySet microarraySet = (DSMicroarraySet) e.getDataSet();
            updateColorContext(microarraySet, e);
        }
    }

    private void updateColorContext(DSMicroarraySet microarraySet,
                                    org.geworkbench.events.
                                    PhenotypeSelectorEvent e) {
        ColorContext colorContext = (ColorContext) microarraySet.getObject(
                ColorContext.class);
        if (colorContext != null) {
            CSMicroarraySetView view = new CSMicroarraySetView(microarraySet);
            view.useItemPanel(true);
            if (e.getTaggedItemSetTree() != null &&
                e.getTaggedItemSetTree().size() > 0) {
                DSPanel activatedArrays = e.getTaggedItemSetTree().activeSubset();
                view.setItemPanel(activatedArrays);
            }
            colorContext.updateContext(view);
        }
    }

    /**
     * Method for receiving Dataset
     * annotations from the <code>CommentsPane</code>
     *
     * @param ce <code>CommentsEventOld</code> thrown by <code>CommentsPane</code>
     */
//    @Subscribe
//    public void receive(org.geworkbench.events.CommentsEventOld ce, Object source) {
//        // Do no bother if the comment change is not for currently selected
//        // microarray set.
//        if (ce == null || ce.getMicroarray() == null || !(selectedNode instanceof MicroarraySetNode) || projectRenderer.microarraySetNodeSelection == null || projectRenderer.microarraySetNodeSelection.getMicroarraySet() != ce.getMicroarray()) {
//            return;
//        }
//
//        // Otherwise, mark that the selected node had its comments modified. This
//        // information will be needed in the method checkModifiedMASet(), in order
//        // to decide if the microarray set should be persisted.
//        DSMicroarraySet temp = projectRenderer.microarraySetNodeSelection.getMicroarraySet();
//        temp.addNameValuePair(COMMENTS_MODIFIED, new Boolean(true));
//    }

    /**
     * Method for receiving
     * <code>TableChangeEvent</code> from the <code>TabularView</code> widget.
     * This event contains dataset as altered by the user
     *
     * @param tce <code>TableChangeEvent</code> from the <code>TabularView</code>
     *            widget
     */
    @Subscribe
            public void receive(SingleValueEditEvent tce, Object source) {
        DSDataSet changedMASet = tce.getReferenceMicroarraySet();
        // This component only handles changes to the currently selected
        // micorarray.
        MicroarraySetNode selectedNode = projectRenderer.
                                         microarraySetNodeSelection;
        if (selectedNode != null &&
            selectedNode.getMicroarraySet() == changedMASet) {
            // Update the "history" information to mirror the editing activity.
            Object[] prevHistory = changedMASet.getValuesForName(HISTORY);
            changedMASet.clearName(HISTORY);
            changedMASet.addNameValuePair(HISTORY,
                                          (prevHistory == null ? "" :
                                           (String) prevHistory[0]) +
                                          "Signal for marker " + " edited to " +
                                          tce.getNewValue().getValue() + "\n");
            fireNodeSelectionEvent(selectedNode);
        }

    }

    /**
     * For receiving micorarray name change events.
     *
     * @param mnce <code>MicroarrayNameChangeEvent</code> containing the
     *             micorarray that was renamed.
     */
    @Subscribe
            public void receive(MicroarrayNameChangeEvent mnce, Object source) {
        //    DSMicroarraySet changedMASet = mnce.getMicroarray().getMicroarraySet();
        MicroarraySetNode selectedNode = projectRenderer.
                                         microarraySetNodeSelection;
        // This component only handles changes to the currently selected
        // micorarray.
        if (selectedNode != null) { //&& selectedNode.getMicroarraySet() == changedMASet) {
            // If the microarray is already dirty, then the change in the name
            // will be persisted when we change the selected microarray set. We
            // only need to handle the case when this is a "clean" array
            //      if (! ( (MAMemoryStatus) changedMASet).isDirty()) {
            selectedNode.persist();
            //      }

            fireNodeSelectionEvent(selectedNode);
        }

    }

    /**
     * For receiving the results of applying
     * a normalizer to a microarray set.
     *
     * @param ne
     */
    @Subscribe
            public void receive(NormalizationEvent ne, Object source) {
        if (ne == null) {
            return;
        }
        DSMicroarraySet sourceMA = ne.getOriginalMASet();
        if (sourceMA == null) {
            return;
        }
        DSMicroarraySet resultMA = ne.getNormalizedMASet();
        updateColorContext(resultMA);
        // Set up the "history" information for the new dataset.
        Object[] prevHistory = sourceMA.getValuesForName(HISTORY);
        if (prevHistory != null) {
            sourceMA.clearName(HISTORY);
        }
        sourceMA.addNameValuePair(HISTORY,
                                  (prevHistory == null ? "" :
                                   (String) prevHistory[0]) +
                                  "Normalized with " + ne.getInformation() +
                                  "\n");
        // Notify interested components that the selected dataset has changed
        // The event is thrown only if the normalized dataset is the one
        // currently selectd in the project panel.
        DSDataSet currentDS = (selection != null ? selection.getDataSet() : null);
        if (currentDS != null && currentDS instanceof DSMicroarraySet &&
            (DSMicroarraySet) currentDS == sourceMA) {
            publishProjectEvent(new ProjectEvent(ProjectEvent.SELECTED,
                                                 sourceMA, selectedNode));
        }
    }

    /**
     * For receiving the results of applying
     * a filter to a microarray set.
     *
     * @param fe
     */
    @Subscribe
            public void receive(org.geworkbench.events.FilteringEvent fe,
                                Object source) {
        if (fe == null) {
            return;
        }
        DSMicroarraySet sourceMA = fe.getOriginalMASet();
        if (sourceMA == null) {
            return;
        }
        // Set up the "history" information for the new dataset.
        Object[] prevHistory = sourceMA.getValuesForName(HISTORY);
        if (prevHistory != null) {
            sourceMA.clearName(HISTORY);
        }
        sourceMA.addNameValuePair(HISTORY,
                                  (prevHistory == null ? "" :
                                   (String) prevHistory[0]) + "Filtered with " +
                                  fe.getInformation() + "\n");
        // Notify interested components that the selected dataset has changed
        // The event is thrown only if the dataset filtered is the one
        // currently selectd in the project panel.
        DSDataSet currentDS = (selection != null ? selection.getDataSet() : null);

        if (currentDS != null && currentDS instanceof DSMicroarraySet &&
            (DSMicroarraySet) currentDS == sourceMA) {
            publishProjectEvent(new ProjectEvent(ProjectEvent.SELECTED,
                                                 sourceMA, selectedNode));
        }
    }

    /**
     * Clears the current workspace from the project window and notifies all
     * componets that have registered to receive workspace clearing events.
     */
    protected void clear() {
        if (root != null) {
            root.removeAllChildren();
            projectTreeModel.reload(root);
            projectRenderer.clearNodeSelections();
            selectedNode = null;
        }

        publishProjectEvent(new ProjectEvent(ProjectEvent.CLEARED, null, null));
        selection.clearNodeSelections();
    }

    /**
     * Used as the "name" in the name-value pair that keeps track of the history
     * of changes that a given dataset is being submitted to.
     */
    public static final String HISTORY = "History";

    /**
     * Used as the "name" in the name-value pair that keeps track of the comments
     * of a microarray set have been modified.
     */
    protected final String COMMENTS_MODIFIED = "Comments modified";

    @Publish
            public ImageSnapshotEvent publishImageSnapshot(ImageSnapshotEvent
            event) {
        return event;
    }

    /**
     * Store the currently loaded workspace to a file withe the designated
     * name.
     *
     * @param filename
     */
    protected void serialize(String filename) {
        try {
            FileOutputStream f = new FileOutputStream(filename);
            ObjectOutput s = new ObjectOutputStream(f);
            s.writeObject(root);
            s.flush();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }

    }

    /**
     * Interface <code>MenuListener</code> method that returns the appropriate
     * <code>ActionListener</code> to handle <code>MenuEvent</code> generated by
     * <code>MenuItem</code> referenced by <code>menuKey</code> attribute
     *
     * @param menuKey refers to <code>MenuItem</code>
     * @return <ActionListener> to handle <code>MenuEvent</code> generated by
     *         <code>MenuItem</code>
     */
    public ActionListener getActionListener(String menuKey) {
        return (ActionListener) listeners.get(menuKey);
    }

    /**
     * Interface <code>VisualPlugin</code> method that returns a
     * <code>Component</code> which is the visual representation of
     * the this plugin.
     *
     * @return <code>Component</code> visual representation of
     *         <code>ProjectPane</code>
     */
    public Component getComponent() {
        return jProjectPanel;
    }

    class WorkspaceFileFilter extends FileFilter {
        String fileExt;

        WorkspaceFileFilter() {
            fileExt = ".wsp";
        }

        public String getExtension() {
            return fileExt;
        }

        public String getDescription() {
            return "Workspace Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            if (f.isDirectory() || f.getName().endsWith(fileExt)) {
                return true;
            }

            return returnVal;
        }

    }


    public abstract class ImageFileFilter extends FileFilter {
        public abstract String getExtension();
    }


    public class BitmapFileFilter extends ImageFileFilter {
        public String getDescription() {
            return "Bitmap Files";
        }

        public boolean accept(File f) {
            String name = f.getName();
            boolean imageFile = name.endsWith("bmp") || name.endsWith("BMP");
            if (f.isDirectory() || imageFile) {
                return true;
            }

            return false;
        }

        public String getExtension() {
            return "bmp";
        }

    }


    public class JPEGFileFilter extends ImageFileFilter {
        public String getDescription() {
            return "Joint Photographic Experts Group Files";
        }

        public boolean accept(File f) {
            String name = f.getName();
            boolean imageFile = name.endsWith("jpg") || name.endsWith("JPG");
            if (f.isDirectory() || imageFile) {
                return true;
            }

            return false;
        }

        public String getExtension() {
            return "jpg";
        }

    }


    public class PNGFileFilter extends ImageFileFilter {
        public String getDescription() {
            return "Portable Network Graphics Files";
        }

        public boolean accept(File f) {
            String name = f.getName();
            boolean imageFile = name.endsWith("png") || name.endsWith("PNG");
            if (f.isDirectory() || imageFile) {
                return true;
            }

            return false;
        }

        public String getExtension() {
            return "png";
        }

    }


    public class TIFFFileFilter extends ImageFileFilter {
        public String getDescription() {
            return "Tag(ged) Image File Format";
        }

        public boolean accept(File f) {
            String name = f.getName();
            boolean imageFile = name.endsWith("tif") || name.endsWith("TIF") ||
                                name.endsWith("tiff") || name.endsWith("TIFF");
            if (f.isDirectory() || imageFile) {
                return true;
            }

            return false;
        }

        public String getExtension() {
            return "tif";
        }

    }


    /**
     * <code>JComponent</code> types that constitute the <code>ProjectPanel</code>
     */
    protected JPanel jProjectPanel = new JPanel();
    protected JPanel jDataSetPanel = new JPanel();
    protected BorderLayout borderLayout2 = new BorderLayout();
    protected BorderLayout borderLayout4 = new BorderLayout();
    protected GridBagLayout gridBagLayout3 = new GridBagLayout();
    protected JPanel jDataPane = new JPanel();
    protected JLabel jDataSetLabel = new JLabel();
    protected JScrollPane jDataSetScrollPane = new JScrollPane();
    protected BorderLayout borderLayout1 = new BorderLayout();
    protected ProjectTreeNode root = new ProjectTreeNode("Workspace");
    protected DefaultTreeModel projectTreeModel = new DefaultTreeModel(root);
    protected JTree projectTree = new JTree(projectTreeModel);
    protected TreeNodeRenderer projectRenderer = new TreeNodeRenderer(selection);
    protected JPopupMenu jRootMenu = new JPopupMenu();
    protected JPopupMenu jProjectMenu = new JPopupMenu();
    protected JPopupMenu jMArrayMenu = new JPopupMenu();
    protected JMenuItem jLoadProjectItem = new JMenuItem();
    protected JMenuItem jNewProjectItem = new JMenuItem();
    protected JMenuItem jLoadMArrayItem = new JMenuItem();
    protected JMenuItem jLoadRemoteMArrayItem = new JMenuItem();
    protected JMenuItem jMergeDatasets = new JMenuItem();
    protected JMenuItem jRenameProjectItem = new JMenuItem();
    protected JMenuItem jRemoveDataSetItem = new JMenuItem();
    protected JMenuItem jRenameDataset = new JMenuItem();
    /**
     * PlaceHolder for <code>JComponent</code> listeners to be added to the
     * application's <code>JMenuBar</code> through the application configuration
     * functionality
     */
    protected HashMap listeners = new HashMap();

    protected void jbInit() throws Exception {
        ActionListener listener = null;
        jDataSetPanel.setLayout(borderLayout4);
        jDataPane.setLayout(gridBagLayout3);
        jDataSetScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        jDataSetScrollPane.setMinimumSize(new Dimension(122, 80));
        jDataSetLabel.setBorder(BorderFactory.createEtchedBorder());
        jDataSetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jDataSetLabel.setText("Project Folders");
        ToolTipManager.sharedInstance().registerComponent(projectTree);
        projectTree.setCellRenderer(projectRenderer);
        projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.
                DISCONTIGUOUS_TREE_SELECTION);
        jRenameProjectItem.setText("Rename Project");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRenameProjectItem_actionPerformed(e);
            }

        };
        listeners.put("Edit.Rename.Project", listener);
        jRenameProjectItem.addActionListener(listener);
        jRenameDataset.setText("Rename File");
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRenameDataset_actionPerformed(e);
            }

        };
        listeners.put("Edit.Rename.File", listener);
        jRenameDataset.addActionListener(listener);
        jDataPane.add(jDataSetPanel,
                      new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0,
                                             GridBagConstraints.WEST,
                                             GridBagConstraints.BOTH,
                                             new Insets(0, 0, 0, 0), 0, 0));
        jDataSetPanel.add(jDataSetLabel, BorderLayout.NORTH);
        jDataSetPanel.add(jDataSetScrollPane, BorderLayout.CENTER);
        projectTree.setBorder(new EmptyBorder(1, 1, 0, 0));
        jDataSetScrollPane.getViewport().add(projectTree, null);
        jDataSetPanel.add(jDataSetScrollPane, BorderLayout.CENTER);
        jProjectPanel.setLayout(borderLayout2);
        jProjectPanel.add(jDataPane, BorderLayout.CENTER);
        jProjectPanel.setName("Projects");
        jNewProjectItem.setText("New Project");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jNewProjectItem_actionPerformed(e);
            }

        };
        listeners.put("File.New.Project", listener);
        jNewProjectItem.addActionListener(listener);
        jLoadMArrayItem.setText("Open File(s)");
        listener = new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jLoadMArrayItem_actionPerformed(e);
            }

        };
        listeners.put("File.Open.File", listener);
        jLoadMArrayItem.addActionListener(listener);
        jMergeDatasets.setText("Merge Files");
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMergeDatasets_actionPerformed(e);
            }

        };
        listeners.put("File.Merge Datasets", listener);
        jMergeDatasets.addActionListener(listener);
        jRootMenu.add(jNewProjectItem);
        jProjectMenu.add(jLoadMArrayItem);
        jProjectMenu.addSeparator();
        jProjectMenu.add(jRenameProjectItem);
        jMArrayMenu.add(jMergeDatasets);
        jMArrayMenu.add(jRenameDataset);
        projectTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jProjectTree_mouseClicked(e);
            }

            public void mouseReleased(MouseEvent e) {
                jProjectTree_mouseReleased(e);
            }

        });
        // Add the action listeners that respond to the various menu selections
        // and popup selections.
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveWorkspace_actionPerformed(e);
            }

        };
        listeners.put("File.Save.Workspace", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openWorkspace_actionPerformed(e);
            }

        };
        listeners.put("File.Open.Workspace", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newWorkspace_actionPerformed(e);
            }

        };
        listeners.put("File.New.Workspace", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                projectRemove_actionPerformed(e);
            }

        };
        listeners.put("File.Remove.Project", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileRemove_actionPerformed(e);
            }

        };
        listeners.put("File.Remove.File", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                imageRemove_actionPerformed(e);
            }

        };
        listeners.put("File.Remove.Image", listener);
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                export_actionPerformed(e);
            }

        };
        listeners.put("File.Export", listener);
        // Attempt to create the directory where temp files will be saved.
        String tempFileDirectory = System.getProperty(
                "temporary.files.directory");
        if (tempFileDirectory != null) {
            File td = new File(tempFileDirectory);
            if (!td.exists()) {
                td.mkdirs();
            }

        }

    }

    protected void saveWorkspace_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(".");
        String wsFilename = null;
        FileFilter filter = new WorkspaceFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Save Current Workspace");
        String extension = ((WorkspaceFileFilter) filter).getExtension();
        int choice = fc.showSaveDialog(jProjectPanel);
        if (choice == JFileChooser.APPROVE_OPTION) {
            wsFilename = fc.getSelectedFile().getAbsolutePath();
            if (!wsFilename.endsWith(extension)) {
                wsFilename += extension;
            }

            serialize(wsFilename);
        }

    }

    protected void openWorkspace_actionPerformed(ActionEvent e) {
        // Prompt user for designating the file containing the workspace to be opened.
        JFileChooser fc = new JFileChooser(".");
        String wsFilename = null;
        FileFilter filter = new WorkspaceFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Open Workspace");
        String extension = ((WorkspaceFileFilter) filter).getExtension();
        int choice = fc.showOpenDialog(jProjectPanel);
        if (choice == JFileChooser.APPROVE_OPTION) {
            wsFilename = fc.getSelectedFile().getAbsolutePath();
            if (!wsFilename.endsWith(extension)) {
                wsFilename += extension;
            }

            try {
                FileInputStream in = new FileInputStream(wsFilename);
                ObjectInputStream s = new ObjectInputStream(in);
                ProjectTreeNode tempNode = (ProjectTreeNode) s.readObject();
                // Clean up local structures and notify interested components to clean
                // themselves up.
                clear();
                root = tempNode;
                projectRenderer.clearNodeSelections();
                projectTreeModel = new DefaultTreeModel(root);
                projectTree.setModel(projectTreeModel);
            } catch (Exception exc) {
                exc.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Check that the file contains a valid workspace.",
                                              "Open Workspace Error",
                                              JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    protected void newWorkspace_actionPerformed(ActionEvent e) {
        clear();
    }

    protected void imageRemove_actionPerformed(ActionEvent e) {
        if (projectTree == null || projectTree.getSelectionPath() == null ||
            !(projectTree.getSelectionPath().getLastPathComponent() instanceof
              ImageNode)) {
            JOptionPane.showMessageDialog(null, "Select an image node.",
                                          "Delete Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImageNode image = (ImageNode) projectTree.getSelectionPath().
                          getLastPathComponent();
        ProjectTreeNode parent = (ProjectTreeNode) image.getParent();
        projectTreeModel.removeNodeFromParent(image);
        // Set the selected node to be the image's parent (a microarrayset
        // node).
        setNodeSelection(parent);
        //    fireNodeSelectionEvent((ProjectTreeNode) root.getChildAt(0));
        publishImageSnapshot(new ImageSnapshotEvent("ImageSnapshot", null,
                ImageSnapshotEvent.Action.SHOW));
    }

    @Script
            public void loadDataSet(String filename, FileFormat inputFormat) throws
            Exception {
        File[] dataSetFiles = new File[1];
        dataSetFiles[0] = new File(filename);
        if (inputFormat instanceof DataSetFileFormat) {
            progressBar.setStringPainted(true);
            progressBar.setString("Loading");
            progressBar.setIndeterminate(true);
            jDataSetPanel.setCursor(Cursor.getPredefinedCursor(Cursor.
                    WAIT_CURSOR));
            DSDataSet dataSet;
            if (dataSetFiles.length == 1) {
                dataSet = ((DataSetFileFormat) inputFormat).getDataFile(
                        dataSetFiles[0]);
            } else {
                dataSet = ((DataSetFileFormat) inputFormat).getDataFile(
                        dataSetFiles);
            }
            if (dataSet instanceof DSMicroarraySet) {
                addColorContext((DSMicroarraySet) dataSet);
            }
            progressBar.setString("");
            progressBar.setIndeterminate(false);
            jDataSetPanel.setCursor(Cursor.getPredefinedCursor(Cursor.
                    DEFAULT_CURSOR));

            // If everything went OK, register the newly created microarray set.
            if (dataSet != null) {
                //String directory = dataSetFile.getPath();
                System.out.println("data set parsed");
                jNewProjectItem_actionPerformed(null);
                addDataSetNode(dataSet, true);
            } else {
                System.out.println("Could not load file: " + dataSetFiles);
            }
        } else {
            // super.fileOpenAction(dataSetFiles, inputFormat);
        }
    }

    @Script
            public DSDataSet getDataSet() {
        return selection.getDataSet();
    }

}
