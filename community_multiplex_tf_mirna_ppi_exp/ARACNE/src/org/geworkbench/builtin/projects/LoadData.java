package org.geworkbench.builtin.projects;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.parsers.FileFormat;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Popup to select a file (local or remote) to open.
 */
public class LoadData extends JDialog {
    private BorderLayout borderLayout1 = new BorderLayout();
    private JPanel jPanel1 = new JPanel();
    private GridLayout gridLayout1 = new GridLayout();
    private JPanel jPanel2 = new JPanel();
    private JPanel jPanel3 = new JPanel();
    private FlowLayout flowLayout1 = new FlowLayout();
    private FlowLayout flowLayout2 = new FlowLayout();
    private JRadioButton jRadioButton1 = new JRadioButton();
    private JRadioButton jRadioButton2 = new JRadioButton();
    private JPanel jPanel4 = new JPanel();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JFileChooser jFileChooser1 = new JFileChooser();
    private JPanel jPanel5 = new JPanel();
    private ButtonGroup buttonGroup1 = new ButtonGroup();
    private ButtonGroup buttonGroup2 = new ButtonGroup();
    private ButtonGroup buttonGroup3 = new ButtonGroup();
    private JLabel jLabel3 = new JLabel();
    private JPanel jPanel9 = new JPanel();
    private JPanel jPanel11 = new JPanel();
    private JPanel jPanel12 = new JPanel();
    private JRadioButton jRadioButton6 = new JRadioButton();
    private JLabel jLabel2 = new JLabel();
    private JRadioButton jRadioButton5 = new JRadioButton();
    private GridLayout gridLayout3 = new GridLayout();
    private JRadioButton jRadioButton4 = new JRadioButton();
    private JRadioButton jRadioButton3 = new JRadioButton();
    private JLabel jLabel1 = new JLabel();
    private GridLayout gridLayout4 = new GridLayout();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel jLabel4 = new JLabel();
    private JTextArea experimentInfoArea = new JTextArea();
    private GridLayout gridLayout2 = new GridLayout();
    private GridLayout grid4 = new GridLayout();
    private String format = null;

    private JCheckBox mergeCheckBox;
    private JPanel lowerPanel;
    private JPanel mergePanel;
    private JPanel remoteControlPanel;

    private JTabbedPane lowTabPane;
    private JPanel caArrayIndexServicePanel;
    private final String DEFAULTINDEXURL =
            "http://156.145.29.52/ogsa/services/edu/columbia/CaARRAYIndexService";
    //private final String DEFAULTINDEXURL = "http://splashgrid.cu-genome.org/ogsa/services/edu/columbia/CaARRAYIndexService";

    private String indexURL = DEFAULTINDEXURL;
    private JTextField indexField;
    private JButton updateIndexButton;


    /**
     * The project panel that manages the dialog box.
     */
    public ProjectPanel parentProjectPanel = null;

    /**
     * Stores the <code>FileFormat</code> objects for the supported input
     * formats.
     */
    private FileFormat[] supportedInputFormats = null;
    BorderLayout borderLayout7 = new BorderLayout();
    JPanel jPanel7 = new JPanel();
    JRadioButton jRadioButton7 = new JRadioButton();
    JRadioButton jRadioButton8 = new JRadioButton();
    JPanel jPanel10 = new JPanel();

    String[] DEFAULTRESOUCES = new String[] {"caARRAY", "GEDP"};
    DefaultComboBoxModel resourceModel = new DefaultComboBoxModel(
            DEFAULTRESOUCES);
    JComboBox jComboBox1 = new JComboBox(resourceModel);

    JButton addButton = new JButton();
    JButton editButton = new JButton();
    JButton deleteButton = new JButton();
    BoxLayout boxLayout21;
    JButton openRemoteResourceButton = new JButton();
    private boolean merge;

