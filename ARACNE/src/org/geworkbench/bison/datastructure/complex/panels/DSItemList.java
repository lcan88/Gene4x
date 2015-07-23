package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSIdentifiable;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.List;

/**
 * Specifies a list of {@link DSNamed} objects, accessible by label.
 */
public interface DSItemList <T extends DSNamed> extends List<T>, DSIdentifiable {
    /**
     * Gets an item by label.
     *
     * @param label the label of the requested object.
     * @return the requested object, or <code>null</code> if it was not found.
     */
    T get(String label);

    /**
     * Gets an item by item template.
     *
     * @param item the item to fetch. Only the {@link org.geworkbench.bison.datastructure.properties.DSNamed#getLabel()}
     *             method will be used.
     * @return the requested object, or <code>null</code> if it was not found.
     */
    T get(T item);

    /**
     * Renames the given named item. Map-based item lists can update their mapping.
     *
     * @param item  item to be renamed (but has not yet been renamed). Must be a member of the list.
     * @param label new name for item.
     */
    void rename(T item, String label);
}
