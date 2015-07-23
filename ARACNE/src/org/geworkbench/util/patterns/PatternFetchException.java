package org.geworkbench.util.patterns;


/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: This exception is thrown when a pattern can't be fetched
 * from a pattern source. See class medusa.components.patternSource;</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PatternFetchException extends RuntimeException {

    public PatternFetchException() {
    }

    public PatternFetchException(String message) {
        super(message);
    }

    public PatternFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PatternFetchException(Throwable cause) {
        super(cause);
    }
}
