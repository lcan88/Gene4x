package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.DefaultIdentifiable;

/**
 * Default implementation of FinalPanel.
 */
public class CSPanel <T extends DSNamed> extends CSItemList<T> implements DSPanel<T> {

    protected String label = "";
    protected String subLabel = "";
    protected boolean active = false;
    protected int serial = 0;

    /**
     * Used in the implementation of the <code>Identifiable</code> interface.
     */
    private org.geworkbench.bison.util.DefaultIdentifiable panelId = new DefaultIdentifiable();
    /**
     * Used in the implementation of the <code>Describable</code> interface.
     */
    private CSDescribable descriptions = new CSDescribable();
    /**
     * <code>PanelEntry</code> comprising this <code>Panel</code>
     */

    /**
     * The subPanels
     */
    private DSItemList<DSPanel<T>> subPanels = new CSItemList<DSPanel<T>>();

    /**
     * The manually selected items
     */
    protected DSPanel<T> selection;// selected markerset

    // @todo - watkin - revisit the concept of the "selection" panel
    private CSPanel(boolean selection) {
        label = "Selection";
    }

    /**
     * Constructs a new CSPanel.
     */
    public CSPanel() {
        selection = new CSPanel<T>(true);
        subPanels.add(selection);
    }

    /**
     * Constructs a new CSPanel with the specified label.
     *
     * @param label the label for this CSPanel.
     */
    public CSPanel(String label) {
        this.label = label;
        selection = new CSPanel<T>(true);
        subPanels.add(selection);
    }

    public CSPanel(String label, boolean temp) {
        this.label = label;
        //        selection = new CSPanel<T>(true);
        //        subPanels.add(selection);
    }


    /**
     * Constructs a new CSPanel with the specified label and sublabel.
     *
     * @param label    the label for this CSPanel.
     * @param subLabel the sublabel for this CSPanel.
     */
    public CSPanel(String label, String subLabel) {
        this.label = label;
        this.subLabel = subLabel;
        selection = new CSPanel<T>(true);
        subPanels.add(selection);
    }

    /**
     * Creates a CSPanel from an existing panel.
     *
     * @param panel the panel from which to create this CSPanel.
     */
    public CSPanel(DSPanel<T> panel) {
        setLabel(panel.getLabel());
        setSubLabel(panel.getSubLabel());
        addAll(panel);
        setActive(true);
    }

    @Override public int size() {
        int n = super.size();
        for (DSPanel<T> panel : subPanels) {
            if (panel.isActive()) {
                n += panel.size();
            }
        }
        return n;
    }

    public T getProperItem(int index) {
        return super.get(index);
    }

    public int getNumberOfProperItems() {
        return super.size();
    }

