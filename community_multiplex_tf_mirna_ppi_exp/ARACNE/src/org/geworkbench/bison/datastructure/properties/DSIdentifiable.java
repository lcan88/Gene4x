package org.geworkbench.bison.datastructure.properties;

/**
 * Allows the association of an ID with an object.
 */
public interface DSIdentifiable {
    /**
     * Return the ID of the implementing object.
     *
     * @return
     */
    String getID();

    /**
     * Sets the ID of the implementing object.
     *
     * @param id
     */
    void setID(String id);
}
