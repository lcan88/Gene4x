package org.geworkbench.engine.preferences;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author John Watkinson
 */
public class PreferencesDialog {

    public static final int MARGIN = 20;
    public static final int SPACING = 8;

    private static interface FieldComponent {
        public String getValue();

        public JComponent getComponent();
    }

    private static class TextFieldComponent extends JTextField implements FieldComponent {
        public TextFieldComponent(String text) {
            super(text);
        }

        public String getValue() {
            return getText();
        }

        public JComponent getComponent() {
            return this;
        }
    }

    public static class FileFieldComponent implements FieldComponent {

        private JTextField textField;
        private JPanel panel;

        public FileFieldComponent(final String initialValue, final FileFilter filter, final Component parent) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            textField = new JTextField(initialValue);
            JButton button = new JButton("...");
            panel.add(textField);
            panel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser;
                    chooser = new JFileChooser(initialValue);
                    if (filter != null) {
                        chooser.setFileFilter(filter);
                    }
                    int returnVal = chooser.showOpenDialog(parent);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        textField.setText(chooser.getSelectedFile().getPath());
                    }
                }
            });
        }

        public String getValue() {
            return textField.getText();
        }

        public JComponent getComponent() {
            return panel;
        }
    }

    private static class ComboBoxComponent extends JComboBox implements FieldComponent {
        public ComboBoxComponent(final String[] items, String selected) {
            super(items);
            setSelectedItem(selected);
        }

        public String getValue() {
            return (String) getSelectedItem();
        }

        public JComponent getComponent() {
            return this;
        }
    }

    private static class DialogResult {
        public boolean result = true;
    }

    private Preferences preferences;
    private FieldComponent[] components;

    public PreferencesDialog(Preferences preferences) {
        this.preferences = preferences;
    }

    public Preferences showPreferencesDialog(Frame owner) {
        final JDialog dialog = new JDialog(owner, "Preferences", true);
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(Box.createVerticalStrut(MARGIN), BorderLayout.NORTH);
        main.add(Box.createVerticalStrut(MARGIN), BorderLayout.SOUTH);
        main.add(Box.createHorizontalStrut(MARGIN), BorderLayout.WEST);
        main.add(Box.createHorizontalStrut(MARGIN), BorderLayout.EAST);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        main.add(panel, BorderLayout.CENTER);
        java.util.List<Field> fields = preferences.getFields();
        int n = fields.size();
        JLabel[] labels = new JLabel[n];
        components = new FieldComponent[n];
        int maxLabelWidth = 0;
        for (int i = 0; i < n; i++) {
            Field field = fields.get(i);
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
            labels[i] = new JLabel(field.getName() + ": ");
            if (labels[i].getPreferredSize().width > maxLabelWidth) {
                maxLabelWidth = labels[i].getPreferredSize().width;
            }
            fieldPanel.add(labels[i]);
            if (field instanceof ChoiceField) {
                ChoiceField choiceField = (ChoiceField) field;
                components[i] = new ComboBoxComponent(choiceField.getAllowedValues(), choiceField.toString());
            } else if (field instanceof FileField) {
                FileField fileField = (FileField) field;
                components[i] = new FileFieldComponent(fileField.toString(), fileField.getFilter(), dialog);
            } else {
                components[i] = new TextFieldComponent(field.toString());
            }
            fieldPanel.add(components[i].getComponent());
            panel.add(fieldPanel);
            panel.add(Box.createVerticalStrut(SPACING));
        }
        for (int i = 0; i < labels.length; i++) {
            Dimension size = labels[i].getPreferredSize();
            size.width = maxLabelWidth;
            labels[i].setPreferredSize(size);
        }
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalStrut(maxLabelWidth));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalGlue());
        panel.add(buttonPanel);
        final Preferences newPrefs = preferences.makeCopy();
        final DialogResult dialogResult = new DialogResult();
        dialog.setContentPane(main);
        // Add behavior to buttons
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Loop through field components and validate
                boolean failed = false;
                for (int i = 0; i < components.length; i++) {
                    FieldComponent component = components[i];
                    Field field = newPrefs.getFields().get(i);
                    String value = component.getValue();
                    try {
                        field.fromString(value);
                    } catch (ValidationException validationException) {
                        JOptionPane.showMessageDialog(dialog,
                                validationException.getMessage(),
                                "Error",
                                JOptionPane.WARNING_MESSAGE);
                        failed = true;
                        component.getComponent().grabFocus();
                        break;
                    }
                }
                if (!failed) {
                    dialog.dispose();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogResult.result = false;
                dialog.dispose();
            }
        });
        dialog.pack();
        dialog.setVisible(true);
        if (dialogResult.result) {
            return newPrefs;
        } else {
            return preferences;
        }
    }

    public static void main(String[] args) {
        Preferences prefs = new Preferences();
        TextField field1 = new TextField("Name");
        DoubleField field2 = new DoubleField("Amount");
        field2.setValue(5.4);
        ChoiceField field3 = new ChoiceField("Gender", new String[]{"Male", "Female"});
//        FileFilter filter = new FileFilter() {
//            public boolean accept(File f) {
//                if (f.getName().endsWith(".exe")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//            public String getDescription() {
//                return "Executables (*.exe)";
//            }
//        };
        FileField field4 = new FileField("File", null);
        field4.setValue(new File("c:/windows/system32/notepad.exe"));
        prefs.addField(field1);
        prefs.addField(field2);
        prefs.addField(field3);
        prefs.addField(field4);
        PreferencesDialog dialog = new PreferencesDialog(prefs);
        Preferences result = dialog.showPreferencesDialog(null);
        System.out.println("Result: " + result);
    }
}
