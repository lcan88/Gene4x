package org.geworkbench.engine.parsers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.CSMarkerVector;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSExpressionMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used to load data like that found at http://genome-www.stanford.edu/clustering/Figure2.txt
 * The above example is used to train the SVM implementation.
 *
 * @version 1.0
 * @author Matt Hall
 */

public class SVMNormalizedMatrixFileFormat extends DataSetFileFormat {

    static Log log = LogFactory.getLog(SVMNormalizedMatrixFileFormat.class);

    String[] maExtensions = {"txt"};
    ExpressionResource resource = new ExpressionResource();
    SVMNormalizedMatrixFilter maFilter = null;

    public SVMNormalizedMatrixFileFormat() {
        formatName = "Normalized no-confidence expression matrix";
        maFilter = new SVMNormalizedMatrixFilter();
        Arrays.sort(maExtensions);
    }

    public Resource getResource(File file) {
        try {
            resource.setReader(new BufferedReader(new FileReader(file)));
            resource.setInputFileName(file.getName());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
        return resource;
    }

    public String[] getFileExtensions() {
        return maExtensions;
    }

    public boolean checkFormat(File file) {
        return true;
    }

    public DSDataSet getDataFile(File file) {
        return (DSDataSet) getMArraySet(file);
    }

    private static int INDEX_CLASSIFICATIONS = 2;
    private static int INDEX_DATA = 8;

    public DSMicroarraySet getMArraySet(File file) {
        CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        maSet.setLabel(file.getName());
        maSet.setAbsPath(file.getAbsolutePath());

        // First line contains titles and marker names
        BufferedReader fin = null;
        try {
            fin = new BufferedReader(new FileReader(file));
            String[] header = fin.readLine().split("\t");
            int markerCount = header.length - INDEX_DATA;


            List<String[]> dataLines = new ArrayList<String[]>();
            String line = fin.readLine();
            while (line != null) {
                dataLines.add(line.split("\t"));
                line = fin.readLine();
            }

            maSet.initialize(dataLines.size(), markerCount);


            CSMarkerVector markers = maSet.getMarkerVector();
            int markerCounter = INDEX_DATA;
            for (DSGeneMarker dsGeneMarker : markers) {
                dsGeneMarker.setGeneName(header[markerCounter]);
                dsGeneMarker.setDescription(header[markerCounter]);
                dsGeneMarker.setLabel(header[markerCounter]);
                markerCounter++;
            }

            int count = 0;
            for (String[] values : dataLines) {
                DSMicroarray ma = maSet.get(count);
                ma.setLabel(values[0]);

                // Handle class data
                for (int i = INDEX_CLASSIFICATIONS; i < INDEX_DATA; i++) {
                    String classification = values[i];
                    try {
                        if (Integer.parseInt(classification) == 1) {
                            DSAnnotationContext<DSMicroarray> context = manager.getContext(maSet, "Classification");
                            CSAnnotationContext.initializePhenotypeContext(context);
                            context.labelItem(maSet.get(count), header[i]);
                        }
                    } catch (NumberFormatException e) {
                        log.warn("Couldn't parse class value " + header[i] + " on line " + count);
                    }
                }

                for (int i = INDEX_DATA; i < values.length; i++) {
                    String value = values[i];
                    try {
                        CSExpressionMarkerValue markerValue = new CSExpressionMarkerValue(Float.parseFloat(value));
                        markerValue.setPresent();
                        ma.setMarkerValue(i - INDEX_DATA, markerValue);
                    } catch (NumberFormatException e) {
                        log.warn("Couldn't format value at " + (i - INDEX_DATA) + " for " + values[0]+", setting to 0.");
                        ma.setMarkerValue(i - INDEX_DATA, new CSExpressionMarkerValue(0));
                    }
                }

                count++;
            }
        } catch (Exception e) {
            log.error(e);
        }

        return maSet;
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new UnsupportedOperationException("Method getOptions() not yet implemented.");
    }

    public FileFilter getFileFilter() {
        return maFilter;
    }

    /**
     * getDataFile
     *
     * @param files File[]
     * @return DataSet
     */
    public DSDataSet getDataFile(File[] files) {
        return null;
    }

    class SVMNormalizedMatrixFilter extends FileFilter {

        public String getDescription() {
            return "Normalized no-confidence expression matrix";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            for (int i = 0; i < maExtensions.length; ++i)
                if (f.isDirectory() || f.getName().endsWith(maExtensions[i])) {
                    return true;
                }
            return returnVal;
        }
    }
}
