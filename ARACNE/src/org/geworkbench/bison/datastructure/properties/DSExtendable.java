package org.geworkbench.bison.datastructure.properties;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * The concept behind interface <code>DSExtendable</code> is borrowed directly
 * from the definition of the <code>Extendable</code> interface in MAGE-OM.
 * It provides a generic mechanism for attaching arbitrary infromation
 * (in the form of name-value pairs) to an implementing class. Since the
 * semanitcs of such information is use-specific, scant use of this
 * mechanism is advisable.
 *
 * @author First Genetic Trust Inc.
 * @version 1.0
 */
public interface DSExtendable {

    /**
     * Adds a new name-value mapping.
     *
     * @param name
     * @param value
     */
    void addNameValuePair(String name, Object value);

    /**
     * Returns all values mapped to the designated name.
     *
     * @param name
     * @return All values associated with the argument <code>name</code>.
     */
    Object[] getValuesForName(String name);

    /**
     * Forces that the <code>name</code> be associated with at most one value.
     * Attempting to add a name-value pair for a 'name' that already has an
     * associate value, results in old name-value association be replaced by the
     * new one.
     * <p/>
     * Calling this method for an existing 'name' results in all the associated
     * name-value pairs to be cleared.
     *
     * @param name The 'name' upon which uniqueness of value will be enforced.
     */
    void forceUniqueValue(String name);

    /**
     * Conjugate method for <code>forceUniqueValue</code>. Enables a 'name' to
     * accept mulptiple name-value asscociations.
     *
     * @param name The 'name' for which multiplicity of value is enabled.
     */
    void allowMultipleValues(String name);

    /**
     * Check if <code>forceUniqueValue()</code> is in effect for the designated
     * name.
     *
     * @param name
     * @return
     */
    boolean isUniqueValue(String name);

    /**
     * Removes all values associated with the given <code>name</code>.
     *
     * @param name
     */
    void clearName(String name);
}
