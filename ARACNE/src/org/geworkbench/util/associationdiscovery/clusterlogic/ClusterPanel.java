package org.geworkbench.util.associationdiscovery.clusterlogic;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: Plug And Play</p>
 * <p>Description: Dynamic Proxy Implementation of enGenious</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: First Genetic Trust Inc.</p>
 *
 * @author Manjunath Kustagi
 * @version 1.0
 */

public class ClusterPanel extends JPanel {

    JLabel jLabel1 = new JLabel();
    public JTextField jEntropyThr = new JTextField();
    JLabel jLabel2 = new JLabel();
    public JTextField jSigmaX = new JTextField();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    public DefaultListModel scoreList = new DefaultListModel();
    JList jScores = new JList(scoreList);
    JLabel jLabel3 = new JLabel();
    public JTextField jPseudoX = new JTextField();

    public ClusterPanel() {
        this.setName(getComponentName());
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        jLabel1.setText("Entropy Threshold:");
        this.setLayout(gridBagLayout1);
        jEntropyThr.setPreferredSize(new Dimension(63, 21));
        jEntropyThr.setText("0.5");
        jEntropyThr.setHorizontalAlignment(SwingConstants.TRAILING);
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("Sigma x:");
        jSigmaX.setText("3.0");
        jSigmaX.setHorizontalAlignment(SwingConstants.TRAILING);
        jScrollPane1.setBorder(BorderFactory.createLoweredBevelBorder());
        jLabel3.setText("Pseudo Count:");
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jPseudoX.setHorizontalAlignment(SwingConstants.TRAILING);
        jPseudoX.setText("1.0");
        this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        this.add(jEntropyThr, new GridBagConstraints(1, 0, 1, 1, 0.2, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 0, 0, 0), 0, 0));
        this.add(jScrollPane1, new GridBagConstraints(2, 0, 1, 5, 0.5, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
        jScrollPane1.getViewport().add(jScores, null);
        this.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
        this.add(jSigmaX, new GridBagConstraints(1, 1, 1, 1, 0.3, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
        this.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 4), 0, 0));
        this.add(jPseudoX, new GridBagConstraints(1, 2, 1, 1, 0.2, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 41, 0));
    }

    public JComponent[] getVisualComponents(String areaName) {
        JComponent[] panels = null;
        if (areaName.equalsIgnoreCase("CommandArea")) {
            panels = new JComponent[1];
            panels[0] = this;
        }
        return panels;
    }

    public String getComponentName() {
        return "Cluster Logic";
    }
}
