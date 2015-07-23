package org.geworkbench.components.microarrays;

import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.*;
import org.geworkbench.util.microarrayutils.MicroarrayVisualizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * <p>Title: Plug And Play Framework</p>
 * <p>Description: Architecture for enGenious Plug&Play</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust</p>
 *
 * @author Andrea Califano
 * @version 1.0
 */

@AcceptTypes({DSMicroarraySet.class}) public class MicroarrayPanel extends MicroarrayVisualizer implements VisualPlugin, MenuListener {
    //IMarkerGraphSubscriber markerPlot = null;
    //JMicroarrayVisualzer visualizer           = new JMicroarrayVisualzer(this);
    MicroarrayDisplay microarrayImageArea = new MicroarrayDisplay(this);
    int x = 0;
    int y = 0;
    JToolBar jToolBar = new JToolBar();
    JSlider jMASlider = new JSlider();
    JSlider intensitySlider = new JSlider();
    JCheckBox jShowAllMArrays = new JCheckBox();
    JTextField jMALabel = new JTextField(20) {
        @Override public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    };
    JLabel intensityLabel = new JLabel("Intensity");
    JLabel arrayLabel = new JLabel("Array");
    JPopupMenu jDisplayPanelPopup = new JPopupMenu();
    JMenuItem jShowMarkerMenu = new JMenuItem();
    JMenuItem jRemoveMarkerMenu = new JMenuItem();
    JMenuItem jSaveImageMenu = new JMenuItem();
    JCheckBox jShowAllMarkers = new JCheckBox();
    MicroarrayColorGradient valueGradient = new MicroarrayColorGradient(Color.gray, Color.gray, Color.gray);
    BorderLayout jLayout = new BorderLayout();
    HashMap listeners = new HashMap();
    DSMicroarraySet<DSMicroarray> mArraySet = null;
    private boolean forcedSliderChange = false;
    public static final int IMAGE_SNAPSHOT_WIDTH = 800;

    public MicroarrayPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ActionListener getActionListener(String key) {
        return (ActionListener) listeners.get(key);
    }

    public void setMicroarraySet(DSMicroarraySet maSet) {
        // Note that the check to guarantee that this is in fact a valid MA Set and that it is
        // different from the previous one is already performed at the superclass level.
        // This method should never be called other than by the superclass JNotifyMAChange method.
        mArraySet = maSet;
        microarrayImageArea.setMicroarrays(mArraySet);
        if(maSet!=null){
            org.geworkbench.bison.util.colorcontext.ColorContext colorContext = (
                    org.geworkbench.bison.util.colorcontext.ColorContext) maSet.
                    getObject(org.geworkbench.bison.util.colorcontext.
                              ColorContext.class);
            valueGradient.setMinColor(colorContext.getMinColorValue(
                    intensitySlider.getValue()));
            valueGradient.setCenterColor(colorContext.getMiddleColorValue(
                    intensitySlider.getValue()));
            valueGradient.setMaxColor(colorContext.getMaxColorValue(
                    intensitySlider.getValue()));
            valueGradient.repaint();
            reset();
            selectMicroarray(0);
        }
    }

    protected void reset() {
        super.reset();
        if (mArraySet != null) {
            jMASlider.setMaximum(dataSetView.items().size() - 1);
            jMASlider.setMinimum(0);
            jMASlider.setValue(0);
        } else {
            jMASlider.setMaximum(0);
            jMASlider.setMinimum(0);
            jMASlider.setValue(0);
        }
    }

    protected void addPattern(Object pattern) {
        microarrayImageArea.patterns.add(pattern);
    }

    protected void clearPatterns() {
        microarrayImageArea.patterns.clear();
    }

    void pushMask(Object mask) {
        microarrayImageArea.masks.push(mask);
    }

    void popMask() {
        microarrayImageArea.masks.pop();
    }

    void clearMasks() {
        microarrayImageArea.masks.clear();
    }

