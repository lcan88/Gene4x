package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author John Watkinson
 */
public class CSSignificanceResultSet <T extends DSGeneMarker> extends CSAncillaryDataSet implements DSSignificanceResultSet<T> {

    private class SignificanceComparator implements Comparator<Integer> {

        public int compare(Integer x, Integer y) {
            double sigX = significance.get(panel.get(x));
            double sigY = significance.get(panel.get(y));
            if (sigX > sigY) {
                return 1;
            } else if (sigX < sigY) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    private HashMap<T, Double> significance;
    private double alpha;
    private String[][] labels = new String[2][];
    private DSPanel<T> panel;

    public CSSignificanceResultSet(DSMicroarraySet parent, String label, String[] caseLabels, String[] controlLabels, double alpha) {
        super(parent, label);
        this.alpha = alpha;
        significance = new HashMap<T, Double>();
        labels[0] = caseLabels;
        labels[1] = controlLabels;
        panel = new CSPanel<T>(label);
    }

    public File getDataSetFile() {
        // not needed
        return null;
    }

    public void setDataSetFile(File file) {
        // no-op
    }

    public Double getSignificance(T marker) {
        Double v = significance.get(marker);
        if (v == null) {
            return 1d;
        } else {
            return v;
        }
    }

    public void setSignificance(T marker, double value) {
        significance.put(marker, value);
        if (value < alpha) {
            panel.add(marker);
        }
    }

    public DSPanel<T> getSignificantMarkers() {
        return panel;
    }

    public double getCriticalPValue() {
        return alpha;
    }

    public String[] getLabels(int index) {
        return labels[index];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sortMarkersBySignificance() {
        int n = panel.size();
        ArrayList<Integer> indices = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            indices.add(i);
        }
        Collections.sort(indices, new SignificanceComparator());
        CSPanel<T> newPanel = new CSPanel<T>();
        for (int i = 0; i < n; i++) {
            newPanel.add(panel.get(indices.get(i)));
        }
        panel = newPanel;
    }

    public DSMicroarraySet getParentDataSet() {
        return (DSMicroarraySet) super.getParentDataSet();
    }
}
