package org.geworkbench.engine.preferences;

/**
 * @author John Watkinson
 */
public class DoubleField extends Field {

    private double value;

    public DoubleField(String fieldName) {
        super(fieldName);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void copyValueFrom(Field other) {
        if (other instanceof DoubleField) {
            value = ((DoubleField) other).getValue();
        }
    }

    public void fromString(String s) throws ValidationException {
        try {
            value = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Number required.", nfe);
        }
    }

    public String toString() {
        return "" + value;
    }

    public DoubleField clone() {
        DoubleField clone = new DoubleField(getName());
        clone.setValue(value);
        return clone;
    }

}
