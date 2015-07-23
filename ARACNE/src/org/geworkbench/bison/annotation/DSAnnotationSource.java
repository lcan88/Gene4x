package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * Defines the contract for a source of annotation data for items.
 *
 * @author John Watkinson
 */
public interface DSAnnotationSource<T extends DSNamed> {

    /**
     * Retrieves the annotation for an item.
     * @param item the item for which to retrieve the annotation.
     * @param annotationType the annotation type.
     * @return the value for the given annotation type for that item.
     */
    public <Q> Q getAnnotationForItem(T item, DSAnnotationType<Q> annotationType);

}
