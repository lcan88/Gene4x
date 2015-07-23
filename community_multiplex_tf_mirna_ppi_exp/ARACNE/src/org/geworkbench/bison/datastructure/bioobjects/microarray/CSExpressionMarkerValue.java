package org.geworkbench.bison.datastructure.bioobjects.microarray;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class CSExpressionMarkerValue extends CSMarkerValue implements DSAffyMarkerValue, Serializable {

    /**
     * Formats values to be displayed
     */
    protected static DecimalFormat formatter = new DecimalFormat("##.##");

    public CSExpressionMarkerValue() {
    }

    /**
     * Constructor
     *
     * @param v double expression value
     */
    public CSExpressionMarkerValue(float v) {
        value = v;
    }

    /**
     * Constructor
     *
     * @param jme MarkerExpression marker from which this marker is to be cloned
     */
    public CSExpressionMarkerValue(CSExpressionMarkerValue jme) {
        this.confidence = jme.confidence;
        this.value = jme.value;
    }

    /**
     * Display representation of this marker
     *
     * @return String representation
     */
    public String representation() {
        String representation = new String("V:" + value);
        return representation;
    }

    /**
     * Gets a <code>String</code> representation of this marker containing status
     * along with the actual expression value
     *
     * @return String
     */
    public String toString() {
        String string = null;
        ;
        String mask = null;
        if (isMasked()) {
            mask = new String("X");
        } else {
            mask = new String("");
        }
        if (!isMissing()) {
            string = new String(formatter.format(getValue()) + "\t" + getStatusAsChar());
        } else {
            string = new String("?" + "\t" + getStatusAsChar());
        }
        return string;
    }

    /**
     * This method returns the dimensionality of the marker. Since expression
     * levels are one-dimensional
     *
     * @return Always one
     */
    public int getDimensionality() {
        return 1;
    }

    public DSMarkerValue deepCopy() {
        DSMarkerValue copy = new CSExpressionMarkerValue(this);
        return (DSMarkerValue) copy;
    }

    public int compareTo(Object o) {
        return Double.compare(((CSAffyMarkerValue) o).getValue(), getValue());
    }
}
