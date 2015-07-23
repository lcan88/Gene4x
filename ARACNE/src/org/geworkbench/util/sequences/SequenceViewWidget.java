package org.geworkbench.util.sequences;

import org.geworkbench.bison.datastructure.biocollections.Collection;
import org.geworkbench.bison.datastructure.biocollections.DSCollection;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        CSSequenceSet;
import org.geworkbench.bison.datastructure.biocollections.sequences.
        DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.sequence.DSSequence;
import org.geworkbench.bison.datastructure.complex.pattern.DSMatchedPattern;
import org.geworkbench.bison.datastructure.complex.pattern.DSPatternMatch;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSMatchedSeqPattern;
import org.geworkbench.bison.datastructure.complex.pattern.sequence.
        DSSeqRegistration;
import org.geworkbench.events.SequenceDiscoveryTableEvent;
import org.geworkbench.util.PropertiesMonitor;
import org.geworkbench.util.patterns.CSMatchedSeqPattern;
import org.geworkbench.util.patterns.PatternOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 * <p>Widget provides all GUI services for sequence panel displays.</p>
 * <p>Widget is controlled by its associated component, SequenceViewAppComponent</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Califano Lab</p>
 * @author
 * @version 1.0
 */

public class SequenceViewWidget extends JPanel {
    private HashMap listeners = new HashMap();
    private ActionListener listener = null;
    private final int xOff = 60;
    private final int yOff = 20;
    private final int xStep = 5;
    private final int yStep = 12;
    private int prevSeqId = 0;
    private int prevSeqDx = 0;
    private DSSequenceSet sequenceDB = new CSSequenceSet();
    private DSSequenceSet displaySequenceDB = new CSSequenceSet();
    //Layouts
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private BorderLayout borderLayout1 = new BorderLayout();
    private BorderLayout borderLayout2 = new BorderLayout();
    private BorderLayout borderLayout3 = new BorderLayout();
    //Panels and Panes
    private JPanel jPanel1 = new JPanel();
    private JScrollPane seqScrollPane = new JScrollPane();

    private SequenceViewWidgetPanel seqViewWPanel = new SequenceViewWidgetPanel();
    //Models
    //private ArrayList         selectedPatterns  = new ArrayList();
    private DSCollection<DSMatchedPattern<DSSequence,
            DSSeqRegistration>>
            selectedPatterns = new Collection<DSMatchedPattern<DSSequence,
                               DSSeqRegistration>>();
    private PropertiesMonitor propertiesMonitor = null; //debug
    private JToolBar jToolBar1 = new JToolBar();
    private JToggleButton showAllBtn = new JToggleButton();

    public SequenceViewWidget() {
        try {
            jbInit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {

        propertiesMonitor = PropertiesMonitor.getPropertiesMonitor();

        this.setLayout(borderLayout2);
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setMinimumSize(new Dimension(14, 25));
        jPanel1.setPreferredSize(new Dimension(14, 25));

        seqScrollPane.setBorder(BorderFactory.createEtchedBorder());
        seqViewWPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jDisplayPanel_mouseClicked(e);
            }
        });
        this.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(InputMethodEvent e) {
            }

