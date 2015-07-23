package org.geworkbench.bison.testing;

import junit.framework.Test;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.testing.list.AbstractTestList;

import java.util.Collection;
import java.util.List;

/**
 * Tests {@link CSPanel}.
 *
 * @author John Watkinson
 */
public class TestCSPanel extends AbstractTestList {

    // These are field variables for now so that some of the basic lists tests can test two collection's elements
    // for equality
    CSMicroarray m1 = new CSMicroarray(1);
    CSMicroarray m2 = new CSMicroarray(2);
    CSMicroarray m3 = new CSMicroarray(3);

    CSMicroarray m4 = new CSMicroarray(4);
    CSMicroarray m5 = new CSMicroarray(5);
    CSMicroarray m6 = new CSMicroarray(6);

    public TestCSPanel(String s) {
        super(s);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestCSPanel.class);
    }

    public List makeEmptyList() {
        CSPanel<CSMicroarray> p1 = new CSPanel<CSMicroarray>("P1", "p1");
//        CSMicroarray m1 = new CSMicroarray(1);
//        CSMicroarray m2 = new CSMicroarray(2);
//        CSMicroarray m3 = new CSMicroarray(3);
//        p1.add(m1);
//        p1.add(m2);
//        p1.add(m3);
        return p1;
    }

    public Object[] getFullElements() {
        return new Object[]{m1,m2,m3};
    }

    public Object[] getOtherElements() {
        return new Object[]{m4,m5,m6};
    }

    public Object[] getFullNonNullElements() {
        return getFullElements();
    }

    public Collection makeConfirmedCollection() {
        return new CSPanel<CSMicroarray>("P1", "p1");
    }

    public Collection makeConfirmedFullCollection() {
        CSPanel<CSMicroarray> p1 = new CSPanel<CSMicroarray>("P1", "p1");
        p1.add(m1);
        p1.add(m2);
        p1.add(m3);
        return p1;
    }

    public void xtestCSPanel() {
        // Create new panels
        CSPanel<CSMicroarray> p1 = new CSPanel<CSMicroarray>("P1", "p1");
        CSPanel<CSMicroarray> p2 = new CSPanel<CSMicroarray>("P2", "p2");
        // Create some microarrays
        CSMicroarray m1 = new CSMicroarray(1);
        CSMicroarray m2 = new CSMicroarray(2);
        CSMicroarray m3 = new CSMicroarray(3);
        // Check labels
        assertEquals("P1", p1.getLabel());
        assertEquals("p1", p1.getSubLabel());
        p1.setLabel("P1-New");
        p1.setSubLabel("p1-New");
        assertEquals("P1-New", p1.getLabel());
        assertEquals("p1-New", p1.getSubLabel());
        // Add items to p1
        p1.add(m1);
        p1.add(m2);
        // Test size
        assertEquals(2, p1.size());
        // Add items to p2
        p2.add(m3);
        // Add p2 to p1 and ensure
        p1.setPanel(0, p2);
        DSItemList<DSPanel<CSMicroarray>> panels = p1.panels();
        assertEquals(1, panels.size());
        assertEquals(p2, panels.get(0));
        // Assert that it was added
        assertTrue(p1.panels().contains(p2));
        // Test size (should still be 2, only active subpanels contribute to size).
        assertEquals(2, p1.size());
        // Ensure not active
        assertFalse(p2.isActive());
        // Activate p2
        p2.setActive(true);
        assertTrue(p2.isActive());
        // Test size.
        assertEquals(3, p1.size());
        //  Check active subset
        assertEquals(p1.activeSubset().size(), 1);
        // Test look-up of m3
        assertEquals(p1.getPanel(m3), p2);
        // Test look-up by index
        assertEquals(p1.get(0), m1);
        assertEquals(p1.get(1), m2);
        assertEquals(p1.get(2), m3);
        // Clear p2
        p2.clear();
        // Ensure size drops to 2
        assertEquals(2, p1.size());
        // Clear p1
        p1.clear();
        // Ensure that size is now 0
        assertEquals(0, p1.size());
        // Ensure that p2 is no longer a subpanel
        assertEquals(p1.panels().size(), 0);
    }
}
