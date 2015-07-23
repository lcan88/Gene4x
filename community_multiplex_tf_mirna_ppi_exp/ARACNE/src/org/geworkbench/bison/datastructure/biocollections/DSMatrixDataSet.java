package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 * <p/>
 * Represents a data set that is organized in a two-dimensional matrix of double values.
 *
 * @author Adam Margolin
 * @version 3.0
 */
public interface DSMatrixDataSet <T extends DSBioObject> {

    /**
     * Gets a value by row and column.
     *
     * @param row
     * @param col
     * @return the value at row x col.
     */
    public double getValue(int row, int col);

    /**
     * Returns a row of the matrix data set.
     * Changing the values in the array has no effect on the underlying data of this data set.
     * @param index the row number.
     * @return an of array of the values for that row.
     */
    public double[] getRow(int index);
}