            public void caretPositionChanged(InputMethodEvent e) {
                this_caretPositionChanged(e);
            }
        }); this.addPropertyChangeListener(new java.beans.
                                           PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                this_propertyChange(e);
            }
        });
        showAllBtn.setToolTipText(
                "Push down to show sequences with selected patterns.");
        showAllBtn.setText("All / Matching Pattern");

        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllBtn_actionPerformed(e);
            }
        });

        this.add(jPanel1, BorderLayout.SOUTH);
        this.add(seqScrollPane, BorderLayout.CENTER);
        this.add(jToolBar1, BorderLayout.NORTH);
        jToolBar1.add(showAllBtn, null);
        if (sequenceDB != null) {
            seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
        }
        seqViewWPanel.initialize(selectedPatterns, sequenceDB);
        seqScrollPane.getViewport().add(seqViewWPanel, null);
        seqViewWPanel.setShowAll(showAllBtn.isSelected());
    }

    //sets all required session objects such as patternTable, sequenceDB, and model
    public void patternSelectionHasChanged(SequenceDiscoveryTableEvent e) {

//    setPatterns(e.getPatterns());
//    seqViewWPanel.initialize(selectedPatterns, e.getSequenceDB());

        setPatterns(e.getPatternMatchCollection());

        // CSSequenceSet sdb = e.getSequenceDB();

        if (sequenceDB != null) {
            seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());

            if (showAllBtn.isSelected()) {
                DSSequenceSet db = sequenceDB.createSubSetSequenceDB(
                        createTempDBIndex());
                int indexArray[] = sequenceDB.getMatchIndex();

                db.setMatchIndex(sequenceDB.getMatchIndex());

                seqViewWPanel.initialize(selectedPatterns, db);
            } else {
                seqViewWPanel.initialize(selectedPatterns, sequenceDB);
            }
            showPatterns();
        }
    }


    /**
     * Create a boolean list to define which sequences will be included in the new DataSet.
     * @return boolean[]
     */

    public boolean[] createTempDBIndex() {
        int size = sequenceDB.getSequenceNo();
        boolean[] included = new boolean[size];
        for (int i = 0; i < size; i++) {
            included[i] = false;
        }
        if (selectedPatterns != null) {

            for (int row = 0; row < selectedPatterns.size(); row++) {
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                              selectedPatterns.get(row);
                // Pattern pattern = (Pattern) selectedPatterns.get(row);
                if (pattern != null) {
                    //   if (pattern.getClass().isAssignableFrom(PatternImpl.class)) {
                    if (pattern instanceof CSMatchedSeqPattern) {

                        if (pattern != null) {
                            for (int locusId = 0;
                                               locusId < pattern.getSupport();
                                               locusId++) {

                                int seqId = ((CSMatchedSeqPattern) pattern).
                                            getId(locusId);
                                if (seqId < size) {
                                    included[seqId] = true;
                                } else {

                                    System.out.println(
                                            "Check at SVWidget: mismatch");
                                    return included;

                                }
                            }
                        }
                    }
                }
            }
        }
        return included;
    }


    /**
     * Update the detail of sequence.
     * @param e MouseEvent
     */
    //From Xiaoqing's observation, it looks like that below method only works for PatternImpl class.

    void jDisplayPanel_mouseClicked(MouseEvent e) {

        final Font font = new Font("Courier", Font.BOLD, 10);
        // Get the mouse position
        int x = e.getX();
        int y = e.getY();
        // get the position of the sequence and the offset, relative to the sequence start from
        // the mouse position
        int seqId = getSeqId(y);
        int seqDx = getSeqDx(x);
        // Check if we are clicking on a new sequence
        if ((seqId != prevSeqId) || (seqDx != prevSeqDx)) {
            Graphics g = jPanel1.getGraphics();
            g.clearRect(0, 0, jPanel1.getWidth(), jPanel1.getHeight());

            //replace all sequenceDB with currentSequenceDB for the all/Matching Pattern function.
            if ((seqId >= 0) && (seqId < displaySequenceDB.getSequenceNo())) {

                int newIndex[] = displaySequenceDB.getMatchIndex();

                g.setFont(font);
                DSSequence sequence = displaySequenceDB.getSequence(seqId);
                if (sequence != null) {
                    if ((seqDx >= 0) && (seqDx < sequence.length())) {
                        //turn anti alising on
                        ((Graphics2D) g).setRenderingHint(RenderingHints.
                                KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        //shift the selected pattern/sequence into middle of the panel.
                        int startPoint = 0;
                        if (seqDx > 50) {
                            startPoint = seqDx - 50;
                        }
                        String seqAscii = sequence.getSequence().substring(
                                startPoint);
                        g.drawString(seqAscii, 10, 20);
                        FontMetrics fm = g.getFontMetrics(font);
                        Rectangle2D r = fm.getStringBounds(seqAscii, g);
                        for (DSMatchedPattern<DSSequence,
                             DSSeqRegistration> pattern : selectedPatterns) {

                            //Match the old seqID with reverseIndex, xq.
                            int reverseIndex[] = displaySequenceDB.
                                                 getReverseIndex();

                            int reverseSeqId = -1;
                            if (showAllBtn.isSelected()) {

                                reverseSeqId = (reverseIndex[seqId] != -1) ?
                                               reverseIndex[seqId] : seqId;
                            } else {
                                reverseSeqId = seqId;
                            }

                            if (pattern != null) {
                                int id = 0;
                                while ((id < pattern.getSupport()) &&
                                       (pattern.get(id).getObject().getSerial() <
                                        reverseSeqId)) {
                                    id++;
                                }

                                while ((id < pattern.getSupport()) &&
                                       (pattern.get(id).getObject().getSerial() ==
                                        reverseSeqId)) {

                                    double scale = (r.getWidth() + 3) /
                                            (double) (seqAscii.length());
                                    DSPatternMatch<DSSequence,
                                            ? extends DSSeqRegistration>
                                            match = pattern.get(id);
                                    Object registration = match.getRegistration();
                                    if (registration instanceof
                                        DSSeqRegistration) {
                                        DSSeqRegistration seqReg = (
                                                DSSeqRegistration) registration;
                                        int dx = seqReg.x1;
                                        double x1 = (dx - startPoint) * scale +
                                                10;
                                        double x2 = ((double) seqReg.length()) *
                                                scale;
                                        g.setColor(PatternOperations.
                                                getPatternColor(pattern.
                                                hashCode()));
                                        g.drawRect((int) x1, 2, (int) x2, 23);
                                    }
                                    id++;
                                }
                            } else {
                                System.out.println(
                                        "Weid null pattern return: Check code in Sequence View Widget");
                            }
                        }
                    }
                }
                prevSeqId = seqId;
                prevSeqDx = seqDx;
            } else {

            }
        }
    }

    private int getSeqId(int y) {
        int seqId = (y - yOff) / yStep;
        return seqId;
    }

    private int getSeqDx(int x) {
        double scale = Math.min(5.0,
                                (double) (seqViewWPanel.getWidth() - 20 - xOff) /
                                (double) displaySequenceDB.getMaxLength());
        int seqDx = (int) ((double) (x - xOff) / scale);
        return seqDx;
    }

    void showPatterns() {
        if (selectedPatterns.size() > 0) {
            for (int i = 0; i < selectedPatterns.size(); i++) {
                DSMatchedSeqPattern pattern = (DSMatchedSeqPattern)
                                              selectedPatterns.get(i);
                if (pattern instanceof CSMatchedSeqPattern) {
                    if (pattern.getASCII() == null) {
                        PatternOperations.fill((CSMatchedSeqPattern) pattern,
                                               displaySequenceDB);
                    }
                    //( (DefaultListModel) patternList.getModel()).addElement(pattern);
                    this.repaint();
                }
            }
        }
    }

    void this_caretPositionChanged(InputMethodEvent e) {
        showPatterns();
    }

    void this_propertyChange(PropertyChangeEvent e) {

        showPatterns();
    }

    public void deserialize(String filename) {
        /*
              FileInputStream stream;
              ObjectInput oos;
         try{
           stream = new FileInputStream(filename);
           oos = new ObjectInputStream(stream);
           panel = (MarkerPanelSetImpl)oos.readObject();
           jScrollPane2.getViewport().remove(panelTree);
           panelTreeModel = (DefaultTreeModel)oos.readObject();
           root = (DefaultMutableTreeNode)panelTreeModel.getRoot();
           panelTree = new JTree(panelTreeModel);
           panelTree.addMouseListener(panelTreeListener);
           panelTree.setEditable(false);
           panelTreeSelection = panelTree.getSelectionModel();
           panelTreeSelection.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
           panelTree.setCellRenderer(treeRenderer);
           jScrollPane2.getViewport().add(panelTree, null);
           String label = panel.getLabel();
           if (panels.containsKey(label)){
             int i = 0;
             while (panels.containsKey(label)){
               label += ++i;
             }
         JOptionPane.showMessageDialog(genePanel, "Renamed PanelSet as a PanelSet " +
         "with the same name already exists in " +
                                           "the list of PanelSets",
                                           "Renamed PanelSet",
                                           JOptionPane.INFORMATION_MESSAGE);
             panel.setName(label);
           }
           panels.put(label, panel);
           jPanelSetItem.addItem(label);
           jPanelSetItem.setSelectedItem(label);
           genePanel.invalidate();
           genePanel.repaint();
           panelModified();
         }
         catch (IOException ioe){ ioe.printStackTrace(); }
         catch (ClassNotFoundException cnfe){ cnfe.printStackTrace(); }
         */
    }




    public void setSequenceDB(DSSequenceSet db) {
        sequenceDB = db;
        displaySequenceDB = db;
        //selectedPatterns = new ArrayList();
        if (sequenceDB != null) {
            seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
            seqViewWPanel.initialize(null, db);
            selectedPatterns.clear();
            repaint();
        }
    }

    public DSSequenceSet getSequenceDB() {
        return sequenceDB;
    }



    public void setPatterns(DSCollection<DSMatchedPattern<DSSequence,
                            DSSeqRegistration>> matches) {
        selectedPatterns.clear();
        for (int i = 0; i < matches.size(); i++) {
            selectedPatterns.add(matches.get(i));
        }
    }


    public void showAllBtn_actionPerformed(ActionEvent e) {
        seqViewWPanel.setShowAll(showAllBtn.isSelected());
        if (selectedPatterns.size() > 0) {
            if (showAllBtn.isSelected()) {

                displaySequenceDB = sequenceDB.createSubSetSequenceDB(
                        createTempDBIndex());
                displaySequenceDB.setMatchIndex(sequenceDB.getMatchIndex());
                displaySequenceDB.setReverseIndex(sequenceDB.getReverseIndex());
                if (sequenceDB != null) {
                    seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
                    seqViewWPanel.initialize(selectedPatterns,
                                             displaySequenceDB);

                }
            } else {
                if (sequenceDB != null) {
                    seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
                    displaySequenceDB = sequenceDB;
                    seqViewWPanel.initialize(selectedPatterns, sequenceDB);
                }
            }

        } else {
            if (sequenceDB == null) {
                JOptionPane.showMessageDialog(null,
                                              "No sequence is stored right now, please load sequences first.",
                                              "No Pattern",
                                              JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No pattern is stored right now, please generate patterns with Pattern Discory module first.",
                                              "No Pattern",
                                              JOptionPane.ERROR_MESSAGE);
                seqViewWPanel.setMaxSeqLen(sequenceDB.getMaxLength());
                displaySequenceDB = sequenceDB;
                seqViewWPanel.setShowAll(false);
                seqViewWPanel.initialize(null, sequenceDB);

            }
            showAllBtn.setSelected(false);

        }

        this.repaint();
    }

}
