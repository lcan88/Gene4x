package org.geworkbench.bison.datastructure.properties;

public interface DSAffyIdentifiable extends DSUnigene, DSLocusLinkIdentifiable {
    String getAffyId();

    void setAffyId(String affyId);
}
