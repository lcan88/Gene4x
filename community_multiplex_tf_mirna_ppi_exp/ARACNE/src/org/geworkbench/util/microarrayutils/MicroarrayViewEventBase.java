package org.geworkbench.util.microarrayutils;

/*
 * The geworkbench project
 *
 * Copyright (c) 2006 Columbia University
 *
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.bison.datastructure.biocollections.views.DSMicroarraySetView;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.events.SubpanelChangedEvent;

/**
 * @author unattributable
 * @see VisualPlugin
 */
public abstract class MicroarrayViewEventBase implements VisualPlugin {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * The reference microarray set.
	 */
	protected DSMicroarraySet<DSMicroarray> refMASet = null;
	protected DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView = null;
	protected boolean onlyActivatedMarkers = false;
	protected boolean onlyActivatedArrays = false;
	protected JCheckBox chkAllMarkers = new JCheckBox("All Markers", true);
	protected JCheckBox chkAllArrays = new JCheckBox("All Arrays", true);
	protected JButton plotButton = new JButton("Plot");
	private final String markerLabelPrefix = "  Markers: ";
	protected JLabel numMarkersSelectedLabel = new JLabel(markerLabelPrefix);
	protected JPanel mainPanel;
	protected JToolBar jToolBar3;
	protected DSPanel<DSGeneMarker> markers = null;
	protected DSPanel<DSGeneMarker> activatedMarkers = null;
	protected DSItemList<? extends DSGeneMarker> uniqueMarkers = null;
	protected DSPanel activatedArrays = null;
	private DefaultListModel ls2 = new DefaultListModel();
	private boolean isPlotRefresh = false;

	/**
	 *
	 *
	 */
	public MicroarrayViewEventBase() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * getComponent
	 *
	 * @return Component
	 */
	public Component getComponent() {
		return mainPanel;
	}

	/**
	 * @param event
	 * @return SubpanelChangedEvent
	 */
	@Publish
	public SubpanelChangedEvent publishSubpanelChangedEvent(
			org.geworkbench.events.SubpanelChangedEvent event) {
		return event;
	}

	/**
	 * receiveProjectSelection
	 *
	 * @param e
	 *            ProjectEvent
	 */
	@Subscribe
	@SuppressWarnings("unchecked")
	public void receive(org.geworkbench.events.ProjectEvent e, Object source) {

		log.debug("Source object " + source);

		if (e.getMessage().equals(org.geworkbench.events.ProjectEvent.CLEARED)) {
			refMASet = null;
			fireModelChangedEvent(null);
		} else {
			DSDataSet dataSet = e.getDataSet();
			if (dataSet instanceof DSMicroarraySet) {
				if (refMASet != dataSet) {
					this.refMASet = (DSMicroarraySet) dataSet;
					// panels are now invalid
					activatedArrays = null;
					activatedMarkers = null;
					uniqueMarkers = null;
				}
			}
			refreshMaSetView();
		}
	}

	/**
	 * geneSelectorAction
	 *
	 * @param e
	 *            GeneSelectorEvent
	 */
	@Subscribe
	@SuppressWarnings("unchecked")
	public void receive(GeneSelectorEvent e, Object source) {

		log.debug("Source object " + source);

		markers = e.getPanel();
		activatedMarkers = new CSPanel();
		if (markers != null && markers.size() > 0) {

            ls2.clear();
			for (int j = 0; j < markers.panels().size(); j++) {
				DSPanel<DSGeneMarker> mrk = markers.panels().get(j);
				if (mrk.isActive()) {
					for (int i = 0; i < mrk.size(); i++) {
						if (!ls2.contains(mrk.get(i))) {
							ls2.addElement(mrk.get(i));
						}
						activatedMarkers.add(mrk.get(i));

					}

				}
			}
			markers = activatedMarkers;

			numMarkersSelectedLabel.setText(markerLabelPrefix
					+ activatedMarkers.size());

		}

		else
			numMarkersSelectedLabel.setText(markerLabelPrefix);

		refreshMaSetView();
	}

	/**
	 * phenotypeSelectorAction
	 *
	 * @param e
	 *            PhenotypeSelectorEvent
	 */
	@Subscribe
	@SuppressWarnings("unchecked")
	public void receive(org.geworkbench.events.PhenotypeSelectorEvent e,
			Object source) {

		log.debug("Source object " + source);

		if (e.getTaggedItemSetTree() != null)

			activatedArrays = e.getTaggedItemSetTree().activeSubset();

		refreshMaSetView();

	}

