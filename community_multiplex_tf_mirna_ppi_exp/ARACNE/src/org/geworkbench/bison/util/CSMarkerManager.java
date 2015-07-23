package org.geworkbench.bison.util;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import java.util.Map;
import java.util.WeakHashMap;

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
 * @deprecated Panels can be obtained from {@link org.geworkbench.bison.annotation.DSAnnotationContext}, which is
 * retrieved from the {@link org.geworkbench.bison.annotation.DSAnnotationContextManager}.
 */
public class CSMarkerManager {
    /**
     * This class implements a singleton instance to manage all dataset criteria
     * a criteria (i.e. criteria collection) can be associated with a data set via
     * the manager and retrieved based on the data set.
     */
    protected CSMarkerManager() {
    }

    static final CSMarkerManager manager = new CSMarkerManager();
    // With a weak hashmap, the map entry will be removed automatically once DSDataSet has no hard references remaining
    Map<DSDataSet, DSPanel> markerMap = new WeakHashMap<DSDataSet, DSPanel>();

    static public DSPanel<DSGeneMarker> getMarkerPanel(DSDataSet dataSet) {
        return manager.getMarkerPanelHelper(dataSet);
    }

    static public void setMarkerPanel(DSDataSet dataSet, DSPanel<DSGeneMarker> criteria) {
        manager._setMarkerPanel(dataSet, criteria);
    }

    private DSPanel<DSGeneMarker> getMarkerPanelHelper(DSDataSet dataSet) {
        DSPanel<DSGeneMarker> panel = markerMap.get(dataSet);
        if (panel == null) {
            panel = new CSPanel<DSGeneMarker>("");
            markerMap.put(dataSet, panel);
        }
        return panel;
    }

    private void _setMarkerPanel(DSDataSet dataSet, DSPanel<DSGeneMarker> panel) {
        markerMap.put(dataSet, panel);
    }
}
