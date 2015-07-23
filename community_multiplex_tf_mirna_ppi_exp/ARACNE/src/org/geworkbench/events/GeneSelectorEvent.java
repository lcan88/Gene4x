package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version $Id: GeneSelectorEvent.java,v 1.1.1.1 2005/07/28 22:36:26 watkin Exp $
 */

public class GeneSelectorEvent extends Event {

    public static final int MARKER_SELECTION = 1;
    public static final int PANEL_SELECTION = 2;

    private DSPanel<DSGeneMarker> panel;
    private DSGeneMarker genericMarker;
    private int type;

    public GeneSelectorEvent(DSPanel<DSGeneMarker> p) {

        super(null);
        panel = p;

    }

    public GeneSelectorEvent(DSGeneMarker mi) {
        super(null);
        genericMarker = mi;

    }

    public DSPanel<DSGeneMarker> getPanel() {
        return panel;
    }

    public DSGeneMarker getGenericMarker() {
        return genericMarker;
    }
}
