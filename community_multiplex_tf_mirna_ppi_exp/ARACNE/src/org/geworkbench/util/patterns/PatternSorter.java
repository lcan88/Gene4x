package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;

import java.util.Comparator;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

/**
 * This class is used to sort patterns
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PatternSorter implements Comparator {
    int mode = 1;

    public void setMode(int m) {
        mode = m;
    }

    public int compare(Object o1, Object o2) {
        DSMatchedSeqPattern p1 = (DSMatchedSeqPattern) o1;
        DSMatchedSeqPattern p2 = (DSMatchedSeqPattern) o2;
        switch (mode) {
            case 1: // Sort by Sequence Count
                if (p1.getSupport() < p2.getSupport()) {
                    return 1;
                } else if (p1.getSupport() == p2.getSupport()) {
                    if (p1.getUniqueSupport() < p2.getUniqueSupport()) {
                        return 1;
                    } else if (p1.getUniqueSupport() == p2.getUniqueSupport()) {
                        if (p1.getLength() < p2.getLength()) {
                            return 1;
                        } else if (p1.getLength() == p2.getLength()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            case 2: // Sort by Support
                if (p1.getUniqueSupport() < p2.getUniqueSupport()) {
                    return 1;
                } else if (p1.getUniqueSupport() == p2.getUniqueSupport()) {
                    if (p1.getSupport() < p2.getSupport()) {
                        return 1;
                    } else if (p1.getSupport() == p2.getSupport()) {
                        if (p1.getLength() < p2.getLength()) {
                            return 1;
                        } else if (p1.getLength() == p2.getLength()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
                //break;
            case 3: // Sort by length (Tokens)
                if (p1.getLength() < p2.getLength()) {
                    return 1;
                } else if (p1.getLength() == p2.getLength()) {
                    if (p1.getUniqueSupport() < p2.getUniqueSupport()) {
                        return 1;
                    } else if (p1.getUniqueSupport() == p2.getUniqueSupport()) {
                        if (p1.getSupport() < p2.getSupport()) {
                            return 1;
                        } else if (p1.getSupport() == p2.getSupport()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            case 4: // Sort by pvalue
                if (p1.getPValue() < p2.getPValue()) {
                    return 1;
                } else if (p1.getPValue() == p2.getPValue()) {
                    if (p1.getSupport() < p2.getSupport()) {
                        return 1;
                    } else if (p1.getSupport() == p2.getSupport()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
                //break;
            default:
                return -1;
        }
    }
}