    public LoadData(ProjectPanel parent) {
        parentProjectPanel = parent;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoadData() {
        // parentProjectPanel = new ProjectPanel();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDirectory(String directory) {
        jFileChooser1.setCurrentDirectory(new File(directory));
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    private void jbInit() throws Exception {
        // format = getLastDataFormat();


        //change it to false in order to provide a progress bar for loading remote data.
        this.setModal(false);
        this.getContentPane().setLayout(borderLayout1);

        jPanel1.setLayout(gridLayout1);
        jPanel2.setLayout(flowLayout1);
        jPanel3.setLayout(flowLayout2);
        jRadioButton1.setText("Local File");
        jRadioButton2.setText("GEDP");
        jPanel4.setLayout(borderLayout2);
        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel3.setText("Select column to use");
        grid4.setColumns(2);
        grid4.setHgap(10);
        grid4.setRows(1);
        grid4.setVgap(10);
        jPanel5.setLayout(gridBagLayout1);
        jRadioButton6.setDebugGraphicsOptions(0);
        jRadioButton6.setText("Mean");
        jRadioButton6.setSelected(true);
        jLabel2.setText("GenePix");
        jRadioButton5.setText("Median");
        jPanel12.setLayout(gridLayout3);
        gridLayout3.setColumns(1);
        gridLayout3.setRows(3);
        jRadioButton4.setText("Signal");
        jRadioButton4.setSelected(true);
        jRadioButton3.setText("Log Average");
        jLabel1.setText("Affymetrix");
        jPanel11.setLayout(gridLayout4);
        gridLayout4.setColumns(1);
        gridLayout4.setRows(3);
        jPanel9.setLayout(gridLayout2);
        jLabel4.setMaximumSize(new Dimension(200, 15));
        jLabel4.setMinimumSize(new Dimension(200, 15));
        jLabel4.setPreferredSize(new Dimension(200, 15));
        jLabel4.setText("Experiment Information:");
        experimentInfoArea.setPreferredSize(new Dimension(300, 300));
        experimentInfoArea.setText("");
        experimentInfoArea.setEditable(false);
        experimentInfoArea.setLineWrap(true);
        experimentInfoArea.setWrapStyleWord(true);
        gridLayout2.setColumns(1);
        this.setResizable(false);
        jRadioButton7.setText("caARRAY");
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        addButton.setToolTipText("Add a new resource");
        addButton.setText("Add A New Resource");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }

        });
        editButton.setToolTipText("Edit an existed source");
        editButton.setText("Edit");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        deleteButton.setToolTipText("Delete an existed resource");
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //  displayRemoteResourceDiolog(jComboBox1.getSelectedItem(), RemoteResourceDialog.DELETE);
                deleteButton_actionPerformed(e);
            }

        });
        boxLayout21 = new BoxLayout(jPanel10, BoxLayout.X_AXIS);
        jPanel10.setLayout(boxLayout21);
        openRemoteResourceButton.setText("Go");
        openRemoteResourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRemoteResourceButton_actionPerformed(e);
            }
        });
        jComboBox1.setPreferredSize(new Dimension(50, 19));

        mergePanel = new JPanel();
        mergePanel.setLayout(new BoxLayout(mergePanel, BoxLayout.X_AXIS));
        mergeCheckBox = new JCheckBox("Merge Files", false);
        mergeCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        merge = mergeCheckBox.isSelected();
                                         }
        });
        //jComboBox1 = new JComboBox(remoteResourceDialog.getResourceNames());
        //jComboBox1 = new JComboBox(new String[]{"TEST"});
        jPanel10.add(jComboBox1);
        jPanel10.add(openRemoteResourceButton);
        jPanel10.add(addButton);
        jPanel10.add(editButton);
        jPanel10.add(deleteButton);
        remoteControlPanel = new JPanel();
        //lowerPanel.add(mergePanel);
        mergePanel.add(mergeCheckBox);
        mergePanel.add(Box.createGlue());
        lowerPanel.add(jPanel1);
        //lowerPanel.add(jPanel10);
        lowTabPane = new JTabbedPane();
        lowTabPane.add(lowerPanel, "Main");
        caArrayIndexServicePanel = new JPanel();
        lowTabPane.add(caArrayIndexServicePanel, "caARRAY Index Service");
        lowTabPane.setSelectedIndex(0);
        indexField = new JTextField(indexURL);
        updateIndexButton = new JButton("Update");
        caArrayIndexServicePanel.add(indexField);
        caArrayIndexServicePanel.add(updateIndexButton);

        updateIndexButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        // this.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
        //this.getContentPane().add(lowTabPane, BorderLayout.SOUTH);
        //Merge Merge panel and Jpanel 1 in 1 line.
        jPanel1.add(mergePanel);
        jPanel1.add(jPanel2, null);
        jPanel2.add(jRadioButton1, null);
        //removed by XQ
