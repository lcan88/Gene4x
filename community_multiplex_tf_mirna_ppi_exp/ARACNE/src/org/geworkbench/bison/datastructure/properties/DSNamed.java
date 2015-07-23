package org.geworkbench.bison.datastructure.properties;

/**
 * Specifies an object that has a mutable name or <b>label</b>.
 */
public interface DSNamed {
    /**
     * Gets the label for this object.
     *
     * @return the label.
     */
    String getLabel();

    /**
     * Sets the label for this object.
     *
     * @param label the new label for this object.
     */
    void setLabel(String label);
}
