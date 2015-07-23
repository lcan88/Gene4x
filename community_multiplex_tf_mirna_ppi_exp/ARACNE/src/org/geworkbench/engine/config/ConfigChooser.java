package org.geworkbench.engine.config;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author John Watkinson
 */
public class ConfigChooser extends JDialog {

    private JList configList;
    private JTextArea descriptionField;
    private JCheckBox defaultCheckBox;

    public ConfigChooser(String[] configs, final String[] descriptions) throws HeadlessException {
        setTitle("Configuration");
        setModal(true);
        FormLayout layout = new FormLayout("100dlu,6dlu,100dlu", "p,4dlu,p,p,4dlu,p");
        layout.setColumnGroups(new int[][]{{1, 3}});
        PanelBuilder builder = new PanelBuilder(layout);
        builder.addSeparator("Choose a Configuration");
        CellConstraints cc = new CellConstraints();
        int row = 3;
        builder.add(new JLabel("Configuration"), cc.xy(1, row));
        builder.add(new JLabel("Description"), cc.xy(3, row));
        row++;
        descriptionField = new JTextArea(descriptions[0]);
        descriptionField.setEditable(false);
        configList = new JList(configs);
        configList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configList.setSelectedIndex(0);
        configList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = e.getFirstIndex();
                descriptionField.setText(descriptions[index]);
            }
        });
        builder.add(configList, cc.xy(1, row));
        builder.add(descriptionField, cc.xy(3, row));
        row += 2;
        defaultCheckBox = new JCheckBox("Don't ask me again", false);
        JButton okButton = new JButton("OK");
        builder.add(defaultCheckBox, cc.xy(1, row));
        builder.add(okButton, cc.xy(3, row, "right, top"));
        JPanel container = new JPanel();
        container.setBorder(new EmptyBorder(4, 4, 4, 4));
        container.add(builder.getPanel());
        getContentPane().add(container);
        pack();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        String[] configs = new String[] {"Default", "All", "Microarray"};
        String[] descriptions = new String[] {
                "The default.",
                "All.",
                "Microarray only."
        };
        ConfigChooser chooser = new ConfigChooser(configs, descriptions);
    }
}
