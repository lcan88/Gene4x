package org.geworkbench.bison.testing;

import junit.framework.TestCase;
import org.geworkbench.bison.annotation.*;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * @author John Watkinson
 */
public abstract class TestDSAnnotationContext extends TestCase {

    //// Provide concrete implementations to this test case by implementing the methods below

    public abstract DSAnnotationContextManager getAnnotationContextManager();

    public abstract <Q> DSAnnotationType<Q> createAnnotationType(String label, Class<Q> type);

    //// Test methods

    public void testAnnotations() {
        DSAnnotationContextManager manager = getAnnotationContextManager();
        // Create a dataset
        DSDataSet<DSMicroarray> dataSet = UtilsForTests.createTestExprMicroarraySet();
        DSMicroarray item1 = dataSet.get(0);
        DSMicroarray item2 = dataSet.get(1);
        DSMicroarray item3 = dataSet.get(2);
        // Use the default context
        DSAnnotationContext<DSMicroarray> context = manager.getCurrentContext(dataSet);
        assertNotNull(context);
        // Create an annotation
        DSAnnotationType<String> gender = createAnnotationType("Gender", String.class);
        // Ensure that the type didn't already exist
        assertTrue(context.addAnnotationType(gender));
        // Apply annotation
        context.annotateItem(item1, gender, "Female");
        context.annotateItem(item2, gender, "Male");
        context.annotateItem(item3, gender, "Female");
        // Check values
        assertEquals(context.getAnnotationForItem(item1, gender), "Female");
        assertEquals(context.getAnnotationForItem(item2, gender), "Male");
        assertEquals(context.getAnnotationForItem(item3, gender), "Female");
        // Create another annotation
        DSAnnotationType<Double> bloodPressure = createAnnotationType("Blood Pressure", Double.class);
        // Ensure that the type didn't already exist
        assertTrue(context.addAnnotationType(bloodPressure));
        // Ensure that the type already exists if we add it again
        assertFalse(context.addAnnotationType(bloodPressure));
        // Apply annotations
        context.annotateItem(item1, bloodPressure, 85.0);
        context.annotateItem(item2, bloodPressure, 102.0);
        context.annotateItem(item3, bloodPressure, 96.0);
        // Check values
        assertEquals(context.getAnnotationForItem(item1, bloodPressure), 85.0);
        assertEquals(context.getAnnotationForItem(item2, bloodPressure), 102.0);
        assertEquals(context.getAnnotationForItem(item3, bloodPressure), 96.0);
        // Create a third annotation implicitly by applying values
        DSAnnotationType<Integer> age = createAnnotationType("Age", Integer.class);
        context.annotateItem(item1, age, 54);
        context.annotateItem(item2, age, 22);
        context.annotateItem(item3, age, 67);
        // Check values
        assertEquals(context.getAnnotationForItem(item1, age), new Integer(54));
        assertEquals(context.getAnnotationForItem(item2, age), new Integer(22));
        assertEquals(context.getAnnotationForItem(item3, age), new Integer(67));
        // Make sure there are exactly three annotations: gender, blood pressure and age
        assertEquals(context.getNumberOfAnnotationTypes(), 3);
        assertSame(context.getAnnotationType(0), gender);
        assertSame(context.getAnnotationType(1), bloodPressure);
        assertSame(context.getAnnotationType(2), age);
        // Make sure item1 has annotations for all three of the types
        DSAnnotationType[] types = context.getAnnotationTypesForItem(item1);
        assertEquals(types.length, 3);
        // Remove blood pressure annotation from item1
        assertTrue(context.removeAnnotationFromItem(item1, bloodPressure));
        // Make sure it is gone
        assertNull(context.getAnnotationForItem(item1, bloodPressure));
        // Try to remove again, make sure it returns false
        assertFalse(context.removeAnnotationFromItem(item1, bloodPressure));
        // Get all the annotations for item1 and ensure that there are only two
        types = context.getAnnotationTypesForItem(item1);
        assertEquals(types.length, 2);
        // Remove the blood pressure annotation type altogether
        assertTrue(context.removeAnnotationType(bloodPressure));
        // Ensure that it is indeed gone
        assertEquals(context.getNumberOfAnnotationTypes(), 2);
        assertSame(context.getAnnotationType(0), gender);
        assertSame(context.getAnnotationType(1), age);
        // Removing again should return false
        assertFalse(context.removeAnnotationType(bloodPressure));
        // Ensure that there are no left-over values in any of the items
        assertNull(context.getAnnotationForItem(item1, bloodPressure));
        assertNull(context.getAnnotationForItem(item2, bloodPressure));
        assertNull(context.getAnnotationForItem(item3, bloodPressure));
    }

