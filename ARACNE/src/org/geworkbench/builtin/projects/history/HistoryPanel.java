package org.geworkbench.builtin.projects.history;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.builtin.projects.ProjectPanel;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Subscribe;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * This application component is responsible for displaying the history of
 * modifications (editing, normalization, filtering, etc) that a microarray
 * set has undergone.
 */
@AcceptTypes({DSDataSet.class}) public class HistoryPanel implements VisualPlugin {
    /**
     * Text to display when there are no user comments entered.
     */
    private final String DEFAULT_MESSAGE = "";
    /**
     * The currently selected microarray set.
     */
    protected DSMicroarraySet maSet = null;
    protected String datasetHistory = DEFAULT_MESSAGE;
    private BorderLayout borderLayout1 = new BorderLayout();
    protected JScrollPane jScrollPane1 = new JScrollPane();
    protected JTextArea historyTextArea = new JTextArea(datasetHistory);
    protected JPanel historyPanel = new JPanel();

    public String getName() {
        return "History Pane";
    }

    public HistoryPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        historyPanel.setLayout(borderLayout1);
        historyTextArea.setText(datasetHistory);
        historyTextArea.setLineWrap(true);
        historyTextArea.setWrapStyleWord(true);
        historyTextArea.setEditable(false);
        historyPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(historyTextArea, null);
    }

    public Component getComponent() {
        return historyPanel;
    }

    /**
     * Application listener for receiving events that modify the currently
     * selected microarray set.
     *
     * @param e
     */
    @Subscribe public void receive(org.geworkbench.events.ProjectEvent e, Object source) {
        DSDataSet dataSet = e.getDataSet();
        if (e != null && dataSet instanceof DSMicroarraySet) {
            datasetHistory = DEFAULT_MESSAGE;
            if (e.getMessage().equals(org.geworkbench.events.ProjectEvent.CLEARED))
                maSet = null;
            else if (dataSet != null) {
                maSet = (DSMicroarraySet) dataSet;
                Object[] values = maSet.getValuesForName(ProjectPanel.HISTORY);
                if (values != null && values.length > 0) {
                    datasetHistory = (String) values[0];
                    if (datasetHistory.trim().equals(""))
                        datasetHistory = DEFAULT_MESSAGE;
                }
            }
            historyTextArea.setText(datasetHistory);
            historyTextArea.setCaretPosition(0); // For long text.
        }
    }
}

