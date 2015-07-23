package org.geworkbench.bison.testing;

import junit.framework.TestCase;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

/**
 * @author John Watkinson
 */
public class TestCSMicroarraySetView extends TestCase {

    public TestCSMicroarraySetView(String s) {
        super(s);
    }

    public void testCSMicroarraySetView() {
        try {
            DSMicroarraySet microarraySet = UtilsForTests.createTestExprMicroarraySet();
            CSMicroarraySetView msv = new CSMicroarraySetView(microarraySet);
            assertSame(msv.getMicroarraySet(), microarraySet);
            CSPanel arrayPanel = new CSPanel();
            CSPanel<DSGeneMarker> markerPanel = new CSPanel<DSGeneMarker>();
            msv.setItemPanel(arrayPanel);
            msv.setMarkerPanel(markerPanel);
            DSItemList<DSGeneMarker> markers = microarraySet.getMarkers();
            arrayPanel.add(microarraySet.get(0));
            arrayPanel.add(microarraySet.get(1));
            markerPanel.add(markers.get(0));
            markerPanel.add(markers.get(1));
            markerPanel.add(markers.get(2));
            // Turn panels off
            msv.useItemPanel(false);
            msv.useMarkerPanel(false);
            assertEquals(msv.items().size(), microarraySet.size());
            assertEquals(msv.markers().size(), microarraySet.getMarkers().size());
            // Turn panels on
            msv.useItemPanel(true);
            msv.useMarkerPanel(true);
            assertEquals(msv.items().size(), 2);
            assertEquals(msv.markers().size(), 3);
            // However, the 'all' methods should be unchanged
            assertEquals(msv.allMarkers().size(), microarraySet.getMarkers().size());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
