package org.geworkbench.engine.config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PluginClassRegistry {
    static HashMap pluginClasses = new HashMap();

    public PluginClassRegistry() {
    }

    public static void getByName() {


    }

    public static PluginClass getByClassName(String classname) {
        return (PluginClass) pluginClasses.get(classname);
    }

    public static void addClass(String className, PluginClass pl) {
        pluginClasses.put(className, pl);

    }

    public static PluginClass[] getPluginClasses() {
        ArrayList plg = new ArrayList();
        //PluginClass[] result=null;

        plg.addAll(pluginClasses.values());
        PluginClass[] array = new PluginClass[plg.size()];
        for (int i = 0; i < plg.size(); i++) {
            array[i] = (PluginClass) plg.get(i);
        }
        return array;


    }

}