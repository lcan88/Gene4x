package org.geworkbench.bison.datastructure.properties;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;

public interface DSUnigene {
    int getUnigeneId();

    void setUnigeneId(int unigeneId);

    int getOrganism();

    public String getUnigeneAsString();

    void setOrganism(String organism);

    void set(DSGeneMarker marker);

    void set(String label);
}
