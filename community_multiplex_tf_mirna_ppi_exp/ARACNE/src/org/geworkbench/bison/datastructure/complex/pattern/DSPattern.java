package org.geworkbench.bison.datastructure.complex.pattern;

import java.util.List;


/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface DSPattern <T,R> {
    /**
     * returns the registration of the best match to the object
     *
     * @param object T
     * @return R
     */
    R match(T object);

    /**
     * returns all the matches to the object with pvalue greated than p
     *
     * @param object T
     * @return DSPatternMatches
     */
    List<DSPatternMatch<T, R>> match(T object, double p);

    /**
     * returns the string representation of the pattern
     *
     * @param object       T
     * @param registration R
     * @return String
     */
    String toString(T object, R registration);
}
