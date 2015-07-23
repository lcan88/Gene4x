package org.geworkbench.util.visualproperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog that displays some shapes and allows one to be chosen.
 *
 * @author John Watkinson
 */
public class JShapeDialog extends JDialog {

    private static final int NUMBER_OF_COLUMNS = 4;

    private static final int WAITING_FOR_RESULT = -1;

    private int result = WAITING_FOR_RESULT;

    public JShapeDialog(Frame owner, String title, Shape[] shapes, Paint paint, final int previousIndex) throws HeadlessException {
        super(owner, title, true);
        init(shapes, paint, previousIndex);
    }

    public JShapeDialog(Dialog owner, String title, Shape[] shapes, Paint paint, final int previousIndex) throws HeadlessException {
        super(owner, title, true);
        init(shapes, paint, previousIndex);
    }

    private void init(Shape[] shapes, Paint paint, final int previousIndex) {
        result = previousIndex;
        Container container = getContentPane();
        JPanel content = new JPanel();
        container.add(content);
        content.setBorder(new EmptyBorder(6, 6, 6, 6));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        int n = shapes.length;
        int rows = n / NUMBER_OF_COLUMNS;
        if (n % NUMBER_OF_COLUMNS != 0) {
            rows++;
        }
        int index = 0;
        final Dimension size = new Dimension(10, 10);
        for (int y = 0; y < rows; y++) {
            JPanel panel = new JPanel();
            content.add(panel);
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            for (int x = 0; x < NUMBER_OF_COLUMNS; x++) {
                if (index >= n) {
                    // Add blank component
                    JComponent blank = new JComponent() {
                        @Override public Dimension getPreferredSize() {
                            return size;
                        }
                    };
                    panel.add(blank);
                } else {
                    JShapeButton shapeButton = new JShapeButton(shapes[index], paint);
                    if (index == previousIndex) {
                        shapeButton.setDrawBorder(true);
                    }
                    shapeButton.setPreferredSize(size);
                    final int i = index;
                    shapeButton.setActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            shapeSelected(i);
                        }
                    });
                    panel.add(shapeButton);
                }
                index++;
            }
        }
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void shapeSelected(int index) {
        result = index;
        dispose();
    }

    public int getResult() {
        return result;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test JDialog");
        frame.pack();
        frame.setVisible(true);
        JShapeDialog dialog = new JShapeDialog(frame, "Choose a Shape", PanelVisualProperties.AVAILABLE_SHAPES, Color.BLUE, 5);
        dialog.pack();
        dialog.setSize(400, 400);
        dialog.setVisible(true);
        int index = dialog.getResult();
        System.out.println("Shape: " + index + " selected.");
    }
}
