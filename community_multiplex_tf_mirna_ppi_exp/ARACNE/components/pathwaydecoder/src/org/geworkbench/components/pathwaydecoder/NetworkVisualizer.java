package org.geworkbench.components.pathwaydecoder;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public interface NetworkVisualizer {
    public void showName(String name);

    public void showAffyId(String name);

    public void showProcess(String process);

    public void showFunction(String function);

    public void buildSubGraph(DSGeneMarker marker);

    public void showGraph(int x, int y, int gene1, int gene2);
}
