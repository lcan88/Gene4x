package org.geworkbench.bison.datastructure.properties;


/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Allows the association of arbitrary descriptions with an oject. Concept
 * borrowed from MAGE-OM interface <code>Describable</code>
 */
public interface DSDescribable {

    /**
     * Append a new description to the list of existing descriptions.
     *
     * @param description The new description to be added.
     */
    void addDescription(String description);

    /**
     * Get all available descriptions in the form of an array of strings.
     *
     * @return An array containing all descriptions.
     */
    String[] getDescriptions();

    /**
     * Remove the designated description.
     *
     * @param description The description to be removed.
     */
    void removeDescription(String description);
}
