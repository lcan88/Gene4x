package org.geworkbench.events;

import org.geworkbench.engine.config.events.Event;
import org.geworkbench.engine.config.events.EventSource;

import java.awt.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public class ComponentDockingEvent extends Event {

    private String area = null;
    private Component component = null;

    public ComponentDockingEvent(EventSource source, Component comp, String ar) {
        super(source);
        area = ar;
        component = comp;
    }

    public Component getComponent() {
        return component;
    }

    public String getArea() {
        return area;
    }
}
