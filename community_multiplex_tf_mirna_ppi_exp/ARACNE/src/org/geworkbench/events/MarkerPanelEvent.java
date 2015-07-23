package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MarkerPanelEvent extends Event {

    private DSPanel<DSGeneMarker> panels = null;

    public MarkerPanelEvent(DSPanel<DSGeneMarker> panels) {
        super(null);
        this.panels = panels;
    }

    public DSPanel<DSGeneMarker> getPanels() {
        return panels;
    }

}

