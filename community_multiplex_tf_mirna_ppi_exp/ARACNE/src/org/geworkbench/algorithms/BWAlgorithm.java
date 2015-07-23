package org.geworkbench.algorithms;

/**
 * <p>Title: C2B2PNP</p>
 * <p>Description: Every Algorithm should implement this interface</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author $Author: watkin $
 * @version $Revision: 1.1.1.1 $
 */

public interface BWAlgorithm {
    /**
     * Start the algorithm.
     */
    public void start();

    /**
     * Stop the algorithm.
     */
    public void stop();

    /**
     * Returns the completion of this algorithm.
     * 0.0 not complete; 1.0 complete
     */
    public double getCompletion();

}
