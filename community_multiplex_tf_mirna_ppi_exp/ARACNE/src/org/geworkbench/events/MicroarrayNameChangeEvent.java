package org.geworkbench.events;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MicroarrayNameChangeEvent extends Event {
    DSMicroarray mArray = null;

    public MicroarrayNameChangeEvent(EventSource source, DSMicroarray ma) {
        super(source);
        mArray = ma;
    }

    public DSMicroarray getMicroarray() {
        return mArray;
    }

}
