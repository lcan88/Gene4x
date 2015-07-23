package org.geworkbench.bison.datastructure.biocollections.views;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;


/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public interface DSMicroarraySetView <T extends DSGeneMarker, Q extends DSMicroarray> extends DSMatrixDataSetView<Q> {

    public double getValue(T object, int arrayIndex);

    public double getMeanValue(T marker, int maIndex);

    public double[] getRow(T object);

    public void setMicroarraySet(DSMicroarraySet<Q> ma);

    public DSMicroarraySet<Q> getMicroarraySet();

    // watkin - Moved from DSDataSetView

    /**
     * @return A DSItemList containing all the <code>T</code> type objects (generally markers)
     *         associated with this <code>DSDataView</code>.
     */
    public DSItemList<T> markers();

    /**
     * Set/resets marker subselection based on activated panels.
     *
     * @param status
     */
    public void useMarkerPanel(boolean status);

    /**
     * Gets the status of marker activation
     *
     * @return the status of marker activation
     */
    public boolean useMarkerPanel();

    /**
     * Allows to assign a specific microarray panel selection
     *
     * @param markerPanel DSPanel
     */
    public void setMarkerPanel(DSPanel<T> markerPanel);

    /**
     * Allows to retrieve the marker panel selection
     */
    public DSPanel<T> getMarkerPanel();

    public DSItemList<T> allMarkers();

    public Q get(int index);

    DSItemList<T> getUniqueMarkers();
}
