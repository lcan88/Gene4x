package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * This defines the contract for a manager of contexts for the annotation, labelling and classification of the items
 * of a {@link DSItemList}.
 * <p>
 * For each item list, there can be one or more {@link DSAnnotationContext DSAnnotationContexts}. Additionally, there
 * is a <i>default</i> context associated with each data set.
 *
 * @author John Watkinson
 */
public interface DSAnnotationContextManager {

    /**
     * Retrieves an array of all contexts for the given item list.
     * @param itemList the item list for which to retrieve contexts.
     * @return an array of contexts.
     */
    public <T extends DSNamed> DSAnnotationContext<T>[] getAllContexts(DSItemList<T> itemList);

    /**
     * Gets a context by name for the given item list.
     * @param itemList the item list.
     * @param name the name of the context.
     * @return the context with the given name for the given item list, or null if it does not exist.
     */
    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, String name);

    /**
     * Checks for the existence of a named context for an item list.
     * @param itemList the item list to search.
     * @param name the name to search.
     * @return <tt>true</tt> if the specified context exists, <tt>false</tt> otherwise.
     */
    public boolean hasContext(DSItemList itemList, String name);

    /**
     * Creates a new context, replacing an existing context if one exists for the same item list and name.
     * @param itemList the item list against which the new context should be created.
     * @param name the name of the context to create.
     * @return the newly-created context.
     */
    public <T extends DSNamed> DSAnnotationContext<T> createContext(DSItemList<T> itemList, String name);

    /**
     * Gets the count of contexts for the given item list.
     * @param itemList the item list for which to count contexts.
     * @return the number of contexts for the item list.
     */
    public <T extends DSNamed> int getNumberOfContexts(DSItemList<T> itemList);

    /**
     * Retrieves a context by index.
     * @param itemList the item list for which to retrieve the context.
     * @param index the index in to the list of contexts.
     * @return the context at the specified index for the specified item list.
     */
    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, int index);

    /**
     * Removes a context by item list and name.
     * @param itemList the item list for which to remove a context.
     * @param name the name of the context to remove.
     * @return <tt>true</tt> if the context existed before the removal, false otherwise.
     */
    public boolean removeContext(DSItemList itemList, String name);

    /**
     * Renames the specified context.
     * @param itemList the item list for which to rename a context.
     * @param oldName the old name of the context.
     * @param newName the new name of the context.
     * @throws IllegalArgumentException if a context already exists for the specified item list and new name.
     * @return <tt>true</tt> if a context existed by the old name, <tt>false</tt> if no such context existed.
     */
    public boolean renameContext(DSItemList itemList, String oldName, String newName);

    /**
     * Retrieves the currently-selected context for the given item list.
     * @param itemList the item list for which to select the current context.
     * @return the currently-selected context. If no context is explicitly selected, a <i>default</i> context is
     * returned. This method never returns <tt>null</tt>.
     */
    public <T extends DSNamed> DSAnnotationContext<T> getCurrentContext(DSItemList<T> itemList);

    /**
     * Sets the currently-selected context.
     * @param itemList the item list for which to set the current context.
     * @param context the context to select.
     */
    public <T extends DSNamed> void setCurrentContext(DSItemList<T> itemList, DSAnnotationContext<T> context);

    /**
     * Copies all context from one item list to another. The results are undefined if the target item list does not
     * contain all the elements of the source item list. However, the order of the elements need not be the same
     * between the two lists.
     * @param from the source item list.
     * @param to the target item list.
     */
    public <T extends DSNamed> void copyContexts(DSItemList<T> from, DSItemList<T> to);
}
