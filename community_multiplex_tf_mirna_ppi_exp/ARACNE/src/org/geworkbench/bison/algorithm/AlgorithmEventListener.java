package org.geworkbench.bison.algorithm;

import java.util.EventListener;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public interface AlgorithmEventListener extends EventListener {

    /**
     * Method called on an <code>AlgorithmEventListener</code> to notify
     * <code>AlgorithmEvent</code>
     *
     * @param event <code>AlgorithmEvent</code>
     */
    public void receiveAlgorithmEvent(org.geworkbench.bison.algorithm.AlgorithmEvent event);
}
