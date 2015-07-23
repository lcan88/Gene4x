package org.geworkbench.util.associationdiscovery.cluster;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) Andrea Califano</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class ClusterTableModel extends AbstractTableModel {
    String columnNames[] = {"S", "M", "ZS"};
    ArrayList<CSMatchedMatrixPattern> patterns = new ArrayList<CSMatchedMatrixPattern>();

    public final String getColumnName(int col) {
        return columnNames[col];
    }

    public int getRowCount() {
        if (patterns == null) {
            return 0;
        } else {
            return patterns.size();
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Class getColumnClass(int col) {
        switch (col) {
            case 0:
                return Integer.class;
                //break;
            case 1:
                return Integer.class;
                //break;
            case 2:
                return Double.class;
                //break;
        }
        return super.getColumnClass(col);
    }

    public CSMatchedMatrixPattern getPatternAt(int row) {
        return patterns.get(row);
    }

    public Object getValueAt(int row, int col) {
        CSMatchedMatrixPattern pt = patterns.get(row);
        switch (col) {
            case 0:
                if (pt.getClass() == CSMatchedMatrixPattern.class) {
                    return new Integer(pt.getSupport());
                } else {
                    return new String("[" + pt.getSupport() + "]");
                }
            case 1:
                return new Integer(pt.getPattern().markers().length);
            case 2:
                return new Double(pt.getPValue());
        }
        return new Integer(0);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void add(CSMatchedMatrixPattern value) {
        patterns.add(value);
        this.fireTableStructureChanged();
        fireTableRowsInserted(patterns.size(), patterns.size());
    }

    public void clear() {
        patterns.clear();
        fireTableDataChanged();
    }

    public ArrayList getPatterns() {
        return patterns;
    }
}
