package org.geworkbench.bison.parsers;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSAffyMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 *
 * @todo - Phase out as there a class called AffyParser. Also, remove dependency from Bison.
 */

/**
 * Handles the parsing of single array Affymetrix txt files.
 */

public class AffymetrixParser {

    public static final char DETECTION_ABSENT = 'A';
    public static final char DETECTION_MARGINAL = 'M';
    public static final char DETECTION_PRESENT = 'P';

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
     * The experiment information stored as properties
     */
    protected Hashtable props = new Hashtable();

    /**
     * Bit to specify if a header was found in the file being parsed
     */
    private boolean headerFound = false;

    /**
     * Constructor
     *
     * @param ctu columns to use for parsing
     */
    public AffymetrixParser(List ctu) {
        columnsToUse = ctu;
    }

    /**
     * New <code>Microarray</code> instance into which the data is loaded on
     * parsing
     */
    private DSMicroarray microarray = null;

    private int markerIndex = 0;

    /**
     * Key to walk through columns parsed
     */
    private int columnOrderIndex = 0;

    private Vector accessions = new Vector();

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
        if (line.indexOf("Probe Set Name") < 0) {
            if (!headerFound)
                return;
        } else {
            headerFound = true;
            // Some files (GCOS) have a blank column header for the first column.
            if (line.startsWith("\t")) {
                columnOrderIndex++;
            }
            StringTokenizer st = new StringTokenizer(line, "\t\n");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                if (token.equals("Probe Set Name"))
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
    }

    /**
     * Reads the argument line (coming from an Affy MAS 5.0 formatted file)
     * and parses out experiment information and individual marker values. It is
     * assumed that each file contains exactly one microarray. The parsing will
     * result in a <code>MicroarraySet</code> object containing exactly one
     * <code>Microarray</code> (the one described by the file).
     *
     * @param line Input file line.
     */
    public void parseLine(String line) {
        String token = null;
        if (line == null)
            return;
        // If this is not a column header or a data line, just add it to the
        // experiment info.
        if (line.indexOf("Probe Set Name") < 0) {
            if (!headerFound) {
                experimentInfo = experimentInfo.concat("\n" + line);
                String[] propval = line.split(":");
                if (propval.length >= 2)
                    props.put(propval[0].toString(), propval[1].toString());
                return;
            }

        } else {
            // Read in the header line
            headerFound = true;
            // Some files (GCOS) have a blank column header for the first column.
            if (line.startsWith("\t")) {
                columnOrderIndex++;
            }
            StringTokenizer st = new StringTokenizer(line, "\t\n");
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
                    if (type.equals("String"))
                        value = new String(token.toCharArray());
                    else if (type.equals("Integer"))
                        value = Integer.valueOf(token);
                    else if (type.equals("Double"))
                        value = Double.valueOf(token);
                    else if (type.equals("Character"))
                        value = new Character(token.charAt(0));
                    if (value != null)
                        ctu.put(column, value);
                }

                if (!st.hasMoreTokens())
                    break;
                token = st.nextToken().trim();
                tokenIndex++;
            } while (true);

            ( (CSAffyMarkerValue) microarray.getMarkerValue(markerIndex++)).init(context);
            //AffyMarkerValue affyMarker = new AffyMarkerValueImpl(context);
            //microarray.addMarkerValue(affyMarker);
        }
    }


    /**
     * Gets property based on experiment information
     */
    public String getProperty(String prop) {
        return (String) props.get(prop);
    }

    /**
     * Sets the <code>Microarray</code> to be populated
     *
     * @param m IMicroarray
     */
    public void setMicroarray(DSMicroarray m) {
        microarray = m;
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
