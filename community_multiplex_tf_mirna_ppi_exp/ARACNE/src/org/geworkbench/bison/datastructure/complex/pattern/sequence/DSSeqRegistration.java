package org.geworkbench.bison.datastructure.complex.pattern.sequence;

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
public class DSSeqRegistration implements DSPValue {
    public double getPValue() {
        return (double) pValue;
    }

    public void setPValue(double pValue) {
        this.pValue = (float) pValue;
    }

    public float pValue = 1.0F;
    public int x1 = 0;
    public int x2 = 0;
    // This is used to encode the 5'-3' (0) or 3'-5' (1) strand
    public int strand = 0;

    public int length() {
        return x2 - x1;
    }

}
