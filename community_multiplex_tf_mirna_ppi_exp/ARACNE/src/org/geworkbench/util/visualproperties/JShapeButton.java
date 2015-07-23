package org.geworkbench.util.visualproperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * A component that displays a shape and allows the user to click on it.
 *
 * @author John Watkinson
 */
public class JShapeButton extends JComponent {

    private static final float STROKE_WIDTH = 2f;


    private Shape shape;
    private Paint paint;
    private ActionListener listener;
    private int percentSize = 80;
    private boolean drawBorder = false;
    private Dimension preferredSize;

    public JShapeButton(Shape shape, Paint paint) {
        this.shape = shape;
        this.paint = paint;
        addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                if (listener != null) {
                    listener.actionPerformed(new ActionEvent(this, 0, ""));
                }
            }
        });
    }

    public void setActionListener(ActionListener listener) {
        this.listener = listener;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setPercentSize(int percentSize) {
        this.percentSize = percentSize;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension size = getSize();
        Rectangle2D shapeBounds = shape.getBounds2D();
        double widthFactor = size.width * percentSize / 100 / shapeBounds.getWidth();
        double heightFactor = size.height * percentSize / 100 / shapeBounds.getHeight();
        double factor = widthFactor;
        if (widthFactor > heightFactor) {
            factor = heightFactor;
        }
        int centerX = size.width / 2;
        int centerY = size.height / 2;
        if (drawBorder) {
            g.setStroke(new BasicStroke(2 * STROKE_WIDTH));
            g.drawRect(0, 0, size.width, size.height);
        }
        g.translate(centerX, centerY);
        if (widthFactor < heightFactor) {
            g.scale(widthFactor, widthFactor);
        } else {
            g.scale(heightFactor, heightFactor);
        }
        g.setStroke(new BasicStroke((float) (STROKE_WIDTH / factor)));
        g.setPaint(paint);
        g.fill(shape);
        g.setPaint(Color.black);
        g.draw(shape);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("JShapeButton Test");
        Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);
        final JShapeButton shapeButton = new JShapeButton(shape, Color.red);
        shapeButton.setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shapeButton.setDrawBorder(!shapeButton.isDrawBorder());
                shapeButton.repaint();
            }
        });
        frame.getContentPane().add(shapeButton);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setVisible(true);
    }
}
