package org.geworkbench.bison.datastructure.complex.pattern;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.util.DSPValue;

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
public interface DSMatchedPattern <T,R> extends DSPValue, DSNamed {
    public DSPattern<T, R> getPattern();

    public int getSupport();

    public int getUniqueSupport();

    public DSPatternMatch<T, R> get(int i);

    public List<DSPatternMatch<T, R>> matches();

    public void addAll(DSMatchedPattern<T, R> matches);

    public boolean add(DSPatternMatch<T, R> match);
}
