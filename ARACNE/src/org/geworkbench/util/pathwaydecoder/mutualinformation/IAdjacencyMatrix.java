package org.geworkbench.util.pathwaydecoder.mutualinformation;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;

import java.util.HashMap;
import java.util.Set;


/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author Califano Lab
 * @version 1.0
 */

public interface IAdjacencyMatrix {

    /**
     * this adds information from adj to this adj mtx <BR>
     * currently the MI edges are added as STRINGS <BR>
     *
     * @param adj AdjacencyMatrix
     */
    // void addAdjMatrix(AdjacencyMatrix adj);

    HashMap getGeneRows();

    HashMap getKeyMapping();

    /**
     * returns the strength of the edge between geneId1 and geneId2 (0.0 == no edge)
     *
     * @param geneId1 int
     * @param geneId2 int
     * @return float
     */
    float get(int geneId1, int geneId2);

    HashMap getInteractionMap();

    HashMap getInteraction(int geneId);

    /**
     * Returns a map with all the edges to geneId
     *
     * @param geneId int
     * @return HashMap
     */
    HashMap get(int geneId);

    void addGeneRow(int geneId);

    /**
     * returns the interaction strength (MI) at position pos<BR>
     * it returns 99999 in the case it is null <BR>
     * @param geneId1 int
     * @param geneId2 int
     * @return double
     */
    //    double getInteractionType2Strength(int pos);

    /**
     * returns the interaction strength (MI) between geneA and geneB<BR>
     * if there is no interaction, return 0.00
     * if there is no MI interaction, return 99998, so that it can be drawn...
     * otherwise returns the MI interaction
     * @param geneId1 int
     * @param geneId2 int
     * @return double
     */
    //    double getInteractionType2Strength(int geneId1, int geneId2);

    /**
     * returns all of the MI edges
     * @return ArrayList
     */
    //    ArrayList getAllMIStrengths();

    /**
     * returns all [geneA, interactionType, geneB] in this adjmtx
     * @return ArrayList
     */
    //    ArrayList getAllInteractionType2();

    /**
     * type 2 interaction refers to this experimental data type
     * which stores the data in array lists
     * @param geneId1 int
     * @param geneId2 int
     * @param interaction String
     */
    //    void addInteractionType2(int geneId1, int geneId2, String interaction);

    /**
     * add the gene pair along with the Mutual Information
     * @param geneId1 int
     * @param geneId2 int
     * @param mi float
     */
    //    void addInteractionType2(int geneId1, int geneId2, double mi);

    /**
     * this changes the interaction (edge) strength between geneA and geneB
     * @param geneId1 int
     * @param geneId2 int
     * @param mi double
     */
    //    void changeInteractionType2Strength(int geneId1, int geneId2, double mi);

    /**
     * true = mutual information is used to construct the network
     * @param flg boolean
     */
    //    void setMIflag(boolean flg);

    //    boolean getMIflag();

    //    ArrayList getUniqueInteractionType();

    //    int getNumType2Interaction();

    /**
     * returns the idx-th geneA
     * @param idx int
     * @return int
     */
    // int getInteractionType2GeneAID(int idx);

    // int getInteractionType2GeneBID(int idx);

    // String getInteractionType2Action(int idx);

    // void addInteraction(int geneId1, int geneId2, String interaction);

    // String getInteractionText();

    /**
     * the filter to be applied
     * @param filter Object[]
     */
    // void setFilter(Object[] filter);

    // Object[] getFilter();

    /**
     * this should be modified to show something like:
     * the list of interacting genes???
     *
     * maybe just generate the interaction list on the fly???
     * @param text String
     */
    //    void setInteractionText(String text);

    /**
     * Adds and edge between geneId1 and geneId2
     *
     * @param geneId1 int
     * @param geneId2 int
     * @param edge    float
     */
    void add(int geneId1, int geneId2, float edge);

    void addDirectional(int geneId1, int geneId2, float edge);

    String getMarkerName(int index);

    void addMarkerName(int pos, String gname);

    /**
     * if we don't specify the gene name, use the annotation from the Affy array<BR>
     * serial = the serial number of the gene on the chip<BR>
     *
     * @param serial int
     */
    void addMarkerName(int serial);

    int getMappedId(int geneId);

    void clear();

    int size();

    Set getKeys();

    Object getValue(Object key);

    int getEdgeNo(int bin);

    double getThreshold(int bin);

    int getConnectionNo(int geneId, double threshold);

    void setMicroarraySet(DSMicroarraySet microarraySet);

    DSMicroarraySet getMicroarraySet();
}
