package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.*;
import org.geworkbench.bison.util.RandomNumberGenerator;
import org.geworkbench.bison.util.Range;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSExprMicroarraySet extends CSMicroarraySet<DSMicroarray> implements Serializable {

    private HashMap properties = new HashMap();
    private ArrayList descriptions = new ArrayList();

    private org.geworkbench.bison.parsers.DataParseContext dataContext = new org.geworkbench.bison.parsers.DataParseContext();

    private ReaderMonitor createProgressReader(String display, File file) throws FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(null, display, fileIn);
        ReaderMonitor retValue = new ReaderMonitor();

        retValue.pm = progressIn.getProgressMonitor();
        retValue.reader = new BufferedReader(new InputStreamReader(progressIn));
        return retValue;
    }

    public CSExprMicroarraySet() {
        super(RandomNumberGenerator.getID(), "");
        super.type = DSMicroarraySet.expPvalueType;
        /** @todo Remove if not used */
        //        addObject(DSRangeMarker.class, CSExpressionMarker.class);
        addDescription("Microarray experiment");
        DSAnnotationContext<DSMicroarray> context = CSAnnotationContextManager.getInstance().getCurrentContext(this);
        CSAnnotationContext.initializePhenotypeContext(context);
    }

    public File getFile() {
        return file;
    }

    public void put(Object key, Object value) {
        properties.put(key, value);
    }

    public Object get(Object key) {
        return properties.get(key);
    }

    public String[] getDescriptions() {
        String[] descr = new String[descriptions.size()];
        for (int i = 0; i < descriptions.size(); i++) {
            descr[i] = (String) descriptions.get(i);
        }
        return descr;
    }

    public void removeDescription(String descr) {
        descriptions.remove(descr);
    }

    public void addDescription(String descr) {
        descriptions.add(descr);
    }

    public String getDataSetName() {
        return label;
    }

    public boolean loadingCancelled = false;

    public void read(File _file) {
        file = _file;
        label = file.getName();
        readFromFile(file);
    }

    public String getName() {
        return label;
    }

    public void readFromFile(File file) {
        currGeneId = 0;
        CMicroarrayParser parser = new CMicroarrayParser();
        ReaderMonitor rm = null;
        this.label = file.getName();
        this.absPath = file.getAbsolutePath();
        try {
            rm = createProgressReader("Getting structure information from " + file.getName(), file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        String line;
        try {
            while ((line = rm.reader.readLine()) != null) {
                parser.parseLine(line, this);
                if (rm.pm != null) {
                    if (rm.pm.isCanceled()) {
                        loadingCancelled = true;
                        rm.reader.close();
                        return;
                    }
                }
            }
            rm.reader.close();
            if (this.getCompatibilityLabel() == null) {
//                AnnotationParser.callUserDefinedAnnotation();
            }
        } catch (InterruptedIOException iioe) {
            iioe.printStackTrace();
            loadingCancelled = true;
            return;
        } catch (Exception ioe) {
            ioe.printStackTrace();
            return;
        } finally {
        }
        try {
            rm = createProgressReader("Loading Data from " + file.getName(), file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        initialize(parser.microarrayNo, parser.markerNo);
        try {
            int i = 1;
            while ((line = rm.reader.readLine()) != null) {
                parser.executeLine(line, this);
                if (rm.pm.isCanceled()) {
                    loadingCancelled = true;
                    rm.reader.close();
                    return;
                }
                if (i % 1000 == 0) {
                    System.gc();
                }
            }
        } catch (InterruptedIOException iioe) {
            loadingCancelled = true;
            return;
        } catch (Exception ioe) {
            System.out.println("Error while parsing line: " + line);
            ioe.printStackTrace();
            return;
        } finally {
            //            setPhenotype();

        }
    }

    public boolean initialized = false;

    public void initialize(int maNo, int mrkNo) {
        // this is required so that the microarray vector may create arrays of the right size
        for (int microarrayId = 0; microarrayId < maNo; microarrayId++) {
            add(microarrayId, new CSMicroarray(microarrayId, mrkNo, "Test", null, null, false, type));
        }

        for (int i = 0; i < mrkNo; i++) {
            CSExpressionMarker mi = new CSExpressionMarker();
            mi.reset(i, maNo, mrkNo);
            markerVector.add(i, mi);
        }

        initialized = true;
    }

    int currGeneId = 0;

    private class CMicroarrayParser {
        //total number of microarry
        public int microarrayNo = 0;
        public int markerNo = 0;

        //total number of properties
        public int propNo = 0;
        Vector phenotypes = new Vector();
        int phenotypeNo = 0;

        int currMicroarrayId = 0;

        void executeLine(String line, DSMicroarraySet<DSMicroarray> mArraySet) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            if (line.charAt(0) == '#') {
                return; //
            }
            //Ask Manjunath why the tokenizer was replaced with a split
            // Sun advices that: "StringTokenizer is a legacy class that is retained
            // for compatibility reasons although its use is discouraged in new code."
            // http://java.sun.com/j2se/1.4.2/docs/api/java/bisonparsers/StringTokenizer.html
            // - Manju
            // watkin - I changed it back to a StringTokenizer, as it will ignore redundant delimiters (tabs)
            // while String.split will not.
            StringTokenizer tokenizer = new StringTokenizer(line, "\t", false);
            int n = tokenizer.countTokens();
            String[] st = new String[n];
            for (int i = 0; i < n; i++) {
                st[i] = tokenizer.nextToken();
            }

            if (st.length > 0) {
                String token = st[0];
                /**
                 * This handles the first line, which contains the microarray labels
                 * separated by tabs.
                 */
                if (token.equalsIgnoreCase("PDFModel")) {
                } else if (token.contains("ID")) {
                    //          String phLabel = st[1];
                    //          AnnotationParser.reset(phLabel);
                    boolean isAccession = true; //phLabel.equalsIgnoreCase("Anotation");
                    int i = 0;
                    //read the first line and put label of the arrays in.
                    for (int j = 2; j < st.length; j++) {
                        if (isAccession) {
                            String maLabel = new String(st[j]);
                            get(i++).setLabel(maLabel);
                        }
                    }
                } else if (token.equalsIgnoreCase("Description")) {
                    //This handles all the phenotype definition lines
                    String phLabel = new String(st[1]);
                    int i = 0;
                    DSAnnotationContext<DSMicroarray> context = manager.getContext(mArraySet, phLabel);
                    CSAnnotationContext.initializePhenotypeContext(context);
                    for (int j = 2; j < st.length; j++) {
                        String valueLabel = new String(st[j]);
                        if ((valueLabel != null) && (!valueLabel.equalsIgnoreCase(""))) {
                            context.labelItem(mArraySet.get(j - 2), valueLabel);
                        }
                        // ---
                        // String valueLabel = new String(st[j]);
//                        DSAnnotLabel property = new CSAnnotLabel(phLabel);
//                        if (CSCriterionManager.getCriteria(mArraySet) == null) {
//                            CSCriterionManager.setCriteria(mArraySet, new CSCriteria());
//                            CSCriterionManager.setClassCriteria(mArraySet, new CSClassCriteria());
//                        }
//                        DSCriteria criteria = CSCriterionManager.getCriteria(mArraySet);
//                        if ((valueLabel != null) && (!valueLabel.equalsIgnoreCase(""))) {
//                            DSAnnotValue value = new CSAnnotValue(valueLabel, valueLabel.hashCode());
//                            if (criteria.get(property) == null)
//                                criteria.put(property, new CSPanel<DSMicroarray>(phLabel));
//                            criteria.addItem(((DSMicroarray) mArraySet.get(i++)), property, value);
//                        }
                    }
                } else if (line.charAt(0) != '\t') {
                    // This handles individual gene lines with (value, pvalue) pairs separated by tabs
                    int i = 0;
                    DSGeneMarker mi = (DSGeneMarker)mArraySet.getMarkers().get(currGeneId);
                    if (markerVector.size() > currGeneId) {
                        mi = markerVector.get(currGeneId);
                    }
                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mi).reset(currGeneId, microarrayNo, microarrayNo);
//                    if (mi == null) {
//                        mi = new CSExpressionMarker();
//                        ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mi).reset(currGeneId, microarrayNo, microarrayNo);
//                    }
                    //set the affyid field of current marker.
                    mi.setLabel(token);
                    String label = new String(st[1]);
                    //set the annotation field of current marker
                    mi.setDescription(label);

                    markerVector.add(currGeneId, mi);
                    try {
                        String[] result = AnnotationParser.getInfo(token, AnnotationParser.LOCUSLINK);
                        String locus = " ";
                        if ((result != null) && (!result[0].equals(""))) {

                            locus = result[0];

                        }
                        markerVector.get(currGeneId).getUnigene().set(token);

                        if (locus.compareTo(" ") != 0) {
                            markerVector.get(currGeneId).setGeneId(Integer.parseInt(locus));
                        }

                        String[] geneNames = AnnotationParser.getInfo(token, AnnotationParser.ABREV);
                        if (geneNames != null) {
                            markerVector.get(currGeneId).setGeneName(geneNames[0]);
                        }
                    } catch (Exception e) {
                        System.out.println("error parsing " + token);
                        e.printStackTrace();
                    }
                    boolean pValueExists = ((st.length - 2) > microarrayNo);
                    for (int j = 2; j < st.length; j++) {
                        CSMarkerValue marker = (CSMarkerValue) get(i).getMarkerValue(currGeneId);
                        String value = st[j];
                        if ((value == null) || (value.equalsIgnoreCase(""))) { // skip the extra '/t'
                            value = st[++j];
                        }
                        String pValue;
                        if (Boolean.parseBoolean(System.getProperty("expressionMA.usePValue")) || pValueExists) {
                            j++;
                            pValue = st[j];
                        } else {
                            pValue = 1.0 + "";
                        }

                        parse(marker, value, pValue);
                        ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(currGeneId)).check(marker, false);
                        if (marker.isMasked() || marker.isMissing()) {
                            maskedSpots++;
                        }
                        //getIMicroarray(i).setIMarker(i, marker);
                        i++;
                    }
                    currGeneId++;
                }
            }
        } //end of executeLine()

        void parseLine(String line, DSMicroarraySet mArraySet) {
            if (line.charAt(0) == '#') {
                return;
            }
            //String[] st = line.split("\t");
            int startindx = line.indexOf('\t');
            if (startindx > 0) {
                //                String token = st[0];
                if (line.startsWith("PDFModel")) {
                } else if (line.substring(0, 6).toLowerCase().equals("affyid")||line.substring(0, 7).toLowerCase().equals("probeid")) {
                    String[] st = line.split("\t");
                    String value = st[1];
                    for (int j = 2; j < st.length; j++) {
                        if ((st[j] != null) && (!st[j].equalsIgnoreCase(""))) {
                            microarrayNo++;
                        }
                    }
                } else {
                    if (line.substring(0, 11).equalsIgnoreCase("Description")) {
                        String[] st = line.split("\t");
                        String phenoLabel = new String(st[1]);
                        phenotypes.add(phenotypeNo, phenoLabel);
                        phenotypeNo++;
                        //                        countMicroarrayNo(st);
                    } else if (line.charAt(0) != '\t') {
                        if (mArraySet.getCompatibilityLabel() == null) {
                            String token = line.substring(0, startindx);
                            String chiptype = AnnotationParser.matchChipType(token, false);
                            if (chiptype != null) {
                                mArraySet.setCompatibilityLabel(chiptype);
                            }
                        }
                        markerNo++;
                    }
                }
            } //end of parseline()
        } //end of inner clase parser
    }

    public void parse(CSMarkerValue marker, String value, String status) {
        if (Character.isLetter(status.charAt(0))) {
            try {
                char c = status.charAt(0);
                if (Character.isLowerCase(c)) {
                    marker.mask();
                }
                switch (Character.toUpperCase(c)) {
                    case 'P':
                        marker.setPresent();
                        break;
                    case 'A':
                        marker.setAbsent();
                        break;
                    case 'M':
                        marker.setMarginal();
                        break;
                    default:
                        marker.setMissing(true);
                        break;
                }
                parse(marker, value);
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        } else {
            try {
                double v = Double.parseDouble(value);
                Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(currGeneId)).getRange();
                if (dataContext.isLog) {
                    double y = Math.log(Math.max(dataContext.minValue, dataContext.addValue + v));
                    marker.setValue(y);
                    range.max = Math.max(range.max, y);
                    range.min = Math.min(range.min, y);
                } else {
                    marker.setValue(v);
                    range.max = Math.max(range.max, v);
                    range.min = Math.min(range.min, v);
                }
                double p = Double.parseDouble(status);
                marker.setConfidence(p);
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        }
    }

    public void parse(DSMutableMarkerValue marker, String value) {
        if (marker instanceof CSExpressionMarkerValue) {
            String[] parseableValue = value.split(":");
            String expression = parseableValue[parseableValue.length - 1];
            try {
                double v = Double.parseDouble(expression);
                org.geworkbench.bison.util.Range range = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) markerVector.get(currGeneId)).getRange();
                if (dataContext.isLog) {
                    double y = Math.log(Math.max(dataContext.minValue, dataContext.addValue + v));
                    marker.setValue(y);
                    range.max = Math.max(range.max, y);
                    range.min = Math.min(range.min, y);
                } else {
                    marker.setValue(v);
                    range.max = Math.max(range.max, v);
                    range.min = Math.min(range.min, v);
                }
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        } else {
            String[] parseableValue = value.split(":");
            String expression = parseableValue[parseableValue.length - 1];
            try {
                double v = Double.parseDouble(expression);
                if (dataContext.isLog) {
                    double y = Math.log(Math.max(dataContext.minValue, dataContext.addValue + v));
                    marker.setValue(y);
                } else {
                    marker.setValue(v);
                }
            } catch (NumberFormatException e) {
                marker.setValue(0.0);
                marker.setMissing(true);
            }
        }
    }

    public boolean save(File file) {
        // Make sure that the file exists and we can write to it
        try {
            file.createNewFile();
            if (!file.canWrite()) {
                return false;
            }
            this.absPath = file.getAbsolutePath();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // start processing the data.
            // Start with the header line, comprising the array names.
            String outLine = "AffyID" + "\t" + "Annotation";
            for (int i = 0; i < size(); ++i) {
                outLine = outLine.concat("\t" + get(i).toString());
            }
            writer.write(outLine);
            writer.newLine();

            DSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            int n = manager.getNumberOfContexts(this);
            for (int i = 0; i < n; i++) {
                DSAnnotationContext<DSMicroarray> context = manager.getContext(this, i);
                StringBuilder line = new StringBuilder("Description" + '\t' + context.getName());
                for (Iterator<DSMicroarray> iterator = this.iterator(); iterator.hasNext();) {
                    DSMicroarray microarray = iterator.next();
                    String label = "Undefined";
                    String[] labels = context.getLabelsForItem(microarray);
                    // watkin - Unfortunately, the file format only supports one label per context.
                    if (labels.length > 0) {
                        label = labels[0];
                    }
                    line.append('\t' + label);
                }
                writer.write(line.toString());
                writer.newLine();
            }
            /*
            DSCriteria<DSBioObject> criteria = CSCriterionManager.getCriteria(this);
            for (DSPanel<DSBioObject> property : criteria.values()) {
                // For each phenotypic criteria (Default one, "Accession" is ignored)
                String line = "Description" + "\t" + property.getLabel();
                for (int k = 0; k < this.size(); ++k) {
                    DSMicroarray mArray = get(k);
                    DSPanel<DSBioObject> value = property.getPanel(mArray);
                    if (value == null) {
                        line = line + '\t' + "Undefined";
                    } else {
                        line = line + '\t' + value.getLabel();
                    }
                }
                writer.write(line);
                writer.newLine();
            }
            */
            ProgressMonitor pm = new ProgressMonitor(null, "Total " + markerVector.size(), "saving ", 0, markerVector.size());
            // Proceed to write one marker at a time
            for (int i = 0; i < markerVector.size(); ++i) {
                pm.setProgress(i);
                pm.setNote("saving " + i);
                outLine = markerVector.get(i).getLabel();
                outLine = outLine.concat('\t' + getMarkers().get(i).getLabel());
                for (int j = 0; j < size(); ++j) {
                    DSMarkerValue mv = get(j).getMarkerValue(i);
                    outLine = outLine.concat("\t" + (float) mv.getValue() + '\t') + (float) mv.getConfidence();
                }
                writer.write(outLine);
                writer.newLine();
            }
            pm.close();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public boolean save(File file, int numArrays) {
        // Make sure that the file exists and we can write to it
        try {
            file.createNewFile();
            if (!file.canWrite()) {
                return false;
            }
            this.absPath = file.getAbsolutePath();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            // start processing the data.
            // Start with the header line, comprising the array names.
            String outLine = "AffyID" + "\t" + "Annotation";
            for (int i = 0; i < numArrays; ++i) {
                //                outLine = outLine.concat("\t" + getMicroarray(i).getLabel());
                outLine = outLine.concat("\t" + i);
            }
            writer.write(outLine);
            writer.newLine();
            // Proceed to write one marker at a time
            for (int i = 0; i < markerVector.size(); ++i) {
                outLine = getMarkers().get(i).getLabel();
                outLine = outLine.concat("\t" + getMarkers().get(i).getLabel());

                for (int j = 0; j < numArrays; ++j) {
                    DSMarkerValue mv = get(j).getMarkerValue(i);
                    outLine = outLine.concat("\t" + mv.getValue() + "\t") + mv.getConfidence();
                }
                writer.write(outLine);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    public void writeToFile(String fileName) {
        final String f = fileName;
        //         JOptionPane.sho

        Thread t = new Thread() {
            public void run() {
                File file = new File(f);
                save(file);
            }
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    public void writeToFile(String fileName, int numArrays) {
        File file = new File(fileName);
        save(file, numArrays);
    }

    public int getPlatformType() {
        return AFFYMETRIX_PLATFORM;
    }

    // Convenience class - used as the return value of method
    // <code>createProgressReader()</code>.
    private class ReaderMonitor {
        BufferedReader reader = null;
        ProgressMonitor pm = null;
    }

//    public void computeRange() {
//        CSMicroarraySetView<DSGeneMarker, DSMicroarray> view = new CSMicroarraySetView();
//        DSDataSet dataSet = (DSDataSet) this;
//        view.setDataSet(dataSet);
//        DSCriteria criteria = CSCriterionManager.getCriteria(dataSet);
//        view.setItemPanel(criteria.getSelectedCriterion());
//        view.useItemPanel(true);
//        for (DSGeneMarker marker : getMarkers()) {
//            ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).reset(marker.getSerial(), 0, 0);
//        }
//        if (view.items().size() == 1) {
//            // @todo - watkin - move this range calculation in to a different ColorContext, maybe?
//            DSMicroarray ma = view.items().get(0);
//            Range range = new org.geworkbench.bison.util.Range();
//            for (DSGeneMarker marker : getMarkers()) {
//                DSMutableMarkerValue mValue = (DSMutableMarkerValue) ma.getMarkerValue(marker.getSerial());
//                range.min = Math.min(range.min, mValue.getValue());
//                range.max = Math.max(range.max, mValue.getValue());
//                range.norm.add(mValue.getValue());
//            }
//            for (DSGeneMarker marker : getMarkers()) {
//                Range markerRange = ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).getRange();
//                markerRange.min = range.min;
//                markerRange.max = range.max;
//                markerRange.norm = range.norm;
//            }
//        } else {
//            for (DSGeneMarker marker : getMarkers()) {
//                for (DSMicroarray ma : view.items()) {
//                    DSMutableMarkerValue mValue = (DSMutableMarkerValue) ma.getMarkerValue(marker.getSerial());
//                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) marker).check(mValue, false);
//                }
//            }
//        }
//    }
}
