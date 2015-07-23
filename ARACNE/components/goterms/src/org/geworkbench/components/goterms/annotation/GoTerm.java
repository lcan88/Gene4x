package org.geworkbench.components.goterms.annotation;

import gov.nih.nci.caBIO.bean.ExpressionMeasurement;
import gov.nih.nci.caBIO.bean.Gene;
import gov.nih.nci.caBIO.bean.GoOntology;
import gov.nih.nci.caBIO.bean.GoOntologySearchCriteria;
import gov.nih.nci.common.exception.ManagerException;
import gov.nih.nci.common.exception.OperationException;
import gov.nih.nci.common.search.SearchResult;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;

import javax.swing.*;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class overrides the toString() method in GoOntology to display it
 * on a JTree.
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Columbia University</p>
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

public class GoTerm implements Serializable{

    private String name = "";
    private String id = null;
    private Vector geneNames = new Vector();
    int selected = 0;
    Vector alternateIds = new Vector();
    Vector<String> descendantIDs = new Vector<String>();
    Vector<String> selectedList = new Vector<String>();
    Vector<String> descendantSelectedList = new Vector<String>();
    private boolean enabled = true;
    /**
     *     Serializable fields.
     */
    private final static ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("name", String.class),
        new ObjectStreamField("id", String.class),
        new ObjectStreamField("selected", int.class),
        new ObjectStreamField("geneNames", Vector.class),
        new ObjectStreamField("alternateIds", Vector.class),
        new ObjectStreamField("descendantIDs", Vector.class),
        new ObjectStreamField("selectedList", Vector.class),
        new ObjectStreamField("descendantSelectedList", Vector.class),
        new ObjectStreamField("enabled", boolean.class)
    };


    /**
     * Default Constructor
     */
    public GoTerm() {
    }

    public void reset(){
        geneNames.clear();
        selected = 0;
        descendantIDs.clear();
        selectedList.clear();
        descendantSelectedList.clear();
    }

    public Vector getSelectedList(){
        return selectedList;
    }

    /**
     * Gets the number of enrties in category at this level
     * @return int
     */
    public int getSelected() {
        return selectedList.size();
    }

    /**
     *
     * @return int
     */
    public int getDescendantSelectedListCount(){
        return descendantSelectedList.size();
    }

    /**
     *
     * @return int
     */
    public Vector<String> getDescendantSelectedList(){
        return descendantSelectedList;
    }

    /**
     *
     * @param id String
     */
    public void addSelected(String id){
        String geneName = AnnotationParser.getGeneName(id.trim());
        if (geneName != null && !selectedList.contains(geneName.trim())){
            selectedList.add(geneName.trim());
            descendantSelectedList.add(geneName.trim());
        }
    }

    public void addDescendantSelectedList(Vector<String> descendants){
        for (String id: descendants){
            if (id!= null && !id.trim().equals("") && !selectedList.contains(id.trim()) && !descendantSelectedList.contains(id.trim()))
                descendantSelectedList.add(id.trim());
        }
    }

    /**
     * Gets the number of enrties in at this level and sub-levels categories
     * @return int
     */
    public int getRelatedNo() {
        return geneNames.size();
    }

    /**
     * Sets the name of this category
     * @param goterm String
     */
    public void setName(String goterm) {
        name = goterm;
    }

    /**
     * Sets the <code>ID</code> of this category. Ideally an
     * <a href="http://www.omg.org/cgi-bin/doc?lifesci/2003-12-02">LSID</href>
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Same GO Term could have multiple Ids
     * @param aid String
     */
    public void addAlternateId(String aid) {
        alternateIds.add(aid);
    }

    /**
     * Returns the alternate Ids for this GO Term
     * @return String[]
     */
    public String[] getAlternateIds() {
        String[] aids = new String[alternateIds.size()];
        System.arraycopy(alternateIds.toArray(), 0, aids, 0, alternateIds.size());
        return aids;
    }

    /**
     * Checks to see if alternate Ids exist
     * @return boolean
     */
    public boolean hasAlternateIds() {
        return!alternateIds.isEmpty();
    }

    /**
     * Gets the name of this category
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the <code>ID</code> of this category. Ideally an
     * <a href="http://www.omg.org/cgi-bin/doc?lifesci/2003-12-02">LSID</href>
     * @return String
     */
    public String getId() {
        return id;
    }

    public void addDescendantIDs(Vector<String> descendants){
        for (String ai : descendants) {
            if (!ai.trim().equals("") && !descendantIDs.contains(ai.trim())) {
                descendantIDs.add(ai.trim());
            }
        }
    }

    public Vector<String> getDescendants(){
        return descendantIDs;
    }

    public int getDescendantCount(){
        return descendantIDs.size() + geneNames.size();
    }

    /**
     * Sets gene symbol in this category from a <code>Vector<String></code>
     * of gene symbols
     * @param affyString String
     */
    public void addGeneNamesFromList(Vector<String> aids) {
        if (aids != null) {
            for (String aid : aids) {
                addGeneName(aid);
            }
        }
    }

    public void addGeneNameIfInList(Vector<String> aids, Vector refList){
        if (aids != null) {
            for (String aid : aids) {
                if (refList.contains(aid.trim())){
                    addGeneName(aid);
                }
            }
        }
    }

    /**
     * This method will add an gene symbol to the list if it's not in there already.
     * @param affyid String
     */
    public void addGeneName(String affyid) {
        if (affyid != null) {
            String geneName = AnnotationParser.getGeneName(affyid.trim());
            if (geneName != null && !geneNames.contains(geneName.trim()) && !geneName.trim().equalsIgnoreCase("")) {
                geneNames.add(geneName.trim());
                if (!descendantIDs.contains(geneName.trim()))
                    descendantIDs.add(geneName.trim());
            }
        }
    }

    /**
     * Clears the list of asscoiated ids so that toggle behaviour between reference user defined
     * lists and full chip probe sets are enabled
     */
    public void clear(){
        geneNames.clear();
    }

    /**
     * Gets the <code>String</code> representation of this category
     * @return String
     */
    public String toString() {
        return name + "(" + descendantSelectedList.size() + "/" + descendantIDs.size() + ")";
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean e){
        enabled = e;
    }

    /**
     * Gets the <code>GeneExpressionInfoImpl[]</code> representation of Affy Ids
     * contained in this category
     * @return GeneExpressionInfoImpl[]
     */
    public CSExpressionMarker[] getRelatedAffyGenes() {
        if (geneNames.size() > 0) {
            CSExpressionMarker[] result = new CSExpressionMarker[geneNames.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = new CSExpressionMarker();
                result[i].setLabel((String) geneNames.get(i));
                result[i].setDescription("");
            }
            return result;
        }
        return null;
    }

    /**
     * Gets the <code>GeneExpressionInfoImpl[]</code> representation of Affy Ids
     * contained in this category
     * @return GeneExpressionInfoImpl[]
     */
    public CSExpressionMarker[] getAllRelatedAffyGenes() {
        if (descendantIDs.size() > 0) {
            CSExpressionMarker[] result = new CSExpressionMarker[descendantIDs.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = new CSExpressionMarker();
                result[i].setLabel((String) descendantIDs.get(i));
                result[i].setDescription("");
            }
            return result;
        }
        return null;
    }

    /**
     * Gets the <code>GeneExpressionInfoImpl[]</code> representation of Affy Ids
     * contained in this category as encapsulated in the
     * <code>HashSet</code> parameter
     * @param hash HashSet stores a set of selected probesets, the return result will be only from this hash set.
     * @return GeneExpressionInfoImpl[]
     */
    public CSExpressionMarker[] getSelectedAffyGenes() {
        if (selectedList == null) {
            return null;
        }
        if (selectedList.size() > 0) {
            CSExpressionMarker[] result = new CSExpressionMarker[selectedList.size()];
            Object[] r = selectedList.toArray();
            for (int i = 0; i < result.length; i++) {
                result[i] = new CSExpressionMarker();
                result[i].setLabel((String) r[i]);
                result[i].setDescription("");
            }
            return result;
        }
        return null;
    }

    /**
     * Gets the <code>GeneExpressionInfoImpl[]</code> representation of Affy Ids
     * contained in all related categories as encapsulated in the
     * <code>HashSet</code> parameter
     * @param hash HashSet stores a set of selected probesets, the return result will be only from this hash set.
     * @return GeneExpressionInfoImpl[]
     */
    public CSExpressionMarker[] getAllSelectedAffyGenes() {
        if (descendantSelectedList == null) {
            return null;
        }
        if (descendantSelectedList.size() > 0) {
            CSExpressionMarker[] result = new CSExpressionMarker[descendantSelectedList.size()];
            Object[] r = descendantSelectedList.toArray();
            for (int i = 0; i < result.length; i++) {
                result[i] = new CSExpressionMarker();
                result[i].setLabel((String) r[i]);
                result[i].setDescription("");
            }
            return result;
        }
        return null;
    }

    /**
     * Gets associated <a href="http://ncicb.nci.nih.gov/core/caBIO">caBIO</href>
     * <code>GoOntology</code> entries for this category
     * @return GenericMarkerImpl[]
     */
    public CSGeneMarker[] getCabioGenes() {

        Gene[] genes = null;
        try {
            GoOntologySearchCriteria cs = new GoOntologySearchCriteria();
            cs.setName(name);
            SearchResult sr = cs.search();
            GoOntology[] goes = (GoOntology[]) sr.getResultSet();
            if (goes.length >= 1) {
                genes = goes[0].getGenes();
            }
        } catch (Exception ex1) {
            JOptionPane.showMessageDialog(null, "Can't access CabioServer.");
        }

        if ((genes != null) && (genes.length > 0)) {
            HashMap v = new HashMap();

            for (int i = 0; i < genes.length; i++) {
                CSGeneMarker jt = new CSGeneMarker();
                jt.getUnigene().setUnigeneId(genes[i].getClusterId().intValue());
                try {
                    ExpressionMeasurement[] exps = genes[i].getExpressionMeasurements();
                    if (exps.length > 0) {
                        jt.setLabel(exps[0].getName());
                    }
                } catch (OperationException ex2) {
                }
                String org = null;
                try {
                    org = genes[i].getOrganismAbbreviation();
                } catch (ManagerException ex) {
                    ex.printStackTrace();
                }
                jt.getUnigene().setOrganism(org);
                jt.setGeneId(Integer.parseInt(genes[i].getLocusLinkId()));
                jt.setDescription(genes[i].getName());
                v.put(genes[i].getLocusLinkId(), jt);
            }
            CSGeneMarker[] jt = new CSGeneMarker[v.size()];
            int count = 0;
            for (Iterator it = v.keySet().iterator(); it.hasNext(); ) {
                jt[count++] = (CSGeneMarker) v.get(it.next());
            }
            return jt;
        }
        return null;
    }
}
