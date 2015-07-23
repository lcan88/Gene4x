package org.geworkbench.components.microarrays;

import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;

/**
 * <p>Title: caWorkbench</p>
 *
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 *
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 *
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 3.0
 */

/**
 * For EVD panel, only caculate T test without any correction.
 */

public class SimpleTTest {

    private static final int TTEST_ONE_CLASS = 8;
    private static int CASES = 1;
    private static int CONTROLS = 2;
    private static int NEITHER_GROUP = 3;
    private static int TWO_SAMPLE = 1;
    private double maxT;
    private double minT;
    private double[] originalTValues;
    private float[][] expMatrix;
    boolean calculate_genes;
    boolean calculate_experiments;
    private int numGenes, numExps;
    int[] groupAssignments;
    boolean useAllCombs;
    int tTestDesign;
    private int TTestDesign;
    private DSItemList<? extends DSGeneMarker> item;

    public SimpleTTest() {
        maxT = -100;
        minT = 100;
    }

    public void reset() {
        expMatrix = null;
        groupAssignments = null;
        numGenes = numExps = tTestDesign = 0;

    }

    private float getTValue(int gene, float[][] inputMatrix) {
        float[] geneValues = new float[numExps];
        for (int i = 0; i < numExps; i++) {
            geneValues[i] = inputMatrix[gene][i];
        }

        int groupACounter = 0;
        int groupBCounter = 0;

        for (int i = 0; i < groupAssignments.length; i++) {
            if (groupAssignments[i] == CASES) {
                groupACounter++;
            } else if (groupAssignments[i] == CONTROLS) {
                groupBCounter++;
            }
        }

        float[] groupAValues = new float[groupACounter];
        float[] groupBValues = new float[groupBCounter];

        groupACounter = 0;
        groupBCounter = 0;

        for (int i = 0; i < groupAssignments.length; i++) {
            if (groupAssignments[i] == CASES) {
                groupAValues[groupACounter] = geneValues[i];
                groupACounter++;
            } else if (groupAssignments[i] == CONTROLS) {
                groupBValues[groupBCounter] = geneValues[i];
                groupBCounter++;
            }
        }

        float tValue = calculateTValue(groupAValues, groupBValues);
        return tValue;
    }


    public double[] execute(Object input, boolean enableActiveMarkers) {
        reset();
        if (input == null) {
            return null;
        }

        assert input instanceof DSMicroarraySetView;



        DSMicroarraySetView<? extends DSGeneMarker,? extends DSMicroarray> data = (DSMicroarraySetView) input;
         DSMicroarraySet<DSMicroarray> set = (DSMicroarraySet) data.getDataSet();
        // DSDataSet set = data.getDataSet();
        DSPanel<DSGeneMarker> genes = new CSPanel<DSGeneMarker>("");

        //fix bug 235. the data.size() will includes duplicate. so need create a new panel.
        genes.addAll(data.markers());

        data.useItemPanel(true);
        data.useMarkerPanel(enableActiveMarkers);
        item = data.markers();
        int temp = item.size();

        int markers = genes.size();
        int arrays = data.items().size();
          groupAssignments = new int[arrays];

        expMatrix = new float[markers][arrays];
        originalTValues = new double[markers];
        for (int j = 0; j < arrays; j++) {
            DSMicroarray ma = set.get(j);
            for (int i = 0; i < markers; i++) {
            DSGeneMarker marker = genes.get(i);

                expMatrix[i][j] = (float) ma.getMarkerValue(marker).getValue();;
            }
        }


//
//        //        DSDataSetView<DSMarker, DSMicroarray> data = (DSDataSetView<DSMarker, DSMicroarray>)input;
//        DSMicroarraySetView<? extends DSGeneMarker, ? extends DSMicroarray> data = (DSMicroarraySetView) input;
//        data.useItemPanel(true);
//        data.useMarkerPanel(enableActiveMarkers);
//        item = data.markers();
//        int markers = data.markers().size();
//        int arrays = data.items().size();
//
//        expMatrix = new float[markers][arrays];
//        originalTValues = new double[markers];
//
//        for (int i = 0; i < markers; i++) {
//            for (int j = 0; j < arrays; j++) {
//                //                expMatrix[i][j] = (float)data.items().get(j).getMarkerValue(i).getValue();
//                expMatrix[i][j] = (float) data.getValue(i, j);
//            }
//        }
//
//        groupAssignments = new int[arrays];
//        //.getObject();
//        DSDataSet set = data.getDataSet();

        if (set instanceof DSMicroarraySet) {
            DSMicroarraySet<DSMicroarray> maSet = (DSMicroarraySet<DSMicroarray>) set;
            // DSClassCriteria criteria = CSCriterionManager.getClassCriteria(maSet);
            DSAnnotationContext<DSMicroarray> context = CSAnnotationContextManager.getInstance().getCurrentContext(maSet);
            tTestDesign = TTEST_ONE_CLASS;
            for (int i = 0; i < arrays; i++) {
                DSMicroarray ma = data.items().get(i);
                if (ma instanceof DSMicroarray) {
                    //DSPanel panel = selCriterion.panels().get(ma);
                    String label = context.getClassForItem(ma);
                    if (label.equals(CSAnnotationContext.CLASS_CASE)) {
                        groupAssignments[i] = CASES;
                    } else if (label.equals(CSAnnotationContext.CLASS_CONTROL)) {
                        groupAssignments[i] = CONTROLS;
                        tTestDesign = TWO_SAMPLE;
                    } else {
                        groupAssignments[i] = NEITHER_GROUP;
                    }
                } else {
                    groupAssignments[i] = NEITHER_GROUP;
                }
            }

            numGenes = genes.size();
            numExps = data.items().size();

            //    if (tTestDesign == TtestAnalysisPanel.BETWEEN_SUBJECTS) {
            for (int i = 0; i < numGenes; i++) {
                double tValue = getTValue(i);

                originalTValues[i] = tValue;
                if (tValue > maxT) {
                    maxT = tValue;
                }
                if (tValue < minT) {
                    minT = tValue;
                }
            }
            return originalTValues;

            //     }

        }
        return null;
    }


