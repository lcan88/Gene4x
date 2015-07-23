package org.geworkbench.util.associationdiscovery.cluster;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.DSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.util.CSCriterionManager;
import org.geworkbench.bison.util.DSPValue;
import org.geworkbench.bison.util.Normal;
import org.geworkbench.util.associationdiscovery.statistics.ClusterStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PatternOps {
    static ClusterStatistics patternStats = null;

    /**
     * This function masks the specific values for each marker/object combination
     * in a pattern. It does not mask either the complete marker nor the complete
     * object.
     *
     * @param pattern CSMatchedMatrixPattern
     * @param set     DSMicroarraySet
     */
    static public void maskPattern(CSMatchedMatrixPattern pattern, DSMicroarraySet set) {
        for (DSPatternMatch<DSMicroarray, ? extends DSPValue> match : pattern.matches()) {
            for (DSGeneMarker marker : pattern.getPattern().markers()) {
                match.getObject().getMarkerValue(marker).mask();
            }
        }
    }

    /**
     * should do the same but not sure it is working right
     *
     * @param mArraySet DSMicroarraySet
     * @param pattern   CSMatchedMatrixPattern
     */
    static public void maskMatchingMarkers(DSMicroarraySet mArraySet, CSMatchedMatrixPattern pattern) {
        // Loop over all markers in the pattern
        for (DSGeneMarker marker : pattern.getPattern().markers()) {
            // The following is the marker value to match
            DSMarkerValue marker0 = pattern.get(0).getObject().getMarkerValue(marker);
            // Now iterate over all the microarrays
            for (DSPatternMatch<DSMicroarray, ? extends DSPValue> match : pattern.matches()) {
                DSMarkerValue marker1 = match.getObject().getMarkerValue(marker);
                if (marker0.equals(marker1)) {
                    match.getObject().getMarkerValue(marker).mask();
                }
            }
        }
    }

    /**
     * Should mask the corresponding marker value across all objects that match the
     * pattern.
     *
     * @param mArraySet DSMicroarraySet
     * @param pattern   CSMatchedMatrixPattern
     */
    public static void maskMatchingObjects(DSMicroarraySet<DSMicroarray> mArraySet, CSMatchedMatrixPattern pattern) {
        // Get the actual pattern
        DSMatrixPattern p = pattern.getPattern();
        boolean matched = false;
        // Loop over all the microarrays
        for (DSMicroarray ma : mArraySet) {
            if (p.match(ma).getPValue() < 1) {
                //                matched = true;
                //                System.out.print(ma.getSerial() + ", ");
                for (DSGeneMarker marker : pattern.getPattern().markers()) {
                    ma.getMarkerValue(marker).mask();
                }
            }
        }
        //        if(matched) System.out.println("");
    }

    static public boolean matchesPattern(CSMatchedMatrixPattern pat, DSMicroarraySet<DSMicroarray> set, int chipId1) {
        DSMicroarray chip1 = set.get(chipId1);
        DSMicroarray chip2 = pat.getItem(0);
        boolean isMatch = true;
        for (int k = 0; k < pat.getPattern().markers().length; k++) {
            DSGeneMarker marker = pat.getPattern().markers()[k];
            DSMarkerValue spot1 = chip1.getMarkerValue(marker);
            DSMarkerValue spot2 = chip2.getMarkerValue(marker);
            if (spot1.isMissing() || (spot1.getValue() != spot2.getValue())) {
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }

    static public boolean matchesPattern(CSMatchedMatrixPattern pattern, DSMicroarray ma1, DSMicroarraySet set) {
        DSMicroarray ma2 = pattern.get(0).getObject();
        boolean isMatch = true;
        for (DSGeneMarker marker : pattern.getPattern().markers()) {
            DSMarkerValue spot1 = ma1.getMarkerValue(marker);
            DSMarkerValue spot2 = ma2.getMarkerValue(marker);
            if (spot1.isMissing() || (spot1.getValue() != spot2.getValue())) {
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }

    static public double GetExpectedNo(CSMatchedMatrixPattern pattern, DSMicroarraySet set) {
        if (patternStats == null) {
            patternStats = new ClusterStatistics(set);
            patternStats.init();
        }
        return patternStats.getExpectedNo(pattern);
    }

    /**
     * maskMatchingSupport
     *
     * @param pattern       Pattern
     * @param microarraySet DSMicroarraySet
     */
    public static void maskMatchingSupport(CSMatchedMatrixPattern pattern, DSMicroarraySet set) {
        for (DSPatternMatch<DSMicroarray, ? extends DSPValue> match : pattern.matches()) {
            if (matchesPattern(pattern, match.getObject(), set)) {
                match.getObject().enable(false);
            }
        }
    }

    public static void unmaskAll(DSMicroarraySet<DSMicroarray> mArraySet) {
        for (DSGeneMarker marker : mArraySet.getMarkers()) {
            for (DSMicroarray ma : mArraySet) {
                ma.getMarkerValue(marker).unmask();
            }
        }
    }

    /**
     * refinePattern
     *
     * @param mp            CSMatchedMatrixPattern
     * @param microarraySet DSMicroarraySet
     *                      This method will extend the set of genes in a matrix pattern to include other genes
     *                      that have a t-test with low p-value on the same set of microarrays
     */
    public static void refinePatternGenes(CSMatchedMatrixPattern mp, DSMicroarraySet<DSMicroarray> microarraySet, double pValue) {
        // Get the Class manager
        // todo - watkin - refactor to use CSAnnotationContext
        DSClassCriteria classCriteria = CSCriterionManager.getClassCriteria(microarraySet);
        List<DSPatternMatch<DSMicroarray, DSPValue>> matches = mp.matches();
        DSItemList<? extends DSBioObject> background = null;
        // Determine if the pattern was obtained by unsupervised analysis
        if (classCriteria.isUnsupervised()) {
            background = classCriteria.getTaggedItems(CSClassCriteria.all);
            if (background.size() == 0) {
                background = microarraySet;
            }
        } else {
            background = classCriteria.getTaggedItems(CSClassCriteria.controls);
        }
        for (DSPatternMatch<DSMicroarray, DSPValue> pm : matches) {
            background.remove(pm.getObject());
        }
        ArrayList<DSGeneMarker> markers = new ArrayList<DSGeneMarker>();
        for (DSGeneMarker m : microarraySet.getMarkers()) {
            String name = m.getLabel();
            if (name.equalsIgnoreCase("32845_at")) {
                int xxx = 1;
            }
            Normal fg = new Normal();
            Normal bg = new org.geworkbench.bison.util.Normal();
            for (DSPatternMatch<DSMicroarray, DSPValue> pm : matches) {
                fg.add(pm.getObject().getMarkerValue(m).getValue());
            }
            for (DSBioObject bo : background) {
                DSMicroarray ma = (DSMicroarray) bo;
                bg.add(ma.getMarkerValue(m).getValue());
            }
            double tTest = Normal.tTest(fg, bg);
            TTestImpl test = new TTestImpl();
            StatisticalSummaryValues fgSummary = new StatisticalSummaryValues(fg.getMean(), fg.getVariance(), (int) fg.getN(), 0, 0, 0);
            StatisticalSummaryValues bgSummary = new StatisticalSummaryValues(bg.getMean(), bg.getVariance(), (int) bg.getN(), 0, 0, 0);
            try {
                double t = test.tTest(fgSummary, bgSummary);
                if (t < pValue) {
                    markers.add(m);
                }
            } catch (MathException ex) {
            } catch (IllegalArgumentException ex) {
            }
        }
        if (markers.size() > 0) {
            DSMatrixPattern pattern = mp.getPattern();
            markers.addAll(Arrays.asList(pattern.markers()));
            pattern.init(markers.size());
            DSMicroarray array = mp.getItem(0);
            int i = 0;
            for (DSGeneMarker m : markers) {
                pattern.markers()[i] = m;
                i++;
            }
            for (DSPatternMatch<DSMicroarray, DSPValue> match : mp.matches()) {
                DSMicroarray mArray = match.getObject();
                pattern.add(mArray);
            }
        }
    }
}
