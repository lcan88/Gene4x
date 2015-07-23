package org.geworkbench.components.cytoscape;

import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.Options;
import cytoscape.*;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.data.servers.BioDataServer;
import cytoscape.view.CyNetworkView;
import giny.model.Node;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.engine.config.MenuListener;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.config.events.EventSource;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.engine.management.Publish;
import org.geworkbench.engine.management.Script;
import org.geworkbench.engine.management.Subscribe;
import org.geworkbench.events.AdjacencyMatrixEvent;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.events.PhenotypeSelectorEvent;
import org.geworkbench.util.Util;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrix;
import org.geworkbench.util.pathwaydecoder.mutualinformation.AdjacencyMatrixDataSet;
import org.geworkbench.util.pathwaydecoder.mutualinformation.IAdjacencyMatrix;
import phoebe.PNodeView;
import phoebe.event.PSelectionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: Bioworks</p>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence
 * and Genotype Analysis</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author manjunath at genomecenter dot columbia dot edu
 * @version 1.0
 */

@AcceptTypes({AdjacencyMatrixDataSet.class})
public class CytoscapeWidget extends EventSource implements VisualPlugin, MenuListener {

    protected Vector windows = new Vector();
    protected CytoscapeVersion version = new CytoscapeVersion();
    protected Logger logger;
    protected boolean uiSetup = false;
    protected DSMicroarraySet maSet = null;
    protected AdjacencyMatrixDataSet adjSet = null;
    protected CyNetwork cytoNetwork = null;
    protected AdjacencyMatrix adjMatrix = null;
    protected CyNetworkView view = null;
    PSelectionHandler selectionHandler = null;
    protected Vector adjStorage = new Vector(); // to store the adjacency matrices it received
    protected HashSet<String> dataSetIDs = new HashSet<String>();

    public CytoscapeWidget() {
        String[] args = new String[]{"-b", "annotation/manifest", "--JLD", "plugins"};
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
        Options.setDefaultIconSize(new Dimension(18, 18));

        init(args);
    }

    /**
     * <code>VisualPlugin</code> method
     *
     * @return <code>Component</code> the view for this component
     */
    public Component getComponent() {
        JMenuBar menuBar = Cytoscape.getDesktop().getCyMenus().getMenuBar();
        Container contentPane = Cytoscape.getDesktop().getContentPane();
        final CytoscapeObj theCytoscapeObj = Cytoscape.getCytoscapeObj();
        if (!uiSetup) {
            Component[] components = contentPane.getComponents();
            contentPane.removeAll();
            contentPane.add(menuBar, BorderLayout.NORTH);
            contentPane.add(components[0], BorderLayout.CENTER);
            contentPane.add(components[1], BorderLayout.SOUTH);
            uiSetup = true;
        }

        contentPane.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
                theCytoscapeObj.saveCalculatorCatalog();
            }

            public void componentMoved(ComponentEvent e) {
                theCytoscapeObj.saveCalculatorCatalog();
            }

            public void componentResized(ComponentEvent e) {
                theCytoscapeObj.saveCalculatorCatalog();
            }

