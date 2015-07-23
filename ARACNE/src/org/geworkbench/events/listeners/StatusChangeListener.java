package org.geworkbench.events.listeners;

import org.geworkbench.events.ProgressBarEvent;
import org.geworkbench.events.StatusBarEvent;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Used to update status change for the
 * SequenceDiscoveryViewWidget Class.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface StatusChangeListener extends java.util.EventListener {
    public void progressBarChanged(ProgressBarEvent evt);

    public void statusBarChanged(StatusBarEvent evt);
}
