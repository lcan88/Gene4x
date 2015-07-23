package org.geworkbench.engine.config;

import org.apache.commons.digester.Digester;
import org.geworkbench.engine.config.events.*;
import org.geworkbench.engine.config.rules.*;
import org.jdom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.StringTokenizer;

public class UIController { // extends Digester
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    static Document pluginDoc = null;
    static Document configDoc = null;
    //  public static Digester uiLauncher = new Digester();
    private static Digester uiLauncher = UILauncher.uiLauncher;

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------

    static void configureII() {
        uiLauncher.addRule("geaw-config/plugin-class", new PluginClassRule("org.geworkbench.engine.config.PluginClass"));

    }

    public static void removePlugin(String id) throws ListenerEventMismatchException, AppEventListenerException {
        PluginDescriptor compDes = PluginRegistry.getPluginDescriptor(id);

        //code to remove plugin from GUI
        //PluginRegistry.removePlugin(PluginRegistry.getPluginDescriptor(id));
        Container gui = ((VisualPlugin) compDes.getPlugin()).getComponent().getParent();

        gui.remove(((VisualPlugin) compDes.getPlugin()).getComponent());

    }

    public static void addtoVisualArea(String id, String visualarea) {
        PluginDescriptor compDes = PluginRegistry.getPluginDescriptor(id);
        if (!compDes.isVisualPlugin()) {
            System.err.println("PluginObject::addGUIComponent - Attempt to add as " + "GUI component the plugin with id = " + compDes.getID() + ", which does not implement " + "interface VisualPlugin.\n");
            return;
        }
        GeawConfigObject.getGuiWindow().addToContainer(visualarea, ((VisualPlugin) compDes.getPlugin()).getComponent(), compDes.getLabel(), compDes.getPluginClass());
    }

    public static void addExtensionPoint(PluginDescriptor compDes, String extPoint) {
        PluginRegistry.addPlugInAtExtension(compDes, extPoint);

        org.geworkbench.util.Debug.debug("PluginObject::addExtensionPoint --> Adding plugin id = " + compDes.getID() + " to extension point " + extPoint);
    }

