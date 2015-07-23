package org.geworkbench.bison.datastructure.biocollections;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSSequentialItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.util.HashVector;

import java.util.List;
import java.util.Vector;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
public class CSMarkerVector extends CSSequentialItemList<DSGeneMarker> implements
        DSItemList<DSGeneMarker> {

    // watkin -- changed these hashvectors to not enforce uniqueness. They were slow to the point of unusability.
    // Uniqueness is not a requirement of these data structures anyways.
    HashVector<Integer,
            DSGeneMarker> geneIdMap = new HashVector<Integer, DSGeneMarker>(false);
    HashVector<String,
            DSGeneMarker> geneNameMap = new HashVector<String, DSGeneMarker>(false);
    boolean mapGeneNames = true;

    public CSMarkerVector() {
    }

    public CSMarkerVector(List<DSGeneMarker> markerList) {
        super();
        this.addAll(markerList);
    }

    public DSGeneMarker getMarkerByUniqueIdentifier(String label) {
        return super.get(label);
    }

    /**
     *
     */
    //There is a bug related to CSMarkerVector,  similar to the bug listed above.
    //geneNameMap and geneIDMap in CSMarkerVector always are empty (size =1 or 0)
    //add a new correctMaps() method in CSMarkerVector to temp fix the probelm.
    //geneIdMap still is empty.

    public void correctMaps() {
        geneIdMap.clear();
        geneNameMap.clear();
        for (DSGeneMarker item : this) {
            Integer geneId = new Integer(item.getGeneId());
            if (geneId != null && geneId.intValue() != -1) {
                geneIdMap.addItem(geneId, item);
            }

            if (mapGeneNames) {
                String geneName = item.getGeneName();
                String label = item.getLabel();
                if (geneName != null && (!"---".equals(geneName))) {
                    if (label != null && geneName.equals("")) {
                        geneNameMap.addItem(label, item);
                    } else {
                        geneNameMap.addItem(geneName, item);
                    }
                }
            }

        }
    }

    public Vector<DSGeneMarker> getMatchingMarkers(String aString) {
        Vector<DSGeneMarker> matchingMarkers = new Vector<DSGeneMarker>();
        DSGeneMarker uniqueKeyMarker = super.get(aString);
        if (uniqueKeyMarker != null) {
            matchingMarkers.add(uniqueKeyMarker);
        }
        try {
            Vector<DSGeneMarker> markersSet;
            if (mapGeneNames) {
                markersSet = geneNameMap.get(aString);
                int size = geneNameMap.size();
                if (markersSet != null && markersSet.size() > 0) {
                    for (DSGeneMarker marker : markersSet) {
                        if (!matchingMarkers.contains(marker)) {
                            matchingMarkers.add(marker);
                        }
                    }
                }

                Integer geneId = Integer.parseInt(aString);
                markersSet = geneIdMap.get(geneId);
                if (markersSet != null && markersSet.size() > 0) {
                    for (DSGeneMarker marker : markersSet) {
                        if (!matchingMarkers.contains(marker)) {
                            matchingMarkers.add(marker);
                        }
                    }
                }

            }
        } catch (Exception e) {

        }
        return matchingMarkers;
    }

    public Vector<DSGeneMarker> getMatchingMarkers(int geneId) {
        return getMatchingMarkers(new Integer(geneId));
    }


    public Vector<DSGeneMarker> getMatchingMarkers(Integer geneId) {
        if (geneId.intValue() == -1) {
            return null;
        }

        return geneIdMap.get(geneId);
    }

    public DSGeneMarker get(String aString) {
        if (aString == null) {
            return null;
        }

        DSGeneMarker marker = super.get(aString);
        if (marker == null) {
            Vector<DSGeneMarker> matchingMarkers = getMatchingMarkers(aString);
            if (matchingMarkers != null && matchingMarkers.size() > 0) {
                marker = matchingMarkers.get(0);
            }
        }
        return marker;
    }

    public boolean add(DSGeneMarker item) {
        boolean result = false;
        if (item != null) {
            if (!this.contains(item)) {
                result = super.add(item);
                if (result) {
                    Integer geneId = new Integer(item.getGeneId());
                    if (geneId != null && geneId.intValue() != -1) {
                        geneIdMap.addItem(geneId, item);
                    }

                    if (mapGeneNames) {
                        String geneName = item.getGeneName();
                        String label = item.getLabel();
                        if (geneName != null && (!"---".equals(geneName))) {
                            geneNameMap.addItem(geneName, item);

                        }

                    }
                }
            }
        }
        return result;
    }

    public void add(int i, DSGeneMarker item) {
        super.add(i, item);
        Integer geneId = new Integer(item.getGeneId());
        if (geneId != null && geneId.intValue() != -1) {
            geneIdMap.addItem(geneId, item);
        }

        if (mapGeneNames) {
            String geneName = item.getGeneName();
            if (geneName != null && (!"---".equals(geneName))) {
                geneNameMap.addItem(geneName, item);
            }
        }

    }

    public DSGeneMarker get(DSGeneMarker item) {
        DSGeneMarker marker = super.get(item);
        if (marker == null) {
            Vector<DSGeneMarker> matchingMarkers = getMatchingMarkers(item);
            if (matchingMarkers != null && matchingMarkers.size() > 0) {
                marker = matchingMarkers.get(0);
            }
        }

        //        if(marker == null){
        //            System.out.println("null marker");
        //        }
        return marker;
    }

    public Vector<DSGeneMarker> getMatchingMarkers(DSGeneMarker item) {
        if (mapGeneNames && (item.getGeneName() != null) &&
            (item.getGeneName().length() > 0)) {
            return geneNameMap.get(item.getGeneName());
        } else {
            return geneIdMap.get(new Integer(item.getGeneId()));
        }
    }

    public boolean contains(Object item) {
        if (item instanceof DSGeneMarker) {
            return this.contains((DSGeneMarker) item);
        } else {
            return false;
        }
    }

    public boolean contains(DSGeneMarker item) {
        // Contains must use a strict sense of equality!
        DSGeneMarker marker = super.get(item);
        if (marker != null) {
            return true;
        } else {
            return false;
        }
    }

    //To change
    public boolean remove(Object item) {
        boolean removed = super.remove(item);
        if (removed) {
            if (item instanceof DSGeneMarker) {
                geneIdMap.remove(new Integer(((DSGeneMarker)item).getGeneId()));
            }
            return true;
        } else {
            return false;
        }
    }

    public DSGeneMarker remove(int index) {
        DSGeneMarker marker = super.remove(index);
        geneIdMap.remove(new Integer(marker.getGeneId()));
        return marker;
    }

    public void clear() {
        super.clear();
        geneIdMap.clear();
    }

    public boolean setLabel(int index, String label) {
        DSGeneMarker item = get(index);
        if (item != null) {
            String oldLabel = item.getLabel();
            if (oldLabel != null) {
                objectMap.remove(oldLabel);
            }
            item.setLabel(label);
            objectMap.put(label, item);
            return true;
        } else {
            return false;
        }
    }
}
