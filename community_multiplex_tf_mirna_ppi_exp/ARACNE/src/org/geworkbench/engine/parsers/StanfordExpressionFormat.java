package org.geworkbench.engine.parsers;

import org.geworkbench.bison.annotation.CSCriteria;
import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.util.*;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */
public class StanfordExpressionFormat extends DataSetFileFormat {
    public StanfordExpressionFormat() {
        formatName = "Stanford Expression Format";
        maFilter = new AffyFilter();
        Arrays.sort(maExtensions);
    }

    String[] maExtensions = {"txt", "sta"};
    org.geworkbench.engine.parsers.ExpressionResource resource = new ExpressionResource();
    AffyFilter maFilter = null;
    int microarrayNo = 0;
    int phenotypeNo = 0;
    int markerNo = 0;

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

    public DSMicroarraySet getMArraySet(File file) {
        CSExprMicroarraySet maSet = new CSExprMicroarraySet();
        try {
            read(maSet, file);
        } catch (Exception e) {
        }
        if (maSet.loadingCancelled)
            return null;
        return maSet;
    }

    private void read(DSMicroarraySet maSet, File file) {
        microarrayNo = 0;
        phenotypeNo = 0;
        markerNo = 0;
        maSet.setLabel(file.getName());
        maSet.setAbsPath(file.getAbsolutePath());
        BufferedReader reader = null;

        try {
            FileInputStream fileIn = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fileIn));
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                parseLine(line, maSet);
            }
            reader.close();
            //            if (maSet.getCompatibilityLabel() == null) {
            //                JOptionPane.showMessageDialog(null, "Can't recognize the chiptype of the file.");
            //                AnnotationParser.callUserDefinedAnnotation();
            //            }
        } catch (InterruptedIOException iioe) {
            //loadingCancelled = true;
            return;
        } catch (Exception ioe) {
            int i = 0;
            return;
        } finally {
        }
        try {
            FileInputStream fileIn = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fileIn));
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return;
        }
        maSet.initialize(microarrayNo, markerNo);
        try {
            int i = 1;
            int geneId = 0;
            while ((line = reader.readLine()) != null) {
                geneId += executeLine(line, geneId, maSet);
            }
        } catch (InterruptedIOException iioe) {
            //loadingCancelled = true;
            return;
        } catch (Exception ioe) {
            System.out.println("Error while parsing line: " + line);
            return;
        }
    }

    public List getOptions() {
        /**@todo Implement this org.geworkbench.engine.parsers.FileFormat abstract method*/
        throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
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

    /**
     * Defines a <code>FileFilter</code> to be used when the user is prompted
     * to select Affymetrix input files. The filter will only display files
     * whose extension belongs to the list of file extension defined in {@link
     * #affyExtensions}.
     */
    class AffyFilter extends FileFilter {

        public String getDescription() {
            return "Stanford Microarray Format";
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

    void parseLine(String line, DSMicroarraySet mArraySet) {
        if (line.charAt(0) == '#') {
            return;
        }
        if (line.startsWith("YORF")) {
            String[] st = line.split("\t");
            String name = st[1];
            String gweight = st[2];
            for (int j = 3; j < st.length; j++) {
                if ((st[j] != null) && (!st[j].equalsIgnoreCase(""))) {
                    microarrayNo++;
                }
            }
        } else if (line.startsWith("EWEIGHT")) {
        } else if (line.substring(0, 11).equalsIgnoreCase("Description")) {
            //            String[] st = line.split("\t");
            //            String phenoLabel = new String(st[1]);
            //            phenotypes.add(phenotypeNo, phenoLabel);
            //            phenotypeNo++;
        } else {
            if (mArraySet.getCompatibilityLabel() == null) {
                String[] st = line.split("\t");
                String chiptype = AnnotationParser.matchChipType(st[0], false);
                if (chiptype != null) {
                    mArraySet.setCompatibilityLabel(chiptype);
                }
            }
            markerNo++;
        }
    } //end of inner clase parser

    int executeLine(String line, int geneId, DSMicroarraySet<DSMicroarray> mArraySet) {
        if (line.charAt(0) == '#') {
            return 0; //
        }
        String[] st = line.split("\t");

        if (st.length > 0) {
            String token = st[0];
            /**
             * This handles the first line, which contains the microarray labels
             * separated by tabs.
             */
            if (token.equalsIgnoreCase("YORF")) {
                int i = 0;
                //read the first line and put label of the arrays in.
                for (int j = 3; j < st.length; j++) {
                    String maLabel = new String(st[j]);
                    String[] labels = maLabel.split("\\.");
                    mArraySet.get(i).setLabel(maLabel);
                    if (labels.length > 1) {
                        String valueLabel = new String(labels[0]);
                        DSAnnotLabel property = new CSAnnotLabel("Phenotype");
                        // todo - watkin - if this format is to be used, this needs to be refactored to use CSAnnotationContext
                        if (CSCriterionManager.getCriteria(mArraySet) == null) {
                            CSCriterionManager.setCriteria(mArraySet, new CSCriteria());
                            CSCriterionManager.setClassCriteria(mArraySet, new CSClassCriteria());
                        }
                        DSCriteria criteria = CSCriterionManager.getCriteria(mArraySet);
                        if ((valueLabel != null) && (!valueLabel.equalsIgnoreCase(""))) {
                            DSAnnotValue value = new CSAnnotValue(valueLabel, valueLabel.hashCode());
                            if (criteria.get(property) == null)
                                criteria.put(property, new CSPanel<DSMicroarray>());
                            criteria.addItem(mArraySet.get(i), property, value);
                        }

                    }
                    i++;
                }
            } else if (token.equalsIgnoreCase("EWEIGHT")) {
            } else {
                // This handles individual gene lines with (value, pvalue) pairs separated by tabs
                int i = 0;
                DSGeneMarker mi = mArraySet.getMarkers().get(geneId);
                if (mi == null) {
                    mi = new CSExpressionMarker();
                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mi).reset(geneId, microarrayNo, microarrayNo);
                }
                //set the affyid field of current marker.
                mi.setLabel(st[0]);
                //set the annotation field of current marker
                mi.setDescription(st[1]);

                //                try {
                //                    String[] result = AnnotationParser.getInfo(token, AnnotationParser.LOCUSLINK);
                //                    String locus = " ";
                //                    if ((result != null) && (!result[0].equals(""))) {
                //                    locus = result[0];
                //            }
                //                    markerVector.get(geneId).getUnigene().set(token);

                //                    if (locus.compareTo(" ") != 0) {
                //                        markerVector.get(currGeneId).setLocusLink(Integer.parseInt(locus));
                //                    }

                //                    String[] geneNames = AnnotationParser.getInfo(token, AnnotationParser.ABREV);
                //                    if (geneNames != null) {
                //                        markerVector.get(currGeneId).setGeneName(geneNames[0]);
                //                    }
                //                } catch (Exception e) {
                //                    System.out.println("error parsing " + token);
                //                    e.printStackTrace();
                //                }
                mArraySet.getMarkers().add(geneId, mi);
                for (int j = 3; j < st.length; j++) {
                    DSMutableMarkerValue marker = (DSMutableMarkerValue) mArraySet.get(i).getMarkerValue(geneId);
                    String value = st[j];
                    marker.setConfidence(1);
                    try {
                        double v = Double.parseDouble(value);
                        Range range = ((DSRangeMarker) mArraySet.getMarkers().get(geneId)).getRange();
                        marker.setValue(v);
                        range.max = Math.max(range.max, v);
                        range.min = Math.min(range.min, v);
                        range.norm.add(v);
                    } catch (NumberFormatException e) {
                        marker.setValue(0.0);
                        marker.setMissing(true);
                    }
                    ((org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker) mArraySet.getMarkers().get(geneId)).check(marker, false);
                    i++;
                }
                return 1;
            }
        }
        return 0;
    } //end of executeLine()

}