//        jPanel1.add(jPanel7, null);
//       jPanel7.add(jRadioButton7, null);
//        jPanel1.add(jPanel3, null);
//        jPanel3.add(jRadioButton2, null);
        //deletion ends here.
        jPanel1.add(jPanel7, null);
        jRadioButton8.setText("Remote");
        jPanel7.add(jRadioButton8, null);

        //XQ modification ends here.
        this.getContentPane().add(jPanel4, BorderLayout.CENTER);
        jPanel4.add(jFileChooser1, BorderLayout.CENTER);
        jPanel5.add(jLabel3,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                           GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0), 0, 17));
        jPanel5.add(jPanel12,
                    new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 0, 23, 0), 27, 30));
        jPanel12.add(jLabel2, null);
        jPanel12.add(jRadioButton6, null);
        jPanel12.add(jRadioButton5, null);
        jPanel5.add(jPanel11,
                    new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                                           GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(0, 0, 86, 0), 27, 30));
        jPanel11.add(jLabel1, null);
        jPanel11.add(jRadioButton4, null);
        jPanel11.add(jRadioButton3, null);
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        buttonGroup1.add(jRadioButton7);
        buttonGroup1.add(jRadioButton8);
        buttonGroup2.add(jRadioButton5);
        buttonGroup2.add(jRadioButton6);
        buttonGroup3.add(jRadioButton3);
        buttonGroup3.add(jRadioButton4);
        jPanel9.setPreferredSize(new Dimension(200, 40));
        jRadioButton1.setSelected(true);
        jRadioButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRadioButton2_actionPerformed(e);
            }
        });
        jRadioButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                remoteButtonSelection_actionPerformed(e);
            }
        });
        jRadioButton7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mageButtonSelection_actionPerformed(e);
            }
        });
        jRadioButton8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRemotePanel_actionPerformed(e);
            }
        });

        jFileChooser1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jFileChooser1_actionPerformed(e);
            }
        });
        this.getContentPane().setSize(new Dimension(683, 450));
        this.setTitle("Open File");
        this.updateExistedResourcesGUI();
        pack();
    }

    /**
     * Queries the <code>PluginRegistry</code> for all supported file formats
     * and sets the file chooser options accordingly.
     */
    public void setupInputFormats() {
        int i;
        // Get the supported formats from the registry.
        /**
         PluginDescriptor[] inputFormats = PluginRegistry.getPluginsAtExtension("input-format");
         if (inputFormats != null) {
         supportedInputFormats = new FileFormat[inputFormats.length];
         for (i = 0; i < inputFormats.length; ++i) {
         supportedInputFormats[i] = (FileFormat) inputFormats[i].getPlugin();
         }
         }
         **/
        supportedInputFormats = ComponentRegistry.getRegistry().getModules(org.
                geworkbench.engine.parsers.FileFormat.class);

        // Setup the file chooser options.
        jFileChooser1.resetChoosableFileFilters();
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.setMultiSelectionEnabled(true);
        for (i = 0; i < supportedInputFormats.length; ++i) {
            jFileChooser1.addChoosableFileFilter(supportedInputFormats[i].
                                                 getFileFilter());
        }

        int idx = 0;
        if ((format != null) && (!format.equals("")) &&
            ((idx = Integer.parseInt(format)) <
             supportedInputFormats.length)) {
            jFileChooser1.setFileFilter(supportedInputFormats[idx].
                                        getFileFilter());
        }
    }

    /**
     * displayRemoteResourceDiolog for add a new resouce.
     *
     * @param option int
     */
    void displayRemoteResourceDialog(int option) {
    }

    /**
     * displayRemoteResourceDiolog for edit/delete an existed resource.
     *
     * @param shortname Object
     * @param option int
     */
    void displayRemoteResourceDialog(Object shortname, int option) {
    }

    /**
     * updateExistedResources, should be called after any button related to
     * remoteresource is clicked.
     */
    private void updateExistedResourcesGUI() {
    }


    /**
     * deleteButton_actionPerformed, remove the selected resource after
     * confirmation.
     *
     * @param e ActionEvent
     */
    public void deleteButton_actionPerformed(ActionEvent e) {
        String deleteResourceStr = (String) jComboBox1.getSelectedItem();
        if (deleteResourceStr != null) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Do you really want to remove the remote source: " +
                    deleteResourceStr + "?", "Warning",
                    JOptionPane.OK_CANCEL_OPTION);
        }
    }

    /**
     * Responds to the selection of an input file by the user
     *
     * @param e
     */
    private void jFileChooser1_actionPerformed(ActionEvent e) {
        // The FileFormat corresponding to the user selected FileFilter.
        FileFormat selectedFormat = null;
        int i;
        if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
            // Get the format that the user designated for the selected file.
            FileFilter selectedFilter = jFileChooser1.getFileFilter();
            if (selectedFilter == null) {
                return; // Just to cover the case where no plugins are defined.
            }
            File[] files = jFileChooser1.getSelectedFiles();
            for (i = 0; i < supportedInputFormats.length; ++i) {
                if (selectedFilter == supportedInputFormats[i].getFileFilter()) {
                    try {
                        String format = i + "\n";
                        String filepath = null;
                        filepath = jFileChooser1.getCurrentDirectory().
                                   getCanonicalPath();
                        setLastDataInfo(filepath, format);
                        // Delegates the actual file loading to the project panel
                        parentProjectPanel.fileOpenAction(files,
                                supportedInputFormats[i],
                                mergeCheckBox.isSelected());
                        dispose();
                        return;
                    } catch (org.geworkbench.engine.parsers.
                             InputFileFormatException iffe) {
                        // Let the user know that there was a problem parsing the file.
                        JOptionPane.showMessageDialog(null,
                                "The input file does not comply with the designated format.",
                                "Parsing Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                    }
                }
            }
        } else if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION) {
            dispose();
        }
    }


    /**
     * Automatically update the RemoteResources Information from an Index service.
     *
     *
     */
    private void autoUpdateIndexService() {
    }


    /**
     * Responds to the user selection to see the remote files.
     *
     * @param e
     */
    private void remoteButtonSelection_actionPerformed(ActionEvent e) {
    }

    private void mageButtonSelection_actionPerformed(ActionEvent e) {
    }

    public void addRemotePanel() {
    }

    private void addRemotePanel_actionPerformed(ActionEvent e) {
        addRemotePanel();
        lowerPanel.add(jPanel10);
        this.validate();
        this.repaint();
    }


    private void jRadioButton2_actionPerformed(ActionEvent e) {
    }

    /**
     * Returns the experiment information for the most recently selected remote
     * file.
     *
     * @return
     */
    public String getExperimentInformation() {
        String expInfo = experimentInfoArea.getText();
        return (expInfo == null ? "" : expInfo);
    }

    static public String getLastDataDirectory() {
        String dir = System.getProperty("data.files.dir");
        // This is where we store user data information
        String filename = System.getProperty("temporary.files.directory") + System.getProperty("userSettings");
        try {
            File file = new File(filename);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                br.readLine(); // skip the format information
                dir = br.readLine();
                br.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (dir.equals(".")) {
            dir = System.getProperty("user.dir");
        }
        return dir;
    }

    static public String getLastDataFormat() {
        String format = "";
        // This is where we store user data information
        String filename = System.getProperty("temporary.files.directory") + System.getProperty("userSettings");
        try {
            File file = new File(filename);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                format = br.readLine();
                br.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return format;
    }

    static public void setLastDataInfo(String dir, String format) {
        try { //save current settings.
            BufferedWriter br = new BufferedWriter(new FileWriter(System.getProperty("temporary.files.directory") + System.
                    getProperty("userSettings")));
            br.write(format);
            br.write(dir);
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void openRemoteResourceButton_actionPerformed(ActionEvent e) {
        mageButtonSelection_actionPerformed(e);

    }

    public boolean isMerge() {
        return merge;
    }
}
