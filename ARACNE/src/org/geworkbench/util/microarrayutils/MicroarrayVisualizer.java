package org.geworkbench.util.microarrayutils;

import org.geworkbench.bison.annotation.DSCriteria;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.classification.phenotype.CSClassCriteria;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.builtin.projects.ProjectSelection;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ImageSnapshotEvent;
import org.geworkbench.events.MarkerSelectedEvent;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.util.associationdiscovery.cluster.CSMatchedMatrixPattern;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public abstract class MicroarrayVisualizer {
    protected DSMicroarraySetView<DSGeneMarker, DSMicroarray> dataSetView = new CSMicroarraySetView<DSGeneMarker, DSMicroarray>();
    //    protected DSMicroarraySet mArraySet = null;
    //    protected DSItemList<DSMicroarray> mArrayVector = null;
    //    protected DSItemList<DSMarker> markerVector = null;
    //    protected boolean showAllMArrays = true;
    //    protected boolean showAllMarkers = true;
    protected int microarrayId = 0;
    protected int markerId = 0;
    protected JPanel mainPanel = new JPanel();
    protected BorderLayout borderLayout = new BorderLayout();
    protected boolean usePanel = false;
    protected DSCriteria<DSBioObject> phCriteria = null;
    protected CSClassCriteria classCriteria = null;
    protected DSPanel<DSGeneMarker> markerPanel;
    protected DSPanel<DSBioObject> mArrayPanel;
    protected DSItemList<DSGeneMarker> uniqueMarkers = null;

    public MicroarrayVisualizer() {
        try {
            jbInit();
        } catch (Exception ex) {
        }
    }

    @Publish public org.geworkbench.events.SubpanelChangedEvent publishSubpanelChangedEvent(org.geworkbench.events.SubpanelChangedEvent event) {
        return event;
    }

    @Publish public org.geworkbench.events.AdjacencyMatrixEvent publishAdjacencyMatrixEvent(org.geworkbench.events.AdjacencyMatrixEvent event) {
        return event;
    }

    @Publish public ImageSnapshotEvent publishImageSnapshotEvent(ImageSnapshotEvent event) {
        return event;
    }

    @Publish public MarkerSelectedEvent publishMarkerSelectedEvent(MarkerSelectedEvent event) {
        return event;
    }

    private void jbInit() throws Exception {
        mainPanel.setLayout(borderLayout);
    }

    public DSItemList<DSGeneMarker> getUniqueMarkers() {
        return uniqueMarkers;
    }

    public final void changeMicroArraySet(DSMicroarraySet<DSMicroarray> maSet) {
        dataSetView.setMicroarraySet(maSet);
        reset();
        setMicroarraySet(maSet);
    }

    /**
     * Just a dummy implementation rather than an abstract method so that JBuilder does not show
     * a red component.
     *
     * @param microarraySet
     */
    protected void setMicroarraySet(DSMicroarraySet set) {

    }

    /**
     * Just a dummy implementation rather than an abstract method so that JBuilder does not show
     * a red component.
     */
    protected void reset() {
        uniqueMarkers = dataSetView.getUniqueMarkers();
    }

    public void notifyPatternSelection(CSMatchedMatrixPattern[] selectedPatterns) {
        clearPatterns();
        for (int i = 0; i < selectedPatterns.length; i++) {
            addPattern(selectedPatterns[i]);
        }
        //repaint();
    }

    /**
     * Just a dummy implementation rather than an abstract method so that JBuilder does not show
     * a red component.
     */
    protected void clearPatterns() {
    }

    /**
     * Just a dummy implementation rather than an abstract method so that JBuilder does not show
     * a red component.
     *
     * @param pattern
     */
    protected void addPattern(CSMatchedMatrixPattern pattern) {
    }

    public void showAllMArrays(boolean showAll) {
        dataSetView.useItemPanel(!showAll);
        reset();
        repaint();
    }

    public void showAllMarkers(boolean showAll) {
        dataSetView.useMarkerPanel(!showAll);
        uniqueMarkers = dataSetView.getUniqueMarkers();
        repaint();
    }

    @Subscribe public void receive(org.geworkbench.events.ProjectEvent projectEvent, Object source) {
        // System.out.println("receiveProjectSelection() in microarrayvisualizer");
        if (projectEvent.getMessage().equals(ProjectEvent.CLEARED)) {
            changeMicroArraySet(null);
            repaint();
            return;
        }
            ProjectSelection selection = ((ProjectPanel) source).getSelection();
            DSDataSet dataFile = selection.getDataSet();
            if (dataFile != null && dataFile instanceof DSMicroarraySet) {
                DSMicroarraySet set = (DSMicroarraySet) dataFile;
                changeMicroArraySet(set);
            } else {
                changeMicroArraySet(null);
            }
            repaint();
    }

    public void repaint() {
        mainPanel.repaint();
    }

    public Component getComponent() {
        return mainPanel;
    }

    public DSMicroarraySetView<DSGeneMarker, DSMicroarray> getDataSetView() {
        return dataSetView;
    }

    public DSMicroarraySet<? extends DSMicroarray> getDataSet() {
        return dataSetView.getMicroarraySet();
    }
}
