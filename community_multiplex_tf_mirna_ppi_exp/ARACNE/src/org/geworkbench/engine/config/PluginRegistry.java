package org.geworkbench.engine.config;

import org.geworkbench.engine.config.events.BroadcastEventRegistry;
import org.geworkbench.engine.config.events.EventSource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Registry keeping stock of (1) all the plugins defined for an application, and
 * (2) the association of plugins with extension points.
 */
public class PluginRegistry {
    // ---------------------------------------------------------------------------
    // --------------- Instance and static variables
    // ---------------------------------------------------------------------------
    /**
     * Stores the application plugins and their mappings to extension points.
     * Every extension point is a key in this HashMap. Its corresponsing value
     * is a Vector, containing the <code>PluginDescriptor</code>s for the
     * plugins associated with the extension point.
     */
    private static HashMap extensionPointsMap = new HashMap();
    /**
     * stores the info about visual area and visual plugin info.
     */
    private static HashMap visualAreaMap = new HashMap();
    /**
     * Stores all application plugins.
     */
    private static Vector componentVector = new Vector(100);
    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Checks if the designated extension point exists in the
     * <code>PluginRegistry</code>.
     *
     * @param extPoint The extension point to check
     */
    public static boolean existsExtensionPoint(String extPoint) {
        return (extensionPointsMap.containsKey(extPoint));
    }

    /**
     * Adds a plugin component descriptor at a named extension point.
     *
     * @param compDes  The <code>PluginDescriptor</code> object corresponding
     *                 to the plugin.
     * @param extPoint The name of the extension point where the plugin will be
     *                 added.
     */
    public static void addPlugInAtExtension(PluginDescriptor compDes, String extPoint) {
        Vector extPointVector;
        if (!extensionPointsMap.containsKey(extPoint)) {
            extensionPointsMap.put(extPoint, new Vector());
        }

        extPointVector = (Vector) extensionPointsMap.get(extPoint);
        if (!extPointVector.contains(compDes)) {
            extPointVector.add(compDes);
        }

    }

    /**
     * Returns all plugins associated with the designated extension point
     *
     * @param extPoint The name of the extension point.
     * @return An array containing the <code>PluginDescriptor</code> objects
     *         for all plugins associated with the extension point.
     */
    public static PluginDescriptor[] getPluginsAtExtension(String extPoint) {
        return extensionPointsMap.containsKey(extPoint) ? (PluginDescriptor[]) (((Vector) extensionPointsMap.get(extPoint)).toArray(new PluginDescriptor[1])) : null;
    }

    /**
     * Returns the list of all extension points for the designated plugin descriptor.
     *
     * @param compDes The plugin to check.
     * @return List of strings, each string being the name of an extension point.
     */
    public static String[] getExtensionsForPlugin(PluginDescriptor compDes) {
        Vector extensionPoints; // Put the results here.
        java.util.Iterator iter;
        String extensionName;
        // Set things up.
        extensionPoints = new Vector();
        iter = extensionPointsMap.keySet().iterator();
        while (iter.hasNext()) {
            extensionName = (String) iter.next();
            if (((Vector) extensionPointsMap.get(extensionName)).contains(compDes)) {
                extensionPoints.add(extensionName);
            }

        }

        return (String[]) extensionPoints.toArray(new String[1]);
    }

    /**
     * Registers the designated plugin.
     *
     * @param compDes Plugin <code>PluginDescriptor</code>.
     */
    public static void addPlugin(PluginDescriptor compDes) {
        if (!componentVector.contains(compDes)) {
            componentVector.add(compDes);
        }

    }

    /**
     * @param id The id of a plugin.
     * @return The plugin from the <code>PluginRegistry</code> that has the designated
     *         ID. Otherwise, if such a plugin does not exist, null.
     */
    public static PluginDescriptor getPluginDescriptor(String id) {
        int size = componentVector.size();
        int i;
        for (i = 0; i < size; ++i) {
            if (((PluginDescriptor) componentVector.get(i)).getID().compareTo(id) == 0) {
                return (PluginDescriptor) componentVector.get(i);
            }

        }

        return null;
    }

    /**
     * @param plugin Instance of an actual plugin.
     * @return The corresponding <code>PluginDescripto</code> from the <
     *         code>PluginRegistry</code>. Otherwise, if such a plugin does
     *         not exist, null.
     */
    public static PluginDescriptor getPluginDescriptor(Object plugin) {
        int size = componentVector.size();
        int i;
        for (i = 0; i < size; ++i) {
            if (((PluginDescriptor) componentVector.get(i)).getPlugin() == plugin) {
                return (PluginDescriptor) componentVector.get(i);
            }

        }

        return null;
    }

