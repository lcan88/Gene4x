package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSDescribable;
import org.geworkbench.bison.datastructure.properties.DSIdentifiable;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.datastructure.properties.DSSequential;

import java.io.Serializable;

/**
 * Implementing classes are activatable/deactivatable item lists.
 */
public interface DSPanel <T extends DSNamed> extends DSItemList<T>, DSNamed, DSIdentifiable, DSDescribable, Serializable, DSSequential {
    /**
     * Gets the sub-label for this DSPanel.
     *
     * @return the sub-label.
     */
    String getSubLabel();

    /**
     * Sets the sub-label for this DSPanel.
     *
     * @param label the new sub-label.
     */
    void setSubLabel(String label);

    /**
     * returns a string representation of the panel
     *
     * @return a string representation of the panel
     */
    public String toString();

    /**
     * Active panels are used by the visual displays. Inactive ones are ignored
     * Sets the active state of this panel
     */
    public void setActive(boolean flag);

    /**
     * Active panels are used by the visual displays. Inactive ones are ignored
     *
     * @return the active state of this panel
     */
    public boolean isActive();

    /**
     * Gets all the sub-panels of the panel.
     *
     * @return the panels contained by this panel.
     */
    public DSItemList<DSPanel<T>> panels();

    /**
     * Gets all the active sub-panels of the panel.
     *
     * @return the active panels contained by this panel.
     */
    public DSPanel<T> activeSubset();

    /**
     * Gets the sub-panel that contains the given item.
     *
     * @param item the item to search for in the sub-panels.
     * @return the containing sub-panel, or <code>null</code> if the item was not found.
     */
    public DSPanel<T> getPanel(T item);

    /**
     * Gets the selected sub-panel.
     *
     * @return the selected sub-panel, or <code>null</code> if no panel is selected.
     */
    public DSPanel<T> getSelection();

    /**
     * Used to determine if the current index is a sub-panel boundary
     *
     * @param index int
     * @return boolean
     */
    public boolean isBoundary(int index);

    /**
     * Gets a <i>proper</i> item by index. A proper item is an item that belongs to this
     * panel but not to its subpanels.
     */
    public T getProperItem(int index);

    /**
     * Gets the number of <i>proper</i> items in this panel. A proper item is an item that belongs to this
     * panel but not to its subpanels.
     */
    public int getNumberOfProperItems();

    public void renameSubPanel(DSPanel<T> child, String newName);

}
