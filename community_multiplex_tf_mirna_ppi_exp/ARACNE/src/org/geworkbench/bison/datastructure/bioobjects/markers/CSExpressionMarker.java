package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.util.Range;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;

/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

public class CSExpressionMarker extends CSGeneMarker implements org.geworkbench.bison.datastructure.bioobjects.markers.DSRangeMarker, Serializable {
    private Range range = new org.geworkbench.bison.util.Range();


    @Override public Object clone() {
        CSExpressionMarker clone = (CSExpressionMarker) super.clone();
        clone.range = new org.geworkbench.bison.util.Range();
        return clone;
    }

    public CSExpressionMarker() {
        markerId = 0;
    }

    public CSExpressionMarker(int id) {
        markerId = id;
    }

    /**
     * @param marker
     * @param isCase
     */
    public void check(DSMutableMarkerValue marker, boolean isCase) {
        range.min = Math.min(range.min, marker.getValue());
        range.max = Math.max(range.max, marker.getValue());
        range.norm.add(marker.getValue());
    }

    /**
     * @param id
     * @param casesNum
     * @param controlsNum
     */
    public void reset(int id, int casesNum, int controlsNum) {
        //ready     = false;
        range.max = -999999;
        range.min = +999999;
        range.norm = new org.geworkbench.bison.util.Normal();
        markerId = id;
    }

    public Range getRange() {
        return range;
    }

    public void write(BufferedWriter writer) throws IOException {
        writer.write(label);
        writer.write('\t');
        writer.write(label);
    }

    /**
     * todo make this more generalize
     *
     * @return String
     */
    public String toString() {
        return getCanonicalLabel();
    }

    /**
     * @param o
     * @return
     */
    public boolean equals(DSGeneMarker o) {
        return label.equalsIgnoreCase(o.getLabel());
    }

    /**
     * Program that use equals() method will automatically use this one indtead of the above method.
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        //change this temporarily to match on the GeneID so we can call contains on markers that
        //represent equivalent genes. May need a separate class for this.
        return super.equals(o);

        //        if(o != null) {
        //            if(o instanceof DSGeneMarker) {
        //                if(label == null) {
        //                    if(this == o) {
        //                        return true;
        //                    }
        //                    return false;
        //                }
        //                return getLabel().equalsIgnoreCase((((DSGeneMarker)o).getLabel()));
        //            } else {
        //                if(o instanceof CSExpressionMarker) {
        //                    return markerId == ((CSExpressionMarker)o).markerId;
        //                } else {
        //                    return false;
        //                }
        //            }
        //
        //        } else {
        //            return false;
        //        }
    }

    protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        super.readObject(ois);
        range = new org.geworkbench.bison.util.Range();
    }

    public DSGeneMarker deepCopy() {
        CSExpressionMarker gi = new CSExpressionMarker(markerId);
        gi.range = new org.geworkbench.bison.util.Range();
        gi.range.min = this.range.min;
        gi.range.max = this.range.max;
        gi.label = label;
        gi.geneId = geneId;
        gi.unigene = unigene;
        return gi;
    }

    public void setSerial(int serial) {
        this.markerId = serial;
    }

    static final Comparator SORT_LocusLink = new Comparator() {
        public int compare(Object o1, Object o2) {
            CSExpressionMarker rs1 = (CSExpressionMarker) o1;
            CSExpressionMarker rs2 = (CSExpressionMarker) o2;
            if ((rs1.geneId == 0) && (rs2.geneId == 0)) {
                return -1;
            } else if (rs1.geneId == rs2.geneId) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    static final Comparator SORT_Unigene = new Comparator() {
        public int compare(Object o1, Object o2) {
            CSExpressionMarker rs1 = (CSExpressionMarker) o1;
            CSExpressionMarker rs2 = (CSExpressionMarker) o2;

            if ((rs1.unigene.getUnigeneId() == 0) && (rs2.unigene.getUnigeneId() == 0)) {
                return -1;
            } else if (rs1.unigene == rs2.unigene) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    public int getGeneId() {
        if (this.geneId == -1) {
            try {
                String uni = (AnnotationParser.getInfo(label, AnnotationParser.LOCUSLINK)[0]);

                if (uni.compareTo("") != 0) {
                    this.geneId = Integer.parseInt(uni);
                } else {
                    this.geneId = 0;
                }
            } catch (Exception e) {
                //                System.out.println("error parsing " + this.accession);
                //                e.printStackTrace();
            }
        }
        return this.geneId;
    }
}
