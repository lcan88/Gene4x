package org.geworkbench.bison.annotation;

import org.geworkbench.bison.datastructure.properties.DSNamed;

/**
 * @author John Watkinson
 */
public class CSAnnotationType<T extends DSNamed> implements DSAnnotationType<T> {

    private Class<T> type;
    private String label;

    public CSAnnotationType(Class<T> type, String label) {
        this.type = type;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Class<T> getType() {
        return type;
    }
}
