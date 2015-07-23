package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class PhenotypePanelEvent extends Event {

    DSPanel<DSMicroarray> panels = null;

    public PhenotypePanelEvent(EventSource source, DSPanel<DSMicroarray> panels) {
        super(source);
        this.panels = panels;
    }

    public DSPanel<DSMicroarray> getPhenotypeCategories() {
        return panels;
    }

}
