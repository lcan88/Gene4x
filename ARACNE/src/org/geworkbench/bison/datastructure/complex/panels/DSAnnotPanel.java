package org.geworkbench.bison.datastructure.complex.panels;

import org.geworkbench.bison.datastructure.properties.DSNamed;

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
public interface DSAnnotPanel <T extends DSNamed, U> extends DSPanel<T> {
    public DSAnnotatedPanel<T, U> getPanel(int i);

    public DSAnnotatedPanel<T, U> getPanel(T item);

    public DSAnnotatedPanel<T, U> getPanel(String label);
}
