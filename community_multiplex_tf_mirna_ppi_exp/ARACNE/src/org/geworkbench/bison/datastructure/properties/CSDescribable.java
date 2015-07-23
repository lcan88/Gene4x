package org.geworkbench.bison.datastructure.properties;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Vector;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Baseline implementation of <code>Describable</code>.
 */
public class CSDescribable implements DSDescribable, Serializable {
    /**
     * Contains the description strings.
     */
    protected Vector descriptions = new Vector();
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("descriptions", Vector.class)};

    public void addDescription(String desc) {
        if (desc != null)
            descriptions.add(desc);
    }

    public String[] getDescriptions() {
        return (String[]) descriptions.toArray(new String[0]);
    }

    public void removeDescription(String desc) {
        if (desc == null)
            return;
        for (int i = 0; i < descriptions.size(); ++i)
            if (descriptions.get(i) == desc) {
                descriptions.remove(i);
                break;
            }

    }

}
