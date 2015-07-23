package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.config.events.Event;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;


/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia Genomics Center</p>
 *
 * @author not attributable
 * @version $Id: DiscoverEvent.java,v 1.2 2006/01/13 22:48:37 watkin Exp $
 */

public class DiscoverEvent extends Event {

    public enum Action {
        DISCOVER,
        GET_PATTERN,
        GET_STATUS
    }

    public int minSupport = 0;
    public int minMarker = 0;
    public double delta = 0.0;
    public boolean isBusy = true;
    public boolean isBlocking = true;
    public double percentDone = 0;
    public int patternNo = 0;
    public int patternId = 0;
    public CSMatchedMatrixPattern pattern = null;
    public int maxPatternNo = 0; // chao add
    public DSMicroarraySet set = null;
    private Action action;

    public DiscoverEvent(int s, int m, double d, int p, Action action) {
        super(null);
        minSupport = s;
        minMarker = m;
        delta = d;
        maxPatternNo = p;
        this.action = action;
    }

    public void setPatternId(int id) {
        patternId = id;
    }

    public Action getAction() {
        return action;
    }
}
