package org.geworkbench.bison.datastructure.complex.pattern.matrix;

import org.geworkbench.bison.util.DSPValue;

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
public class CSPValued implements DSPValue {
    float pvalue = 0.0F;

    public double getPValue() {
        return (double) pvalue;
    }

    public void setPValue(double p) {
        pvalue = (float) p;
    }
}
