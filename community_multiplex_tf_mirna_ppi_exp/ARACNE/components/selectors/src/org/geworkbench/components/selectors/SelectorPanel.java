package org.geworkbench.components.selectors;

import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.complex.panels.CSItemList;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.bison.datastructure.properties.DSSequential;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.Overflow;
import org.geworkbench.engine.management.Script;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.ProjectEvent;
import org.geworkbench.events.SubpanelChangedEvent;
import org.geworkbench.util.JAutoList;
import org.geworkbench.util.visualproperties.PanelVisualProperties;
import org.geworkbench.util.visualproperties.PanelVisualPropertiesManager;
import org.geworkbench.util.visualproperties.VisualPropertiesDialog;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author John Watkinson
 */
public abstract class SelectorPanel<T extends DSSequential> implements VisualPlugin, MenuListener {

    public static final String SELECTION_LABEL = "Selection";

    // Data models
    protected DSAnnotationContext<T> context;
    protected SelectorTreeModel<T> treeModel;
    protected DSItemList<T> itemList;
    // protected DSPanel<T> itemPanel;
    protected ItemListModel listModel;
    // protected PanelsEnabledTreeModel treeModel;
    //protected CSPanel<T> emptyPanel;
    protected CSAnnotationContext<T> emptyContext;
    protected CSItemList<T> emptyList;
    // Components
    protected JPanel mainPanel;
    protected JAutoList itemAutoList;
    protected JTree panelTree;
    // Menu items
    protected JPopupMenu itemListPopup = new JPopupMenu();
    protected JMenuItem addToPanelItem = new JMenuItem("Add to Set");
    protected JMenuItem clearSelectionItem = new JMenuItem("Clear Selection");
    protected JPopupMenu treePopup = new JPopupMenu();
    protected JMenuItem renamePanelItem = new JMenuItem("Rename");
    protected JMenuItem activatePanelItem = new JMenuItem("Activate");
    protected JMenuItem deactivatePanelItem = new JMenuItem("Deactivate");
    protected JMenuItem deletePanelItem = new JMenuItem("Delete");
    protected JMenuItem printPanelItem = new JMenuItem("Print");
    protected JMenuItem visualPropertiesItem = new JMenuItem("Visual Properties");
    protected JPopupMenu rootPopup = new JPopupMenu();
    protected JPopupMenu itemPopup = new JPopupMenu();
    protected JMenuItem removeFromPanelItem = new JMenuItem("Remove from Set");
    protected JPanel lowerPanel = new JPanel();
    protected JComboBox contextSelector;
    protected JButton newContextButton;
    // Context info for right-click events
    TreePath rightClickedPath = null;

    private JCheckBox bypassCheckbox;

    protected Class<T> panelType;
    protected SelectorTreeRenderer treeRenderer;

    private HashMap<String, ActionListener> menuListeners;

