package org.geworkbench.engine.parsers.sequences;

import org.geworkbench.bison.parsers.resources.AbstractResource;

/**
 * <p>Title: SequenceResource</p>
 * <p>Description: Copy of GenepixResource </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author Saroja Hanasoge
 * @version 1.0
 */

public class SequenceResource extends AbstractResource {
    /**
     * The name of the input Affy file that gives rise to the inputReader
     */
    String inputFileName = null;

    /**
     * Set the file name for the input file from which this resource is generated.
     *
     * @param iFName The input file name.
     */
    public void setInputFileName(String iFName) {
        inputFileName = iFName;
    }

    /**
     * Return the name of the input file from which this resource is generated
     *
     * @return
     */
    public String getInputFileName() {
        return inputFileName;
    }
}
