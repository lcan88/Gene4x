package org.geworkbench.engine.config;

import java.awt.event.ActionListener;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Interface to be implemented by all plugin components that want to register
 * listeners for menu items from the menu bar.
 */
public interface MenuListener {
    /**
     * Returns an <code>ActionListener</code> that will be listening to a menu item.
     * In the most general case, the implementing class may wish to register
     * multiple listeners to multiple menu items. The argument <code>var</code>
     * is a mnemonic passed to the implementing class in order to designate
     * the particular menu item which the returned <code>ActionListener</code>
     * will be associated with.
     *
     * @param var Mnemonic designator of a menu item.
     * @return An <code>ActionListener</code> that will be listening to Action
     *         events from the menu item indicated by <code>var</code>.
     */
    public ActionListener getActionListener(String var);
}