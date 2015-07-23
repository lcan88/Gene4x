package org.geworkbench.events;

import java.util.EventListener;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public interface MicroarraySetViewListener extends EventListener {
    void modelChanged(MicroarraySetViewEvent e);
}