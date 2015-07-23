package org.geworkbench.bison.datastructure.biocollections.views;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

import java.io.Serializable;

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
public class CSDataSetView <Q extends DSBioObject> implements DSDataSetView<Q>, Serializable {

    DSDataSet<Q> dataSet;

    /**
     * Contains the active microarrays, organized as a DSPanel.
     */
    protected DSPanel<Q> itemPanel = new CSPanel<Q>("");

    // protected DSClassCriteria classCriteria = null;

    /**
     * Designates if the microarray subselection imposed by the activated
     * phenotypic categories is imposed on the this microarray set view.
     */
    protected boolean useItemPanel = false;

    public int size() {
        return items().size();
    }

    public Q get(int index) {
        return items().get(index);
    }

    /**
     * @return The microarray at the desiganted index position, if
     *         <code>index</code> is non-negative and no more than the
     *         total number of microarrays in the set. <code>null</code>
     *         otherwise.
     */
    public DSItemList<Q> items() {
        if ((useItemPanel && (itemPanel != null) && (itemPanel.size() > 0)) || dataSet == null) {
            return itemPanel;
        } else {
            //to change
            return dataSet;
        }
    }

    /**
     * Set/reset microarray subselection based on activated phenotypes.
     *
     * @param status
     */
    public void useItemPanel(boolean status) {
        useItemPanel = status;
    }

    /**
     * Gets the statuc of Phenotype Activation
     *
     * @return
     */
    public boolean useItemPanel() {
        return useItemPanel;
    }

    public void setItemPanel(DSPanel<Q> mArrayPanel) {
        this.itemPanel = mArrayPanel;
    }

    public DSPanel<Q> getItemPanel() {
        return itemPanel;
    }

    public void setDataSet(DSDataSet<Q> qs) {
        dataSet = (DSMicroarraySet) qs;
    }

    public DSDataSet<Q> getDataSet() {
        return (DSDataSet) dataSet;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
