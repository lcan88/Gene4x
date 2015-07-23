package org.geworkbench.engine.skin;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.config.GUIFramework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 3.0
 */

public class Workbench extends GUIFramework {

    JDesktopPane desktop = new JDesktopPane();

    public Workbench() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktop);
        setJMenuBar(createMenuBar());
        desktop.setDesktopManager(new InternalFrameManager());
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuItem_actionPerformed(e);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuItem_actionPerformed(e);
            }
        });
        menu.add(menuItem);

        return menuBar;
    }

    protected InternalFrame createFrame() {
        InternalFrame frame = new InternalFrame();
        frame.setVisible(true);
        //        try {
        //            frame.setSelected(true);
        //            frame.setIcon(true);
        //        } catch (PropertyVetoException e) {}
        return frame;
    }

    void menuItem_actionPerformed(ActionEvent e) {
        if ("new".equals(e.getActionCommand())) {
            createFrame();
        } else {
            System.exit(0);
        }
    }

    public void addToContainer(String areaName, Component visualPlugin) {
        InternalFrame frame = createFrame();
        frame.setContentPane((Container) visualPlugin);
        desktop.add(frame);
    }

    public void addToContainer(String areaName, Component visualPlugin, String pluginName, Class mainPluginClass) {
        // Todo - deal with mainPluginClass mapping as in Skin
        InternalFrame frame = createFrame();
        frame.setContentPane((Container) visualPlugin);
        frame.setTitle(pluginName);
        desktop.add(frame);
    }

    public void remove(Component visualPlugin) {
    }

    public String getVisualArea(Component visualPlugin) {
        return "";
    }

    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    static int row = 0, column = 0;
    static int minimizedFrameCount = 0;

    class InternalFrame extends JInternalFrame {

        public InternalFrame() {
            super("", true, //resizable
                    true, //closable
                    true, //maximizable
                    true);//iconifiable
            openFrameCount++;
            //...Create the GUI and put it in the window...

            //...Then set the window size or call pack...
            setSize(300, 300);

            //Set the window's location.
            setLocation((xOffset - 20) * openFrameCount, (yOffset - 20) * openFrameCount);
            ImageIcon icon = new ImageIcon(Workbench.class.getResource("blue_sphere.bmp"));
            JLabel label = new JLabel(icon);
            //            getDesktopIcon().setUI((DesktopIconUI)BasicDesktopIconUI.createUI(label));
        }
    }

    class InternalFrameManager extends DefaultDesktopManager {
        protected Rectangle getBoundsForIconOf(JInternalFrame f) {
            minimizedFrameCount++;
            if (column * 40 >= 300) {
                column = 0;
                row++;
            }
            return new Rectangle((column++) * 40, row * 40, 32, 32);
        }
    }

    public void setVisualizationType(DSDataSet type) {
        //ToDo: Show only those visualizations that support the selected type
    }
}
