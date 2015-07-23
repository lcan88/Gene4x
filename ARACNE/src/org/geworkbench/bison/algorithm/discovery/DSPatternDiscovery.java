package org.geworkbench.bison.algorithm.discovery;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.util.DSPValue;

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
public interface DSPatternDiscovery {
    int NormalDiscovery = 1;
    int ExhaustiveDiscovery = 2;
    int HierarchicalDiscovery = 3;

    public DSMatchedPattern<DSMicroarray, DSPValue> get(int id);

    public DSDiscoveryStatus status();

    public void addAlgorithmEventListener(org.geworkbench.bison.algorithm.AlgorithmEventListener ael);

    public void notifyService();

    public void setMicroarraySet(DSMicroarraySet maSet);

    public void setType(int t);

    public void setDelta(double d);

    public void setMaxControlSupport(int n);

    public void setMaxCaseSupport(int n);

    public void setMinCaseSupport(int n);

    public void setMinMarkerNo(int n);

    public void setCtrlStep(int n);

    public void setMaxPatNo(int n);

    public void setMaxCasePatternNo(int n);

    public void setMaxCtrlPatternNo(int n);

    public void start();

    public void stop();

    public boolean getBusy();
}
