package org.geworkbench.bison.datastructure.biocollections.classification.phenotype;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.CSAnnotValue;
import org.geworkbench.bison.util.DSAnnotLabel;
import org.geworkbench.bison.util.DSAnnotValue;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * This keeps track of the <i>class</i> of panels and its members. The most common classes are Case and Control.
 * <p>
 * @todo - watkin - Eliminate icons from Bison and move in to caWorkbench.
 *
 * @author not attributable
 * @version 1.0
 */
public class CSClassCriteria extends CSPanel<DSBioObject> implements DSClassCriteria {

    private static class ClassCriterion implements Serializable {
        // This map is used to store the available annotation values. Note that this
        // allows new values to be registered and assigned on the fly
        public HashMap<DSAnnotValue, ImageIcon> annotValues = new HashMap<DSAnnotValue, ImageIcon>();
        public HashMap<DSAnnotValue, DSPanel<DSBioObject>> annotPanels = new HashMap<DSAnnotValue, DSPanel<DSBioObject>>();
        public HashMap<DSPanel, DSAnnotValue> paClass = new HashMap<DSPanel, DSAnnotValue>();
        public HashMap<DSBioObject, DSAnnotValue> maClass = new HashMap<DSBioObject, DSAnnotValue>();
    }

    private Map<DSAnnotLabel, ClassCriterion> classCriteria = new HashMap<DSAnnotLabel, ClassCriterion>();
    private DSAnnotLabel selectedLabel = null;

    // These are the default values that get registered automatically
    public static final DSAnnotValue cases = new CSAnnotValue("Case", 1);
    public static final DSAnnotValue controls = new CSAnnotValue("Control", 0);
    public static final DSAnnotValue test = new CSAnnotValue("Test", -100);
    public static final DSAnnotValue ignore = new CSAnnotValue("Ignore", -99);
    public static final DSAnnotValue all = new CSAnnotValue("All", -98);

    public final static ImageIcon redPinIcon = new ImageIcon(CSClassCriteria.class.getResource("redpin.gif"));
    public final static ImageIcon whitePinIcon = new ImageIcon(CSClassCriteria.class.getResource("whitepin.gif"));
    public final static ImageIcon eraserIcon = new ImageIcon(CSClassCriteria.class.getResource("eraser.gif"));
    public final static ImageIcon markerIcon = new ImageIcon(CSClassCriteria.class.getResource("marker.gif"));


    //    protected DSPanel<DSBioObject> selectedPanel     = null;
    //    protected int                       selectedItem      = -1;

    //    protected DSPanel<DSBioObject>      casePanels        = new CSPanel<DSBioObject>("Case");
    //    protected DSPanel<DSBioObject>      ctrlPanels        = new CSPanel<DSBioObject>("Control");
    //    protected DSPanel<DSBioObject>      testPanels        = new CSPanel<DSBioObject>("Test");
    //    protected DSPanel<DSBioObject>      ignrPanels        = new CSPanel<DSBioObject>("Ignore");
    // Maps a panel with a value (class)

    public CSClassCriteria() {
        super("Class Criteria");
    }

    public CSClassCriteria(String label, String subLabel) {
        super(label, subLabel);
    }

    public DSAnnotLabel getSelectedCriterion() {
        return selectedLabel;
    }

    public void setSelectedCriterion(DSAnnotLabel label) {
        selectedLabel = label;
        if (classCriteria.get(label) == null) {
            initLabel(label);
        }
    }

    private void initLabel(DSAnnotLabel label) {
        ClassCriterion classCriterion = new ClassCriterion();
        classCriterion.annotValues.put(cases, redPinIcon);
        classCriterion.annotValues.put(controls, whitePinIcon);
        classCriterion.annotValues.put(test, markerIcon);
        classCriterion.annotValues.put(ignore, eraserIcon);

        classCriterion.annotPanels.put(cases, new CSPanel<DSBioObject>("Case"));
        classCriterion.annotPanels.put(controls, new CSPanel<DSBioObject>("Control"));
        classCriterion.annotPanels.put(test, new CSPanel<DSBioObject>("Test"));
        classCriterion.annotPanels.put(ignore, new CSPanel<DSBioObject>("Ignore"));
        classCriteria.put(label, classCriterion);
    }

