package org.geworkbench.bison.algorithm;

import java.util.EventObject;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Mechanism for development and distribution of highly sophisticated, modular software applications for biomedical research</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public class AlgorithmEvent extends EventObject {

    public static final int algorithmTimeTick = -2;
    public static final int algorithmCompleted = -1;
    public static final int algorithmUndefined = 0;
    /**
     * <code>String</code> describing the changed state of the analysis
     */
    private int algorithmState = 0;

    /**
     * If the transition was made successfully
     */
    private boolean success = false;

    /**
     * <code>Object</code> that can be used to store any other useful information
     * relating to the analysis
     */
    private Object utilityObject = null;

    /**
     * Default constructor
     *
     * @param source <code>Object</code> generating this event
     */
    public AlgorithmEvent(Object source) {
        super(source);
    }

    public AlgorithmEvent(Object source, int state, Object object, boolean succ) {
        super(source);
        algorithmState = state;
        utilityObject = object;
        success = succ;
    }

    /**
     * Returns the current state of the algorithm
     *
     * @return String state
     */
    public int getState() {
        return algorithmState;
    }

    /**
     * Utility <code>Object</code>
     *
     * @return Object
     */
    public Object getObject() {
        return utilityObject;
    }

    /**
     * If last transition was successful
     *
     * @return boolean
     */
    public boolean getSuccess() {
        return success;
    }
}
