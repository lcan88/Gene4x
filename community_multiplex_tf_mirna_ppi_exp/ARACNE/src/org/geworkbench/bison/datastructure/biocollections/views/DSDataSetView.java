package org.geworkbench.bison.datastructure.biocollections.views;


import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
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
 * Provides a view in to a {@link DSDataSet} and its associated {@link DSPanel DSPanels}.
 * The view can be optionally limited to exposing only those elements in active panels.
 *
 * @author Adam Margolin
 * @version 3.0
 */

public interface DSDataSetView <Q extends DSBioObject> {

    public int size();

    public Q get(int index);


    /**
     * @return A DSItemList containing all the <code>Q</code> type objects (generally microarrays)
     *         associated with this <code>DSDataView</code>.
     */
    public DSItemList<Q> items();

    /**
     * Set/reset item subselection based on activated panels.
     */
    public void useItemPanel(boolean status);

    /**
     * Gets the status of item activation.
     *
     */
    public boolean useItemPanel();

    /**
     * Assigns a specific item panel selection.
     */
    public void setItemPanel(DSPanel<Q> mArrayPanel);

    /**
     * Assigns a specific item panel selection.
     */
    public DSPanel<Q> getItemPanel();

    /**
     * Sets the reference {@link DSDataSet} for this <code>DSDataSetView</code>.
     *
     * @param dataSet The new reference dataset.
     */
    public void setDataSet(DSDataSet<Q> dataSet);

    /**
     * Get the <code>DSDataSet</code> object underlying this is view
     *
     * @return The reference <code>DSDataSet</code> object.
     */
    public DSDataSet<Q> getDataSet();

}
