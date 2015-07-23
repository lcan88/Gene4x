package org.geworkbench.util.pathwaydecoder;

import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.jgraph.graph.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneNetworkManager {
    public class Interaction {
        public int gene1;
        public int gene2;
        public float strength;
        public float correlation;

        public String toString() {
            return null;
        }
    }

    private ConnectionSet connections = new ConnectionSet();
    private Map nodes = new Hashtable();
    private Map genes = new HashMap();
    private Map attributes = new Hashtable();
    private DefaultGraphModel networkModel = null;
    private ArrayList newCells = new ArrayList();

    public GeneNetworkManager(DefaultGraphModel nm) {
        networkModel = nm;
    }

    public void addGene(String key, DSGeneMarker gm) {
        genes.put(key, gm);
    }

    public DSGeneMarker getGene(String key) {
        return (DSGeneMarker) genes.get(key);
    }

    public void createCell(DSGeneMarker gm, Point p, boolean immediate, int connectionNo) {
        DefaultGraphCell cell = (DefaultGraphCell) nodes.get(gm);
        if (cell == null) {
            GenericMarkerNode gmn = new org.geworkbench.util.pathwaydecoder.GenericMarkerNode(gm);
            gmn.setConnectionNo(connectionNo);
            cell = new DefaultGraphCell(gmn);
            DefaultPort port = new DefaultPort("Floating");
            cell.add(port);
        }

        nodes.put(gm, cell);
        Map map = createDefaultAttributes();
        GraphConstants.setBounds(map, new Rectangle(p, new Dimension(40, 15)));
        GraphConstants.setAutoSize(map, true);
        GraphConstants.setOpaque(map, true);
        GraphConstants.setBorderColor(map, Color.black);
        GraphConstants.setFont(map, new Font("Arial", Font.PLAIN, 10));
        attributes.put(cell, map);
        newCells.add(cell);
        if (immediate) {
            networkModel.insert(newCells.toArray(), attributes, connections, null, null);
            newCells.clear();
            attributes.clear();
        }
    }

    public void connect(DSGeneMarker gm1, DSGeneMarker gm2, boolean immediate, float strength, double pearsonCorrelation) {
        Interaction interaction = new Interaction();
        interaction.gene1 = gm1.getSerial();
        interaction.gene2 = gm2.getSerial();
        interaction.strength = strength;
        interaction.correlation = (float) pearsonCorrelation;
        DefaultEdge edge = new DefaultEdge();
        edge.setUserObject(interaction);
        DefaultGraphCell cell1 = (DefaultGraphCell) nodes.get(gm1);
        DefaultGraphCell cell2 = (DefaultGraphCell) nodes.get(gm2);
        if ((cell1 != null) && (cell2 != null)) {
            List child1 = cell1.getChildren();
            List child2 = cell2.getChildren();
            if (!child1.isEmpty() && !child2.isEmpty()) {
                connections.connect(edge, cell1.getChildren().toArray()[0], cell2.getChildren().toArray()[0]);
            } else {
                connections.connect(edge, cell1, cell2);
                System.out.println("Node is pissing port in GeneNetworkManager");
            }
        }
        Map map = createDefaultAttributes();
        GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
        Color color = null;
        if (pearsonCorrelation > 0) {
            color = new Color(0, 0, Math.min((int) (350.0 * pearsonCorrelation), 255));
        } else {
            color = new Color(Math.min((int) (350.0 * Math.abs(pearsonCorrelation)), 255), 0, 0);
        }
        GraphConstants.setLineColor(map, color);
        GraphConstants.setLineWidth(map, strength);
        GraphConstants.setLineEnd(map, GraphConstants.ARROW_NONE);
        attributes.put(edge, map);
        newCells.add(edge);
        if (immediate) {
            networkModel.insert(newCells.toArray(), attributes, connections, null, null);
            newCells.clear();
            attributes.clear();
        }
    }

    private Map createDefaultAttributes() {
        Map map = GraphConstants.createMap();
        GraphConstants.setBorderColor(map, Color.black);
        GraphConstants.setAutoSize(map, true);
        return map;
    }

    public boolean contains(DSGeneMarker gm) {
        return (nodes.get(gm) != null);
    }

    public Map getNodes() {
        return nodes;
    }

    public Map getAttributes() {
        return attributes;
    }

    public Set getEdges(Object[] cells) {
        return DefaultGraphModel.getEdges(networkModel, cells);
    }

    public DefaultGraphCell getNode(DSGeneMarker gm1) {
        return (DefaultGraphCell) nodes.get(gm1);
    }

    public void clear() {
        Object[] roots = this.networkModel.getRoots(this.networkModel);
        List allNodes = this.networkModel.getDescendantList(networkModel, roots);
        networkModel.remove(allNodes.toArray());
        nodes.clear();
        attributes.clear();
        connections = new ConnectionSet();
        genes.clear();
    }

    public void layout() {
        networkModel.insert(newCells.toArray(), attributes, connections, null, null);
        newCells.clear();
        attributes.clear();
    }
}
