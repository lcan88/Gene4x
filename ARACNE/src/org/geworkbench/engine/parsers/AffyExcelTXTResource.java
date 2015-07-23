package org.geworkbench.engine.parsers;

import org.geworkbench.bison.parsers.resources.AbstractResource;


public class AffyExcelTXTResource extends AbstractResource {
    public AffyExcelTXTResource() {
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
