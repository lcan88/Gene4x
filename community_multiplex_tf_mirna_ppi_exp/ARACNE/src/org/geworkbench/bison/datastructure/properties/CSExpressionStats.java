package org.geworkbench.bison.datastructure.properties;

import java.util.HashMap;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSExpressionStats {
    /**
     * Mean of marker values over all the cases
     */
    private double meanCases = 0;

    /**
     * Mean of marker values over all the controls
     */
    private double meanControls = 0;

    /**
     * Variance of marker values over all the cases
     */
    private double varianceCases = 0;

    /**
     * Variance of marker values over all the cases
     */
    private double varianceControls = 0;

    /**
     * Number of cases.
     */
    private int numOfCases = 0;

    /**
     * Number of controls
     */
    private int numOfControls = 0;

    private boolean ready = false;

    // Expression values from the control arrays
    private double[] casesValues = null;

    // Expression values from the cases arrays
    private double[] controlsValues = null;

    private HashMap caseDistribution = new HashMap();
    private HashMap controlDistribution = new HashMap();

    public CSExpressionStats() {
    }
}
