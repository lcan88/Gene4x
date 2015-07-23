package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.util.network.GeneNetworkEdgeImpl;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

class IntersectionCellRenderer extends JLabel implements ListCellRenderer {
    public ArrayList listH = new ArrayList();
    public ArrayList listL = new ArrayList();
    public ArrayList listA = new ArrayList();
    public ArrayList listLH = new ArrayList();
    public ArrayList listLA = new ArrayList();
    public ArrayList listHA = new ArrayList();
    public ArrayList listLHA = new ArrayList();

    final DecimalFormat format = new DecimalFormat("###.#");

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.
    public IntersectionCellRenderer() {
        ;
    }

    public Component getListCellRendererComponent(JList list, Object value, // value to display
                                                  int index, // cell index
                                                  boolean isSelected, // is the cell selected
                                                  boolean cellHasFocus) { // the list and the cell have the focus

        if (value instanceof GeneNetworkEdgeImpl) {
            GeneNetworkEdgeImpl edge = (GeneNetworkEdgeImpl) value;
            String name = "";
            try {
                name = edge.toString();
                //String text = AffyDataManager.getInfo(edge.m2.getAccession(), 0)[0];
                //name = "[" + format.format(edge.pValue) + "] " + edge.m2.getAccession() + " " + text;
            } catch (Exception ex) {
                name = edge.toString();
                //name = "[" + format.format(edge.pValue) + "] " + edge.m2.getAccession() + " " + edge.m2.toString();
            }
            setText(name);
            if (listLHA.contains(value)) {
                boolean inL = listL.contains(value);
                boolean inH = listH.contains(value);
                boolean inA = listA.contains(value);
                if (inL && !inH && !inA) {
                    setForeground(Color.green);
                } else if (inH && !inL && !inA) {
                    setForeground(Color.red);
                } else if (inA && !inL && !inH) {
                    setForeground(Color.blue);
                } else if (inH && inL && !inA) {
                    setForeground(Color.magenta);
                } else if (inH && inA && !inL) {
                    setForeground(Color.pink);
                } else if (inL && inA && !inH) {
                    setForeground(Color.orange);
                } else {
                    setForeground(Color.black);
                }
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(list.getBackground());
            }
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

    public void clear() {
        listL.clear();
        listH.clear();
        listA.clear();
        listHA.clear();
        listLA.clear();
        listLH.clear();
        listLHA.clear();
    }

    public void sort() {
        Collections.sort(listL);
        Collections.sort(listH);
        Collections.sort(listA);
        Collections.sort(listLH);
        Collections.sort(listLA);
        Collections.sort(listHA);
        Collections.sort(listLHA);
    }
}