    /**
     * @param klass Class of an actual plugin.
     * @return The corresponding <code>PluginDescripto</code> from the <
     *         code>PluginRegistry</code>. Otherwise, if such a plugin does
     *         not exist, null.
     */
    public static PluginDescriptor[] getPluginDescriptorsOfType(Class klass) {
        int size = componentVector.size();
        int i;
        Vector descriptors = new Vector();
        for (i = 0; i < size; i++) {
            if (klass.isAssignableFrom(((PluginDescriptor) componentVector.get(i)).getPlugin().getClass())) {
                descriptors.add(componentVector.get(i));
            }

        }

        PluginDescriptor[] toBeReturned = new PluginDescriptor[descriptors.size()];
        i = 0;
        for (Enumeration e = descriptors.elements(); e.hasMoreElements(); i++) {
            toBeReturned[i] = (PluginDescriptor) e.nextElement();
        }

        return toBeReturned;
    }

    /**
     * Returns all registred application plugins
     *
     * @return An array containing the <code>PluginDescriptor</code> objects
     *         for all plugins in the application.
     */
    public static PluginDescriptor[] getPlugins() {
        return (PluginDescriptor[]) (componentVector.toArray(new PluginDescriptor[1]));
    }

    /**
     * Returns all plugins that are not associated with any extension point
     *
     * @return An array containing the <code>PluginDescriptor</code> objects
     *         for all plugins not associated with any extension point.
     */
    public static PluginDescriptor[] getUnmappedPlugins() {
        Vector retValue = new Vector();
        int size;
        size = componentVector.size();
        for (int i = 0; i < size; ++i) {
            if (getExtensionsForPlugin((PluginDescriptor) componentVector.get(i)).length == 0) {
                retValue.add(componentVector.get(i));
            }

        }

        return (PluginDescriptor[]) (retValue.toArray(new PluginDescriptor[1]));
    }

    /**
     * remove plugin from registry
     *
     * @param compDes
     * @author Xuegong Wang
     */
    public static void removePlugin(PluginDescriptor compDes) {
        if (!componentVector.contains(compDes)) {
            componentVector.remove(compDes);
        }

    }

    /**
     * @param compDes
     * @param extPoint
     * @author Xuegong Wang
     */
    public static void removePlugInAtExtension(PluginDescriptor compDes, String extPoint) {
        Vector extPointVector;
        if (!extensionPointsMap.containsKey(extPoint)) {
            extensionPointsMap.put(extPoint, new Vector());
        }

        extPointVector = (Vector) extensionPointsMap.get(extPoint);
        if (extPointVector.contains(compDes)) {
            extPointVector.remove(compDes);
        }

    }

    public static void addVisualAreaInfo(String visualAreaName, VisualPlugin comp) {
        visualAreaMap.put(comp, visualAreaName);
    }

    public static void removeVisualAreaInfo(VisualPlugin comp) {
        visualAreaMap.remove(comp);
    }

    public static String getVisualAreaInfo(VisualPlugin comp) {
        return (String) visualAreaMap.get(comp);
    }

    /**
     * For debugging purposes only. Prints the contents of the registry.
     */
    public static void debugPrint() {
        Vector classVector; // Put the results here.
        Iterator iter;
        PluginDescriptor compDes;
        String key;
        int size;
        if (org.geworkbench.util.Debug.debugStatus) {
            System.out.println("\n\nContents of PluginRegistry");
            System.out.println("--------------------------");
            // Set things up and list all application plugins.
            classVector = new Vector();
            System.out.println("********** List of application plugins");
            size = componentVector.size();
            for (int i = 0; i < size; ++i) {
                ((PluginDescriptor) componentVector.get(i)).debugPrint();
            }

            // List the application plugins under each extension point.
            classVector = new Vector();
            iter = extensionPointsMap.keySet().iterator();
            while (iter.hasNext()) {
                System.out.println(">>>>>>>>" + (key = (String) iter.next()));
                size = ((Vector) extensionPointsMap.get(key)).size();
                for (int i = 0; i < size; ++i) {
                    ((PluginDescriptor) ((Vector) extensionPointsMap.get(key)).get(i)).debugPrint();
                }

            }

            // List the registered listeners for each plugin
            classVector = new Vector();
            System.out.println("********** List of registered listeners per event source");
            size = componentVector.size();
            // Go over each application plugin
            for (int i = 0; i < size; ++i) {
                compDes = (PluginDescriptor) componentVector.get(i);
                System.out.println(">>>>> Working with component with ID = " + compDes.getID());
                try {
                    // Check if a plugin is an event source
                    if (Class.forName("org.geworkbench.engine.config.events.EventSource").isAssignableFrom(compDes.getPlugin().getClass())) {
                        ((EventSource) compDes.getPlugin()).debugPrint();
                    } else {
                        System.out.println("\t\tNot an Event Source");
                    }

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }

            }

            // Finally, print the broadcast events
            System.out.println("\n\nPrinting the broadcast registry");
            BroadcastEventRegistry.debugPrint();
            System.out.flush();
        }

    }

}

