package org.geworkbench.engine.management;


/**
 * @author John Watkinson
 */
public class EventException extends RuntimeException {
    public EventException() {
        super();
    }

    public EventException(String s) {
        super(s);
    }

    public EventException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventException(Throwable throwable) {
        super(throwable);
    }
}
