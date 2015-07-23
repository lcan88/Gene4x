package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSSeqRegistration;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Event thrown when a row is selected on the Sequence Discovery Panel</p>
 * <p>This event is thrown when a row is selected on the patternTable by
 * the SequenceDiscoveryViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author Saroja Hanasoge
 * @version $Id: SequenceDiscoveryTableEvent.java,v 1.2 2005/12/22 18:44:02 watkin Exp $
 */

public class SequenceDiscoveryTableEvent extends Event {

    public SequenceDiscoveryTableEvent(DSCollection<DSMatchedPattern<DSSequence, DSSeqRegistration>> patternMatches) {
        super(null);
        this.patternMatches = patternMatches;
    }

    private DSCollection<DSMatchedPattern<DSSequence, DSSeqRegistration>> patternMatches = null;

    public DSCollection<DSMatchedPattern<DSSequence, DSSeqRegistration>> getPatternMatchCollection() {
        return patternMatches;
    }


    /*
    private CSSequenceSet sequenceDB = null;
    private Pattern[]  patterns   = null;
    private Parameters parms = null;

    public SequenceDiscoveryTableEvent(EventSource source, Pattern[]  _patterns) {
        super(source);
        setPatterns(_patterns);
    }

    public Pattern[] getPatterns() {
        return patterns;
    }

    public void setPatterns(Pattern[] _patterns) {
        patterns = _patterns;
    }

    public CSSequenceSet getSequenceDB() {
        return sequenceDB;
    }

    public void setSequenceDB(CSSequenceSet seqDB) {
        sequenceDB = seqDB;
    }

    public void setParms(Parameters p) {
        parms = p;
    }

    public Parameters getParms() {
        return parms;
    }
    */
}
