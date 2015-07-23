package org.geworkbench.bison.testing;

import junit.framework.TestCase;
import org.geworkbench.bison.annotation.CSCriteria;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.CSAnnotLabel;
import org.geworkbench.bison.util.CSAnnotValue;

/**
 * @author John Watkinson
 */
public class TestCSCriteria extends TestCase {

    private static class Item implements DSNamed {

        public Item(String label) {
            this.label = label;
        }

        private String label;

        public String getLabel() {
            return label;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public TestCSCriteria(String s) {
        super(s);
    }

    public void testCSCriteria() {
        CSCriteria criteria = new CSCriteria();
        CSAnnotLabel label1 = new CSAnnotLabel("1");
        CSAnnotLabel label2 = new CSAnnotLabel("2");
        CSAnnotLabel label3 = new CSAnnotLabel("3");
        CSPanel panel1 = new CSPanel("1");
        CSPanel panel2 = new CSPanel("2");
        CSPanel panel3 = new CSPanel("3");
        CSAnnotValue valueA = new CSAnnotValue("A", 1);
        CSAnnotValue valueB = new CSAnnotValue("B", 2);
        CSAnnotValue valueC = new CSAnnotValue("C", 3);
        CSAnnotValue valueD = new CSAnnotValue("D", 4);
        Item item1 = new Item("Item 1");
        Item item2 = new Item("Item 2");
        Item item3 = new Item("Item 3");
        Item item4 = new Item("Item 4");
        Item item5 = new Item("Item 5");
        Item item6 = new Item("Item 6");
        // Add the first two panels to the criteria
        criteria.put(label1, panel1);
        criteria.put(label2, panel2);
        // Add some items
        criteria.addItem(item1, label1, valueA);
        criteria.addItem(item2, label1, valueA);
        criteria.addItem(item3, label1, valueB);
        // item1 in panel1
        criteria.setSelectedCriterion(label1);
        DSPanel value = criteria.getValue(item1);
        // The following should be ignored because label3 is not used
        criteria.addItem(item1, label3, valueA);
    }
}
