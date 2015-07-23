package org.geworkbench.bison.util;

import java.io.Serializable;

/**
 * An implementation of {@link DSAnnotLabel} that is based on an arbitrary object.
 */
public class CSAnnotLabel implements DSAnnotLabel, Serializable {

    private Object object;

    /**
     * Creates a new annotation label.
     *
     * @param object the annotation label object.
     */
    public CSAnnotLabel(Object object) {
        this.object = object;
    }

    @Override public boolean equals(Object o) {
        if (o instanceof CSAnnotLabel) {
            boolean foo = object.equals(((CSAnnotLabel) o).object);
            return foo;
        } else {
            return super.equals(o);
        }
    }

    @Override public int hashCode() {
        return object.hashCode();
    }

    @Override public String toString() {
        return object.toString();
    }
}
