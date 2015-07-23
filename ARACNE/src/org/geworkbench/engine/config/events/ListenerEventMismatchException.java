package org.geworkbench.engine.config.events;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Thrown when attempting to associate a component with an Event for which the
 * component does not implement an appropriate <code>AppEventListener</code>
 * interface for.
 */
public class ListenerEventMismatchException extends org.geworkbench.util.BaseException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public ListenerEventMismatchException() {
        super();
    }

    public ListenerEventMismatchException(String message) {
        super(message);
    }

}