package org.geworkbench.engine.parsers.microarray;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.engine.parsers.FileFormat;
import org.geworkbench.engine.parsers.InputFileFormatException;

import java.io.File;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


public abstract class DataSetFileFormat extends FileFormat {
  public DataSetFileFormat() {
  }
  abstract public DSDataSet getDataFile(File file) throws InputFileFormatException;
  abstract public DSDataSet getDataFile(File[] files) throws InputFileFormatException;
}