    public SelectorPanel(Class<T> panelType, String name) {
        this.panelType = panelType;
        listModel = new ItemListModel();
        itemAutoList = new ItemList(listModel);
        // Initialize data models
        emptyList = new CSItemList<T>();
        // emptyPanel = new CSPanel<T>("");
        emptyContext = new CSAnnotationContext<T>("", null);
        itemList = emptyList;
        treeModel = new SelectorTreeModel<T>(emptyContext);
        // Initialize components
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        itemAutoList.getList().setCellRenderer(new ListCellRenderer());
        panelTree = new JTree(treeModel);
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        // Add context selector
        contextSelector = new JComboBox();
        Dimension minSize = contextSelector.getMinimumSize();
//        Dimension prefSize = contextSelector.getPreferredSize();
        contextSelector.setMaximumSize(new Dimension(1000, minSize.height));
        newContextButton = new JButton("New");
        newContextButton.setEnabled(false);
        JPanel contextPanel = new JPanel();
        contextPanel.setLayout(new BoxLayout(contextPanel, BoxLayout.X_AXIS));
        contextPanel.add(contextSelector);
        contextPanel.add(newContextButton);
        JLabel groupLabel = new JLabel(" " + name + " Sets");
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.add(groupLabel);
        labelPanel.add(Box.createHorizontalGlue());
        bypassCheckbox = new JCheckBox("Bypass", false);
        // Temporarily not in use for 3.1 release - watkin
        // labelPanel.add(bypassCheckbox);
        lowerPanel.add(labelPanel);
        lowerPanel.add(contextPanel);
        lowerPanel.add(new JScrollPane(panelTree));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemAutoList, lowerPanel);
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.65);
        treeRenderer = new SelectorTreeRenderer(this);
        panelTree.setCellRenderer(treeRenderer);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        // Initialize popups
        itemListPopup.add(addToPanelItem);
        itemListPopup.add(clearSelectionItem);
        treePopup.add(renamePanelItem);
        treePopup.add(activatePanelItem);
        treePopup.add(deactivatePanelItem);
        treePopup.add(deletePanelItem);
        treePopup.add(printPanelItem);

        // Removing the "Export" popup item, until we decide what the export
        // functionlity is, if anything (since there is also a "Save" option.
        // treePopup.add(exportPanelItem);

        treePopup.add(visualPropertiesItem);
        // todo - move to a new gui setup
        itemPopup.add(removeFromPanelItem);
        // Add behaviors
        menuListeners = new HashMap<String, ActionListener>();
        panelTree.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                panelTreeClicked(e);
            }
        });
        ActionListener addToPanelListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addToLabelPressed();
            }
        };
        addToPanelItem.addActionListener(addToPanelListener);
        menuListeners.put("Commands.Panels.Add to Set", addToPanelListener);
        ActionListener clearListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSelectionPressed();
            }
        };
        clearSelectionItem.addActionListener(clearListener);
        menuListeners.put("View.Clear Selection", clearListener);
        ActionListener renameListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameLabelPressed(rightClickedPath);
            }
        };
        renamePanelItem.addActionListener(renameListener);
        menuListeners.put("Commands.Panels.Rename", renameListener);
        ActionListener activateListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                activateOrDeactivateLabelPressed(true);
            }
        };
        activatePanelItem.addActionListener(activateListener);
        menuListeners.put("Commands.Panels.Activate", activateListener);
        ActionListener deactivateListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                activateOrDeactivateLabelPressed(false);
            }
        };
        deactivatePanelItem.addActionListener(deactivateListener);
        ActionListener deleteListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePanelPressed();
            }
        };
        deletePanelItem.addActionListener(deleteListener);
        menuListeners.put("Commands.Panels.Delete", deleteListener);
        menuListeners.put("Commands.Panels.Deactivate", deactivateListener);
        printPanelItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printPanelPressed(rightClickedPath);
            }
        });
        visualPropertiesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visualPropertiesPressed(rightClickedPath);
            }
        });
        removeFromPanelItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFromLabelPressed();
            }
        });
        contextSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DSAnnotationContext newContext = (DSAnnotationContext) contextSelector.getSelectedItem();
                switchContext(newContext);
            }
        });
        newContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewContext();
            }
        });
    }

    protected String getLabelForPath(TreePath path) {
        Object obj = path.getLastPathComponent();
        if (obj instanceof String) {
            return (String) obj;
        } else {
            return null;
        }
    }

    private T getItemForPath(TreePath path) {
        Object obj = path.getLastPathComponent();
        if (panelType.isAssignableFrom(obj.getClass())) {
            return panelType.cast(obj);
        } else {
            return null;
        }
    }

    private void ensurePathIsSelected(TreePath path) {
        if (path != null) {
            boolean alreadySelected = false;
            TreePath[] paths = panelTree.getSelectionPaths();
            if (paths != null) {
                for (int i = 0; i < paths.length; i++) {
                    if (paths[i].getLastPathComponent().equals(path.getLastPathComponent())) {
                        alreadySelected = true;
                        break;
                    }
                }
            }
            if (!alreadySelected) {
                panelTree.setSelectionPath(path);
            }
        }
    }

    private void ensureItemIsSelected(int index) {
        boolean alreadySelected = false;
        int[] indices = itemAutoList.getList().getSelectedIndices();
        if (indices != null) {
            for (int i = 0; i < indices.length; i++) {
                if (index == indices[i]) {
                    alreadySelected = true;
                    break;
                }
            }
        }
        if (!alreadySelected) {
            itemAutoList.getList().setSelectedIndex(index);
        }
    }

    private void removePanel(final String label) {
        final int index = treeModel.getIndexOfChild(context, label);
        context.removeLabel(label);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                treeModel.fireLabelRemoved(label, index);
            }
        });
    }

    protected void addPanel(DSPanel<T> panel) {
        final String label = panel.getLabel();
        context.addLabel(label);
        for (int i = 0; i < panel.size(); i++) {
            T t = panel.get(i);
            context.labelItem(t, panel.getLabel());
        }
        final int index = treeModel.getIndexOfChild(context, label);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                treeModel.fireLabelAdded(label, index);
            }
        });
    }

    protected void panelTreeClicked(final MouseEvent e) {
        TreePath path = panelTree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            String label = getLabelForPath(path);
            T item = getItemForPath(path);
            if ((e.isMetaDown()) && (e.getClickCount() == 1)) {
                rightClickedPath = path;
                ensurePathIsSelected(rightClickedPath);
                if (label != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showTreePopup(e);
                        }
                    });
                } else if (item != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            itemPopup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    });
                } else { // root
                    // Show root popup
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            rootPopup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    });
                }
            } else {
                if (label != null) {
                    if (e.getX() < panelTree.getPathBounds(path).x + treeRenderer.getCheckBoxWidth()) {
                        context.setLabelActive(label, !context.isLabelActive(label));
                        treeModel.valueForPathChanged(path, label);
                        throwLabelEvent();
                    } else {
                        labelClicked(e, path, label);
                    }
                } else if (item != null) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        publishSingleSelectionEvent(item);
                    }
                }
            }
        } else {
            panelTree.clearSelection();
        }
    }

    protected void labelClicked(MouseEvent e, TreePath path, String label) {
        // No-op
    }

    protected void showTreePopup(MouseEvent e) {
        treePopup.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void activateOrDeactivateLabelPressed(boolean value) {
        String[] labels = getSelectedTreesFromTree();
        if (labels.length > 0) {
            for (int i = 0; i < labels.length; i++) {
                context.setLabelActive(labels[i], value);
                // Notify model
                treeModel.fireLabelChanged(labels[i]);
            }
            throwLabelEvent();
        }
    }

    private void itemClicked(int index, MouseEvent e) {
        if (index != -1) {
            if (itemList != null) {
                T item = itemList.get(index);
                publishSingleSelectionEvent(item);
            }
        }
    }

    private void itemDoubleClicked(int index, MouseEvent e) {
        // Get double-clicked item
        T item = itemList.get(index);
        if (context.hasLabel(item, SELECTION_LABEL)) {
            context.removeLabelFromItem(item, SELECTION_LABEL);
        } else {
            context.labelItem(item, SELECTION_LABEL);
        }
        treeModel.fireLabelItemsChanged(SELECTION_LABEL);
        throwLabelEvent();
        listModel.refreshItem(index);
    }

    private void itemRightClicked(int index, final MouseEvent e) {
        ensureItemIsSelected(index);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                itemListPopup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    protected void removeFromLabelPressed() {
        TreePath[] paths = panelTree.getSelectionPaths();
        HashSet<String> affectedLabels = new HashSet<String>();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            Object obj = path.getLastPathComponent();
            if (panelType.isAssignableFrom(obj.getClass())) {
                T item = panelType.cast(obj);
                // Path must have a panel as the second-to-last component
                String label = (String) path.getParentPath().getLastPathComponent();
                context.removeLabelFromItem(item, label);
                affectedLabels.add(label);
            }
        }
        for (Iterator<String> iterator = affectedLabels.iterator(); iterator.hasNext();) {
            String label = iterator.next();
            treeModel.fireLabelItemsChanged(label);
        }
        throwLabelEvent();
    }

    protected void addToLabelPressed() {
        T[] items = getSelectedItemsFromList();
        if (items.length > 0) {
            // Is there already a selected panel?
            String defaultLabel = getSelectedLabelFromTree();
            if (defaultLabel == null) {
                defaultLabel = "";
            }
            String label = JOptionPane.showInputDialog("Set Label:", defaultLabel);
            if (label == null) {
                return;
            } else {
                if (context.indexOfLabel(label) == -1) {
                    addPanel(new CSPanel<T>(label));
                }
                for (int i = 0; i < items.length; i++) {
                    T item = items[i];
                    context.labelItem(item, label);
                }
                panelTree.scrollPathToVisible(new TreePath(new Object[]{context, label}));
                treeModel.fireLabelItemsChanged(label);
                throwLabelEvent();
            }
        }
    }

    protected void clearSelectionPressed() {
        context.clearItemsFromLabel(SELECTION_LABEL);
        itemAutoList.getList().repaint();
        treeModel.fireLabelItemsChanged(SELECTION_LABEL);
        throwLabelEvent();
    }

    /**
     * Only effects the right-clicked path, not the entire selection
     */
    protected void renameLabelPressed(TreePath path) {
        String oldLabel = getLabelForPath(path);
        if (oldLabel != null) {
            String newLabel = JOptionPane.showInputDialog("New Label:", oldLabel);
            if (newLabel != null) {
                // todo: check for an existing panel with this name
                context.renameLabel(oldLabel, newLabel);
                treeModel.fireLabelChanged(newLabel);
                throwLabelEvent();
            }
        }
    }

    protected void deletePanelPressed() {
        String[] labels = getSelectedTreesFromTree();
        if (labels.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(getComponent(), "Delete selected set" + (labels.length > 1 ? "s" : "") + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                panelTree.clearSelection();
                for (int i = 0; i < labels.length; i++) {
                    String label = labels[i];
                    // Cannot delete root label or selection label
                    if (!SELECTION_LABEL.equals(label)) {
                        removePanel(label);
                    }
                }
                throwLabelEvent();
            }
        }
    }

    protected void printPanelPressed(TreePath path) {
        String label = getLabelForPath(path);
        if (label != null) {
            // Get a PrinterJob
            PrinterJob job = PrinterJob.getPrinterJob();
            // Ask user for page format (e.g., portrait/landscape)
            PageFormat pf = job.pageDialog(job.defaultPage());
            // Specify the Printable is an instance of
            // PrintListingPainter; also provide given PageFormat
            job.setPrintable(new PrintListingPainter(label), pf);
            // Print 1 copy
            job.setCopies(1);
            // Put up the dialog box
            if (job.printDialog()) {
                // Print the job if the user didn't cancel printing
                try {
                    job.print();
                } catch (Exception pe) {
                    pe.printStackTrace();
                }
            }
        }
    }

    /**
     * Only effects the right-clicked path, not the entire selection
     */
    protected void visualPropertiesPressed(TreePath path) {
        DSAnnotationContextManager contextManager = CSAnnotationContextManager.getInstance();
        String label = getLabelForPath(path);
        DSPanel panel = contextManager.getCurrentContext(itemList).getItemsWithLabel(label);
        if (panel != null) {
            // Get current active index of panel for default visual properties
            int index = 0;
            if (context.isLabelActive(label)) {
                int n = context.getNumberOfLabels();
                for (int i = 0; i < n; i++) {
                    String l = context.getLabel(i);
                    if (context.isLabelActive(l)) {
                        index++;
                    }
                    if (label.equals(l)) {
                        break;
                    }
                }
            }
            VisualPropertiesDialog dialog = new VisualPropertiesDialog(null, "Change Visual Properties", label, index);
            dialog.pack();
            dialog.setSize(600, 600);
            dialog.setVisible(true);
            if (dialog.isPropertiesChanged()) {
                PanelVisualPropertiesManager manager = PanelVisualPropertiesManager.getInstance();
                PanelVisualProperties visualProperties = dialog.getVisualProperties();
                if (visualProperties == null) {
                    manager.clearVisualProperties(panel);
                } else {
                    manager.setVisualProperties(panel, visualProperties);
                }
                throwLabelEvent();
            }
        }
    }

    /**
     * Convenience method to get all the selected items in the item list.
     */
    private T[] getSelectedItemsFromList() {
        int[] indices = itemAutoList.getList().getSelectedIndices();
        int n = indices.length;
        T[] items = (T[]) Array.newInstance(panelType, n);
        for (int i = 0; i < n; i++) {
            items[i] = itemList.get(indices[i]);
        }
        return items;
    }

    /**
     * Convenience method to get all the selected panels in the panel tree.
     */
    protected String[] getSelectedTreesFromTree() {
        TreePath[] paths = panelTree.getSelectionPaths();
        int n = paths.length;
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            TreePath path = paths[i];
            Object obj = path.getLastPathComponent();
            if (obj instanceof String) {
                list.add((String) obj);
            }
        }
        return list.toArray(new String[]{});
    }

    /**
     * Convenience method to get all the selected panels in the panel tree.
     */
    private T[] getSelectedItemsFromTree() {
        TreePath[] paths = panelTree.getSelectionPaths();
        int n = paths.length;
        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < n; i++) {
            TreePath path = paths[i];
            Object obj = path.getLastPathComponent();
            if (panelType.isAssignableFrom(obj.getClass())) {
                list.add(panelType.cast(obj));
            }
        }
        return list.toArray((T[]) Array.newInstance(panelType, 0));
    }

    /**
     * Gets the single panel selected from the tree if there is one
     */
    private String getSelectedLabelFromTree() {
        TreePath path = panelTree.getSelectionPath();
        if (path == null) {
            return null;
        } else {
            Object obj = path.getLastPathComponent();
            if (obj instanceof String) {
                return (String) obj;
            } else {
                return null;
            }
        }
    }

    protected void dataSetCleared() {
        treeModel.setContext(emptyContext);
        itemList = emptyList;
        itemAutoList.getList().repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                treeModel.fireTreeStructureChanged();
            }
        });
        contextSelector.removeAllItems();
        newContextButton.setEnabled(false);
        // throwLabelEvent();
    }

    protected abstract boolean dataSetChanged(DSDataSet dataSet);

    protected abstract void throwLabelEvent();

    public Component getComponent() {
        return mainPanel;
    }

    /**
     * Called when a data set is selected or cleared in the project panel.
     */
    @Subscribe public void receive(ProjectEvent projectEvent, Object source) {
        if (projectEvent.getMessage().equals(ProjectEvent.CLEARED)) {
            dataSetCleared();
        }
        DSDataSet dataSet = projectEvent.getDataSet();
        boolean processed = processDataSet(dataSet);
        if (!processed) {
            dataSet = projectEvent.getParent();
            if (dataSet != null) {
                processDataSet(dataSet);
            }
        }
    }

    /**
     * Called when a component wishes to add, change or remove a panel.
     */
    @Subscribe(Overflow.class) public void receive(org.geworkbench.events.SubpanelChangedEvent spe, Object source) {
        if (panelType.isAssignableFrom(spe.getType())) {
            DSPanel<T> receivedPanel = spe.getPanel();
            String panelName = receivedPanel.getLabel();
            switch (spe.getMode()) {
                case SubpanelChangedEvent.NEW: {
                    if (context.indexOfLabel(panelName) != -1) {
                        int number = 1;
                        String newName = panelName + " (" + number + ")";
                        receivedPanel.setLabel(newName);
                        while (context.indexOfLabel(newName) != -1) {
                            number++;
                            newName = panelName + " (" + number + ")";
                            receivedPanel.setLabel(newName);
                        }
                    }
                    addPanel(receivedPanel);
                    break;
                }
                case SubpanelChangedEvent.SET_CONTENTS: {
                    boolean foundPanel = false;
                    if (context.indexOfLabel(panelName) != -1) {
                        foundPanel = true;
                        // Delete everything from the panel and re-add
                        context.clearItemsFromLabel(panelName);
                        for (T marker : receivedPanel) {
                            context.labelItem(marker, panelName);
                        }
                        synchronized (treeModel) {
                            treeModel.fireLabelItemsChanged(panelName);
                        }
                        throwLabelEvent();
                    }
                    if (!foundPanel) {
                        // Add it as a new panel
                        addPanel(receivedPanel);
                    }
                    break;
                }
                case SubpanelChangedEvent.DELETE: {
                    if (context.indexOfLabel(panelName) != -1) {
                        int index = context.indexOfLabel(panelName);
                        context.removeLabel(panelName);
                        treeModel.fireLabelRemoved(panelName, index);
                    }
                    break;
                }
                default:
                    throw new RuntimeException("Unknown subpanel changed event mode: " + spe.getMode());
            }
        }
    }


    protected boolean processDataSet(DSDataSet dataSet) {
        if (dataSet != null) {
            return dataSetChanged(dataSet);
        } else {
            dataSetCleared();
            return false;
        }
    }

    @Script public void setDataSet(DSDataSet dataSet) {
        processDataSet(dataSet);
    }

    @Script
    public void createPanel(int a, int b, boolean c) {
        // todo implement
    }

    protected abstract void publishSingleSelectionEvent(T item);


    /**
     * Printable that is responsible for printing the contents of a panel.
     */
    private class PrintListingPainter implements Printable {
        private Font fnt = new Font("Helvetica", Font.PLAIN, 8);
        private int rememberedPageIndex = -1;
        private long rememberedFilePointer = -1;
        private boolean rememberedEOF = false;
        private int index = 0;
        private int lastIndex = 0;
        DSPanel<T> panel;

        public PrintListingPainter(String label) {
            this.panel = context.getItemsWithLabel(label);
        }

        /**
         * Called by the print job.
         */
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            DecimalFormat format = new DecimalFormat("#.####");
            try {
                int itemNo = Math.min(panel.size(), 500);
                // For catching IOException
                if (pageIndex != rememberedPageIndex) {
                    // First time we've visited this page
                    rememberedPageIndex = pageIndex;
                    lastIndex = index;
                    // If encountered EOF on previous page, done
                    if (rememberedEOF) {
                        return Printable.NO_SUCH_PAGE;
                    }
                    // Save current position in input file
                } else {
                    index = lastIndex;
                }
                g.setColor(Color.black);

                int x = (int) pf.getImageableX() + 10;
                int y = (int) pf.getImageableY() + 12;

                // Put the panel name as a title
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString(panel.getLabel(), x, y);

                // Now do the rest
                g.setFont(fnt);
                y += 36;
                while (y + 12 < pf.getImageableY() + pf.getImageableHeight()) {
                    if (index >= itemNo) {
                        rememberedEOF = true;
                        break;
                    }
                    DSSequential gm = panel.get(index);
                    String line = "[" + gm.getSerial() + "]";
                    g.drawString(line, x, y);
                    g.drawString(gm.getLabel(), x + 30, y);
                    g.drawString(gm.toString(), x + 160, y);
                    y += 12;
                    index++;
                }
                return Printable.PAGE_EXISTS;
            } catch (Exception e) {
                return Printable.NO_SUCH_PAGE;
            }
        }
    }

    /**
     * List Model backed by the item list.
     */
    protected class ItemListModel extends AbstractListModel {

        public int getSize() {
            if (itemList == null) {
                return 0;
            }
            return itemList.size();
        }

        public Object getElementAt(int index) {
            if (itemList == null) {
                return null;
            }
            return itemList.get(index);
        }

        public T getItem(int index) {
            return itemList.get(index);
        }

        /**
         * Indicates to the associated JList that the contents need to be redrawn.
         */
        public void refresh() {
            if (itemList == null) {
                fireContentsChanged(this, 0, 0);
            } else {
                fireContentsChanged(this, 0, itemList.size());
            }
        }

        public void refreshItem(int index) {
            fireContentsChanged(this, index, index);
        }

    }

    /**
     * Auto-scrolling list for items that customizes the double-click and right-click behavior.
     */
    protected class ItemList extends org.geworkbench.util.JAutoList {

        public ItemList(ListModel model) {
            super(model);
        }

        @Override protected void elementDoubleClicked(int index, MouseEvent e) {
            itemDoubleClicked(index, e);
        }

        @Override protected void elementRightClicked(int index, MouseEvent e) {
            itemRightClicked(index, e);
        }

        @Override protected void elementClicked(int index, MouseEvent e) {
            itemClicked(index, e);
        }

    }

    protected class ListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Font font = component.getFont();
            Font boldFont = font.deriveFont(Font.BOLD);
            if (context.hasLabel(itemList.get(index), SELECTION_LABEL)) {
                component.setFont(boldFont);
            }
