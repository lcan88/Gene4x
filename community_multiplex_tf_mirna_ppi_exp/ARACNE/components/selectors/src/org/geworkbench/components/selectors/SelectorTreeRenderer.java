package org.geworkbench.components.selectors;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author John Watkinson
 */
public class SelectorTreeRenderer extends DefaultTreeCellRenderer {

    protected JCheckBox checkBox;
    private JPanel component;
    protected JLabel cellLabel;
    private Color selectionForeground, selectionBackground, textForeground, textBackground;
    private int checkBoxWidth;
    protected SelectorPanel selectorPanel;

    public SelectorTreeRenderer(SelectorPanel panel) {
        selectorPanel = panel;
        checkBox = new JCheckBox();
        checkBox.setBackground(Color.WHITE);
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
        checkBoxWidth = checkBox.getPreferredSize().width;
        component = new JPanel();
        // component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));
        component.setLayout(new BorderLayout());
        component.setBackground(Color.WHITE);
        component.add(checkBox, BorderLayout.WEST);
        cellLabel = new JLabel("");
        // cellLabel.setHorizontalAlignment(JLabel.LEFT);
        cellLabel.setOpaque(true);
        cellLabel.setIconTextGap(0);
        component.add(cellLabel, BorderLayout.CENTER);
        Font fontValue;
        fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) {
            cellLabel.setFont(fontValue);
        }
    }

    public int getCheckBoxWidth() {
        return checkBoxWidth;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof String) {
            String label = (String) value;
            // Use custom renderer
            String displayLabel = label + " [" + selectorPanel.getContext().getItemsWithLabel(label).size() + "]";
            cellLabel.setText(" " + displayLabel);
            checkBox.setSelected(selectorPanel.getContext().isLabelActive(label));
            if (selected) {
                cellLabel.setForeground(selectionForeground);
                cellLabel.setBackground(selectionBackground);
            } else {
                cellLabel.setForeground(textForeground);
                cellLabel.setBackground(textBackground);
            }
            return component;
        }
        // Root
        if (value == selectorPanel.getContext()) {
            return super.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);
        }
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }

}
