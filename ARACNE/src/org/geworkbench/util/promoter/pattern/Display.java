package org.geworkbench.util.promoter.pattern;

import java.awt.*;

public class Display {
    //eventually these should be replaced by something more informative such as a shape object.
    public static final int RECTANGLE = 0;
    public static final int ROUNDRECT = 1;
    public static final int RECT3D = 2;
    public static final int OVAL = 3;
    Color cl = null;
    double height = 0;
    int length = 0;
    int shape = 0;

    public Color getColor() {
        return cl;
    }

    public void setColor(Color color) {
        cl = color;

    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }
}
