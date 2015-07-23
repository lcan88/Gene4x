package org.geworkbench.components.selectors;

import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.events.PhenotypeSelectorEvent;
import org.geworkbench.events.SingleMicroarrayEvent;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * @author John Watkinson
 */
public class PhenotypePanel extends SelectorPanel<DSMicroarray> {

    public static final String[] CLASSES =
            {"Case", "Control", "Test", "Ignore"};

    public static final Color[] CLASS_COLORS =
            {Color.RED, Color.WHITE, Color.GREEN, Color.LIGHT_GRAY};

    public static final ImageIcon[] CLASS_ICONS =
            {
                    new ImageIcon(PhenotypePanel.class.getResource("redpin.gif")),
                    new ImageIcon(PhenotypePanel.class.getResource("whitepin.gif")),
                    new ImageIcon(PhenotypePanel.class.getResource("greenpin.gif")),
                    new ImageIcon(PhenotypePanel.class.getResource("graypin.gif"))
            };

    public static final String DEFAULT_CLASS = "Control";

    private class ClassificationListener implements ActionListener {
        private int classIndex;

        public ClassificationListener(int classIndex) {
            this.classIndex = classIndex;
        }

        public void actionPerformed(ActionEvent e) {
            String[] labels = getSelectedTreesFromTree();
            if (labels.length > 0) {
                for (int i = 0; i < labels.length; i++) {
                    context.assignClassToLabel(labels[i], CLASSES[classIndex]);
                    // Notify model
                    treeModel.fireLabelChanged(labels[i]);
                }
                throwLabelEvent();
            }
        }
    }

    private static class PhenotypeCellRenderer extends SelectorTreeRenderer {

        public PhenotypeCellRenderer(final PhenotypePanel panel) {
            super(panel);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (value instanceof String) {
                String label = (String) value;
                String clazz = selectorPanel.getContext().getClassForLabel(label);
                int index = getIndexForClass(clazz);
                // checkBox.setBackground(CLASS_COLORS[index]);
                cellLabel.setIcon(CLASS_ICONS[index]);
            } else if (value instanceof DSMicroarray) {
                // Todo - somehow show class of individual items
//                String clazz = selectorPanel.getContext().getClassForItem((DSMicroarray) value);
//                int index = getIndexForClass(clazz);
//                comp.setBackground(CLASS_COLORS[index]);
            }
            return comp;
        }
    }

    private DSMicroarraySet<DSMicroarray> set;
    private JRadioButtonMenuItem[] rightClickClassButtons;
    private JRadioButtonMenuItem[] leftClickClassButtons;
    private JPopupMenu classPopup;

    public PhenotypePanel() {
        super(DSMicroarray.class, "Array/Phenotype");
        // Add "Classification" item and sub-items
        JMenu classificationMenu = new JMenu("Classification");
        classPopup = new JPopupMenu();
        ButtonGroup classGroup = new ButtonGroup();
        rightClickClassButtons = new JRadioButtonMenuItem[4];
        leftClickClassButtons = new JRadioButtonMenuItem[4];
        for (int i = 0; i < CLASSES.length; i++) {
            rightClickClassButtons[i] = new JRadioButtonMenuItem(CLASSES[i]);
            leftClickClassButtons[i] = new JRadioButtonMenuItem(CLASSES[i]);
            classificationMenu.add(rightClickClassButtons[i]);
            classPopup.add(leftClickClassButtons[i]);
            rightClickClassButtons[i].addActionListener(new ClassificationListener(i));
            leftClickClassButtons[i].addActionListener(new ClassificationListener(i));
            classGroup.add(rightClickClassButtons[i]);
            classGroup.add(leftClickClassButtons[i]);
        }
        treePopup.add(classificationMenu);
        setTreeRenderer(new PhenotypeCellRenderer(this));
        // Add classification legend
        JPanel legend = new JPanel();
        legend.setBackground(Color.WHITE);
        legend.setLayout(new BoxLayout(legend, BoxLayout.X_AXIS));
        for (int i = 0; i < CLASSES.length; i++) {
            JLabel classLabel = new JLabel(CLASSES[i], CLASS_ICONS[i], SwingConstants.TRAILING);
            classLabel.setIconTextGap(0);
            legend.add(classLabel);
            legend.add(Box.createHorizontalGlue());
        }
        lowerPanel.add(legend);
    }

    private static int getIndexForClass(String clazz) {
        for (int i = 0; i < CLASSES.length; i++) {
            if (CLASSES[i].equals(clazz)) {
                return i;
            }
        }
        return -1;
    }

    protected void labelClicked(MouseEvent e, TreePath path, String label) {
        if (e.getX() < panelTree.getPathBounds(path).x + treeRenderer.getCheckBoxWidth() + CLASS_ICONS[0].getIconWidth()) {
            String clazz = context.getClassForLabel(label);
            int i = getIndexForClass(clazz);
            leftClickClassButtons[i].setSelected(true);
            classPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    protected void showTreePopup(MouseEvent e) {
        String label = getLabelForPath(rightClickedPath);
        String clazz = context.getClassForLabel(label);
        int i = getIndexForClass(clazz);
        rightClickClassButtons[i].setSelected(true);
        super.showTreePopup(e);
    }

    protected void initializeContext(DSAnnotationContext context) {
        super.initializeContext(context);
        for (int i = 0; i < CLASSES.length; i++) {
            context.addClass(CLASSES[i]);
        }
        context.setDefaultClass(DEFAULT_CLASS);
    }

    protected boolean dataSetChanged(DSDataSet dataSet) {
        if (dataSet instanceof DSMicroarraySet) {
            set = (DSMicroarraySet<DSMicroarray>) dataSet;
            setItemList(set);
            return true;
        } else {
            dataSetCleared();
            return false;
        }
    }

    protected void throwLabelEvent() {
        PhenotypeSelectorEvent event = new PhenotypeSelectorEvent(context.getLabelTree(), set);
        publishPhenotypeSelectorEvent(event);
    }

    protected void publishSingleSelectionEvent(DSMicroarray item) {
        SingleMicroarrayEvent event = new SingleMicroarrayEvent(item, "Selected");
        publishSingleMicroarrayEvent(event);
    }

    @Publish public PhenotypeSelectorEvent publishPhenotypeSelectorEvent(PhenotypeSelectorEvent event) {
        return event;
    }

    @Publish public SingleMicroarrayEvent publishSingleMicroarrayEvent(SingleMicroarrayEvent event) {
        return event;
    }

}
