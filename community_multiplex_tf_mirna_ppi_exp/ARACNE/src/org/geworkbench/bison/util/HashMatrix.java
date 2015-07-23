package org.geworkbench.bison.util;

import java.util.HashMap;

public class HashMatrix {
    HashMap rowMap;
    HashMap columnIndices;

    public HashMatrix() {
    }

    public HashMatrix(HashMap rowMap, HashMap columnIndices) {
        this.rowMap = rowMap;
        this.columnIndices = columnIndices;
    }

    public Object get(Object rowKey, Object columnKey) {
        int columnIndex = new Integer(columnIndices.get(columnKey).toString()).intValue();
        Object[] row = (Object[]) rowMap.get(rowKey);
        return row[columnIndex];
    }

    public HashMatrix(String[][] data) {
        createHashMatrix(data);
    }

    void createHashMatrix(String[][] data) {
        String[] columnHeaders = data[0];

        columnIndices = new HashMap();
        rowMap = new HashMap();

        for (int i = 1; i < columnHeaders.length; i++) {
            columnIndices.put(columnHeaders[i], new Integer(i - 1));
        }

        for (int lineCtr = 1; lineCtr < data.length; lineCtr++) {
            String[] lineVals = data[lineCtr];
            if (lineVals.length > 0) {
                String[] vals = new String[lineVals.length - 1];
                for (int i = 1; i < lineVals.length; i++) {
                    vals[i - 1] = lineVals[i];
                }
                rowMap.put(new String(lineVals[0]), vals);
            }
        }
    }
}
