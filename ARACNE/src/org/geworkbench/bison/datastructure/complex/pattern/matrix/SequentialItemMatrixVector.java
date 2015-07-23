package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import cern.colt.matrix.DoubleMatrix2D;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class SequentialItemMatrixVector <T extends DSNamed> {
    Vector<DoubleMatrix2D> matrices = new Vector<DoubleMatrix2D>();
    protected DSItemList<T> markerVector;

    public SequentialItemMatrixVector() {
    }

    public DoubleMatrix2D get(int index) {
        return matrices.get(index);
    }

    public DSItemList<T> getMarkerVector() {
        return markerVector;
    }

    public Vector<DoubleMatrix2D> getMatrices() {
        return matrices;
    }

    public void setMarkerVector(DSItemList markerVector) {
        this.markerVector = markerVector;
    }

    public void setMatrices(Vector<DoubleMatrix2D> matrices) {
        this.matrices = matrices;
    }

}
