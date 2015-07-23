package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.properties.CSUnigene;
import org.geworkbench.bison.datastructure.properties.DSUnigene;

import java.io.*;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSGeneMarker implements DSGeneMarker, Serializable {

    protected String label = null;
    protected String description = null;
    protected String abrev = null;
    protected int markerId = 0;
    //refactored locusLinkId to geneId -- AM
    protected int geneId = -1;
    protected DSUnigene unigene = new CSUnigene();
    String geneName = new String();
    protected int disPlayType = DSGeneMarker.AFFY_TYPE;


    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Shouldn't happen
            e.printStackTrace();
            return null;
        }
    }

    public CSGeneMarker() {
    }

    public CSGeneMarker(String label) {
        this.label = label;
    }

    public void setDisPlayType(int disPlayType) {
        this.disPlayType = disPlayType;
    }

    public int getDisPlayType() {
        return disPlayType;
    }

    /**
     * getLabel
     *
     * @return String
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String label) {
        this.description = new String(label);
    }

    /**
     * getAccession
     *
     * @return String
     */
    public String getLabel() {
        return label;
    }

    public int getGeneId() {
        return geneId;
    }

    public void setLabel(String label) {
        this.label = new String(label);
    }

    public void setGeneId(int locusLink) {
        this.geneId = locusLink;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    /**
     * getSerial
     *
     * @return int
     */
    public int getSerial() {
        return markerId;
    }

    public void setSerial(int id) {
        markerId = id;
    }

    protected void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CSGeneMarker) {
            CSGeneMarker mInfo = (CSGeneMarker) obj;

            String markerLabel = mInfo.getLabel();
            if (markerLabel != null) {
                return markerLabel.equals(label);
            } else {
                return (label == null);
            }
        }
        return false;

        //            String label = mInfo.getLabel();
        //            if(label != null){
        //                return label.equalsIgnoreCase(this.label);
        //            }else{
        //                return false;
        //            }
        //        }else{
        //            return false;
        //        }
        //        }

        //        if(obj instanceof CSGeneMarker) {
        //            CSGeneMarker mInfo = (CSGeneMarker)obj;
        //            if(label == null) {
        //                return false;
        //            } else {
        //                if((label != null) && (mInfo.getDescription() != null)) {
        //                    if(label.compareTo(mInfo.getDescription()) == 0) {
        //                        if(this.getClass().equals(mInfo.getClass())) {
        //                            return true;
        //                        }
        //                    }
        //                }
        //                return isEquivalent(mInfo);
        //            }
        //        } else {
        //            return false;
        //        }
    }

    /**
     * Implementation of the <code>Comparable</code> interface.
     *
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        return label.compareToIgnoreCase(((DSGeneMarker) o).getLabel());
    }

    public String toString() {
        return getCanonicalLabel();
    }

    public String getShortName() {
        String name = AnnotationParser.getGeneName(label);
        if(name == null) {
            return label;
        }
        return name;
    }

    public boolean isEquivalent(DSGeneMarker i) {
        //I think we may want to enforce only comparing genes on the geneId. This is the only thing
        //that guarantees 1-to-1 equivalence -- AM
        if ((this.getGeneId() == -1) || (i.getGeneId() == -1)) {
            return false;
        } else {
            return this.getGeneId() == i.getGeneId();
        }

        //        if((this.getGeneId() == -1) || (i.getGeneId() == -1)) { //can we use locuslink to compare?
        //            if((this.getUnigene().getUnigeneId() <= 0) || (i.getUnigene().getUnigeneId() <= 0)) { //can we use unigene to compare?
        //                if((this.getShortName() == null) || (i.getShortName() == null)) { //can we use shortname to compare?
        //                    if((this.getDescription() == null) || (i.getDescription() == null)) { //can we use label?
        //                        return false;
        //                    } else {
        //                        if(this.getDescription() == i.getDescription()) { //compare label
        //                            return true;
        //                        } else {
        //                            return false;
        //                        }
        //
        //                    }
        //                } else {
        //                    if(this.getShortName() == i.getShortName()) {
        //                        return true;
        //                    } else {
        //                        return false;
        //                    }
        //                }
        //            } else {
        //                if(this.getUnigene() == i.getUnigene()) {
        //                    return true;
        //                } else {
        //                    return false;
        //                }
        //            }
        //
        //        } else {
        //            if(this.getGeneId() == i.getGeneId()) {
        //                return true;
        //            } else {
        //                return false;
        //            }
        //        }

    }

    public int hashCode() {
        // watkin -- made consistent with equals()
        return label.hashCode();
        //        if (geneId != -1) {
        //            return geneId;
        //        } else if (label != null) {
        //            return label.hashCode();
        //        } else {
        //            return markerId;
        //        }
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(label);
        writer.write('\t');
        writer.write(description);
    }

    public DSUnigene getUnigene() {
        return unigene;
    }

    public String getGeneName() {
        if (geneName == null) {
            geneName = getShortName();
        }
        return geneName;
    }

    public DSGeneMarker deepCopy() {
        CSGeneMarker copy = null;
        try {
            copy = (CSGeneMarker) this.getClass().newInstance();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }

        copy.setLabel(getLabel());
        copy.setSerial(markerId);
        copy.setDescription(description);
        copy.abrev = abrev;
        copy.geneId = geneId;
        copy.unigene = unigene;
        return copy;
    }

    public String getCanonicalLabel() {
        String disType = "";
        if (disPlayType == AFFY_TYPE) {
            disType = label;
        } else {
            if (disPlayType == UNIGENE_TYPE) {
                int uni = this.getUnigene().getUnigeneId();
                if (uni > 0) {
                    disType = this.getUnigene().getUnigeneAsString();
                } else {
                    disType = "NA";
                }
            } else if (disPlayType == LOCUSLINK_TYPE) {
                int lo = this.getGeneId();
                if (lo != 0) {
                    disType = lo + "";
                } else {
                    disType = "NA";

                }
            }
        }
        String text0 = "";

        try {
            String text1 = AnnotationParser.getInfo(label, AnnotationParser.DESCRIPTION)[0];
            text0 = disType + ": (" + getShortName() + ") " + text1;
        } catch (Exception ex) {
            text0 = this.getDescription();
            if (text0 != null) {
                int index = text0.indexOf("/DEFINITION");
                if (index >= 0) {
                    String text = text0.substring(index + 12);
                    text0 = label + ": " + text.replace('"', ' ').trim();
                } else {
                    String text = new String(text0);
                    if (text0.indexOf("Cluster Incl.") >= 0) {
                        index = text0.indexOf(":");
                        text = text.substring(index + 1);
                    }
                    String[] labels = text.split("/");
                    text0 = label + ": " + labels[0].replace('"', ' ').trim();
                }
            }
        }

        if (text0 == null || text0.length() == 0) {
            return disType;
        } else {
            return text0;
        }
    }
}
