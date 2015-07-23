package org.geworkbench.events;

import java.util.EventListener;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Contract for listening to <code>HierClusterModelEvent</code>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface HierClusterModelEventListener extends EventListener {
    void hierClusterModelChange(HierClusterModelEvent hcme);
}

