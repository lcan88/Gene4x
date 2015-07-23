package org.geworkbench.engine.config.events;

import org.geworkbench.engine.config.PluginRegistry;
import org.geworkbench.util.Debug;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Default Implementation of the ListenerRegistry interface.
 */
public class ListenerRegistryImpl implements ListenerRegistry {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    /**
     * Contains all plugin components registered to receive event notifications.
     */
    private HashMap listenerRegistry = new HashMap();

    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    // Method implementation from interface ListenerRegistry
    public void addEventListener(Class listenerClass, AppEventListener listener) throws ListenerEventMismatchException, AppEventListenerException {
        Vector registeredListeners;
        // Check that the "listenerClass" does indeed describe a sub-interface of
        // AppEventListener.
        if (!isAppEventListener(listenerClass)) {
            throw new AppEventListenerException("ListenerRegistryImpl::addEventListener - " + "listenerClass argument " + listenerClass.getName() + " does not implement an AppEventListener sub-interface.");
        }

        // Check that the "listener" implements the interface described by
        // "listenerClass"
        if (!listenerClass.isInstance(listener)) {
            throw new ListenerEventMismatchException("ListenerRegistryImpl::addEventListener - " + "'listener' argument " + listener.getClass().getName() + " does not implement the interface " + listenerClass.getName() + " prescribed by the 'listenerClass' argument.");
        }

        // Register the "listener" to be notified for events that can be handled by
        // the interface described in "listenerClass".
        if (!listenerRegistry.containsKey(listenerClass)) {
            listenerRegistry.put(listenerClass, new Vector());
        }

        registeredListeners = (Vector) listenerRegistry.get(listenerClass);
        if (!registeredListeners.contains(listener)) {
            registeredListeners.add(listener);
        }

    }

    // Method implementation from interface ListenerRegistry
    public AppEventListener[] getListenersForEvent(Class listenerClass) throws AppEventListenerException {
        AppEventListener[] returnValue = null;
        if (listenerClass != null) {
            if (!isAppEventListener(listenerClass)) {
                throw new AppEventListenerException("ListenerRegistryImpl::getListenersForEvent - " + "'listenerClass' argument " + listenerClass.getName() + "does not implement a subinteface of AppEventListener. ");
            }

            if (listenerRegistry.containsKey(listenerClass)) {
                returnValue = (AppEventListener[]) ((Vector) listenerRegistry.get(listenerClass)).toArray(new AppEventListener[listenerRegistry.size()]);
            }

        }

        return returnValue;
    }

    // Method implementation from interface ListenerRegistry
    public void removeListener(AppEventListener listener) {
        Vector upForRemoval; // Collection of listeners to be removed.
        Iterator iter;
        if (listener == null) {
            return;
        }

        // Set things up and then perform the removal.
        upForRemoval = new Vector();
        upForRemoval.add(listener);
        iter = listenerRegistry.values().iterator();
        while (iter.hasNext()) {
            ((Vector) iter.next()).removeAll(upForRemoval);
        }

    }

    // Method implementation from interface ListenerRegistry
    public Class[] getEventsForListener(AppEventListener listener) {
        Vector classVector; // Put the results here.
        Iterator iter;
        Class key;
        if (listener == null) {
            return null;
        }

        // Set things up.
        classVector = new Vector();
        iter = listenerRegistry.keySet().iterator();
        // Find the <code>AppEventListner</code> interfaces that the "listener"
        // has registered with this registry.
        while (iter.hasNext()) {
            key = (Class) iter.next();
            if (((Vector) listenerRegistry.get(key)).contains(listener)) {
                classVector.add(key);
            }

        }

        return (Class[]) classVector.toArray(new Class[1]);
    }

    /**
     * Checks if the parameter <code>listenerClass</code> describes a class that
     * implements a sub-interface of the </code>AppEventListener</code>.
     *
     * @param listenerClass The <code>Class</code> to check.
     * @return The result of the check.
     */
    private boolean isAppEventListener(Class listenerClass) {
        boolean checkClass = false;
        try {
            checkClass = (listenerClass.isInterface() && Class.forName("org.geworkbench.engine.config.events.AppEventListener").isAssignableFrom(listenerClass));
        } catch (ClassNotFoundException e) {
            // This should never happen - The class "AppEventListener" should
            // always be there.
            System.err.println("ListenerRegistryImpl::addEventListener - " + "Class AppEventListener not found");
            System.err.flush();
            //System.exit(1);
        }

        return checkClass;
    }

    /**
     * removes from retistry the
     *
     * @param listenerClass
     * @param listener
     * @throws ListenerEventMismatchException
     * @throws AppEventListenerException
     * @author Xuegong Wang
     */
    public void removeEventListener(Class listenerClass, AppEventListener listener) throws ListenerEventMismatchException, AppEventListenerException {
        Vector registeredListeners;
        // Check that the "listenerClass" does indeed describe a sub-interface of
        // AppEventListener.
        if (!isAppEventListener(listenerClass)) {
            throw new AppEventListenerException("ListenerRegistryImpl::addEventListener - " + "listenerClass argument " + listenerClass.getName() + " does not implement an AppEventListener sub-interface.");
        }

        // Check that the "listener" implements the interface described by
        // "listenerClass"
        if (!listenerClass.isInstance(listener)) {
            throw new ListenerEventMismatchException("ListenerRegistryImpl::addEventListener - " + "'listener' argument " + listener.getClass().getName() + " does not implement the interface " + listenerClass.getName() + " prescribed by the 'listenerClass' argument.");
        }

        // Remove the "listener" to be notified for events that can be handled by
        // the interface described in "listenerClass".
        if (listenerRegistry.containsKey(listenerClass)) {
            registeredListeners = (Vector) listenerRegistry.get(listenerClass);
            if (registeredListeners.contains(listener)) {
                registeredListeners.remove(listener);
            }

        }

    }

    /**
     * For debuging purposes.
     */
    public void debugPrint() {
        Vector classVector; // Put the results here.
        Vector pluginVector;
        Iterator iter;
        Class key;
        int size;
        if (Debug.debugStatus) {
            // Set things up.
            classVector = new Vector();
            iter = listenerRegistry.keySet().iterator();
            // Find the <code>AppEventListner</code> interfaces that have been
            // registered with each 'key' listener type.
            while (iter.hasNext()) {
                key = (Class) iter.next();
                System.out.println("\tListener Type = " + key.getName());
                pluginVector = (Vector) listenerRegistry.get(key);
                size = pluginVector.size();
                for (int i = 0; i < size; ++i) {
                    System.out.println("\t\tRegistered Listerner is component with ID = " + PluginRegistry.getPluginDescriptor((Object) pluginVector.get(i)).getID());
                }

            }

        }

    }

}
