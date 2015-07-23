package org.geworkbench.engine.config.rules;

import org.geworkbench.util.BaseException;

/**
 *
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
public class NotEventSourceException extends BaseException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public NotEventSourceException() {
        super();
    }

    public NotEventSourceException(String message) {
        super(message);
    }

}