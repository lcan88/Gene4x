package org.geworkbench.engine.config.rules;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Thrown when the menu path specified within some <code>&lt;menu-item&gt;</code>
 * block of the configuration file, is in conflict with the already existing menu
 * structure. A conflict situation arises when:
 * <UI>
 * <LI>   The ending point of the new menu item path coincides with an existing
 * submenu.</LI>
 * <LI>   A submenu node in the new menu item path coincides with an existing
 * final menu item.</LI>
 * </UI>
 */
public class DynamicMenuItemException extends org.geworkbench.util.BaseException {
    // ---------------------------------------------------------------------------
    // --------------- Constructors
    // ---------------------------------------------------------------------------
    public DynamicMenuItemException() {
        super();
    }

    public DynamicMenuItemException(String message) {
        super(message);
    }

}