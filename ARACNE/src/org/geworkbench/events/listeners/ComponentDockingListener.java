package org.geworkbench.events.listeners;

import org.geworkbench.engine.config.events.AppEventListener;

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

public interface ComponentDockingListener extends AppEventListener {
    void dockingAreaChanged(Component comp, String newArea);
}
