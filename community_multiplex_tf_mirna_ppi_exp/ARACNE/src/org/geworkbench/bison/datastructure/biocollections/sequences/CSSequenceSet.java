package org.geworkbench.bison.datastructure.biocollections.sequences;

import org.geworkbench.bison.datastructure.biocollections.CSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.SequenceMarker;
import org.geworkbench.bison.datastructure.bioobjects.sequence.CSSequence;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.panels.CSSequentialItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.*;
import java.util.HashMap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSSequenceSet<T extends DSSequence> extends CSDataSet<T> implements DSSequenceSet<T> {

    static private HashMap databases = new HashMap();
    private boolean dirty = false;
    private boolean isDNA = true;
    //private ArrayList sequences = new ArrayList();
    private int maxLength = 0;
    private String label = "Undefined";
    private int sequenceNo = 0;
    private File file = null;
    private DSItemList<SequenceMarker> markerList = null;
    //added by xiaoqing for bug fix to create subsetSequenceDB. which matchs the old sequences index with new temp sub seqenceDB index.
    //Need rewrite to fit with caWorkbench3.
    private int[] matchIndex;
    private int[] reverseIndex;

    public CSSequenceSet() {
        setID(RandomNumberGenerator.getID());
    }

    public String getDataSetName() {
        return label;
    }

    public void addASequence(T sequence) {
        if (!sequence.isDNA()) {
            isDNA = false;
        }
        this.add(sequence);
        sequence.setSerial(this.indexOf(sequence));
        // @todo - watkin - This marker is not stored anywhere!? Why is it created?
        SequenceMarker marker = new org.geworkbench.bison.datastructure.
                                bioobjects.markers.SequenceMarker();
        marker.parseLabel(sequence.getLabel());
        marker.setSerial(this.size());

        if (sequence.length() > maxLength) {
            maxLength = sequence.length();
        }
    }

    public int getSequenceNo() {

        return this.size();
    }

    public T getSequence(int i) {
        if ((this.size() == 0) && (file != null)) {
            readFASTAFile(file);
        }
        if (i < this.size() && i >= 0) {
            return this.get(i);
        } else {
            return null;
        }
    }

    public T getSequence(DSGeneMarker marker) {
    if ((this.size() == 0) && (file != null)) {
        readFASTAFile(file);
    }
    if (markerList.contains(marker)) {
        int i = markerList.indexOf(marker);
        return this.get(i);
    } else {
        return null;
    }
}

public DSSequenceSet getActiveSequenceSet(DSPanel<? extends DSGeneMarker> markerPanel){
    CSSequenceSet sequenceDB = new CSSequenceSet();
    if(markerPanel!=null && markerPanel.size()>0){
        for(DSGeneMarker marker: markerPanel){

                T newSequence = this.getSequence(marker);
                 if(newSequence!=null){
                sequenceDB.addASequence(newSequence);
            }
        }
        sequenceDB.setFASTAFile(file);
    }
    return sequenceDB;
}



    public int getMaxLength() {
        return maxLength;
    }

    public boolean isDNA() {
        return isDNA;
    }

    public static DSSequenceSet createFASTAfile(File file) {
        CSSequenceSet seqDB = new CSSequenceSet();
        seqDB.readFASTAFile(file);
        return seqDB;
    }

    public void readFASTAFile(File inputFile) {
        file = inputFile;
        label = file.getName();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            T sequence = null;
            String data = new String();
            String s = reader.readLine();
            int num = 0;
            while (reader.ready()) {
                if (s.trim().length() == 0) {

                } else if (s.startsWith(">")) {
                    num++;
                    if (sequence != null) {
                        sequence.setSequence(data);
                        addASequence(sequence);
                    }
                    sequence = (T)(new CSSequence());
                    sequence.setLabel(s);
                    data = new String();
                } else {
                    data += s;
                }
                s = reader.readLine();

            }
            if (sequence != null) {
                sequence.setSequence(data + s);
                addASequence(sequence);
            }
        } catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }
        parseMarkers();
        databases.put(file.getPath(), this);
        addDescription("# of sequences: " + size());
    }

    public void parseMarkers() {
        markerList = new CSSequentialItemList<SequenceMarker>();
        for (int markerId = 0; markerId < size(); markerId++) {
            SequenceMarker marker = new SequenceMarker();
            DSSequence sequence = this.get(markerId);
            marker.parseLabel(sequence.getLabel());
            sequence.addDescription(sequence.getLabel());
            // Use the short label as the label for the sequence as well (bug #251)
            if ((marker.getLabel() != null) && (marker.getLabel().length() > 0)) {
                sequence.setLabel(marker.getLabel());
            }
            marker.setSerial(markerId);
            markerList.add(markerId, marker);
        }
    }

    /**
     * initIndexArray
     */
    private void initIndexArray() {
        int size = size();
        matchIndex = new int[size];
        reverseIndex = new int[size];
        for (int i = 0; i < size; i++) { //init.
            matchIndex[i] = -1;
            reverseIndex[i] = -1;
        }

    }

    public void readFromResource() {

    }

    public void writeToResource() {

    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean flag) {
        dirty = flag;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMatchIndex(int[] matchIndex) {
        this.matchIndex = matchIndex;
    }

    public void setReverseIndex(int[] reverseIndex) {
        this.reverseIndex = reverseIndex;
    }


    public File getFile() {
        return file;
    }

    public String toString() {
        if (file != null) {
            return file.getName();
        } else {
            return label;
        }
    }

    static public CSSequenceSet getSequenceDB(File file) {
        CSSequenceSet sequenceDB = (CSSequenceSet) databases.get(file.getPath());
        if (sequenceDB == null) {
            sequenceDB = new CSSequenceSet();
            sequenceDB.readFASTAFile(file);
        }
        return sequenceDB;
    }

    public String getFASTAFileName() {
        return file.getAbsolutePath();
    }

    public void setFASTAFile(File f) {
        file = f;

    }

    public String getLabel() {
        return label;
    }

    /**
     * getCompatibilityLabel
     *
     * @return String
     */
    public String getCompatibilityLabel() {
        return "FASTA";
    }

    public DSItemList<? extends DSGeneMarker> getMarkerList() {
        return markerList;
    }

    @Override public T get(String label){
        DSGeneMarker marker = getMarkerList().get(label);
        if (marker != null)
            return get(marker.getSerial());
        return super.get(label);
    }

    public int[] getMatchIndex() {
        return matchIndex;
    }

    public int[] getReverseIndex() {
        return reverseIndex;
    }

    public DSSequenceSet createSubSetSequenceDB(boolean[] included) {
        DSSequenceSet newDB = new CSSequenceSet();
        int newIndex = 0;
        initIndexArray();
        for (int i = 0; i < included.length; i++) {
            if (i<this.size() && included[i]) {
                newDB.addASequence(getSequence(i));
                matchIndex[i] = newIndex;
                reverseIndex[newIndex] = i;

                newIndex++;
            }
        }
        return newDB;
    }


    public void writeToFile(String fileName) {
        file = new File(fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < this.getSequenceNo(); i++) {
                T s = this.getSequence(i);
                out.write(">" + s.getLabel() + "\n");
                out.write(s.getSequence() + "\n");
            }
            out.close();
        } catch (IOException ex) {
            System.out.println("Error opening file: " + fileName);
        }

    }
}
