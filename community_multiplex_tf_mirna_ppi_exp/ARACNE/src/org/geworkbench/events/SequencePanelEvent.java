package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

/**
 * <p>Event thrown when a sequence file is loaded; it is thrown by
 * SequenceViewAppComponent</p>
 * <p>SequenceViewAppComponent throws this event right it loads a FASTA
 * file; listeners include the SequenceDiscoveryViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author Saroja Hanasoge
 * @version $Id: SequencePanelEvent.java,v 1.2 2005/12/22 18:44:02 watkin Exp $
 */

public class SequencePanelEvent extends Event {
    private String _value = null;
    private DSPanel<DSSequence> _panels = null;
    private DSSequenceSet sequenceDB = null;

    public SequencePanelEvent(EventSource source, DSPanel<DSSequence> p, DSSequenceSet db) {
        super(source);
        _panels = p;
        sequenceDB = db;
    }

    public DSPanel<DSSequence> getPanels() {
        return _panels;
    }

    public void setSequenceDB(DSSequenceSet sDB) {
        sequenceDB = sDB;
    }

    public DSSequenceSet getSequenceDB() {
        return sequenceDB;
    }
}
