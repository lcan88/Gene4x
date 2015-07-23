package org.geworkbench.util;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Base exception class for all application exceptions.
 */
public class BaseException extends Exception {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

}