    private void jbInit() throws Exception {
        microarrayImageArea.setLayout(jLayout);
        microarrayImageArea.setBorder(BorderFactory.createEtchedBorder());
        microarrayImageArea.setOpaque(false);

        microarrayImageArea.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                microarrayImageArea_mouseMoved(e);
            }
        });
        microarrayImageArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                microarrayImageArea_mouseClicked(e);
            }

            public void mouseExited(MouseEvent e) {
                microarrayImageArea_mouseExited(e);
            }

            public void mouseReleased(MouseEvent e) {
                microarrayImageArea_mouseReleased(e);
            }
        });
        jMASlider.setValue(0);
        jMASlider.setMaximum(0);
        jMASlider.setMinimum(0);
        jMASlider.setSnapToTicks(true);
        jMASlider.setPaintTicks(true);
        jMASlider.setMinorTickSpacing(1);
        jMASlider.setMajorTickSpacing(5);
        jMASlider.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        jMASlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chipSlider_stateChanged(e);
            }
        });

        intensitySlider.setPaintTicks(true);
        intensitySlider.setValue(100);
        intensitySlider.setMinorTickSpacing(2);
        intensitySlider.setMinimum(1);
        intensitySlider.setMaximum(200);
        intensitySlider.setMajorTickSpacing(50);
        intensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                intensitySlider_stateChanged(e);
            }
        });


        jShowMarkerMenu.setText("Show Marker");
        jShowMarkerMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jShowMarkerMenu_actionPerformed(e);
            }
        });

        jRemoveMarkerMenu.setText("Remove Marker");
        jRemoveMarkerMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRemoveMarkerMenu_actionPerformed(e);
            }
        });

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jSaveImageMenu_actionPerformed(e);
            }
        };

        listeners.put("File.Image Snapshot", listener);
        jSaveImageMenu.setText("Image Snapshot");
        jSaveImageMenu.addActionListener(listener);

        jShowAllMArrays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jShowAllMArrays_actionPerformed(e);
            }
        });
        jShowAllMArrays.setSelected(true);
        jShowAllMArrays.setText("All Arrays");
        jShowAllMarkers.setText("All Markers");
        jShowAllMarkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jShowAllMarkers_actionPerformed(e);
            }
        });
        jShowAllMarkers.setSelected(true);
        jShowAllMarkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jShowAllMarkers_actionPerformed(e);
            }
        });
        jMALabel.setEditable(false);
        mainPanel.add(jToolBar, BorderLayout.SOUTH);
        jToolBar.setLayout(new BoxLayout(jToolBar, BoxLayout.X_AXIS));
        jToolBar.add(jShowAllMArrays, null);
        jToolBar.add(Box.createHorizontalStrut(5), null);
        jToolBar.add(jShowAllMarkers, null);
        jToolBar.add(Box.createGlue(), null);
        jToolBar.add(jMALabel, null);
        jToolBar.add(Box.createGlue(), null);
        jToolBar.add(new JLabel("-"));
        jToolBar.add(Box.createHorizontalStrut(2), null);
        jToolBar.add(valueGradient);
        jToolBar.add(Box.createHorizontalStrut(2), null);
        jToolBar.add(new JLabel("+"));
        jToolBar.add(Box.createGlue(), null);
        jToolBar.add(intensityLabel, null);
        jToolBar.add(Box.createHorizontalStrut(5), null);
        jToolBar.add(intensitySlider, null);
        jToolBar.add(Box.createGlue(), null);
        jToolBar.add(arrayLabel, null);
        jToolBar.add(Box.createHorizontalStrut(5), null);
        jToolBar.add(jMASlider, null);
        mainPanel.add(microarrayImageArea, BorderLayout.CENTER);
        jDisplayPanelPopup.add(jShowMarkerMenu);
        jDisplayPanelPopup.add(jRemoveMarkerMenu); // Popup Menu Added to the MAImageArea
        jDisplayPanelPopup.add(jSaveImageMenu);
    }

    void chipSlider_stateChanged(ChangeEvent e) {
        int mArrayId = jMASlider.getValue();
        if ((mArrayId >= 0) && !forcedSliderChange) {
            if (selectMicroarray(mArrayId)) {
                DSMicroarray array = dataSetView.items().get(mArrayId);
                publishPhenotypeSelectedEvent(new org.geworkbench.events.PhenotypeSelectedEvent(array));
                updateLabel(array);
            }
        }
    }

    void intensitySlider_stateChanged(ChangeEvent e) {
        float v = intensitySlider.getValue() / 100.0f;
        if (v > 1) {
            microarrayImageArea.setIntensity((float) (1 + Math.exp(v) - Math.exp(1.0)));
        } else {
            microarrayImageArea.setIntensity(v);
        }
        microarrayImageArea.repaint();
    }

    private void updateLabel(DSMicroarray array) {
        jMALabel.setText(array.getLabel());
    }

    @Publish public org.geworkbench.events.PhenotypeSelectedEvent publishPhenotypeSelectedEvent(org.geworkbench.events.PhenotypeSelectedEvent event) {
        return event;
    }

    private boolean selectMicroarray(int mArrayId) {
        if (mArraySet != null && dataSetView.items().size() > 0) {
            DSMicroarray mArray = dataSetView.items().get(mArrayId);
            if (mArray != null) {
                microarrayId = mArray.getSerial();
                microarrayImageArea.setMicroarray(mArray);
                //subscriber.notifyChange(this, IMicroarrayIdChangeSubscriber.class);
                //subscriber.notifyChange(this, IMarkerIdClickSubscriber.class);
                try {
                    microarrayImageArea.repaint();
                } catch (java.lang.Exception exception) {
                    exception.printStackTrace();
                }
            }
            return true;
        } else {
            microarrayImageArea.setMicroarray(null);
            return false;
        }
    }

    void microarrayImageArea_mouseMoved(MouseEvent event) {
        if (mArraySet != null) {
            x = event.getX();
            y = event.getY();
            markerId = microarrayImageArea.getGeneIdAndRubberBand(x, y);
            if ((markerId >= 0) && (markerId < mArraySet.size()) && (markerId != microarrayImageArea.selectedGeneId)) {
                microarrayImageArea.selectedGeneId = markerId;
                //selectedGeneId is not used anywhere
                //this is not right, markerID is just the relative position of the marker in current image
                //it can't be used to find the marker in microarryset++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++!!!!!
                //subscriber.notifyChange(this, IMarkerIdChangeSubscriber.class);
            }
        }
    }

    void microarrayImageArea_mouseExited(MouseEvent e) {
        microarrayImageArea.rubberBandBox(-1, -1);
    }

    public void showValidOnly(boolean show) {
        microarrayImageArea.showValidOnly = show;
        mainPanel.repaint();
    }

    void jShowMarkerMenu_actionPerformed(ActionEvent e) {

        //markerPlot.notifyMarkerGraph(microarraySet, microarrayId, markerId, true);
        microarrayImageArea.graphGene(markerId);
        microarrayImageArea.repaint();
    }

    public String getComponentName() {
        return "Microarray Display Panel";
    }

    void jRemoveMarkerMenu_actionPerformed(ActionEvent e) {
        //markerPlot.notifyMarkerGraph(microarraySet, visualizer.microarrayId, visualizer.markerId, false);
        microarrayImageArea.ungraphGene(markerId);
        microarrayImageArea.repaint();
    }

    void jSaveImageMenu_actionPerformed(ActionEvent e) {
        Image currentImage = microarrayImageArea.getCurrentImage();
        if (currentImage != null && microarrayImageArea.microarray != null) {
//            int width = currentImage.getWidth(null);
//            int height = currentImage.getHeight(null);
//            double factor = IMAGE_SNAPSHOT_WIDTH / (double) width;
//            width = (int) (width * factor);
//            height = (int) (height * factor);
//            Image scaled = currentImage.getScaledInstance(width, height, Image.SCALE_FAST);
            ImageIcon newIcon = new ImageIcon(currentImage, "Microarray Image: " + microarrayImageArea.microarray.getLabel());
            org.geworkbench.events.ImageSnapshotEvent event = new org.geworkbench.events.ImageSnapshotEvent("MicroarrayPanel ImageSnapshot", newIcon, ImageSnapshotEvent.Action.SAVE);
            publishImageSnapshotEvent(event);
        }
    }

    void jShowAllMArrays_actionPerformed(ActionEvent e) {
        showAllMArrays(jShowAllMArrays.isSelected());
    }

    void microarrayImageArea_mouseClicked(MouseEvent e) {

        if (mArraySet != null) {
            //int clickNo = e.getClickCount(); //? not used anywhere

            String uid = Integer.toString(markerId);
            if (markerId != -1) {
                MarkerSelectedEvent mse = new MarkerSelectedEvent(mArraySet.getMarkers().get(markerId));
                publishMarkerSelectedEvent(mse);
            }
        }
    }

    void microarrayImageArea_mouseReleased(MouseEvent e) {
        if (e.isMetaDown()) {
            jDisplayPanelPopup.show(mainPanel, e.getX(), e.getY());
        }
    }

    void jShowAllMarkers_actionPerformed(ActionEvent e) {
        showAllMarkers(jShowAllMarkers.isSelected());
    }

    /**
     * geneSelectorAction
     *
     * @param e GeneSelectorEvent
     */
    @Subscribe public void receive(GeneSelectorEvent e, Object source) {
        if (e.getPanel() != null) {
            markerPanel = e.getPanel().activeSubset();
            dataSetView.setMarkerPanel(markerPanel);
            reset();
            repaint();
        }
    }

    @Subscribe public void receive(SingleMicroarrayEvent event, Object source) {
        DSMicroarray array = event.getMicroarray();
        displayMicroarray(array);
    }

    private void displayMicroarray(DSMicroarray array) {
        int index = dataSetView.items().indexOf(array);
        if (index != -1) {
            selectMicroarray(index);
            forcedSliderChange = true;
            jMASlider.setValue(index);
            forcedSliderChange = false;
            updateLabel(array);
        }
    }

    /**
     * phenotypeSelectorAction
     *
     * @param e PhenotypeSelectorEvent
     */
    @Subscribe public void receive(PhenotypeSelectorEvent e, Object source) {
        if (e.getTaggedItemSetTree() != null) {
            DSMicroarray oldArray = null;
            try {
                oldArray = dataSetView.items().get(microarrayId);
            } catch (IndexOutOfBoundsException ioobe) {
                // Ignore -- no arrays
            }
            mArrayPanel = e.getTaggedItemSetTree();
            dataSetView.setItemPanel((DSPanel) mArrayPanel);
            reset();
            // Keep old microarray selection, if possible
            if (oldArray != null) {
                displayMicroarray(oldArray);
            }
            repaint();
        }
    }

    private class MicroarrayColorGradient extends JComponent {
        private Color minColor, maxColor, centerColor;

        public MicroarrayColorGradient(Color minColor, Color centerColor, Color maxColor) {
            this.minColor = minColor;
            this.maxColor = maxColor;
            this.centerColor = centerColor;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(new GradientPaint(0, 0, minColor, getWidth()/2, 0, centerColor));
            g2d.fillRect(0, 0, getWidth()/2, getHeight());
            g2d.setPaint(new GradientPaint(getWidth()/2, 0, centerColor, getWidth(), 0, maxColor));
            g2d.fillRect(getWidth()/2, 0, getWidth(), getHeight());
        }

        public Color getMinColor() {
            return minColor;
        }

        public void setMinColor(Color minColor) {
            this.minColor = minColor;
        }

        public Color getMaxColor() {
            return maxColor;
        }

        public void setMaxColor(Color maxColor) {
            this.maxColor = maxColor;
        }

        public Color getCenterColor() {
            return centerColor;
        }

        public void setCenterColor(Color centerColor) {
            this.centerColor = centerColor;
        }
    }
}
