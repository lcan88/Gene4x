package org.geworkbench.builtin.projects.comments;

import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.CommentsEvent;
import org.geworkbench.events.ProjectEvent;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author First Genetic Trust
 * @version 1.0
 */
@AcceptTypes({DSDataSet.class}) public class CommentsPanel implements VisualPlugin {
    /**
     * Used as the "name" part in the name-value pair that stores the user
     * comments in a <code>MicroarraySet</code> object X. In particular, if
     * the array returned by a call to
     * <code>X.getValuesForName(COMMENTS_ID_STRING)</code> is not null, then
     * the first entry in the array will contain the user comments associated
     * with the microarray set X.
     */
    private final String COMMENTS_ID_STRING = "User Comments";
    /**
     * Text to display when there are no user comments entered.
     */
    private final String DEFAULT_MESSAGE = "";
    /**
     * The currently selected microarray set.
     */
    protected DSDataSet dataSet = null;
    protected ImageIcon image = null;
    protected String userComments = DEFAULT_MESSAGE;
    private BorderLayout borderLayout1 = new BorderLayout();
    protected JScrollPane jScrollPane1 = new JScrollPane();
    protected JTextArea commentsTextArea = new JTextArea(userComments);
    protected JPanel commentsPanel = new JPanel();
    private boolean liveMode = true;

    public String getName() {
        return "Comments Pane";
    }

    public CommentsPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        commentsPanel.setLayout(borderLayout1);
        commentsTextArea.setText(userComments);
        commentsTextArea.setLineWrap(true);
        commentsTextArea.setWrapStyleWord(true);
        commentsTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                commentModified_actionPerformed(e);
            }

            public void removeUpdate(DocumentEvent e) {
                commentModified_actionPerformed(e);
            }

            public void changedUpdate(DocumentEvent e) {
            }

        });
        commentsPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(commentsTextArea, null);
        JButton loadCustomButton = new JButton("Load Custom Data Annotations...");
        JPanel loadPanel = new JPanel();
        loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.X_AXIS));
        loadPanel.add(loadCustomButton);
        commentsPanel.add(loadPanel, BorderLayout.SOUTH);
        loadCustomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCustomAnnotations();
            }
        });
    }

    private void loadCustomAnnotations() {
        JFileChooser fc = new JFileChooser(".");
        javax.swing.filechooser.FileFilter filter = new FileFilter() {

            public String getDescription() {
                return "Comma Separated Values Files";
            }

            public boolean accept(File f) {
                boolean returnVal = false;
                if (f.isDirectory() || f.getName().toLowerCase().endsWith(".csv")) {
                    return true;
                }
                return returnVal;
            }

        };
        fc.setFileFilter(filter);
        fc.setDialogTitle("Open Marker Set");
        int choice = fc.showOpenDialog(commentsPanel.getParent());
        if (choice == JFileChooser.APPROVE_OPTION) {
            // Load custom annotations in annotation parser
            AnnotationParser.parseCustomAnnotations(fc.getSelectedFile(), dataSet);
        }
    }

    public Component getComponent() {
        return commentsPanel;
    }

    private void commentModified_actionPerformed(DocumentEvent e) {
        if (e == null || e.getDocument() != commentsTextArea.getDocument()) {
            return;
        }
        // No action required if nothing changed.
        if (commentsTextArea.getText().trim().equals(userComments.trim())) {
            return;
        }
        userComments = commentsTextArea.getText();
        if (liveMode) {
            publishCommentsEvent(new CommentsEvent(userComments));
        }
//        if (dataSet != null) {
//            dataSet.clearName(COMMENTS_ID_STRING);
//            dataSet.addNameValuePair(COMMENTS_ID_STRING, userComments);
//            publishCommentsEvent(new org.geworkbench.events.CommentsEventOld(dataSet, userComments));
//        } else if (image != null) {
//            image.setDescription(userComments);
//        }
    }

    @Publish public CommentsEvent publishCommentsEvent(CommentsEvent event) {
        return event;
    }

    @Subscribe public void receive(CommentsEvent event, Object source) {
        liveMode = false;
        commentsTextArea.setText(event.getText());
        commentsTextArea.setCaretPosition(0);
        liveMode = true;
    }

    @Subscribe public void receive(ProjectEvent event, Object source) {
        dataSet = event.getDataSet();
    }

    /**
     * Application listener for receiving events that modify the currently
     * selected microarray set.
     *
     * @param e
     */
//    @Subscribe
//    public void receive(ProjectEvent e, Object source) {
//        DSDataSet dataSet = e.getDataSet();
//        if (e.getMessage().equals(ProjectEvent.CLEARED)) {
//            this.dataSet = null;
//            userComments = DEFAULT_MESSAGE;
//        } else if (dataSet != this.dataSet) {
//            this.dataSet = dataSet;
//            image = null;
//            userComments = DEFAULT_MESSAGE;
//            Object[] values = this.dataSet.getValuesForName(COMMENTS_ID_STRING);
//            if (values != null && values.length > 0) {
//                userComments = (String) values[0];
//                if (userComments.trim().equals(""))
//                    userComments = DEFAULT_MESSAGE;
//            }
//        }
//        commentsTextArea.setText(userComments);
//        commentsTextArea.setCaretPosition(0);   // For long text.
//    }
//
//    @Subscribe
//    public void receive(ImageSnapshotEvent event, Object source) {
//        image = event.getImage();
//        dataSet = null;
//        commentsTextArea.setText(image.getDescription());
//    }
}