//            if (!isSelected) {
//                if (context.hasLabel(itemList.get(index), SELECTION_LABEL)) {
//                    component.setBackground(Color.YELLOW);
//                }
//            } else {
//                if (context.hasLabel(itemList.get(index), SELECTION_LABEL)) {
//                    component.setBackground(Color.GREEN);
//                }
//            }
            return component;
        }
    }

    public DSAnnotationContext<T> getContext() {
        return context;
    }

    public void setTreeRenderer(SelectorTreeRenderer treeRenderer) {
        this.treeRenderer = treeRenderer;
        panelTree.setCellRenderer(treeRenderer);
    }

    private boolean resetContextMode = false;

    public void setItemList(DSItemList<T> itemList) {
        resetContextMode = true;
        try {
            this.itemList = itemList;
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            context = manager.getCurrentContext(itemList);
            initializeContext(context);
            contextSelector.removeAllItems();
            int n = manager.getNumberOfContexts(itemList);
            for (int i = 0; i < n; i++) {
                DSAnnotationContext aContext = manager.getContext(itemList, i);
                contextSelector.addItem(aContext);
                if (aContext == context) {
                    contextSelector.setSelectedIndex(i);
                }
            }
            newContextButton.setEnabled(true);
            // Refresh list
            listModel.refresh();
            // Refresh tree
            treeModel.setContext(context);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    treeModel.fireTreeStructureChanged();
                }
            });
            throwLabelEvent();
        } finally {
            resetContextMode = false;
        }
    }

    protected void createNewContext() {
        String name = JOptionPane.showInputDialog("New group name:");
        DSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
        if (manager.hasContext(itemList, name)) {
            JOptionPane.showMessageDialog(mainPanel, "Group already exists.");
        } else {
            context = manager.createContext(itemList, name);
            initializeContext(context);
            contextSelector.addItem(context);
            contextSelector.setSelectedItem(context);
            manager.setCurrentContext(itemList, context);
            // Refresh list
            listModel.refresh();
            // Refresh tree
            treeModel.setContext(context);
            treeModel.fireTreeStructureChanged();
            throwLabelEvent();
        }
    }

    protected void switchContext(DSAnnotationContext newContext) {
        if (!resetContextMode && (newContext != null)) {
            context = newContext;
            contextSelector.setSelectedItem(context);
            DSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            manager.setCurrentContext(itemList, context);
            // Refresh list
            listModel.refresh();
            // Refresh tree
            treeModel.setContext(context);
            treeModel.fireTreeStructureChanged();
            throwLabelEvent();
        }
    }

    protected void initializeContext(DSAnnotationContext context) {
        context.addLabel(SELECTION_LABEL);
    }

    public ActionListener getActionListener(String var) {
        return menuListeners.get(var);
    }

}
