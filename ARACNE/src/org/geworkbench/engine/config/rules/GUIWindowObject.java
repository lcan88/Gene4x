package org.geworkbench.engine.config.rules;

import org.geworkbench.engine.config.GUIFramework;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Describes the object that is pushed on the <code>UILauncher</code> stack
 * when processing the pattern "geaw-config/gui-window". This object is
 * responsible for instantiating the top level gui window.
 */
public class GUIWindowObject {
    /**
     * @param className
     */
    public void createGUI(String className) {
        GUIFramework newGui;
        // System.out.println(className);
        // System.out.flush();
        try {
            newGui = (GUIFramework) Class.forName(className).newInstance();
            // There is no checking to see if a GUIFramework is already defined.
            // This hsould not be the case as the DTD allows for only one <gui-window>
            // element.
            GeawConfigObject.setGuiWindow(newGui);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}