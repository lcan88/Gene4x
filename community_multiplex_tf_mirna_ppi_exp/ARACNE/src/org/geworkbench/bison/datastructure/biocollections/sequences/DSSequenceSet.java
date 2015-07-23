package org.geworkbench.bison.datastructure.biocollections.sequences;

import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;

import java.io.Serializable;
import java.util.RandomAccess;

/**
 * @author John Watkinson
 */
public interface DSSequenceSet<T extends DSSequence> extends RandomAccess, Cloneable, Serializable, DSDataSet<T>, DSCollection<T> {

    void addASequence(T sequence);

    int getSequenceNo();

    T getSequence(int i);

    int getMaxLength();

    boolean isDNA();

    void setMatchIndex(int[] matchIndex);

    void setReverseIndex(int[] reverseIndex);

    DSItemList<? extends DSGeneMarker> getMarkerList();

    int[] getMatchIndex();

    int[] getReverseIndex();

    DSSequenceSet createSubSetSequenceDB(boolean[] included);
}
