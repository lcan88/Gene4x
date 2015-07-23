package org.geworkbench.bison.annotation;

import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import java.util.Set;

/**
 * This intefaces allows for the association of annotations with specific objects. There are two contexts:
 * <ol>
 * <li>Generic context. allows to annotate a specific object, independent
 * of the specific project. E.g. a marker in all projects
 * <li>Project specific context. Allows to get an annotation for an object
 * in a specific project, which may be different from that in a different
 * project. E.g. the same microarray in two projects
 * </ol>
 */
public interface DSAnnotationRegistry {

    /**
     * Sets a context specific annotation.
     *
     * @param context object denoting the context.
     * @param object  the object to annotate.
     * @param label   the annotation label.
     * @param value   the annotation value.
     */
    public void setAnnotation(Object context, Object object, DSAnnotLabel label, DSAnnotValue value);

    /**
     * Sets a non-context specific annotation.
     *
     * @param object the object to annotate.
     * @param label  the annotation label.
     * @param value  the annotation value.
     */
    public void setAnnotation(Object object, DSAnnotLabel label, DSAnnotValue value);

    /**
     * Retrieves the context specific annotation IF EXISTING AND DIFFERENT from the
     * non-context specific one. Otherwise returns the non-context specific one
     *
     * @param context object denoting the context.
     * @param object  the annotated object.
     * @param label   the annotation label.
     * @return the annotation value.
     */
    public DSAnnotValue getAnnotation(Object context, Object object, DSAnnotLabel label);

    /**
     * Retrieves the non-context specific annotation.
     *
     * @param object the object to annotate.
     * @param label  the annotation label.
     * @return the annotation value.
     */
    public DSAnnotValue getAnnotation(Object object, DSAnnotLabel label);

    /**
     * Returns all the labels that an object has across all contexts.
     *
     * @param object the object for which annotations are desired.
     * @return a set of all the annotation labels for the given object.
     */
    public Set<DSAnnotLabel> getAnnotationLabels(Object object);

    /**
     * Returns all the labels that an object has in a specific context.
     *
     * @param context object denoting the context.
     * @param object  the annotated object.
     * @return the set of annotation labels for the given object and context.
     */
    public Set<DSAnnotLabel> getAnnotationLabels(Object context, Object object);

    /**
     * Returns the list of all values that have been associated with a specific label.
     *
     * @param label the annotation label.
     * @return the set of values for the given label.
     */
    public Set<DSAnnotValue> getAnnotationValues(DSAnnotLabel label);
}
