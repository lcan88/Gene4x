package org.geworkbench.bison.datastructure.properties;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>Title: Bioworks</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CSUnigene implements DSUnigene, Serializable {
    public static ArrayList<String> organisms = new ArrayList<String>();

    static {
        organisms.add("Bt");
        organisms.add("Hs");
        organisms.add("Mm");
        organisms.add("Rn");
        organisms.add("Ssc");
        organisms.add("Gga");
        organisms.add("Str");
        organisms.add("Xl");
        organisms.add("Dr");
        organisms.add("Ola");
        organisms.add("Omy");
        organisms.add("Cin");
        organisms.add("Dm");
        organisms.add("Aga");
        organisms.add("Spu");
        organisms.add("Cel");
        organisms.add("Pta");
        organisms.add("Ppa");
        organisms.add("Gma");
        organisms.add("Les");
        organisms.add("At");
        organisms.add("Mtr");
        organisms.add("Stu");
        organisms.add("Vvi");
        organisms.add("Hv");
        organisms.add("Os");
        organisms.add("Sof");
        organisms.add("Sbi");
        organisms.add("Ta");
        organisms.add("Zm");
        organisms.add("Cre");
        organisms.add("Ddi");
        organisms.add("Tgo");
    }

    protected int unigeneId = -1;
    protected int organism = -1;

    public CSUnigene() {
    }

    public String getUnigeneAsString() {
        if (organism > 0) {
            return (String) organisms.get(organism) + "." + unigeneId;
        } else {
            return "";
        }
    }

    public int getUnigeneId() {
        return unigeneId;
    }

    public void setUnigeneId(int unigeneId) {
        this.unigeneId = unigeneId;
    }

    public int getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = CSUnigene.organisms.indexOf(organism);
    }

    public void set(DSGeneMarker marker) {
        if (unigeneId == -1) {
            try {
                String[] annots = AnnotationParser.getInfo(marker.getLabel(), AnnotationParser.UNIGENE);
                String uni = "";
                if ((annots != null) && (annots.length > 0)) {
                    uni = annots[0];
                    if (uni.compareTo("") != 0) {
                        String[] unigeneStrings = uni.split("\\.");
                        if (unigeneStrings.length > 1) {
                            organism = organisms.indexOf(unigeneStrings[0]);
                            unigeneId = Integer.parseInt(unigeneStrings[1]);
                        } else {
                            unigeneId = -marker.getSerial();
                        }
                    } else {
                        this.unigeneId = -marker.getSerial();
                    }
                } else {
                    this.unigeneId = -marker.getSerial();
                }
            } catch (Exception e) {
                System.out.println("error parsing " + marker.getLabel());
                e.printStackTrace();
                unigeneId = -marker.getSerial();
            }
        }

    }

    public void set(String label) {
        String[] unigeneIds = AnnotationParser.getInfo(label, AnnotationParser.UNIGENE);
        if (unigeneIds != null) {
            String unigeneid = unigeneIds[0];
            if (unigeneid.length() > 1) {
                String unigene = unigeneid.substring(unigeneid.indexOf('.') + 1, unigeneid.length());
                unigeneId = Integer.parseInt(unigene);
                String org = unigeneid.substring(0, unigeneid.indexOf('.'));
                organism = (organisms.indexOf(org));
            }
        } else {
            unigeneIds = AnnotationParser.getInfo(label, AnnotationParser.UNIGENE);
        }
    }
}
