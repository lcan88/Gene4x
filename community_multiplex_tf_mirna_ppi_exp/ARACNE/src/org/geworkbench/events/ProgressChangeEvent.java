package org.geworkbench.events;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: Encapsulate an event progress from an algorithm.
 * If an algorithm has different progress parameter/s that needed to be updated
 * then this class should be extended. (The model for the view of this algorithm
 * will need to cast the event to the
 * right class inoreder to get the extra parameters.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ProgressChangeEvent {
    //number of patterns found.
    private int patternFound = 0;

    public ProgressChangeEvent(int patternFound) {
        this.patternFound = patternFound;
    }

    /**
     * Returns the number of patterns found.
     *
     * @return pattern the number of patterns found so far.
     */
    public int getPatternFound() {
        return patternFound;
    }

    public void setPatternFound() {
        this.patternFound = patternFound;
    }
}