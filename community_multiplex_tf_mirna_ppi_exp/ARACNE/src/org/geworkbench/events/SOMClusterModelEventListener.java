package org.geworkbench.events;

import java.util.EventListener;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Interface for responding to <code>SOMClusterModelEvent</code> events
 * (remember, these are Java events, not application events).
 * notifying of some change to data related to SOM clustering.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface SOMClusterModelEventListener extends EventListener {
    void somClusterModelChange(SOMClusterModelEvent hcme);
}