package org.geworkbench.util.visualproperties;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.jfree.chart.ChartColor;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Keeps track of the visual display properties of panels.
 *
 * @author John Watkinson
 */
public class PanelVisualProperties {

    public static Shape[] AVAILABLE_SHAPES = {new Rectangle2D.Double(-3, -3, 6, 6), new Ellipse2D.Double(-3, -3, 6, 6), new Polygon(new int[]{0, 3, -3}, new int[]{-3, 3, 3}, 3), new Polygon(new int[]{0, 3, 0, -3}, new int[]{-3, 0, 3, 0}, 4), new Polygon(new int[]{-3, -3, -1, -1, 1, 1, 3, 3, 1, 1, -1, -1}, new int[]{-1, 1, 1, 3, 3, 1, 1, -1, -1, -3, -3, -1}, 12), new Polygon(new int[]{-3, 3, 0}, new int[]{-3, -3, 3}, 3), new Ellipse2D.Double(-3, -1.5, 6, 3), new Polygon(new int[]{-3, 3, -3}, new int[]{-3, 0, 3}, 3), new Rectangle2D.Double(-1.5, -3, 3, 6), new Polygon(new int[]{-3, 3, 3}, new int[]{0, -3, 3}, 3), new Rectangle2D.Double(-3, -1.5, 6, 3), new Ellipse2D.Double(-1.5, -3, 3, 6), };

    public static Paint[] DEFAULT_PAINTS = ChartColor.createDefaultPaintArray();

    private int shapeIndex;
    private Color color;

    public PanelVisualProperties(int shapeIndex, Color paint) {
        this.shapeIndex = shapeIndex;
        this.color = paint;
    }

    public int getShapeIndex() {
        return shapeIndex;
    }

    public void setShapeIndex(int shape) {
        this.shapeIndex = shape;
    }

    public Shape getShape() {
        return AVAILABLE_SHAPES[shapeIndex];
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override public String toString() {
        return new ToStringBuilder(this).append(color).append(shapeIndex).toString();
    }
}
