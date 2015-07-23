package org.geworkbench.engine.config.events;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Registry for application event listeners. It maintains the link between
 * individual application event listeners and the corresponding event types
 * the listeners have registered to listen for.
 */
public interface ListenerRegistry {
    /**
     * Verifies that <code>listener</code> implements the sub-interface of
     * <code>AppEventListener</code> desribed by the <code>listenerClass</code>.
     * In that event, registers <code>listener</code> as a component that receives
     * events that are handled by the <code>listenerClass</code> interface.
     *
     * @param listenerClass Describes a sub-interface of <code>AppEventListener</code>.
     * @param listener      A plugin component.
     * @throws ListenerEventMismatchException Thrown if <code>listener</code> does
     *                                        not implement the interface described by <code>listenerClass</code>.
     * @throws AppEventListenerException      Thrown if <code>listenerClass</code> is
     *                                        not a sub-interface of <code>AppEventListener</code>.
     */
    void addEventListener(Class listenerClass, AppEventListener listener) throws ListenerEventMismatchException, AppEventListenerException;

    /**
     * @param listenerClass Described a sub-interface of <code>AppEventListener</code>.
     * @return The list of all application listeners that are registered as
     *         implementing the interface described by the <code>listenerClass</code>.
     */
    AppEventListener[] getListenersForEvent(Class listenerClass) throws AppEventListenerException;

    /**
     * Removes the specified component from the registry, thus precluding that
     * component from receiving any more events from this registry.
     *
     * @param listener The component to be removed.
     */
    void removeListener(AppEventListener listener);

    /**
     * Recovers all sub-interfaces of <code>AppEventListener</code> that are
     * implemented by <code>listener</code> and have also been registered in this
     * registry.
     *
     * @param listener A plugin component.
     * @return Array of sub-interfaces of <code>AppEventListener</code>.
     */
    Class[] getEventsForListener(AppEventListener listener);
}