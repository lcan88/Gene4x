package org.geworkbench.bison.algorithm.classification;

import org.geworkbench.bison.datastructure.properties.DSNamed;
import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;

import java.util.List;
import java.io.File;

/**
 * Implementing classifiers are able to run classifications on objects.
 */
public abstract class Classifier extends CSAncillaryDataSet {

    String[] classifications;

    protected Classifier(DSDataSet parent, String label, String[] classifications) {
        super(parent, label);
        this.classifications = classifications;
    }

    /**
     * Runs a classification on the given object.
     *
     * @param data the data to classify.
     * @return a classification of the given object from the list returned by {@link org.geworkbench.bison.algorithm.classification.Classifier#getClassifications()}
     */
    public abstract String classify(float[] data);

    public String[] getClassifications() {
        return classifications;
    }

    public File getDataSetFile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDataSetFile(File file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
