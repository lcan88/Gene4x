package org.geworkbench.components.microarrays;

/*
 * The geworkbench_3.0 project
 * 
 * Copyright (c) 2006 Columbia University
 * 
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSGenotypicMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.engine.config.VisualPlugin;
import org.geworkbench.engine.management.AcceptTypes;
import org.geworkbench.events.MicroarraySetViewEvent;
import org.geworkbench.util.microarrayutils.MicroarrayViewEventBase;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * <p>Title: caWorkbench</p>
 * <p/>
 * <p>Description: Modular Application Framework for Gene Expession, Sequence and Genotype
 * Analysis</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p/>
 * <p>Company: Columbia University</p>
 *
 * @author Adam Margolin
 * @version 3.0
 */
@AcceptTypes({DSMicroarraySet.class})
public class TabularMicroarrayPanel extends MicroarrayViewEventBase implements VisualPlugin {
	
	Log log = LogFactory.getLog(this.getClass());

    public TabularMicroarrayPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TableCellRenderer renderer = new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (col == 0) {
                c.setBackground(Color.lightGray);
                ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                ((JLabel) c).setBorder(BorderFactory.createRaisedBevelBorder());
            } else {
                DSGeneMarker stats = uniqueMarkers.get(row);
                if (stats != null) {
                    DSMutableMarkerValue marker = maSetView.items().get(col - 1).getMarkerValue(stats.getSerial());
                    if (marker.isMissing()) {
                        c.setBackground(Color.yellow);
                    } else if (marker.isMasked()) {
                        c.setBackground(Color.pink);
                    } else if (marker.isMissing()) {
                        c.setBackground(Color.cyan);
                    } else {
                        c.setBackground(Color.white);
                    }
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    if (isSelected) {
                        ((JLabel) c).setBorder(BorderFactory.createEtchedBorder());
                    } else {
                        ((JLabel) c).setBorder(BorderFactory.createEmptyBorder());
                    }
                }
            }
            return c;
        }
    };
    TableModel microarrayTableModel = new AbstractTableModel() {
        NumberFormat nf = NumberFormat.getInstance();

        public int getColumnCount() {
            if (maSetView == null || refMASet == null) {
                return 0;
            } else {
                int cCount = maSetView.items().size() + 1;
                return cCount;
            }
        }

        public int getRowCount() {
            if (uniqueMarkers == null) {
                return 0;
            } else {
                return uniqueMarkers.size();
            }
        }

        public Object getValueAt(int row, int col) {
            nf.setMaximumFractionDigits(2);
            if (uniqueMarkers.size() > row) {
                DSGeneMarker stats = uniqueMarkers.get(row);
                if (stats != null) {
                    if (col == 0) {
                        return stats.toString() + stats.getDescription();
                    } else {
                        DSMicroarray array = maSetView.get(col - 1);
                        DSMarkerValue value = array.getMarkerValue(row);
                        if (value instanceof DSGenotypicMarkerValue) {
                            return value.toString();
                        } else {
                            //                        String value = maSetView.items().get(col - 1).getMarkerValue(stats).toString();
                            double v = maSetView.getValue(row, col - 1);
                            return nf.format(v);
                        }
                        //                        return value + "";
                    }
                } else {
                    return "Invalid";
                }
            } else {
                return "";
            }
        }

        public String getColumnName(int col) {
            if (mainPanel.isVisible()) {
                if (maSetView.items().size() > col - 1) {
                    if (col == 0) {
                        return "Marker";
                    } else {
                        return maSetView.items().get(col - 1).getLabel();
                    }
                }
            }
            return "";
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    JTable jTable1 = null;

    protected void jbInit() throws Exception {
        super.jbInit();
        //DecimalFormat format = new DecimalFormat("#.###"); TODO - is this here as a reminder (not used)?
        JScrollPane jScrollPane1 = new JScrollPane();
        JToolBar jToolBar1 = new JToolBar();
        JCheckBox jShowAllMArrays = new JCheckBox();
        JCheckBox jShowAllMarkers = new JCheckBox();

        ///JTableHeader jTableHeader1; - TODO unused
        jTable1 = new JTable(microarrayTableModel) {
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(20 * getModel().getColumnCount(), 20 * getModel().getRowCount());
            }

            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };

        //jTableHeader1 = jTable1.getTableHeader();
        jTable1.setEnabled(true);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable1.setDefaultRenderer(Object.class, renderer);
        jTable1.setCellSelectionEnabled(true);

        jToolBar1.setBorder(null);
        mainPanel.add(jScrollPane1, BorderLayout.CENTER);
        //        mainPanel.add(jToolBar1, BorderLayout.SOUTH);
        jToolBar1.add(jShowAllMArrays, null);
        jToolBar1.add(jShowAllMarkers, null);
        jScrollPane1.getViewport().add(jTable1, null);
        // Popup Menu Added to the DrawArea
        // this.add(jDisplayPanelPopup);
    }

    //public void notifyComponent(Object subscriber, Class anInterface) {
    // All standard interfaces are handled by the superclass
    //visualizer.notifyComponent(subscriber, anInterface);
    //}

    protected void fireModelChangedEvent(MicroarraySetViewEvent event) {
    	
    	resetTablularView();
    }
    
    private void resetTablularView() {
		
		if (chkAllMarkers.isSelected()){
			reset();
		} else if (onlyActivatedMarkers && activatedMarkers.size() > 0){
			reset();
		}
		/* TODO - selection process in this panel is a candidate for refactoring 
		 * it turns out that panels().size() represents the number of "unselected" marker panels (it doesn't matter if 'Selection' is selected)... 
		 * when (multiple) panels (ie. p1, p2) are selected, this size is 1 (=Selection panel).  
		 * For now, I have allowed this to handle the "remove" case. 
		 */
		else if (markers != null && markers.panels().size() >= 1){
			reset();
		} else{
			log.debug(" static tabular view ");
		}
	}

    protected void reset() {
        synchronized (jTable1) {
            jTable1.setModel(new DefaultTableModel());
            jTable1.setModel(microarrayTableModel);
        }
    }
}
