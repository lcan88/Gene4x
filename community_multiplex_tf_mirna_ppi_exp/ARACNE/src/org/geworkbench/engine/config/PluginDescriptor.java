package org.geworkbench.engine.config;

import org.geworkbench.engine.config.rules.*;
import org.geworkbench.engine.management.ComponentRegistry;
import org.geworkbench.engine.management.ComponentResource;
import org.geworkbench.util.Debug;

import javax.help.HelpSet;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * The <code>PluginDescriptor</code> class stores references to the plugin
 * components comprising the application. These components are initialized as
 * a result of processing the <code>&lt;plugin&gt;</code> rules in the
 * application's configuration file.
 */
public class PluginDescriptor extends IdentifiableImpl implements Comparable {
    // ---------------------------------------------------------------------------
    // --------------- Instance and static variables
    // ---------------------------------------------------------------------------
    /**
     * Instance of a plugin component.
     */
    private Object plugin = null;
    /**
     * The keys of this map are <code>JMenuItem</code>s whose selection results
     * in a notification to the plugin ONLY if the plugin has the focus (this
     * plug in must be one implementing the <code>VisualPlugin</code> interface.
     * Every key is mapped to a
     * <code>Vector</code> containing all <code>ActionListener</code>s that
     * the plugin has registered with the corresponding menu item.
     */
    private HashMap onFocusMenuItems = new HashMap();
    /**
     * The keys of this map are <code>JMenuItem</code>s whose selection results
     * in a notification being sent to the plugin REGARDLESS of the plugin having
     * the focus or not (the plugin is not required in this case to implement the
     * <code>VisualPlugin</code> interface. Every key is mapped to a
     * <code>Vector</code> containing all <code>ActionListener</code>s that
     * the plugin has registered with the corresponding menu item.
     */
    private HashMap alwaysMenuItems = new HashMap();
    /**
     * The heplset associated with this plug in.
     */
    private HelpSet helpSet = null;
    /**
     * the information about the menuitems.
     */
    private ArrayList menuItemInfos = new ArrayList();

    /**
     * Stores the component for each module method.
     */
    private Map<String, Object> modules = new HashMap<String, Object>();

    /**
     * Stores the configuration module mappings (name -> id).
     */
    private Map<String, String> moduleMappings = new HashMap<String, String>();

    /**
     * Stores the visual location directive
     */
    private String visualLocation;

    /**
     * Order in which this component was loaded, and which it prefers to be displayed relative to others.
     */
    private int preferredOrder;

    /**
     * The class of this plugin descriptor.
     */
    private Class pluginClass;

    /**
     * Set of subscription types to ignore.
     */
    private Set<Class> subscriptionIgnoreSet = new HashSet<Class>();

    private String pluginClassPath;

    private ClassLoader loader;

    private ComponentResource resource;

    private ComponentMetadata metadata;

    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    /**
     * Instantiates a descriptor for a plugin component described by a class file
     * and assignes to it the
     * designated id and name.
     *
     * @param className A string containing the fully qualified class name.
     * @param someID          Assigned id.
     * @param someName        Assigned name.
     */
    public PluginDescriptor(String className, String someID, String someName, String resourceName, int preferredOrder) {
        super(someID, someName);
        this.preferredOrder = preferredOrder;
        this.pluginClassPath = className;
        resource = null;
        if (resourceName != null) {
            resource = ComponentRegistry.getRegistry().getComponentResourceByName(resourceName);
            if (resource == null) {
                System.out.println("Warning: Resource '" + resourceName + "' for component '" + someName + "' not found.");
            }
        }
        try {
            if (resource == null) {
                pluginClass = Class.forName(className);
                loader = pluginClass.getClassLoader();
            } else {
                loader = resource.getClassLoader();
                pluginClass = loader.loadClass(className);
            }
            instantiate();
        } catch (ClassNotFoundException e) {
            throw new org.geworkbench.util.BaseRuntimeException("Could not instantiate plugin: " + className, e);
        }
    }

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------

