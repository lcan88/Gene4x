package org.geworkbench.components.pathwaydecoder;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.annotationparser.AnnotationParser;
import org.geworkbench.bison.datastructure.complex.panels.CSPanel;
import org.geworkbench.bison.datastructure.complex.panels.DSPanel;
import org.geworkbench.events.GeneSelectorEvent;
import org.geworkbench.util.pathwaydecoder.GeneNetworkManager;
import org.geworkbench.util.pathwaydecoder.GenericMarkerNode;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphModel;
import sun.awt.image.codec.JPEGImageEncoderImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class GeneNetwork extends JGraph {
    private DefaultGraphCell selectedCell = null;
    private DefaultEdge selectedEdge = null;
    private NetworkVisualizer visualizer = null;

    JPopupMenu jPopupMenu1 = new JPopupMenu();
    JMenuItem jMenuItem1 = new JMenuItem();
    JMenuItem jMenuItem2 = new JMenuItem();
    JMenuItem jMenuItem3 = new JMenuItem();

    private GeneNetworkPanel geneNetworkPanel;

    public GeneNetwork(GraphModel model, NetworkVisualizer visualizer, GeneNetworkPanel geneNetworkPanel) {
        super(model);
        this.geneNetworkPanel = geneNetworkPanel;
        this.visualizer = visualizer;
        // MouseListener that Prints the Cell on Doubleclick
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        jMenuItem1.setText("Expand");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuItem1_actionPerformed(e);
            }
        });
        jMenuItem2.setText("Hide");
        jMenuItem3.setText("Graph");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showGraph(e);
            }
        });

        /*
                addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == e.BUTTON3) {
                            // Get Cell under Mousepointer
                            int x = e.getX(), y = e.getY();
                            Object cell = getFirstCellForLocation(x, y);
                            // Print Cell Label
                            if (cell != null) {
                                if (cell instanceof DefaultGraphCell) {
                                    selectedCell = (DefaultGraphCell) cell;
                                    Object userObject = selectedCell.getUserObject();
                                    if (userObject != null) {
                                        System.out.println("Cell: " + userObject);
                                    }
                                }
                            }
                            jPopupMenu1.show(getParent(), x, y);
                        }
                    }
                });
         */
        this.addGraphSelectionListener(new GraphSelectionListener() {

            /**
             * valueChanged
             *
             * @param graphSelectionEvent GraphSelectionEvent
             */
            public void valueChanged(GraphSelectionEvent graphSelectionEvent) {
                selectionChanged(graphSelectionEvent);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                this_mouseReleased(e);
            }
        });
        jPopupMenu1.add(jMenuItem1);
        jPopupMenu1.add(jMenuItem2);
    }

    // Return Cell Label as a Tooltip
    public String getToolTipText(MouseEvent e) {
        if (e != null) {
            // Fetch Cell under Mousepointer
            Object c = getFirstCellForLocation(e.getX(), e.getY());
            if (c != null) {
                // Convert Cell to String and Return
                if (c instanceof DefaultGraphCell) {
                    Object o = ((DefaultGraphCell) c).getUserObject();
                    if (o instanceof GenericMarkerNode) {
                        try {
                            String affyId = ((GenericMarkerNode) o).getLabel();
                            c = affyId;
                            CSExpressionMarker gm = new CSExpressionMarker();
                            gm.setLabel(affyId);
                            String text = "";
                            // Show the name
                            c = text = gm.getShortName();
                            //visualizer.showName(text);
                            //visualizer.showAffyId(affyId);
                        } catch (Exception ex) {
                            return c.toString();
                            //return null;
                        }
                    }
                }
            }
            return convertValueToString(c);
        }
        return null;
    }

    public DefaultGraphCell getSelectedCell() {
        return selectedCell;
    }

    public DSGeneMarker getGenericMarker() {
        if (selectedCell != null) {
            Object userObject = selectedCell.getUserObject();
            if ((userObject != null) && (userObject instanceof DSGeneMarker)) {
                return (DSGeneMarker) userObject;
            }
        }
        return null;
    }

    void jMenuItem1_actionPerformed(ActionEvent e) {
        DSGeneMarker marker = getGenericMarker();
        if (marker != null) {
            visualizer.buildSubGraph(marker);
        }
    }

    void this_mouseReleased(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        // Get Cell under Mousepointer
        Object cell = getFirstCellForLocation(x, y);
        // Print Cell Label
        if (cell != null) {

            if (cell instanceof DefaultEdge) {
                selectedEdge = (DefaultEdge) cell;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    showGraph(x, y, selectedEdge);
                }
            } else if (cell instanceof DefaultGraphCell) {
                selectedCell = (DefaultGraphCell) cell;
                if (e.isMetaDown()) {
                    jPopupMenu1.show(this, x, y);
                } else {
                    Object userObject = selectedCell.getUserObject();
                    //if (userObject != null) {
                    if (userObject instanceof GenericMarkerNode) {
                        try {
                            String affyId = ((GenericMarkerNode) userObject).getLabel();
                            String text = "";
                            try {
                                // Show the name
                                //                String[] sn = AffyDataManager.getInfoFromDB(affyId, 1);

                                String[] sn = AnnotationParser.getInfo(affyId, AnnotationParser.DESCRIPTION);
                                if (sn != null) {
                                    text = sn[0];
                                } else {
                                    text = ((GenericMarkerNode) userObject).getDescription();
                                }
                            } catch (Exception ex) {
                                text = ((org.geworkbench.util.pathwaydecoder.GenericMarkerNode) userObject).getDescription();
                                //text = "Unknown";
                            }
                            visualizer.showName(text);
                            visualizer.showAffyId(affyId);
                            // show the process

                            try {
                                //                String[] processes = AffyDataManager.getInfoFromDB(affyId, 2);
                                String[] processes = AnnotationParser.getInfo(affyId, AnnotationParser.PATHWAY);
                                //added
                                if (processes.length > 0) {
                                    text = "";
                                    for (int i = 0; i < processes.length; i++) {
                                        text = text + processes[i] + "; -- ";
                                    }
                                } else {
                                    text = "Unknown";
                                }
                            } catch (Exception ex) {
                                text = "Unknown";
                            }
                            visualizer.showProcess(text);

                            // Show the function
                            try {
                                //                String[] functions = AffyDataManager.getInfoFromDB(affyId, 3);
                                String[] functions = AnnotationParser.getInfo(affyId, AnnotationParser.GOTERM);
                                if (functions.length > 0) {
                                    text = "";
                                    for (int fId = 0; fId < functions.length; fId++) {
                                        text = text + functions[fId] + ", -- ";
                                    }
                                } else {
                                    text = "Unknown";
                                }
                            } catch (Exception ex) {
                                text = "Unknown";
                            }
                            visualizer.showFunction(text);
                            /*
                            try {
                              String[] name = AffyDataManager.getInfoFromDB(affyId, 0);
                              if ( (name != null) && (name.length >= 1)) {
                                text = name[0];
                              } else {
                                text = "Unknown";
                              }
                            } catch (Exception ex) {
                              text = "Unknown";
                            }
                            */
                        } catch (Exception ex) {
                        }
                        //System.out.println("Cell: " + userObject);
                    }
                }
            }
        }
    }

    public void print() {
        int w = this.getWidth();
        int h = this.getHeight();
        VolatileImage image = this.createVolatileImage(w, h);
        this.print(image.getGraphics());
        BufferedImage bImage = image.getSnapshot();
        OutputStream out = null;
        try {
            out = new FileOutputStream("out.jpg");
            JPEGImageEncoder encoder = new JPEGImageEncoderImpl(out);
            encoder.encode(bImage);
            out.flush();
            out.close();
        } catch (ImageFormatException ex1) {
        } catch (IOException ex1) {
        }
    }

    void selectionChanged(GraphSelectionEvent graphSelectionEvent) {
        Object[] cells = this.getSelectionCells();
        DSPanel<DSGeneMarker> panel = new CSPanel<DSGeneMarker>("Network Sel");
        DSPanel<DSGeneMarker> simplePanel = new CSPanel<DSGeneMarker>("Net", "Selection");
        panel.panels().add(simplePanel);
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] instanceof DefaultGraphCell) {
                selectedCell = (DefaultGraphCell) cells[i];
                Object userObject = selectedCell.getUserObject();
                if (userObject instanceof GenericMarkerNode) {
                    simplePanel.add((GenericMarkerNode) userObject);
                }
            }
        }
        GeneSelectorEvent event = null;
        event = new GeneSelectorEvent(panel);
        geneNetworkPanel.getGeneProfiler().publishGeneSelectorEvent(event);
    }

    public void showGraph(int x, int y, DefaultEdge edge) {
        GeneNetworkManager.Interaction interaction = (GeneNetworkManager.Interaction) edge.getUserObject();
        visualizer.showGraph(x, y, interaction.gene1, interaction.gene2);
    }
}
