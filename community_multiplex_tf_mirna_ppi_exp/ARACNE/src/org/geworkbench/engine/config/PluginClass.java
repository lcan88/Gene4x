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

public class PluginClass extends IdentifiableImpl {
    private Class pluginClass;
    private String name;

    public PluginClass() {

    }

    public void createPluginClass(String classname, String name) {
        this.setID(classname);
        this.setLabel(name);
        try {
            Thread.currentThread().getContextClassLoader().loadClass(classname);
            this.pluginClass = Class.forName(classname);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    // watkin - unused
//    public void createInstance(String id) {
//        PluginObject po = new PluginObject();
//        po.createPlugin(id, name, this.getID()); //one should be URI
//        po.finish();
//    }

    public boolean isVisualPlugin() {
        try {
            if (!Class.forName("org.geworkbench.engine.config.VisualPlugin").isAssignableFrom(pluginClass))
                return false;
            else
                return true;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace(System.err);
            return false;
        }

    }


}
