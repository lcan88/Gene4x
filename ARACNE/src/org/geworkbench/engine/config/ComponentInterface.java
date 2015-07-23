package org.geworkbench.engine.config;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ComponentInterface {
    private boolean active = false;
    private Class interfaceClass = null;

    public ComponentInterface(Class intClass, boolean activated) {
        interfaceClass = intClass;
        active = activated;
    }

    public boolean isActive() {
        return active;
    }

    public void setActiveFlag(boolean activeFlag) {
        active = activeFlag;
    }

    public void flipActiveFlag() {
        active = (!active);
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class intClass) {
        interfaceClass = intClass;
    }
}
