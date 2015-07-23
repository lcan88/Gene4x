package org.geworkbench.bison.datastructure.bioobjects.sequence;

import org.geworkbench.bison.datastructure.biocollections.DSAncillaryDataSet;

import java.io.Serializable;
import java.util.RandomAccess;

/**
 * @author John Watkinson
 */
public interface DSAlignmentResultSet extends RandomAccess, Cloneable, DSAncillaryDataSet, Serializable {

    String getResultFilePath();
}
