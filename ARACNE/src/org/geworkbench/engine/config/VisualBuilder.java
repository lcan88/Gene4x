package org.geworkbench.engine.config;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import org.geworkbench.engine.config.events.*;
import org.geworkbench.engine.config.rules.GeawConfigObject;
import org.geworkbench.engine.config.rules.PluginObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class VisualBuilder extends JFrame {
    static boolean isparsed = false;
    String path;
    HashMap plugins = null;
    JTabbedPane mainPane = new JTabbedPane();

    Vector CompListVector = new Vector(); //List of all components
    Vector CompVector = new Vector(); //list of all visual components
    ArrayList visualAreas = new ArrayList(5);

    boolean mandatorySel = true;

    EGTreeNode rootNode = new EGTreeNode("Root", "Root");
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    JPopupMenu jTreeCompMenu = new JPopupMenu();
    JMenuItem jNewInstanceMenu = new JMenuItem();
    JMenuItem jEnableMenu = new JMenuItem();
    JMenuItem jDisableMenu = new JMenuItem();
    JMenuItem jRemoveMenu = new JMenuItem();
    JPanel jPanel1 = new JPanel();
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel jAddNewPanel = new JPanel();
    JList jarentry = new JList();
    JScrollPane jarpane = new JScrollPane();
    BorderLayout borderLayout1 = new BorderLayout();

    JButton AddToListButton = new JButton();
    JButton jAddNewButton = new JButton();

    JPanel compPanel = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JPanel jPanel3 = new JPanel();
    JPanel jPanel4 = new JPanel();
    GridLayout gridLayout2 = new GridLayout();
    GridLayout gridLayout3 = new GridLayout();

    JScrollPane jScrollPane6 = new JScrollPane();

    JScrollPane jScrollPane7 = new JScrollPane();
    GridLayout gridLayout4 = new GridLayout();
    JLabel jVisualAreaLabel1 = new JLabel();
    BorderLayout borderLayout5 = new BorderLayout();
    JPanel jPluginPanel1 = new JPanel();
    JComboBox jVisualAreaComboBox1 = new JComboBox();
    JPanel jVisualAreaPanel1 = new JPanel();
    JList jBroadcastListenerList1 = new JList();
    JPanel jPanel8 = new JPanel();
    BorderLayout borderLayout7 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTree jComponentTree = new JTree();
    JPanel jPanel11 = new JPanel();
    JPanel jPanel10 = new JPanel();
    JLabel jLabel2 = new JLabel();
    JScrollPane jScrollPane8 = new JScrollPane();
    BorderLayout borderLayout8 = new BorderLayout();
    JList jInterfaceList = new JList();
    BorderLayout borderLayout9 = new BorderLayout();
    JLabel jInterfacesLabel = new JLabel();
    GridLayout gridLayout5 = new GridLayout();
    JList jEventSourcesList = new JList(); //the plugins for each interface
    JScrollPane jScrollPane9 = new JScrollPane();
    JPanel jPanel9 = new JPanel();
    JPanel jPanel7 = new JPanel();
    BorderLayout borderLayout10 = new BorderLayout();
    JButton jAssignButton = new JButton();
    JScrollPane jScrollPane10 = new JScrollPane();
    JList jAssignedList = new JList();
    JPanel jPanel5 = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    GridLayout gridLayout1 = new GridLayout();
    JLabel jVisualAreaLabel = new JLabel();
    JPanel jPanel6 = new JPanel();
    JComboBox jVisualAreaComboBox = new JComboBox();
    BorderLayout borderLayout6 = new BorderLayout();
    JPanel jVisualAreaPanel = new JPanel();
    JList jBroadcastListenerList = new JList();
    JButton jButton1 = new JButton();
    JButton jSaveButton = new JButton();

    static class NestedEGTreeNode {
        public static final int isADescription = 0;
        public static final int isAComponent = 1;
        public static final int IsAnInterface = 2;
        public static final int isAClass = 3;
    }

    class EGTreeNode extends DefaultMutableTreeNode {

        protected int type = 0;
        protected Object component;
        protected Class anInterface;
        protected String description;
        protected String className;
        protected String resourceName = null;

        public EGTreeNode(String Name, Object object) {

            description = Name;
            if (PluginDescriptor.class.isAssignableFrom(object.getClass())) { //When it's an PluginDescriptor
                type = NestedEGTreeNode.isAComponent;
                component = object;
                className = ((PluginDescriptor) component).getID();
            } else if (object.getClass() == Class.class) { //When It's an Interface
                type = NestedEGTreeNode.IsAnInterface;
                anInterface = (Class) object;
            } else if (PluginClass.class.isAssignableFrom(object.getClass())) { //when it's an PluginClass
                type = NestedEGTreeNode.isAClass;
                component = object;
                className = ((PluginClass) object).getID();
            } else { //when object is a String
                type = NestedEGTreeNode.isADescription;
                className = (String) object;

            }
        }

        public String getResourceName() {
            return resourceName;
        }

        public Object getComponent() {
            return component;
        }

        public Class getInterface() {
            return anInterface;
        }

        public String getDescription() {
            return description;
        }

        public String getClassName() {
            return className;
        }

        int getType() {
            return type;
        }

        public String toString() {
            return description;
        }
    }

    class GTCellRenderer extends JCheckBox implements TreeCellRenderer {
        ImageIcon image1 = new ImageIcon(VisualBuilder.class.getResource("Bean24.gif"));
        ImageIcon image2 = new ImageIcon(VisualBuilder.class.getResource("Visualeye.gif"));
        ImageIcon image3 = new ImageIcon(VisualBuilder.class.getResource("Visualeye_Nonactive.gif"));

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean cellHasFocus) {
            EGTreeNode component = (EGTreeNode) value;
            String s = null;
            boolean isVisual = false;
            if (component.getType() == NestedEGTreeNode.isAComponent) {
                this.setIcon(null);
                /**
                 * check whether it's visual plugin
                 */
                if (((PluginDescriptor) component.getComponent()).isVisualPlugin()) {
                    isVisual = true;
                    if (((VisualPlugin) ((PluginDescriptor) component.getComponent()).getPlugin()).getComponent().getParent() != null) {
                        this.setSelected(true);
                    } else {
                        this.setSelected(false);
                    }
                } else {
                    this.setSelected(false);
                }
                s = component.getDescription();
            } else if (component.getType() == NestedEGTreeNode.isADescription) {

                this.setIcon(image1);
                s = component.getDescription();

                this.setSelected(!component.isLeaf());
            } else {
                this.setSelected(!component.isLeaf());
                isVisual = ((PluginClass) component.getComponent()).isVisualPlugin();
                if (isVisual) {
                    if (this.isSelected())
                        this.setIcon(image2);
                    else
                        this.setIcon(image3);
                } else {
                    this.setIcon(image1);
                }
                s = component.getDescription();
            }
            this.setText(s);
            if (isSelected) {
                setBackground(Color.lightGray);
            } else {
                setBackground(Color.white);
            }
            if (this.isSelected()) {

                setForeground(Color.BLUE);
            } else {
                setForeground(Color.DARK_GRAY);
            }

            setEnabled(true);
            setFont(tree.getFont());
            return this;
        }
    }

    class InterfaceCellRenderer extends JPanel implements ListCellRenderer {
        JCheckBox box = new JCheckBox();
        JLabel label = new JLabel();

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            BorderLayout bd = new BorderLayout();
            this.setLayout(bd);
            this.add(box, BorderLayout.WEST);
            this.add(label, BorderLayout.CENTER);

            Class anInterface = ((ComponentInterface) value).getInterfaceClass();
            String s = anInterface.toString();
            String[] parts = s.split("\\.");
            label.setText(parts[parts.length - 1]);
            box.setSelected(((ComponentInterface) value).isActive());
            if (isSelected) {
                box.setBackground(list.getSelectionBackground());
                box.setForeground(list.getSelectionForeground());
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());

            return this;
        }

        public JCheckBox getCheckBox() {
            return box;
        }
    }

    public void setRegistry() {
        visualAreas.add(0, GUIFramework.TOOL_AREA);
        visualAreas.add(1, GUIFramework.COMMAND_AREA);
        visualAreas.add(2, GUIFramework.SELECTION_AREA);
        visualAreas.add(3, GUIFramework.VISUAL_AREA);
        visualAreas.add(4, GUIFramework.PROJECT_AREA);

        // all the plugins displayed on the component list
        PluginClass[] plgs = PluginClassRegistry.getPluginClasses();

        for (int i = 0; i < plgs.length; i++) {
            CompListVector.add(plgs[i]);
        }

        // comp tree, should be all the compClass in xml file.
        for (int i = 0; i < plgs.length; i++) {

            Class cls = plgs[i].getPluginClass();
            EGTreeNode node = new EGTreeNode(plgs[i].getLabel(), plgs[i]);
            PluginDescriptor[] activeInstances = PluginRegistry.getPluginDescriptorsOfType(cls);

            for (int ins = 0; ins < activeInstances.length; ins++) {

                EGTreeNode compNode = new EGTreeNode("[" + activeInstances[ins].getID() + " " + ins + "] " + activeInstances[ins].getLabel(), activeInstances[ins]);
                treeModel.insertNodeInto(compNode, node, node.getChildCount());
                jComponentTree.scrollPathToVisible(new TreePath(compNode.getPath()));

            }
            treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
            jComponentTree.scrollPathToVisible(new TreePath(node.getPath()));
        }
    }

    /**
     * this will actully enable the instance to hook up with other plugins.
     *
     * @param e ActionEvent
     */
    void jEnableMenu_actionPerformed(ActionEvent e) {
        //need to check if this is already enabled

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jComponentTree.getLastSelectedPathComponent();
        if (node.getParent() != rootNode) { //when it's an instance
            PluginDescriptor value = (PluginDescriptor) ((EGTreeNode) node).getComponent();
            if (value.getID().equalsIgnoreCase(jLabel1.getText())) {
                if (value.isVisualPlugin()) { //add visual plugin to the selected area
                    String visualArea = (String) jVisualAreaComboBox.getSelectedItem();
                    if ((visualArea != null) && (!visualArea.equalsIgnoreCase("")))
                        GeawConfigObject.getGuiWindow().addToContainer(visualArea, ((VisualPlugin) value.getPlugin()).getComponent(), value.getLabel(), value.getPluginClass());
                    PluginRegistry.addVisualAreaInfo(visualArea, (VisualPlugin) value.getPlugin());
                }
                //get interfaces
                ListModel ls = jBroadcastListenerList.getModel();
                for (int c = 0; c < ls.getSize(); c++) {
                    ComponentInterface ci = (ComponentInterface) ls.getElementAt(c);
                    if (ci.isActive()) {
                        try {
                            BroadcastEventRegistry.addEventListener(ci.getInterfaceClass(), ((AppEventListener) value.getPlugin()));
                        } catch (AppEventListenerException ex) {
                            ex.printStackTrace();

                        } catch (ListenerEventMismatchException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                /**
                 * @todo add coupled ls, broadcastls etc
                 */
            } else {
                JOptionPane.showMessageDialog(null, "Parameters not set for the component.");
            }
        }
    }

    void jDisableMenu_actionPerformed(ActionEvent e) {
        EGTreeNode componentClass = (EGTreeNode) jComponentTree.getLastSelectedPathComponent();
        if (componentClass.getParent() != rootNode) {
            PluginDescriptor compDes = (PluginDescriptor) componentClass.getComponent();
            if (compDes.isVisualPlugin()) {
                Container gui = ((VisualPlugin) compDes.getPlugin()).getComponent().getParent();
                gui.remove(((VisualPlugin) compDes.getPlugin()).getComponent());
            }
            /**
             * @todo add more to remove other registries?
             */
            jComponentTree.repaint();
        }
    }

    void jRemoveMenu_actionPerformed(ActionEvent e) {

        TreePath[] rows = jComponentTree.getSelectionPaths();
        boolean modified = false;
        for (int i = 0; i < rows.length; i++) {
            TreePath row = rows[i];
            EGTreeNode componentClass = (EGTreeNode) row.getLastPathComponent();
            if (componentClass.getType() == NestedEGTreeNode.isAComponent) {
                PluginRegistry.removePlugin(((PluginDescriptor) componentClass.getComponent()));
                ((DefaultTreeModel) jComponentTree.getModel()).removeNodeFromParent(componentClass);
                jComponentTree.removeSelectionPath(row);
                modified = true;
            }
        }
        if (modified) {
            jComponentTree.repaint();
        }
    }

    void jNewInstanceMenu_actionPerformed(ActionEvent e) {
        EGTreeNode componentClass = (EGTreeNode) jComponentTree.getLastSelectedPathComponent();
        if (componentClass.getParent() == rootNode) {
            // System.out.println("before:"+PluginRegistry.getPlugins().length);
            PluginObject comp = new PluginObject();
            comp.createPlugin(("[" + (componentClass.getChildCount() + 1) + "]" + componentClass.getDescription()), componentClass.getDescription(), componentClass.getClassName(), componentClass.getResourceName());
            //add extensionpoints
            // ArrayList extensions = ( (PluginClass) componentClass.getComponent()).
            //     getExtensions();
            //  for (Iterator it = extensions.iterator(); it.hasNext(); ) {
            //    comp.addExtensionPoint( (String) it.next());
            //   }
            /**
             * @todo more detailed code involving whether to add it to the gui or broadcastListener
             */
            comp.finish();
            PluginDescriptor compdes = PluginRegistry.getPluginDescriptor("[" + (componentClass.getChildCount() + 1) + "]" + componentClass.getDescription());
            // System.out.println("after"+PluginRegistry.getPlugins().length);
            if (compdes != null) {
                String label = new String("[" + (componentClass.getChildCount() + 1) + "] " + compdes.getLabel());
                EGTreeNode compNode = new EGTreeNode(label, compdes);
                treeModel.insertNodeInto(compNode, componentClass, componentClass.getChildCount());
                jComponentTree.scrollPathToVisible(new TreePath(compNode.getPath()));
                CompListVector.add(compNode.getComponent());
            } else {
                System.out.println("Component " + componentClass.getDescription() + " could not be created");
            }
            jComponentTree.doLayout();
        }
    }

    void mouseClickedOnTree(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int selRow = jComponentTree.getRowForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                //find the plugininstance
                //shows the info about this plugininstance on the pluginPanel
                // including the broadcastListenerList.
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jComponentTree.getLastSelectedPathComponent();
                if (node.getParent() != rootNode) { //when it's an instance
                    PluginDescriptor value = (PluginDescriptor) (((EGTreeNode) node).getComponent());

                    String visualArea = "";
                    if (value.isVisualPlugin()) {
                        visualArea = PluginRegistry.getVisualAreaInfo(((VisualPlugin) value.getPlugin()));

                        if (visualArea == null) {
                            visualArea = "";
                        }

                        jVisualAreaComboBox.setSelectedIndex((visualAreas.indexOf(visualArea)));
                        jVisualAreaComboBox.setSelectedItem(visualArea);
                        jVisualAreaComboBox.setEnabled(true);

                    } else {
                        jVisualAreaComboBox.setSelectedIndex(0);
                        jVisualAreaComboBox.setSelectedItem("");
                        jVisualAreaComboBox.setEnabled(false);
                    }
                    jLabel1.setText(value.getID());
                    try {
                        Class[] intfs = value.getPlugin().getClass().getInterfaces();
                        DefaultListModel listmodel = new DefaultListModel();
                        HashMap broadcasts = new HashMap();
                        for (int j = 0; j < intfs.length; j++) {

                            if (Class.forName("org.geworkbench.engine.config.events.AppEventListener").isAssignableFrom(intfs[j])) {
                                ComponentInterface compin = new ComponentInterface(intfs[j], false);

                                broadcasts.put(intfs[j], compin);
                                listmodel.addElement(intfs[j].getName());
                                AppEventListener aparray[] = BroadcastEventRegistry.getListenersForEvent(intfs[j]);
                                if (aparray != null) {
                                    for (int i = 0; i < aparray.length; i++) {

                                        if (aparray[i] != null && aparray[i].equals(value.getPlugin())) {
                                            compin.setActiveFlag(true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        jBroadcastListenerList.setListData(broadcasts.values().toArray());
                        jInterfaceList.setModel(listmodel);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (e.isMetaDown()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) jComponentTree.getLastSelectedPathComponent();

            if (node.getParent() == rootNode) { //class
                jTreeCompMenu.add(jNewInstanceMenu);
                jTreeCompMenu.remove(jEnableMenu);
                jTreeCompMenu.remove(jDisableMenu);
                jTreeCompMenu.remove(jRemoveMenu); //? this may not needed
            } else { //instance
                jTreeCompMenu.remove(jNewInstanceMenu);
                PluginDescriptor value = (PluginDescriptor) (((EGTreeNode) node).getComponent());
                if (value.isVisualPlugin()) {
                    if ((((VisualPlugin) (value.getPlugin())).getComponent().getParent() != null)) {
                        jTreeCompMenu.remove(jEnableMenu);
                        jTreeCompMenu.add(jDisableMenu);
                        jTreeCompMenu.remove(jRemoveMenu);
                    } else {
                        jTreeCompMenu.add(jEnableMenu);
                        jTreeCompMenu.remove(jDisableMenu);
                        jTreeCompMenu.add(jRemoveMenu);
                    }
                } else {
                    jTreeCompMenu.remove(jEnableMenu);
                    jTreeCompMenu.remove(jDisableMenu);
                    jTreeCompMenu.add(jRemoveMenu);
                }
            }
            jTreeCompMenu.show(jComponentTree, e.getX(), e.getY());
        }
    }

    public VisualBuilder(Frame frame, String title, boolean modal) {
        if (!isparsed) {
            UIController.parse(System.getProperty("class.definition.file"));
            isparsed = true;
        }
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public VisualBuilder() {
        this(null, "", false);
    }

    void jbInit() throws Exception {
        jNewInstanceMenu.setText("New Instance");
        jEnableMenu.setText("Enable");
        jDisableMenu.setText("Disable");
        jRemoveMenu.setText("Remove");

        //jAssignedList.setCellRenderer(new ComponentCellRenderer());

        AddToListButton.setText("Add To List");
        AddToListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddToListButton_actionPerformed(e);
            }
        });
        jAddNewButton.setActionCommand("jButton1");
        jAddNewButton.setText("Import Jar");
        jAddNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //jAddNewButton_actionPerformed(e);
            }
        });
        mainPane.setMinimumSize(new Dimension(500, 300));
        mainPane.setOpaque(false);
        mainPane.setPreferredSize(new Dimension(500, 300));
        //    jLoadConfigComboBox.setSelectedItem(path);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.setTitle("Visual Designer");
        compPanel.setLayout(borderLayout3);
        jPanel3.setLayout(gridLayout2);
        gridLayout2.setColumns(4);
        jAssignButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jAssignButton_actionPerformed(e);
            }
        });
        jAssignButton.setText("Assign Interfaces");
        jPanel4.setLayout(gridLayout3);
        jVisualAreaLabel1.setText(" Visual Area");
        jVisualAreaLabel1.setOpaque(true);
        jPluginPanel1.setLayout(borderLayout5);
        jPluginPanel1.setOpaque(false);
        jPluginPanel1.setBorder(BorderFactory.createEtchedBorder());
        jVisualAreaPanel1.setLayout(gridLayout4);
        jVisualAreaPanel1.setOpaque(false);
        jBroadcastListenerList1.setEnabled(true);
        jBroadcastListenerList1.setOpaque(false);
        jBroadcastListenerList1.setCellRenderer(new InterfaceCellRenderer());
        jBroadcastListenerList1.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                super.mouseReleased(e);
                jBroadcastListenerList_mouseClicked(e);
            }
        });
        jPanel8.setLayout(borderLayout7);
        jComponentTree.setRootVisible(false);
        jComponentTree.setModel(treeModel);
        jComponentTree.setCellRenderer(new GTCellRenderer());
        jComponentTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseClickedOnTree(e);
            }
        });
        gridLayout3.setColumns(4);
        jPanel10.setLayout(borderLayout8);
        jLabel2.setMaximumSize(new Dimension(71, 24));
        jLabel2.setMinimumSize(new Dimension(71, 15));
        jLabel2.setOpaque(true);
        jLabel2.setPreferredSize(new Dimension(71, 24));
        jLabel2.setRequestFocusEnabled(true);
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel2.setText("Event Sources");
        jInterfaceList.setPreferredSize(new Dimension(0, 0));
        jInterfaceList.setToolTipText("Interfaces");
        jInterfaceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jInterfaceList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jInterfaceList_mouseClicked(e);
            }
        });
        jInterfacesLabel.setMaximumSize(new Dimension(49, 24));
        jInterfacesLabel.setMinimumSize(new Dimension(49, 15));
        jInterfacesLabel.setOpaque(true);
        jInterfacesLabel.setPreferredSize(new Dimension(49, 24));
        jInterfacesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jInterfacesLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        jInterfacesLabel.setText("Interfaces");
        gridLayout5.setColumns(1);
        gridLayout5.setRows(2);
        jEventSourcesList.setBackground(Color.white);
        jEventSourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jPanel9.setLayout(borderLayout9);
        jPanel7.setLayout(gridLayout5);
        jPanel11.setLayout(borderLayout10);
        jAssignButton.setMaximumSize(new Dimension(135, 24));
        jAssignButton.setMinimumSize(new Dimension(135, 24));
        jAssignButton.setActionCommand("assignInterfaces");
        jAssignButton.setSelected(false);
        jAssignButton.setText("Assign Interfaces");

        jPanel5.setLayout(borderLayout4);
        jLabel1.setMaximumSize(new Dimension(43, 24));
        jLabel1.setMinimumSize(new Dimension(43, 24));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel1.setText("Plugin Name");
        jVisualAreaLabel.setOpaque(true);
        jVisualAreaLabel.setText(" Visual Area");
        jPanel6.setLayout(borderLayout6);
        //    jVisualAreaComboBox.setSelectedIndex(0);
        //    jVisualAreaComboBox.setSelectedItem("");
        jVisualAreaPanel.setLayout(gridLayout1);
        jVisualAreaPanel.setOpaque(false);
        jBroadcastListenerList.setEnabled(true);
        jBroadcastListenerList.setOpaque(false);
        jBroadcastListenerList.setCellRenderer(new InterfaceCellRenderer());
        jBroadcastListenerList.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                super.mouseReleased(e);
                jBroadcastListenerList_mouseClicked(e);
            }
        });

        jNewInstanceMenu.setText("New Instance");
        jEnableMenu.setText("Enable");
        jDisableMenu.setText("Disable");
        jRemoveMenu.setText("Remove");
        jNewInstanceMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jNewInstanceMenu_actionPerformed(e);
            }
        });
        jEnableMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jEnableMenu_actionPerformed(e);
            }
        });
        jDisableMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jDisableMenu_actionPerformed(e);
            }
        });
        jRemoveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRemoveMenu_actionPerformed(e);
            }
        });
        jPanel1.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        jAddNewPanel.setAlignmentX((float) 0.0);
        jAddNewPanel.setAlignmentY((float) 0.0);
        jAddNewPanel.setMaximumSize(new Dimension(32767, 32767));
        jAddNewPanel.setMinimumSize(new Dimension(1700, 700));
        jAddNewPanel.setPreferredSize(new Dimension(1700, 700));
        jAddNewPanel.setActionMap(null);
        jAddNewPanel.setLayout(borderLayout1);
        jarentry.setMaximumSize(new Dimension(800, 800));
        jarentry.setMinimumSize(new Dimension(700, 700));
        jarentry.setPreferredSize(new Dimension(700, 700));
        jarentry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }

            public void mouseClicked(MouseEvent e) {
                jarentry_mouseClicked(e);
                super.mouseReleased(e);
            }
        });

        jPanel7.setBorder(BorderFactory.createLineBorder(Color.black));
        jPanel5.setBorder(BorderFactory.createLineBorder(Color.black));
        jPanel8.setBorder(BorderFactory.createLineBorder(Color.black));
        jPanel11.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton1.setText("jButton1");
        jSaveButton.setText("SaveSettings");
        jSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jSaveButton_actionPerformed(e);
            }
        });
        jTreeCompMenu.add(jNewInstanceMenu);
        jTreeCompMenu.add(jEnableMenu);
        jTreeCompMenu.add(jDisableMenu);
        jTreeCompMenu.add(jRemoveMenu);

        jAddNewPanel.add(jarpane, BorderLayout.CENTER);
        jAddNewPanel.add(jPanel1, BorderLayout.SOUTH);
        jarpane.add(jarentry);
        jPanel1.add(jButton1, null);
        jPanel1.add(jAddNewButton, null);
        jPanel1.add(AddToListButton, null);
        mainPane.add(compPanel, "Component Panel");
        compPanel.add(jPanel3, BorderLayout.NORTH);
        compPanel.add(jPanel4, BorderLayout.CENTER);

        mainPane.add(jAddNewPanel, "Add New Comps");
        this.getContentPane().add(mainPane, BorderLayout.CENTER);
        jPanel4.add(jPanel8, null);
        jPanel8.add(jScrollPane1, BorderLayout.CENTER);
        jPanel4.add(jPanel5, null);
        jPanel5.add(jLabel1, BorderLayout.NORTH);
        jPanel5.add(jPanel6, BorderLayout.CENTER);
        jPanel6.add(jBroadcastListenerList, BorderLayout.CENTER);
        jPanel6.add(jVisualAreaPanel, BorderLayout.NORTH);
        jVisualAreaPanel.add(jVisualAreaLabel, null);
        jVisualAreaPanel.add(jVisualAreaComboBox, null);
        jPanel4.add(jPanel7, null);
        jPanel9.add(jLabel2, BorderLayout.NORTH);
        jPanel9.add(jScrollPane8, BorderLayout.CENTER);
        jPanel7.add(jPanel10, null);
        jScrollPane9.getViewport().add(jInterfaceList, null);
        jPanel7.add(jPanel9, null);
        jPanel10.add(jInterfacesLabel, BorderLayout.NORTH);
        jPanel10.add(jScrollPane9, BorderLayout.CENTER);
        jScrollPane8.getViewport().add(jEventSourcesList, null);
        jPanel4.add(jPanel11, null);
        jPanel11.add(jAssignButton, BorderLayout.NORTH);
        jPanel11.add(jScrollPane10, BorderLayout.CENTER);
        jPanel11.add(jSaveButton, BorderLayout.SOUTH);
        jScrollPane10.getViewport().add(jAssignedList, null);
        jScrollPane1.getViewport().add(jComponentTree, null);
        //jComponentTree.add(jTreeCompMenu);
        jVisualAreaComboBox.addItem("");
        jVisualAreaComboBox.addItem("Toolbar");
        jVisualAreaComboBox.addItem("VisualArea");
        jVisualAreaComboBox.addItem("CommandArea");
        jVisualAreaComboBox.addItem("SelectionArea");
        jVisualAreaComboBox.addItem("ProjectArea");

    }

    //when user select of deselect the interfaces.
    void jBroadcastListenerList_mouseClicked(MouseEvent e) {
        ComponentInterface anInterface = (ComponentInterface) jBroadcastListenerList.getSelectedValue();

        if (anInterface != null) {
            anInterface.flipActiveFlag();
            jBroadcastListenerList.repaint();
        }
    }

    private void jarentry_mouseClicked(MouseEvent e) {

        int index = jarentry.locationToIndex(e.getPoint());
        if (e.getClickCount() == 2) {
            String selected = (String) jarentry.getModel().getElementAt(index);
            selected = selected.substring(0, selected.length() - 6);
            selected = selected.replaceAll("/", ".");
            selected = selected.replaceAll("/", ".");
            Element comp = new Element("plugin");
            comp.setAttribute("id", "");
            comp.setAttribute("name", "");
            comp.setAttribute("class", selected);
            try {

                ClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + path)});
                Class oneClass = loader.loadClass(selected);
            } catch (IllegalArgumentException ex2) {
                ex2.printStackTrace();
            } catch (SecurityException ex1) {
                ex1.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void AddToListButton_actionPerformed(ActionEvent e) {
        Iterator it = plugins.keySet().iterator();
        while (it.hasNext()) {
            Element x = (Element) it.next();
        }
    }

    void jLoadConfigComboBox_actionPerformed(ActionEvent e) {

        System.out.println("jLoadConfigComboBox_actionPerformed");
        /*
             String filename = (String) jLoadConfigComboBox.getSelectedItem();
             if (filename != null) {
          filename = filename + ".xml";
             }
             PluginDescriptor[] plugins = PluginRegistry.getPlugins();
             for (int i = 0; i < plugins.length; i++) {
          PluginRegistry.removePlugin(plugins[i]);
          //check if plugin is eventlistener
         BroadcastEventRegistry.removeListener((AppEventListener)plugins[i].getPlugin());
             }
         */
    }

    /**
     * it fill the pluginlists with eventsources for user to pick up
     */

    void jInterfaceList_mouseClicked(MouseEvent e) {
        PluginDescriptor[] plugins = PluginRegistry.getPlugins();
        DefaultListModel pluginlistmodel = new DefaultListModel();
        DefaultListModel assignedlistmodel = new DefaultListModel();
        String classname = (String) jInterfaceList.getSelectedValue();
        Class interfaceClass = null;
        try {
            interfaceClass = Class.forName(classname);
        } catch (ClassNotFoundException ex1) {
            ex1.printStackTrace();
        }
        for (int i = 0; i < plugins.length; i++) {
            try {
                if (Class.forName("org.geworkbench.engine.config.events.EventSource").isAssignableFrom(plugins[i].getPlugin().getClass())) {

                    AppEventListener[] aps = ((EventSource) plugins[i].getPlugin()).getListenersForEvent(interfaceClass);
                    EGTreeNode node = (EGTreeNode) jComponentTree.getLastSelectedPathComponent();
                    PluginDescriptor value = (PluginDescriptor) (node.getComponent());
                    boolean hooked = false;
                    if (aps != null) {
                        for (int j = 0; j < aps.length; j++) {
                            if (value.getPlugin().equals(aps[j])) {
                                hooked = true;
                                break;
                            }
                        }
                    }
                    if (hooked) {
                        assignedlistmodel.addElement(plugins[i].getID());
                    } else {
                        pluginlistmodel.addElement(plugins[i].getID());
                    }
                }
            } catch (AppEventListenerException ex2) {
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        jAssignedList.setModel(assignedlistmodel);
        jEventSourcesList.setModel(pluginlistmodel);
    }

    /**
     * this assigns an interface to a eventsource
     *
     * @param e ActionEvent
     */
    void jAssignButton_actionPerformed(ActionEvent e) {
        int indexInter = jInterfaceList.getSelectedIndex();
        int indexPlug = jEventSourcesList.getSelectedIndex();

        Class interf = null;
        try {
            interf = Class.forName(((String) (jInterfaceList.getModel().getElementAt(indexInter))));
        } catch (ClassNotFoundException ex1) {
            ex1.printStackTrace();
        }
        String plgid = (String) jEventSourcesList.getModel().getElementAt(indexPlug);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jComponentTree.getLastSelectedPathComponent();
        PluginDescriptor value = (PluginDescriptor) (((EGTreeNode) node).getComponent());
        Object eventSource = PluginRegistry.getPluginDescriptor(plgid).getPlugin();
        try {
            if (Class.forName("org.geworkbench.engine.config.events.EventSource").isAssignableFrom(eventSource.getClass())) {
                ((EventSource) eventSource).addEventListener(interf, (AppEventListener) value.getPlugin());

                ((DefaultListModel) jEventSourcesList.getModel()).removeElementAt(indexPlug);
                ((DefaultListModel) jAssignedList.getModel()).addElement(plgid);
                JOptionPane.showMessageDialog(null, "assignment successful.");
            } else {
                JOptionPane.showMessageDialog(null, "assignment unsuccessful.");
            }
        } catch (AppEventListenerException ex) {
            ex.printStackTrace();
        } catch (ListenerEventMismatchException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * save current setting into xml
     *
     * @param e ActionEvent
     */

    void jSaveButton_actionPerformed(ActionEvent e) {
        PluginDescriptor[] plugins = PluginRegistry.getPlugins();
        Document compsDocument = new Document();
        Element root = new Element("geaw-config").addContent(new Element("gui-window").setAttribute("name", "Main GUI").setAttribute("class", "org.geworkbench.engine.skin.Skin"));
        compsDocument.setRootElement(root);
        HashMap hs = new HashMap();
        for (int count = 0; count < plugins.length; count++) { //to make a registry of coupled listeners
            try {
                if (Class.forName("org.geworkbench.engine.config.events.EventSource").isAssignableFrom(plugins[count].getPlugin().getClass())) {
                    for (int count2 = 0; count2 < plugins.length; count2++) {
                        if (Class.forName("org.geworkbench.engine.config.events.AppEventListener").isAssignableFrom(plugins[count2].getPlugin().getClass())) {
                            Class[] ints = ((EventSource) plugins[count].getPlugin()).getEventsForListener(((AppEventListener) plugins[count2].getPlugin()));
                            if (ints != null) {
                                HashMap tt = (HashMap) hs.get(plugins[count2]);
                                if (tt == null) {
                                    tt = new HashMap();
                                    hs.put(plugins[count2], tt);
                                }
                                for (int h = 0; h < ints.length; h++) {
                                    if (ints[h] != null) {
                                        tt.put(ints[h].getName(), plugins[count].getID());
                                    }
                                }

                            }

                        }
                    }
                }
            } catch (ClassNotFoundException ex1) {
                ex1.printStackTrace();
            }

        }

        for (int i = 0; i < plugins.length; i++) {
            Element comp = new Element("plugin");
            comp.setAttribute("id", plugins[i].getID());
            comp.setAttribute("name", plugins[i].getLabel());
            comp.setAttribute("class", plugins[i].getPlugin().getClass().getName());
            //ext point
            String[] exts = PluginRegistry.getExtensionsForPlugin(plugins[i]);
            if (exts != null) {
                for (int j = 0; j < exts.length; j++) {
                    if (exts[j] != null) {
                        comp.addContent(new Element("extension-point").setAttribute("name", exts[j]));
                    }
                }
            }
            //visual area

            if (plugins[i].isVisualPlugin()) {
                String visualarea = PluginRegistry.getVisualAreaInfo((VisualPlugin) plugins[i].getPlugin());
                if (visualarea != null)
                    comp.addContent(new Element("gui-area").setAttribute("name", visualarea));
            }
            //jmenu

            if (plugins[i].isMenuListener()) {

                ArrayList hashes = plugins[i].getMenuItemInfos();
                for (Iterator hash = hashes.iterator(); hash.hasNext();) {
                    HashMap onehs = (HashMap) hash.next();
                    String path = (String) onehs.get("path");
                    String mode = (String) onehs.get("mode");
                    String var = (String) onehs.get("var");
                    String icon = (String) onehs.get("icon");
                    String accelerator = (String) onehs.get("accelerator");
                    Element menuitem = new Element("menu-item").setAttribute("path", path);
                    if (mode != null) {
                        menuitem.setAttribute("mode", mode);
                    }
                    if (var != null) {
                        menuitem.setAttribute("var", var);
                    }
                    if (icon != null) {
                        menuitem.setAttribute("icon", icon);
                    }
                    if (accelerator != null) {
                        menuitem.setAttribute("accelerator", accelerator);
                    }
                    comp.addContent(menuitem);

                }

            }

            //broadcastlistener
            try {
                if (Class.forName("org.geworkbench.engine.config.events.AppEventListener").isAssignableFrom(plugins[i].getPlugin().getClass())) {
                    Class[] listeners = BroadcastEventRegistry.getEventsForListener((AppEventListener) plugins[i].getPlugin());
                    for (int k = 0; k < listeners.length; k++) {
                        if (listeners[k] != null) {
                            comp.addContent(new Element("broadcast-event").setAttribute("event", listeners[k].getName()));
                        }
                    }

                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            //coupled listener
            HashMap tt = (HashMap) hs.get(plugins[i]);
            if (tt != null) {
                for (Iterator itor = tt.keySet().iterator(); itor.hasNext();) {
                    String interfacename = (String) itor.next();
                    comp.addContent(new Element("coupled-event").setAttribute("event", interfacename).setAttribute("target", ((String) tt.get(interfacename))));

                }

            }
            System.out.println(comp);
            root.addContent(comp);

        }
        FileWriter writer = null;
        try {
            writer = new FileWriter("test.xml");
            XMLOutputter outputter = new XMLOutputter("  ", true);
            outputter.output(compsDocument, System.out);
            outputter.output(compsDocument, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
