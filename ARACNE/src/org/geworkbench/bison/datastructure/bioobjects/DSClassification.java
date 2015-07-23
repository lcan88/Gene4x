package org.geworkbench.bison.datastructure.bioobjects;

import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * Implementing classifications have the notion of a classification and its associated confidence value (p-value).
 */
public interface DSClassification<T extends DSNamed> extends DSPValued {

    /**
     * Gets the classification of this object.
     *
     * @return the classification.
     */
    String getClassification();

    /**
     * Sets the classification of this object.
     *
     * @param classification the new classification.
     */
    void setClassification(String classification);

    /**
     * Gets the object that was classified.
     */
    T getClassifiedObject();

}

