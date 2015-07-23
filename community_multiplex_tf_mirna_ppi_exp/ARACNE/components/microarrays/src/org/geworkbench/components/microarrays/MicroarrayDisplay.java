package org.geworkbench.components.microarrays;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;
import org.geworkbench.util.microarrayutils.MicroarrayVisualizer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.Vector;

/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class MicroarrayDisplay extends JPanel {
    DSMicroarray microarray = null;
    BorderLayout borderLayout1 = new BorderLayout();
    protected int dx = 0;
    protected int dy = 0;
    protected int wx = 0;
    protected int wy = 0;
    protected int selectedGeneId = -1; ///selectedGeneId is not used anywhere??
    protected double scaleX = 1;
    protected double scaleY = 1;
    protected int sel = 0;
    protected int cols = 0;
    protected int rows = 0;
    protected int selRow = -1;
    protected int selCol = -1;
    protected boolean showValidOnly = false;
    protected Vector patterns = new Vector();
    protected Stack masks = new Stack();
    protected MicroarrayVisualizer microarrayVisualizer = null;
    protected int[] patternGenes = null;
    protected char[] maskedGenes = null;
    protected char[] graphedGenes = null;
    protected float intensity = 1f;

    TitledBorder titledBorder1;

    public MicroarrayDisplay(org.geworkbench.util.microarrayutils.MicroarrayVisualizer visualizer) {

        microarrayVisualizer = visualizer;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void setMicroarray(DSMicroarray microarray) {

        this.microarray = microarray;
        if (microarray != null) {
            int maxMarkerNo = microarray.getMarkerNo();
            if (maxMarkerNo > 0) {
                patternGenes = new int[maxMarkerNo];
                maskedGenes = new char[maxMarkerNo];
                graphedGenes = new char[maxMarkerNo];
            }
        } else {
            patternGenes = null;
            maskedGenes = graphedGenes = null;
        }
    }

    void setMicroarrays(DSMicroarraySet microarrays) {
        rows = 0;
        cols = 0;
        if (microarrays != null) {
            int maxMarkerNo = microarrays.size();
            if (maxMarkerNo > 0) {
                rows = (int) Math.ceil(Math.sqrt((double) microarrayVisualizer.getUniqueMarkers().size()));
                cols = rows;
                patternGenes = new int[maxMarkerNo];
                maskedGenes = new char[maxMarkerNo];
                graphedGenes = new char[maxMarkerNo];
            }
        } else {
            patternGenes = null;
            maskedGenes = graphedGenes = null;
        }
        if ((rows == 0) || (cols == 0)) {
            patternGenes = null;
            maskedGenes = null;
            graphedGenes = null;
        }
    }

    void jbInit() throws Exception {
        titledBorder1 = new TitledBorder("");
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setLayout(borderLayout1);
    }

    protected void computeScale() {
        Rectangle r = this.getBounds();
        Insets i = this.getInsets();
        r.grow(-i.left, -i.top);
        scaleX = (double) r.width / (double) cols;
        scaleY = (double) r.height / (double) rows;
        dx = r.x;
        dy = r.y;
        wx = r.width;
        wy = r.height;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (microarray != null) {
            rows = (int) Math.ceil(Math.sqrt((double) microarrayVisualizer.getUniqueMarkers().size()));
            cols = rows;
            float[] hsb = new float[3];
            computeScale();
//            Image memImg = createImage();
//            currentImage = memImg;
//            g.drawImage(memImg, dx, dy, wx, wy, null);
            drawImage((Graphics2D) g);
            g.setColor(Color.black);
        }
    }

    public Image getCurrentImage() {
        Image image = new BufferedImage(wx - dx, wy - dy, BufferedImage.TYPE_INT_RGB);
        Graphics2D ig = (Graphics2D) image.getGraphics();
        ig.setColor(Color.white);
        ig.fillRect(0,0,wx-dx, wy-dy);

        drawImage(ig);

        return image;
    }

    protected void drawImage(Graphics2D g) {
        int geneNo = microarrayVisualizer.getUniqueMarkers().size();
        int geneId = 0;
        DSDataSet maSet = microarrayVisualizer.getDataSetView().getDataSet();
        if (maSet != null) {
            org.geworkbench.bison.util.colorcontext.ColorContext colorContext = (org.geworkbench.bison.util.colorcontext.ColorContext) maSet.getObject(org.geworkbench.bison.util.colorcontext.ColorContext.class);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (geneId < geneNo) {
                        DSGeneMarker stats = microarrayVisualizer.getUniqueMarkers().get(geneId);
                        //this place should get the right marker with the selecte markers or all markers


                        //this is the markerid of the whole microarryset
                        int markerId = stats.getSerial();
                        if (patternGenes != null  && markerId >= 0 && markerId < patternGenes.length) {
                            DSMutableMarkerValue spot = (DSMutableMarkerValue) microarray.getMarkerValue(stats.getSerial());

                            if ((spot == null) || (spot.isMissing())) {
                                g.setColor(Color.yellow);
                            } else if (spot.isMasked()) {
                                g.setColor(Color.white);
                            } else if (!showValidOnly || (!spot.isMissing())) {
                                g.setColor(colorContext.getMarkerValueColor(spot, stats, intensity));
                                //pixels[geneId] = spot.getAbsColor(stats, 1.0F).getRGB();
                            } else {
                                g.setColor(Color.lightGray);
                            }
                            int x0 = dx + (int) ((double) col * scaleX);
                            int x1 = dx + (int) ((double) (col + 1) * scaleX);
                            int y0 = dy + (int) ((double) row * scaleY);
                            int y1 = dy + (int) ((double) (row + 1) * scaleY);

                            g.fillRect(x0, y0, x1 - x0, y1 - y0);

                            boolean needOutline = true;
                            if (patternGenes[markerId] > 0) {
                                g.setColor(Color.blue);
                            } else if (maskedGenes[markerId] > 0) {
                                g.setColor(Color.orange);
                            } else if (graphedGenes[geneId] == '1') {
                                g.setColor(Color.green);
                            } else {
                                needOutline = false;
                            }
                            if (needOutline) {
                                g.setStroke(new BasicStroke(3));
                                g.drawRect(x0, y0, x1 - x0 - 1, y1 - y0 - 1);
                                g.setStroke(new BasicStroke(1));
                            }

                            geneId++;
                        }
                    }
                }
            }
        }
    }

    protected int getCol(int geneId) {
        return geneId % cols;
    }

    protected int getRow(int geneId) {
        return geneId / cols;
    }

    protected int getGeneIdAndRubberBand(int x, int y) {
        int geneId = -1;
        if ((scaleX > 0) && (scaleY > 0)) {
            int row = (int) (y / scaleY);
            int col = (int) (x / scaleX);
            rubberBandBox(row, col);
            geneId = row * cols + col;
            if ((geneId < 0) || (geneId >= microarrayVisualizer.getUniqueMarkers().size())) {
                return -1;
            }
        }
        return microarrayVisualizer.getUniqueMarkers().get(geneId).getSerial();
    }

    protected void rubberBandBox(int row, int col) {
        int x0, x1, y0, y1;
        if ((col != selCol) || (row != selRow)) {
            Graphics g = getGraphics();
            if ((selCol != -1) && (selRow != -1)) {
                x0 = dx + (int) ((double) selCol * scaleX);
                x1 = dx + (int) ((double) (selCol + 1) * scaleX);
                y0 = dy + (int) ((double) selRow * scaleY);
                y1 = dy + (int) ((double) (selRow + 1) * scaleY);
                g.setXORMode(Color.black);
                g.setColor(Color.white);
                g.drawRect(x0, y0, x1 - x0, y1 - y0);
            }
            if ((col != -1) && (row != -1)) {
                x0 = dx + (int) ((double) col * scaleX);
                x1 = dx + (int) ((double) (col + 1) * scaleX);
                y0 = dy + (int) ((double) row * scaleY);
                y1 = dy + (int) ((double) (row + 1) * scaleY);
                g.setXORMode(Color.black);
                g.setColor(Color.white);
                g.drawRect(x0, y0, x1 - x0, y1 - y0);
            }
            selCol = col;
            selRow = row;
        }
    }

    public DSMicroarray getMicroarray() {
        return microarray;
    }

    public void addPattern(CSMatchedMatrixPattern pattern) {
        if (patternGenes != null) {
            for (int i = 0; i < pattern.getPattern().markers().length; i++) {
                int geneId = pattern.getPattern().markers()[i].getSerial();
                patternGenes[geneId]++;
            }
        }
    }

    public void delPattern(CSMatchedMatrixPattern pattern) {
        if (patternGenes != null) {
            for (int i = 0; i < pattern.getPattern().markers().length; i++) {
                int geneId = pattern.getPattern().markers()[i].getSerial();
                patternGenes[geneId]--;
            }
        }
    }

    public void clearPatterns() {
        if (patternGenes != null) {
            for (int i = 0; i < patternGenes.length; i++) {
                patternGenes[i] = 0;
            }
        }
    }

    public void pushMask(CSMatchedMatrixPattern pattern) {
        if (maskedGenes != null) {
            masks.push(pattern);
            for (int i = 0; i < pattern.getPattern().markers().length; i++) {
                int geneId = pattern.getPattern().markers()[i].getSerial();
                maskedGenes[geneId]++;
            }
        }
    }

    public void popMask() {
        if (maskedGenes != null) {
            CSMatchedMatrixPattern pattern = (CSMatchedMatrixPattern) masks.pop();
            for (int i = 0; i < pattern.getPattern().markers().length; i++) {
                int geneId = pattern.getPattern().markers()[i].getSerial();
                if (maskedGenes[geneId] > 0) {
                    maskedGenes[geneId]--;
                }
            }
        }
    }

    public void maskGene(int geneId) {
        if (maskedGenes != null)
            maskedGenes[geneId]++;
    }

    public void unmaskGene(int geneId) {
        if (maskedGenes != null)
            maskedGenes[geneId] = 0;
    }

    public void graphGene(int geneId) {
        if (graphedGenes != null)
            graphedGenes[geneId] = '1';
    }

    public void ungraphGene(int geneId) {
        if (graphedGenes != null)
            graphedGenes[geneId] = '0';
    }

    public void clearMask() {
        if (maskedGenes != null)
            for (int i = 0; i < maskedGenes.length; i++) {
                maskedGenes[i] = 0;
            }
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
