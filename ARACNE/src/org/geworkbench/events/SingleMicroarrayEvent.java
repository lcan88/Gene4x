package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class SingleMicroarrayEvent extends Event {
    public SingleMicroarrayEvent(DSMicroarray array, String message) {
        super(null);
        microarray = array;
        value = message;
    }

    public DSMicroarray getMicroarray() {
        return microarray;
    }

    public String getMessage() {
        return value;
    }

    private DSMicroarray microarray = null;
    private String value = null;
}