    /**
     * Gets the item from by index.
     *
     * @param index the index of the item
     * @return the item, or <code>null</code> if not found.
     */
    @Override public T get(int index) {
        int n = super.size();
        if (index < n) {
            return super.get(index);
        } else {
            index -= n;
            for (DSPanel<T> panel : subPanels) {
                if (panel.isActive()) {
                    int size = panel.size();
                    if (index < size) {
                        return panel.get(index);
                    } else {
                        index -= size;
                    }
                }
            }
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    @Override public int indexOf(Object item) {
        int index = super.indexOf(item);
        if (index != -1) {
            return index;
        } else {
            int offset = super.size();
            for (DSPanel<T> panel : subPanels) {
                if (panel.isActive()) {
                    index = panel.indexOf(item);
                    if (index != -1) {
                        return index + offset;
                    } else {
                        offset += panel.size();
                    }
                }
            }
            return -1;
        }
    }

    public boolean isBoundary(int index) {
        int n = super.size();
        if (index < n) {
            return false;
        } else {
            index -= n;
            for (DSPanel<T> panel : subPanels) {
                if (panel.isActive()) {
                    int size = panel.size();
                    if (index == size - 1) {
                        return true;
                    } else {
                        if (index > size) {
                            index -= size;
                        } else {
                            return false;
                        }
                    }
                }
            }
            return false;
            //            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    /**
     * Gets the label for this panel.
     *
     * @return label the label for this panel.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Clears all data from this panel and all sub-panels.
     */
    @Override public void clear() {
        super.clear();
        subPanels.clear();
    }

    /**
     * Sets the label for this panel.
     *
     * @param label the new label for this panel.
     */
    public void setLabel(String label) {
        this.label = new String(label);
    }

    /**
     * Get all sub-panels for this panel.
     *
     * @return
     */
    public DSItemList<DSPanel<T>> panels() {
        return subPanels;
    }

    /**
     * Set sub-panel by index.
     *
     * @param index the index at which to place this sub-panel.
     * @param panel the sub-panel itself.
     */
    public void setPanel(int index, DSPanel<T> panel) {
        subPanels.add(index, panel);
    }

    /**
     * Get the panel that contains the given item.
     *
     * @param item the item for which to search.
     * @return the panel in which the item resides, or <code>null</code> if the item was not found.
     */
    public DSPanel<T> getPanel(T item) {
        for (DSPanel<T> panel : subPanels) {
            if (panel.contains(item)) {
                return panel;
            }
        }
        return null;
    }

    /**
     * Gets the special 'selection' panel.
     *
     * @return the selection panel.
     */
    public DSPanel<T> getSelection() {
        return selection;
    }

    /**
     * Gets the sub-label for this panel.
     *
     * @return the sub-label for this panel.
     */
    public String getSubLabel() {
        return subLabel;
    }

    /**
     * Sets the sub-label for this panel.
     *
     * @param label the new sub-label.
     */
    public void setSubLabel(String label) {
        subLabel = new String(label);
    }

    /**
     * Sets this <code>Panel</code> to be 'Active' or 'Inactive'.
     *
     * @param flag activation state of this panel: <code>true</code> for 'Active', <code>false</code> for 'Inactive'.
     */
    public void setActive(boolean flag) {
        active = flag;
    }

    /**
     * Obtains the activation state of this <code>Panel</code>
     *
     * @return activation state either <code>true</code> for 'Active',
     *         <code>false</code> for 'Inactive'.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the <code>String</code> representation of this <code>Panel</code>.
     *
     * @return <code>String</code> representation of this <code>Panel</code>.
     */
    public String toString() {
        return label + " [" + size() + "]";
    }

    /**
     * Two panels are equal if their labels are identical.
     *
     * @param o object with which to compare.
     * @return <code>true</code> if the object is a {@link DSPanel} and its label is the same as this panel's label.
     */
    public boolean equals(Object o) {
        if (o instanceof DSPanel && this.getLabel() != null && this.getSubLabel() != null) {
            return (this.getLabel().equalsIgnoreCase(((DSPanel) o).getLabel()) && this.getSubLabel().equalsIgnoreCase(((DSPanel) o).getSubLabel()));
        }
        return false;
    }

    @Override public int hashCode() {
        return getLabel().hashCode();
    }

    /**
     * Returns the ID for this panel.
     *
     * @return the ID of the panel.
     */
    public String getID() {
        return panelId.getID();
    }

    /**
     * Sets the ID of this panel
     *
     * @param id the new ID.
     */
    public void setID(String id) {
        panelId.setID(id, "PanelImpl");
    }

    /**
     * Adds a description to this panel.
     *
     * @param description the new description to add.
     */
    public void addDescription(String description) {
        descriptions.addDescription(description);
    }

    /**
     * Gets all descriptions for the panel.
     *
     * @return the array of descriptions.
     */
    public String[] getDescriptions() {
        return descriptions.getDescriptions();
    }

    /**
     * Removes a description from the panel.
     *
     * @param description the full text of the description to remove.
     */
    public void removeDescription(String description) {
        descriptions.removeDescription(description);
    }

    /**
     * Gets the index of this object in its ordered container.
     *
     * @return the serial (or index) of this object.
     */
    public int getSerial() {
        return serial;
    }

    /**
     * Sets the index of this object in its ordered container.
     *
     * @param i the new serial (or index).
     */
    public void setSerial(int i) {
        serial = i;
    }

    /**
     * Gets all the active sub-panels of the panel.
     *
     * @return the active panels contained by this panel.
     */
    public DSPanel<T> activeSubset() {
        CSPanel<T> activePanels = new CSPanel<T>(getLabel());
        // Include selection if it is activated
        if (selection.isActive()) {
            activePanels.selection = selection;
        }
        activePanels.setActive(true);
        int i = 0;
        for (DSPanel panel : panels()) {
            if (panel.isActive())
                activePanels.setPanel(i++, panel);
        }
        return activePanels;
    }

    public void renameSubPanel(DSPanel<T> child, String newName) {
        subPanels.rename(child, newName);
    }
}
