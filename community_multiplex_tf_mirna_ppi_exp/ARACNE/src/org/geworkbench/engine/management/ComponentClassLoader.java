package org.geworkbench.engine.management;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A classloader that allows class definitions in the component to override those of the parent.
 *
 * @author John Watkinson
 */
public class ComponentClassLoader extends URLClassLoader {

    ClassLoader parent;

    public ComponentClassLoader(URL[] urls) {
        super(urls);
        parent = getParent();
        if (parent == null) {
            parent = getSystemClassLoader();
        }
    }

    public ComponentClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
        if (parent == null) {
            parent = getSystemClassLoader();
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    /**
     * Attempts to load the class in this classloader before deferring to parent.
     */
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> type = findLoadedClass(name);
        if (type == null) {
            try {
                type = findClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore-- type will be null
            }
        }
        if (type == null) {
            try {
                type = parent.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore-- type will be null
            }
        }
        if (type == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(type);
        }
        return type;
    }
}
