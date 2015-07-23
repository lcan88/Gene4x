package org.geworkbench.events.listeners;

import org.geworkbench.events.ProgressChangeEvent;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Subscribers to this interface get notify when a progress
 * is made in an algorithm of a discovery.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface ProgressChangeListener extends java.util.EventListener {
    public void progressChanged(ProgressChangeEvent evt);
}