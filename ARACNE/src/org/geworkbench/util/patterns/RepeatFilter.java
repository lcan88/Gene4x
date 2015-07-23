package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class RepeatFilter extends JPanel implements PatternFilter {
    JLabel jLabel1 = new JLabel();
    JTextField jTextField1 = new JTextField();

    public RepeatFilter() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JPanel getParameterPanel() {
        return this;
    }

    boolean isPeriodic(int dx, CSMatchedSeqPattern p, int l) {
        for (int x = 0; x < dx; x++) {
            char c = p.ascii.charAt(x);
            int n = 1;
            boolean repeat = true;
            while (x + n * dx < l) {
                if (c != p.ascii.charAt(x + n * dx)) {
                    return false;
                }
                n++;
            }
        }
        return true;
    }

    public ArrayList filter(ArrayList patterns, DSSequenceSet sequenceDB) {
        ArrayList filteredPatterns = new ArrayList();
        Iterator it = patterns.iterator();
        int period = Integer.parseInt(jTextField1.getText());
        while (it.hasNext()) {
            org.geworkbench.util.patterns.CSMatchedSeqPattern p = (CSMatchedSeqPattern) it.next();
            if (p.ascii == null) {
                PatternOperations.fill(p, sequenceDB);
            }
            int l = p.ascii.length();
            boolean periodic = false;
            for (int dx = 1; dx < period; dx++) {
                if (isPeriodic(dx, p, l)) {
                    periodic = true;
                    break;
                }
            }
            if (!periodic) {
                filteredPatterns.add(p);
            }
        }
        return filteredPatterns;
    }

    private void jbInit() throws Exception {
        jLabel1.setText("Period:");
        jTextField1.setPreferredSize(new Dimension(30, 21));
        jTextField1.setText("3");
        this.add(jLabel1, null);
        this.add(jTextField1, null);
    }
}
