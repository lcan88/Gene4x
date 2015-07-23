package org.geworkbench.engine.config.events;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Base class for all application events.
 */
public class Event {
    // ---------------------------------------------------------------------------
    // --------------- Instance variables
    // ---------------------------------------------------------------------------
    /**
     * Reference to the application plugin throwing the event.
     */
    protected EventSource source;
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    /**
     * Instantiate an event thrown by the desiganted source.
     *
     * @param s The event source.
     */
    public Event(EventSource s) {
        source = s;
    }


}