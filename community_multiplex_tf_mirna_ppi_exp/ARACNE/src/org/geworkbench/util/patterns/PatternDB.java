package org.geworkbench.util.patterns;

import org.geworkbench.bison.datastructure.biocollections.CSAncillaryDataSet;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.DSMatchedSeqPattern;
import org.geworkbench.bison.util.RandomNumberGenerator;
import polgara.soapPD_wsdl.Parameters;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PatternDB extends CSAncillaryDataSet implements Serializable {
    private final static ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("dirty", boolean.class), new ObjectStreamField("file", File.class), new ObjectStreamField("sequenceFile", File.class), new ObjectStreamField("parms", Parameters.class)};

    static private ImageIcon icon = new ImageIcon(PatternDB.class.getResource("pattern.gif"));

    private boolean dirty = false;
    protected ArrayList patterns = new ArrayList();
    private Parameters parms = new Parameters();
    protected File dataSetFile;

    public PatternDB(File _file, File _seqFile, DSDataSet parent) {
        super(parent, "PatternDB");
        dataSetFile = new File(_seqFile.getName());
        read(file);
        setID(RandomNumberGenerator.getID());
    }

    public PatternDB(File _seqFile, DSDataSet parent) {
        super(parent, "PatternDB");
        dataSetFile = new File(_seqFile.getName());
    }

    public boolean read(File _file) {
        try {
            file = new File(_file.getCanonicalPath());
            label = file.getName();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String s = reader.readLine();
            if (s.startsWith("File:")) {
                File newFile = new File(s.substring(5));
                if (!dataSetFile.getName().equalsIgnoreCase(newFile.getName())) {
                    return false;
                }
                s = reader.readLine();
            }
            patterns.clear();
            while (s != null) {
                CSMatchedSeqPattern pattern = new org.geworkbench.util.patterns.CSMatchedSeqPattern(s);
                patterns.add(pattern);
                s = reader.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }
        return true;
    }

    public void write(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int i = 0;
            Iterator it = patterns.iterator();
            String path = this.getDataSetFile().getCanonicalPath();
            writer.write(org.geworkbench.util.AlgorithmSelectionPanel.DISCOVER);
            writer.newLine();
            writer.write("File:" + path);
            writer.newLine();
            while (it.hasNext()) {
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern) it.next();
                writer.write("[" + i++ + "]\t");
                pattern.write(writer);
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }
    }

    public void setFile(File _file) {
        file = _file;
        label = file.getName();
    }

    public int getPatternNo() {
        if (patterns == null) {
            patterns = new ArrayList();
            read(file);
        }
        return patterns.size();
    }

    public DSMatchedSeqPattern getPattern(int i) {
        if ((patterns.size() == 0) && (file != null)) {
            read(file);
        }
        if (i < patterns.size()) {
            return (DSMatchedSeqPattern) patterns.get(i);
        }
        return null;
    }

    public void add(DSMatchedSeqPattern pattern) {
        patterns.add(pattern);
    }

    public void setParameters(Parameters p) {
        parms = p;
    }

    public void write() {
        String name = dataSetFile.getName() + Math.random() + ".pat";
        file = new File(name);
        write(file);
    }

    public boolean equals(Object ads) {
        if (ads instanceof PatternDB) {
            PatternDB pdb = (PatternDB) ads;
            if (pdb.getPatternNo() == getPatternNo()) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * TODO - unused
     *
     * @param out ObjectOutputStream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * TODO - unused
     *
     * @param in ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * TODO - unused
     * writeToFile
     *
     * @param fileName String
     */
    public void writeToFile(String fileName) {
    }

    public File getDataSetFile() {
        return dataSetFile;
    }

    public void setDataSetFile(File _file) {
        dataSetFile = _file;
    }
}
