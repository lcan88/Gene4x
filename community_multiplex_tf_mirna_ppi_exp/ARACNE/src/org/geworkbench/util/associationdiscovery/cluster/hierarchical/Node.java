package org.geworkbench.util.associationdiscovery.cluster.hierarchical;

import org.geworkbench.util.patterns.CSMatchedSeqPattern;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Represents a node for a tree in the result of
 * a Hierarchical algorithm. </p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author $AUTHOR$
 * @version 1.0
 */
public class Node {

    public CSMatchedSeqPattern pattern;
    public org.geworkbench.util.patterns.CSMatchedHMMSeqPattern hmmPattern;
    public int patIncluded;
    public int patExcluded;
    public int hPatIncluded;
    public int hPatExcluded;

    public Node(CSMatchedSeqPattern _pattern) {
        //default pattern.
        pattern = _pattern;
        patIncluded = patExcluded = 0;
        hPatIncluded = hPatExcluded = 0;
    }

}
