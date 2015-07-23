package org.geworkbench.bison.datastructure.bioobjects.microarray;

import org.geworkbench.bison.datastructure.bioobjects.markers.genotype.CSGenotypeMarker;
import org.geworkbench.bison.util.Range;

import java.awt.*;
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

public class CSGenotypicMarkerValue extends CSMarkerValue implements Serializable, DSGenotypicMarkerValue {

    /**
     * Formats values to be displayed
     */
    protected static DecimalFormat formatter = new DecimalFormat("##.##");

    /**
     * Bit to specify if this marker contains two alleles
     */
    public boolean isGT = true;

    private short allele0;
    private short allele1;

    /**
     * Constructor
     *
     * @param mg MarkerGenotype to be cloned
     */
    public CSGenotypicMarkerValue(CSGenotypicMarkerValue mg) {
        setGenotype(mg.allele0, mg.allele1);
        confidence = mg.confidence;
        isGT = mg.isGT;
    }

    /**
     * Constructor to create this marker from two alleles
     *
     * @param a1 int allele
     * @param a2 int allele
     */
    public CSGenotypicMarkerValue(int a1, int a2) {
        setGenotype((short)a1, (short)a2);
    }

    /**
     * Constructor to create this marker from one allele
     *
     * @param allele_1 int
     */
    public CSGenotypicMarkerValue(int allele_1) {
        allele0 = (short)allele_1;
        value = allele0;
        isGT = false;
    }

    /**
     * Sets the primary allele
     *
     * @param allele int
     */
    public void setAllele(int allele) {
        allele0 = (short)allele;
        isGT = false;
    }

    /**
     * Sets genotype
     *
     * @param allele0 int
     * @param allele1 int
     */
    public void setGenotype(int allele0, int allele1) {
        if (allele1 == 0) {
            value = 0;
        } else {
            value = allele0 + allele1 * (1 << 16);        
        }
        this.allele0 = (short)allele0;
        this.allele1 = (short)allele1;
        isGT = true;
    }

    /**
     * Gets either of the two alleles
     *
     * @param id int either of the two dimensions
     * @return int allele as int
     */
    public short getAllele(int id) {
        switch (id) {
            case 0:
                return allele0;
            case 1:
                return allele1;
        }
        return 0;
    }

    /**
     * Obtains a <code>String</code> representation of the genotype
     *
     * @return String representation
     */
    public String representation() {
        String representation = null;
        if (isGT) {
            representation = new String("G:" + getAllele(0) + "|" + getAllele(1));
        } else {
            representation = new String("A:" + getAllele(0));
        }
        return representation;
    }

    /**
     * Gets a <code>String</code> representation of this marker
     *
     * @return String
     */
    public String toString() {
        String string = null;
        if (!isMissing()) {
            string = getAllele(0) + "|" + getAllele(1);
            //            string = new String(formatter.format(getValue()) + "\t" +
            //                                getStatusAsChar());
        } else {
            string = "?";
        }
        return string;
    }

    /**
     * @param m IMarker
     * @return boolean
     */
    public boolean equals(DSMarkerValue m) {
        CSGenotypicMarkerValue aMarker = (CSGenotypicMarkerValue) m;
        if ((aMarker == null) || (isAbsent() || aMarker.isAbsent())) {
            return false;
        }
        return ((isGT == aMarker.isGT) && (value == aMarker.value));
    }

    /**
     * This method returns the dimensionality of the marker. Genotype markers
     * are 2-dimensional while Allele/Haplotype markers are 1-dimensional
     *
     * @return the dimensionality of the marker.
     */
    public int getDimensionality() {
        if (isGT) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Gets a copy of this marker
     *
     * @return MarkerValue
     */
    public DSMarkerValue deepCopy() {
        DSMarkerValue copy = new CSGenotypicMarkerValue(this);
        return copy;
    }

    public void parse(String signal, int gtBase) {
        short a1;
        short a2;
        String[] parseableValue = signal.split(":");
        String[] allele = parseableValue[parseableValue.length - 1].split("[| /]");
        switch (allele.length) {
            case 1:
                int v = Short.parseShort(allele[0]);
                setGenotype((short)(v / gtBase), (short)(v % gtBase));
                setMissing(v == 0);
                break;
            case 2:
                a1 = Short.parseShort(allele[0]);
                a2 = Short.parseShort(allele[1]);
                setGenotype(a1, a2);
                setMissing((a1 == 0) || (a2 == 0));
                break;
            default:
                a1 = Short.parseShort(allele[0]);
                a2 = Short.parseShort(allele[allele.length - 1]);
                setGenotype(a1, a2);
                setMissing((a1 == 0) || (a2 == 0));
                break;
        }
    }

    public void parse(String signal, String status, int gtBase) {
        try {
            char c = status.charAt(0);
            if (Character.isLowerCase(c)) {
                mask();
            }
            parse(signal, gtBase);
        } catch (NumberFormatException e) {
            setGenotype((short)0, (short)0);
            setMissing(true);
        }
    }

    public Class getMarkerStatsClass() {
        return CSGenotypeMarker.class;
    }

    public Color getColor(org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker stats, float intensity) {
        CSGenotypeMarker gtStats = (CSGenotypeMarker) stats;
        org.geworkbench.bison.util.Range range = gtStats.getRange();
        Color color = null;
        float v = (float) ((value - (range.max + range.min) / 2) / (range.max - range.min)) * intensity;
        if (v > 0) {
            v = Math.min(1.0F, v);
            color = new Color(0F, v, 0F);
        } else {
            v = Math.min(1.0F, -v);
            color = new Color(0F, 1F, v);
        }
        return color;
    }

    public Color getAbsColor(org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker stats, float intensity) {
        CSGenotypeMarker gtStats = (CSGenotypeMarker) stats;
        Range range = gtStats.getRange();
        if (getAllele(0) == getAllele(1)) {
            if (getAllele(0) == 1) {
                return Color.yellow;
            } else if (getAllele(0) == 2) {
                return Color.yellow.darker();
            }
            float v = (float) getAllele(0) / (float) range.max * intensity;
            v = Math.max(v, -1.0F);
            v = Math.min(v, +1.0F);
            return new Color(v, 0F, 0F);
        } else {
            float v1 = (float) getAllele(0) / (float) range.max * intensity;
            v1 = Math.max(v1, -1.0F);
            v1 = Math.min(v1, +1.0F);
            float v2 = (float) getAllele(1) / (float) range.max * intensity;
            v2 = Math.max(v2, -1.0F);
            v2 = Math.min(v2, +1.0F);
            return new Color(v1, v2, v2);
        }
    }

    public double getValue() {
        if (isGT) {
            return value;
        } else {
            return allele0;
        }
    }

    public int compareTo(Object o) {
        return Double.compare(((CSAffyMarkerValue) o).getValue(), getValue());
    }


}