            public void componentShown(ComponentEvent e) {
                theCytoscapeObj.saveCalculatorCatalog();
            }
        });

        //        contentPane.addMouseListener(new MouseAdapter() {
        //            public void mousePressed(MouseEvent e) {
        //                mouseButtonDown = true;
        //            }
        //
        //            public void mouseReleased(MouseEvent e) {
        //                mouseButtonDown = false;
        //            }
        //        });

        return contentPane;
    }

    protected void init(String[] args) {

        CytoscapeConfig config = new CytoscapeConfig(args);

        if (config.helpRequested()) {
            displayHelp(config);
        } else if (config.inputsError()) {
            inputError(config);
        } else if (config.displayVersion()) {
            displayHelp(config);
        }

        setupLogger(config);
        logger.info(config.toString());

        CytoscapeObj cytoscapeObj = new CytoscapeObj(config);

        String bioDataDirectory = config.getBioDataDirectory();
        BioDataServer bioDataServer = Cytoscape.loadBioDataServer(bioDataDirectory); // null;

        boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
        String defaultSpecies = Semantics.getDefaultSpecies(null, cytoscapeObj);

        Cytoscape.getDesktop().setVisible(false);

        Iterator gi = config.getGeometryFilenames().iterator();
        Iterator ii = config.getInteractionsFilenames().iterator();
        while (gi.hasNext()) {
            CyNetwork network = Cytoscape.createNetwork((String) gi.next(), Cytoscape.FILE_GML, false, null, null);
        }
        while (ii.hasNext()) {
            Cytoscape.createNetwork((String) ii.next(), Cytoscape.FILE_SIF, canonicalize, bioDataServer, defaultSpecies);
        }

        logger.info("reading attribute files");
        Cytoscape.loadAttributes(config.getNodeAttributeFilenames(), config.getEdgeAttributeFilenames(), canonicalize, bioDataServer, defaultSpecies);
        logger.info(" done");

        String expDataFilename = config.getExpressionFilename();
        if (expDataFilename != null) {
            logger.info("reading " + expDataFilename + "...");
            try {
                Cytoscape.loadExpressionData(expDataFilename, config.getWhetherToCopyExpToAttribs());
            } catch (Exception e) {
                logger.severe("Exception reading expression data file '" + expDataFilename + "'");
                logger.severe(e.getMessage());
                e.printStackTrace();
            }
            logger.info("  done");
        }
        Cytoscape.getDesktop().setupPlugins();
    }

    protected void displayHelp(CytoscapeConfig config) {
        System.out.println(version);
        System.out.println(config.getUsage());
    }

    protected void inputError(CytoscapeConfig config) {
        System.out.println(version);
        System.out.println("------------- Inputs Error");
        System.out.println(config.getUsage());
        System.out.println(config);
    }

    /**
     * configure logging:  cytoscape.props specifies what level of logging
     * messages are written to the console; by default, only SEVERE messages
     * are written.  in time, more control of logging (i.e., optional logging
     * to a file, disabling console logging, per-window or per-plugin logging)
     * can be provided
     */
    protected void setupLogger(CytoscapeConfig config) {
        logger = Logger.getLogger("global");
        Properties properties = config.getProperties();
        String level = properties.getProperty("logging", "SEVERE");

        if (level.equalsIgnoreCase("severe")) {
            logger.setLevel(Level.SEVERE);
        } else if (level.equalsIgnoreCase("warning")) {
            logger.setLevel(Level.WARNING);
        } else if (level.equalsIgnoreCase("info")) {
            logger.setLevel(Level.INFO);
        } else if (level.equalsIgnoreCase("config")) {
            logger.setLevel(Level.CONFIG);
        } else if (level.equalsIgnoreCase("all")) {
            logger.setLevel(Level.ALL);
        } else if (level.equalsIgnoreCase("none")) {
            logger.setLevel(Level.OFF);
        } else if (level.equalsIgnoreCase("off")) {
            logger.setLevel(Level.OFF);
        }
    }

    /**
     * geneSelectorAction
     *
     * @param e GeneSelectorEvent
     */
    public void geneSelectorAction(GeneSelectorEvent e) {
    }

    /**
     * getActionListener
     *
     * @param var String
     * @return ActionListener
     */
    public ActionListener getActionListener(String var) {
        return null;
    }

    /**
     * receiveProjectSelection
     *
     * @param e ProjectEvent
     */
    public void receiveProjectSelection(org.geworkbench.events.ProjectEvent e) {
        if (e.getDataSet() instanceof DSMicroarraySet) {
            maSet = (DSMicroarraySet) e.getDataSet();
        }
    }

    /**
     * phenotypeSelectorAction
     *
     * @param e PhenotypeSelectorEvent
     */
    public void phenotypeSelectorAction(PhenotypeSelectorEvent e) {
    }

    /**
     * receivedAdjacencyMatrix
     *
     * @param ae AdjacencyMatrixEvent
     */
    public void receiveAdjacencyMatrix(AdjacencyMatrixEvent ae) {
        if (ae.getAdjacencyMatrix().getSource() == AdjacencyMatrix.fromGeneNetworkPanelTakenGoodCareOf) {
            return;
        }
        String name = (maSet == null ? "Test" : maSet.getLabel());
        String tmpname = ae.getAdjacencyMatrix().getLabel();
        if (tmpname != null) {
            name = tmpname + " [" + name + "]";
        }
        cytoNetwork = Cytoscape.createNetwork(name);

        System.out.println(" adjSize  " + ae.getAdjacencyMatrix().getEdgeNo(5));
    }

    public void adjacencyMatrixLoaded(AdjacencyMatrixEvent ae) {
    }

    public AdjacencyMatrix getSelectedAdjMtx() {
        int idx = Integer.parseInt(Cytoscape.getCurrentNetwork().getIdentifier());
        return getStoredAdjMtx(idx);
    }

    public AdjacencyMatrix getStoredAdjMtx(int idx) {
        return (AdjacencyMatrix) this.adjStorage.get(idx);
    }

    /**
     * drawNetwork
     *
     * @param ae AdjacencyMatrixEvent
     */
    public void drawNetwork(AdjacencyMatrixEvent ae) {
        if (ae.getNetworkFocus() == -1) {
            drawCompleteNetwork(ae.getAdjacencyMatrix(), ae.getThreshold());
        } else {
            drawCentricNetwork(ae.getAdjacencyMatrix(), ae.getNetworkFocus(), ae.getThreshold(), ae.getDisplayDepth());
        }
    }

    public void drawCompleteNetwork(AdjacencyMatrix adjMatrix, double threshold) {
        this.adjMatrix = adjMatrix;
        Iterator keysIt = adjMatrix.getGeneRows().keySet().iterator();
        while (keysIt.hasNext()) {
            int key = ((Integer) keysIt.next()).intValue();
            createSubNetwork(key, threshold, 1);
        }
    }

    public void drawCentricNetwork(AdjacencyMatrix adjMatrix, int networkFocus, double threshold, int depth) {
        this.adjMatrix = adjMatrix;
        createSubNetwork(networkFocus, threshold, depth);
    }

    /**
     * @param geneId    int
     * @param threshold double
     * @param level     int
     * @deprecated -- maybe not used anymore...
     *             create the subnetwork and label or filter the interactions <BR>
     *             using the tradition method (center -> neighbor + iteration) <BR>
     */
    private void createSubNetworkAndInteraction(int geneId, double threshold, int level) {
        if (level == 0) {
            return;
        }
        HashMap map = adjMatrix.getInteraction(geneId);

        if (map != null) {
            DSGeneMarker gm1 = (DSGeneMarker) maSet.getMarkers().get(geneId);
            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                Integer id2 = (Integer) it.next();
                if ((id2.intValue() < maSet.getMarkers().size()) && (id2.intValue() != 12600)) {
                    String actiontype = map.get(id2).toString();
                    boolean thresholdTest = false;
                    thresholdTest = true;
                    if (thresholdTest) {
                        DSGeneMarker gm2 = (DSGeneMarker) maSet.getMarkers().get(id2.intValue());

                        String geneName1 = gm1.getShortName();
                        String geneName2 = gm2.getShortName();
                        if (!geneName2.equalsIgnoreCase(geneName1)) {
                            String g1Name = gm1.getShortName();
                            String g2Name = gm2.getShortName();

                            CyNode n1 = Cytoscape.getCyNode(g1Name);
                            CyNode n2 = Cytoscape.getCyNode(g2Name);
                            if (n1 == null) {
                                n1 = Cytoscape.getCyNode(g1Name, true);
                                n1.setIdentifier(g1Name);
                                cytoNetwork.setNodeAttributeValue(n1, "Unigene", gm1.getUnigene().getUnigeneAsString());
                                cytoNetwork.setNodeAttributeValue(n1, "Serial", new Integer(gm1.getSerial()));
                            }

                            cytoNetwork.addNode(n1);
                            if (n2 == null) {
                                n2 = Cytoscape.getCyNode(g2Name, true);
                                n2.setIdentifier(g2Name);
                                cytoNetwork.setNodeAttributeValue(n2, "Unigene", gm2.getUnigene().getUnigeneAsString());
                                cytoNetwork.setNodeAttributeValue(n2, "Serial", new Integer(gm2.getSerial()));
                            }

                            cytoNetwork.addNode(n2);

                            // set up the edge
                            CyEdge e = Cytoscape.getCyEdge(g1Name, g1Name + ".pp." + g2Name, g2Name, actiontype);
                            cytoNetwork.addEdge(e);

                            // draw the subnetwork of this node2
                            createSubNetwork(gm2.getSerial(), threshold, level - 1);
                        } else {
                        }
                    }
                }
            }
        }
    }

    private void createSubNetwork(int geneId, double threshold, int level) {
        if (level == 0) {
            return;
        }
        // get the first neighbors of gene(geneID)
        HashMap map = adjMatrix.get(geneId);
        if (map != null) {
            DSGeneMarker gm1 = (DSGeneMarker) maSet.getMarkers().get(geneId);
            int gm1Size = adjMatrix.getConnectionNo(gm1.getSerial(), threshold);

            for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                Integer id2 = (Integer) it.next();
                if ((id2.intValue() < maSet.getMarkers().size()) && (id2.intValue() != 12600)) {
                    Float v12 = (Float) map.get(id2);
                    boolean thresholdTest = false;
                    thresholdTest = v12.doubleValue() > threshold;
                    if (thresholdTest) {
                        DSGeneMarker gm2 = (DSGeneMarker) maSet.getMarkers().get(id2.intValue());
                        int gm2Size = adjMatrix.getConnectionNo(gm2.getSerial(), threshold);
                        String geneName1 = gm1.getShortName();
                        String geneName2 = gm2.getShortName();
                        if (!geneName2.equalsIgnoreCase(geneName1)) {
                            String g1Name = gm1.getShortName();
                            String g2Name = gm2.getShortName();
                            CyNode n1 = Cytoscape.getCyNode(g1Name);
                            CyNode n2 = Cytoscape.getCyNode(g2Name);
                            if (n1 == null) {
                                n1 = Cytoscape.getCyNode(g1Name, true);
                                n1.setIdentifier(g1Name);
                                cytoNetwork.setNodeAttributeValue(n1, "Unigene", gm1.getUnigene().getUnigeneAsString());
                                cytoNetwork.setNodeAttributeValue(n1, "Serial", new Integer(gm1.getSerial()));
                            }
                            cytoNetwork.setNodeAttributeValue(n1, "HubSize", new String(g1Name + ": " + Integer.toString(gm1Size)));
                            cytoNetwork.setNodeAttributeValue(n1, "Hub", new Integer(gm1Size));
                            cytoNetwork.addNode(n1);
                            if (n2 == null) {
                                n2 = Cytoscape.getCyNode(g2Name, true);
                                n2.setIdentifier(g2Name);
                                cytoNetwork.setNodeAttributeValue(n2, "Unigene", gm2.getUnigene().getUnigeneAsString());
                                cytoNetwork.setNodeAttributeValue(n2, "Serial", new Integer(gm2.getSerial()));
                            }
                            cytoNetwork.setNodeAttributeValue(n2, "HubSize", new String(g2Name + ": " + Integer.toString(gm2Size)));
                            cytoNetwork.setNodeAttributeValue(n2, "Hub", new Integer(gm2Size));
                            cytoNetwork.addNode(n2);
                            CyEdge e = null;
                            if (gm1.getSerial() > gm2.getSerial()) {
                                e = Cytoscape.getCyEdge(g1Name, g1Name + ".pp." + g2Name, g2Name, "pp");
                            } else {
                                e = Cytoscape.getCyEdge(g2Name, g2Name + ".pp." + g1Name, g1Name, "pp");
                            }
                            cytoNetwork.addEdge(e);
                            createSubNetwork(gm2.getSerial(), threshold, level - 1);
                        } else {
                        }
                    }
                }
            }
        }
    }

    private void cyNetWorkView_graphViewChanged(GraphViewChangeEvent gvce) {
        if (view != null && cytoNetwork != null) {
            java.util.List nodes = view.getSelectedNodes();
            DSPanel selectedMarkers = new CSPanel("Selected Genes", "Cytoscape");
            for (int i = 0; i < nodes.size(); i++) {
                PNodeView pnode = (PNodeView) nodes.get(i);
                Node node = pnode.getNode();
                if (node instanceof CyNode) {
                    int serial = ((Integer) cytoNetwork.getNodeAttributeValue((CyNode) node, "Serial")).intValue();
                    selectedMarkers.add(maSet.getMarkers().get(serial));
                }
            }
            selectedMarkers.setActive(true);
            publishSubpanelChangedEvent(new org.geworkbench.events.SubpanelChangedEvent(DSGeneMarker.class, selectedMarkers, org.geworkbench.events.SubpanelChangedEvent.SET_CONTENTS));
        }
    }

    @Publish
    public org.geworkbench.events.SubpanelChangedEvent publishSubpanelChangedEvent(org.geworkbench.events.SubpanelChangedEvent event) {
        return event;
    }


    /**
     * receiveProjectSelection
     *
     * @param e ProjectEvent
     */
    @Subscribe public void receive(org.geworkbench.events.ProjectEvent e, Object source) {
        DSDataSet dataSet = e.getDataSet();
        if (dataSet instanceof AdjacencyMatrixDataSet) {
            adjSet = (AdjacencyMatrixDataSet) dataSet;
            maSet = adjSet.getMatrix().getMicroarraySet();
            boolean found = false;
            String foundID = null;
            if (!dataSetIDs.contains(adjSet.getID())) {
                dataSetIDs.add(adjSet.getID());
            } else {
                Set networks = Cytoscape.getNetworkSet();
                for (Iterator iterator = networks.iterator(); iterator.hasNext();) {
                    String id = (String) iterator.next();
                    CyNetwork network = Cytoscape.getNetwork(id);
                    String title = network.getTitle();
                    if (title.equals(adjSet.getNetworkName())) {
                        found = true;
                        foundID = id;
                        break;
                    }
                }
            }
            if (!found) {
                receiveMatrix();
            } else {
                Cytoscape.getDesktop().getNetworkPanel().focusNetworkNode(foundID);
            }
        }
    }

    private void receiveMatrix() {
        // 1) RECEIVE event
        String name = adjSet.getNetworkName();
        String tmpname = adjSet.getMatrix().getLabel();
        if (tmpname != null) {
            name = tmpname + " [" + name + "]";
        }
        Set networks = Cytoscape.getNetworkSet();
        HashSet<String> names = new HashSet<String>();
        for (Iterator iterator = networks.iterator(); iterator.hasNext();) {
            String id = (String) iterator.next();
            CyNetwork network = Cytoscape.getNetwork(id);
            String title = network.getTitle();
            names.add(title);
        }
        name = Util.getUniqueName(name, names);
        adjSet.setNetworkName(name);
        cytoNetwork = Cytoscape.createNetwork(name);
        // 2) DRAW NETWORK event
        if (adjSet.getGeneId() == -1) {
            drawCompleteNetwork(adjSet.getMatrix(), adjSet.getThreshold());
        } else {
            drawCentricNetwork(adjSet.getMatrix(), adjSet.getGeneId(), adjSet.getThreshold(), adjSet.getDepth());
        }
        // 3) FINISH event
        if (maSet != null) {
            view = Cytoscape.createNetworkView(cytoNetwork, maSet.getLabel());
            view.addGraphViewChangeListener(new GraphViewChangeListener() {
                public void graphViewChanged(GraphViewChangeEvent graphViewChangeEvent) {
                    cyNetWorkView_graphViewChanged(graphViewChangeEvent);
                }
            });
        }

    }

    /**
     * phenotypeSelectorAction
     *
     * @param e PhenotypeSelectorEvent
     */
    @Subscribe public void receive(org.geworkbench.events.PhenotypeSelectorEvent e, Object source) {
    }

    @Subscribe public void receive(AdjacencyMatrixEvent ae, Object source) {
        switch (ae.getAction()) {
            case LOADED: {
                // no-op
            }
            break;
            case RECEIVE: {
                // System.out.println("receiveAdjacencyMatrix from cytoscapeWidget");
                if (ae.getAdjacencyMatrix().getSource() == AdjacencyMatrix.fromGeneNetworkPanelTakenGoodCareOf) {
                    // this adj mtx should be sent only to EviInt panel since it has been drawn
                    // by GeneNetworkPanel once
                    return;
                }
                String name = (maSet == null ? "Test" : maSet.getLabel());
                String tmpname = ae.getAdjacencyMatrix().getLabel();
                if (tmpname != null) {
                    name = tmpname + " [" + name + "]";
                }
                cytoNetwork = Cytoscape.createNetwork(name);

                // this.adjStorage.add(ae.getAdjacencyMatrix());
                System.out.println(" adjSize  " + ae.getAdjacencyMatrix().getEdgeNo(5));
                // System.out.println("Current adj storage: "+ myio.vector2tring(this.adjStorage, "\n"));
            }
            break;
            case DRAW_NETWORK: {
                // System.out.println("drawNetwork from cytoscapeWidget");
                if (ae.getNetworkFocus() == -1) {
                    drawCompleteNetwork(ae.getAdjacencyMatrix(), ae.getThreshold());
                } else {
                    drawCentricNetwork(ae.getAdjacencyMatrix(), ae.getNetworkFocus(), ae.getThreshold(), ae.getDisplayDepth());
                }
            }
            break;
//            case DRAW_NETWORK_AND_INTERACTION:
//                {
//                    // System.out.println("drawNetworkAndInteraction from cytoscapeWidget");
//                    adjMatrix = (EvdAdjacencyMatrix) ae.getAdjacencyMatrix();
//                    createWholeNetwork();
//                }
//                break;
            case FINISH: {
                if (maSet != null) {
                    view = Cytoscape.createNetworkView(cytoNetwork, maSet.getLabel());
                    view.addGraphViewChangeListener(new GraphViewChangeListener() {
                        public void graphViewChanged(GraphViewChangeEvent graphViewChangeEvent) {
                            cyNetWorkView_graphViewChanged(graphViewChangeEvent);
                        }
                    });
                }
            }
            break;
        }
    }

    public void setAdjacencyMatrix(IAdjacencyMatrix adjMat) {
        String name = ((maSet = adjMat.getMicroarraySet()) == null ? "Test" : maSet.getLabel());
        cytoNetwork = Cytoscape.createNetwork(name);
        this.adjStorage.add(adjMat);
    }

    @Script
    public DSPanel getFirstNeighbors(int geneid, double threshold, int level) {
        createSubNetwork(geneid, threshold, level);
        int[] indices = cytoNetwork.neighborsArray(geneid);
        DSPanel selectedMarkers = new CSPanel("First Neighbors", cytoNetwork.getNode(geneid).getIdentifier());
        for (int i = 0; i < indices.length; i++) {
            Node node = cytoNetwork.getNode(indices[i]);
            if (node instanceof CyNode) {
                int serial = ((Integer) cytoNetwork.getNodeAttributeValue((CyNode) node, "Serial")).intValue();
                selectedMarkers.add(maSet.getMarkers().get(serial));
            }
            selectedMarkers.setActive(true);
        }
        return selectedMarkers;
    }

    @Script
    public void computeAndDrawFirstNeighbors(DSGeneMarker m, AdjacencyMatrix am) {
        adjMatrix = am;
        maSet = am.getMicroarraySet();
//        try {
//        while (maSet == null) //THIS IS A BIG PROBLEM
//            Thread.currentThread().sleep(1000);
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        cytoNetwork = Cytoscape.createNetwork(maSet.getLabel() + m.getSerial());
        createSubNetwork(m.getSerial(), 0, 1);
        view = Cytoscape.createNetworkView(cytoNetwork, maSet.getLabel());
        view.addGraphViewChangeListener(new GraphViewChangeListener() {
            public void graphViewChanged(GraphViewChangeEvent graphViewChangeEvent) {
                cyNetWorkView_graphViewChanged(graphViewChangeEvent);
            }
        });
    }

    @Script
    public void computeAndDrawFirstNeighbors(DSPanel<DSGeneMarker> p, int i, AdjacencyMatrix am) {
        adjMatrix = am;
        cytoNetwork = Cytoscape.createNetwork(maSet.getLabel() + i);
        createSubNetwork(p.get(i).getSerial(), 0, 1);
        view = Cytoscape.createNetworkView(cytoNetwork, maSet.getLabel());
        view.addGraphViewChangeListener(new GraphViewChangeListener() {
            public void graphViewChanged(GraphViewChangeEvent graphViewChangeEvent) {
                cyNetWorkView_graphViewChanged(graphViewChangeEvent);
            }
        });
    }
}
