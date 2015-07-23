package org.geworkbench.bison.model.analysis;

import javax.swing.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Generic representation of Analyses parameters. This endeavours to capture
 * both the data model of the parameters and also a visual representation.
 * The actual look of the parameter component would be decided by generalizing
 * components.
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class ParameterPanel extends JPanel {
    /**
     * Bit to retain dirty state, if this parameter set has been changed
     */
    private boolean d = false;
    /**
     * The name of this parameter set
     */
    private String name = null;

    /**
     * Default constructor
     */
    public ParameterPanel() {
        name = "UnNamed Set";
    }

    public ParameterPanel(String n) {
        name = n;
    }

    /**
     * Checks if this parameter set has been modified
     *
     * @return dirty bit
     */
    public boolean isDirty() {
        return d;
    }

    /**
     * Sets the dirty bit to true/false
     *
     * @param dirtyBit state of this this parameter set
     */
    public void setDirty(boolean dirtyBit) {
        d = dirtyBit;
    }

    /**
     * Sets the name of this parameter set
     *
     * @param n name
     */
    public void setName(String n) {
        name = n;
    }

    /**
     * Gets the name of this parameter set
     *
     * @return name
     */
    public String getName() {
        return name;
    }

}