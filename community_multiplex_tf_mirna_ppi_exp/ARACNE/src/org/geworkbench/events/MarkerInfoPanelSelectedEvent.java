package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.EventSource;

import java.util.ArrayList;

public class MarkerInfoPanelSelectedEvent {
    //  /IMarkerInfoPanel panel;
    ArrayList panel;

    public MarkerInfoPanelSelectedEvent(EventSource source, DSPanel<DSGeneMarker> im) {
    }

    public MarkerInfoPanelSelectedEvent(EventSource source, ArrayList imi) {
        panel = imi;
    }

    public ArrayList getMarkerInfoPanel() {
        return panel;
    }
}
