package org.geworkbench.bison.datastructure.bioobjects.sequence;

import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.datastructure.properties.CSExtendable;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.regex.Matcher;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSSequence implements DSSequence, Serializable {

    private final static ObjectStreamField[] serialPersistentFields = 
    {
        new ObjectStreamField("isDNA", boolean.class), 
        new ObjectStreamField("label", String.class), 
        new ObjectStreamField("sequence", String.class),
        new ObjectStreamField("descriptions", CSDescribable.class),
        new ObjectStreamField("extend", CSExtendable.class),
        new ObjectStreamField("id", String.class),
        new ObjectStreamField("serial", int.class),
        new ObjectStreamField("isEnabled", boolean.class)
    };

    static final String[] repeats = {"(at){5,}", "a{7,}", "c{7,}", "g{7,}", "t{7,}"};
    static final java.util.regex.Pattern[] repeatPattern = new java.util.regex.Pattern[repeats.length];
    static java.util.regex.Pattern dnaPattern = java.util.regex.Pattern.compile("[^#acgtnxACGTNX]");

    private String id = "";
    private int serial = -1;
    private boolean isEnabled = true;
    private String sequence = new String();
    private String label = new String();
    private boolean isDNA = true;
    /**
     * Used in the implementation of the <code>Extendable</code> interface.
     */
    protected CSExtendable extend = new CSExtendable();
    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    protected CSDescribable descriptions = new CSDescribable();

    public CSSequence() {
    }

    public CSSequence(String l, String s) {
        setSequence(s);
        label = l;
    }

    public String getLabel() {
        return label;
    }

    public String getSequence() {
        return sequence;
    }

    public CSSequence getSubSequence(int from, int to){
        if (from >= 0 && to >= 0 && from < sequence.length() && to < sequence.length())
            return new CSSequence(getLabel(), getSequence().substring(from, to));
        return null;
    }
    
    public void setLabel(String l) {
        label = l;
    }

    public void setSequence(String s) {
        sequence = s;
        Matcher m = dnaPattern.matcher(sequence);
        if (m.find()) {
            isDNA = false;
        }
    }

    public String toString() {
        return label;
    }

    public int length() {
        return sequence.length();
    }

    public boolean isDNA() {
        return isDNA;
    }

    public void maskRepeats() {
        for (int i = 0; i < repeats.length; i++) {
            if (repeatPattern[i] == null) {
                repeatPattern[i] = java.util.regex.Pattern.compile(repeats[i]);
            }
            Matcher m = repeatPattern[i].matcher(sequence);
            sequence = m.replaceAll("#########");
        }
    }

    public void setID(String _id) {
        id = _id;
    }

    public String getID() {
        return id;
    }

    public void setSerial(int _serial) {
        serial = _serial;
    }

    public int getSerial() {
        return serial;
    }

    public boolean enabled() {
        return isEnabled;
    }

    public void enable(boolean status) {
        isEnabled = status;
    }

    public void clearName(String name) {
        extend.clearName(name);
    }

    public void forceUniqueValue(String name) {
        extend.forceUniqueValue(name);
    }

    public void allowMultipleValues(String name) {
        extend.allowMultipleValues(name);
    }

    public boolean isUniqueValue(String name) {
        return extend.isUniqueValue(name);
    }

    public void addDescription(String desc) {
        descriptions.addDescription(desc);
    }

    public String[] getDescriptions() {
        return descriptions.getDescriptions();
    }

    public void removeDescription(String desc) {
        descriptions.removeDescription(desc);
    }

    public void addNameValuePair(String name, Object value) {
        extend.addNameValuePair(name, value);
    }

    public Object[] getValuesForName(String name) {
        return extend.getValuesForName(name);
    }

    public void shuffle() {
        char[] tokens = new char[sequence.length()];
        for (int i = 0; i < sequence.length(); i++) {
            tokens[i] = sequence.charAt((int) (Math.random() * sequence.length()));
        }
        sequence = new String(tokens);
    }
}
