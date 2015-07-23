package org.geworkbench.bison.parsers;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.util.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * <p/>
 * Encapsulates all the knowledge required for importing
 * data from files that comply to the Genepix data format.
 *
 * @author First Genetic Trust Inc.
 * @version 1.0
 */
public class GenepixParser2 {
    /**
     * New <code>Microarray</code> instance into which the data is loaded on
     * parsing
     */
    private DSMicroarray microarray = null;
    /**
     * Key to walk through columns parsed
     */
    private int columnOrderIndex = 0;
    private int microarrayId = 0;
    /**
     * List of column names to use while parsing
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
     * Marker Accessions returned by parsing inputfile
     */
    private Vector accessions = new Vector();

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public GenepixParser2(List ctu) {
        columnsToUse = ctu;
    }

    private int accessionIndex = 0;

    /**
     * Calculates the number of markers in the dataset
     *
     * @param line String
     */
    public void process(String line) {
        String token = null;
        if (line == null)
            return;
        if (line.indexOf("F635 Median") < 0) {
            if (!headerFound)
                return;
        } else {
            headerFound = true;
            StringTokenizer st = new StringTokenizer(line, "\t\n");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                if (token.equals("\"ID\"") || token.equals("ID"))
                    accessionIndex = columnOrderIndex;
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

                if (!st.hasMoreTokens())
                    break;
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
        microarrayId = 0;
    }

    /**
     * Reads the argument line (coming from an Genepix formatted file)
     * and parses out experiment information and individual marker values. It is
     * assumed that each file contains exactly one microarray. The parsing will
     * result in a <code>MicroarraySet</code> object containing exectly one
     * <code>Microarray</code> (the one described by the file).
     *
     * @param line      Input file line.
     * @param mArraySet The result microarray set to be filled with data.
     */
    public void parseLine(String line) {
        String token = null;
        if (line == null)
            return;
        // If this is not a column header or a data line, just add it to the
        // experiment info.
        if (line.indexOf("F635 Median") < 0) {
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
                // remove the leading and trailing " characters
                token = token.substring(1, token.length() - 1);
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
            org.geworkbench.bison.parsers.GenepixParseContext context = new GenepixParseContext(columnsToUse);
            HashMap ctu = context.getColumnsToUse();
            String type = null;
            Object value = null;
            String column = null;
            do {
                column = (String) columnOrder.get(new Integer(tokenIndex));
                if (column != null) {
                    type = (String) ctu.get(column);
                    if (type.equals("String"))
                        value = new String(token.toCharArray());
                    else if (type.equals("Integer"))
                        value = Integer.valueOf(token);
                    else if (type.equals("Double"))
                        value = Double.valueOf(token);
                    else if (type.equals("Character"))
                        value = new Character(token.charAt(0));
                    ctu.put(column, value);
                }

                if (!st.hasMoreTokens())
                    break;
                token = st.nextToken().trim();
                tokenIndex++;
            } while (true);
            DSGenepixMarkerValue gpixMarker = new CSGenepixMarkerValue(context);
            if (microarray == null) {
                // Create the microarray if not previously done
                microarray = new CSMicroarray(accessions.size());
            }
            microarray.setMarkerValue(microarrayId, gpixMarker);
        }

    }

    /**
     * Returns the microarray produced by reading the input file.
     *
     * @return
     */
    public DSMicroarray getMicroarray() {
        return microarray;
    }

    /**
     * Returns the experiment execution information as it was found at the
     * preamble of the input file.
     *
     * @return
     */
    public String getExperimentInfo() {
        return experimentInfo;
    }

}

