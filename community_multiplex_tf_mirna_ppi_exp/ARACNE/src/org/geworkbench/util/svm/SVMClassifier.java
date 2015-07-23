package org.geworkbench.util.svm;

import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.algorithm.classification.Classifier;

import java.util.List;
import java.util.ArrayList;

public class SVMClassifier extends Classifier {

    private float[] alpha;
    private int[] trainingClassifications;
    private KernelFunction kernel;
    private List<float[]> trainingSet;
    private double b;

    public SVMClassifier(DSDataSet parent, String label, float[] alpha,
                         int[] trainingClassifications, KernelFunction kernel, List<float[]> trainingSet, double b) {
        super(parent, label, new String[]{"case", "control"});
        this.alpha = alpha;
        this.trainingClassifications = trainingClassifications;
        this.kernel = kernel;
        this.trainingSet = trainingSet;
        this.b = b;
    }

    public String classify(float[] data) {
        double v = discriminant(data);
        // > 0: Case, < 0: Control
        if (v < 0) {
            // Not in case 1
            return getClassifications()[1];
        } else {
            return getClassifications()[0];
        }
    }

    private double discriminant(float[] input) {
        double v = 0;
        for (int i = 0; i < trainingClassifications.length; i++) {
            if (alpha[i] > 0) {
                v += alpha[i] * trainingClassifications[i] * kernel.eval(input, trainingSet.get(i));
            }
        }
        return v - b;
    }

}
