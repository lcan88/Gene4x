package org.geworkbench.util.sequences;

import java.awt.*;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JViewport;

import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import org.geworkbench.util.patterns.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SequenceViewWidgetPanel extends JPanel {
    final int xOff = 60;
    final int yOff = 20;
    final int xStep = 5;
    final int yStep = 12;
    double scale = 1.0;
    int maxLen = 1;
    int maxSeqLen = 1;
    //ArrayList  selectedPatterns   = null;
    DSCollection<DSMatchedPattern<DSSequence,
            DSSeqRegistration>> selectedPatterns = null;
    DSSequenceSet sequenceDB = null;
    boolean showAll = false;

    public SequenceViewWidgetPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
    }

    private void setMaxLength() {
        if (selectedPatterns != null) {
            for (Iterator iter = selectedPatterns.iterator(); iter.hasNext(); ) {
                DSMatchedSeqPattern item = (DSMatchedSeqPattern) iter.next();
                maxLen = Math.max(maxLen, item.getMaxLength());
            }
        }
    }

    //public void initialize(ArrayList patterns, CSSequenceSet seqDB) {
    public void initialize(DSCollection<DSMatchedPattern<DSSequence,
                           DSSeqRegistration>> matches, DSSequenceSet seqDB) {
        selectedPatterns = matches;
        sequenceDB = seqDB;
        repaint();
    }

    public void paintComponent(Graphics g) {
        Font f = new Font("Courier New", Font.PLAIN, 10);

        if (sequenceDB != null) {
            int rowId = -1;
            int[] rows = {};
            //int maxLn = sequenceDB.getMaxLength();
            int seqNo = sequenceDB.getSequenceNo();
            if (sequenceDB.getSequenceNo() == 0) {
                if (selectedPatterns != null) {
                    for (int row = 0; row < selectedPatterns.size(); row++) {
                        DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                selectedPatterns.get(row);
                        if (pattern instanceof CSMatchedSeqPattern) {
                            CSMatchedSeqPattern pat = (CSMatchedSeqPattern)
                                    pattern;
                            if ((pat != null) && (pat.getSupport() > 0)) {
                                seqNo = Math.max(seqNo,
                                                 pat.getId(pat.getSupport() - 1));
                            }
                        }
                    }
                }
            }
            scale = Math.min(5.0,
                             (double) (this.getWidth() - 20 - xOff) /
                             (double) maxSeqLen);
           // System.out.println("IN SVWPanel: " + scale + maxSeqLen);
            g.clearRect(0, 0, getWidth(), getHeight());
            // draw the patterns
            g.setFont(f);
            JViewport scroller = (JViewport)this.getParent();
            Rectangle r = new Rectangle();
            r = scroller.getViewRect();
            if ((rows.length == 1) && showAll && (selectedPatterns != null)) {
                int patId = rows[0];
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                              selectedPatterns.get(patId);
                if (pattern instanceof CSMatchedSeqPattern ||
                        pattern instanceof CSMatchedHMMSeqPattern) {
                    CSMatchedSeqPattern pat = (CSMatchedSeqPattern) pattern;
                    int lastSeqId = -1;
                    for (int locusId = 0; locusId < pat.getSupport(); locusId++) {
                        int seqId = pat.getId(locusId);
                        if (seqId > lastSeqId) {
                            rowId++;
                            drawSequence(g, rowId, seqId, maxSeqLen);
                            lastSeqId = seqId;
                        }
                        drawPattern(g, rowId, locusId, pat, r,
                                    PatternOperations.getPatternColor(pat.
                                hashCode()));
                    }
                } else if (pattern instanceof FlexiblePattern) {
                    FlexiblePattern fp = (FlexiblePattern) pattern;
                    int lastSeqId = -1;
                    Iterator it = fp.mLocus.iterator();
                    while (it.hasNext()) {
                        FlexiblePattern.TwoLocus tl = (FlexiblePattern.TwoLocus)
                                it.next();
                        int seqId = tl.seqId;
                        rowId++;
                        if (seqId > lastSeqId) {
                            drawSequence(g, rowId, seqId, maxSeqLen);
                            lastSeqId = seqId;
                        }
                        CSMatchedSeqPattern p0 = (CSMatchedSeqPattern) fp.
                                                 patterns.get(0);
                        CSMatchedSeqPattern p1 = (CSMatchedSeqPattern) fp.
                                                 patterns.get(1);
                        drawFlexiPattern(g, rowId, tl.dx0, p0, r,
                                         PatternOperations.getPatternColor(p0.
                                hashCode()));
                        drawFlexiPattern(g, rowId, tl.dx1, p1, r,
                                         PatternOperations.getPatternColor(p1.
                                hashCode()));
                    }
                }
            } else {
                for (int seqId = 0; seqId < seqNo; seqId++) {
                    rowId++;
                    drawSequence(g, seqId, seqId, maxSeqLen);
                }
                if (selectedPatterns != null) {
                    for (int row = 0; row < selectedPatterns.size(); row++) {
                        DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                selectedPatterns.get(row);
                        if (pattern != null) {
                            if (pattern.getClass().isAssignableFrom(
                                    CSMatchedSeqPattern.class) ||
                                pattern.getClass().isAssignableFrom(
                                    CSMatchedHMMSeqPattern.class) ) {
                                CSMatchedSeqPattern pat = (CSMatchedSeqPattern)
                                        pattern;
                                if (pattern != null) {
                                    for (int locusId = 0;
                                            locusId < pattern.getSupport();
                                            locusId++) {
                                        int seqId = pat.getId(locusId);

                                        if (showAll) {
                                            int newIndex[] = sequenceDB.
                                                getMatchIndex();
//                                            System.out.println(newIndex + " is null? in svwp");
                                            if (newIndex != null &&
                                                newIndex[seqId] != -1) {

                                                if (drawPattern(g,
                                                    newIndex[seqId],
                                                    locusId, pat,
                                                    r,
                                                    PatternOperations.
                                                    getPatternColor(
                                                    row))) {
                                                    break;
                                                }
                                            }
                                            else {
                                                System.out.println(
                                                    "Something wrong here" +
                                                    locusId);
                                            }

                                        }
                                        else {
                                            if (drawPattern(g, seqId, locusId,
                                                pat,
                                                r,
                                                PatternOperations.
                                                getPatternColor(
                                                row))) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int maxY = (rowId + 1) * yStep + yOff;
            setPreferredSize(new Dimension(this.getWidth() - yOff, maxY));
            revalidate();
        }
    }

    void setShowAll(boolean all) {
        showAll = all;
    }

    public void setMaxSeqLen(int maxSeqLen) {
        this.maxSeqLen = maxSeqLen;
    }

    void drawSequence(Graphics g, int rowId, int seqId, double len) {
        String lab = ">seq " + seqId;
        if (sequenceDB.getSequenceNo() > 0) {
            DSSequence theSequence = sequenceDB.getSequence(seqId);
            len = (double) theSequence.length();
            lab = theSequence.getLabel();

        }
        int y = yOff + rowId * yStep;
        int x = xOff + (int) (len * scale);
        g.setColor(Color.black);
        if (lab.length() > 10) {
            g.drawString(lab.substring(0, 10), 4, y + 3);
        } else {
            g.drawString(lab, 4, y + 3);
        }
        g.drawLine(xOff, y, x, y);
    }

    boolean drawPattern(Graphics g, int rowId, int locusId,
                        CSMatchedSeqPattern pat, Rectangle r, Color color) {
        int y = yOff + rowId * yStep;
        if (y > r.y) {
            if (y > r.y + r.height) {
                return true;
            }
            double x0 = pat instanceof CSMatchedHMMSeqPattern ?
                        ((CSMatchedHMMSeqPattern) pat).getStart(locusId) :
                        (double) pat.getOffset(locusId);
            double dx = pat instanceof CSMatchedHMMSeqPattern ?
                        (((CSMatchedHMMSeqPattern) pat).getEnd(locusId) - x0) :
                        pat.getExtent();
            int xa = xOff + (int) (x0 * scale) + 1;
            int xb = xa + (int) (dx * scale) - 1;
            g.setColor(color);
            g.draw3DRect(xa, y - 2, xb - xa, 4, false);
        }
        return false;
    }

    boolean drawFlexiPattern(Graphics g, int rowId, double x0,
                             CSMatchedSeqPattern pat, Rectangle r, Color color) {
        int y = yOff + rowId * yStep;
        if (y > r.y) {
            if (y > r.y + r.height) {
                return true;
            }
            double dx = pat.getExtent();
            int xa = xOff + (int) (x0 * scale) + 1;
            int xb = xa + (int) (dx * scale) - 1;
            g.setColor(color);
            g.draw3DRect(xa, y - 2, xb - xa, 4, false);
        }
        return false;
    }

    public int getMaxSeqLen() {
        return maxSeqLen;
    }
}
