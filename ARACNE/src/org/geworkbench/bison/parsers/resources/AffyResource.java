package org.geworkbench.bison.parsers.resources;

import java.io.File;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust Inc.
 * @version 1.0
 */

/**
 * Extends <code>Resource</code> to allow handling Affy input files.
 */
public class AffyResource extends AbstractResource {
    /**
     * The input Affy file that gives rise to the inputReader
     */
    File inputFile = null;

    /**
     * Set the file name for the input file from which this resource is generated.
     *
     * @param iFName The input file name.
     */
    public void setInputFile(File file) {
        inputFile = file;
    }

    /**
     * Return the name of the input file from which this resource is generated
     *
     * @return
     */
    public File getInputFile() {
        return inputFile;
    }

}

