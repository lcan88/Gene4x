package org.geworkbench.engine.preferences;

/**
 * @author John Watkinson
 */
public class TextField extends Field {

    private String value;

    public TextField(String fieldName) {
        super(fieldName);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void copyValueFrom(Field other) {
        if (other instanceof TextField) {
            value = ((TextField) other).getValue();
        }
    }

    public void fromString(String s) {
        value = s;
    }

    public String toString() {
        return value;
    }

    public TextField clone() {
        TextField clone = new TextField(getName());
        clone.setValue(value);
        return clone;
    }
}
