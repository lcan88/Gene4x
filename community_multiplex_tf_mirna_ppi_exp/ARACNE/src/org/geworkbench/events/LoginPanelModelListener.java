package org.geworkbench.events;

import java.util.EventListener;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: LoginPanelModelListener defines the interface for an object
 * that listens for changes in LoginPanelModel.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface LoginPanelModelListener extends EventListener {
    public void loginPanelChanged(org.geworkbench.events.LoginPanelModelEvent evt);
}