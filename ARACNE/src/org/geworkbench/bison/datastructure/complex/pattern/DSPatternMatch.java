package org.geworkbench.bison.datastructure.complex.pattern;

import org.geworkbench.bison.util.DSPValue;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface DSPatternMatch <T, R> extends DSPValue {
    public T getObject() throws IndexOutOfBoundsException;

    public R getRegistration() throws IndexOutOfBoundsException;

    public void setObject(T object);

    public void setRegistration(R reg);
}
