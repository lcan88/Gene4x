package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Range;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public interface DSRangeMarker {

    Range getRange();

    /**
     * Checks if the designated IMarker belongs to a microarray that is
     * part of the "Cases" criteria.
     */
    void check(DSMutableMarkerValue marker, boolean isCase);

    // Resets the number of Cases (phNo) and Controls (bkNo).
    void reset(int id, int phNo, int bkNo);
}