    /**
     * This function implements the key functionality of this class. It returns
     * a collection of all the items associated with the given annotation value
     *
     * @param annotationValue DSAnnotValue
     * @return ArrayList
     */
    public DSItemList<DSBioObject> getTaggedItems(DSAnnotValue annotationValue) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        DSItemList<DSBioObject> results = new CSItemList<DSBioObject>();
        for (DSBioObject bObject : classCriterion.maClass.keySet()) {
            DSAnnotValue v = classCriterion.maClass.get(bObject);
            if (annotationValue == all || v == annotationValue) {
                results.add(bObject);
            }
        }
        return results;
    }


    /**
     * Given a possibly annotated bio object, return its annotation value (class)
     * E.g. case, control, test, etc.
     *
     * @param item Microarray
     * @return DSAnnotValue
     */
    public DSAnnotValue getValue(DSBioObject item) {
        if (selectedLabel != null) {
            ClassCriterion classCriterion = classCriteria.get(selectedLabel);
            DSAnnotValue value = classCriterion.maClass.get(item);
            if (value != null) {
                return value;
            }
        }
        // The default of objects that have not been registered is control
        return controls;
    }

    /**
     * Associate a certain value (class) with a given panel
     * This also associates all the items in the panel with
     * the given value (class). E.g. case, control, test, etc.
     *
     * @param v     DSAnnotValue
     * @param panel DSPanel
     */
    public void addPanel(DSAnnotValue v, DSPanel<DSBioObject> panel) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        if (classCriterion.annotValues.get(v) == null) {
            classCriterion.annotValues.put(v, redPinIcon);
            classCriterion.annotPanels.put(v, new CSPanel<DSBioObject>(v.toString()));
        }
        classCriterion.paClass.put(panel, v);
        for (DSBioObject ma : panel) {
            addItem(ma, v);
        }
    }

    /**
     * disassociate a panel with a given value (class). This
     * also disassociates the corresponding items
     *
     * @param v     DSAnnotValue
     * @param panel DSPanel
     */
    public void removePanel(DSAnnotValue v, DSPanel<DSBioObject> panel) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        DSPanel<DSBioObject> registeredPanel = classCriterion.annotPanels.get(v);
        if (registeredPanel != null) {
            classCriterion.paClass.remove(panel);
            for (DSBioObject ma : panel) {
                classCriterion.maClass.remove(ma);
            }
        }
    }

    /**
     * Given a panel find if it has a specific value (class)
     * E.g. case, control, test, etc.
     *
     * @param panel DSPanel
     * @return DSAnnotValue
     */
    public DSAnnotValue getValue(DSPanel panel) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        DSAnnotValue value = classCriterion.paClass.get(panel);
        if (value != null) {
            return value;
        } else {
            return CSClassCriteria.controls;
        }
    }

    /**
     * Associate a given item (e.g. microarray) with a value (class)
     * E.g. case, control, test, etc.
     *
     * @param item  Microarray
     * @param value DSAnnotValue
     */
    public void addItem(DSBioObject item, DSAnnotValue value) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        // Find if this item was previously associated with a different
        // value.
        DSAnnotValue v = classCriterion.maClass.get(item);
        if (v != null) {
            classCriterion.maClass.remove(item);
        }
        classCriterion.maClass.put(item, value);
    }

    /**
     * size
     */
    public int size(DSAnnotValue value) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        int count = 0;
        for (DSAnnotValue v : classCriterion.maClass.values()) {
            if (v == value) {
                count++;
            }
        }
        return count;
    }

    public boolean isUnsupervised() {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        // An unsupervised analysis is performed under the following conditions
        // 1 - no classification selections have been made. I.e. no cases, no controls
        // 2 - all classifications other than test and ignore are in the same class
        //DSPanel<DSBioObject> casePanel = annotPanels.get(cases);
        if (classCriterion.maClass.keySet().size() == 0) {
            // no classification selections have been made. I.e. no cases, no controls
            return true;
        } else {
            DSAnnotValue mainType = null;
            for (DSAnnotValue av : classCriterion.maClass.values()) {
                if (mainType == null) {
                    if (av != ignore && av != test) {
                        mainType = av;
                    }
                } else {
                    if (av != mainType) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ImageIcon getIcon(DSAnnotValue value) {
        ClassCriterion classCriterion = classCriteria.get(selectedLabel);
        ImageIcon icon = classCriterion.annotValues.get(value);
        return icon;
    }

    static public Color getSelectionColor(int v) {
        Color color = null;
        if (v == cases.hashCode()) {
            color = Color.red;
        } else if (v == controls.hashCode()) {
            color = Color.blue;
        } else if (v == test.hashCode()) {
            color = Color.yellow;
        } else if (v == ignore.hashCode()) {
            color = Color.darkGray;
        } else {
            color = Color.white;
        }
        return color;
    }
}
