package org.geworkbench.bison.annotation;

/**
 * Defines the contract for an annotation type. It consists of an object that serves as a label and a class that
 * indicates the type of the associated values. For example, an annotation type for <i>blood pressure</i> might have
 * the label be the {@link String} "Blood Pressure" and the type be {@link Double java.lang.Double}.
 *
 * @author John Watkinson
 */
public interface DSAnnotationType<T> {

    /**
     * Retrieves the label (name) for this annotation type.
     */
    public String getLabel();

    /**
     * Retrieves the type of the annotation type's associated values.
     */
    public Class<T> getType();
    
}
