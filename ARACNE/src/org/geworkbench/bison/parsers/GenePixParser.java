package org.geworkbench.bison.parsers;

import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSGenepixMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;

import java.util.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

/**
 * Handles the parsing of single array GenePix gpr files.
 */

public class GenePixParser {
    public GenePixParser() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * List of the column names (among those available in the Geenpix gpr file format and
     * listed in {@link org.geworkbench.bison.parsers.GenepixParseContext#columnNames
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

    public GenePixParser(List ctu) {
        columnsToUse = ctu;
    }

    //added for Genepix flags filter.
    private boolean isFlagged = false;
    private TreeSet<String> flagsValue = new TreeSet<String>();


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
        if (line.indexOf("F635 Median") < 0) {
            if (!headerFound) {
                return;
            }
        } else {
            headerFound = true;
            StringTokenizer st = new StringTokenizer(line, "\t\n");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                if (token.equals("ID") || token.equals("\"ID\"")) {
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
        isFlagged = false;
        flagsValue = new TreeSet<String>();
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

    /**
     * Reads the argument line (coming from an Genepix gpr formatted file)
     * and parses out experiment information and individual marker values. It is
     * assumed that each file contains exactly one microarray. The parsing will
     * result in a <code>MicroarraySet</code> object containing exactly one
     * <code>Microarray</code> (the one described by the file).
     *
     * @param line Input file line.
     */
    public void parseLine(String line) {
        String token = null;
        if (line == null) {
            return;
        }
        // If this is not a column header or a data line, just add it to the
        // experiment info.
        if (line.indexOf("F635 Median") < 0) {
            if (!headerFound) {
                experimentInfo = experimentInfo.concat("\n" + line);
                String[] propval = line.split(":");
                if (propval.length >= 2) {
                    props.put(propval[0].toString(), propval[1].toString());
                }
                return;
            }

        } else {
            // Read in the header line
            headerFound = true;
            StringTokenizer st = new StringTokenizer(line, "\t\n");
            while (st.hasMoreTokens()) {
                token = st.nextToken().trim();
                if (token.startsWith("\"") && token.endsWith("\"")) {
                    token = token.split("\"")[1];
                }
                columnOrderIndex++;
                if (columnsToUse.contains(token)) {
                    columnOrder.put(new Integer(columnOrderIndex), token);
                }
            }
            return;
        }

        StringTokenizer st = new StringTokenizer(line, "\t\n");
        if (st.hasMoreTokens()) {
            token = st.nextToken().trim();
            int tokenIndex = 1;
            GenepixParseContext context = new org.geworkbench.bison.parsers.
                    GenepixParseContext(columnsToUse);
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
                    if (value != null) {
                        ctu.put(column, value);
                    }
                }

                if (!st.hasMoreTokens()) {
                    break;
                }
                token = st.nextToken().trim();
                tokenIndex++;
            } while (true);
            populateValues(ctu,
                    (DSGenepixMarkerValue) microarray.
                            getMarkerValue(markerIndex++));
        }
    }

    /**
     * Calculate the signal value from the channel values.
     */
    private void populateValues(Map columns, DSGenepixMarkerValue gmv) {
        Object value = null;
        double ch1f = 0d, ch2f = 0d, ch1b = 0d, ch2b = 0d, ratio = 0d;
        int flag = 0;

        boolean medianMissing = false;
        if (!CSGenepixMarkerValue.getComputeSignalMethod().usesMean()) {
            if (columns.containsKey("F532 Median")) {
                value = columns.get("F532 Median");
                if (value instanceof Double) {
                    ch1f = ((Double) value).doubleValue();
                }
            } else {
                medianMissing = true;
            }
            if (columns.containsKey("B532 Median")) {
                value = columns.get("B532 Median");
                if (value instanceof Double) {
                    ch1b = ((Double) value).doubleValue();
                }
            } else {
                medianMissing = true;
            }
            if (columns.containsKey("F635 Median")) {
                value = columns.get("F635 Median");
                if (value instanceof Double) {
                    ch2f = ((Double) value).doubleValue();
                }
            } else {
                medianMissing = true;
            }
            if (columns.containsKey("B635 Median")) {
                value = columns.get("B635 Median");
                if (value instanceof Double) {
                    ch2b = ((Double) value).doubleValue();
                }
            } else {
                medianMissing = true;
            }
        }
        if (columns.containsKey("Flags")) {
            value = columns.get("Flags");
            if (value instanceof String) {

                if (!value.equals("0")) {
                    isFlagged = true;
                    flagsValue.add((String) value);
                }

            }
            gmv.setFlag((String) value);
        }

        if (CSGenepixMarkerValue.getComputeSignalMethod().usesMean() || medianMissing) {
            if (columns.containsKey("F532 Mean")) {
                value = columns.get("F532 Mean");
                if (value instanceof Double) {
                    ch1f = ((Double) value).doubleValue();
                }
            }
            if (columns.containsKey("B532 Mean")) {
                value = columns.get("B532 Mean");
                if (value instanceof Double) {
                    ch1b = ((Double) value).doubleValue();
                }
            }
            if (columns.containsKey("F635 Mean")) {
                value = columns.get("F635 Mean");
                if (value instanceof Double) {
                    ch2f = ((Double) value).doubleValue();
                }
            }
            if (columns.containsKey("B635 Mean")) {
                value = columns.get("B635 Mean");
                if (value instanceof Double) {
                    ch2b = ((Double) value).doubleValue();
                }
            }
        }
        if (columns.containsKey("Ratio of Means")) {
            value = columns.get("Ratio of Means");
            if (value instanceof Double) {
                ratio = ((Double) value).doubleValue();
            }
        }

        gmv.setCh1Fg(ch1f);
        gmv.setCh1Bg(ch1b);
        gmv.setCh2Fg(ch2f);
        gmv.setCh2Bg(ch2b);
        
//        double val = 0d;
//        if (ch2f != ch2b) {
//            val = (ch1f - ch1b) / (ch2f - ch2b);
//        } else {
//            val = (ch1f - ch1b);
//        }
//        gmv.setValue(val);
//        gmv.setMissing(false);
        gmv.computeSignal();
    }

    private void jbInit() throws Exception {
    }
}
