package org.geworkbench.util.visualproperties;

import org.geworkbench.bison.datastructure.complex.panels.CSPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author John Watkinson
 */
public class VisualPropertiesDialog extends JDialog {

    private boolean propertiesChanged = false;

    private JShapeButton display;
    private PanelVisualProperties properties;
    private PanelVisualProperties defaultProperties;
    private boolean defaults = false;

    public VisualPropertiesDialog(Frame owner, String title, Object item, int defaultIndex) throws HeadlessException {
        super(owner, title, true);
        // Look for existing visual properties
        PanelVisualPropertiesManager manager = PanelVisualPropertiesManager.getInstance();
        properties = manager.getVisualProperties(item);
        defaultProperties = manager.getDefaultVisualProperties(defaultIndex);
        if (properties == null) {
            properties = new PanelVisualProperties(defaultProperties.getShapeIndex(), defaultProperties.getColor());
            defaults = true;
        }
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        display = new JShapeButton(properties.getShape(), properties.getColor());
        content.add(display, BorderLayout.CENTER);
        JPanel spacingPanel = new JPanel();
        spacingPanel.setLayout(new BoxLayout(spacingPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        spacingPanel.add(buttonPanel);
        spacingPanel.add(Box.createVerticalStrut(6));
        content.add(spacingPanel, BorderLayout.SOUTH);
        JButton changeColorButton = new JButton("Change Color...");
        JButton changeShapeButton = new JButton("Change Shape...");
        JButton defaultButton = new JButton("Default Color/Shape");
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(Box.createGlue());
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(changeColorButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(changeShapeButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(defaultButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(Box.createGlue());
        // Add behavior
        changeColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(VisualPropertiesDialog.this, "Choose Color", properties.getColor());
                if (newColor != null) {
                    properties.setColor(newColor);
                    display.setPaint(newColor);
                    defaults = false;
                    display.repaint();
                }
            }
        });
        changeShapeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JShapeDialog dialog = new JShapeDialog(VisualPropertiesDialog.this, "Choose Shape", PanelVisualProperties.AVAILABLE_SHAPES, properties.getColor(), properties.getShapeIndex());
                dialog.pack();
                dialog.setSize(400, 400);
                dialog.setVisible(true);
                int index = dialog.getResult();
                properties.setShapeIndex(index);
                display.setShape(PanelVisualProperties.AVAILABLE_SHAPES[index]);
                defaults = false;
                display.repaint();
            }
        });
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                properties.setShapeIndex(defaultProperties.getShapeIndex());
                properties.setColor(defaultProperties.getColor());
                display.setShape(PanelVisualProperties.AVAILABLE_SHAPES[properties.getShapeIndex()]);
                display.setPaint(properties.getColor());
                defaults = true;
                display.repaint();
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                propertiesChanged = true;
                dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                propertiesChanged = false;
                dispose();
            }
        });
    }

    public boolean isPropertiesChanged() {
        return propertiesChanged;
    }

    public PanelVisualProperties getVisualProperties() {
        if (defaults) {
            return null;
        } else {
            return properties;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test JDialog");
        frame.pack();
        frame.setVisible(true);
        VisualPropertiesDialog dialog = new VisualPropertiesDialog(frame, "Customize Visual Properties", new CSPanel(), 0);
        dialog.pack();
        dialog.setSize(600, 600);
        dialog.setVisible(true);
        if (dialog.isPropertiesChanged()) {
            System.out.println("Properties changed to: " + dialog.getVisualProperties());
        } else {
            System.out.println("Properties left unchanged.");
        }
    }
}
