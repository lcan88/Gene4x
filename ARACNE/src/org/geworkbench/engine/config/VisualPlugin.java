package org.geworkbench.engine.config;

import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Interface to be implemented by all plugin components that have a visual
 * representation.
 */
public interface VisualPlugin {
    /**
     * Return the <code>Component</code> that is the visual representation of
     * the implementing plugin.
     */
    public Component getComponent();
}