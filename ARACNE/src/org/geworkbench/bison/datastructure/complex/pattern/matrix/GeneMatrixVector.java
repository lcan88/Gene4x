package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

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
public class GeneMatrixVector extends SequentialItemMatrixVector<DSGeneMarker> {
    public GeneMatrixVector() {
        super();
        markerVector = new CSMarkerVector();
    }
}
