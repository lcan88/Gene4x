package org.geworkbench.bison.annotation;

import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A straightforward implementation of {@link DSAnnotationRegistry}.
 */
public class CSAnnotationRegistry implements DSAnnotationRegistry {
    /**
     * Structure
     * context is a hashmap that contains a set of hashmaps (contextObjects). One for each context
     * contextObjects are hashmaps that map an object to a hashmap (labelValues).
     * In the labelValues, each label corresponds to a value
     */
    protected HashMap<Object, HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>>> contexts = new HashMap<Object, HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>>>();
    protected HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>> objects = new HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>>();
    protected HashMap<DSAnnotLabel, HashSet<DSAnnotValue>> values = new HashMap<DSAnnotLabel, HashSet<DSAnnotValue>>();

    /**
     * See DSAnnotationRegistry for a description of the methods
     */
    public CSAnnotationRegistry() {

    }

    // Unused
    private void storeLabelValue(DSAnnotLabel label, DSAnnotValue value) {
        // Add the value to the set for the corresponding label
        HashSet<DSAnnotValue> labelValues = values.get(label);
        if (labelValues == null) {
            labelValues = new HashSet<DSAnnotValue>();
            labelValues.add(value);
            values.put(label, labelValues);
        } else {
            labelValues.add(value);
        }
    }

    /**
     * Sets a context specific annotation.
     *
     * @param context object denoting the context.
     * @param object  the object to annotate.
     * @param label   the annotation label.
     * @param value   the annotation value.
     */
    public void setAnnotation(Object context, Object object, DSAnnotLabel label, DSAnnotValue value) {
        // Add the value to the set for the corresponding label
        values.get(label).add(value);
        // Check if the current context exists
        HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>> contextObjects = contexts.get(context);
        if (contextObjects == null) {
            // create the label-value table
            HashMap<DSAnnotLabel, DSAnnotValue> labelValues = new HashMap<DSAnnotLabel, DSAnnotValue>();
            labelValues.put(label, value);
            // create the object context
            contextObjects = new HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>>();
            contextObjects.put(object, labelValues);
            // add the object context to the context
            contexts.put(context, contextObjects);
        } else {
            // retrieve the label-value table
            HashMap<DSAnnotLabel, DSAnnotValue> labelValues = contextObjects.get(object);
            if (labelValues == null) {
                // create the label-value
                labelValues = new HashMap<DSAnnotLabel, DSAnnotValue>();
                labelValues.put(label, value);
                // add it to the contexts
                contextObjects.put(context, labelValues);
            } else {
                labelValues.put(label, value);
            }
        }
    }

    /**
     * Sets a non-context specific annotation.
     *
     * @param object the object to annotate.
     * @param label  the annotation label.
     * @param value  the annotation value.
     */
    public void setAnnotation(Object object, DSAnnotLabel label, DSAnnotValue value) {
        // Add the value to the set for the corresponding label
        values.get(label).add(value);
        // Check if the label-value table exists
        HashMap<DSAnnotLabel, DSAnnotValue> labelValues = objects.get(object);
        if (labelValues == null) {
            labelValues = new HashMap<DSAnnotLabel, DSAnnotValue>();
            labelValues.put(label, value);
            objects.put(object, labelValues);
        } else {
            labelValues.put(label, value);
        }
    }

    /**
     * Retrieves the context specific annotation IF EXISTING AND DIFFERENT from the
     * non-context specific one. Otherwise returns the non-context specific one
     *
     * @param context object denoting the context.
     * @param object  the annotated object.
     * @param label   the annotation label.
     * @return the annotation value.
     */
    public DSAnnotValue getAnnotation(Object context, Object object, DSAnnotLabel label) {
        HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>> contextObjects = contexts.get(context);
        if (contextObjects != null) {
            HashMap<DSAnnotLabel, DSAnnotValue> labelValues = contextObjects.get(object);
            if (labelValues != null) {
                DSAnnotValue value = labelValues.get(label);
                return value;
            }
        }
        return getAnnotation(object, label);
    }

    /**
     * Retrieves the non-context specific annotation.
     *
     * @param object the object to annotate.
     * @param label  the annotation label.
     * @return the annotation value.
     */
    public DSAnnotValue getAnnotation(Object object, DSAnnotLabel label) {
        HashMap<DSAnnotLabel, DSAnnotValue> labelValues = objects.get(object);
        if (labelValues != null) {
            DSAnnotValue value = labelValues.get(label);
            return value;
        }
        return null;
    }

    /**
     * Returns all the labels that an object has across all contexts.
     *
     * @param object the object for which annotations are desired.
     * @return a set of all the annotation labels for the given object.
     */
    public Set<DSAnnotLabel> getAnnotationLabels(Object object) {
        return objects.get(object).keySet();
    }

    /**
     * Returns all the labels that an object has in a specific context.
     *
     * @param context object denoting the context.
     * @param object  the annotated object.
     * @return the set of annotation labels for the given object and context.
     */
    public Set<DSAnnotLabel> getAnnotationLabels(Object context, Object object) {
        HashMap<Object, HashMap<DSAnnotLabel, DSAnnotValue>> contextObjects = contexts.get(context);
        if (contextObjects != null) {
            HashMap<DSAnnotLabel, DSAnnotValue> labelValues = contextObjects.get(object);
            if (labelValues != null) {
                Set keys = labelValues.keySet();
                return keys;
            }
        }
        return null;
    }

    /**
     * Returns the list of all values that have been associated with a specific label.
     *
     * @param label the annotation label.
     * @return the set of values for the given label.
     */
    public Set<DSAnnotValue> getAnnotationValues(DSAnnotLabel label) {
        return values.get(label);
    }
}