    public void testLabels() {
        DSAnnotationContextManager manager = getAnnotationContextManager();
        // Create a dataset
        DSDataSet<DSMicroarray> dataSet = UtilsForTests.createTestExprMicroarraySet();
        DSMicroarray item1 = dataSet.get(0);
        DSMicroarray item2 = dataSet.get(1);
        DSMicroarray item3 = dataSet.get(2);
        // Use a specific context
        DSAnnotationContext<DSMicroarray> context = manager.createContext(dataSet, "Disease Type");
        assertNotNull(context);
        // Create a label
        String none = "None";
        assertTrue(context.addLabel(none));
        // Creating it again should result in a false return value
        assertFalse(context.addLabel(none));
        // Apply label
        assertTrue(context.labelItem(item1, none));
        // Applying it again should return false
        assertFalse(context.labelItem(item1, none));
        // Check that label is applied
        assertTrue(context.hasLabel(item1, none));
        // Create another label
        String type1 = "Type 1";
        assertTrue(context.addLabel(type1));
        // Creating it again should result in a false return value
        assertFalse(context.addLabel(type1));
        // Apply label
        context.labelItem(item2, type1);
        context.labelItem(item3, type1);
        // Ensure that both items are labelled
        assertTrue(context.hasLabel(item2, type1));
        assertTrue(context.hasLabel(item3, type1));
        DSPanel<DSMicroarray> type1Items = context.getItemsWithLabel(type1);
        assertEquals(type1Items.size(), 2);
        assertTrue(type1Items.contains(item2));
        assertTrue(type1Items.contains(item3));
        // Create a third label implicitly by labelling an item
        String type2 = "Type 2";
        assertTrue(context.labelItem(item3, type2));
        assertTrue(context.hasLabel(item3, type2));
        // Check that item3 has both type1 and type2 labels
        Object[] labels = context.getLabelsForItem(item3);
        assertEquals(labels.length, 2);
        // Ensure that the three labels are in the context, in order
        assertEquals(context.getNumberOfLabels(), 3);
        assertSame(context.getLabel(0), none);
        assertSame(context.getLabel(1), type1);
        assertSame(context.getLabel(2), type2);
        // Get single-label selection
        DSPanel<DSMicroarray> labelPanel = context.getItemsWithLabel(type1);
        // Ensure that it has the right structure
        assertEquals(labelPanel.getLabel(), type1);
        // Note: default selection sub-panel is always present
        assertEquals(labelPanel.panels().size(), 1);
        assertEquals(labelPanel.size(), 2);
        assertTrue(labelPanel.contains(item2));
        assertTrue(labelPanel.contains(item3));
        // Get multi-label OR selection
        DSPanel<DSMicroarray> orPanel = context.getItemsWithAnyLabel(type1, type2);
        // Ensure that it has the right structure
        DSItemList<DSPanel<DSMicroarray>> subPanels = orPanel.panels();
        // Note: default selection sub-panel is always present
        assertEquals(subPanels.size(), 3);
        DSPanel type1Panel = subPanels.get(1);
        DSPanel type2Panel = subPanels.get(2);
        assertEquals(type1Panel.getLabel(), type1);
        assertEquals(type2Panel.getLabel(), type2);
        assertEquals(type1Panel.size(), 2);
        assertEquals(type2Panel.size(), 1);
        assertTrue(type1Panel.contains(item2));
        assertTrue(type1Panel.contains(item3));
        assertTrue(type2Panel.contains(item3));
        // Get multi-label AND selection
        DSPanel<DSMicroarray> andPanel = context.getItemsWithAllLabels(type1, type2);
        // Ensure that it has the right structure
        subPanels = andPanel.panels();
        // Note: default selection sub-panel is always present
        assertEquals(subPanels.size(), 1);
        assertEquals(andPanel.size(), 1);
        assertTrue(andPanel.contains(item3));
        // Activate type1 and type2 using both methods
        context.activateLabel(type1);
        context.setLabelActive(type2, true);
        assertFalse(context.isLabelActive(none));
        assertTrue(context.isLabelActive(type1));
        assertTrue(context.isLabelActive(type2));
        // Get a panel of all activated items
        DSPanel<DSMicroarray> activePanel = context.getActiveItems();
        // Ensure that it has the right structure
        subPanels = activePanel.panels();
        // Note: default selection sub-panel is always present
        assertEquals(subPanels.size(), 3);
        type1Panel = subPanels.get(1);
        type2Panel = subPanels.get(2);
        assertEquals(type1Panel.getLabel(), type1);
        assertEquals(type2Panel.getLabel(), type2);
        assertEquals(type1Panel.size(), 2);
        assertEquals(type2Panel.size(), 1);
        assertTrue(type1Panel.contains(item2));
        assertTrue(type1Panel.contains(item3));
        assertTrue(type2Panel.contains(item3));
        // Remove type1 label from item3
        assertTrue(context.removeLabelFromItem(item3, type1));
        // Removing again should return false
        assertFalse(context.removeLabelFromItem(item3, type1));
        // Ensure that it is really removed
        assertFalse(context.hasLabel(item3, type1));
        // Add the label back
        assertTrue(context.labelItem(item3, type1));
        assertTrue(context.hasLabel(item3, type1));
        // Remove type1 label altogether
        assertTrue(context.removeLabel(type1));
        // Repeating remove should return false
        assertFalse(context.removeLabel(type1));
        // Ensure that remaining labels are None and Type 2
        assertEquals(context.getNumberOfLabels(), 2);
        assertSame(context.getLabel(0), none);
        assertSame(context.getLabel(1), type2);
        // Ensure that item2 has no labels and item3 has only type2 now.
        assertEquals(context.getLabelsForItem(item2).length, 0);
        labels = context.getLabelsForItem(item3);
        assertEquals(labels.length, 1);
        assertSame(labels[0], type2);
        assertFalse(context.hasLabel(item2, type1));
        assertFalse(context.hasLabel(item3, type1));
        // Create an annotation for use in a criterion
        final DSAnnotationType<Integer> age = createAnnotationType("Age", Integer.class);
        context.annotateItem(item1, age, 54);
        context.annotateItem(item2, age, 22);
        context.annotateItem(item3, age, 67);
        // Create a criterion for "senior"
        DSCriterion<DSMicroarray> seniorCriterion = new DSCriterion<DSMicroarray>() {
            public boolean applyCriterion(DSMicroarray item, DSAnnotationSource<DSMicroarray> annotationSource) {
                Integer ageValue = annotationSource.getAnnotationForItem(item, age);
                if (ageValue != null) {
                    return ageValue >= 55;
                } else {
                    return false;
                }
            }
        };
        // Create a criterion label for this
        String senior = "Senior";
        assertTrue(context.addCriterionLabel(senior, seniorCriterion));
        // Adding the label again should return false, even if not a criteria label
        assertFalse(context.addCriterionLabel(senior, seniorCriterion));
        assertFalse(context.addLabel(senior));
        // Ensure that this label is known to be a criterion label and not the others
        assertTrue(context.isCriterionLabel(senior));
        assertFalse(context.isCriterionLabel(none));
        assertFalse(context.isCriterionLabel(type2));
        // Ensure that the criterion can be retrieved
        assertSame(context.getCriterionForLabel(senior), seniorCriterion);
        // Ensure that the others have null criterion
        assertNull(context.getCriterionForLabel(none));
        assertNull(context.getCriterionForLabel(type2));
        // Check that only item3 has this label
        assertFalse(context.hasLabel(item1, senior));
        assertFalse(context.hasLabel(item2, senior));
        assertTrue(context.hasLabel(item3, senior));
        // Change the annotation of item1 to make it now satisfy the criterion
        context.annotateItem(item1, age, 55);
        assertTrue(context.hasLabel(item1, senior));
        // Test that the "Selection" label, which is special for DSPanel, works
        // Make all labels inactive
        int n = context.getNumberOfLabels();
        for (int i = 0; i < n; i++) {
            context.setLabelActive(context.getLabel(i), false);
        }
        String selection = "Selection";
        context.labelItem(item1, selection);
        DSPanel selectionPanel = context.getItemsWithLabel(selection);
        // Only selection panel
//        assertEquals(1, selectionPanel.panels().size());
//        assertSame(item1, ((DSPanel)selectionPanel.panels().get(0)).get(0));
        // Activate selection
        context.activateLabel(selection);
        activePanel = context.getActiveItems();
        // Only selection panel
        assertEquals(1, activePanel.panels().size());
        assertSame(item1, activePanel.panels().get(0).get(0));
    }

