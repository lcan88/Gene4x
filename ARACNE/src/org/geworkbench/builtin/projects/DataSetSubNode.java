package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */

public class DataSetSubNode extends ProjectTreeNode {
    public DSAncillaryDataSet _aDataSet = null;

    public DataSetSubNode(DSAncillaryDataSet ads) {
        _aDataSet = ads;
        super.setUserObject(ads);
    }
}
