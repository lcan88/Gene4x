package org.geworkbench.util;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Base runtime exception class for all application exceptions.
 */
public class BaseRuntimeException extends RuntimeException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public BaseRuntimeException() {
        super();
    }

    public BaseRuntimeException(String message) {
        super(message);
    }

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}