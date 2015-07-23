package org.geworkbench.bison.parsers;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Parse context to customize parsing of Genepix files
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class GenepixParseContext implements Serializable {
    /**
     * List of possible column names from input file that can be parsed
     */
    public static final String[] columnNames = {"Block", "Column", "Row", "Name", "ID", "X", "Y", "Dia.", "F635 Median", "F635 Mean", "F635 SD", "B635 Median", "B635 Mean", "B635 SD", "% > B635+1SD", "% > B635+2SD", "F635 % Sat.", "F532 Median", "F532 Mean", "F532 SD", "B532 Median", "B532 Mean", "B532 SD", "% > B532+1SD", "% > B532+2SD", "F532 % Sat.", "Ratio of Medians", "Ratio of Means", "Median of Ratios", "Mean of Ratios", "Ratios SD", "Rgn Ratio", "Rgn R²", "F Pixels", "B Pixels", "Sum of Medians", "Sum of Means", "Log Ratio", "Flags"};
    /**
     * Column datatypes for the columns listed in <code>columnNames</code>
     * in the same order
     */
    //Change Flags Values as String, following the request of Aris.

    static final String[] columnTypes = {"Integer", "Integer", "Integer", "String", "String", "Integer", "Integer", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Integer", "Integer", "Double", "Double", "Double", "String"};
    /**
     * List of the column names (among those available in the Genepix file format and
     * listed in {@link org.geworkbench.bison.parsers.GenepixParseContext#columnNames
     * columnNames}) to be used in building the currrent <code>MicroarraySet</code>.
     */
    private HashMap columnsToUse = new HashMap();
    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("columnNames", String[].class), new ObjectStreamField("columnTypes", String[].class), new ObjectStreamField("columnsToUse", HashMap.class)};

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public GenepixParseContext(List ctu) {
        for (int i = 0; i < columnNames.length; i++) {
            if (ctu.contains(columnNames[i])) {
                columnsToUse.put(columnNames[i], columnTypes[i]);
            }

        }

    }

    /**
     * Gets the columns to use for parsing
     *
     * @return columns to use for parsing
     */
    public HashMap getColumnsToUse() {
        return columnsToUse;
    }

}
