package org.geworkbench.engine.config.events;

import org.geworkbench.util.BaseException;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Thrown when a <code>Class</code> object expected to implement an
 * <code>AppEventListener</code> interface, turns out not to.
 */
public class AppEventListenerException extends BaseException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public AppEventListenerException() {
        super();
    }

    public AppEventListenerException(String message) {
        super(message);
    }

}