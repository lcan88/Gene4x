package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Contains information about the change in the value of a marker within a
 * microarray set. The event stores both the old and the new value for the
 * affected marker.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class SingleValueEditEvent extends Event {

    DSMarkerValue newValue = null;
    DSDataSet maSet = null;

    public SingleValueEditEvent(DSDataSet mSet, DSMarkerValue nv) {
        super(null);
        maSet = mSet;
        newValue = nv;
    }

    public DSMarkerValue getNewValue() {
        return newValue;
    }

    public DSDataSet getReferenceMicroarraySet() {
        return maSet;
    }
}
