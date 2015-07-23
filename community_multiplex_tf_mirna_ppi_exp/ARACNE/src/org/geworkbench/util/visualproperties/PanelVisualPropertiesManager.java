package org.geworkbench.util.visualproperties;


import java.awt.*;
import java.util.WeakHashMap;

/**
 * Maintains visual properties for panels.
 * <p/>
 * If a panel is no longer referenced from the application, then it is automatically removed from this manager as well.
 *
 * @author John Watkinson
 */
public class PanelVisualPropertiesManager {

    private static PanelVisualPropertiesManager instance;

    public static PanelVisualPropertiesManager getInstance() {
        if (instance == null) {
            instance = new PanelVisualPropertiesManager();
        }
        return instance;
    }

    private WeakHashMap<Object, PanelVisualProperties> panelToVisualProperties;

    public PanelVisualPropertiesManager() {
        panelToVisualProperties = new WeakHashMap<Object, PanelVisualProperties>();
    }

    public void setVisualProperties(Object panel, PanelVisualProperties properties) {
        panelToVisualProperties.put(panel, properties);
    }

    public PanelVisualProperties getVisualProperties(Object item) {
        return panelToVisualProperties.get(item);
    }

    public void clearVisualProperties(Object item) {
        panelToVisualProperties.remove(item);
    }

    public void clearAllVisualProperties() {
        panelToVisualProperties.clear();
    }

    public PanelVisualProperties getDefaultVisualProperties(int index) {
        int shapeIndex = index % PanelVisualProperties.AVAILABLE_SHAPES.length;
        int colorIndex = index % PanelVisualProperties.DEFAULT_PAINTS.length;
        return new PanelVisualProperties(shapeIndex, (Color) PanelVisualProperties.DEFAULT_PAINTS[colorIndex]);
    }
}
