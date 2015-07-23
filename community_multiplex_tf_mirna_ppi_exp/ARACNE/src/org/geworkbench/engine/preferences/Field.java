package org.geworkbench.engine.preferences;

import java.io.Serializable;

/**
 * @author John Watkinson
 */
public abstract class Field implements Serializable, Cloneable {

    private String name;

    protected Field(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void fromString(String s) throws ValidationException;

    public abstract void copyValueFrom(Field other);

    protected Field clone() {
        try {
            return (Field) super.clone();
        } catch (CloneNotSupportedException e) {
            // Clone supported.
        }
        // Unreachable
        return null;
    }
}
