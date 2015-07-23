package org.geworkbench.bison.datastructure.biocollections.views;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public interface DSMatrixDataSetView <Q extends DSBioObject> extends DSDataSetView<Q> {
    public double[] getRow(int index);

    public double getValue(int markerIndex, int arrayIndex);
}
