package org.geworkbench.components.cytoscape;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public abstract class Cytoscape extends cytoscape.Cytoscape {
    public static cytoscape.view.CytoscapeDesktop getDesktop() {
        if (defaultDesktop == null) {
            defaultDesktop = new CytoscapeDesktop(getCytoscapeObj().getConfiguration().getViewType());
        }
        return defaultDesktop;
    }
}
