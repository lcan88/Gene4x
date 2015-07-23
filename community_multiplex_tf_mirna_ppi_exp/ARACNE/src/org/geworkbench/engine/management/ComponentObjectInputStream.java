package org.geworkbench.engine.management;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * An object input stream that can load objects using a specific classloader (such as a component's classloader).
 *
 * @author John Watkinson
 */
public class ComponentObjectInputStream extends ObjectInputStream {

    private ClassLoader classLoader;

    public ComponentObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        return classLoader.loadClass(desc.getName());
    }
}
