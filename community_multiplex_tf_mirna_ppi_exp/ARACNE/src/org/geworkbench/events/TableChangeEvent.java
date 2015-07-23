package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;


/**
 * <p>Title: Gene Expression Analysis Toolkit</p>
 * <p>Description: medusa Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version $Id: TableChangeEvent.java,v 1.1.1.1 2005/07/28 22:36:26 watkin Exp $
 */

public class TableChangeEvent extends Event {
    String _value = null;
    DSMicroarraySet _maSet = null;

    public TableChangeEvent(EventSource source, String message, DSMicroarraySet ms) {
        super(source);
        _value = message;
        _maSet = ms;
    }

    public String getMessage() {
        return _value;
    }

    public DSMicroarraySet getMicroarray() {
        return _maSet;
    }
}
