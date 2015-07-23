package org.geworkbench.engine.management;

import javax.swing.*;


/**
 * @author John Watkinson
 */
public class SwingModel implements SynchModel {
    public void initialize() {
        // no-op
    }

    public void shutdown() {
        // no-op
    }

    public void addTask(Runnable task, Object subscriber, Object object, Object publisher) {
        SwingUtilities.invokeLater(task);
    }
}
