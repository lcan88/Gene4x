package org.geworkbench.builtin.projects.experimentinformation;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ProjectEvent;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 * @author First Genetic Trust
 * @version 1.0
 */

/**
 * This application component is responsible for displaying the exepriment
 * information (if any) associated with a microarray set.
 */
@AcceptTypes({DSDataSet.class}) public class ExperimentInformationPanel implements VisualPlugin {

    /**
     * Text to display when there are no user comments entered.
     */
    private final String DEFAULT_MESSAGE = "";

    /**
     * The currently selected microarray set.
     */
    protected DSMicroarraySet maSet = null;

    protected String experimentInfo = DEFAULT_MESSAGE;

    private BorderLayout borderLayout1 = new BorderLayout();

    protected JScrollPane jScrollPane1 = new JScrollPane();

    protected JTextArea experimentTextArea = new JTextArea(experimentInfo);

    protected JPanel experimentPanel = new JPanel();

    public String getName() {
        return "Experiment Info";
    }

    public ExperimentInformationPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        experimentPanel.setLayout(borderLayout1);
        experimentTextArea.setText(experimentInfo);
        experimentTextArea.setLineWrap(true);
        experimentTextArea.setWrapStyleWord(true);
        experimentTextArea.setEditable(false);
        experimentPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(experimentTextArea, null);
    }

    public Component getComponent() {
        return experimentPanel;
    }

    /**
     * Application listener for receiving events that modify the currently
     * selected microarray set.
     *
     * @param e
     */
    @Subscribe public void receive(ProjectEvent e, Object source) {
        DSDataSet dataSet = e.getDataSet();
        if (e != null && dataSet instanceof DSMicroarraySet) {
            experimentInfo = DEFAULT_MESSAGE;
            if (e.getMessage().equals(ProjectEvent.CLEARED))
                maSet = null;
            else if (dataSet != null) {
                maSet = (DSMicroarraySet) dataSet;
                String[] descriptions = maSet.getDescriptions();
                if (descriptions != null && descriptions.length > 0)
                    experimentInfo = "";
                for (int i = 0; i < descriptions.length; i++)
                    experimentInfo += descriptions[i] + "\n";
                if (experimentInfo == null || experimentInfo.trim().equals(""))
                    experimentInfo = DEFAULT_MESSAGE;
            }
            experimentTextArea.setText(experimentInfo);
            experimentTextArea.setCaretPosition(0); // For long text.
        }
    }
}
