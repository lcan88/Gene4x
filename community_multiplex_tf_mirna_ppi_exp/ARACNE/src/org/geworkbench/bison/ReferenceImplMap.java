package org.geworkbench.bison;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Provides a map of default reference implementations of <code>bison</code>
 * interfaces
 * @author manjunath at genomecenter dot columbia dot edu
 */

public class ReferenceImplMap extends HashMap<Class, Class>{

    private static ReferenceImplMap map = null;

    private ReferenceImplMap() {
        super();
        try {
            Properties props = new Properties();
            InputStream is = ReferenceImplMap.class.getResourceAsStream(
                    "ReferenceImplMap.properties");
            props.load(is);
            is.close();
            Set keys = props.keySet();
            for (Object key : keys){
                Class keyType = Class.forName((String) key);
                Class valueType = Class.forName((String)props.get(key));
                this.put(keyType, valueType);
            }
        } catch (ClassNotFoundException cnfe){
        } catch (IOException ioe){}
    }

    public static ReferenceImplMap getDefaultImplementationMap(){
        if (map == null){
            map = new ReferenceImplMap();
        }
        return map;
    }

    public Class getDefaultImplementation(Class bisonType){
        return this.get(bisonType);
    }
}
