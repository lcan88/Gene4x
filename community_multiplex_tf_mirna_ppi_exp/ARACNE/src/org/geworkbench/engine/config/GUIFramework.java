package org.geworkbench.engine.config;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import javax.swing.*;
import java.awt.*;

/**
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust, Inc.</p>
 * @author First Genetic Trust, Inc.
 * @version 1.0
 */

/**
 * Base class for the top-level window of an application.
 */
public abstract class GUIFramework extends JFrame {

    public static final String TOOL_AREA = "Toolbar";

    public static final String VISUAL_AREA = "VisualArea";

    public static final String SELECTION_AREA = "SelectionArea";

    public static final String COMMAND_AREA = "CommandArea";

    public static final String PROJECT_AREA = "ProjectArea";

    /**
     * Adds the <code>visualPlugin</code> to the container identified by the name
     * <code>areaName</code>.
     *
     * @param areaName
     * @param visualPlugin
     */
    public abstract void addToContainer(String areaName, Component visualPlugin);

    /**
     * Adds the <code>visualPlugin</code> to the container identified by the name
     * <code>areaName</code>. The visual plugin will be displayed using the name
     * <code>pluginName</code>
     *
     * @param areaName
     * @param visualPlugin
     * @param pluginName
     * @param mainPluginClass The main class for this plugin as defined in all.xml. Used to determine which plugins to display for a given datatype
     */
    public abstract void addToContainer(String areaName, Component visualPlugin, String pluginName, Class mainPluginClass);

    /**
     * Removes the designated <code>visualPlugin</code> from the GUI.
     *
     * @param visualPlugin
     */
    public abstract void remove(Component visualPlugin);

    /**
     * get the infomation about the visual area the visualPlugin was plugged into
     *
     * @param visualPlugin
     */
    public abstract String getVisualArea(Component visualPlugin);

    /**
     * Allows a project node selection event to tell the GUIFramework which type of data is going to be
     * visualized. Will allow for the display of only those visualizations that support the selected data type.
     * @param type
     */
    public abstract void setVisualizationType(DSDataSet type);
}
