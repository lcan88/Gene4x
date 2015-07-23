package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.engine.config.events.Event;

/**
 * The event for the selection of a single phenotype.
 *
 * @author John Watkinson
 */
public class PhenotypeSelectedEvent extends Event {

    DSBioObject object;

    public PhenotypeSelectedEvent(DSBioObject object) {
        super(null);
        this.object = object;
    }

    public DSBioObject getObject() {
        return object;
    }
}