    public static void registerBroadcastListener(PluginDescriptor compDes, String listnerClassName) {
        try {
            BroadcastEventRegistry.addEventListener(Class.forName(listnerClassName), (AppEventListener) compDes.getPlugin());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void registerCoupledListener(PluginDescriptor compDes, String listenerClassName, String sourceID) throws NotEventSourceException, AppEventListenerException, ListenerEventMismatchException {
        Class listenerClass;
        try {
            listenerClass = Class.forName(listenerClassName);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return;
        }
        EventSource eventSource = getEventSourceWithID(sourceID);
        eventSource.addEventListener(listenerClass, (AppEventListener) compDes.getPlugin());
    }

    /**
     * remove the coupled listeners
     *
     * @param listenerClass
     * @param sourceID
     */

    public static void reMoveCoupledListeners(PluginDescriptor compDes, String sourceID) throws NotEventSourceException {
        EventSource eventSource = getEventSourceWithID(sourceID);
        eventSource.removeListener((AppEventListener) compDes.getPlugin());
    }

    /**
     * Get the plugin with the designated ID. This plugin <b>must</b> be of type
     * <code>EventSource</code>.
     *
     * @param id The ID to search for.
     * @return The <code>EventSource</code> plugin with that id.
     * @throws NotEventSourceException
     */
    private static EventSource getEventSourceWithID(String id) throws NotEventSourceException {
        Object eventSource;

        eventSource = PluginRegistry.getPluginDescriptor(id).getPlugin();

        // Make sure that the eventSource subclasses the EventSource class.
        // If this is the case, add the current plugin as a listener to that
        // event source.
        try {
            if (!Class.forName("org.geworkbench.engine.config.events.EventSource").isAssignableFrom(eventSource.getClass())) {
                throw new NotEventSourceException("PluginObject::getEventSourceWithID()" + " - Attempt to couple listener to the component with ID " + id + " which does not extend the class org.geworkbench.engine.config.events.EventSource.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return (EventSource) eventSource;
    }

    /**
     * Invoked by the Digester. Registers an <code>ActionListener</code> with
     * a menu item.
     *
     * @param path
     * @param mode
     * @param var
     * @param icon
     * @param accelerator
     */
    public static void registerMenuItem(PluginDescriptor compDes, String path, String mode, String var, String icon, String accelerator) throws NotMenuListenerException, NotVisualPluginException, MalformedMenuItemException {
        JMenuItem menuItem;
        ActionListener menuListener;
        int i;

        // Allowed values for the 'mode' parameter.
        final String[] menuModifiers = {"onFocus", "always"};

        // First, check that the plugin wishing to register the ActionListener
        // implements MenuListener.
        if (!compDes.isMenuListener()) {
            throw new NotMenuListenerException("PluginObject::registerMenuItem - " + "Attempt to register a menu item listener by component with ID = " + compDes.getID() + ", that is not a MenuListener.");
        }

        // Then, verify that the 'mode' variable takes one among the values that
        // are permitted..
        // Just in case
        for (i = 0; i < menuModifiers.length; ++i) {
            if (mode.compareTo(menuModifiers[i]) == 0) {
                break;
            }
        }
        if (i == menuModifiers.length) {
            throw new MalformedMenuItemException("PluginObject::registerMenuItem - " + "Invalid value found for argument 'mode' = " + mode);
        }

        // Then, if the mode of listening is "onFocus", check that the plugin has
        // a visual representation.
        if ((mode.compareTo("onFocus") == 0) && (!compDes.isVisualPlugin())) {
            throw new NotVisualPluginException("PluginObject::registerMenuItem - " + "Attempt to register a 'onFocus' menu item listener by component with ID = " + compDes.getID() + ", which is not a VisualPlugin.");
        }

        // Return the menu item corresponding to the designated path. If the menu
        // item does not already exist, it gets created.
        try {
            menuItem = getMenuItem(compDes, path, icon, accelerator);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return;
        }

        // Query the plugin to get the ActionListener that should be
        // used with this menu item.
        menuListener = ((MenuListener) compDes.getPlugin()).getActionListener(var);
        if (menuListener != null) {
            compDes.addMenuListener(menuItem, menuListener, mode);

            // If the 'mode' for the listener is "always", go on and add the listener
            // to the menu item.
        }
        if (mode.compareTo("always") == 0) {
            menuItem.addActionListener(menuListener);
        }
    }

    /**
     * Returns the menu item specified by the <code>path</code> parameter. E.g.,
     * if <code>path = "File.Save.Workspace"</code>, the menu item will be
     * found within the "Save" submenu of the main "File" menu. Arbitrarily deep
     * menu nesting is allowed. If such a menu item does not already exist in the
     * menu structure, it is created. If possible, the <code>setIcon</code> and
     * <code>setAccelerator</code> methods of the returned menu item will have been
     * set using the parameters <code>icon</code> and <code>accelerator</code>.
     *
     * @param path        Describes the position within the menu structure of the menu item.
     * @param icon        Optional icon to be associated with the menu item.
     * @param accelerator Optional accelerator to be associated with the menu item.
     * @return The designated menu item.
     * @throws MalformedMenuPathException
     * @throws DynamicMenuItemException
     * @throws ClassNotFoundException
     */
    private static JMenuItem getMenuItem(PluginDescriptor compDes, String path, String icon, String accelerator) throws MalformedMenuItemException, DynamicMenuItemException, ClassNotFoundException {
        StringTokenizer tokens; // Breaks up 'path' into its parts.
        JMenu[] mainMenuItems;
        String topLevelMenuText;
        JMenu newMenu, topMenu;
        int len;
        int i;

        // Identify the various sub-menus that lead to the new/existing menu item.
        // There must be at least one such sub-menu (the top-level one).
        tokens = new StringTokenizer(path, GeawConfigObject.menuItemDelimiter);
        if (tokens.countTokens() <= 1) {
            throw new MalformedMenuItemException("PluginObject::getMenuItem - " + "The menu path '" + path + "' is not properly formed.");
        }

        topLevelMenuText = tokens.nextToken();

        // Initialize 'mainMenuItems' with the top-level menu items
        MenuElement[] temp = GeawConfigObject.getMenuBar().getSubElements();
        mainMenuItems = new JMenu[temp.length];
        len = mainMenuItems.length;
        for (i = 0; i < len; ++i) {
            mainMenuItems[i] = (JMenu) temp[i];

            // Find (or create, if not already there) the top-level sub-menu designated
            // in the 'path' argument.
        }
        for (i = 0; i < len; ++i) {
            if (mainMenuItems[i].getText().compareTo(topLevelMenuText) == 0) {
                break;
            }
        }

        // If the top-level submenu is not there, create it.
        if (i == len) {
            newMenu = new JMenu();
            // newMenu.setFont(GeawConfigObject.menuItemFont);
            newMenu.setText(topLevelMenuText);

            // Make sure that the "Help" menu remains at the end.
            GeawConfigObject.getMenuBar().remove(GeawConfigObject.getHelpMenu());
            GeawConfigObject.getMenuBar().add(newMenu);
            GeawConfigObject.getMenuBar().add(GeawConfigObject.getHelpMenu());
            topMenu = newMenu;
        } else {
            topMenu = mainMenuItems[i];
        }
        return addMenuItem(compDes, tokens, topMenu, icon, accelerator);
    }

    /**
     * Recursively navigates the existing menu structure until it finds the
     * spot where the menu item specified by parameter <code>tokens</code> should
     * reside. If no such menu item already exists, it is being created.
     *
     * @param tokens      A list of successive menu titles desribing the (remaining)
     *                    hierarchical menu structure of the menu item being processed.
     * @param parentMenu  The current root of the explored menu structure.
     * @param icon        An optional icon to be associated with the menu item.
     * @param accelerator An optional accelerator to be associated with the menu item.
     * @return The found (or created) menu item that is placed at the requested point
     *         within the menu structure.
     * @throws DynamicMenuItemException
     * @throws ClassNotFoundException
     */
    private static JMenuItem addMenuItem(PluginDescriptor compDes, StringTokenizer tokens, JMenu parentMenu, String icon, String accelerator) throws DynamicMenuItemException, ClassNotFoundException {
        JMenuItem[] parentMenuItems;
        String menuText;
        JMenu newMenu;
        JMenuItem theMenuItem;
        int len;
        int i;

        menuText = tokens.nextToken();

        // Initialize 'parentMenuItems' with the menu items of the 'parentMenu'.
        MenuElement[] temp = parentMenu.getPopupMenu().getSubElements();
        parentMenuItems = new JMenuItem[temp.length];
        len = parentMenuItems.length;
        for (i = 0; i < len; ++i) {
            parentMenuItems[i] = (JMenuItem) temp[i];

            // Check if there is already an existing menu item with the text we are
            // currently ptocessing.
        }
        for (i = 0; i < len; ++i) {
            if (parentMenuItems[i].getText().compareTo(menuText) == 0) {
                break;
            }
        }

        if (i == len) {

            // Check if this is the final part of the original processing path.
            // In that case, create and return the new menu item.
            if (tokens.countTokens() == 0) {
                theMenuItem = new JMenuItem(menuText);
                if (icon != null) {
                    theMenuItem.setIcon(new ImageIcon(compDes.getPlugin().getClass().getResource(icon)));
                }
                parentMenu.add(theMenuItem);
                return theMenuItem;
            } else {
                newMenu = new JMenu();
                // newMenu.setFont(GeawConfigObject.menuItemFont);
                newMenu.setText(menuText);
                parentMenu.add(newMenu);
                return addMenuItem(compDes, tokens, newMenu, icon, accelerator);
            }
        } else {
            if (Class.forName("javax.swing.JMenu").isAssignableFrom(parentMenuItems[i].getClass())) {
                if (tokens.countTokens() == 0) {
                    throw new DynamicMenuItemException("PluginObject::addMenuItem - " + "Final menu item conflict with existing submenu.");
                } else {
                    return addMenuItem(compDes, tokens, (JMenu) parentMenuItems[i], icon, accelerator);
                }
            } else {
                if (tokens.countTokens() != 0) {
                    throw new DynamicMenuItemException("PluginObject::addMenuItem - " + "A subpath in a new menu item conflict with an existing terminal " + "menu item.");
                } else {
                    // If the existing menu item does not have an associate icon,
                    // add the icon requested by the <menu-item> currently processed.
                    if (parentMenuItems[i].getIcon() == null && icon != null) {
                        parentMenuItems[i].setIcon(new ImageIcon(compDes.getClass().getResource(icon)));
                    }
                    return parentMenuItems[i];
                }
            }
        }
    }

    public void checkSubscriberInterface(Object component) {
        Class[] interfaces = component.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName().endsWith("Subscriber")) {
                //ArrayList components = (ArrayList)subscribers.get(interfaces[i]);
                // if(components == null) {
                //   components = new ArrayList();
                //   subscribers.put(interfaces[i], components);
                // }
                //  components.add(component);
            }
        }
    }

    public void uncheckSubscriberInterface(Object component) {
        Class[] interfaces = component.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName().endsWith("Subscriber")) {
                // ArrayList components = (ArrayList)subscribers.get(interfaces[i]);
                //  if(components != null) {
                //      components.remove(component);
                // }
            }
        }
    }

    public static void parse(String xmlfilepath) {
        try {
            InputStream is = Class.forName("org.geworkbench.engine.config.UIController").getResourceAsStream(xmlfilepath);
            configureII();
            uiLauncher.parse(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
