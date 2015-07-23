package org.geworkbench.bison.testing;

import junit.framework.TestCase;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;

/**
 * @author John Watkinson
 */
public class TestCSMicroarray extends TestCase {

    public TestCSMicroarray(String s) {
        super(s);
    }

    public void testCSMicroarray() {
        // Create microarray and populate with 3 values
        CSMicroarray microarray = new CSMicroarray(3);
        {
            CSMarkerValue v = new CSExpressionMarkerValue(1);
            v.setConfidence(0.0);
            microarray.setMarkerValue(0, v);
        }
        {
            CSMarkerValue v = new CSExpressionMarkerValue(2);
            v.setConfidence(0.5);
            microarray.setMarkerValue(1, v);
        }
        {
            CSMarkerValue v = new CSExpressionMarkerValue(3);
            v.setConfidence(0.75);
            microarray.setMarkerValue(2, v);
        }
        assertEquals(3, microarray.getMarkerNo());
        // a marker is undefined if it is out of range or missing
        // To be added back in later, once the thresholds are determined for p-values.
        /*
        assertTrue(microarray.isMarkerUndefined(4));
        assertTrue(microarray.isMarkerUndefined(0));
        assertFalse(microarray.isMarkerUndefined(1));
        */
        assertEquals(1.0, microarray.getMarkerValue(0).getValue());
        assertEquals(2.0, microarray.getMarkerValue(1).getValue());
        assertEquals(3.0, microarray.getMarkerValue(2).getValue());

    }

}
