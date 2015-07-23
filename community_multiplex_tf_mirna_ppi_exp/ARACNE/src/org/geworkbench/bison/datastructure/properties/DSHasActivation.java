package org.geworkbench.bison.datastructure.properties;

/**
 * Implementing classes can be <b>enabled</b> and <b>disabled</b>.
 */
public interface DSHasActivation {

    /**
     * Gets the activation status.
     *
     * @return <code>true</code> if activated, <code>false</code> if deactivated.
     */
    boolean enabled();

    /**
     * Sets the activation status.
     *
     * @param status <code>true</code> if activated, <code>false</code> if deactivated.
     */
    void enable(boolean status);
}
