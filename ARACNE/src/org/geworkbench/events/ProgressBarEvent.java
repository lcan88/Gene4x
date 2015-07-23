package org.geworkbench.events;

import java.awt.*;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: An event which indicates that the progress bar
 * need to be changed.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ProgressBarEvent {
    /**
     * Event types
     */
    public static final int INITIALIZE = 0;
    public static final int UPDATE = 1;
    private int eventType;
    private int min = 0;
    private int max = 100;
    private int percentComplete = 0;
    //Color of the progress bar.
    private Color color = Color.GRAY;
    //a message to display in the progress bar
    String message = "";

    /**
     * Constructor. min max value default to 0 100.
     *
     * @param message
     * @param color
     * @param percent
     */
    public ProgressBarEvent(String message, Color color, int percent) {
        this.percentComplete = percent;
        if (color != null) {
            this.color = color;
        }
        if (message != null) {
            this.message = message;
        }
    }

    /**
     * Constructor.
     *
     * @param min min value for the bar.
     * @param max max value for the bar.
     */
    public ProgressBarEvent(String message, Color color, int percent, int min, int max) {
        this(message, color, percent);

        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getType() {
        return eventType;
    }

    public void setMin(int min) {
        if (min < 0) {
            this.min = 0;
        }
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Sets the percentage of the computation.
     *
     * @param percentComplete the percent completed.
     */
    public void setPercentDone(int percent) {
        this.percentComplete = percent;
    }

    public int getPercentDone() {
        return percentComplete;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setColor(Color progressColor) {
        this.color = progressColor;
    }

    public Color getColor() {
        return color;
    }
}
