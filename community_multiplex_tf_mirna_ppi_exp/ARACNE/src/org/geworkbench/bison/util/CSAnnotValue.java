package org.geworkbench.bison.util;

/**
 * An implementation of {@link DSAnnotValue} that is based on an arbitrary object and a corresponding key.
 */
public class CSAnnotValue implements DSAnnotValue {

    private int key;
    private Object object;

    /**
     * Constructs a new CSAnnotValue.
     *
     * @param object the annotation object.
     * @param key    a key corresponding ot the annotation object.
     */
    public CSAnnotValue(Object object, int key) {
        this.object = object;
        this.key = key;
    }

    /**
     * @todo - watkin - This is very unconventional. A CSAnnotValue can be 'equals' to an Integer? Also, this is not consistent with hashCode().
     */
    @Override public boolean equals(Object o) {
        if (o instanceof Integer) {
            return (key == ((Integer) o).intValue());
        } else if (o instanceof CSAnnotValue) {
            return (object.equals(((CSAnnotValue) o).object));
        }
        return super.equals(o);
    }

    @Override public int hashCode() {
        return key;
    }

    @Override public String toString() {
        return object.toString();
    }
}
