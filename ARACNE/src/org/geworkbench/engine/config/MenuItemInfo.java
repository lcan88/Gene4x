package org.geworkbench.engine.config;

/**
 * Holds information pertaining to a main menu item for a component.
 *
 * @author John Watkinson
 */
public class MenuItemInfo {

    private String path;
    private String mode;
    private String var;
    private String icon;
    private String accelerator;

    public MenuItemInfo(String path, String mode, String var, String icon, String accelerator) {
        this.path = path;
        this.mode = mode;
        this.var = var;
        this.icon = icon;
        this.accelerator = accelerator;
    }

    public String getPath() {
        return path;
    }

    public String getMode() {
        return mode;
    }

    public String getVar() {
        return var;
    }

    public String getIcon() {
        return icon;
    }

    public String getAccelerator() {
        return accelerator;
    }
}
