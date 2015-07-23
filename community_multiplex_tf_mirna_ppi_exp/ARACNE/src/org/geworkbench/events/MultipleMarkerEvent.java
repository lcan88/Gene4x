package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MultipleMarkerEvent extends Event {

    public MultipleMarkerEvent(DSItemList<DSGeneMarker> mInfos, String message) {
        super(null);
        markerInfos = mInfos;
        value = message;
    }

    public DSItemList<DSGeneMarker> getMarkerInfos() {
        return markerInfos;
    }

    public String getMessage() {
        return value;
    }

    private DSItemList<DSGeneMarker> markerInfos = null;
    private String value = null;
}
