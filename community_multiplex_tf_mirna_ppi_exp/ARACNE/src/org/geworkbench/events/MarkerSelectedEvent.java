package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id: MarkerSelectedEvent.java,v 1.1.1.1 2005/07/28 22:36:26 watkin Exp $
 */

public class MarkerSelectedEvent extends Event {

    private DSGeneMarker marker;

    public MarkerSelectedEvent(DSGeneMarker m) {
        super(null);
        marker = m;
    }

    public DSGeneMarker getMarker() {
        return marker;
    }
}
