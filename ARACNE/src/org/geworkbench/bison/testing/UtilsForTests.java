package org.geworkbench.bison.testing;

import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;

/**
 * @author John Watkinson
 */
public class UtilsForTests {

    /**
     * Creates a test microarray set with 3 microarrays, 4 markers, and the following (value, p) pairs:
     * <table>
     * <tr>
     * <td><b>&nbsp;</b></td><td><b>One</b></td><td><b>Two</b></td><td><b>Three</b></td><td><b>Four</b></td>
     * </tr>
     * <tr>
     * <td><b>A</b></td><td>(1, 1)</td><td>(2, 0.5)</td><td>(3, 1)</td><td>(4, 0.5)</td>
     * </tr>
     * <tr>
     * <td><b>B</b></td><td>(2, 0.75)</td><td>(4, 0.25)</td><td>(6, 0.75)</td><td>(8, 0.25)</td>
     * </tr>
     * <tr>
     * <td><b>C</b></td><td>(3, 1)</td><td>(6, 0.5)</td><td>(9, 1)</td><td>(12, 0.5)</td>
     * </tr>
     * </table>
     *
     * @return the created microarray set.
     */
    public static CSExprMicroarraySet createTestExprMicroarraySet() {
        CSExprMicroarraySet ems = new CSExprMicroarraySet();
        CSMarkerVector markerVector = ems.getMarkerVector();
        // Create markers
        {
            CSExpressionMarker marker = new CSExpressionMarker(0);
            marker.setLabel("One");
            marker.setGeneName("Name: One");
            marker.setDescription("Description: One");
            markerVector.add(marker);
        }
        {
            CSExpressionMarker marker = new CSExpressionMarker(1);
            marker.setLabel("Two");
            marker.setGeneName("Name: Two");
            marker.setDescription("Description: Two");
            markerVector.add(marker);
        }
        {
            CSExpressionMarker marker = new CSExpressionMarker(2);
            marker.setLabel("Three");
            marker.setGeneName("Name: Three");
            marker.setDescription("Description: Three");
            markerVector.add(marker);
        }
        {
            CSExpressionMarker marker = new CSExpressionMarker(3);
            marker.setLabel("Four");
            marker.setGeneName("Name: Four");
            marker.setDescription("Description: Four");
            markerVector.add(marker);
        }
        // Create microarrays
        for (int i = 0; i < 3; i++) {
            CSMicroarray microarray = new CSMicroarray(4);
            microarray.setSerial(0);
            microarray.setLabel("" + (char) ('A' + i));
            for (int j = 0; j < 4; j++) {
                float v = (i + 1) * (j + 1);
                CSExpressionMarkerValue value = new CSExpressionMarkerValue(v);
                double c = 0.25;
                if (i % 2 == 0) {
                    c += 0.25;
                }
                if (j % 2 == 0) {
                    c += 0.5;
                }
                value.setConfidence(c);
                microarray.setMarkerValue(0, value);
            }
            ems.add(microarray);
        }
        ems.setLabel("Test MicroarraySet");
        return ems;
    }
}
