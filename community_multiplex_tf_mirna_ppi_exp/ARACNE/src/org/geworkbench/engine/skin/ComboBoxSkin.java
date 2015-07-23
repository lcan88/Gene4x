package org.geworkbench.engine.skin;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.GUIFramework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public class ComboBoxSkin extends GUIFramework {

    JPanel contentPane;
    JLabel statusBar = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    JSplitPane jSplitPane1 = new JSplitPane();
    BorderLayout borderLayout5 = new BorderLayout();
    GuiArea visualPanel = new GuiArea(VISUAL_AREA);
    GuiArea jControlPanel = new GuiArea(COMMAND_AREA);
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout3 = new BorderLayout();
    JSplitPane jSplitPane2 = new JSplitPane();
    JSplitPane jSplitPane3 = new JSplitPane();
    BorderLayout borderLayout4 = new BorderLayout();
    GuiArea selectionPanel = new GuiArea(SELECTION_AREA);
    GridLayout gridLayout1 = new GridLayout();
    JToolBar jToolBar = new JToolBar();
    GuiArea projectPanel = new GuiArea(PROJECT_AREA);
    BorderLayout borderLayout9 = new BorderLayout();

    Hashtable areas = new Hashtable();

    public ComboBoxSkin() {
        registerAreas();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int guiWidth = (int) (dim.getWidth() * 0.9);
        int guiHeight = (int) (dim.getHeight() * 0.9);

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(guiWidth, guiHeight));
        this.setTitle(System.getProperty("application.title"));
        statusBar.setText(" ");
        jSplitPane1.setBorder(BorderFactory.createLineBorder(Color.black));
        jSplitPane1.setDoubleBuffered(true);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setBackground(Color.black);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setDoubleBuffered(true);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setDividerSize(8);
        jSplitPane2.setOneTouchExpandable(true);
        jSplitPane2.setResizeWeight(0.9);
        jSplitPane2.setMinimumSize(new Dimension(0, 0));
        jSplitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setBorder(BorderFactory.createLineBorder(Color.black));
        jSplitPane3.setDoubleBuffered(true);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setDividerSize(8);
        jSplitPane3.setOneTouchExpandable(true);
        jSplitPane3.setResizeWeight(0.1);
        jSplitPane3.setMinimumSize(new Dimension(0, 0));
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jSplitPane2, JSplitPane.RIGHT);
        jSplitPane2.add(jControlPanel, JSplitPane.BOTTOM);
        jSplitPane2.add(visualPanel, JSplitPane.TOP);
        jSplitPane1.add(jSplitPane3, JSplitPane.LEFT);
        jSplitPane3.add(selectionPanel, JSplitPane.BOTTOM);
        jSplitPane3.add(projectPanel, JSplitPane.LEFT);
        contentPane.add(jToolBar, BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(230);
        jSplitPane2.setDividerLocation((int) (guiHeight * 0.60));
        jSplitPane3.setDividerLocation((int) (guiHeight * 0.35));
        this.setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 2);
    }

    /**
     * Associates Visual Areas with Component Holders
     */
    protected void registerAreas() {
        areas.put(VISUAL_AREA, visualPanel);
        areas.put(COMMAND_AREA, jControlPanel);
        areas.put(SELECTION_AREA, selectionPanel);
        areas.put(PROJECT_AREA, projectPanel);
    }

    public void addToContainer(String areaName, Component visualPlugin) {
        if (visualPlugin != null)
            addToContainer(areaName, visualPlugin, visualPlugin.getName(), visualPlugin.getClass());
    }

    public void addToContainer(String areaName, Component visualPlugin, String pluginName, Class mainPluginClass) {
        // Todo - Deal with mainPluginClass as in Skin
        GuiArea ga = (GuiArea) areas.get(areaName);
        if (ga != null)
            ga.addComponent(visualPlugin, pluginName);
    }

    /**
     * Removes the designated <code>visualPlugin</code> from the GUI.
     *
     * @param visualPlugin component to be removed
     */
    public void remove(Component visualPlugin) {
        for (Enumeration e = areas.elements(); e.hasMoreElements();)
            ((GuiArea) e.nextElement()).remove(visualPlugin);
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    public String getVisualArea(Component visualPlugin) {
        for (Enumeration e = areas.elements(); e.hasMoreElements();) {
            GuiArea ga = (GuiArea) e.nextElement();
            if (ga.contains(visualPlugin))
                return ga.getName();
        }
        return null;
    }

    // Keep track of keyboard short-cut for docking/undocking
    // This needs to have global scope for Ctrl + N to toggle through components
    // in list
    boolean ctrlKeyPressed = false;

    boolean shiftPressed = false;

    // Placed this outside DockableComponent for consistency with location of
    // ctrlKeyPressed
    boolean DKeyPressed = false;

    private class DockableComponent extends JPanel {

        // Look out for the Ctrl-D keyboard shortcut combination in order to
        // toggle the docking status of the component.
        protected KeyListener shortCutHandler = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlKeyPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    DKeyPressed = true;
                    if (ctrlKeyPressed)
                        docker_actionPerformed(null);
                } else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                    if (ctrlKeyPressed && shiftPressed)
                        previous_actionPerformed(null);
                    if (ctrlKeyPressed && !shiftPressed)
                        next_actionPerformed(null);
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlKeyPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                    shiftPressed = false;
                else if (e.getKeyCode() == KeyEvent.VK_D)
                    DKeyPressed = false;
            }
        };

        // Get the keyboard focus so that we can listen to the Ctrl-D keyboard
        // events.
        protected MouseListener mListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        };

        public boolean isFocusTraversable() {
            return true;
        }

        private JLabel initiator = null;
        private String description = null;
        private Component plugin = null;
        private JPanel buttons = new JPanel();
        private JPanel topBar = new JPanel();
        private JButton docker = new JButton();
        private JButton minimize = new JButton();
        private JButton remove = new JButton();
        private boolean docked = true;
        private GuiArea guiArea = null;

        private ImageIcon dock_grey = new ImageIcon(ComboBoxSkin.class.getResource("dock_grey.gif"));
        private ImageIcon dock = new ImageIcon(ComboBoxSkin.class.getResource("dock.gif"));
        private ImageIcon dock_active = new ImageIcon(ComboBoxSkin.class.getResource("dock_active.gif"));
        private ImageIcon undock_grey = new ImageIcon(ComboBoxSkin.class.getResource("undock_grey.gif"));
        private ImageIcon undock = new ImageIcon(ComboBoxSkin.class.getResource("undock.gif"));
        private ImageIcon undock_active = new ImageIcon(ComboBoxSkin.class.getResource("undock_active.gif"));

        DockableComponent(Component plugin, String desc) {
            this.plugin = plugin;

            docker.setPreferredSize(new Dimension(16, 16));
            docker.setBorderPainted(false);
            docker.setIcon(undock_grey);
            docker.setRolloverEnabled(true);
            docker.setRolloverIcon(undock);
            docker.setPressedIcon(undock_active);
            docker.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    docker_actionPerformed(e);
                }
            });

            buttons.setLayout(new GridLayout(1, 3));
            buttons.add(docker);
            initiator = new JLabel(" ");
            initiator.setForeground(Color.darkGray);
            initiator.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.6f));
            initiator.setOpaque(true);

            topBar.setLayout(new BorderLayout());
            topBar.add(initiator, BorderLayout.CENTER);
            topBar.add(buttons, BorderLayout.EAST);

            setLayout(new BorderLayout());
            add(topBar, BorderLayout.NORTH);
            add(plugin, BorderLayout.CENTER);
            description = desc;
            setFocusable(true);
            addKeyListener(shortCutHandler);
            addMouseListener(mListener);
        }

        private JFrame frame = null;

        private void docker_actionPerformed(ActionEvent e) {
            if (docked) {
                docker.setIcon(dock_grey);
                docker.setRolloverIcon(dock);
                docker.setPressedIcon(dock_active);
                docker.setSelected(false);
                frame = new JFrame(description);
                frame.setUndecorated(false);
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        closeWindow_actionPerformed(we);
                    }
                });
                frame.getContentPane().setLayout(new BorderLayout());
                getGuiArea().undock(this);
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int width = (int) (dim.getWidth() * 0.7);
                int height = (int) (dim.getHeight() * 0.7);
                setPreferredSize(new Dimension(width, height));
                frame.getContentPane().add(this, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                docked = false;
                return;
            } else {
                if (frame != null) {
                    docker.setIcon(undock_grey);
                    docker.setRolloverIcon(undock);
                    docker.setPressedIcon(undock_active);
                    docker.setSelected(false);
                    docked = true;
                    getGuiArea().reDock(this);
                    frame.getContentPane().remove(this);
                    frame.dispose();
                }
            }
        }

        private void previous_actionPerformed(ActionEvent e) {
            if (docked)
                getGuiArea().showPrevious();
        }

        private void next_actionPerformed(ActionEvent e) {
            if (docked)
                getGuiArea().showNext();
        }

        private void closeWindow_actionPerformed(AWTEvent e) {
            guiArea.removeComponent(plugin);
            frame.dispose();
        }

        public Component getPlugin() {
            return plugin;
        }

        /**
         * Designate which GUI are this component docks at.
         *
         * @param ga GuiArea
         */
        public void setGuiArea(GuiArea ga) {
            guiArea = ga;
        }

        /**
         * Returns the <code>GuiArea</code> where this component was registered at
         * and where it docks.
         *
         * @return GuiArea
         */
        public GuiArea getGuiArea() {
            return guiArea;
        }

        public String toString() {
            return description;
        }
    }


    // A GUI area is a container for multiple components. Components
    // are listed within and can be selected from a combo box.
    private class GuiArea extends JPanel {
        JToolBar areaToolbar = new JToolBar();
        LocalComboBox areaCombo = new LocalComboBox();
        String name = null;

        // List of components registered with the GUI area and docked.
        ArrayList dockedComponents = new ArrayList();

        // List of components registered with the GUI area and currently undocked.
        ArrayList undockedComponents = new ArrayList();

        GuiArea(String areaName) {
            super();
            name = areaName;
            setLayout(new BorderLayout());
            areaToolbar = new JToolBar();
            areaCombo = new LocalComboBox();
            areaCombo.setBackground(Color.WHITE);
            add(areaToolbar, BorderLayout.NORTH);
            areaToolbar.add(areaCombo);

            // Listener for handling component selection from the combo box.
            // When a component is selected, it is just brought to the front.
            areaCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    DockableComponent wrapper = null;

                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        wrapper = (DockableComponent) e.getItem();
                        if (wrapper != null && dockedComponents.contains(wrapper))
                            wrapper.setVisible(false);
                    } else if (e.getStateChange() == ItemEvent.SELECTED) {
                        wrapper = (DockableComponent) e.getItem();
                        if (wrapper != null) {
                            remove(wrapper);
                            add(wrapper, BorderLayout.CENTER, 0);
                            wrapper.setVisible(true);
                            wrapper.requestFocusInWindow(); // Request focus to handle Ctrl-D
                        }
                        revalidate();
                        repaint();
                    }
                }
            });

        }

        public String getName() {
            return name;
        }

        /**
         * Adds the <code>visualPlugin</code> using <code>pluginName</code> as
         * its display name.
         *
         * @param visualPlugin Component
         * @param pluginName   String
         */
        public void addComponent(Component visualPlugin, String pluginName) {
            DockableComponent wrapper = new DockableComponent(visualPlugin, pluginName);
            wrapper.setGuiArea(this);
            this.add(wrapper, BorderLayout.CENTER);
            dockedComponents.add(wrapper);

            // It is necessary that the addItem() method below is invoked after
            // the component has been added to the container, i.e., after the
            // statement "this.add(wrapper, BorderLayout.CENTER)" above.
            areaCombo.addItem(wrapper);
            areaCombo.setSelectedItem(wrapper);
        }

        public void removeComponent(Component visualPlugin) {
            for (int i = 0; i < dockedComponents.size(); ++i)
                if (((DockableComponent) dockedComponents.get(i)).getPlugin() == visualPlugin) {
                    DockableComponent wrapper = (DockableComponent) dockedComponents.get(i);
                    dockedComponents.remove(i);
                    areaCombo.removeItem(wrapper);
                    remove(wrapper);
                }
            for (int i = 0; i < undockedComponents.size(); ++i)
                if (((DockableComponent) undockedComponents.get(i)).getPlugin() == visualPlugin) {
                    DockableComponent wrapper = (DockableComponent) undockedComponents.get(i);
                    undockedComponents.remove(i);
                    areaCombo.removeItem(wrapper);
                }
        }

        /**
         * Temporarily removes the designated <code>visualPlugin<code> from the
         * list of components available in this GUI area. The plugin must already be
         * registered with the GUI area.
         *
         * @param visualPlugin DockableImpl
         */
        public void undock(DockableComponent wrapper) {
            if (wrapper != null && dockedComponents.contains(wrapper)) {
                dockedComponents.remove(wrapper);
                undockedComponents.add(wrapper);
                remove(wrapper); // Remove from container
                areaCombo.removeItem(wrapper); // Remove from combo box
                //                areaCombo.setSelectedItem(getComponent(0));
                if (dockedComponents.size() > 0)
                    areaCombo.setSelectedItem(getComponent(0));
                else {
                    revalidate();
                    repaint();
                }
            }
        }

        /**
         * Redocks back to the visual area a component that was previously
         * undocked.
         *
         * @param visualPlugin DockableImpl
         */
        public void reDock(DockableComponent wrapper) {
            if (wrapper != null && undockedComponents.contains(wrapper)) {
                undockedComponents.remove(wrapper);
                dockedComponents.add(wrapper);
                add(wrapper, BorderLayout.CENTER, 0); // Add back to container
                areaCombo.addItem(wrapper);
                areaCombo.setSelectedItem(getComponent(0));
            }

        }

        /**
         * Shows the previous component in the visual area
         *
         * @param wrapper DockableComponent current visible component
         */
        public void showPrevious() {
            int index = areaCombo.getSelectedIndex();
            int count = areaCombo.getItemCount();
            if ((index - 1) >= 0 && (index - 1) < count) {
                areaCombo.setSelectedIndex(index - 1);
            } else
                areaCombo.setSelectedIndex(count - 1);
        }

        /**
         * Shows the next available component in the visual area
         *
         * @param wrapper DockableComponent current visible component
         */
        public void showNext() {
            int index = areaCombo.getSelectedIndex();
            int count = areaCombo.getItemCount();
            if ((index + 1) < count) {
                areaCombo.setSelectedIndex(index + 1);
            } else
                areaCombo.setSelectedIndex(0);
        }

        /**
         * Returns <code>true</code> if this visual area contains the designated
         * component, <code>false</code> otherwise.
         *
         * @param comp Component
         * @return boolean
         */
        public boolean contains(Component comp) {
            for (int i = 0; i < dockedComponents.size(); ++i)
                if (((DockableComponent) dockedComponents.get(i)).getPlugin() == comp)
                    return true;
            for (int i = 0; i < undockedComponents.size(); ++i)
                if (((DockableComponent) undockedComponents.get(i)).getPlugin() == comp)
                    return true;
            return false;
        }
    }


    /**
     * Extends <code>JComboBox</code> to enable listing of items in alphabetic
     * order.
     */
    static private class LocalComboBox extends JComboBox {
        public void addItem(Object ob) {
            if (getItemCount() == 0)
                super.addItem(ob);
            else {
                Object[] sortedElements = new Object[getItemCount()];
                for (int i = 0; i < getItemCount(); ++i)
                    sortedElements[i] = super.getItemAt(i);

                // Add item to Combo box, in alphabetical order.
                int index = Arrays.binarySearch(sortedElements, ob, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return o1.toString().toUpperCase().compareTo(o2.toString().toUpperCase());
                    }
                });
                insertItemAt(ob, -(index + 1));

            }
        }
    }

    public void setVisualizationType(DSDataSet type) {
        //ToDo: Show only those visualizations that support the selected type
    }
}
