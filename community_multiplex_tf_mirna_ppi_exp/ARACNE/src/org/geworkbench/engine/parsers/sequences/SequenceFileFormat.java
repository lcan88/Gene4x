package org.geworkbench.engine.parsers.sequences;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.sequences.CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.engine.parsers.InputFileFormatException;
import org.geworkbench.engine.parsers.microarray.DataSetFileFormat;

import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * <p>Title: SequenceFileFormat</p>
 * <p>Description: SequenceFileFormat</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 *
 * @author Saroja Hanasoge
 * @version 1.0
 */


public class SequenceFileFormat extends DataSetFileFormat {

  String[]         fastaExtensions = {"FA", "fasta", "fa", "txt"};
  SequenceResource resource        = new SequenceResource();
  FASTAFilter      fastaFilter     = null;

  public SequenceFileFormat() {
    formatName  = "FASTA File";
    fastaFilter = new FASTAFilter();
    //Arrays.sort(fastaExtensions);
  }

  public Resource getResource(File file) {
    try {
      resource.setReader(new BufferedReader(new FileReader(file)));
      resource.setInputFileName(file.getName());
    } catch (IOException ioe) {
        ioe.printStackTrace(System.err);
    }
    return resource;
  }

  public String[] getFileExtensions() {
    return fastaExtensions;
  }

  public boolean checkFormat(File file){

        // accepted acid codes for the FASTA format
        char[] accepted = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'k', 'l',
            'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '-', '*', '#'};

        java.util.HashSet charSet = new java.util.HashSet();
        for (int i = 0; i < accepted.length; i++) {
            charSet.add(new Character(accepted[i]));
        }

        boolean ret = false;

        try {
            BufferedReader rd = new BufferedReader(new FileReader(file));
            try {
                String str = null;

                while ((str = rd.readLine()) != null){
                    String trim = str.trim();

                    if (trim.length() == 0 || trim.charAt(0) == '>'){
                        continue;
                    }

                    for (int i = 0; i < trim.length(); i++){
                        if (!charSet.contains(new Character(trim.charAt(i)))){
                            return false;
                        }
                        ret = true;
                    }
                }
            }
            catch (Exception e) {
                return false;
            }
            finally {
                rd.close();
            }
        }
        catch (Exception f) {
            return false;
        }
        return ret;
  }

  public DSDataSet getDataFile(File file) throws InputFileFormatException{
        if (file == null)
            return null;

        // Check that the file is OK before starting allocating space for it.
        if (!checkFormat(file))
            throw new InputFileFormatException(
                "SequenceFileFormat::getDataFile - " +
                "Attempting to open a file that does not comply with the " +
                "FASTA format.");

    DSSequenceSet sequenceDB = null;
    try {
      sequenceDB = CSSequenceSet.createFASTAfile(file);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
    return sequenceDB;
  }
  public DSDataSet getDataFile(File[] files) {

    return null;
  }


  public DSMicroarraySet getMArraySet(File file) {
    return null;
  }

  public List getOptions() {
    /**@todo Implement this geaw.resource.FileFormat abstract method*/
    throw new java.lang.UnsupportedOperationException("Method getOptions() not yet implemented.");
  }

  public FileFilter getFileFilter() {
    return fastaFilter;
  }
  /**
   *
   * Defines a <code>FileFilter</code> to be used when the user is prompted
   * to select Affymetrix input files. The filter will only display files
   * whose extension belongs to the list of file extension defined in {@link
   * #affyExtensions}.
   */
  public class FASTAFilter extends FileFilter {

    public String getDescription() {
      return "FASTA Files";
    }

    public boolean accept(File f) {
      boolean returnVal = false;
      for (int i = 0; i < fastaExtensions.length; ++i)
        if (f.isDirectory() || f.getName().endsWith(fastaExtensions[i])) {
          return true;
        }
      return returnVal;
    }
  }
}
