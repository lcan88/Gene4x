package org.geworkbench.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for a graphical display of progress in a process where maybe only the stopping condition is known, but not how
 * long or how many iterations it will take to get there.
 * User: mhall
 * Date: Jan 12, 2006
 * Time: 11:11:39 AM
 */
public class ProgressGraph extends JComponent {

    static Log log = LogFactory.getLog(ProgressGraph.class);

    String description = "";

    int ymax, ymin;
    int numPoints = 30;
    List<Integer> points;
    int bandMin = -1, bandMax = -1;

    public ProgressGraph(int ymin, int ymax, int numPoints) {
        this.ymax = ymax;
        this.ymin = ymin;
        this.numPoints = numPoints;
        this.points = new ArrayList<Integer>();
        setPreferredSize(new Dimension(numPoints * 2, 20));
    }

    public void addPoint(int value) {
        if (points.size() > numPoints) {
            points.remove(0);
        }
        if (value > ymax) {
            points.add(ymax);
        } else {
            points.add(value);
        }
    }

    public void clearPoints() {
        points.clear();
    }

    public void setBandRange(int min, int max) {
        bandMin = min;
        bandMax = max;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int getY(int dataY, float yscale) {
        return (int) (getHeight() - (dataY - ymin) * yscale);
    }

    private int getX(int xVal, float dx) {
        return (int) (xVal * dx);
    }

    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        float dx = (getWidth() + 0f) / points.size();
        float yscale = getHeight() / (ymax - ymin + 0f);

        //            var width = data.length * dx + 1;

        // Antialias everything. Makes the data a little fuzzier which
        // may not be the right thing to do.
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the image with the background color
        g.setPaint(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        // If a bandColor was specified, draw the band
        if (bandMin > -1 && bandMax > -1) {
            AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g.setComposite(myAlpha);
            g.setPaint(Color.blue);
            g.fillRect(0, getY(bandMax, yscale),
                    getWidth(), getY(bandMin, yscale) - getY(bandMax, yscale));
        }

        // Set the line color and width
        AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g.setComposite(myAlpha);

        g.setPaint(Color.black);
        g.setStroke(new BasicStroke(1));

        // Now build the line
        GeneralPath line = new GeneralPath();
        if (points.size() > 0) {
            line.moveTo(getX(0, dx), getY(points.get(0), yscale));
            for (int i = 1; i < points.size(); i++) {
//                log.debug("Line to "+getX(i, dx)+", "+getY(points.get(i), yscale));
                line.lineTo(getX(i, dx), getY(points.get(i), yscale));
            }
        }

        // And draw the line
        g.draw(line);

        // Write description if set
        if (!"".equals(description)) {
            // We want the message to be approximately correct for the height of the graph, so do some math here for that
            int testFontSize = 12;
            Font font = new Font("Arial", Font.PLAIN, testFontSize);
            LineMetrics metrics = font.getLineMetrics(description, g.getFontRenderContext());
            float heightAt12 = metrics.getHeight();
            float idealHeight = getHeight() / 2;
            int fontSize = Math.round((idealHeight / heightAt12) * testFontSize);
            font = new Font("Arial", Font.PLAIN, fontSize);
            metrics = font.getLineMetrics(description, g.getFontRenderContext());
//            log.debug("Using font size " + fontSize);
            g.setFont(font);

            myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            g.setComposite(myAlpha);

            g.drawString(description, 3, metrics.getHeight() - metrics.getDescent());
        }

        // If the dotColor was set, draw the dot
        if (points.size() > 0) {
            g.setPaint(Color.red);
            g.fillOval(getX(points.size() - 1, dx), getY(points.get(points.size() - 1), yscale), 3, 3);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(80,30));
        panel.setOpaque(true); //content panes must be opaque
        frame.setContentPane(panel);

        ProgressGraph graph = new ProgressGraph(0, 20, 20);
        graph.setBandRange(2,7);
        graph.setDescription("svm progress");
        graph.addPoint(10);
        graph.addPoint(11);
        graph.addPoint(12);
        graph.addPoint(3);
        graph.addPoint(15);
        graph.addPoint(19);
        graph.addPoint(6);
        graph.addPoint(15);
        graph.addPoint(10);
        graph.addPoint(10);
        graph.addPoint(11);
        graph.addPoint(12);
        graph.addPoint(3);
        graph.addPoint(15);
        graph.addPoint(19);
        graph.addPoint(6);

        panel.add(graph, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }
}
