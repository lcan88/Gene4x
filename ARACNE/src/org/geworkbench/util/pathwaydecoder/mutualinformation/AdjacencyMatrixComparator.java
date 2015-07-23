package org.geworkbench.util.pathwaydecoder.mutualinformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AdjacencyMatrixComparator {
    int numTrueConnections = 0;
    int numRecoveredConnections = 0;
    int numFalseConnections = 0;

    public int getNumTrueConnections() {
        return numTrueConnections;
    }

    public int getNumRecoveredConnections() {
        return numRecoveredConnections;
    }

    public void setNumFalseConnections(int numFalseConnections) {
        this.numFalseConnections = numFalseConnections;
    }

    public void setNumTrueConnections(int numTrueConnections) {
        this.numTrueConnections = numTrueConnections;
    }

    public void setNumRecoveredConnections(int numRecoveredConnections) {
        this.numRecoveredConnections = numRecoveredConnections;
    }

    public int getNumFalseConnections() {
        return numFalseConnections;
    }

    public AdjacencyMatrixComparator() {
    }

    /**
     * Uses adj1 as the true matrix and calculates how well adj2 is constructed based
     * on adj1
     *
     * @param adj1 AdjacencyMatrix
     * @param adj2 AdjacencyMatrix
     * @return HashMap
     */
    public void compare(AdjacencyMatrix adj1, AdjacencyMatrix adj2, double miThresh) {
        numTrueConnections = 0;
        numRecoveredConnections = 0;
        numFalseConnections = 0;

        int size = adj1.size();

        Set keys = adj1.getKeys();

        Iterator keyIt = keys.iterator();
        while (keyIt.hasNext()) {
            Object key = keyIt.next();

            HashMap adj1Row = (HashMap) adj1.getValue(key);
            adj1Row = makeTriangular(((Integer) key).intValue(), adj1Row);
            HashMap adj2Row = (HashMap) adj2.getValue(key);
            if (null != adj2Row) {
                adj2Row = makeTriangular(((Integer) key).intValue(), adj2Row);
            }

            numTrueConnections += adj1Row.size();
            //Test how many connections were recovered
            Set adj1RowKeys = adj1Row.keySet();
            Iterator adj1RowKeysIt = adj1RowKeys.iterator();
            while (adj1RowKeysIt.hasNext()) {
                Object adj1RowKey = adj1RowKeysIt.next();
                if (adj2Row != null) {
                    Float adj2RowMI = (Float) adj2Row.get(adj1RowKey);
                    if (null == adj2RowMI || adj2RowMI.floatValue() < miThresh) {

                    } else {
                        numRecoveredConnections++;
                    }
                }
            }

            //Test how many connections are wrong
            if (adj2Row != null) {
                Set adj2RowKeys = adj2Row.keySet();
                Iterator adj2RowKeysIt = adj2RowKeys.iterator();
                while (adj2RowKeysIt.hasNext()) {
                    Object adj2RowKey = adj2RowKeysIt.next();

                    Float adj2RowMI = (Float) adj2Row.get(adj2RowKey);
                    Float adj1RowMI = (Float) adj1Row.get(adj2RowKey);
                    if (null == adj1RowMI && adj2RowMI.floatValue() >= miThresh) {
                        numFalseConnections++;
                    } else {
                    }
                }
            }
        }
    }

    HashMap makeTriangular(int rowIndex, HashMap row) {
        HashMap newRow = new HashMap();

        Set rowKeys = row.keySet();
        Iterator rowKeysIt = rowKeys.iterator();
        while (rowKeysIt.hasNext()) {
            Object rowKey = rowKeysIt.next();
            int rowKeyIndex = ((Integer) rowKey).intValue();
            if (rowKeyIndex > rowIndex) {
                newRow.put(rowKey, row.get(rowKey));
            }
        }

        return newRow;
    }
}
