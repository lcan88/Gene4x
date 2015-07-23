package org.geworkbench.engine.parsers;

import org.geworkbench.bison.parsers.resources.AbstractResource;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ExpressionResource extends AbstractResource {
    public ExpressionResource() {
    }

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
