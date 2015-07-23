package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id: PhenotypeSelectorEvent.java,v 1.3 2006/01/13 22:48:37 watkin Exp $
 */

public class PhenotypeSelectorEvent <Q extends DSMicroarray> extends Event {
    private DSPanel<Q> panel;
    private DSDataSet<Q> dataSet;

    public PhenotypeSelectorEvent(DSPanel<Q> p, DSDataSet<Q> d) {
        super(null);
        panel = p;
        dataSet = d;
    }

    public DSPanel<Q> getTaggedItemSetTree() {
        return panel;
    }

    public DSDataSet<Q> getDataSet() {
        return dataSet;
    }
}
