package org.geworkbench.util;


/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 * <p/>
 * <p><b>Purpose</b>: Compute the distances between two vectors given by iterators or two arrays.
 * (e.g., the vectors could be genes or microarrays.)
 * The distance could be Euclidean, Pearson, or Spearman. <emph>If one vector is longer
 * than the other, we compute upto the shorter one and the rest values of the longer
 * one are ignored. If a value is not a number, we silently skip the value.</emph> </p>
 *
 * @author Frank Wei Guo
 * @version 3.0
 */

public interface Distance {
    double compute(DoubleIterator i, DoubleIterator j);

    double compute(double[] a, double[] b);
}
