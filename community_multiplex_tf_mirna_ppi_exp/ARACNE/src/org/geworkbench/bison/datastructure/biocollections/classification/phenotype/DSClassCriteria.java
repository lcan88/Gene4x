package org.geworkbench.bison.datastructure.biocollections.classification.phenotype;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import javax.swing.*;

/**
 * This keeps track of the <i>class</i> of panels and its members. The most common classes are Case and Control.
 */
public interface DSClassCriteria {
    /**
     * Associate a given item (e.g. microarray) with a value (class)
     * E.g. case, control, test, etc.
     *
     * @param item  Microarray
     * @param value DSAnnotValue
     */
    public void addItem(DSBioObject item, DSAnnotValue value);

    /**
     * Associate a certain value (class) with a given panel
     * This also associates all the items in the panel with
     * the given value (class). E.g. case, control, test, etc.
     *
     * @param v     DSAnnotValue
     * @param panel DSPanel
     */
    public void addPanel(DSAnnotValue v, DSPanel<DSBioObject> panel);

    /**
     * returns a collections of items with the given annotation
     *
     * @param annotationValue DSAnnotValue
     * @return DSItemList
     */
    public DSItemList<DSBioObject> getTaggedItems(DSAnnotValue annotationValue);

    /**
     * Gets the currently selected criterion.
     *
     * @return the seelcted criterion label.
     */
    public DSAnnotLabel getSelectedCriterion();

    /**
     * Sets the selected criterion to the label label.
     *
     * @param label the label for the criterion.
     */
    public void setSelectedCriterion(DSAnnotLabel label);

    /**
     * size
     *
     * @param cSAnnotValue DSAnnotValue
     * @return int
     */
    public int size(DSAnnotValue value);

    public ImageIcon getIcon(DSAnnotValue value);

    /**
     * Given a possibly annotated microarray, return its value (class)
     * E.g. case, control, test, etc.
     *
     * @param item Microarray
     * @return DSAnnotValue
     */
    public DSAnnotValue getValue(DSBioObject item);

    /**
     * Given a panel find if it has a specific value (class)
     * E.g. case, control, test, etc.
     *
     * @param panel DSPanel
     * @return DSAnnotValue
     */
    public DSAnnotValue getValue(DSPanel panel);

    public boolean isUnsupervised();

    /**
     * disassociate a panel with a given value (class). This
     * also disassociates the corresponding items
     *
     * @param v     DSAnnotValue
     * @param panel DSPanel
     */
    public void removePanel(DSAnnotValue v, DSPanel<DSBioObject> panel);
}
