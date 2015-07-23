package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: This interface defines a source for getting patterns from.
 * The source is sequential with random access to a pattern.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface SequentialPatternSource extends org.geworkbench.util.patterns.DataSource {

    /**
     * Get the number of patterns in this source.
     * Note: The result of this calls may vary with time.
     */
    public int getPatternSourceSize() throws PatternFetchException;

    /**
     * Get a pattern from this source.
     *
     * @param index pattern at index. 0<= index < getPatternStreamSize();
     */
    public DSMatchedSeqPattern getPattern(int index) throws org.geworkbench.util.patterns.PatternFetchException;

    /**
     * The method should sort the patterns in the data source on field.
     *
     * @param field the sorting field to sort on. See Pattern.
     */
    public void sort(int field);

    /**
     * Mask/Unmask the patterns at the index array.
     *
     * @param index an array of patterns indeces to mask/unmask. If the array
     *              is null all indices will be maks/unmasked.
     * @param mask  mask - true ; unmask - false
     */
    public void mask(int[] index, boolean mask);
}
