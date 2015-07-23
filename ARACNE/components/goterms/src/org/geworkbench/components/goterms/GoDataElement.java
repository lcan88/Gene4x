package org.geworkbench.components.goterms;

import org.geworkbench.components.goterms.annotation.GoTerm;

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
public class GoDataElement implements Comparable {
    public String enriched = "";
    public double chi = 0;
    public double pvalue = 0;
    public double correctedPValue = 0;
    public GoTerm go;

    public GoDataElement(GoTerm g) {
        go = g;
    }

    public int compareTo(Object o) {
        if (this.chi == ((GoDataElement) o).chi) {
            return 0;
        } else if (this.chi > ((GoDataElement) o).chi) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getLine() {
        return go.getName() + '\t' + enriched + '\t' + go.getSelected() + '\t' + go.getRelatedNo() + '\t' + pvalue;
    }
}