	/**
	 * Refreshes the chart view.
	 */
	@SuppressWarnings("unchecked")
	protected void refreshMaSetView() {
		maSetView = getDataSetView();
		uniqueMarkers = maSetView.getUniqueMarkers();

		fireModelChangedEvent(null);
	}

	/**
	 * @param event
	 */
	protected void fireModelChangedEvent(MicroarraySetViewEvent event) {

	}

	/**
	 * @throws Exception
	 */
	protected void jbInit() throws Exception {
		mainPanel = new JPanel();

		jToolBar3 = new JToolBar();
		chkAllMarkers.setToolTipText("");

		BorderLayout borderLayout2 = new BorderLayout();
		mainPanel.setLayout(borderLayout2);

		chkAllMarkers
				.addActionListener(new MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter(
						this));
		chkAllArrays
				.addActionListener(new MicroarrayViewPanelBase_chkShowArrays_actionAdapter(
						this));

		jToolBar3.add(chkAllArrays, null);
		jToolBar3.add(chkAllMarkers, null);

		mainPanel.add(jToolBar3, java.awt.BorderLayout.SOUTH);

		onlyActivatedMarkers = !chkAllMarkers.isSelected();
		onlyActivatedArrays = !chkAllArrays.isSelected();
	}

	/**
	 * @param e
	 */
	void chkShowArrays_actionPerformed(ActionEvent e) {
		onlyActivatedArrays = !((JCheckBox) e.getSource()).isSelected();

		refreshMaSetView();
	}

	/**
	 * @param e
	 */
	void chkActivateMarkers_actionPerformed(ActionEvent e) {
		onlyActivatedMarkers = !((JCheckBox) e.getSource()).isSelected();

		refreshMaSetView();
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DSMicroarraySetView getDataSetView() {
		DSMicroarraySetView dataSetView = new CSMicroarraySetView(this.refMASet);
		if (activatedMarkers != null && activatedMarkers.panels().size() > 0)
			dataSetView.setMarkerPanel(activatedMarkers);
		if (activatedArrays != null && activatedArrays.panels().size() > 0 && activatedArrays.size() > 0)
			dataSetView.setItemPanel(activatedArrays);
		dataSetView.useMarkerPanel(onlyActivatedMarkers);
		dataSetView.useItemPanel(onlyActivatedArrays);

		return dataSetView;
	}

	/**
	 *
	 * @return
	 */
	public boolean isPlotRefresh() {
		return isPlotRefresh;
	}

	/**
	 *
	 * @param isPlotRefresh
	 */
	public void setPlotRefresh(boolean isPlotRefresh) {
		this.isPlotRefresh = isPlotRefresh;
	}

    /**
     * @return
     */
    public DSMicroarraySetView<DSGeneMarker, DSMicroarray> getMaSetView() {
        return maSetView;
    }

    /**
     * @param maSetView
     */
    public void setMaSetView( DSMicroarraySetView<DSGeneMarker, DSMicroarray> maSetView ) {
        this.maSetView = maSetView;
    }

}

/**
 * @author unattributable
 */
class MicroarrayViewPanelBase_chkShowArrays_actionAdapter implements
		java.awt.event.ActionListener {

	private Log log = LogFactory.getLog(this.getClass());

	MicroarrayViewEventBase adaptee;

	MicroarrayViewPanelBase_chkShowArrays_actionAdapter(
			MicroarrayViewEventBase adaptee) {
		this.adaptee = adaptee;
	}

	/**
	 *
	 */
	public void actionPerformed(ActionEvent e) {
		log.debug("ActionEvent " + e);
		adaptee.chkShowArrays_actionPerformed(e);
		adaptee.getComponent().repaint();
	}
}

/**
 * @author unattributable
 */
class MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter implements
		java.awt.event.ActionListener {

	private Log log = LogFactory.getLog(this.getClass());

	MicroarrayViewEventBase adaptee;

	MicroarrayViewPanelBase_chkActivateMarkers_actionAdapter(
			MicroarrayViewEventBase adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		log.debug("actionPerformed " + e);

		adaptee.chkActivateMarkers_actionPerformed(e);
		adaptee.getComponent().repaint();
	}
}