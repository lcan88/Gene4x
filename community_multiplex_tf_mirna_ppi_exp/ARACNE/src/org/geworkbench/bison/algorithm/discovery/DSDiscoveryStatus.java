package org.geworkbench.bison.algorithm.discovery;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */
public interface DSDiscoveryStatus {
    //    public boolean getBusy();
    public double getPercentDone();

    public int getPatternNo();

    public int getCaseSupport();

    public int getCtrlSupport();
}
