package org.geworkbench.util;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;

/**
 * @author John Watkinson
 */
public class FastMatrixModel {

    public enum Metric {
        GENE,
        MICROARRAY
    }

    public static double[][] getMatrix(DSMicroarraySetView view, Metric metric) {
        switch (metric) {
            case GENE:
                {
                    int rows = view.markers().size();
                    int cols = view.items().size();
                    double[][] data = new double[rows][cols];
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            data[i][j] = view.getValue(i, j);
                        }
                    }
                    return data;
                }
            case MICROARRAY:
                {
                    int rows = view.items().size();
                    int cols = view.markers().size();
                    double[][] data = new double[rows][cols];
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            data[i][j] = view.getValue(j, i);
                        }
                    }
                    return data;
                }
        }
        return null;
    }
}