    private void instantiate() {
        ComponentRegistry componentRegistry = ComponentRegistry.getRegistry();
        try {
            ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();
            if (resource != null) {
                Thread.currentThread().setContextClassLoader(resource.getClassLoader());
            }
            plugin = componentRegistry.createComponent(pluginClass, this);
            Thread.currentThread().setContextClassLoader(defaultClassLoader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate plugin:" + pluginClass, e);
        }
    }

    /**
     * Definition of equality for <code>PluginDescriptor</code> objects. Checks
     * for equality will be performed when attempting to add component descriptors
     * in the {@link org.geworkbench.engine.config.PluginRegistry PluginRegistry}.
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        return (this.id == ((PluginDescriptor) obj).id);
    }

    public int compareTo(Object o) {
        if (o instanceof PluginDescriptor) {
            PluginDescriptor other = (PluginDescriptor) o;
            return (id.compareToIgnoreCase(other.id));
        } else {
            return -1;
        }
    }

    public Object getModule(String moduleMethod) {
        return modules.get(moduleMethod);
    }

    public void setModule(String moduleMethod, Object module) {
        modules.put(moduleMethod, module);
    }

    public String getModuleID(String moduleMethod) {
        return moduleMappings.get(moduleMethod);
    }

    public void setModuleID(String moduleMethod, String id) {
        moduleMappings.put(moduleMethod, id);
    }

    public Map<String, String> getModuleMappings() {
        return moduleMappings;
    }

    void setPlugin(Object pgIn) {
        this.plugin = pgIn;
    }

    public Object getPlugin() {
        return plugin;
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(Class pluginClass) {
        this.pluginClass = pluginClass;
    }

    public ClassLoader getClassLoader(){
        return loader;
    }

    public boolean isLoadedFromGear() {
        if (resource == null) {
            return false;
        }
        return resource.isFromGear();
    }

    public int getPreferredOrder() {
        return preferredOrder;
    }

    public void setPreferredOrder(int preferredOrder) {
        this.preferredOrder = preferredOrder;
    }

    /**
     * Registers an <code>ActionListener</code> with a <code>JMenuItem</code>.
     *
     * @param mItem A menu item.
     * @param al    An <code>ActionListener</code>.
     * @param mode  Designates when the <code>ActionListerner</code> will be
     *              notified.
     */
    public void addMenuListener(JMenuItem mItem, ActionListener al, String mode) throws MalformedMenuItemException {
        HashMap hMap;
        Vector actionListeners;
        int i;
        // Register the ActionListener with the menu item.
        hMap = (mode.compareTo("onFocus") == 0 ? onFocusMenuItems : alwaysMenuItems);
        if (!hMap.containsKey(mItem))
            hMap.put(mItem, new Vector());
        actionListeners = (Vector) hMap.get(mItem);
        if (!actionListeners.contains(al))
            actionListeners.add(al);
    }

    /**
     * Checks if the plugin implements the <code>MenuListener</code> interface.
     *
     * @return The result of the test
     */
    public boolean isMenuListener() {
        try {
            if (!Class.forName("org.geworkbench.engine.config.MenuListener").isAssignableFrom(plugin.getClass()))
                return false;
            else
                return true;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace(System.err);
            return false;
        }

    }

    /**
     * Checks if the plugin implements the <code>VisualPlugin</code> interface.
     *
     * @return The result of the test.
     */
    public boolean isVisualPlugin() {
        try {
            if (!Class.forName("org.geworkbench.engine.config.VisualPlugin").isAssignableFrom(pluginClass))
                return false;
            else
                return true;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace(System.err);
            return false;
        }

    }

    /**
     * Invoked when the plugin (which must implement the <code>VisualPlugin</code>
     * interface) looses the keyboard focus. When this happens, this method takes care
     * of removing the "onFocus" ActionListeners that this plugin has associated with
     * various menu items.
     */
    public void disableFocusMenuItems() {
        Iterator iter;
        JMenuItem menuItem;
        Vector actionListeners;
        int len;
        // Set things up.
        iter = onFocusMenuItems.keySet().iterator();
        while (iter.hasNext()) {
            menuItem = (JMenuItem) iter.next();
            actionListeners = (Vector) onFocusMenuItems.get(menuItem);
            len = actionListeners.size();
            for (int i = 0; i < len; ++i)
                menuItem.removeActionListener((ActionListener) actionListeners.get(i));
            // If there are no more ActionListeners associated with the menu item,
            // make the menu item unselectable.
            if (menuItem.getActionListeners().length == 0)
                menuItem.setEnabled(false);
        }

    }

    /**
     * Invoked when the plugin (which must implement the <code>VisualPlugin</code>
     * interface) gains the keyboard focus. When this happens, this method takes care
     * of removing the "onFocus" ActionListeners that this plugin has associated with
     * various menu items.
     */
    public void enableFocusMenuItems() {
        Iterator iter;
        JMenuItem menuItem;
        Vector actionListeners;
        int len;
        // Set things up.
        iter = onFocusMenuItems.keySet().iterator();
        while (iter.hasNext()) {
            menuItem = (JMenuItem) iter.next();
            actionListeners = (Vector) onFocusMenuItems.get(menuItem);
            len = actionListeners.size();
            for (int i = 0; i < len; ++i)
                menuItem.addActionListener((ActionListener) actionListeners.get(i));
            if (menuItem.getActionListeners().length > 0)
                menuItem.setEnabled(true);
        }

    }

    /**
     * Set the help set for this plugin. The help set contains references to
     * the documents comprising the online section that the plugin wants to
     * register with the application's online help system.
     *
     * @param hs The help set to use.
     */
    public void setHelpSet(HelpSet hs) {
        helpSet = hs;
    }

    /**
     * Return the helpset corresponding to this plugin.
     */
    public HelpSet getHelpSet() {
        return helpSet;
    }

    /**
     * add menuitem info into an arraylist
     *
     * @param path        String
     * @param mode        String
     * @param var         String
     * @param icon        String
     * @param accelerator String
     */
    public void addMenuItemInfo(String path, String mode, String var, String icon, String accelerator) {
        HashMap menu = new HashMap();
        menu.put("path", path);
        menu.put("mode", mode);
        menu.put("var", var);
        menu.put("icon", icon);
        menu.put("accelerator", accelerator);
        menuItemInfos.add(menu);
    }

    /**
     * returns the menuitem infos
     *
     * @return ArrayList
     */
    public ArrayList getMenuItemInfos() {
        return menuItemInfos;
    }

    /**
     * For debuging purposes only.
     */
    public void debugPrint() {
        if (Debug.debugStatus) {
            System.out.println("PluginDescriptor -> id = " + id + ", name = " + name + ", Class = " + plugin.getClass().toString());
            System.out.flush();
        }

    }

    public String getVisualLocation() {
        return visualLocation;
    }

    public void setVisualLocation(String visualLocation) {
        this.visualLocation = visualLocation;
    }

    public void addTypeToSubscriptionIgnoreSet(Class type) {
        subscriptionIgnoreSet.add(type);
    }

    public boolean isInSubscriptionIgnoreSet(Class type) {
        return subscriptionIgnoreSet.contains(type);
    }

    public void setComponentMetadata(ComponentMetadata metadata) throws NotMenuListenerException, MalformedMenuItemException, NotVisualPluginException {
        this.metadata = metadata;
        //// Set up help set
        if (metadata.getHelpSet() != null) {
            String pluginHelpSet = metadata.getHelpSet();
            if (pluginHelpSet == null)
                return;
            HelpSet pginHS = null;
            // Attempt to open the help set file and create a helpset object.
            try {
                ClassLoader cl = getPlugin().getClass().getClassLoader();
                URL url = HelpSet.findHelpSet(cl, pluginHelpSet);
                pginHS = new HelpSet(cl, url);
            } catch (Exception ee) {
                System.err.println("Help Set " + pluginHelpSet + " for component " + getLabel() + " not found.");
                return;
            }
            // Add the helpset for the new component to the master set.
            GeawConfigObject.addHelpSet(pginHS);
        }
        //// Set up menu items
        List<MenuItemInfo> menuItems = metadata.getMenuInfoList();
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItemInfo menuItemInfo = menuItems.get(i);
            PluginObject.registerMenuItem(this,
                    menuItemInfo.getPath(),
                    menuItemInfo.getMode(),
                    menuItemInfo.getVar(),
                    menuItemInfo.getIcon(),
                    menuItemInfo.getAccelerator());
        }
    }

    public String toString() {
        return getID() + ": " + getLabel();
    }
}