package org.geworkbench.engine.parsers;

/**
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * <p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Thrown when attempting to open an input file that does not comply with
 * a user-designated format.
 */
public class InputFileFormatException extends org.geworkbench.util.BaseException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public InputFileFormatException() {
        super();
    }

    public InputFileFormatException(String message) {
        super(message);
    }

}