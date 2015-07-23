package org.geworkbench.bison.parsers;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Parse context to customize parsing of Affymetrix files
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class AffyParseContext implements Serializable {
    /**
     * List of possible column names from input file that can be parsed
     */
    public static final String[] columnNames = {"Probe Set Name", "Stat Pairs", "Stat Pairs Used", "Signal", "Detection", "Detection p-value", "Stat Common Pairs", "Signal Log Ratio", "Signal Log Ratio Low", "Signal Log Ratio High", "Change", "Change p-value", "Positive", "Negative", "Pairs", "Pairs Used", "Pairs InAvg", "Log Avg", "Pos/Neg", "Avg Diff", "Abs Call", "Inc", "Dec", "Inc Ratio", "Dec Ratio", "Pos Change", "Neg Change", "Inc/Dec", "DPos-DNeg Ratio", "Log Avg Ratio Change", "Diff Call", "Avg Diff Change", "B=A", "Fold Change", "Sort Score"};
    /**
     * Column datatypes for the columns listed in <code>columnNames</code>
     * in the same order
     */
    static final String[] columnTypes = {"String", "Integer", "Integer", "Double", "Character", "Double", "Integer", "Double", "Double", "Double", "String", "Double", "Integer", "Integer", "Integer", "Integer", "Integer", "Double", "Double", "Double", "Character", "Integer", "Integer", "Double", "Double", "Integer", "Integer", "Double", "Double", "Double", "String", "Double", "Character", "Double", "Double"};
    /**
     * List of the column names (among those available in the Affymetrix file format and
     * listed in {@link org.geworkbench.bison.parsers.AffyParseContext#columnNames
     * columnNames}) to be used in building the current <code>MicroarraySet</code>.
     */
    Hashtable columnsToUse = new Hashtable();
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {//new ObjectStreamField("columnNames", String[].class),
        //new ObjectStreamField("columnTypes", String[].class),
        new ObjectStreamField("columnsToUse", Hashtable.class)};

    /**
     * Default Constructor
     */
    public AffyParseContext() {
    }

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public AffyParseContext(List ctu) {
        columnsToUse.clear();
        for (int i = 0; i < columnNames.length; i++) {
            if (ctu.contains(columnNames[i])) {
                columnsToUse.put(new String(columnNames[i]), new String(columnTypes[i]));
            }

        }

    }

    /**
     * Gets the columns to use for parsing
     *
     * @return columns to use for parsing
     */
    public Map getColumnsToUse() {
        return columnsToUse;
    }

    /**
     * Generates a deep copy of this parse context
     *
     * @return
     */
    public AffyParseContext deepCopy() {
        AffyParseContext copy = null;
        try {
            copy = (AffyParseContext) this.getClass().newInstance();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }

        copy.columnsToUse.putAll(columnsToUse);
        return copy;
    }

}
