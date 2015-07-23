package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import java.util.Map;

public interface DSCriteria <T extends DSNamed> extends Map<DSAnnotLabel, DSPanel<T>> {
    /**
     * Adds an item to the criteria by label/value.
     *
     * @param item  the item to add.
     * @param label the criterion label.
     * @param value the criterion value.
     * @todo - watkin - the concepts used here are confusing (criterion, label, value) It's not clear what is being done.
     */
    public void addItem(T item, DSAnnotLabel label, DSAnnotValue value);

    /**
     * Gets the currently selected criterion.
     *
     * @return the seelcted criterion.
     */
    public DSPanel<T> getSelectedCriterion();

    /**
     * Sets the selected criterion to the label label.
     *
     * @param label the label for the criterion.
     * @return the criterion.
     */
    public DSPanel<T> setSelectedCriterion(DSAnnotLabel label);

    /**
     * Gets the panel that contains the given item.
     *
     * @param item the item for which to search.
     * @return the panel containing the item.
     * @todo - watkin - the name of this method is confusing.
     */
    public DSPanel<T> getValue(T item);
    /**
     * We should add a method to retrieve all the objects that
     * have the specified criterion
     */
}