    public void testClassification() {
        DSAnnotationContextManager manager = getAnnotationContextManager();
        // Create a dataset
        DSDataSet<DSMicroarray> dataSet = UtilsForTests.createTestExprMicroarraySet();
        DSMicroarray item1 = dataSet.get(0);
        DSMicroarray item2 = dataSet.get(1);
        DSMicroarray item3 = dataSet.get(2);
        // Use a specific context
        DSAnnotationContext<DSMicroarray> context = manager.createContext(dataSet, "Class Test");
        assertNotNull(context);
        // Create some labels and label items
        String labelA = "A";
        String labelB = "B";
        String labelC = "C";
        context.labelItem(item1, labelA);
        context.labelItem(item2, labelB);
        context.labelItem(item3, labelB);
        context.labelItem(item3, labelC);
        String classCase = "Case";
        String classControl = "Control";
        String classIgnore = "Ignore";
        // Add class "Case"
        assertTrue(context.addClass(classCase));
        // Adding again should return false
        assertFalse(context.addClass(classCase));
        // As the only class, it should be the default automatically
        assertSame(context.getDefaultClass(), classCase);
        // All labels and items should be in the default class
        assertSame(context.getClassForLabel(labelA), classCase);
        assertSame(context.getClassForLabel(labelB), classCase);
        assertSame(context.getClassForLabel(labelC), classCase);
        assertSame(context.getClassForItem(item1), classCase);
        assertSame(context.getClassForItem(item2), classCase);
        assertSame(context.getClassForItem(item3), classCase);
        // Add "Control" next -- it should take over as the default
        assertTrue(context.addClass(classControl));
        assertSame(context.getDefaultClass(), classControl);
        assertSame(context.getClassForLabel(labelA), classControl);
        assertSame(context.getClassForItem(item1), classControl);
        // Add "Ignore" by implicitly classifying
        assertTrue(context.assignClassToLabel(labelA, classIgnore));
        assertSame(context.getClassForLabel(labelA), classIgnore);
        assertSame(context.getClassForItem(item1), classIgnore);
        // Set "Control" as default class explicitly
        assertTrue(context.setDefaultClass(classControl));
        assertSame(context.getDefaultClass(), classControl);
        assertSame(context.getClassForLabel(labelB), classControl);
        // Ensure that there are three classes, and they are in order
        assertEquals(context.getNumberOfClasses(), 3);
        assertSame(context.getClass(0), classCase);
        assertSame(context.getClass(1), classControl);
        assertSame(context.getClass(2), classIgnore);
        // Assign "Case" to "B"
        assertTrue(context.assignClassToLabel(labelB, classCase));
        // Reassigning the same label should return false
        assertFalse(context.assignClassToLabel(labelB, classCase));
        assertSame(context.getClassForLabel(labelB), classCase);
        assertSame(context.getClassForItem(item2), classCase);
        assertSame(context.getClassForItem(item3), classCase);
        // Assign "Control" to "C"
        assertTrue(context.assignClassToLabel(labelC, classControl));
        // The priority rules dictate that item3 should still be "Case"
        assertSame(context.getClassForItem(item3), classCase);
        // Change labelling and ensure that classes update appropriately
        context.removeLabelFromItem(item3, labelB);
        // Now item3 should be "Control"
        assertSame(context.getClassForItem(item3), classControl);
        // Remove label "A" entirely -- item1 should become "Control" (the default)
        context.removeLabel(labelA);
        assertSame(context.getClassForItem(item1), classControl);
        // Remove class "Control" -- new default is ignore
        assertTrue(context.removeClass(classControl));
        // Removing it again returns false
        assertFalse(context.removeClass(classControl));
        // Ensure there are only the 2 classes remaining and that the labels and items were properly reassigned
        assertEquals(context.getNumberOfClasses(), 2);
        assertSame(context.getClass(0), classCase);
        assertSame(context.getClass(1), classIgnore);
        assertSame(context.getClassForLabel(labelC), classIgnore);
        assertSame(context.getClassForLabel(labelB), classCase);
        assertSame(context.getClassForItem(item1), classIgnore);
        assertSame(context.getClassForItem(item2), classCase);
        assertSame(context.getClassForItem(item3), classIgnore);
        // Assign "Case" to label "C"
        assertTrue(context.assignClassToLabel(labelC, classCase));
        // Get all labels for "Case"
        Object[] caseLabels = context.getLabelsForClass(classCase);
        assertEquals(caseLabels.length, 2);
        assertTrue(((caseLabels[0] == labelB) && (caseLabels[1] == labelC)) || ((caseLabels[0] == labelC) && (caseLabels[1] == labelB)));
        // Relabel item3 with labelB
        context.labelItem(item3, labelB);
        // Get all items for "Case"
        DSPanel<DSMicroarray> caseItems = context.getItemsForClass(classCase);
        // Ensure that thes structure of the panel is correct-- note that each item should appear only once
        // Note: default selection sub-panel is always present
        assertEquals(caseItems.panels().size(), 1);
        assertEquals(caseItems.size(), 2);
        assertTrue(caseItems.contains(item2));
        assertTrue(caseItems.contains(item3));
        // Remove "Case" from label "B"
        assertSame(context.removeClassFromLabel(labelB), classCase);
        // Should now be the default "Ignore"
        assertSame(context.getClassForLabel(labelB), classIgnore);
        // Removing again should return null
        assertNull(context.removeClassFromLabel(labelB));
    }
}
