package org.geworkbench.bison.testing;

import junit.framework.TestCase;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.views.CSDataSetView;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;

/**
 * @author John Watkinson
 */
public class TestCSDataSetView extends TestCase {

    public TestCSDataSetView(String s) {
        super(s);
    }

    public void testCSDataSetView() {
        try {
            CSDataSetView dsv = new CSDataSetView();
            DSDataSet dataSet = UtilsForTests.createTestExprMicroarraySet();
            dsv.setDataSet(dataSet);
            assertSame(dataSet, dsv.getDataSet());
            CSPanel panel = new CSPanel();
            dsv.setItemPanel(panel);
            panel.add(dsv.get(0));
            panel.add(dsv.get(1));
            dsv.useItemPanel(false);
            // Should include all items in dataset
            assertEquals(dataSet.size(), dsv.items().size());
            // Should include just panel items
            dsv.useItemPanel(true);
            assertEquals(2, dsv.items().size());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
