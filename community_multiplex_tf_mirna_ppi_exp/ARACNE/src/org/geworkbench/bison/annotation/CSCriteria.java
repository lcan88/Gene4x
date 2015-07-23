package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import java.util.HashMap;

/**
 * Maintains a criteria selection.
 */
public class CSCriteria <T extends DSNamed> extends HashMap<DSAnnotLabel, DSPanel<T>> implements DSCriteria<T> {

    protected DSPanel<T> selectedCriterion = null;

    /**
     * Creates a new criteria.
     */
    public CSCriteria() {
    }

    /**
     * Gets the currently selected criterion.
     *
     * @return the seelcted criterion.
     */
    public DSPanel<T> getSelectedCriterion() {
        return selectedCriterion;
    }

    /**
     * Sets the selected criterion to the label label.
     *
     * @param label the label for the criterion.
     * @return the criterion.
     */
    public DSPanel<T> setSelectedCriterion(DSAnnotLabel label) {
        selectedCriterion = this.get(label);
        return selectedCriterion;
    }

    /**
     * Gets the panel that contains the given item.
     *
     * @param item the item for which to search.
     * @return the panel containing the item.
     * @todo - watkin - the name of this method is confusing.
     */
    public DSPanel<T> getValue(T item) {
        if (selectedCriterion != null) {
            DSPanel<T> value = selectedCriterion.getPanel(item);
            return value;
        }
        return null;
    }

    /**
     * Adds an item to the criteria by label/value.
     *
     * @param item  the item to add.
     * @param label the criterion label.
     * @param value the criterion value.
     */
    public void addItem(T item, DSAnnotLabel label, DSAnnotValue value) {
        DSPanel<T> criterion = get(label);
        if (criterion != null) {
            // Find if this item was previously contained in another panel
            DSPanel<T> previousPanel = criterion.getPanel(item);
            if (previousPanel != null) {
                // In that case, remove the object
                previousPanel.remove(item);
            }
            // Now add it to the correct panel
            DSPanel<T> criterionValue = criterion.panels().get(value.toString());
            if (criterionValue == null) {
                // Create the final panel if it does not exist
                criterionValue = new CSPanel<T>(value.toString(), label.toString());
                criterion.panels().add(criterionValue);
            }
            criterionValue.add(item);
        }
    }
}
