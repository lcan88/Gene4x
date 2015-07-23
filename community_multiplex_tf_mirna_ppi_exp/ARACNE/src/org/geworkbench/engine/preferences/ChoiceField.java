package org.geworkbench.engine.preferences;

/**
 * @author John Watkinson
 */
public class ChoiceField extends Field {

    String[] allowedValues;
    int selection;

    public ChoiceField(String fieldName, String[] allowedValues) {
        super(fieldName);
        this.allowedValues = allowedValues;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public String getSelectionAsString() {
        return allowedValues[selection];
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }

    public void copyValueFrom(Field other) {
        if (other instanceof ChoiceField) {
            int index = ((ChoiceField) other).getSelection();
            if ((0 <= index) && (index < allowedValues.length)) {
                selection = index;
            }
        }
    }

    public void fromString(String s) throws ValidationException {
        for (int i = 0; i < allowedValues.length; i++) {
            String allowedValue = allowedValues[i];
            if (allowedValue.equals(s)) {
                selection = i;
                return;
            }
        }
        throw new ValidationException("Not a valid choice: '" + s + "'.");
    }

    public String toString() {
        return getSelectionAsString();
    }

    public ChoiceField clone() {
        ChoiceField clone = new ChoiceField(getName(), allowedValues);
        clone.setSelection(selection);
        return clone;
    }

}
