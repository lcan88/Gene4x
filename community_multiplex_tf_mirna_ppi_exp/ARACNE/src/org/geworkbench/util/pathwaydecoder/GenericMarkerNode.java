package org.geworkbench.util.pathwaydecoder;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.properties.DSUnigene;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GenericMarkerNode implements DSGeneMarker, Serializable {
    DSGeneMarker genericMarker = null;
    int connectionNo = 0;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Shouldn't happen
            e.printStackTrace();
            return null;
        }
    }

    public GenericMarkerNode(DSGeneMarker gm) {
        genericMarker = gm;
    }

    public String getDescription() {
        return genericMarker.getDescription();
    }

    public void setDescription(String label) {
        genericMarker.setDescription(label);
    }

    public String getLabel() {
        return genericMarker.getLabel();
    }

    public int getGeneId() {
        return genericMarker.getGeneId();
    }

    public void setLabel(String accession) {
        genericMarker.setLabel(accession);
    }

    public int getSerial() {
        return genericMarker.getSerial();
    }

    public void setSerial(int serial) {
        genericMarker.setSerial(serial);
    }

    public int compareTo(Object o) {
        return genericMarker.compareTo(o);
    }

    public boolean equals(DSGeneMarker m) {
        return genericMarker.equals(m);
    }

    public String getGeneName() {
        return genericMarker.getGeneName();
    }

    public void setGeneName(String geneName) {
        genericMarker.setGeneName(geneName);
    }

    public String toString() {
        String shortName = genericMarker.getShortName() + ":" + connectionNo; // = "N/A: " + this.getLabel();
        return shortName;
    }

    public boolean equals(Object o) {
        //if (o instanceof GenericMarkerNode) {
        return (hashCode() == o.hashCode());
        //}
        //return false;
    }

    public int hashCode() {
        return new Integer(genericMarker.getSerial()).hashCode();
    }

    public Object[] getRelatedInfoObject(int x) { //a dumb implementation
        return null;
    }

    public String[] getRelatedInfoText(int fieldName) {
        return null;
    }

    public void setConnectionNo(int n) {
        connectionNo = n;
    }

    /**
     * getShortName
     *
     * @return String
     */
    public String getShortName() {
        return genericMarker.getShortName();
    }

    /**
     * isEquivalent
     *
     * @param mInfo IGenericMarker
     * @return boolean
     */
    public boolean isEquivalent(DSGeneMarker mInfo) {
        return genericMarker.isEquivalent(mInfo);
    }

    /**
     * @todo check these methods
     */

    public DSGeneMarker deepCopy() {
        return null;
    }

    public void write(BufferedWriter writer) throws IOException {
        genericMarker.write(writer);
    }

    public DSUnigene getUnigene() {
        return genericMarker.getUnigene();
    }

    public void setDisPlayType(int disPlayType) {
        genericMarker.setDisPlayType(disPlayType);
    }

    public int getDisPlayType() {
        return genericMarker.getDisPlayType();
    }

    public void setGeneId(int x) {
        genericMarker.setGeneId(x);
    }

}
