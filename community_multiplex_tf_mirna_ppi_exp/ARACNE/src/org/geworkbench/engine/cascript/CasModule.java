package org.geworkbench.engine.cascript;

import org.geworkbench.engine.config.PluginDescriptor;
import org.geworkbench.engine.management.ComponentRegistry;

import java.io.PrintWriter;

/**
 * the wrapper class for Module
 *
 * @author Behrooz Badii - badiib@gmail.com
 */
class CasModule extends CasDataType {
    public CasModule() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    PluginDescriptor pd;
    String type;
    Object var;

    CasModule(String t) {
        name = null;
        type = t;
        pd = null;
        var = null;
    }
    CasModule(String n, String t) {
        name = n;
        type = t;
        pd = ComponentRegistry.getRegistry().getPluginDescriptorByID(t);
        /*if it is null, that means it didn't exist before we wanted it.
        It will NOT show up in the application.
        */
//        if (pd == null) {
//            ComponentRegistry.getRegistry().getAllPluginDescriptors()
//            pd = new PluginDescriptor(type, RandomNumberGenerator.getID(), name);
//        }
        var = pd.getPlugin();
    }

    public String typename() {
        return "module with type " + type;
    }

    public String getType() {
        return type;
    }

    public Object getPlugin() {
        return pd.getPlugin();
    }

    public CasDataType copy() {
        return new CasModule(name, type);
    }

    public void print(PrintWriter w) {
        //if (name != null) w.print(name + " = ");
        this.toString();
    }

    private void jbInit() throws Exception {
    }
}
