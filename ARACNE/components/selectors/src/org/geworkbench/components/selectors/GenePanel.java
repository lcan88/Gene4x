package org.geworkbench.components.selectors;

import com.Ostermiller.util.CSVPrinter;
import com.Ostermiller.util.ExcelCSVParser;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.sequences.DSSequenceSet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Script;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.MarkerSelectedEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.Util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A panel that handles the creation and management of gene panels, as well as individual gene selection.
 *
 * @author John Watkinson
 */
public class GenePanel extends SelectorPanel<DSGeneMarker> {

    /**
     * <code>FileFilter</code> that is used by the <code>JFileChoose</code> to
     * show just panel set files on the filesystem
     */
    protected static class MarkerPanelSetFileFilter extends javax.swing.filechooser.FileFilter {
        private String fileExt;

        MarkerPanelSetFileFilter() {
            fileExt = ".csv";
        }

        public String getExtension() {
            return fileExt;
        }

        public String getDescription() {
            return "Comma Separated Values Files";
        }

        public boolean accept(File f) {
            boolean returnVal = false;
            if (f.isDirectory() || f.getName().endsWith(fileExt)) {
                return true;
            }
            return returnVal;
        }
    }

    public GenePanel() {
        super(DSGeneMarker.class, "Marker");
        // Add gene panel specific menu items.
        treePopup.add(savePanelItem);
        rootPopup.add(loadPanelItem);
        savePanelItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveButtonPressed(rightClickedPath);
            }
        });
        exportPanelItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportPanelPressed();
            }
        });
        loadPanelItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadButtonPressed();
            }
        });
        // Load button at bottom of component
        JPanel loadPanel = new JPanel();
        loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.X_AXIS));
        JButton loadButton = new JButton("Load Set");
        loadPanel.add(loadButton);
        loadPanel.add(Box.createHorizontalGlue());
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadButtonPressed();
            }
        });
        lowerPanel.add(loadPanel);
    }

    private JMenuItem savePanelItem = new JMenuItem("Save");
    private JMenuItem loadPanelItem = new JMenuItem("Load Set");
    private JMenuItem exportPanelItem = new JMenuItem("Export");

    private void saveButtonPressed(TreePath path) {
        String label = getLabelForPath(path);
        if (label != null) {
            JFileChooser fc = new JFileChooser(".");
            FileFilter filter = new MarkerPanelSetFileFilter();
            fc.setFileFilter(filter);
            fc.setDialogTitle("Save Marker Set");
            String extension = ((MarkerPanelSetFileFilter) filter).getExtension();
            int choice = fc.showSaveDialog(mainPanel.getParent());
            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getAbsolutePath();
                if (!filename.endsWith(extension)) {
                    filename += extension;
                }
                boolean confirmed = true;
                if (new File(filename).exists()) {
                    int confirm = JOptionPane.showConfirmDialog(getComponent(), "Replace existing file?");
                    if (confirm != JOptionPane.YES_OPTION) {
                        confirmed = false;
                    }
                }
                if (confirmed) {
                    DSPanel<DSGeneMarker> panel = context.getItemsWithLabel(label);
                    serializePanel(filename, panel);
                }
            }
        }
    }

    private void loadButtonPressed() {
        JFileChooser fc = new JFileChooser(".");
        javax.swing.filechooser.FileFilter filter = new MarkerPanelSetFileFilter();
        fc.setFileFilter(filter);
        fc.setDialogTitle("Open Marker Set");
        int choice = fc.showOpenDialog(mainPanel.getParent());
        if (choice == JFileChooser.APPROVE_OPTION) {
            DSPanel<DSGeneMarker> panel = deserializePanel(fc.getSelectedFile());
            addPanel(panel);
            throwLabelEvent();
        }
    }

    private void exportPanelPressed() {
        JOptionPane.showMessageDialog(getComponent(), "To be implemented...");
        // todo
    }

    protected void throwLabelEvent() {
        GeneSelectorEvent event = null;
        event = new GeneSelectorEvent(context.getLabelTree());
        if (event != null) {
            publishGeneSelectorEvent(event);
        }
    }

    /**
     * Utility to save a panel to the filesystem as CSV.
     * <p>
     * Format:
     * <p>
     * File name (without .CSV extension) is the name of the panel.
     * <p>
     * Rows of the file contains the label of markers, in order. Only the first column is used.
     *
     * @param filename filename to which the current panel is to be saved.
     */
    private void serializePanel(String filename, DSPanel<DSGeneMarker> panel) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            CSVPrinter out = new CSVPrinter(fileWriter);
            if (panel != null && panel.size() > 0) {
                for (int i = 0; i < panel.size(); i++) {
                    DSGeneMarker marker = panel.get(i);
                    out.println(marker.getLabel());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // Lost cause-- ignore
                }
            }
        }
    }

    /**
     * Utility to obtain the stored panel sets from the filesystem
     *
     * @param file file which contains the stored panel set
     */
    private DSPanel<DSGeneMarker> deserializePanel(final File file) {
        FileInputStream inputStream = null;
        String filename = file.getName();
        if (filename.toLowerCase().endsWith(".csv")) {
            filename = filename.substring(0, filename.length() - 4);
        }
        // Ensure loaded file has unique name
        Set<String> nameSet = new HashSet<String>();
        int n = context.getNumberOfLabels();
        for (int i = 0; i < n; i++) {
            nameSet.add(context.getLabel(i));
        }
        filename = Util.getUniqueName(filename, nameSet);
        DSPanel<DSGeneMarker> panel = new CSPanel<DSGeneMarker>(filename);
        try {
            inputStream = new FileInputStream(file);
            ExcelCSVParser parser = new ExcelCSVParser(inputStream);
            String[][] data = parser.getAllValues();
            for (int i = 0; i < data.length; i++) {
                String[] line = data[i];
                if (line.length > 0) {
                    String label = line[0];
                    DSGeneMarker marker = itemList.get(label);
                    if (marker != null) {
                        panel.add(marker);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Lost cause
                }
            }
        }
        return panel;
    }

    protected boolean dataSetChanged(DSDataSet dataSet) {
        DSItemList items;
        if (dataSet instanceof DSMicroarraySet) {
            DSMicroarraySet maSet = (DSMicroarraySet) dataSet;
            items = maSet.getMarkers();
            setItemList(items);
            return true;
        } else if (dataSet instanceof DSSequenceSet) {
            items = (DSItemList) ((DSSequenceSet) dataSet).getMarkerList();
            setItemList(items);
            return true;
        }
        return false;
    }

    /**
     * Called when a single marker is selected by a component.
     */
    @Subscribe public void receive(MarkerSelectedEvent event, Object source) {
        JList list = itemAutoList.getList();
        int index = itemList.indexOf(event.getMarker());
        list.setSelectedIndex(index);
        list.scrollRectToVisible(list.getCellBounds(index, index));
    }

    @Publish public SubpanelChangedEvent publishSubpanelChangedEvent(SubpanelChangedEvent event) {
        return event;
    }

    @Publish public GeneSelectorEvent publishGeneSelectorEvent(GeneSelectorEvent event) {
        return event;
    }

    protected void publishSingleSelectionEvent(DSGeneMarker item) {
        publishGeneSelectorEvent(new GeneSelectorEvent(item));
    }
    
    @Script public void setDataSet(DSDataSet dataSet) {
        processDataSet(dataSet);
    }

    @Script
    public void createPanel(int a, int b, boolean c) {
        // todo implement
    }

    @Script
    public DSPanel getPanel(String dir) {
        DSPanel<DSGeneMarker> panel = deserializePanel(new File(dir));
        panel.setActive(true);
        addPanel(panel); //redundancy between broadcast and script
        publishGeneSelectorEvent(new GeneSelectorEvent(panel));
        
        return panel;
    }
}
