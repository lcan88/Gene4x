package org.geworkbench.bison.parsers;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Parse context to customize parsing of data from NCI's remote servers.
 */
public class CaARRAYParseContext implements Serializable {
    /**
     * Lists the names of the attributes that can appear as quantitation
     * types.
     */
    public static final String[] columnNames = {"Avg Diff", "Abs Call", "F635 Median", "F635 Mean", "B635 Median", "B635 Mean", "F532 Median", "F532 Mean", "B532 Median", "B532 Mean", "Log2(ratio)", "P - value", "Intensity", "mean of Log2(ratio)", "mean of ratio", "Log2StdDev", "Signal", "Detection", "Detection p - value"};
    /**
     * Lists the expected data types of the attributes listed in
     * <code>columnNames</code>.
     */
    static final String[] columnTypes = {"Double", "Character", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Double", "Character", "Double"};

    /**
     * Store the subset of feature names (among those defined in
     * <code>columnNames</code> to be used when reading a array values
     * using NCI's MAGE API.
     */
    protected HashMap columnsToUse = new HashMap();

    /**
     * Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {//new ObjectStreamField("columnNames", String[].class),
        //new ObjectStreamField("columnTypes", String[].class),
        new ObjectStreamField("columnsToUse", HashMap.class)};

    /**
     * Initialize a parse contenxt with the list of feature names to collect
     * data from.
     *
     * @param ctu
     */
    public CaARRAYParseContext(List ctu) {
        for (int i = 0; i < columnNames.length; i++) {
            if (ctu.contains(columnNames[i])) {
                columnsToUse.put(columnNames[i], columnTypes[i]);
            }
        }
    }

    /**
     * Return the current contents associated with each of the column names
     * designated in <code>columnsToUse</code>.
     *
     * @return
     */
    public HashMap getColumnsToUse() {
        return columnsToUse;
    }
}
