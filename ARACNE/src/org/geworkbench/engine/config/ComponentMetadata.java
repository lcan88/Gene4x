package org.geworkbench.engine.config;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata for a component, such as common name, version, description, etc.
 *
 * @author John Watkinson
 */
public class ComponentMetadata {

    private Class type;
    private String componentResource;

    private String name;
    private String version;
    private String description;
    private ImageIcon icon;
    private String helpSet;

    private List<MenuItemInfo> menuInfoList;

    public ComponentMetadata(Class type, String componentResource) {
        this.type = type;
        this.componentResource = componentResource;
        menuInfoList = new ArrayList<MenuItemInfo>();
    }

    public Class getType() {
        return type;
    }

    public String getComponentResource() {
        return componentResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getHelpSet() {
        return helpSet;
    }

    public void setHelpSet(String helpSet) {
        this.helpSet = helpSet;
    }

    public List<MenuItemInfo> getMenuInfoList() {
        return menuInfoList;
    }

    public void addMenuInfo(String path, String mode, String var, String icon, String accelerator) {
        menuInfoList.add(new MenuItemInfo(path, mode, var, icon, accelerator));
    }

}
