package org.geworkbench.events;

import java.util.EventObject;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: LoginPanelModelEvent is used to notify a listener that a  model
 * has changed.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class LoginPanelModelEvent extends EventObject {

    public LoginPanelModelEvent(Object source) {
        super(source);
    }

}