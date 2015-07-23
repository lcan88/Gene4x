package org.geworkbench.bison.util;

import java.io.Serializable;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Range implements Serializable {
    public double min = Double.MAX_VALUE;
    public double max = Double.MIN_VALUE;
    public Normal norm = new Normal();

    public Range() {
    }
}
