package org.geworkbench.bison.datastructure.properties;

public interface DSAnnotated {
    String getAttribute(String label);

    void setAttribute(String label, String value);

    void removeAttribute(String label);

    boolean hasAttribute(String label);
}
