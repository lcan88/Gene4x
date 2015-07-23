package org.geworkbench.events;

import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.util.EventObject;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * <code>EventObject</code> encapsulating <code>MicroarraySet</code> with
 * Panel and Phenotype groupings
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class MicroarraySetViewEvent <T extends DSGeneMarker, Q extends DSMicroarray> extends EventObject {
    /**
     * The underlying <code>MicorarraySet</code> containing Panel and
     * Phenotype groupings
     */
    private DSMicroarraySetView<T, Q> microarraySetModel = null;

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>MicroarraySetViewEvent</code>
     */
    public MicroarraySetViewEvent(Object source) {
        super(source);
    }

    /**
     * Constructor
     *
     * @param source <code>Object</code> generating this
     *               <code>MicroarraySetViewEvent</code>
     * @param model  <code>MicroarraySetView</code> containing
     *               <code>MicroaraySet</code> views
     */
    public MicroarraySetViewEvent(Object source, DSMicroarraySetView<T, Q> model) {
        super(source);
        microarraySetModel = model;
    }

    /**
     * Gets the <code>MicroarraySetView</code> containing
     * <code>MicroaraySet</code> views
     *
     * @return <code>MicroarraySetView</code> containing
     *         <code>MicroaraySet</code> views
     */
    public DSMicroarraySetView<T, Q> getModel() {
        return microarraySetModel;
    }

}

