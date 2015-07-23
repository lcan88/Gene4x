package org.geworkbench.bison.datastructure.bioobjects.markers;

import org.geworkbench.bison.datastructure.properties.DSSequential;
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
 *          This class is used to represent any arbitrary genetic marker, such as an Affy probe, a DNA sequence, etc.
 *          The accession should be the universal identifier of this data. This should be compatible with caBIO representation
 */

public interface DSGeneMarker extends Comparable, DSSequential, Cloneable, Serializable {

    static final int AFFY_TYPE = 0;
    static final int GENEPIX_TYPE = 1;
    static final int UNIGENE_TYPE = 2;
    static final int LOCUSLINK_TYPE = 3;

    /**
     * Returns the textual description of this Marker
     *
     * @return a String representing the textual representation
     */
    String getDescription();

    void setDescription(String label);

    /**
     * Returns a unique identifier that represent this piece of genetic information
     *
     * @return a unique identifier
     */
    int getGeneId();

    DSUnigene getUnigene();

    /**
     * @return String
     */
    String getShortName();

    boolean isEquivalent(DSGeneMarker mInfo);

    /**
     * Make a deep copy of this marker.
     *
     * @return
     */
    DSGeneMarker deepCopy();

    void write(BufferedWriter writer) throws IOException;

    public void setDisPlayType(int disPlayType);

    public int getDisPlayType();

    public void setGeneId(int x);

    public void setGeneName(String name);

    public String getGeneName();

    public Object clone();

}
