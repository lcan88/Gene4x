package org.geworkbench.engine.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John Watkinson
 */
public class Preferences implements Serializable {

    private List<Field> fields;

    public Preferences() {
        fields = new ArrayList<Field>();
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public List<Field> getFields() {
        return fields;
    }

    public Field getField(String fieldName) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(field.getName() + "=" + field.toString());
        }
        return sb.toString();
    }

    public Preferences makeCopy() {
        Preferences copy = new Preferences();
        for (int i = 0; i < fields.size(); i++) {
            Field field =  fields.get(i);
            Field newField = field.clone();
            copy.addField(newField);
        }
        return copy;
    }

}
