package org.geworkbench.bison.parsers;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSAffyMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.util.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Encapsulates all the knowledge required for importing in the application
 * data found in files complying to the affymetric format (MAS 4.0/5.0).
 *
 * @author First Genetic Trust
 * @version 1.0
 */
public class AffyParser {
    /**
     * List of the column names (among those available in the Affy file format and
     * listed in {@link geaw.bean.microarrray.util.AffyParseContext#columnNames
     * columnNames}) to be used in building the currrent <code>MicroarraySet</code>.
     */
    private List columnsToUse = null;
    /**
     * Indexing of columns parsed to an <code>Integer</code> key
     */
    private Hashtable columnOrder = new Hashtable();
    /**
     * Stores auxiliary experiment execution information found in the input
     * file.
     */
    protected String experimentInfo = "";
    /**
     * Bit to specify if a header was found in the file being parsed
     */
    private boolean headerFound = false;

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public AffyParser(List ctu) {
        columnsToUse = ctu;
        microarray.setID(RandomNumberGenerator.getID());
    }

    /**
     * New <code>Microarray</code> instance into which the data is loaded on
     * parsing
     */
    private DSMicroarray microarray = null;
    /**
     * Key to walk through columns parsed
     */
    private int featureNo = 0;
    private int columnOrderIndex = 0;
    private int markerIndex = 0;
    private Vector accessions = new Vector();
    private int accessionIndex = 0;

    /**
     * Calculates the number of markers in the dataset
     *
     * @param line String
     */
    public void process(String line) {
        String token = null;
        if (line == null) {
            return;
        }
        if (line.indexOf("Probe Set Name") < 0) {
            if (!headerFound) {
                return;
            }
        } else {
            headerFound = true;
            StringTokenizer st = new StringTokenizer(line, "\t\n");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                if (token.equals("Probe Set Name")) {
                    accessionIndex = columnOrderIndex;
                }
                columnOrderIndex++;
            }

            return;
        }

        StringTokenizer st = new StringTokenizer(line, "\t\n");
        int tokenIndex = 0;
        if (st.hasMoreTokens()) {
            do {
                token = st.nextToken().trim();
                if (accessionIndex == tokenIndex++) {
                    String value = new String(token.toCharArray());
                    accessions.add(value);
                    return;
                }

                if (!st.hasMoreTokens()) {
                    break;
                }
            } while (true);
        }

    }

    /**
     * Retrives the accession values preprocessed from the input datafile
     *
     * @return Vector
     */
    public Vector getAccessions() {
        return accessions;
    }

    /**
     * Resets the parser
     */
    public void reset() {
        headerFound = false;
        columnOrderIndex = 0;
        markerIndex = 0;
    }

    /**
     * Reads the argument line (coming from an Affy MAS 5.0 formatted file)
     * and parses out experiment information and individual marker values. It is
     * assumed that each file contains exactly one microarray. The parsing will
     * result in a <code>MicroarraySet</code> object containing exectly one
     * <code>Microarray</code> (the one described by the file).
     *
     * @param line Input file line.
     */
    public void parseLine(String line) {
        if (microarray == null) {
            microarray = new CSMicroarray(accessions.size());
        }
        String token = null;
        if (line == null) {
            return;
        }
        // If this is not a column header or a data line, just add it to the
        // experiment info.
        if (line.indexOf("Probe Set Name") < 0) {
            if (!headerFound) {
                experimentInfo = experimentInfo.concat("\n" + line);
                return;
            }

        } else {
            // Read in the header line
            headerFound = true;
            StringTokenizer st = new StringTokenizer(line, "\t\n"); // ":,=\t\n\r");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                columnOrderIndex++;
                if (columnsToUse.contains(token)) {
                    columnOrder.put(new Integer(columnOrderIndex), token);
                }

            }

            return;
        }

        StringTokenizer st = new StringTokenizer(line, "\t\n"); // ":,=\t\n\r");
        if (st.hasMoreTokens()) {
            token = st.nextToken().trim();
            int tokenIndex = 1;
            AffyParseContext context = new org.geworkbench.bison.parsers.AffyParseContext(columnsToUse);
            Map ctu = context.getColumnsToUse();
            String type = null;
            Object value = null;
            String column = null;
            do {
                column = (String) columnOrder.get(new Integer(tokenIndex));
                if (column != null) {
                    type = (String) ctu.get(column);
                    if (type.equals("String")) {
                        value = new String(token.toCharArray());
                    } else if (type.equals("Integer")) {
                        value = Integer.valueOf(token);
                    } else if (type.equals("Double")) {
                        value = Double.valueOf(token);
                    } else if (type.equals("Character")) {
                        value = new Character(token.charAt(0));
                    }
                    ctu.put(column, value);
                }

                if (!st.hasMoreTokens()) {
                    break;
                }
                token = st.nextToken().trim();
                tokenIndex++;
            } while (true);
            DSMarkerValue affyMarker = new CSAffyMarkerValue(context);
            microarray.setMarkerValue(markerIndex, affyMarker);
        }

    }

    /**
     * Returns the microarray produced by reading the input file.
     *
     * @return microarray produced by reading the input file.
     */
    public DSMicroarray getMicroarray() {
        return microarray;
    }

    /**
     * Returns the experiment execution information as it was found at the
     * preamble of the input file.
     *
     * @return experiment execution information as it was found at the
     *         preamble of the input file.
     */
    public String getExperimentInfo() {
        return experimentInfo;
    }

}
