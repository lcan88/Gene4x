package org.geworkbench.bison.datastructure.bioobjects;

/**
 * Implementing classes have a notion of an object and an associated 'confidence' value (p-value) for that object.
 */
public interface DSPValued {

    /**
     * Get the p-value for this object.
     *
     * @return the p-value (between 0 and 1).
     */
    public double getPValue();

    public void setPValue(double value);

}
