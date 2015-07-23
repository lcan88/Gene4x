package org.geworkbench.builtin.projects;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.CSSequentialItemList;
import org.geworkbench.bison.datastructure.properties.CSDescribable;
import org.geworkbench.bison.util.DSAnnotLabel;

import java.io.ObjectStreamField;
import java.util.HashMap;

/**
 * An abstract implementation of DSDataSet.
 *
 * @todo - watkin - this class is entirely unused! {@link org.geworkbench.bison.datastructure.biocollections.CSDataSet} does not extend it.
 */

public abstract class AbstractDataSet <T extends DSBioObject> extends CSSequentialItemList<T> implements DSDataSet<T> {
    //a unique id for this DataSet
    protected HashMap microarrayProperties = new HashMap();
    protected String id;
    protected String name;
    protected CSDescribable description = new CSDescribable();
    protected DSAnnotLabel property = null;

    final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("id", String.class), new ObjectStreamField("description", CSDescribable.class), new ObjectStreamField("name", String.class)};

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setLabel(String name) {
        this.name = name;
    }

    public String getLabel() {
        return name;
    }

    public String[] getDescriptions() {
        return description.getDescriptions();
    }

    public void removeDescription(String descr) {
        description.removeDescription(descr);
    }

    public void addDescription(String descr) {
        description.addDescription(descr);
    }

    public void addObject(Object tag, Object object) {
        microarrayProperties.remove(tag);
        microarrayProperties.put(tag, object);
        Object obj = microarrayProperties.get(tag);
    }

    public Object getObject(Object tag) {
        Object object = microarrayProperties.get(tag);
        return object;
    }

    public Object getObject(Class tag) {
        Object object = microarrayProperties.get(tag);
        if (object == null) {
            try {
                object = tag.newInstance();
            } catch (IllegalAccessException ex) {
            } catch (InstantiationException ex) {
            }
            microarrayProperties.put(tag, object);
        }
        return object;
    }

    public void setSelectedProperty(DSAnnotLabel _property) {
        property = _property;
    }
}
