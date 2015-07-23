package org.geworkbench.engine.config.events;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Manages the components that have registred listeners for broadcast events.
 * Maintains an event dispatching registry (an implementation of
 * {@link org.geworkbench.engine.config.events.ListenerRegistry ListenerRegistry})
 * that keeps track of which components should receive which events.
 * In order to receive a broadcast event of type XXX, a component must:
 * <UL>
 * <LI> Implement a sub-interface of <code>AppEventListener</code> that can handle
 * events of the XXX type.
 * <LI> Register itself with the <code>BroadcastEventRegistry</code>
 * as a listener of events of the XXX type.
 * </UL>
 * <p/>
 * There is only one registy for broadcast events, hence all methods are static.
 */
public class BroadcastEventRegistry {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    /**
     * Contains all plugin components that receive broadcast event notifications.
     */
    private static ListenerRegistryImpl broadcastListenerRegistry = new ListenerRegistryImpl();
    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Delegates to the same name mathod of <code>broadcastListenerRegistry</code>.
     */
    public static void addEventListener(Class listenerClass, AppEventListener listener) throws ListenerEventMismatchException, AppEventListenerException {
        broadcastListenerRegistry.addEventListener(listenerClass, listener);
    }

    /**
     * Delegates to the same name mathod of <code>broadcastListenerRegistry</code>.
     */
    public static AppEventListener[] getListenersForEvent(Class listenerClass) throws AppEventListenerException {
        return broadcastListenerRegistry.getListenersForEvent(listenerClass);
    }

    /**
     * Delegates to the same name mathod of <code>broadcastListenerRegistry</code>.
     */
    public static void removeListener(AppEventListener listener) {
        broadcastListenerRegistry.removeListener(listener);
    }

    /**
     * Delegates to the same name mathod of <code>broadcastListenerRegistry</code>.
     */
    public static Class[] getEventsForListener(AppEventListener listener) {
        return broadcastListenerRegistry.getEventsForListener(listener);
    }

    public static void removeEventListener(Class cls, AppEventListener listener) {
        try {
            broadcastListenerRegistry.removeEventListener(cls, listener);
        } catch (AppEventListenerException ex) {
        } catch (ListenerEventMismatchException ex) {
        }

    }

    /**
     * Delegates to the same name mathod of <code>broadcastListenerRegistry</code>.
     */
    public static void debugPrint() {
        broadcastListenerRegistry.debugPrint();
    }

}

