package org.geworkbench.test;

import junit.framework.TestCase;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSExprMicroarraySet;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import java.io.File;

/**
 * Tests loading of microarray sets, and makes loading services available to other tests.
 *
 * @author John Watkinson
 */
public class TestFileLoad extends TestCase {

    public static final String DEFAULT_MICROARRAY_SET = "data/aTestDataSet.exp";

    public TestFileLoad(String name) {
        super(name);
    }

    public static DSDataSet loadDataSet(String fileName, DataSetFileFormat fileFormat) throws Exception {
        System.out.println("Loading file...");
        DSDataSet dataSet = fileFormat.getDataFile(new File(fileName));
        System.out.println("...file loaded.");
        return dataSet;
    }

    public static CSExprMicroarraySet loadDefaultMicroarraySet() throws Exception {
        String fileName = DEFAULT_MICROARRAY_SET;
        DataSetFileFormat fileFormat = new org.geworkbench.engine.parsers.ExpressionFileFormat();
        DSDataSet dataSet = loadDataSet(fileName, fileFormat);
        return (CSExprMicroarraySet) dataSet;
    }

    public void testLoadData() throws Exception {
        DSDataSet dataSet = loadDefaultMicroarraySet();
        assertNotNull(dataSet);
    }
}
