package org.geworkbench.events;

import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.engine.config.events.Event;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SubpanelChangedEvent <T extends DSNamed> extends Event {

    public static final int SET_CONTENTS = 1;
    public static final int NEW = 2;
    public static final int DELETE = 4;

    private DSPanel<T> subpanel = null;
    private int mode = SET_CONTENTS;
    private Class<T> type;

    public SubpanelChangedEvent(Class<T> type, DSPanel<T> subpan, int m) {
        super(null);
        this.type = type;
        subpanel = subpan;
        mode = m;
    }

    public DSPanel getPanel() {
        return (subpanel);
    }

    public int getMode() {
        return mode;
    }

    public Class<T> getType() {
        return type;
    }
}
