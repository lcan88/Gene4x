package org.geworkbench.bison.annotation;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.properties.DSNamed;

import java.util.Iterator;
import java.util.WeakHashMap;
import java.io.Serializable;

/**
 * @author John Watkinson
 */
public class CSAnnotationContextManager implements DSAnnotationContextManager {

    private static CSAnnotationContextManager instance;

    public static CSAnnotationContextManager getInstance() {
        if (instance == null) {
            instance = new CSAnnotationContextManager();
        }
        return instance;
    }

    public static final String DEFAULT_CONTEXT_NAME = "Default";

    private WeakHashMap<DSItemList, ListOrderedSet<DSAnnotationContext>> contextMap;
    private WeakHashMap<DSItemList, String> currentContextMap;

    public CSAnnotationContextManager() {
        contextMap = new WeakHashMap<DSItemList, ListOrderedSet<DSAnnotationContext>>();
        currentContextMap = new WeakHashMap<DSItemList, String>();
    }

    public <T extends DSNamed> DSAnnotationContext<T>[] getAllContexts(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return new DSAnnotationContext[0];
        } else {
            return contexts.toArray(new DSAnnotationContext[0]);
        }
    }

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, String name) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts != null) {
            for (Iterator<DSAnnotationContext> iterator = contexts.iterator(); iterator.hasNext();) {
                DSAnnotationContext context = iterator.next();
                if (name.equals(context.getName())) {
                    return context;
                }
            }
        }
        // Create context
        return createContext(itemList, name);
    }

    public boolean hasContext(DSItemList itemList, String name) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts != null) {
            for (Iterator<DSAnnotationContext> iterator = contexts.iterator(); iterator.hasNext();) {
                DSAnnotationContext context = iterator.next();
                if (name.equals(context.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public <T extends DSNamed> DSAnnotationContext<T> createContext(DSItemList<T> itemList, String name) {
        CSAnnotationContext<T> context = new CSAnnotationContext<T>(name, itemList);
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            contexts = new ListOrderedSet<DSAnnotationContext>();
            contextMap.put(itemList, contexts);
        }
        contexts.add(context);
        return context;
    }

    public <T extends DSNamed> int getNumberOfContexts(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return 0;
        } else {
            return contexts.size();
        }
    }

    public <T extends DSNamed> DSAnnotationContext<T> getContext(DSItemList<T> itemList, int index) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            throw new ArrayIndexOutOfBoundsException("Attempt to index an empty context.");
        } else {
            return contexts.get(index);
        }
    }

    public boolean removeContext(DSItemList itemList, String name) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            return false;
        } else {
            DSAnnotationContext context = getContext(itemList, name);
            if (context == null) {
                return false;
            } else {
                return contexts.remove(context);
            }
        }
    }

    public boolean renameContext(DSItemList itemList, String oldName, String newName) {
        DSAnnotationContext context = getContext(itemList, oldName);
        if (hasContext(itemList, newName)) {
            throw new IllegalArgumentException("Context with name '" + newName + "' already exists.");
        }
        if (context == null) {
            return false;
        } else {
            context.setName(newName);
            return true;
        }
    }

    public <T extends DSNamed> DSAnnotationContext<T> getCurrentContext(DSItemList<T> itemList) {
        ListOrderedSet<DSAnnotationContext> contexts = contextMap.get(itemList);
        if (contexts == null) {
            contexts = new ListOrderedSet<DSAnnotationContext>();
            contextMap.put(itemList, contexts);
        }
        if (contexts.isEmpty()) {
            // Create a default context
            CSAnnotationContext<T> context = new CSAnnotationContext<T>(DEFAULT_CONTEXT_NAME, itemList);
            contexts.add(context);
        }
        String currentContext = currentContextMap.get(itemList);
        DSAnnotationContext context = null;
        if (currentContext != null) {
            context = getContext(itemList, currentContext);
        }
        if (context == null) {
            context = contexts.get(0);
            currentContextMap.put(itemList, context.getName());
        }
        return context;
    }

    public <T extends DSNamed> void setCurrentContext(DSItemList<T> itemList, DSAnnotationContext<T> context) {
        currentContextMap.put(itemList, context.getName());
    }

    public <T extends DSNamed> void copyContexts(DSItemList<T> from, DSItemList<T> to) {
        DSAnnotationContext<T>[] contexts = getAllContexts(from);
        ListOrderedSet<DSAnnotationContext> contextSet = contextMap.get(to);
        if (contextSet == null) {
            contextSet = new ListOrderedSet<DSAnnotationContext>();
            contextMap.put(to, contextSet);
        }
        for (int i = 0; i < contexts.length; i++) {
            DSAnnotationContext<T> context = contexts[i];
            contextSet.add(context.clone());
        }
    }

    public static class SerializableContexts implements Serializable {
        private ListOrderedSet<DSAnnotationContext> contexts;
        private String current;

        public SerializableContexts(ListOrderedSet<DSAnnotationContext> contexts, String current) {
            this.contexts = contexts;
            this.current = current;
        }
    }

    public SerializableContexts getContextsForSerialization(DSItemList itemList) {
        return new SerializableContexts(contextMap.get(itemList), currentContextMap.get(itemList));
    }

    public void setContextsFromSerializedObject(DSItemList itemList, SerializableContexts contexts) {
        contextMap.put(itemList, contexts.contexts);
        currentContextMap.put(itemList, contexts.current);
    }
}
