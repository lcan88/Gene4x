package org.geworkbench.bison.datastructure.properties;

import java.io.Serializable;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Baseline implementation of interface <code>Extendable</code>.
 *
 * @author First Genetic Trust Inc.
 * @version 1.0
 */
public class CSExtendable implements DSExtendable, Serializable {
    /**
     * Every entry in this vector is an object of type <code>Name<code> and
     * contains all values associated with a given name.
     */
    Vector nameValuePairs = new Vector();

    public void addNameValuePair(String name, Object value) {
        if (name == null || value == null)
            return;
        Name nameValueList = null;
        if (!nameValuePairs.contains((nameValueList = new Name(name))))
            nameValuePairs.add(nameValueList);
        nameValueList = (Name) nameValuePairs.get(nameValuePairs.indexOf(new Name(name)));
        nameValueList.add(value);
    }

    public Object[] getValuesForName(String name) {
        if (name == null)
            return null;
        int index;
        // Check if the given name is there.
        if ((index = nameValuePairs.indexOf(new Name(name))) < 0)
            return null;
        else {
            Name nameValueList = (Name) nameValuePairs.get(index);
            if (nameValueList.values.size() > 0)
                return nameValueList.values.toArray();
            else
                return null;
        }

    }

    public void forceUniqueValue(String name) {
        int index;
        if (name != null)
            if ((index = nameValuePairs.indexOf(new Name(name))) >= 0) {
                Name nameValueList = (Name) nameValuePairs.get(index);
                nameValueList.uniqueValue = true;
                nameValueList.clearValues();
            }

    }

    public void allowMultipleValues(String name) {
        int index;
        if (name != null)
            if ((index = nameValuePairs.indexOf(new Name(name))) >= 0) {
                Name nameValueList = (Name) nameValuePairs.get(index);
                nameValueList.uniqueValue = false;
            }

    }

    public boolean isUniqueValue(String name) {
        int index;
        if (name != null)
            if ((index = nameValuePairs.indexOf(new Name(name))) >= 0) {
                Name nameValueList = (Name) nameValuePairs.get(index);
                return nameValueList.uniqueValue;
            }

        return false;
    }

    public void clearName(String name) {
        int index;
        if (name != null)
            if ((index = nameValuePairs.indexOf(new Name(name))) >= 0) {
                Name nameValueList = (Name) nameValuePairs.get(index);
                nameValueList.clearValues();
            }

    }

    /**
     * <p>Copyright: Copyright (c) 2003</p>
     * <p>Company: First Genetic Trust, Inc.</p>
     * <p/>
     * Structure used to capture the name-value pairs defined for a given 'name'.
     *
     * @author First Genetic Trust, Inc.
     * @version 1.0
     */
    class Name implements Serializable {
        String name = null;
        boolean uniqueValue = false;
        Vector values = null;

        Name(String n) {
            name = n;
        }

        public boolean equals(Object n) {
            if (n == null || name == null)
                return false;
            if (Name.class.isAssignableFrom(n.getClass()))
                return this.name.equals(((Name) n).name);
            else if (String.class.isAssignableFrom(n.getClass()))
                return this.name.equals((String) n);
            else
                return false;
        }

        public boolean equals(String n) {
            if (n == null || name == null)
                return false;
            else
                return this.name.equals(n);
        }

        /**
         * Add the designated value <code>v</code> at this name-value pair list,
         * respecting any uniqueness requirements set forth by the instance variable
         * <code>uniqueValue</code>.
         *
         * @param v The value to add.
         */
        public void add(Object v) {
            if (values == null)
                values = new Vector();
            if (uniqueValue)
                values.clear();
            values.add(v);
        }

        /**
         * Clear all values associated with the name represented by this object.
         */
        public void clearValues() {
            if (values != null)
                values.clear();
        }

    }

}