    private float getTValue(int gene) {
        float[] geneValues = new float[numExps];
        for (int i = 0; i < numExps; i++) {
            geneValues[i] = expMatrix[gene][i];
        }

        int groupACounter = 0;
        int groupBCounter = 0;

        for (int i = 0; i < groupAssignments.length; i++) {
            if (groupAssignments[i] == CASES) {
                groupACounter++;
            } else if (groupAssignments[i] == CONTROLS) {
                groupBCounter++;
            }
        }

        float[] groupAValues = new float[groupACounter];
        float[] groupBValues = new float[groupBCounter];

        groupACounter = 0;
        groupBCounter = 0;

        for (int i = 0; i < groupAssignments.length; i++) {
            if (groupAssignments[i] == CASES) {
                groupAValues[groupACounter] = geneValues[i];
                groupACounter++;
            } else if (groupAssignments[i] == CONTROLS) {
                groupBValues[groupBCounter] = geneValues[i];
                groupBCounter++;
            }
        }

        float tValue = calculateTValue(groupAValues, groupBValues);
        return tValue;
    }

    private float getMean(float[] group) {
        float sum = 0;
        int n = 0;

        for (int i = 0; i < group.length; i++) {
            if (!Float.isNaN(group[i])) {
                sum = sum + group[i];
                n++;
            }
        }
        if (n == 0) {
            return Float.NaN;
        }
        float mean = sum / (float) n;

        if (Float.isInfinite(mean)) {
            return Float.NaN;
        }
        return mean;
    }

    private float calculateTValue(float[] groupA, float[] groupB) {
        int kA = groupA.length;
        int kB = groupB.length;
        float meanA = getMean(groupA);
        float meanB = getMean(groupB);
        float varA = getVar(groupA);
        float varB = getVar(groupB);

        int numbValidGroupAValues = 0;
        int numbValidGroupBValues = 0;

        for (int i = 0; i < groupA.length; i++) {
            if (!Float.isNaN(groupA[i])) {
                numbValidGroupAValues++;
            }
        }

        for (int i = 0; i < groupB.length; i++) {
            if (!Float.isNaN(groupB[i])) {
                numbValidGroupBValues++;
            }
        }

        if ((numbValidGroupAValues < 2) || (numbValidGroupBValues < 2)) {
            return Float.NaN;
        }

        float tValue = (float) ((meanA - meanB) / Math.sqrt((varA / kA) + (varB / kB)));

        //return Math.abs(tValue);
        return tValue;
    }

    private float getVar(float[] group) {
        float mean = getMean(group);
        int n = 0;

        float sumSquares = 0;

        for (int i = 0; i < group.length; i++) {
            if (!Float.isNaN(group[i])) {
                sumSquares = (float) (sumSquares + Math.pow((group[i] - mean), 2));
                n++;
            }
        }

        if (n < 2) {
            return Float.NaN;
        }

        float var = sumSquares / (float) (n - 1);
        if (Float.isInfinite(var)) {
            return Float.NaN;
        }
        return var;
    }

    public double getMaxT() {
        return maxT;
    }

    public double getMinT() {
        return minT;
    }

    public int getTTestDesign() {
        return TTestDesign;
    }

    public DSItemList getItem() {
        return item;
    }

    public void setItem(DSItemList item) {
        this.item = item;
    }

}
