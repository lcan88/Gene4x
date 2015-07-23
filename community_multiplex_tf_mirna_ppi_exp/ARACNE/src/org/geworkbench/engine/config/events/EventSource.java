package org.geworkbench.engine.config.events;

import org.geworkbench.util.BaseRuntimeException;

import java.lang.reflect.Method;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 *
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Base class for all application plugins that throw events. Maintains an event
 * dispatching registry (an implementation of {@link org.geworkbench.engine.config.events.ListenerRegistry
 * ListenerRegistry}) that manages which components should receive which events.
 * In order to receive an event of type XXX from this event source, a component must:
 * <UL>
 * <LI> Implement a sub-interface of <code>AppEventListener</code> that can handle
 * events of the XXX type.
 * <LI> Register itself with this event source as a listener of events of the XXX
 * type.
 * </UL>
 */
public abstract class EventSource {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    /**
     * Contains all plugin components that receive event notifications from this
     * event source.
     */
    private ListenerRegistryImpl coupledListenerRegistry = new ListenerRegistryImpl();
    // ---------------------------------------------------------------------------
    // --------------- Methods
    // ---------------------------------------------------------------------------
    /**
     * Delegates to the same name mathod of <code>coupledListenerRegistry</code>.
     */
    public void addEventListener(Class listenerClass, AppEventListener listener) throws ListenerEventMismatchException, AppEventListenerException {
        coupledListenerRegistry.addEventListener(listenerClass, listener);
    }

    /**
     * Delegates to the same name mathod of <code>coupledListenerRegistry</code>.
     */
    public AppEventListener[] getListenersForEvent(Class listenerClass) throws AppEventListenerException {
        return coupledListenerRegistry.getListenersForEvent(listenerClass);
    }

    /**
     * Delegates to the same name method of <code>coupledListenerRegistry</code>.
     */
    public void removeListener(AppEventListener listener) {
        coupledListenerRegistry.removeListener(listener);
    }

    /**
     * Delegates to the same name mathod of <code>coupledListenerRegistry</code>.
     */
    public Class[] getEventsForListener(AppEventListener listener) {
        return coupledListenerRegistry.getEventsForListener(listener);
    }

    /**
     * Implementation of application event firing. The method will throw the
     * event to (1) all listeners of type <code>listenerClass</code> that have registered
     * themselves with this <code>EventSource</code> object, and (2) all listeners
     * that have requested to listening to events of this type in a broadcast mode.
     * <p/>
     * The event is thrown
     * by invoking the <code>method</code> on the corresponding listener and passsing
     * <code>event<code> as an argument.
     *
     * @param listenerClass The <code>Class</code> of the listener. This class
     *                      is expected to be implementing <code>AppEventListener</code>.
     * @param method
     * @param event
     * @throws AppEventListenerException
     */
    public void throwEvent(Class listenerClass, String method, Event event) throws AppEventListenerException {
        AppEventListener[] listeners;
        // Check that the method arguments are OK
        if (listenerClass == null || method == null || event == null) {
            throw new BaseRuntimeException("EventSource::throwEvent - " + "The following method arguments are null: " + (listenerClass == null ? "'listenerClass'" : "") + (method == null ? " 'method'" : "") + (event == null ? " 'event'" : ""));
        }
        // Call the listeners for coupled events first
        if ((listeners = getListenersForEvent(listenerClass)) != null) {
            for (int i = 0; i < listeners.length; ++i) {
                try {
                    Method func = listenerClass.getMethod(method, new Class[]{event.getClass()});
                    func.invoke(listeners[i], new Object[]{event});
                } catch (Exception e) {
                    // A reflection-related exception occured.
                    e.printStackTrace();
                }
            }
        }

        // Call the listeners for the broadcast events next
        if ((listeners = BroadcastEventRegistry.getListenersForEvent(listenerClass)) != null) {
            for (int i = 0; i < listeners.length; ++i) {
                AppEventListener listener = listeners[i];
                if (listener != null) {
                    try {
                        Method func = listenerClass.getMethod(method, new Class[]{event.getClass()});
                        func.invoke(listeners[i], new Object[]{event});
                    } catch (Exception e) {
                        // A reflection-related exception occured.
                        System.out.println("Event Management Error: " + listeners[i] + " << " + event);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * For debugging purposes only.
     */
    public void debugPrint() {
        coupledListenerRegistry.debugPrint();
    }

}
