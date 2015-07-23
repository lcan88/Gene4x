package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;

/**
 * <p>PatternPanelEvent </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab </p>
 *
 * @author Saroja Hanasoge
 * @version $Id: AssociationPanelEvent.java,v 1.1.1.1 2005/07/28 22:36:26 watkin Exp $
 */

public class AssociationPanelEvent extends Event {
    public String message = null;
    public CSMatchedMatrixPattern[] patterns = null;

    public AssociationPanelEvent(CSMatchedMatrixPattern[] patterns, String m) {
        super(null);
        message = m;
        this.patterns = patterns;
    }

    public CSMatchedMatrixPattern[] getPatterns() {
        return patterns;
    }
}
